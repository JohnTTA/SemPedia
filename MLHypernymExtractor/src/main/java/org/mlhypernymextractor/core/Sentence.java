package org.mlhypernymextractor.core;
import java.util.ArrayList;

import org.mlhypernymextracor.learning.features.Features;


public class Sentence extends Annotation {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4495199444707765379L;
	private int id;
	private ArrayList<Term> terms = null;
	private String value;
	private String ttgValue;
	private Features features;
	
	public Sentence(int a, int b, int id, String v) {
		super(a,b);
		this.terms = new ArrayList<>();
		this.id = id;
		this.value = v ;
		super.isSentence = true;
	}

	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public ArrayList<Term> getTerms() {
		return terms;
	}


	public void setTerms(ArrayList<Term> terms) {
		this.terms = terms;
	}
	
	public void addTerm(Term t){
		t.setSentence(this);
		this.terms.add(t);
	}
	
	public void removeTerm(Term t){
		for (int i = 0; i < this.terms.size(); i++){
			if(this.terms.get(i).equals(t)){
				this.terms.remove(i);
			}
		}
	}
	
	public String getValue() {
		return value;
	}
	

	public String getTtgValue() {
		return ttgValue;
	}

	public void setTtgValue(String ttgValue) {
		this.ttgValue = ttgValue;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Features getFeatures() {
		return features;
	}

	public void setFeatures(Features features) {
		this.features = features;
	}

	public String printTerms() {
		String res = "";
		for (Term term : this.terms) {
			res += term.getValue()+"("+term.startOffset+","+term.endOffset+")"+", ";
		}
		return res;
	}

	@Override
	public String toString(){
		return this.value + this.terms.toString();
	}
}
