package org.mlhypernymextractor.core;
public class Term extends Annotation {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4884850656474212269L;
	private int id;
	private String value;
	private Sentence sentence;
	private boolean isMotVide = false;

	public Term(int a, int b, String value, int id, boolean motVide) {
		super(a, b);
		this.id = id;
		super.isSentence = false;
		this.value = value;
		this.isMotVide = motVide;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Sentence getSentence() {
		return sentence;
	}

	public void setSentence(Sentence sentence) {
		this.sentence = sentence;
	}

	public boolean isMotVide() {
		return isMotVide;
	}

	public void setMotVide(boolean isMotVide) {
		this.isMotVide = isMotVide;
	}
	
	public boolean equals(Term t) {
		return this.getStartOffset() == t.getStartOffset()
				&& this.getEndOffset() == t.getEndOffset();
	}
	
	@Override
	public String toString(){
		return this.value;
	}

	public int getId() {
		return this.id;
	}

}
