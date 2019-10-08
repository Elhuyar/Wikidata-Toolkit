package org.wikidata.wdtk.examples;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * Elhuyar Foundation definition representation
 * 
 * @author Elhuyar Foundation
 *
 */

public class ElhuyarDefinition {
	
	@JsonProperty("adiera_zki")
	private String adiera_zki;
	
	@JsonProperty("definizioa")
	private String definizioa;
	
	@JsonProperty("jakintza_alorra")
	private String jakintzaAlorra;
	
	@JsonProperty("ja_laburdura")
	private String jakintzaAlorraLaburdura;
	
	public String getAdiera_zki() {
		return adiera_zki;
	}
	public void setAdiera_zki(String adiera_zki) {
		this.adiera_zki = adiera_zki;
	}
	public String getDefinizioa() {
		return definizioa;
	}
	public void setDefinizioa(String definizioa) {
		this.definizioa = definizioa;
	}
	public String getJakintzaAlorra() {
		return jakintzaAlorra;
	}
	public void setJakintzaAlorra(String jakintzaAlorra) {
		this.jakintzaAlorra = jakintzaAlorra;
	}
	public String getJakintzaAlorraLaburdura() {
		return jakintzaAlorraLaburdura;
	}
	public void setJakintzaAlorraLaburdura(String jakintzaAlorraLaburdura) {
		this.jakintzaAlorraLaburdura = jakintzaAlorraLaburdura;
	}
}
