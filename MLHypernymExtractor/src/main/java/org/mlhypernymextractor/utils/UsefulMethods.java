package org.mlhypernymextractor.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;

public class UsefulMethods {
	public static void staxSkipToTheNextStartElement(XMLEventReader reader) throws XMLStreamException{
		while (!reader.nextEvent().isStartElement()) {
		}
	}
	
	public static Writer openUTF8OutputWriter(File file) throws UnsupportedEncodingException, FileNotFoundException{
		return new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file), "UTF-8"));
	}
	
	public static void closeUTF8OutputWriter(Writer writer) throws IOException{
		writer.flush();
		writer.close();
	}
	
	public static BufferedReader openUTF8BufferedReader(File file) throws UnsupportedEncodingException, FileNotFoundException{
		return new BufferedReader(new InputStreamReader(
				new FileInputStream(file), "UTF-8"));
	}
	
	public static void closeUTF8BufferedReader(BufferedReader reader) throws IOException{
			reader.close();
	}
	
	public static String[] tokenize(String sentence) {
		List<String> tokens = new ArrayList<String>();
		BreakIterator bi = BreakIterator.getWordInstance();
		bi.setText(sentence);
		int begin = bi.first();
		int end;
		for (end = bi.next(); end != BreakIterator.DONE; end = bi.next()) {
			String t = sentence.substring(begin, end);
			if (t.trim().length() > 0) {
				tokens.add(sentence.substring(begin, end));
			}
			begin = end;
		}
		if (end != -1) {
			tokens.add(sentence.substring(end));
		}
		return tokens.toArray(new String[tokens.size()]);
	}
	
	public static int[] getTermIndexInSentenceTokens(String term,
			String[] sentenceTokens, int termNbr) {
		int[] res = new int[2];
		String termTokens[] = UsefulMethods.tokenize(term);
		int startIndex = 0;
		int endIndex = 0;
		for (int i = 0; i < sentenceTokens.length; i++) {
			if (sentenceTokens[i].equals(termTokens[0])) {
				int j = 1;
//				if(termNbr == 1)
//					sentenceTokens[i]="Terme1";
//				else
//					sentenceTokens[i]="Terme2";
				i++;
				while (i < sentenceTokens.length && j < termTokens.length) {
					if (sentenceTokens[i].equals(termTokens[j])){
//						if(termNbr == 1)
//							sentenceTokens[i]="Terme1";
//						else
//							sentenceTokens[i]="Terme2";
						endIndex++;
					}
					i++;
					j++;
				}
				if(termTokens.length == endIndex-startIndex+1){
					res[0] = startIndex;
					res[1] = endIndex;
					return res;
				}else return null;
			}
			startIndex++;
			endIndex++;
		}
		return null;
	}
}
