package org.mlhypernymextractor.core;

import gate.util.GateException;
import gate.util.Out;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.annolab.tt4j.TreeTaggerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Main {

	public static String corpusFolderURL = "C:\\Users\\ghamnia\\dump 2016\\homonymie\\corpus\\demo";
	public static ArrayList<URL> corpusDocumentsURLs = new ArrayList<>();
	public static String ANNIE_HOME = "C:\\Users\\ghamnia\\gate-8.1-build5169-BIN\\plugins\\ANNIE";
	public static String termsListDefFolderURL = "C:\\Users\\ghamnia\\dump 2016\\homonymie\\corpus\\demo\\lists";
	// public static final File termsListDefFolder = new File(
	// termsListDefFolderURL);
	public static String xmlResultsFolderURL = corpusFolderURL + "\\results";
	public static String pairsOfTermsFileFolderURL = corpusFolderURL
			+ "\\pairs of terms";
	public static ArrayList<GateResultFile> gateResultFiles = new ArrayList<>();
	// public static BabelNetAPI babel = new BabelNetAPI();
	public static BabelNetAPI babel;
	public static boolean multipleTermsCombination = false;
	// final static Logger logger = Logger.getLogger(Main.class);
	public static boolean ttgAllSentences = false;
	// les constantes
	public static final int POSITIF = 0;
	public static final int NEGATIF = 1;
	public static final int OTHER = 2;

	/**
	 * @param args
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws GateException
	 */
	public static void main(String[] args) throws IOException,
			URISyntaxException, GateException {
		// -Dgate.plugins.home="C:\Users\ghamnia\gate-8.1-build5169-BIN\plugins"
		// -Dgate.home="C:\Users\ghamnia\gate-8.1-build5169-BIN"
		// -Xmx1024m

		if (args.length == 4) {
			System.out.println("entred");
			if (args[0].equals("-generate-multiple-term-combination")) {
				multipleTermsCombination = true;
				File corpusFolder = new File((String) args[1]);
				corpusFolderURL = corpusFolder.toPath().toString();
				System.out.println(corpusFolderURL);
				File ANNIE_HOME_FOLDER = new File((String) args[2]);
				ANNIE_HOME = ANNIE_HOME_FOLDER.toPath().toString();
				File termsListDefFolder = new File((String) args[3]);
				termsListDefFolderURL = termsListDefFolder.toPath().toString();
				System.out.println(termsListDefFolderURL);
				xmlResultsFolderURL = corpusFolderURL + "/results";
				pairsOfTermsFileFolderURL = corpusFolderURL + "/pairs_of_terms";
			}
		} else if (args.length == 3) {
			if (((String) args[0]).equals("-ttg-examples")) {
				if (((String) args[1]).equals("--ttg-all-sentences")) {
					Main.ttgAllSentences = true;
				}
				File file = new File((String) args[2]);
				Main.pairsOfTermsFileFolderURL = file.getParent();
				GateResultFile gateResultFile = createPairsFromFile(file);
				gateResultFile.calculatePairFrequency();
				Out.prln("TreeTagging all sentences");
				try {
					gateResultFile.treeTagg(Main.ttgAllSentences);
				} catch (TreeTaggerException e) {
					e.printStackTrace();
				}
				return;
			} else {
				File corpusFolder = new File((String) args[0]);
				corpusFolderURL = corpusFolder.toPath().toString();
				File ANNIE_HOME_FOLDER = new File((String) args[1]);
				ANNIE_HOME = ANNIE_HOME_FOLDER.toPath().toString();
				File termsListDefFolder = new File((String) args[2]);
				termsListDefFolderURL = termsListDefFolder.toPath().toString();
				xmlResultsFolderURL = corpusFolderURL + "/results";
				pairsOfTermsFileFolderURL = corpusFolderURL + "/pairs_of_terms";
			}
		} else if (args.length == 2) {
			for (int i = 0; i < args.length; i++) {
				if (((String) args[i]).equals("-validation")) {
					File pairsFile = new File((String) args[i + 1]);
					Main.pairsOfTermsFileFolderURL = pairsFile.getParent();
					GateResultFile gateResultFile = createPairsFromFile(pairsFile);
					gateResultFile.validateWithBabelNet();
					return;
				} else if (((String) args[i]).equals("-ttg-examples")) {
					File file = new File((String) args[i + 1]);
					Main.pairsOfTermsFileFolderURL = file.getParent();
					GateResultFile gateResultFile = createExamplesFromBebelNetResultFile(file);
					gateResultFile.calculatePairFrequency();
					Out.pr("Constructing final examples");
					gateResultFile.constructFinalNegatifExamples(1);
					try {
						gateResultFile.treeTagg(Main.ttgAllSentences);
					} catch (TreeTaggerException e) {
						e.printStackTrace();
					}
					gateResultFile.saveTTGResult();
					return;
				} else if (((String) args[i]).equals("-parse")) {
					File file = new File((String) args[i + 1]);
					Main.corpusFolderURL = file.getCanonicalPath();
					Main.pairsOfTermsFileFolderURL = corpusFolderURL
							+ "/pairs_of_terms";
					List<Thread> threads = new ArrayList<Thread>();
					int threadNum = 0;
					for (File f : file.listFiles()) {
						if (f.getName().contains(".xml")) {
							Runnable parser = new RunnableXMLGateFileParser(f);
							Thread myThread = new Thread(parser);
							myThread.setName("Thread n°" + threadNum++);
							myThread.start();
							Out.prln(myThread.getName() + " is running...");
							threads.add(myThread);
						}
					}

					
					boolean allThreadsAreTerminated = false;

					long startTime = System.currentTimeMillis();
					long waitTime = startTime+(long)600000;
					Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(startTime);
					
					Out.prln("All threads are launched at : "
							+ calendar.get(Calendar.DAY_OF_MONTH) + " "
							+ calendar.get(Calendar.HOUR_OF_DAY) + ":"
							+ calendar.get(Calendar.MINUTE));
					while (!allThreadsAreTerminated) {
						long iTime = System.currentTimeMillis();
						int isItTimeToCheckThreads = new Long(waitTime).compareTo(new Long(iTime));
						if (isItTimeToCheckThreads<0) {
							int running = 0;
							for (Thread thread : threads) {
								if (thread.isAlive()) {
									running++;
									Out.prln(thread.getName() + " is running");
								}
							}
							if(running == 0){
								allThreadsAreTerminated = true;
							}else{
								startTime = System.currentTimeMillis();
								calendar.setTimeInMillis(startTime);
								Out.prln(running+" threads are checked at : "
										+ calendar.get(Calendar.DAY_OF_MONTH) + " "
										+ calendar.get(Calendar.HOUR_OF_DAY) + ":"
										+ calendar.get(Calendar.MINUTE));
								waitTime = startTime+(long)600000;
								running = 0;
							}
						}
					}
					Out.prln("All threads are terminated");
					return;
				}
			}

		} else {
			Out.pr("run : java -jar arg1:CorpusFolder arg2:ANNI_HOME arg3:TermsListDefFolder");
			return;
		}

		// r�cuperer tous les documents '.txt' du dossier du corpus
		for (File f : new File(corpusFolderURL).listFiles()) {
			if (f.getName().contains(".txt")) {
				// corpusDocumentsURLs.add(f.toURI().toURL());
				ArrayList<URL> l = new ArrayList<>();
				l.add(f.toURI().toURL());
				GateLauncher.myGateApp(l);
			}
		}
		// lancer le traitement Gate
		// GateLauncher.myGateApp(corpusDocumentsURLs);

		// Parser les fichiers de r�sultats xml Gate
		for (File out : new File(xmlResultsFolderURL).listFiles()) {
			if (out.getName().contains(".xml")) {
				// parser le fichier xml
				GateResultFile f = new GateResultFile(out.toURI().toURL());
				Out.prln("parsing Gate XML file : "
						+ f.getGateResultFileURL().toURI().getPath());
				parseGateXml(out, f.getSentences());
				Out.prln("Gate XML file parsed"
						+ f.getGateResultFileURL().toURI().getPath());
				int nbTerm = 0;
				Out.prln("extracting pairs of terms");
				for (Sentence s : f.getSentences()) {
					nbTerm += s.getTerms().size();
				}
				Out.prln(nbTerm + " terms extracted from "
						+ f.getGateResultFileURL().toURI().getPath());
				gateResultFiles.add(f);
			}
		}

		// Pour chaque fichier de r�sultat
		// 1 -extraire des paires de termes � partir d'une m�me phrase
		// 2 -valider avec babelnet
		for (GateResultFile gateResultFile : gateResultFiles) {
			gateResultFile.extractPairs(multipleTermsCombination);
			Out.prln("Pairs of terms generated from "
					+ gateResultFile.getGateResultFileURL().toURI().getPath());
			gateResultFile.printPairsOfTerms();
			Out.prln("Saving pairs of terms");
			gateResultFile.savePairsInFile();
			// calculate frequency for each pair
			gateResultFile.calculatePairFrequency();

			// Out.prln("Validating with Babelnet");
			// gateResultFile.validateWithBabelNet();
		}

	}

	private static GateResultFile createExamplesFromBebelNetResultFile(File file)
			throws IOException {
		GateResultFile res = new GateResultFile(file.toURI().toURL());
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(file), "UTF8"));
		String str;
		while ((str = in.readLine()) != null) {
			String[] line = str.split(" => ");
			String termsString = line[0];
			String[] terms;

			if (termsString.contains("IS-A")) {
				terms = termsString.split(" IS-A ");
			} else
				terms = termsString.split(", ");

			String[] st = line[1].split(" ; ");
			String typeChar = st[0];
			String sentenceString = st[1];
			Term term1, term2;
			Sentence sentence;
			if (terms[0] != null && terms[1] != null && typeChar != null
					&& sentenceString != null) {
				term1 = new Term(-1, -1, terms[0], false);
				term2 = new Term(-1, -1, terms[1], false);
				sentence = new Sentence(-1, -1, -1, sentenceString);
				sentence.addTerm(term1);
				sentence.addTerm(term2);
				Pair p = new Pair(term1, term2);
				if (typeChar.equals("+"))
					p.setType(POSITIF);
				else if (typeChar.equals("-"))
					p.setType(NEGATIF);
				else
					p.setType(OTHER);
				res.addSentence(sentence);
				res.addPair(p);
				switch (p.getType()) {
				case POSITIF:
					res.addFinalPositifExamples(p);
					break;
				case NEGATIF:
					res.addNegatifExamples(p);
					break;
				default:
					break;
				}
			}
		}
		in.close();
		return res;
	}

	private static GateResultFile createPairsFromFile(File pairsFile)
			throws IOException {
		GateResultFile res = new GateResultFile(pairsFile.toURI().toURL());
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(pairsFile), "UTF8"));
		String str;
		while ((str = in.readLine()) != null) {
			String[] line = str.split(", ", 3);
			Term term1, term2;
			Sentence sentence;
			System.out.println(line[0] + ", " + line[1] + ", " + line[2]);
			if (line[0] != null && line[1] != null && line[2] != null) {
				term1 = new Term(-1, -1, line[0], false);
				term2 = new Term(-1, -1, line[1], false);
				sentence = new Sentence(-1, -1, -1, line[2]);
				sentence.addTerm(term1);
				sentence.addTerm(term2);
				Pair p = new Pair(term1, term2);
				res.addSentence(sentence);
				res.addPair(p);
			}
		}
		for (Pair p : res.getPairsOfTerms()) {
			System.out.println(p.getTerm1().getValue() + ", "
					+ p.getTerm2().getValue() + ", "
					+ p.getSentence().getValue());

		}
		in.close();
		return res;
	}

	public static void parseGateXml(File xmlGate, ArrayList<Sentence> sentences) {
		try {
			Out.prln("Starting parsing for "+xmlGate.getCanonicalPath());
			InputStream inputStream = new FileInputStream(xmlGate);
			Reader reader = new InputStreamReader(inputStream, "UTF-8");
			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(is);

			doc.getDocumentElement().normalize();

			// get all annotations
			NodeList domAnnotations = doc.getElementsByTagName("Annotation");

			ArrayList<Annotation> annotations = new ArrayList<>();
			// r�cup�rer le text
			NodeList textWithNodes = doc.getElementsByTagName("TextWithNodes");
			String text = textWithNodes.item(0).getTextContent();

			for (int temp = 0; temp < domAnnotations.getLength(); temp++) {
				Node nNode = domAnnotations.item(temp);
				if (((Element) nNode).getAttribute("Type").equals("Sentence")) {
					// parcourir les sentences
					int startOffset = Integer.valueOf(((Element) nNode)
							.getAttribute("StartNode"));
					int endOffset = Integer.valueOf(((Element) nNode)
							.getAttribute("EndNode"));
					int id = Integer.valueOf(((Element) nNode)
							.getAttribute("Id"));
					String value = text.substring(startOffset, endOffset);
					value = value.replaceAll("\n", "").replaceAll("\r", "");
					Sentence s = new Sentence(startOffset, endOffset, id, value);
					annotations.add(s);

				} else if (((Element) nNode).getAttribute("Type").equals(
						"Lookup")) {
					// parcourir les termes
					NodeList values = ((Element) nNode)
							.getElementsByTagName("Value");
					if (values.getLength() == 1) {
						Node valueNode = values.item(0);
						if (valueNode.getTextContent().equals("terme")) {
							int startOffset = Integer.valueOf(((Element) nNode)
									.getAttribute("StartNode"));
							int endOffset = Integer.valueOf(((Element) nNode)
									.getAttribute("EndNode"));
							String value = text.substring(startOffset,
									endOffset);
							value = value.replaceAll("\n", "").replaceAll("\r",
									"");
							if (!value.matches("\\d+")) {
								Term t = new Term(startOffset, endOffset,
										value, false);
								annotations.add(t);
							}
						} else if (valueNode.getTextContent()
								.equals("mot_vide")) {
							int startOffset = Integer.valueOf(((Element) nNode)
									.getAttribute("StartNode"));
							int endOffset = Integer.valueOf(((Element) nNode)
									.getAttribute("EndNode"));
							String value = text.substring(startOffset,
									endOffset);
							value = value.replaceAll("\n", "").replaceAll("\r",
									"");

							if (!value.matches("\\d+")) {
								Term t = new Term(startOffset, endOffset,
										value, true);
								annotations.add(t);
							}
						}
					}

				} else {
					continue;
				}
			}

			Out.prln("--Linking terms to their sentences for : "+xmlGate.getCanonicalPath());
			// relier les termes aux sentences correspondantes
			for (Annotation annotation : annotations) {
				if (annotation.isSentence()) {
					int sentenceStartOffset = annotation.getStartOffset();
					int sentenceEndOffset = annotation.getEndOffset();
					for (Annotation term : annotations) {
						if (!term.isSentence()) {
							if (term.getStartOffset() >= sentenceStartOffset
									&& term.getEndOffset() <= sentenceEndOffset) {
								((Sentence) annotation).addTerm((Term) term);
							}
						}
					}
					sentences.add((Sentence) annotation);
				}
			}
			// supprimer les mots vides
			Out.prln("--Deleting empty tokens for : "+xmlGate.getCanonicalPath());
			for (Sentence sentence : sentences) {
				ArrayList<Term> temp = new ArrayList<>(sentence.getTerms());
				for (Term motVide : temp) {
					if (motVide.isMotVide()) {
						for (Term term : temp) {
							if (motVide.equals(term)) {
								// supprimer les deux termes de la phrase
								sentence.removeTerm(motVide);
								sentence.removeTerm(term);
							}
						}
					}

				}

			}

			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
