package org.patternsandmaxentresultscomparator;

import gate.AnnotationSet;
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.LanguageAnalyser;
import gate.ProcessingResource;
import gate.creole.SerialAnalyserController;
import gate.creole.gazetteer.DefaultGazetteer;
import gate.creole.splitter.RegexSentenceSplitter;
import gate.util.GateException;
import gate.util.OffsetComparator;
import gate.util.Out;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

public class GateLauncher {

	public static void myGateApp() throws GateException, IOException {

		Out.prln("Initialising GATE...");
		Gate.init();

		// cr�ation de ANNIE
		Gate.getCreoleRegister().registerDirectories(
				new File(Main.ANNIE_HOME).toURI().toURL());
		Gate.getCreoleRegister().registerDirectories(
				new File(Main.TAGGER_FRAMEWORK_FOLDER).toURI().toURL());

		SerialAnalyserController annieController = (SerialAnalyserController) Factory
				.createResource("gate.creole.SerialAnalyserController",
						Factory.newFeatureMap(), Factory.newFeatureMap(),
						"ANNIE_" + Gate.genSym());

		Corpus corpus = (Corpus) Factory
				.createResource("gate.corpora.CorpusImpl");
		// cr�ation du corpus
		File corpusFolder = new File(Main.CORPUS_FOLDER);
		ArrayList<URL> corpusDocumentsURLs = new ArrayList<>();

		for (File f : corpusFolder.listFiles()) {
			if (f.getName().endsWith(".txt"))
				corpusDocumentsURLs.add(f.toURI().toURL());
		}

		for (URL documentURL : corpusDocumentsURLs) {
			FeatureMap params = Factory.newFeatureMap();
			params.put("sourceUrl", documentURL);
			params.put("preserveOriginalContent", true);
			params.put("collectRepositioningInfo", true);
			params.put("encoding", "UTF-8");
			Out.prln("Creating doc for " + documentURL);
			Document doc = (Document) Factory.createResource(
					"gate.corpora.DocumentImpl", params);

			corpus.add(doc);
		}
		annieController.setCorpus(corpus);
		Out.prln("Corpus created ");

		// cr�ation du SenteceSplitter
		FeatureMap sentenceSplitterParams = Factory.newFeatureMap();
		sentenceSplitterParams.put("encoding", "UTF-8");
		RegexSentenceSplitter senteceSplitter = (RegexSentenceSplitter) Factory
				.createResource("gate.creole.splitter.RegexSentenceSplitter",
						sentenceSplitterParams);
		annieController.add(senteceSplitter);
		// creation du tokenizer
		FeatureMap tokenizerParameters = Factory.newFeatureMap();
		tokenizerParameters.put("encoding", "UTF-8");
		ProcessingResource tokenizer = (ProcessingResource) Factory
				.createResource("gate.creole.tokeniser.DefaultTokeniser",
						tokenizerParameters);
		annieController.add(tokenizer);

		// cr�ation du Gazetteer
		File list = new File(Main.GAZZETER_LIST_DEF);
		FeatureMap gazetterParams = Factory.newFeatureMap();
		URL url = list.toURI().toURL();
		gazetterParams.put("listsURL", url);
		gazetterParams.put("encoding", "UTF-8");
		gazetterParams.put("caseSensitive", false);
		DefaultGazetteer gazetteer = (DefaultGazetteer) Factory.createResource(
				"gate.creole.gazetteer.DefaultGazetteer", gazetterParams);
		annieController.add(gazetteer);
		Out.prln("Gazetter created...");
		
		
		// creation de treetagger
		FeatureMap taggerParameters = Factory.newFeatureMap();
		taggerParameters.put("encoding", "UTF-8");
		taggerParameters.put("taggerBinary", new File(Main.TREE_TAGGER_BIN)
				.toURI().toURL());

		ProcessingResource tagger = (ProcessingResource) Factory
				.createResource("gate.taggerframework.GenericTagger",
						taggerParameters);
		annieController.add(tagger);
		Out.prln("Tagger created...");
		// cr�ation des jape rules
		for (File f : new File(Main.JAPE_RULES_FOLDER).listFiles()) {
			if (f.getName().contains(".jape")) {

				LanguageAnalyser jape = (LanguageAnalyser) Factory
						.createResource("gate.creole.Transducer", gate.Utils
								.featureMap("grammarURL", f.toURI().toURL(),
										"encoding", "UTF-8")); // ensure this
																// matches the
																// file
				annieController.add(jape);
				Out.prln("Jape rules created ");
			}
		}

		Out.prln("Annie created ");
		Out.prln("Proccessing Annie...");
		// lancement de ANNIE
		annieController.execute();
		Out.prln("Annie executed...");
		// sauvegarde des résultats
		saveXMLGate(corpus);
		savePatternResults(corpus);
	}

