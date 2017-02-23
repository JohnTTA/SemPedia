package org.mlhypernymextractor.core;
import java.util.ArrayList;
import java.util.List;

import org.annolab.tt4j.TokenHandler;


public class MyTokenHandler implements TokenHandler<String>{
	List<String> ttgString = new ArrayList<>();

	public String[] getTTGString(){
		return this.ttgString.toArray(new String[ttgString.size()]);
	}

	@Override
	public void token(String token, String pos, String lemma) {
//	this.ttgString += token+"::"+pos+"::"+lemma+" ";
	this.ttgString.add(pos+"/"+lemma);
	}
}
