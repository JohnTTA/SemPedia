package org.mlhypernymextractor.core;

import gate.util.Out;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.annolab.tt4j.TreeTaggerException;
import org.annolab.tt4j.TreeTaggerWrapper;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.io.FilenameUtils;

public class GateResultFile {

	private URL gateResultFileURL;
	private ArrayList<Sentence> sentences = new ArrayList<>();
	private ArrayList<Pair> pairsOfTerms = new ArrayList<>();
	private URL pairsOfTermsFileURL;

	private ArrayList<Pair> finalPositifExamples = new ArrayList<>(); // les
																		// exemples
																		// + �
																		// utiliser
																		// dans
																		// l'apprentissage
	private ArrayList<Pair> negatifExamples = new ArrayList<>(); // les exemples
																	// - sont
																	// souvent
																	// plus
																	// nombreux
																	// que les
																	// exemples
																	// +
	private ArrayList<Pair> finalNegatifExamples = new ArrayList<>(); // les
																		// exemples
																		// - �
																		// utiliser
																		// dans
																		// l'apprentissage

	public GateResultFile(ArrayList<Sentence> l, URL u) {
		this.gateResultFileURL = u;
		this.sentences = l;
	}

	public GateResultFile(URL u) {
		this.gateResultFileURL = u;
	}

	public ArrayList<Sentence> getSentences() {
		return sentences;
	}

	public void setSentences(ArrayList<Sentence> sentences) {
		this.sentences = sentences;
	}

	public void addSentence(Sentence s) {
		this.sentences.add(s);
	}

	public void addPair(Pair p) {
		this.pairsOfTerms.add(p);
	}

	public URL getGateResultFileURL() {
		return gateResultFileURL;
	}

	public void setGateResultFileURL(URL url) {
		this.gateResultFileURL = url;
	}

	public URL getPairsOfTermsFileURL() {
		return pairsOfTermsFileURL;
	}

	public void setPairsOfTermsFileURL(URL pairsOfTermsFileURL) {
		this.pairsOfTermsFileURL = pairsOfTermsFileURL;
	}

	public ArrayList<Pair> getPairsOfTerms() {
		return pairsOfTerms;
	}

	public void setPairsOfTerms(ArrayList<Pair> pairsOfTerms) {
		this.pairsOfTerms = pairsOfTerms;
	}

	public ArrayList<Pair> getFinalPositifExamples() {
		return finalPositifExamples;
	}

	public void setFinalPositifExamples(ArrayList<Pair> finalPositifExamples) {
		this.finalPositifExamples = finalPositifExamples;
	}

	public ArrayList<Pair> getNegatifExamples() {
		return negatifExamples;
	}

	public void setNegatifExamples(ArrayList<Pair> negatifExamples) {
		this.negatifExamples = negatifExamples;
	}

	public ArrayList<Pair> getFinalNegatifExamples() {
		return finalNegatifExamples;
	}

	public void setFinalNegatifExamples(ArrayList<Pair> finalNegatifExamples) {
		this.finalNegatifExamples = finalNegatifExamples;
	}

	public void addFinalPositifExamples(Pair p) {
		this.finalPositifExamples.add(p);
	}

	public void addNegatifExamples(Pair p) {
		this.negatifExamples.add(p);
	}

	public void extractPairs(boolean multipleTermsCombination) {
		if (multipleTermsCombination) {
			this.generateMultipleTermsCombination();
		} else
			this.generateRandomlyTermsPairs();

	}

	private void generateMultipleTermsCombination() {
		Out.prln("Generating pairs for : "+this.gateResultFileURL.getPath());
		for (Sentence s : this.sentences) {
			int i = 0;
			while (i < s.getTerms().size() - 1) {
				for (Term termB : s.getTerms().subList(i + 1,
						s.getTerms().size())) {
					Term termA = s.getTerms().get(i);
					if (!termA.getValue().equals(termB.getValue())) {
						Pair pair = new Pair(termA, termB);
						pairsOfTerms.add(pair);
					}
				}
				i++;
			}
		}
		Out.prln("Generating pairs finished for : "+this.gateResultFileURL.getPath());
	}

