package org.wikidata.wdtk.datamodel.helpers;

import java.util.ArrayList;
import java.util.List;

import org.wikidata.wdtk.datamodel.implementation.ItemIdValueImpl;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.FormDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.LexemeDocument;
import org.wikidata.wdtk.datamodel.interfaces.LexemeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.SenseDocument;

/**
 * Builder class to construct {@link LexemeDocument} objects.
 *
 * @author Elhuyar Foundation
 *
 */
public class ElhuyarLexemeDocumentBuilder
		extends ElhuyarStatementDocumentBuilder<ElhuyarLexemeDocumentBuilder, LexemeDocument> {

	private ItemIdValue lexicalCategory;
	private ItemIdValue language;
	private List<MonolingualTextValue> lemmas = new ArrayList<MonolingualTextValue>();
	private List<FormDocument> forms = new ArrayList<FormDocument>();
	private List<SenseDocument> senses = new ArrayList<SenseDocument>();

	/**
	 * Constructor to start the build from a blank lexeme item.
	 *
	 * @param entityIdValue
	 */
	protected ElhuyarLexemeDocumentBuilder(EntityIdValue entityIdValue) {
		super(entityIdValue);
	}

	/**
	 * Constructor to start the build from an existing lexeme.
	 * 
	 * @param lexemeDocument the lexeme to start the build from
	 */
	protected ElhuyarLexemeDocumentBuilder(LexemeDocument lexemeDocument) {
		super(lexemeDocument);

	}

	/**
	 * Starts the construction of an {@link LexemeDocument} with the given id.
	 *
	 * @param entityIdValue id of the newly constructed lexeme document
	 * @return builder object to continue construction
	 */
	public static ElhuyarLexemeDocumentBuilder forItemId(EntityIdValue entityIdValue) {
		return new ElhuyarLexemeDocumentBuilder(entityIdValue);
	}

	/**
	 * Starts the construction of an {@link LexemeDocument} from an existing value.
	 * 
	 * @param initialDocument the lexeme to start the construction from
	 * @return builder object to continue construction
	 */
	public static ElhuyarLexemeDocumentBuilder fromItemDocument(LexemeDocument lexemeDocument) {
		return new ElhuyarLexemeDocumentBuilder(lexemeDocument);
	}

	/**
	 * Returns the {@link LexemeDocument} that has been built.
	 *
	 * @return constructed lexeme document
	 * @throws IllegalStateException if the object was built already
	 */
	@Override
	public LexemeDocument build() {
		prepareBuild();
		return factory.getLexemeDocument((LexemeIdValue) this.entityIdValue, this.lexicalCategory, this.language,
				this.lemmas, getStatementGroups(), this.forms, this.senses, this.revisionId);

	}

	public ElhuyarLexemeDocumentBuilder withLexicalCategory(String lexicalCategory, String siteIri) {
		this.lexicalCategory = new ItemIdValueImpl(lexicalCategory, siteIri);
		return this;
	}

	public ElhuyarLexemeDocumentBuilder withLexicalCategory(ItemIdValue lexicalCategory) {
		this.lexicalCategory = lexicalCategory;
		return this;
	}

	public ElhuyarLexemeDocumentBuilder withLanguage(String language, String siteIri) {
		this.language = new ItemIdValueImpl(language, siteIri);

		return this;
	}

	public ElhuyarLexemeDocumentBuilder withLemmas(List<MonolingualTextValue> lemmas) {
		this.lemmas = lemmas;
		return this;
	}

	public ElhuyarLexemeDocumentBuilder withForms(List<FormDocument> forms) {
		this.forms = forms;
		return this;
	}

	public ElhuyarLexemeDocumentBuilder withSenses(List<SenseDocument> senses) {

		this.senses = senses;
		return this;
	}

	@Override
	protected ElhuyarLexemeDocumentBuilder getThis() {
		return this;
	}
}
