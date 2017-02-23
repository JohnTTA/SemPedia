package org.mlhypernymextractor.core;

import gate.util.GateException;
import gate.util.Out;
import it.uniroma1.lcl.jlt.util.Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.mlhypernymextractor.utils.UsefulMethods;

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
	 public static BabelNetAPI babel = new BabelNetAPI();
//	public static BabelNetAPI babel;
	public static boolean multipleTermsCombination = false;
	// final static Logger logger = Logger.getLogger(Main.class);
	public static boolean ttgAllSentences = false;
	public static boolean withFeatures = false;
	public static final int windowLength = 3;
	// les constantes
	public static final int POSITIF = 1;
	public static final int NEGATIF = 0;
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
			if (args[0].equals("-generate-multiple-term-combination")) {
				multipleTermsCombination = true;
				File corpusFolder = new File((String) args[1]);
				corpusFolderURL = corpusFolder.toPath().toString();
				System.out.println(corpusFolderURL);
				File ANNIE_HOME_FOLDER = new File((String) args[2]);
				ANNIE_HOME = ANNIE_HOME_FOLDER.toPath().toString();
				File termsListDefFolder = new File((String) args[3]);
				termsListDefFolderURL = termsListDefFolder.toPath().toString();
//				System.out.println(termsListDefFolderURL);
				xmlResultsFolderURL = corpusFolderURL + "/results";
				pairsOfTermsFileFolderURL = corpusFolderURL + "/pairs_of_terms";
				withFeatures  = true;
			}
		} else if (args.length == 3) {
			if (((String) args[0]).equals("-ttg-examples")) {
				if (((String) args[1]).equals("--ttg-all-sentences")) {
					Main.ttgAllSentences = true;
				}
				File file = new File((String) args[2]);
				Main.pairsOfTermsFileFolderURL = file.getParent();
				GateResultFile gateResultFile = createPairsFromPairsFile(file);
				gateResultFile.calculatePairFrequency();
				Out.prln("TreeTagging all sentences");
//				try {
//					gateResultFile.treeTagg(Main.ttgAllSentences);
//				} catch (TreeTaggerException e) {
//					e.printStackTrace();
//				}
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
				return;
			}
		} else if (args.length == 2) {
			for (int i = 0; i < args.length; i++) {
				if (((String) args[i]).equals("-validation")) {
					File pairsFile = new File((String) args[i + 1]);
					Main.pairsOfTermsFileFolderURL = pairsFile.getParent();
					GateResultFile gateResultFile = createPairsFromPairsFile(pairsFile);
					gateResultFile.validateWithBabelNet();
					return;
				} else if (((String) args[i]).equals("-ttg-examples")) {
					File file = new File((String) args[i + 1]);
					Main.pairsOfTermsFileFolderURL = file.getParent();
					Out.prln("Creating objects from BalbelNet Resulte File");
					GateResultFile gateResultFile = createExamplesFromBebelNetResultFile(file);
					Out.prln("Calculating pairs frequency");
					gateResultFile.calculatePairFrequency();
					Out.prln("Constructing final examples");
					gateResultFile.constructFinalNegatifExamples(1);
					gateResultFile.createTrainingSetFile();
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
					long waitTime = startTime + (long) 600000;
					Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(startTime);

					Out.prln("All threads are launched at : "
							+ calendar.get(Calendar.DAY_OF_MONTH) + " "
							+ calendar.get(Calendar.HOUR_OF_DAY) + ":"
							+ calendar.get(Calendar.MINUTE));
					while (!allThreadsAreTerminated) {
						long iTime = System.currentTimeMillis();
						int isItTimeToCheckThreads = new Long(waitTime)
								.compareTo(new Long(iTime));
						if (isItTimeToCheckThreads < 0) {
							int running = 0;
							for (Thread thread : threads) {
								if (thread.isAlive()) {
									running++;
									Out.prln(thread.getName() + " is running");
								}
							}
							if (running == 0) {
								allThreadsAreTerminated = true;
							} else {
								startTime = System.currentTimeMillis();
								calendar.setTimeInMillis(startTime);
								Out.prln(running + " threads are checked at : "
										+ calendar.get(Calendar.DAY_OF_MONTH)
										+ " "
										+ calendar.get(Calendar.HOUR_OF_DAY)
										+ ":" + calendar.get(Calendar.MINUTE));
								waitTime = startTime + (long) 600000;
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
				try {
					f.parse();
//					parseGateXml(out, f.getSentences());
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
//			gateResultFile.printPairsOfTerms();
			Out.prln("Saving pairs of terms");
			gateResultFile.savePairsInFile();
			gateResultFile.calculatePairFrequency();
		}

	}

	/*
	 * this method load GateResultFile from a babelnet result file
	 * pairs file is in this format : Term1\tTerm2\tClass\tsentence
	 */
	private static GateResultFile createExamplesFromBebelNetResultFile(File file)
			throws IOException {
		GateResultFile res = new GateResultFile(file.toURI().toURL());
		BufferedReader in = UsefulMethods.openUTF8BufferedReader(file);
		
//		File featuresFile = new File(file.getParent()+"/"+Files.getFileNameWithoutExtension(file.getName())+"_features.txt");
//		featuresFile.createNewFile();
//		File pairsFile = new File(file.getParent()+"/"+Files.getFileNameWithoutExtension(file.getName())+"_features_file_to_compare.txt");
//		pairsFile.createNewFile();
//		Writer featuresWriter = UsefulMethods.openUTF8OutputWriter(featuresFile);
//		Writer pairsWriter = UsefulMethods.openUTF8OutputWriter(pairsFile);
		
		String str;
		while ((str = in.readLine()) != null) {
			String[] line = str.split("\t", 4);
			String[] terms = {line[0], line[1]};
			
			String typeChar = line[2];
			String sentenceString = line[3];
			
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
//				p.createFeatures();
//				featuresWriter.append(p.getFeatures().toString()+"\t"+p.getType());
//				featuresWriter.append("\n");
//				pairsWriter.append(p.toString());
//				pairsWriter.append("\n");
				
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
//		UsefulMethods.closeUTF8OutputWriter(featuresWriter);
//		UsefulMethods.closeUTF8OutputWriter(pairsWriter);
		UsefulMethods.closeUTF8BufferedReader(in);
		return res;
	}

	/*
	 * this method load GateResultFile from a pairs file
	 * pairs file is in this format : Term1\tTerm2\t\sentence
	 */
	public static GateResultFile createPairsFromPairsFile(File pairsFile)
			throws IOException {
		GateResultFile res = new GateResultFile(pairsFile.toURI().toURL());
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(pairsFile), "UTF8"));
		String str;
		while ((str = in.readLine()) != null) {
			String[] line = str.split("\t", 3);
			Term term1, term2;
			Sentence sentence;
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

	@Deprecated
	public static void parseGateXml(File xmlGate, ArrayList<Sentence> sentences) throws Exception {
		new Exception("This method is remplaced by GateResultFile.parse()");
		try {
			XMLInputFactory factory = XMLInputFactory.newInstance();

			XMLEventReader reader = factory
					.createXMLEventReader(new FileReader(xmlGate));
			String originalText = null;
			
			ObjectOutputStream oosSentences =  new ObjectOutputStream(new FileOutputStream("temp/sentences.ser")) ;
			
			while (reader.hasNext()) {
				XMLEvent event = reader.nextEvent();
				int type = event.getEventType();
				switch (type) {
				case XMLStreamReader.START_ELEMENT:
					StartElement startElement = (StartElement) event;
					String elementName = startElement.getName().toString();
					//c'est les balises <Annotation> qui nous intéresse
					if (elementName.equals("Annotation")) {
						//on récupére d'abord les phrases
						if (startElement.getAttributeByName(new QName("Type"))
								.getValue().equals("Sentence")) {
							int startOffset = Integer.valueOf(startElement
									.getAttributeByName(new QName("StartNode"))
									.getValue());
							int endOffset = Integer.valueOf(startElement
									.getAttributeByName(new QName("EndNode"))
									.getValue());
							int id = Integer.valueOf(startElement
									.getAttributeByName(new QName("Id"))
									.getValue());
							String value = originalText.substring(startOffset, endOffset);
							Sentence sentence = new Sentence(startOffset,
									endOffset, id, value);
							oosSentences.writeObject(sentence);
							sentence = null;
						}
					} else if (elementName.equals("Feature")) {
						UsefulMethods.staxSkipToTheNextStartElement(reader);
						if (reader.getElementText().equals(
								"Original_document_content_on_load")) {
							//we get the original text
							UsefulMethods.staxSkipToTheNextStartElement(reader);
							originalText = reader.getElementText();
						}
					}
					break;
				default:
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
