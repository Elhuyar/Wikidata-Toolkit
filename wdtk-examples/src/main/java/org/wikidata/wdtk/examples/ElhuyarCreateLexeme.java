package org.wikidata.wdtk.examples;

/**
 * 
 * The bot will upload about 10,000 lexemes (all nouns) with their corresponding forms. 
 * Definitions in Basque will be also given, each meaning of the lexem corresponds to a 'Sense'.
 * All the lexemes and definitions belong to the Elhuyar Ikaslearen Hiztegia (Elhuyar Student Dictionary), published in 2008, ISBN: 978-84-95338-96-9.
 * 
 * @author Elhuyar Foundation
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.ElhuyarLexemeDocumentBuilder;
import org.wikidata.wdtk.datamodel.helpers.ElhuyarSenseDocumentBuilder;
import org.wikidata.wdtk.datamodel.helpers.StatementBuilder;
import org.wikidata.wdtk.datamodel.implementation.FormDocumentImpl;
import org.wikidata.wdtk.datamodel.implementation.ItemIdValueImpl;
import org.wikidata.wdtk.datamodel.implementation.MonolingualTextValueImpl;
import org.wikidata.wdtk.datamodel.implementation.TermImpl;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.FormDocument;
import org.wikidata.wdtk.datamodel.interfaces.FormIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.LexemeDocument;
import org.wikidata.wdtk.datamodel.interfaces.LexemeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SenseDocument;
import org.wikidata.wdtk.datamodel.interfaces.SenseIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.util.WebResourceFetcherImpl;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;
import org.wikidata.wdtk.wikibaseapi.LoginFailedException;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataEditor;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ElhuyarCreateLexeme {

	final static String siteIri = "https://www.wikidata.org/wiki/";
	final static String wikidataEntity = "http://www.wikidata.org/entity/";

	// lexemes json folder
	final static String jsonFolder = "wikidata-json-folder";

	// file to save the lexemes wikidata identifier
	final static String identifiersFile = "wikidata-identifiers.txt";

	// Elhuyar Dictionary ID (P6838) wikidata property
	static PropertyIdValue elhuyarIdProperty;

	public static void main(String[] args) {

		ExampleHelpers.configureLogging();

		WebResourceFetcherImpl.setUserAgent("Wikidata Toolkit adding lexemes");

		ApiConnection connection = ApiConnection.getWikidataApiConnection();
		try {
			connection.login("username", "password");

			WikibaseDataEditor wikibaseDataEditor = new WikibaseDataEditor(connection, siteIri);

			obtainElhuyarDictionaryIdProperty(connection);

			recoveryJsonContentAndCreateLexemes(wikibaseDataEditor);

		} catch (LoginFailedException e) {
			System.out.println("An error occurred logging in wikidata. Error: " + e.getMessage());

		} catch (MediaWikiApiErrorException | IOException e) {
			System.out.println("An error occurred obtaining Elhuyar Dictionary Id property . Error: " + e.getMessage());
		}

	}

	/**
	 * Get Elhuyar Dictionary ID (P6838) wikidata property
	 * 
	 * @param connection ApiConnection
	 * @throws MediaWikiApiErrorException
	 * @throws IOException
	 */
	private static void obtainElhuyarDictionaryIdProperty(ApiConnection connection)
			throws MediaWikiApiErrorException, IOException {

		WikibaseDataFetcher wikibaseDataFetcher = new WikibaseDataFetcher(connection, siteIri);
		EntityDocument entityDocument = wikibaseDataFetcher.getEntityDocument("P6838");
		PropertyDocument pd = (PropertyDocument) entityDocument;

		if (pd != null && pd.getDatatype() != null && pd.getDatatype().getIri() != null && pd.getLabels() != null) {
			if (DatatypeIdValue.DT_EXTERNAL_ID.equals(pd.getDatatype().getIri()) && pd.getLabels().containsKey("en")) {
				elhuyarIdProperty = pd.getEntityId();
			}
		} else {
			System.out.println("Do not found Elhuyar Dictionary ID property");
		}

	}

	/**
	 * Create lexeme with forms and definitions
	 * 
	 * @param dataEditor WikibaseDataEditor
	 * @param lexeme     Lexeme
	 * @return lexeme wikidata identifier
	 */
	@SuppressWarnings("finally")
	private static String createLexeme(WikibaseDataEditor dataEditor, ElhuyarLexeme lexeme) {

		String lexeme_id = "";

		LexemeIdValue noidLexeme = LexemeIdValue.NULL;

		List<MonolingualTextValue> lemmas = createLexeme(lexeme.getLexeme(), "eu");

		ItemIdValue lexicalCategory = createLexicalCategory("Q1084"); // noun

		Statement statementElhuyarId = StatementBuilder.forSubjectAndProperty(noidLexeme, elhuyarIdProperty)
				.withValue(Datamodel.makeStringValue(lexeme.getElhuyar_code())).build();

		LexemeDocument lexemeDocument = (LexemeDocument) ElhuyarLexemeDocumentBuilder.forItemId(noidLexeme)
				.withLexicalCategory(lexicalCategory).withLemmas(lemmas).withLanguage("Q8752", wikidataEntity) // Basque
				.withStatement(statementElhuyarId).build();

		LexemeDocument newLexemeDocument;

		try {

			newLexemeDocument = dataEditor.createElhuyarLexemeDocument(lexemeDocument,
					lexeme.getLexeme() + " lexeme creation");

			if (newLexemeDocument != null && newLexemeDocument.getEntityId() != null
					&& newLexemeDocument.getEntityId().getId() != null) {

				lexeme_id = newLexemeDocument.getEntityId().getId();

				addFormsToLexeme(dataEditor, lexeme, newLexemeDocument);

				addSensesToLexeme(dataEditor, lexeme, newLexemeDocument);

			}
		} catch (IOException | MediaWikiApiErrorException e) {

			System.out.println("An error occurred creating lexeme. Error: " + e.getMessage());

		} finally {
			return lexeme_id;
		}

	}

	/**
	 * Set definitions to lexeme
	 * 
	 * @param wikibaseDataEditor WikibaseDataEditor
	 * @param lexeme             Lexeme
	 * @param newLexemeDocument  LexemeDocument
	 */
	private static void addSensesToLexeme(WikibaseDataEditor wikibaseDataEditor, ElhuyarLexeme lexeme,
			LexemeDocument newLexemeDocument) {

		SenseIdValue noIdSense = SenseIdValue.NULL;
		List<MonolingualTextValue> glosses;
		SenseDocument senseDocument;

		for (ElhuyarDefinition adiera : lexeme.getDefinitions()) {

			glosses = createGlosse(adiera.getDefinizioa(), "eu", adiera.getJakintzaAlorraLaburdura());

			senseDocument = (SenseDocument) ElhuyarSenseDocumentBuilder.forSenseId(noIdSense).withGlosses(glosses)
					.build();

			if (senseDocument != null) {

				try {
					wikibaseDataEditor.createElhuyarSenseToLexeme(senseDocument,
							newLexemeDocument.getEntityId().getId());

				} catch (IOException | MediaWikiApiErrorException e) {

					System.out.println("An error occurred in sense creation . Error:" + e.getMessage());
				}

			}

		}

	}

	/**
	 * Set forms to lexeme
	 * 
	 * @param wikibaseDataEditor WikibaseDataEditor
	 * @param lexeme             Lexeme
	 * @param newLexemeDocument  LexemeDocument
	 */
	private static void addFormsToLexeme(WikibaseDataEditor wikibaseDataEditor, ElhuyarLexeme lexeme,
			LexemeDocument newLexemeDocument) {

		FormDocument formDoc;

		for (ElhuyarForm kasua : lexeme.getForms()) {

			formDoc = createFormDocumentContent(kasua);

			try {

				wikibaseDataEditor.createElhuyarFormDocument(formDoc, newLexemeDocument.getEntityId().getId());

			} catch (IOException | MediaWikiApiErrorException e) {
				System.out.println("An error occurred lexeme forms . Error: " + e.getMessage());
			}
		}

	}

	/**
	 * 
	 * Create definition object to current lexeme.
	 * 
	 * @param definition    String
	 * @param language      String
	 * @param knowledgeArea String
	 * @return lexemes definition object
	 * 
	 */

	private static List<MonolingualTextValue> createGlosse(String definition, String language, String knowledgeArea) {

		if (language.isEmpty()) {
			language = "eu";
		}

		if (knowledgeArea != null && !knowledgeArea.isEmpty()) {
			definition = "(" + knowledgeArea + ") " + definition;
		}

		List<MonolingualTextValue> glosses = new ArrayList<MonolingualTextValue>();
		glosses.add(new MonolingualTextValueImpl(definition, language));

		return glosses;

	}

	/**
	 * Create form to current lexeme.
	 * 
	 * @param form Form
	 * @return FormDocument
	 */
	private static FormDocument createFormDocumentContent(ElhuyarForm form) {

		FormIdValue noidForm = FormIdValue.NULL;

		List<ItemIdValue> grammaticalFeatures;

		if (form.getIzaera() != null) {
			grammaticalFeatures = Arrays.asList(new ItemIdValueImpl(form.getKasua(), siteIri),
					new ItemIdValueImpl(form.getZkia(), siteIri), new ItemIdValueImpl(form.getIzaera(), siteIri));
		} else {
			grammaticalFeatures = Arrays.asList(new ItemIdValueImpl(form.getKasua(), siteIri),
					new ItemIdValueImpl(form.getZkia(), siteIri));
		}

		List<MonolingualTextValue> representations = new ArrayList<MonolingualTextValue>();

		MonolingualTextValue rep = new TermImpl("eu", form.getForma());

		representations.add(rep);

		List<StatementGroup> statements = new ArrayList<StatementGroup>();

		FormDocumentImpl formDoc = new FormDocumentImpl(noidForm, representations, grammaticalFeatures, statements, 0L);

		return formDoc;
	}

	/**
	 * Create lexical category to current lexeme.
	 * 
	 * @param lexicalCategory String
	 * @return ItemIdValue
	 */
	private static ItemIdValue createLexicalCategory(String lexicalCategory) {

		return new ItemIdValueImpl(lexicalCategory, wikidataEntity);

	}

	/**
	 * Create a lexeme description
	 * 
	 * @param lexema   String lexeme description
	 * @param language String lexeme description language
	 * @return List<MonolingualTextValue>
	 */
	private static List<MonolingualTextValue> createLexeme(String lexema, String language) {

		if (language.isEmpty()) {
			language = "eu";
		}
		List<MonolingualTextValue> lexemes = new ArrayList<MonolingualTextValue>();
		lexemes.add(new MonolingualTextValueImpl(lexema, language));
		return lexemes;

	}

	/**
	 * Read json file names from folder, parse each file and create a corresponding
	 * lexeme in wikidata
	 * 
	 * @param dataEditor WikibaseDataEditor
	 * 
	 */
	private static void recoveryJsonContentAndCreateLexemes(WikibaseDataEditor dataEditor) {

		List<String> jsonFilesAbsolutePath;

		try {

			jsonFilesAbsolutePath = obtainJsonFilesAbsolutePath(new File(jsonFolder), null);

			extractJsonFilesInformationAndCreateLexemes(jsonFilesAbsolutePath, dataEditor);

			System.out.println("Recovery json content method end successfully.");

		} catch (Exception e) {

			System.out.println("An error occurred recovering json data content. Error:" + e.getMessage());
		}

	}

	/**
	 * 
	 * Parse json files and create a corresponding lexeme in wikidata
	 * 
	 * @param jsonFilesPath List<String> json files absolute path
	 * 
	 * @param dataEditor    WikibaseDataEditor
	 */
	private static void extractJsonFilesInformationAndCreateLexemes(List<String> jsonFilesPath,
			WikibaseDataEditor dataEditor) {

		ElhuyarLexeme lexema = null;

		String lexeme_id;

		for (String jsonFile : jsonFilesPath) {

			try {

				lexema = parseJsonFile(new FileInputStream(jsonFile));

				if (lexema != null) {

					lexeme_id = createLexeme(dataEditor, lexema);

					if (!lexeme_id.isEmpty()) {
						writeIdentifiersInFile(lexema.getElhuyar_code(), lexeme_id, jsonFile);
					} else {
						writeIdentifiersInFile(lexema.getElhuyar_code(), "error", jsonFile);
					}
				} else {
					System.out.println("An error occurred reading json file:" + jsonFile + " and lexeme is null");
				}

			} catch (FileNotFoundException e) {
				System.out.println("An error occurred in json extraction . Error:" + e.getMessage());
			}
		}

	}

	/**
	 * Create a file with wikidata identifiers
	 * 
	 * @param elhuyar_code       String
	 * @param lexeme_wikidata_id String
	 * @param fileName           String
	 */
	private static void writeIdentifiersInFile(String elhuyar_code, String lexeme_wikidata_id, String fileName) {

		try {

			File file = new File(identifiersFile);

			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);

			bw.append(elhuyar_code);
			bw.append("\t");
			bw.append(lexeme_wikidata_id);
			bw.append("\t");
			bw.append(fileName);
			bw.append("\n");

			bw.close();

		} catch (Exception e) {
			System.out.println("An error occurred adding identifiers in file. Error: " + e.getMessage());

		}

	}

	/**
	 * Parse json file and create a corresponding Lexeme object
	 * 
	 * @param jsonFile InputStream
	 * @return Lexeme
	 */
	private static ElhuyarLexeme parseJsonFile(InputStream jsonFile) {

		ObjectMapper objectMapper = new ObjectMapper();
		ElhuyarLexeme lexema = null;
		try {
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
			lexema = objectMapper.readValue(jsonFile, ElhuyarLexeme.class);

		} catch (IOException e) {
			System.out.println("An error occurred reading json file. Error: " + e.getMessage());
		}
		return lexema;
	}

	/**
	 * Create absolute files name list.
	 * 
	 * @param jsonFolder File
	 * @param jsonFiles  List<String>
	 * @return List<String>
	 */
	private static List<String> obtainJsonFilesAbsolutePath(File jsonFolder, List<String> jsonFiles) {

		if (jsonFiles == null) {
			jsonFiles = new ArrayList<String>();
		}

		String fileName;

		for (final File fileEntry : jsonFolder.listFiles()) {

			if (fileEntry.isFile()) {

				fileName = fileEntry.getName();
				if ((fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length()).toLowerCase()).equals("json")){
					jsonFiles.add(jsonFolder.getAbsolutePath() + File.separator + fileEntry.getName());
				}
			} else if (fileEntry.isDirectory()) {

				obtainJsonFilesAbsolutePath(fileEntry, jsonFiles);
			}
		}
		return jsonFiles;

	}

}
