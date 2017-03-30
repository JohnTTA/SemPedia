package org.mlhypernymextractor.core;

import gate.util.GateException;
import gate.util.Out;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class RunnableXMLGateFileParser implements Runnable{

	private File xmlGateFile;
	
	RunnableXMLGateFileParser(File gateFile){
		this.setXmlGateFile(gateFile);
	}
	@Override
	public void run() {
		// parser le fichier xml
		GateResultFile f;
		try {
			f = new GateResultFile(xmlGateFile.toURI()
					.toURL());
		
		Out.prln("Parsing Gate XML file : "
				+ f.getGateResultFileURL().toURI()
						.getPath());
		f.parse();
		Out.prln("Gate XML file parsed "
				+ f.getGateResultFileURL().toURI()
						.getPath());
		int nbTerm = 0;
		Out.prln("Extracting pairs of terms");
		for (Sentence s : f.getSentences()) {
			nbTerm += s.getTerms().size();
		}
		

//		f.extractPairs(Main.multipleTermsCombination);
		Out.prln(nbTerm
				+ " terms extracted from "
				+ f.getGateResultFileURL().toURI()
						.getPath());
//		f.printPairsOfTerms();
//		Out.prln("Saving pairs of terms for : "+xmlGateFile.getCanonicalFile());
//		f.savePairsInFile();
		// calculate frequency for each pair
		Out.prln("Calculating pair frequency for : "+xmlGateFile.getCanonicalFile());
		f.calculatePairFrequency();
		Out.prln("Parsing is successfully finished for : "+xmlGateFile.getCanonicalFile());
		// gateResultFiles.add(f);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (GateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public File getXmlGateFile() {
		return xmlGateFile;
	}
	public void setXmlGateFile(File xmlGateFile) {
		this.xmlGateFile = xmlGateFile;
	}

}