	private void generateRandomlyTermsPairs() {
		for (Sentence s : this.sentences) {
			Random rdm = new Random();
			if (s.getTerms().size() > 1) {
				int rdmVariable1 = rdm.nextInt(s.getTerms().size());
				int rdmVariable2 = rdm.nextInt(s.getTerms().size());
				// il faut pas avoir deux nombres al�atoires identiques
				// ni deux termes identiques
				int limit = s.getTerms().size() * s.getTerms().size();
				int iteration = 0;
				while (rdmVariable1 == rdmVariable2
						|| s.getTerms()
								.get(rdmVariable1)
								.getValue()
								.equals(s.getTerms().get(rdmVariable2)
										.getValue()) || iteration != limit) {
					iteration++;
					rdmVariable2 = rdm.nextInt(s.getTerms().size());
				}

				if (rdmVariable1 == rdmVariable2
						|| s.getTerms()
								.get(rdmVariable1)
								.getValue()
								.equals(s.getTerms().get(rdmVariable2)
										.getValue())) {
					continue;
				} else {
					Term termA = s.getTerms().get(rdmVariable1);
					Term termB = s.getTerms().get(rdmVariable2);
					Pair pair = new Pair(termA, termB);
					pairsOfTerms.add(pair);
				}

			} else
				continue;
		}

	}

	public void printPairsOfTerms() {
		int index = 0;
		for (Pair pair : this.pairsOfTerms) {
			index++;
			System.out.println("Pair " + index + " = {"
					+ pair.getTerm1().getValue() + ","
					+ pair.getTerm2().getValue() + "}");
		}

	}

