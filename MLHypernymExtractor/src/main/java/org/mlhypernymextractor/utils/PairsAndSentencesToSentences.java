package org.mlhypernymextractor.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class PairsAndSentencesToSentences {
	public static void main(String[] args) throws IOException {
		String filePath = "C:\\Users\\ghamnia\\workspace\\TextToBabelNetValidation\\jape_rules\\corpus\\test_data_with_pairs.txt";
		String output = "C:\\Users\\ghamnia\\workspace\\TextToBabelNetValidation\\jape_rules\\corpus\\test_data.txt";

		File inFile = new File(filePath);
		BufferedReader in = new BufferedReader(
				   new InputStreamReader(
		                      new FileInputStream(inFile), "UTF8"));
		File outpuFile = new File(output);
		Writer out = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(outpuFile), "UTF-8"));

		//Un exemple = T1, T2; sentence 0ou1
		String str;
		while ((str = in.readLine()) != null) {
			String[] line = str.split("; ");
			String[] subline = line[1].split("\t");
			String sentence = subline[0];
			out.append(sentence+"\n");
		}
		in.close();
		out.close();
	}
}
