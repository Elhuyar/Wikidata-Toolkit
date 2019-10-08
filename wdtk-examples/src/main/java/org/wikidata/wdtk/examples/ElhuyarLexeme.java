package org.wikidata.wdtk.examples;

/**
 * 
 * Elhuyar Foundation lexeme representation
 * 
 * @author Elhuyar Foundation
 *
 */
public class ElhuyarLexeme {

	@JsonProperty("Elhuyar_code")
	private String elhuyar_code;

	@JsonProperty("kasuak")
	private ElhuyarForm[] forms;

	@JsonProperty("lema")
	private String lexeme;

	@JsonProperty("definizioak")
	private ElhuyarDefinition[] definitions;

	public String getElhuyar_code() {
		return elhuyar_code;
	}

	public void setElhuyar_code(String elhuyar_code) {
		this.elhuyar_code = elhuyar_code;
	}

	public ElhuyarForm[] getForms() {
		return forms;
	}

	public void setForms(ElhuyarForm[] forms) {
		this.forms = forms;
	}

	public String getLexeme() {
		return lexeme;
	}

	public void setLexeme(String lexeme) {
		this.lexeme = lexeme;
	}

	public ElhuyarDefinition[] getDefinitions() {
		return definitions;
	}

	public void setDefinitions(ElhuyarDefinition[] definitions) {
		this.definitions = definitions;
	}
	
}
