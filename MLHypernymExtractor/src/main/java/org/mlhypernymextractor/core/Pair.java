package org.mlhypernymextractor.core;
public class Pair {
	private Term term1;
	private Term term2;
	private int freq = 0;
	private int type;
	
	private Sentence sentence;

	public Pair(Term t1, Term t2) {
		this.term1 = t1;
		this.term2 = t2;

		if (t1.getSentence().getId() == t2.getSentence().getId())
			this.sentence = t1.getSentence();
		else
			this.sentence = null;
	}

	public Term getTerm1() {
		return term1;
	}

	public void setTerm1(Term term1) {
		this.term1 = term1;
	}

	public Term getTerm2() {
		return term2;
	}

	public void setTerm2(Term term2) {
		this.term2 = term2;
	}

	public Sentence getSentence() {
		return sentence;
	}

	public void setSentence(Sentence sentence) {
		this.sentence = sentence;
	}
	
	
	
	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getFreq() {
		return freq;
	}

	public void setFreq(int freq) {
		this.freq = freq;
	}

	
	
	@Override
	public boolean equals(Object p) {
		return (this.term1.getValue().equals(((Pair) p).getTerm1().getValue()) && this.term2
				.getValue().equals(((Pair) p).getTerm2().getValue()))
				|| (this.term1.getValue().equals(
						((Pair) p).getTerm2().getValue()) && this.term2
						.getValue().equals(((Pair) p).getTerm1().getValue()));
	}

	public String getTypeStr() {
		switch (this.type) {
		case Main.POSITIF:
			return "+";
		case Main.NEGATIF:
			return "-";
		case Main.OTHER:
			return "?";
		default:
			return "";
		}
	}
}
