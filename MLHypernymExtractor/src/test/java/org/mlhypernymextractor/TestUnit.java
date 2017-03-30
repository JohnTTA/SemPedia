package org.mlhypernymextractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;
import gate.util.OffsetComparator;
import gate.util.Out;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.HashedMap;
import org.junit.Test;
import org.mlhypernymextractor.core.GateResultFile;
import org.mlhypernymextractor.core.Pair;
import org.mlhypernymextractor.core.Sentence;
import org.mlhypernymextractor.core.Term;
import org.mlhypernymextractor.utils.UsefulMethods;

public class TestUnit {

	// @Test
	public void parserTest() throws ResourceInstantiationException, IOException {
		File file = new File(
				"C:\\Users\\ghamnia\\workspace\\expérimentations\\extraction de relations d'hyperonymie\\les pages d'homonymie\\corpus\\20 pages\\texte à la main\\annotations patrons\\2\\corpus.txt_0000A.xml");
		GateResultFile gate = new GateResultFile(file.toURI().toURL());
		gate.parse();

	}

	// @Test
	public void generateMultipleTermsCombinationTest()
			throws MalformedURLException {
		Sentence sentence1 = new Sentence(-1, -1, 0,
				"Porte est un nom commun et un nom propre.");
		Term term11 = new Term(-1, -1, "nom",-1, false);
		Term term12 = new Term(-1, -1, "commun",-1, false);
		Term term13 = new Term(-1, -1, "propre",-1, false);
		Term term14 = new Term(-1, -1, "Porte",-1, false);

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
		Term term21 = new Term(-1, -1, "République", -1,false);
		Term term22 = new Term(-1, -1, "Macédoine", -1,false);
		Term term23 = new Term(-1, -1, "Yougoslave", -1,false);
		Term term24 = new Term(-1, -1, "Macédoine", -1,false);
		Term term25 = new Term(-1, -1, "Europe du Sud", -1,false);
		Term term26 = new Term(-1, -1, "balkanique", -1,false);

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

//		GateResultFile myFile = new GateResultFile(sentences, new URL("http",
//				"test", "/"));

		// myFile.extractPairs(true);

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

	// @Test
	// public void myTest() throws Exception {
//	 Gate.init();
//	 File file = new File(
//	 "C:\\Users\\ghamnia\\dump 2016\\homonymie\\corpus\\demo\\results\\Babel_0000A.xml");
//	 GateResultFile test = new GateResultFile(file.toURI().toURL());
	// test.parse();
	// }

//	 @Test
	public void featuresTest() throws Exception {
		 
		String var = "vn"; 
		File file = new File("final_"+var+".txt");
		List<String> lines = Files.readAllLines(file.toPath(), Charset.forName("UTF-8"));
		
		File out = new File("final_"+var+"_features.txt");
		out.createNewFile();
		
		Writer w = UsefulMethods.openUTF8OutputWriter(out);
		
		File outPairs = new File("final_"+var+"_pairs.txt");
		out.createNewFile();
		
		Writer wpairs = UsefulMethods.openUTF8OutputWriter(outPairs);
		
		
		for (String line : lines) {
//			Sentence sentence1 = new Sentence(-1, -1, 0,
//					"a est un nom commun un blabla courant");
//			
//			Term term11 = new Term(-1, -1, "nom commun",-1, false);
//			Term term13 = new Term(-1, -1, "courant",-1, false);
			
			String[] lineArr = line.split("\t");
//			System.out.println(line);
//			System.out.println(lineArr[0]);
			Sentence sentence1 = new Sentence(-1, -1, 0,
					lineArr[2]);
			
			Term term11 = new Term(-1, -1, lineArr[0].trim(),-1, false);
			Term term13 = new Term(-1, -1, lineArr[1].trim(),-1, false);
			
			
			
			sentence1.addTerm(term11);
			sentence1.addTerm(term13);
			
			Pair p = new Pair(term11, term13);
			p.createFeatures();
			w.append(p.getFeatures().toString()+"\n");
			
			wpairs.append(term11.getValue()+"\t"+term13.getValue()+"\n");
			
		}
		w.flush();
		wpairs.flush();
		w.close();
		wpairs.close();
		
//		System.out.println(pair.getFeatures().toString());
	}

//	 @Test
	public void evalPatterns() throws IOException {
		int vp = 0;
		int fn = 0;
		int fp = 0;
		int vn=0;
		File handlabledAnnotationsFile = new File("handlabeledAnno.txt");
		List<String> handlabledAnnotations = Files.readAllLines(
				handlabledAnnotationsFile.toPath(), Charset.forName("UTF-8"));
		
		File patternsAnnotationsFile = new File("patrons_res.txt");
		List<String> patternsAnnotations = Files.readAllLines(
				patternsAnnotationsFile.toPath(), Charset.forName("UTF-8"));
		boolean isFp = false;
		
		File f = new File("patternVN.txt");
		f.createNewFile();
		Writer out = UsefulMethods.openUTF8OutputWriter(f);
		for (int i=0; i<patternsAnnotations.size(); i++) {
			String patternAnnotation = patternsAnnotations.get(i);
			if (patternAnnotation.contains("\t")) {
				String[] patternTerms = patternAnnotation.split("\t");
				for (int j=0; j<handlabledAnnotations.size(); j++) {
					String handlabledAnnotation = handlabledAnnotations.get(j);
					String[] handlabledTerms = handlabledAnnotation.split("\t");
					if (matched(handlabledTerms, patternTerms)) {
						vp++;
						j=handlabledAnnotations.size();
//						System.out.println(handlabledTerms[0]+'\t'+handlabledTerms[1]);
						isFp=false;
					} else {
						if(j==handlabledAnnotations.size()-1)
							if(patternTerms.length>1)
						out.append(patternTerms[0]+"\t"+patternTerms[1]+"\n");
						isFp=true;
					}
				}
				
			} else {
			}
			if(isFp)
				System.out.println(patternAnnotation);
			isFp=false;
		}
		out.close();
		double rappel = (double) 320 / 694;
		double precision = (double) 320 / (patternsAnnotations.size());
		System.out.println(vp);
		System.out.println(handlabledAnnotations.size());
		System.out.println("rappel = " + rappel + "\t" + "précision = "
				+ precision);
	}

//	@Test
	public void generateVF() throws IOException{
		File outVPFile = new File("vpfile.txt");
		outVPFile.createNewFile();
		Writer outVP = UsefulMethods.openUTF8OutputWriter(outVPFile);
		
		File outVNFile = new File("vnFile.txt");
		outVNFile.createNewFile();
		Writer outVN = UsefulMethods.openUTF8OutputWriter(outVNFile);
		
		
		File handlabledAnnotationsFile = new File("handlabeledAnno - Copie.txt");
		List<String> handlabledAnnotations = Files.readAllLines(
				handlabledAnnotationsFile.toPath(), Charset.forName("UTF-8"));
		
		File maxEntAnnotationsPairsFile = new File("maxent_pairs.txt");
		List<String> maxEntAnnotationsPairs = Files.readAllLines(
				maxEntAnnotationsPairsFile.toPath(), Charset.forName("UTF-8"));
		
		File maxEntAnnotationsFile = new File("maxent_res.txt");
		List<String> maxEntAnnotations = Files.readAllLines(
				maxEntAnnotationsFile.toPath(), Charset.forName("UTF-8"));
		
		if(maxEntAnnotations.size() == maxEntAnnotationsPairs.size())
			System.err.println("c bon");
		else System.err.println(maxEntAnnotations.size()+"\t"+maxEntAnnotationsPairs.size());
		
		for (int i = 0; i < maxEntAnnotationsPairs.size(); i++) {
			boolean found = false;
			String[] maxEntAnnotationPairs = maxEntAnnotationsPairs.get(i).split("\t");
			for (int j=0 ; j<handlabledAnnotations.size(); j++) {
				String[] handlabledTerms = handlabledAnnotations.get(j).split("\t");
				if(matched(handlabledTerms, maxEntAnnotationPairs)){
					outVP.append(maxEntAnnotationPairs[0]+"\t"+maxEntAnnotationPairs[1]+"\t"+maxEntAnnotations.get(i).substring(maxEntAnnotations.get(i).length()-1, maxEntAnnotations.get(i).length())+"\n");
					found=true;
					j=handlabledAnnotations.size();
				}else{
					found=false;
				}
			}
			if(!found){
				if(maxEntAnnotationPairs.length>1)
				outVN.append(maxEntAnnotationPairs[0]+"\t"+maxEntAnnotationPairs[1]+"\t"+maxEntAnnotations.get(i).substring(maxEntAnnotations.get(i).length()-1, maxEntAnnotations.get(i).length())+"\n");
			}
		}
		outVN.flush();
		outVN.close();
		outVP.flush();
		outVP.close();
	}
	
	
//	@Test 
	public void deleteMotVide() throws IOException{
		File termFile = new File("termes_babelnet.lst");
		List<String> terms = Files.readAllLines(termFile.toPath(),  Charset.forName("UTF-8"));
		File motvidesFile = new File("mots_vides.lst");
		List<String> motvides= Files.readAllLines(motvidesFile.toPath(),  Charset.forName("UTF-8"));
		
		for (int i = 0; i < motvides.size(); i++) {
			for (int j = 0; j < terms.size(); j++) {
				if(motvides.get(i).toLowerCase().trim().equals(terms.get(j).toLowerCase().trim()))
					terms.remove(j);
			}
		}
		
		File res = new File("termes_final.lst");
		res.createNewFile();
		Writer out = UsefulMethods.openUTF8OutputWriter(res);
		for (String string : terms) {
			out.append(string+"\n");
		}
		out.flush();
		out.close();
	}
	
//	
//	@Test
	public void getSentences() throws IOException, GateException{
		File pairFile = new File("res_pairs_man.txt");
		List<String> pairList = Files.readAllLines(pairFile.toPath(), Charset.forName("UTF-8"));
		
		File maxEntFile = new File("pairs.txt");
		List<String> maxentList = Files.readAllLines(maxEntFile.toPath(), Charset.forName("UTF-8"));
		Gate.init();
		File file = new File("corpus_20_inclus_lexi.xml");
		Document gateDoc = Factory.newDocument(file.toURI().toURL());
		File sentenceFile = new File("with_setences.txt");
		sentenceFile.createNewFile();
		Writer out = UsefulMethods.openUTF8OutputWriter(sentenceFile);
		for (String pairLine : pairList) {
			
			String[] line = pairLine.split("\t");
			String term1 = line[1];
			String term2 = line[2];
			
			for (int i = 0; i < maxentList.size(); i++) {
				String maxentLine = maxentList.get(i);
				if(matched(term1, term2, maxentLine)){
					String[] maxentTokens= maxentLine.split("\t");
					int id = Integer.valueOf(maxentTokens[2]);
					
					gate.Annotation sentence = gateDoc.getAnnotations().get(id);
					String sentenceValue = gateDoc.getContent().toString().substring(sentence.getStartNode().getOffset().intValue(), sentence.getEndNode().getOffset().intValue());
					out.append(pairLine+"\t"+sentenceValue.replaceAll("\n", " ").replaceAll("\r"," ")+"\n");
					i = maxentList.size();
				}
				else if(i==maxentList.size()-1){
					out.append(pairLine+"\t"+"\n");
				}
			}
			
		}
		out.flush();
		out.close();
	}
	private boolean matched(String term1, String term2, String maxentLine) {
		boolean term1Found = false;
		boolean term2Found = false;
		String[] maxentTerms = maxentLine.split("\t");
		for (int i = 0; i < maxentTerms.length; i++) {
			if(term1.equals(maxentTerms[i]))
				term1Found = true;
			if(term2.equals(maxentTerms[i]))
				term2Found = true;
		}
		
		return term1Found && term2Found;
	}

//	@Test
	public void generateVNRandom() throws IOException, FileNotFoundException{
		File vnFile = new File("vnFile.txt");
		List<String> vnAnnotations = Files.readAllLines(
				vnFile.toPath(), Charset.forName("UTF-8"));
		File generatedVN = new File("generatedVNFile.txt");
		generatedVN.createNewFile();
		Writer out = UsefulMethods.openUTF8OutputWriter(generatedVN);
		
		Collections.shuffle(vnAnnotations);
		
		for(int i =0; i<600; i++){
			out.append(vnAnnotations.get(i)+"\n");
		}
		out.flush();
		out.close();
		
	}
	
	
	@Test
	public void compareMaxentHand() throws IOException {
		int vp = 0;
		int fn = 0;
		int fp = 0;
		int p = 0;

		File handlabledAnnotationsFile = new File("anno_vn.txt");
		List<String> handlabledAnnotations = Files.readAllLines(
				handlabledAnnotationsFile.toPath(), Charset.forName("UTF-8"));

		File maxEntAnnotationsFile = new File("maxent_res.txt");
		List<String> maxEntAnnotations = Files.readAllLines(
				maxEntAnnotationsFile.toPath(), Charset.forName("UTF-8"));

		File maxEntAnnotationsPairsFile = new File("maxent_pairs.txt");
		List<String> maxEntAnnotationsPairs = Files.readAllLines(
				maxEntAnnotationsPairsFile.toPath(), Charset.forName("UTF-8"));
		System.out.println(maxEntAnnotations.size());

		File compare = new File("comparaison_maxent_hand.txt");
		Writer out = UsefulMethods.openUTF8OutputWriter(compare);

		for (String handlabledAnnotation : handlabledAnnotations) {
			String[] handlabledTerms = handlabledAnnotation.split("\t");

			for (int i = 0; i < maxEntAnnotations.size(); i++) {
				String maxEntAnnotation = maxEntAnnotations.get(i);
				if (maxEntAnnotation.endsWith("1")){
					if(i<maxEntAnnotationsPairs.size()){
					if (maxEntAnnotationsPairs.get(i).contains("\t")) {
						String[] maxEntTerms = maxEntAnnotationsPairs.get(i)
								.split("\t");
						if (matched(handlabledTerms, maxEntTerms) ) {
							vp++;
							if (maxEntAnnotation.endsWith("1")) {
								out.append(handlabledAnnotation + "\t" + "1");
								out.append("\n");
								i = maxEntAnnotations.size();
							} else if (maxEntAnnotation.endsWith("0")) {
								out.append(handlabledAnnotation + "\t" + "0");
								out.append("\n");
								i = maxEntAnnotations.size();
							}
						} else {
							if (i == maxEntAnnotations.size() - 1) {
								out.append(handlabledAnnotation + "\t" + "0");
								out.append("\n");
							}
						}
					}
					}else {
						if (i == maxEntAnnotations.size() - 1) {
							out.append(handlabledAnnotation + "\t" + "0");
							out.append("\n");
						}
					}
					
				}else {
					if (i == maxEntAnnotations.size() - 1) {
						out.append(handlabledAnnotation + "\t" + "0");
						out.append("\n");
					}
				}
			}
		}
		out.flush();
		out.close();
		System.out.println(vp);
		double rappel = (double) vp / (handlabledAnnotations.size());
		double precision = (double) vp / (p);

		// System.out.println("rappel = " + rappel + "\t" + "précision = "
		// + precision);
	}

//	 @Test
	public void evalMaxent() throws IOException {
		int vp = 0;
		int fn = 0;
		int fp = 0;
		int p = 0;

		File handlabledAnnotationsFile = new File("handlabeledAnno.txt");
		List<String> handlabledAnnotations = Files.readAllLines(
				handlabledAnnotationsFile.toPath(), Charset.forName("UTF-8"));

		File maxEntAnnotationsFile = new File("maxent_res_pairs.txt");
		List<String> maxEntAnnotations = Files.readAllLines(
				maxEntAnnotationsFile.toPath(), Charset.forName("UTF-8"));

		
		for (String maxEntAnnotation : maxEntAnnotations) {
			if (maxEntAnnotation.contains("\t")) {
				if (maxEntAnnotation.endsWith("1")) {
					p++;
					String[] patternTerms = maxEntAnnotation.split("\t");
					for (String handlabledAnnotation : handlabledAnnotations) {
						String[] handlabledTerms = handlabledAnnotation
								.split("\t");
						if (matched(handlabledTerms, patternTerms)) {
							vp++;
						} else {
						}
					}
				}
			} else {
			}
		}
		System.out.println(vp);
		double rappel = (double) vp / (handlabledAnnotations.size());
		double precision = (double) vp / (p);

		System.out.println("rappel = " + rappel + "\t" + "précision = "
				+ precision);
	}

	private boolean matched(String[] handlabledTerms, String[] patternTerms) {
		String term1 = handlabledTerms[0];
		String term2 = handlabledTerms[1];

		boolean term1Found = false;
		boolean term2Found = false;

		for (int i = 0; i < patternTerms.length; i++) {
			if (patternTerms[i].toLowerCase().trim().equals(term1.toLowerCase().trim()))
				term1Found = true;
			if (patternTerms[i].toLowerCase().trim().equals(term2.toLowerCase().trim()))
				term2Found = true;
		}

		return term1Found && term2Found;
	}

	// @Test
	public void getTermsAnnotatedByPatterns() throws GateException, IOException {
		Gate.init();
		File f = new File("new.xml");
		Document doc = Factory.newDocument(f.toURI().toURL());
		File file = new File("patrons_res.txt");
		Writer out = UsefulMethods.openUTF8OutputWriter(file);
		AnnotationSet hypernyms = doc.getAnnotations().get("hyperonymie");
		List<gate.Annotation> hypernymsList = new ArrayList<>(hypernyms);
		Collections.sort(hypernymsList, new OffsetComparator());
		AnnotationSet terms = doc.getAnnotations().get("Terme");
		List<gate.Annotation> termsList = new ArrayList<>(terms);
		Collections.sort(termsList, new OffsetComparator());

		for (gate.Annotation hyperAnnotation : hypernymsList) {
			for (gate.Annotation terme : termsList) {
				if (terme.overlaps(hyperAnnotation)) {
					System.out
							.print(doc
									.getContent()
									.toString()
									.substring(
											terme.getStartNode().getOffset()
													.intValue(),
											terme.getEndNode().getOffset()
													.intValue())
									+ "\t");
					out.write(doc
							.getContent()
							.toString()
							.substring(
									terme.getStartNode().getOffset().intValue(),
									terme.getEndNode().getOffset().intValue())
							+ "\t");
				}

			}
			out.write("\n");
			System.out.print("\n");
		}

		out.flush();
		out.close();

	}

	// @Test
	public void getTermIndexInSentenceTokensTest() throws GateException,
			IOException {
		Gate.init();
		File f = new File("hyperonymies.xml");
		Document doc = Factory.newDocument(f.toURI().toURL());

		AnnotationSet lookupAnnotations = doc.getAnnotations().get("Lookup");
		List<gate.Annotation> lookupAnnotationsList = new ArrayList<>(
				lookupAnnotations);
		AnnotationSet conceptXAnnotations = doc.getAnnotations()
				.get("ConceptX");
		List<gate.Annotation> conceptXAnnotationsList = new ArrayList<>(
				conceptXAnnotations);
		AnnotationSet conceptYAnnotations = doc.getAnnotations()
				.get("ConceptY");
		List<gate.Annotation> conceptYAnnotationsList = new ArrayList<>(
				conceptYAnnotations);
		File out = new File("res.txt");
		out.createNewFile();
		Writer wr = UsefulMethods.openUTF8OutputWriter(out);
		Collections.sort(lookupAnnotationsList, new OffsetComparator());

		List<gate.Annotation> conceptAnnotationsList = new ArrayList<>();
		conceptAnnotationsList.addAll(conceptXAnnotationsList);
		conceptAnnotationsList.addAll(conceptYAnnotationsList);

		Collections.sort(conceptAnnotationsList, new OffsetComparator());

		int conceptParsed = 0;
		for (int i = 0; i < lookupAnnotationsList.size(); i++) {
			gate.Annotation lookup = lookupAnnotationsList.get(i);
			int lookupStart = lookup.getStartNode().getOffset().intValue();
			int lookupEnd = lookup.getEndNode().getOffset().intValue();
			for (int j = conceptParsed; j < conceptAnnotationsList.size(); j++) {
				gate.Annotation concept = conceptAnnotationsList.get(j);
				int conceptStart = concept.getStartNode().getOffset()
						.intValue();
				int conceptEnd = concept.getEndNode().getOffset().intValue();

				if (conceptStart > lookupEnd) {

				} else if (lookup.overlaps(concept)) {
					doc.getAnnotations()
							.add(lookup.getStartNode(), lookup.getEndNode(),
									"Terme", concept.getFeatures());
					doc.getAnnotations().remove(concept);
				}

			}
		}
		File file = new File("new.xml");
		file.createNewFile();

		Writer outXml = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file), "UTF-8"));
		outXml.write(doc.toXml());
		outXml.close();
		// for (gate.Annotation lookupAnnotation : term2Annotations) {
		// wr.append(doc.getContent().toString().substring(lookupAnnotation.getStartNode().getOffset().intValue(),
		// lookupAnnotation.getEndNode().getOffset().intValue()));
		// wr.append("\n");
		// }
		wr.flush();
		wr.close();

	}

	// @Test
	public void termes() throws IOException {
		BufferedReader reader = UsefulMethods.openUTF8BufferedReader(new File(
				"termes_beta.txt"));
		File file = new File("termes.txt");
		file.createNewFile();
		Writer out = UsefulMethods.openUTF8OutputWriter(file);

		HashedMap termes = new HashedMap();
		String str;
		while ((str = reader.readLine()) != null) {
			String t = str.trim();
			termes.put(t, t);
		}

		Iterator it = termes.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			out.write(entry.getKey().toString());
			out.write("\n");

		}
		out.flush();
		out.close();
		reader.close();
	}
}
