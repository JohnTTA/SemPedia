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
		String resFilePath = "results.txt";
		String pairsFilePath = "corpus_0000A_pairs.txt";
		String output = "results-maxent-with-pairs.txt";

		File resFile = new File(resFilePath);
		BufferedReader resReader = new BufferedReader(
				   new InputStreamReader(
		                      new FileInputStream(resFile), "UTF8"));
		File inPairsFile = new File(pairsFilePath);
		BufferedReader inPairs = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(inPairsFile), "UTF8"));
		File outpuFile = new File(output);
		Writer out = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outpuFile), "UTF-8"));

		String strRes, strPairs;
		while ((strRes = resReader.readLine()) != null && (strPairs = inPairs.readLine()) != null) {
			
			out.append(strPairs+"\t"+strRes.charAt(strRes.lastIndexOf("\t")+1)+"\n");
			out.flush();
		}
		inPairs.close();
		resReader.close();
		out.close();
	}

}
