package org.mlhypernymextractor.utils;
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
import gate.util.Out;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;

public class ProcessJapeRules {

	public static void main(String[] args) throws GateException, IOException {
		Out.prln("Initialising GATE...");
		Gate.init();

		String ANNIE_HOME = args[0];
		String CORPUS_FOLDER = args[1];
		String JAPE_RULES_FOLDER = args[2];
		String TAGGER_FRAMEWORK_FOLDER = args[3];
		// cr�ation de ANNIE
		Gate.getCreoleRegister().registerDirectories(
				new File(ANNIE_HOME).toURI().toURL());
		Gate.getCreoleRegister().registerDirectories(
				new File(TAGGER_FRAMEWORK_FOLDER).toURI().toURL());

		SerialAnalyserController annieController = (SerialAnalyserController) Factory
				.createResource("gate.creole.SerialAnalyserController",
						Factory.newFeatureMap(), Factory.newFeatureMap(),
						"ANNIE_" + Gate.genSym());

		Corpus corpus = (Corpus) Factory
				.createResource("gate.corpora.CorpusImpl");
		// cr�ation du corpus
		File corpusFolder = new File(CORPUS_FOLDER);
		ArrayList<URL> corpusDocumentsURLs = new ArrayList<>();

		for (File f : corpusFolder.listFiles()) {
			if (f.getName().contains(".txt"))
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
		File list = new File(args[5]);
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
		taggerParameters.put("taggerBinary", new File(args[4]).toURI().toURL());

		ProcessingResource tagger = (ProcessingResource) Factory
				.createResource("gate.taggerframework.GenericTagger",
						taggerParameters);
		annieController.add(tagger);
		Out.prln("Tagger created...");
		// cr�ation des jape rules
		for (File f : new File(JAPE_RULES_FOLDER).listFiles()) {
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
		// sauvegarde des resultats en XML
		for (int i = 0; i < corpus.size(); i++) {
			Document doc = corpus.get(i);
			File file = new File(CORPUS_FOLDER + '/' + doc.getName() + ".xml");
			file.createNewFile();
			Out.prln("Creation GATE XML result for : " + doc.getName());
			Writer out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), "UTF-8"));
			out.write(doc.toXml());
			out.close();
			Out.prln("GATE XML result file created");
		}
	}
}
