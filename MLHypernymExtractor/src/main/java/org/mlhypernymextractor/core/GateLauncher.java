package org.mlhypernymextractor.core;
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
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

public class GateLauncher {

	/***************************************************************************************/
	/***************************************************************************************/

	public static void myGateApp(ArrayList<URL> corpusDocumentsURLs)
			throws GateException, IOException {
		// initialise the GATE library

		Out.prln("Initialising GATE...");
		Gate.init();

		// cr�ation de ANNIE
		Gate.getCreoleRegister().registerDirectories(
				new File(Main.ANNIE_HOME).toURI().toURL());
		SerialAnalyserController annieController = (SerialAnalyserController) Factory
				.createResource("gate.creole.SerialAnalyserController",
						Factory.newFeatureMap(), Factory.newFeatureMap(),
						"ANNIE_" + Gate.genSym());

		Corpus corpus = (Corpus) Factory
				.createResource("gate.corpora.CorpusImpl");
		// cr�ation du corpus
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

		// cr�ation des Gazetteers

		for (File f : new File(Main.termsListDefFolderURL).listFiles()) {
			if (f.getName().contains(".def")) {
				FeatureMap gazetterParams = Factory.newFeatureMap();
				URL url = f.toURI().toURL();
				gazetterParams.put("listsURL", url);
				gazetterParams.put("encoding", "UTF-8");
				gazetterParams.put("caseSensitive", false);
				DefaultGazetteer gazetteer = (DefaultGazetteer) Factory
						.createResource(
								"gate.creole.gazetteer.DefaultGazetteer",
								gazetterParams);
				annieController.add(gazetteer);
			}
		}
		Out.prln("Gazetter created ");
		Out.prln("Annie created ");
		Out.prln("Proccessing Annie...");
		// lancement de ANNIE
		annieController.execute();
		// sauvegarde des resultats en XML
		File resultFolder = new File(Main.corpusFolderURL + "/results");
		resultFolder.mkdir();

		for (int i = 0; i < corpus.size(); i++) {
			Document doc = corpus.get(i);
			File file = new File(resultFolder.getAbsolutePath() + '/'
					+ doc.getName().replace(".txt", "") + ".xml");
			file.createNewFile();
			Out.prln("Creation GATE XML result for : "
					+ doc.getName().replace(".txt", ""));
			
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