	private static void savePatternResults(Corpus corpus) {
		Document doc = corpus.get(0);
		String text = doc.getContent().toString();

		AnnotationSet sentenceGateAnnotations = doc.getAnnotations().get(
				"Sentence");
		AnnotationSet hyperonymieGateAnnotations = doc.getAnnotations().get(
				"hyperonymie");

		List<gate.Annotation> sentenceAnnotations = new ArrayList<>(
				sentenceGateAnnotations);
		Collections.sort(sentenceAnnotations, new OffsetComparator());

		List<gate.Annotation> hyperonymieAnnotations = new ArrayList<>(
				hyperonymieGateAnnotations);
		Collections.sort(hyperonymieAnnotations, new OffsetComparator());
		try {
			XMLOutputFactory xof = XMLOutputFactory.newInstance();
			OutputStream os = new FileOutputStream(new File(Main.CORPUS_FOLDER
					+ '/' + doc.getName() + "_patterns_relations_results"
					+ ".xml"));
			XMLEventWriter xmlWriter = xof.createXMLEventWriter(os, "UTF-8");
			XMLEventFactory event = XMLEventFactory.newInstance();
			xmlWriter.add(event.createStartDocument("UTF-8"));
			xmlWriter.add(event.createStartElement("", "", "hyperonymies"));
			File sentenceTextFile = new File(Main.CORPUS_FOLDER + '/'
					+ "sentences.txt");
			sentenceTextFile.createNewFile();
			Writer out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(sentenceTextFile), "UTF-8"));

			for (gate.Annotation annotation : hyperonymieAnnotations) {
				if (annotation.getType().equals("hyperonymie")) {
					long startOffset = annotation.getStartNode().getOffset();
					long endOffset = annotation.getEndNode().getOffset();
					String rule = (String) annotation.getFeatures().get("rule");
					String value = text.substring(new Long(startOffset)
							.intValue(), new Long(endOffset).intValue());
					String sentenceValue = "";
					for (gate.Annotation sentence : sentenceAnnotations) {
						if (sentence.getStartNode().getOffset() <= annotation
								.getStartNode().getOffset()
								&& sentence.getEndNode().getOffset() >= annotation
										.getEndNode().getOffset()) {
							sentenceValue = text.substring(new Long(sentence
									.getStartNode().getOffset()).intValue(),
									new Long(sentence.getEndNode().getOffset())
											.intValue());
						}
					}
					// out.append(value + "\n");
					try {
						xmlWriter.add(event.createStartElement("", "",
								"hyperonymie"));
						xmlWriter.add(event.createStartElement("", "", "rule"));
						xmlWriter.add(event.createCharacters(rule));
						xmlWriter.add(event.createEndElement("", "", "rule"));
						xmlWriter.add(event.createStartElement("", "",
								"relation"));
						xmlWriter.add(event.createCharacters(value));
						xmlWriter.add(event
								.createEndElement("", "", "relation"));
						xmlWriter.add(event
								.createStartElement("", "", "phrase"));
						xmlWriter.add(event.createCharacters(sentenceValue));
						xmlWriter.add(event.createEndElement("", "", "phrase"));
						xmlWriter.add(event.createEndElement("", "",
								"hyperonymie"));
					} catch (XMLStreamException e) {
						e.printStackTrace();
					}

				}

			}

			xmlWriter.add(event.createEndElement("", "", "hyperonymies"));
			Out.prln("Results saved in : "
					+ sentenceTextFile.getCanonicalPath());

			for (gate.Annotation annotation : sentenceAnnotations) {
				int start = annotation.getStartNode().getOffset().intValue();
				int end = annotation.getEndNode().getOffset().intValue();
				String sentence = text.substring(start, end);
				out.append(sentence);
				out.append("\n");
			}

			out.flush();
			out.close();
			xmlWriter.add(event.createEndDocument());
			xmlWriter.flush();
			xmlWriter.close();

		} catch (XMLStreamException | IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveXMLGate(gate.Corpus corpus) throws IOException {
		// sauvegarde des resultats en XML
		for (int i = 0; i < corpus.size(); i++) {
			Document doc = corpus.get(i);
			File file = new File(Main.CORPUS_FOLDER + '/' + doc.getName()
					+ "_gate" + ".xml");
			file.createNewFile();
			Out.prln("Saving in GATE XML File for : " + doc.getName());

			Writer out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), "UTF-8"));
			out.write(doc.toXml());
			out.close();
			Out.prln("GATE XML result file created");
		}
	}

}

/***************************************************************************************/
/***************************************************************************************/
