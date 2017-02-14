package org.mlhypernymextractor.core;
import java.util.ArrayList;


public class Sentence extends Annotation {
	
	private int id;
	private ArrayList<Term> terms = null;
	private String value;
	private String ttgValue;
	
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

}
