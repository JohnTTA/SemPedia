package org.patternsandmaxentresultscomparator;

import gate.util.GateException;
import gate.util.Out;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class Main {

	public static String ANNIE_HOME = null;
	public static String TAGGER_FRAMEWORK_FOLDER = null;
	public static String CORPUS_FOLDER = null;
	public static String JAPE_RULES_FOLDER = null;
	public static String TREE_TAGGER_BIN = null;

	/**
	 * @param args
	 * @throws XMLStreamException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws XMLStreamException, IOException {
		System.out.println(args.length);
		if (args.length == 5) {
			ANNIE_HOME = args[0];
			TAGGER_FRAMEWORK_FOLDER = args[1];
			CORPUS_FOLDER = args[2];
			JAPE_RULES_FOLDER = args[3];
			TREE_TAGGER_BIN = args[4];
			try {
				GateLauncher.myGateApp();
			} catch (GateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (args.length == 3) {
			if (args[0].equals("-parse")) {
				File xmlFile = new File(args[1]);
				File textFile = new File(args[2]);
				parse(xmlFile, textFile);
			} else
				usage();
		} else
			usage();

	}

	private static void parse(File xmlFile, File textFile) throws XMLStreamException, IOException {
		
		XMLInputFactory factory = XMLInputFactory.newInstance();

		XMLEventReader reader =
		    factory.createXMLEventReader(
		        new FileReader(xmlFile));
		
		BufferedReader textReader = new BufferedReader(new InputStreamReader(new FileInputStream(textFile),"UTF-8"));
		
		while(reader.hasNext()){
			XMLEvent event = reader.nextEvent();
			int type = event.getEventType();
			switch (type) {
			case XMLStreamReader.START_ELEMENT:
				StartElement startElement = (StartElement)event;
				String elementName = startElement.getName().toString();
				if(elementName.equals("Annotation")){
					if(startElement.getAttributeByName(new QName("Type")).getValue().equals("Sentence") ){
						int startOffset = Integer.valueOf(startElement.getAttributeByName(new QName("StartNode")).getValue());
						int endOffset = Integer.valueOf(startElement.getAttributeByName(new QName("EndNode")).getValue());
						int id = Integer.valueOf(startElement.getAttributeByName(new QName("Id")).getValue());
						
						Sentence sentence = new Sentence(startOffset, endOffset, id, "");
					}
				}else if(elementName.equals("Feature")){
					while(!reader.nextEvent().isStartElement()){
						
					}
					
					if(reader.getElementText().equals("Original_document_content_on_load")){
						while(!reader.nextEvent().isStartElement()){
							
						}
							
						String text = reader.getElementText();
						System.out.println(text);
						break;
					}
				}
				break;

			default:
				break;
			}
			
		}
	}

	public static void usage() {
		Out.prln("Usage: java -jar app_name.jar [args]:\n"
				+ "\t args[0] : ANNIE_HOME\n"
				+ "\t args[1] : TAGGER_FRAMEWORK_FOLDER\n"
				+ "\t args[2] : CORPUS_FOLDER\n"
				+ "\t args[3] : JAPE_RULES_FOLDER\n"
				+ "\t args[4] : TREE_TAGGER_BIN\n");
	}
}
