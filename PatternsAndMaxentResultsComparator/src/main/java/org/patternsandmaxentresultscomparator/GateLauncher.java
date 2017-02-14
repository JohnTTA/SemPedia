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

	public static void myGateApp()
			throws GateException, IOException {
		
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

		// creation de treetagger
		FeatureMap taggerParameters = Factory.newFeatureMap();
		taggerParameters.put("encoding", "UTF-8");
		taggerParameters.put("taggerBinary", new File(Main.TREE_TAGGER_BIN).toURI().toURL());

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
		//
		// sauvegarde des resultats en XML
//		for (int i = 0; i < corpus.size(); i++) {
//			Document doc = corpus.get(i);
//			File file = new File(Main.CORPUS_FOLDER + '/' + doc.getName()+"_patterns_results" + ".xml");
//			file.createNewFile();
//			Out.prln("Creation GATE XML result for : " + doc.getName());
//
//			Writer out = new BufferedWriter(new OutputStreamWriter(
//					new FileOutputStream(file), "UTF-8"));
//			out.write(doc.toXml());
//			out.close();
//			Out.prln("GATE XML result file created");
//		}
		
		Document doc = corpus.get(0);
		String text = doc.getContent().toString();
		
		AnnotationSet sentences = doc.getAnnotations().get("Sentence");
		AnnotationSet annotations = doc.getAnnotations().get("hyperonymie");
		
		File fileOutText = new File(Main.CORPUS_FOLDER + '/' + doc.getName()+"_patterns_results" + ".txt");
		fileOutText.createNewFile();

		Writer out = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(fileOutText), "UTF-8"));
		Out.prln("GATE XML result file created");
		for (gate.Annotation annotation : annotations) {
			if(annotation.getType().equals("hyperonymie")){
				long startOffset = annotation.getStartNode().getOffset();
				long endOffset = annotation.getEndNode().getOffset();
				
				String rule = (String) annotation.getFeatures().get("rule");
				String value = text.substring(new Long(startOffset).intValue(), new Long(endOffset).intValue());
				String sentenceValue = "";
				for (gate.Annotation sentence : sentences) {
					if(sentence.getStartNode().getOffset()<=annotation.getStartNode().getOffset()
							&& sentence.getEndNode().getOffset()>=annotation.getEndNode().getOffset()){
						sentenceValue = text.substring(new Long(sentence.getStartNode().getOffset()).intValue(), 
								new Long(sentence.getEndNode().getOffset()).intValue());
					}
				}
				out.append("<hyperonymie>\n");
				out.append("\t<rule>"+rule+"</rule>\n");
				out.append("\t\t<relation>"+value+"</relation>\n");
				out.append("\t\t<phrase>"+sentenceValue+"</phrase>\n");
				out.append("</hyperonymie>\n");
			}
		}
		Out.prln("Results saved in : "+fileOutText.getCanonicalPath());
		out.close();
	}

}

/***************************************************************************************/
/***************************************************************************************/
