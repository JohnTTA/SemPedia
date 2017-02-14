package org.mlhypernymextractor.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws UnsupportedEncodingException, IOException {
		String sentenceFilePath = "C:\\Users\\ghamnia\\workspace\\corpus_homonymie\\homonymie expe 2016\\with_handlabled_terms\\results.txt";
		String pairsFilePath = "C:\\Users\\ghamnia\\workspace\\corpus_homonymie\\homonymie expe 2016\\with_handlabled_terms\\ttg-all-pairs-result.txt";
		String output = "C:\\Users\\ghamnia\\workspace\\corpus_homonymie\\homonymie expe 2016\\with_handlabled_terms\\results-with-pairs.txt";

		File inSentenceFile = new File(sentenceFilePath);
		BufferedReader inSentences = new BufferedReader(
				   new InputStreamReader(
		                      new FileInputStream(inSentenceFile), "UTF8"));
		File inPairsFile = new File(pairsFilePath);
		BufferedReader inPairs = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(inPairsFile), "UTF8"));
		File outpuFile = new File(output);
		Writer out = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outpuFile), "UTF-8"));

		String strSentences, strPairs;
		while ((strSentences = inSentences.readLine()) != null && (strPairs = inPairs.readLine()) != null) {
			String[] pairsLine = strPairs.split("; ");
			String pair = pairsLine[0];
			String[] terms = pair.split(", ");
			String term1 = terms[0];
			String term2 = terms[1];
			
			String[] sentenceLine = strSentences.split("\t");
			String maxentResult = sentenceLine[1];
			out.append(term1+", "+term2+"\t"+maxentResult+"\n");
			out.flush();
			
		}
		inPairs.close();
		inSentences.close();
		out.close();
	}

}