	public void savePairsInFile() throws IOException {
		// sauvegarde des resultats en XML
		File resultFolder = new File(Main.pairsOfTermsFileFolderURL);
		resultFolder.mkdir();

		File file = new File(resultFolder.getAbsolutePath()
				+ '/'
				+ FilenameUtils.getBaseName(this.getGateResultFileURL()
						.getPath()) + ".txt");
		file.createNewFile();
		Out.prln("--Creation pairs of terms document result for : "
				+ file.toURI().getPath());
		Writer out = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file), "UTF-8"));

		for (int i = 0; i < this.pairsOfTerms.size(); i++) {
			String sentence;
			String term1 = this.pairsOfTerms.get(i).getTerm1().getValue();
			String term2 = this.pairsOfTerms.get(i).getTerm2().getValue();
			if (this.pairsOfTerms.get(i).getSentence() != null)
				sentence = this.pairsOfTerms.get(i).getSentence().getValue();
			else
				sentence = "Not from the same sentece : ERROR ";
			out.append(term1 + ", " + term2 + ", " + sentence);
			if (i != this.pairsOfTerms.size() - 1)
				out.append("\n");
		}
		Out.prln("-- Pairs of terms document saved at : "
				+ file.toURI().getPath());
		this.pairsOfTermsFileURL = file.toURI().toURL();
		out.close();
	}

	public void validateWithBabelNet() throws URISyntaxException, IOException {
		int total = 0;
		File file = new File(Main.pairsOfTermsFileFolderURL
				+ '\\'
				+ FilenameUtils.getName(this.gateResultFileURL.getPath()
						.replace(".txt", "-babelnet-result.txt")));
		file.createNewFile();
		Out.prln("Validating pairs of terms with Babelnet for : "
				+ this.gateResultFileURL.toURI().getPath());
		Out.prln("Saving in : " + file.getAbsolutePath());
		Writer out = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file), "UTF-8"));

		for (Pair pair : this.pairsOfTerms) {
			total += validate(pair, out);
		}
		Out.prln(total
				+ " hypernym relations for : "
				+ this.gateResultFileURL.toURI().getPath()
						.replace(".txt", "-babelnet-result.txt"));
		out.close();

	}

	private int validate(Pair pair, Writer out) throws IOException,
			URISyntaxException {
		int rang = 3;

		boolean valid = false;
		// compteur et boolean existsInBabelnet
		Object[] parameters = { 0, false };

		// 1er cas : pair[0] = hypernyme pair[1] = hyponyme
		valid = isAnHypernymRelation(pair.getTerm1().getValue(), pair
				.getTerm2().getValue(), rang, parameters);

		if (valid) {
			pair.setType(Main.POSITIF);
			out.append(pair.getTerm2().getValue() + " IS-A "
					+ pair.getTerm1().getValue() + " => " + "+" + " ; "
					+ pair.getSentence().getValue());

			out.append("\n");

			return (int) parameters[0];
		}
		// 2eme cas : pair[0] = hyponyme pair[1] = hypernyme
		else {
			valid = isAnHypernymRelation(pair.getTerm2().getValue(), pair
					.getTerm1().getValue(), rang, parameters);
		}
		if (valid) {
			pair.setType(Main.POSITIF);
			out.append(pair.getTerm1().getValue() + " IS-A "
					+ pair.getTerm2().getValue() + " => " + "+" + " ; "
					+ pair.getSentence().getValue());
			out.append("\n");

			return (int) parameters[0];
		} else {
			// 3eme cas : si les deux existent dans BabelNet mais n'ont pas de
			// relation, c'est un exemple n�gatif

			if ((boolean) parameters[1]) {
				pair.setType(Main.NEGATIF);
				out.append(pair.getTerm1().getValue() + ", "
						+ pair.getTerm2().getValue() + " => " + "-" + " ; "
						+ pair.getSentence().getValue());
				out.append("\n");
			} else {
				pair.setType(Main.OTHER);
				out.append(pair.getTerm1().getValue() + ", "
						+ pair.getTerm2().getValue() + " => " + "?" + " ; "
						+ pair.getSentence().getValue());
				out.append("\n");
			}
		}

		return (int) parameters[0];
	}

	private boolean isAnHypernymRelation(String hypernym, String hyponyme,
			int rang, Object[] parameters) throws IOException {
		List<String> synsetH = Main.babel.getSynsets(hypernym);
		boolean valid = false;
		// Writer out = (Writer) parameters[2];
		if (synsetH.size() > 0) {
			parameters[1] = true;
			System.out.println("Validating " + hyponyme + " IS-A " + hypernym);
			// Intersecao non vide entre Synset(H) et SuperHyperonymes(h)
			List<List<String>> superhyper = Main.babel.getSuperHyperonyms(
					hyponyme, rang, synsetH);
			if (superhyper.size() > 0) {
				for (List<String> list : superhyper) {
					if (!ListUtils.intersection(list, synsetH).isEmpty()) {
						System.out.println("Intersection => "
								+ ListUtils.intersection(list, synsetH)
										.toString());
						valid = true;
					}
				}
			} else
				parameters[1] = false;
			if (valid) {
				int compteur = (int) parameters[0];
				compteur++;
				parameters[0] = compteur;
			}
		} else {
			parameters[1] = false;
			System.out.println("SYNSET is empty for " + hypernym);
		}
		// parameters[2] = out;
		return valid;
	}

	public void calculatePairFrequency() throws IOException {
		ArrayList<Pair> temp = new ArrayList<Pair>();
		for (Pair p : this.pairsOfTerms) {
			int freq = Collections.frequency(this.pairsOfTerms, p);
			p.setFreq(freq);
			if (!isInList(temp, p))
				temp.add(p);

		}

		File file = new File(Main.pairsOfTermsFileFolderURL
				+ "/"
				+ FilenameUtils.getBaseName(this.getGateResultFileURL()
						.getPath()) + "-frequency.txt");
		// + FilenameUtils.getName(this.gateResultFileURL.getPath()
		// .replace(".xml", "-frequency.txt")));
		file.createNewFile();
		Writer out = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file), "UTF-8"));
		for (Pair p : temp) {
			out.append("{" + p.getTerm1().getValue() + ", "
					+ p.getTerm2().getValue() + "} => " + p.getFreq());
			out.append("\n");
		}
		out.close();
	}

	public boolean isInList(ArrayList<Pair> list, Pair p) {
		for (Pair pair : list) {
			if (p.equals(pair))
				return true;
		}
		return false;
	}

	public void constructFinalNegatifExamples(int threshold) {
		int nmbExamples = this.finalPositifExamples.size();
		int counter = 0;
		for (Pair negatifPair : this.negatifExamples) {
			if (counter != nmbExamples) {
				if (negatifPair.getFreq() >= threshold) {
					this.finalNegatifExamples.add(negatifPair);
				}
				counter++;
			}
		}
	}

	public void treeTagg(boolean ttgAllSentences) throws IOException,
			TreeTaggerException {
		System.out.println("tree-tagging file");
		if (ttgAllSentences) {
			this.ttgAllSenteces();
			System.out.println("boolean");
		} else
			this.ttgPositifAndNegatifExamples();

	}

	private void ttgAllSenteces() throws IOException, TreeTaggerException {
		System.setProperty("treetagger.home", "TreeTagger2");
		System.out.println("tree-tagging begins");
		for (Pair p : this.getPairsOfTerms()) {
			TreeTaggerWrapper<String> tt = new TreeTaggerWrapper<String>();
			tt.setModel("TreeTagger2/model/french.par");
			tt.setHandler(new MyTokenHandler());
			List<String> tokens = tokenize(p.getSentence().getValue());
			tt.process(tokens);
			p.getSentence().setTtgValue(
					((MyTokenHandler) tt.getHandler()).getTTGString());
			tt.destroy();
		}
		System.out.println("tree-tagging finished");

		File file = new File(Main.pairsOfTermsFileFolderURL
				+ "/ttg-all-result.txt");
		file.createNewFile();
		File filePair = new File(Main.pairsOfTermsFileFolderURL
				+ "/ttg-all-pairs-result.txt");
		filePair.createNewFile();
		Out.prln("Tree-Tagger Result in : " + file.toURI().getPath());

		Writer out = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file), "UTF-8"));
		Writer outPair = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(filePair), "UTF-8"));

		for (Pair pair : this.getPairsOfTerms()) {
			String term1 = pair.getTerm1().getValue();
			String term2 = pair.getTerm2().getValue();
			String sentenceTTG = pair.getSentence().getTtgValue();
			out.append(sentenceTTG + "\t" + "?");
			out.append("\n");
			outPair.append(term1 + ", " + term2 + "; "
					+ pair.getSentence().getValue() + "\t" + "?");
			outPair.append("\n");
		}
		out.close();
		outPair.close();
	}

	private void ttgPositifAndNegatifExamples() throws IOException,
			TreeTaggerException {
		System.setProperty("treetagger.home", "TreeTagger2");
		for (Pair positifPair : this.finalPositifExamples) {
			TreeTaggerWrapper<String> tt = new TreeTaggerWrapper<String>();
			tt.setModel("TreeTagger2/model/french.par");
			tt.setHandler(new MyTokenHandler());
			List<String> tokens = tokenize(positifPair.getSentence().getValue());
			tt.process(tokens);
			positifPair.getSentence().setTtgValue(
					((MyTokenHandler) tt.getHandler()).getTTGString());
			tt.destroy();
		}

		for (Pair negatifPair : this.finalNegatifExamples) {
			TreeTaggerWrapper<String> tt = new TreeTaggerWrapper<String>();
			tt.setModel("TreeTagger2/model/french.par");
			tt.setHandler(new MyTokenHandler());
			List<String> tokens = tokenize(negatifPair.getSentence().getValue());
			tt.process(tokens);
			negatifPair.getSentence().setTtgValue(
					((MyTokenHandler) tt.getHandler()).getTTGString());
			tt.destroy();
		}
	}

	public List<String> tokenize(final String aString) {
		List<String> tokens = new ArrayList<String>();
		BreakIterator bi = BreakIterator.getWordInstance();
		bi.setText(aString);
		int begin = bi.first();
		int end;
		for (end = bi.next(); end != BreakIterator.DONE; end = bi.next()) {
			String t = aString.substring(begin, end);
			if (t.trim().length() > 0) {
				tokens.add(aString.substring(begin, end));
			}
			begin = end;
		}
		if (end != -1) {
			tokens.add(aString.substring(end));
		}
		return tokens;
	}

	public void saveTTGResult() throws IOException {

		File file = new File(Main.pairsOfTermsFileFolderURL
				+ "\\ttg-result.txt");
		file.createNewFile();
		File filePair = new File(Main.pairsOfTermsFileFolderURL
				+ "\\ttg-pairs-result.txt");
		filePair.createNewFile();
		Out.prln("Tree-Tagger Result in : " + file.toURI().getPath());

		Writer out = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(file), "UTF-8"));
		Writer outPair = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(filePair), "UTF-8"));

		for (Pair positifPair : this.finalPositifExamples) {
			String term1 = positifPair.getTerm1().getValue();
			String term2 = positifPair.getTerm2().getValue();
			String sentenceTTG = positifPair.getSentence().getTtgValue();
			out.append(sentenceTTG + "\t" + "1");
			out.append("\n");
			outPair.append(term1 + ", " + term2 + "; "
					+ positifPair.getSentence().getValue() + "\t" + "1");
			outPair.append("\n");
		}
		for (Pair negativPair : this.finalNegatifExamples) {
			String term1 = negativPair.getTerm1().getValue();
			String term2 = negativPair.getTerm2().getValue();
			String sentenceTTG = negativPair.getSentence().getTtgValue();
			out.append(sentenceTTG + "\t" + "0");
			out.append("\n");
			outPair.append(term1 + ", " + term2 + "; "
					+ negativPair.getSentence().getValue() + "\t" + "0");
			outPair.append("\n");
		}
		out.close();
		outPair.close();

	}

}
