package org.mlhypernymextractor.core;

import java.io.Serializable;

public class Annotation implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2795857095621648264L;
	protected int startOffset;
	protected int endOffset;
	protected boolean isSentence = false;
	
	public Annotation(int a, int b ){
		this.startOffset = a;
		this.endOffset = b;
	}

	public int getStartOffset() {
		return startOffset;
	}

	public void setStartOffset(int startOffset) {
		this.startOffset = startOffset;
	}

	public int getEndOffset() {
		return endOffset;
	}

	public void setEndOffset(int endOffset) {
		this.endOffset = endOffset;
	}

	public boolean isSentence() {
		return isSentence;
	}

	public void setIsSentence(boolean isSentence) {
		this.isSentence = isSentence;
	}
	
	
}
