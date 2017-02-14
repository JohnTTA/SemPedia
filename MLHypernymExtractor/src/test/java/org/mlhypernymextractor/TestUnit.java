package org.mlhypernymextractor;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.junit.Test;
import org.mlhypernymextractor.core.GateResultFile;
import org.mlhypernymextractor.core.Pair;
import org.mlhypernymextractor.core.Sentence;
import org.mlhypernymextractor.core.Term;

public class TestUnit {

	@Test
	public void generateMultipleTermsCombinationTest()
			throws MalformedURLException {
		Sentence sentence1 = new Sentence(-1, -1, 0,
				"Porte est un nom commun et un nom propre.");
		Term term11 = new Term(-1, -1, "nom", false);
		Term term12 = new Term(-1, -1, "commun", false);
		Term term13 = new Term(-1, -1, "propre", false);
		Term term14 = new Term(-1, -1, "Porte", false);

		ArrayList<Pair> expectedPairs = new ArrayList<>();
		sentence1.addTerm(term11);
		sentence1.addTerm(term12);
		sentence1.addTerm(term13);
		sentence1.addTerm(term14);

		Pair pair1 = new Pair(term11, term12);
		Pair pair2 = new Pair(term11, term13);
		Pair pair3 = new Pair(term11, term14);
		Pair pair4 = new Pair(term12, term13);
		Pair pair5 = new Pair(term12, term14);
		Pair pair6 = new Pair(term13, term14);
		expectedPairs.add(pair1);
		expectedPairs.add(pair2);
		expectedPairs.add(pair3);
		expectedPairs.add(pair4);
		expectedPairs.add(pair5);
		expectedPairs.add(pair6);

		Sentence sentence2 = new Sentence(
				-1,
				-1,
				1,
				"La R�publique de Mac�doine, Ancienne R�publique Yougoslave de Mac�doine (ARYM ou FYROM en anglais), ou simplement Mac�doine est un �tat d�Europe du Sud, situ� dans la p�ninsule balkanique, ind�pendant depuis 1991.");
		Term term21 = new Term(-1, -1, "R�publique", false);
		Term term22 = new Term(-1, -1, "Mac�doine", false);
		Term term23 = new Term(-1, -1, "Yougoslave", false);
		Term term24 = new Term(-1, -1, "Mac�doine", false);
		Term term25 = new Term(-1, -1, "Europe du Sud", false);
		Term term26 = new Term(-1, -1, "balkanique", false);

		sentence2.addTerm(term21);
		sentence2.addTerm(term22);
		sentence2.addTerm(term23);
		sentence2.addTerm(term24);
		sentence2.addTerm(term25);
		sentence2.addTerm(term26);
		Pair pair7 = new Pair(term21, term22);
		Pair pair8 = new Pair(term21, term23);
		Pair pair9 = new Pair(term21, term24);
		Pair pair10 = new Pair(term21, term25);
		Pair pair11 = new Pair(term21, term26);
		Pair pair12 = new Pair(term22, term23);
		Pair pair13 = new Pair(term22, term25);
		Pair pair14 = new Pair(term22, term26);
		Pair pair15 = new Pair(term23, term24);
		Pair pair16 = new Pair(term23, term25);
		Pair pair17 = new Pair(term23, term26);
		Pair pair18 = new Pair(term24, term25);
		Pair pair19 = new Pair(term24, term26);
		Pair pair20 = new Pair(term25, term26);
		expectedPairs.add(pair7);
		expectedPairs.add(pair8);
		expectedPairs.add(pair9);
		expectedPairs.add(pair10);
		expectedPairs.add(pair11);
		expectedPairs.add(pair12);
		expectedPairs.add(pair13);
		expectedPairs.add(pair14);
		expectedPairs.add(pair15);
		expectedPairs.add(pair16);
		expectedPairs.add(pair17);
		expectedPairs.add(pair18);
		expectedPairs.add(pair19);
		expectedPairs.add(pair20);
		ArrayList<Sentence> sentences = new ArrayList<>();
		sentences.add(sentence1);
		sentences.add(sentence2);

		GateResultFile myFile = new GateResultFile(sentences, new URL("http",
				"test", "/"));

		myFile.extractPairs(true);

		ArrayList<Pair> pairs = myFile.getPairsOfTerms();
		assertNotNull(pairs);
		assertEquals(expectedPairs.size(), pairs.size());

		for (int i = 0; i < 20; i++) {
			assertEquals(expectedPairs.get(i).getTerm1().getValue(),
					pairs.get(i).getTerm1().getValue());
			assertEquals(expectedPairs.get(i).getTerm2().getValue(),
					pairs.get(i).getTerm2().getValue());
		}

	}
	
	@Test
	public void myTest(){
		long time = System.currentTimeMillis();
		System.out.println(time);
		long time2 = time+(long)10000;
		boolean test = false;
		while(!test){
			long current = System.currentTimeMillis();
			int finish = new Long(time2).compareTo(new Long(current));
			test = finish<0;
			
		}
		System.out.println(time2 == time2);
		System.out.println("time 1 = " + time);
		System.out.println("time 2 = " + time2);
		System.out.println("time 2 = " + (long)(time+(long)2));
		
	}

}
