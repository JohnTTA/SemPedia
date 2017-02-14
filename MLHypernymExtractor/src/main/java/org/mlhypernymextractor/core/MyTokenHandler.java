package org.mlhypernymextractor.core;
import org.annolab.tt4j.TokenHandler;


public class MyTokenHandler implements TokenHandler<String>{
	String ttgString = "";

	public String getTTGString(){
		return this.ttgString;
	}

	@Override
	public void token(String token, String pos, String lemma) {
	this.ttgString += token+"::"+pos+"::"+lemma+" ";
//		System.out.println(token+"\t"+pos+"\t"+lemma);
		
	}
}
