package org.mlhypernymextracor.learning.features;

import gate.AnnotationSet;
import gate.Document;
import gate.creole.ResourceInstantiationException;
import gate.util.OffsetComparator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.annolab.tt4j.TreeTaggerException;
import org.annolab.tt4j.TreeTaggerWrapper;
import org.mlhypernymextractor.core.GateResultFile;
import org.mlhypernymextractor.core.MyTokenHandler;
import org.mlhypernymextractor.core.Pair;
import org.mlhypernymextractor.utils.UsefulMethods;

import com.google.common.collect.Lists;

public class Features {

	private String[] sentencePosAndLemmesTags;
	private String[] leftTokens;
	private String[] rightTokens;
	private String[] fromTerm1ToTerm2PosAndLemmes;
	private int sentenceTokensCount = 0;
	private int tokensFromTerm1ToTerm2count = 0;
	private boolean inclusion = false;
			
	// window size: tokens count befor term1 and after term2
	public Features(Pair pair, int windowSize) {
		try {
			this.generateFeatures(pair, windowSize);
		} catch (IOException | TreeTaggerException e) {
			e.printStackTrace();
		}
	}

	private void generateFeatures(Pair pair, int windowSize)
			throws IOException, TreeTaggerException {
		String sentence = pair.getSentence().getValue();
		String term1 = pair.getTerm1().getValue();
		String term2 = pair.getTerm2().getValue();

		String[] sentenceTokens = UsefulMethods.tokenize(sentence);
		this.sentenceTokensCount = sentenceTokens.length;
		
		
		// handleSentencePosAndLemmesTags(pair);
		this.sentencePosAndLemmesTags = treetag(sentence);
		int[] indexTerm1 = UsefulMethods.getTermIndexInSentenceTokens(term1,
				sentenceTokens, 1);
		int[] indexTerm2 = UsefulMethods.getTermIndexInSentenceTokens(term2,
				sentenceTokens, 2);
		if (indexTerm1 != null && indexTerm2 != null) {
			int startOffsetTerm1 = indexTerm1[0];
			int startOffsetTerm2 = indexTerm2[0];
			int endOffsetTerm1 = indexTerm1[1];
			int endOffsetTerm2 = indexTerm2[1];

			if(startOffsetTerm1<=startOffsetTerm2 && endOffsetTerm2<=endOffsetTerm1 || startOffsetTerm1>=startOffsetTerm2 && endOffsetTerm2>=endOffsetTerm1)
				this.inclusion = true;
			
			
			if(!inclusion){
				for (int i = startOffsetTerm1; i <= endOffsetTerm1; i++) {
					this.sentencePosAndLemmesTags[i] = "Terme1";
				}
			
				for (int i = startOffsetTerm2; i <= endOffsetTerm2; i++) {
						this.sentencePosAndLemmesTags[i] = "Terme2";
				}
			}else{
				if(startOffsetTerm1<=startOffsetTerm2 && endOffsetTerm2<=endOffsetTerm1){
					for (int i = startOffsetTerm2; i <= endOffsetTerm2; i++) {
						this.sentencePosAndLemmesTags[i] = "Terme2";
					}
					for (int i = startOffsetTerm1; i <= endOffsetTerm1; i++) {
						if(!this.sentencePosAndLemmesTags[i].equals("Terme2"))
						this.sentencePosAndLemmesTags[i] = "Terme1";
					}
				}else{
					for (int i = startOffsetTerm1; i <= endOffsetTerm1; i++) {
						this.sentencePosAndLemmesTags[i] = "Terme1";
					}
					for (int i = startOffsetTerm2; i <= endOffsetTerm2; i++) {
						if(!this.sentencePosAndLemmesTags[i].equals("Terme1"))
						this.sentencePosAndLemmesTags[i] = "Terme2";
					}
					
				}
				
			}
			List<String> sentencePosAndLemmesTagsList = new ArrayList<>();
			
			
			
			boolean terme1found = false;
			boolean terme2found = false;
			for (int i = 0; i < this.sentencePosAndLemmesTags.length; i++) {
				if(this.sentencePosAndLemmesTags[i].equals("Terme1"))
					if(!terme1found){
						sentencePosAndLemmesTagsList.add(this.sentencePosAndLemmesTags[i]);
						terme1found = true;
					}	
					else{
//						endOffsetTerm1--;
//						i++;
					}
				else if(this.sentencePosAndLemmesTags[i].equals("Terme2"))
					if(!terme2found){
						sentencePosAndLemmesTagsList.add(this.sentencePosAndLemmesTags[i]);
						terme2found = true;
					}	
					else{
//						endOffsetTerm2--;
//						i++;
						}
				else sentencePosAndLemmesTagsList.add(this.sentencePosAndLemmesTags[i]);
			}

			this.sentencePosAndLemmesTags = sentencePosAndLemmesTagsList.toArray(new String[sentencePosAndLemmesTagsList.size()]);
			
			startOffsetTerm1 = sentencePosAndLemmesTagsList.indexOf("Terme1");
			endOffsetTerm2 = sentencePosAndLemmesTagsList.indexOf("Terme2");
			handleWindowTokens(startOffsetTerm1, endOffsetTerm2, windowSize, this.sentencePosAndLemmesTags);
			
//			if (startOffsetTerm1 < startOffsetTerm2) {
//				handleWindowTokens(startOffsetTerm1, endOffsetTerm2,
//						windowSize, this.sentencePosAndLemmesTags);
//
//			} else if (startOffsetTerm1 > startOffsetTerm2) {
//				handleWindowTokens(startOffsetTerm2, endOffsetTerm1,
//						windowSize, this.sentencePosAndLemmesTags);
//			} else if (startOffsetTerm1 == startOffsetTerm2) {
//				if (endOffsetTerm1 < endOffsetTerm2) {
//					handleWindowTokens(startOffsetTerm1, endOffsetTerm2,
//							windowSize, this.sentencePosAndLemmesTags);
//				} else {
//					handleWindowTokens(startOffsetTerm2, endOffsetTerm1,
//							windowSize, this.sentencePosAndLemmesTags);
//				}
//			}
		}

	}

	private void handleTokensPositions(){
//		String[] sentenceTokens = (String[])ArrayUtils.addAll(leftTokens, fromTerm1ToTerm2PosAndLemmes);
//		sentenceTokens = (String[])ArrayUtils.addAll(sentenceTokens, rightTokens);
		
		List<String> sentenceTokensList = new ArrayList<>();
		sentenceTokensList.addAll(Arrays.asList(this.leftTokens));
		sentenceTokensList.addAll(Arrays.asList(this.fromTerm1ToTerm2PosAndLemmes));
		sentenceTokensList.addAll(Arrays.asList(this.rightTokens));
			
		int terme1Index = sentenceTokensList.indexOf("Terme1");
		int terme2Index = sentenceTokensList.indexOf("Terme2");
		
		for (int i = 0; i < sentenceTokensList.size(); i++) {
			
		}
	}
	
	
	private void handleSentencePosAndLemmesTags(Pair pair)
			throws ResourceInstantiationException {
		Document doc = GateResultFile.getGateDocument();

		AnnotationSet inputAS = doc.getAnnotations();
		AnnotationSet sentenceTTGAnnotations = inputAS.get("Token", new Long(
				pair.getSentence().getStartOffset()), new Long(pair
				.getSentence().getEndOffset()));
		List<gate.Annotation> mySentenceTokens = new ArrayList<gate.Annotation>(
				sentenceTTGAnnotations);
		Collections.sort(mySentenceTokens, new OffsetComparator());

		List<gate.Annotation> myTerms = new ArrayList<gate.Annotation>();
		myTerms.add(inputAS.get(pair.getTerm1().getId()));
		myTerms.add(inputAS.get(pair.getTerm2().getId()));
		Collections.sort(myTerms, new OffsetComparator());

		AnnotationSet leftTokensAnnotationSet = inputAS.get("Token",
				mySentenceTokens.get(0).getStartNode().getOffset(), myTerms
						.get(0).getStartNode().getOffset());
		// .get(0).getStartNode().getOffset());
		AnnotationSet rightTokensAnnotationSet = inputAS.get("Token", myTerms
				.get(1).getEndNode().getOffset(),
				mySentenceTokens.get(mySentenceTokens.size() - 1).getEndNode()
						.getOffset());

		List<gate.Annotation> leftTokenGateAnnotationList = new ArrayList<>(
				leftTokensAnnotationSet);
		List<gate.Annotation> rightTokenGateAnnotationList = new ArrayList<>(
				rightTokensAnnotationSet);
		Collections.sort(leftTokenGateAnnotationList, new OffsetComparator());
		Collections.sort(rightTokenGateAnnotationList, new OffsetComparator());

		List<String> sentencePosAndLemmesTagsList = new ArrayList<String>();
		List<String> leftTokensList = new ArrayList<String>();
		List<String> rightTokensList = new ArrayList<String>();

		for (gate.Annotation annotation : mySentenceTokens) {
			String cat = (String) annotation.getFeatures().get("category");
			String lemma = (String) annotation.getFeatures().get("lemma");

			if (annotation.withinSpanOf(myTerms.get(0))) {
				sentencePosAndLemmesTagsList.add("Terme1");
			} else if (annotation.withinSpanOf(myTerms.get(1))) {
				sentencePosAndLemmesTagsList.add("Terme2");
			} else {
				sentencePosAndLemmesTagsList.add(cat + "/" + lemma);
				if (annotation.getEndNode().getOffset() < myTerms.get(0)
						.getStartNode().getOffset()) {
					leftTokensList.add(cat + "/" + lemma);
				} else if (annotation.getStartNode().getOffset() > myTerms
						.get(1).getEndNode().getOffset()) {
					rightTokensList.add(cat + "/" + lemma);
				}
			}
		}

		if (leftTokenGateAnnotationList.size() > 3) {
			leftTokenGateAnnotationList = leftTokenGateAnnotationList.subList(
					leftTokenGateAnnotationList.size() - 3,
					leftTokenGateAnnotationList.size() - 1);
		}
		if (rightTokenGateAnnotationList.size() > 3) {
			rightTokenGateAnnotationList = rightTokenGateAnnotationList
					.subList(0, 2);
		}

		for (gate.Annotation annotation : leftTokenGateAnnotationList) {
			String cat = (String) annotation.getFeatures().get("category");
			String lemma = (String) annotation.getFeatures().get("lemma");

			leftTokensList.add(cat + "/" + lemma);
		}

		for (gate.Annotation annotation : rightTokenGateAnnotationList) {
			String cat = (String) annotation.getFeatures().get("category");
			String lemma = (String) annotation.getFeatures().get("lemma");
			rightTokensList.add(cat + "/" + lemma);
		}

		this.leftTokens = rightTokensList.toArray(new String[leftTokensList
				.size()]);
		this.rightTokens = rightTokensList.toArray(new String[rightTokensList
				.size()]);
		this.sentencePosAndLemmesTags = sentencePosAndLemmesTagsList
				.toArray(new String[sentencePosAndLemmesTagsList.size()]);
	}

	private void handleWindowTokens(int leftStartOffset, int rightEndOffset,
			int windowSize, String[] ttgSentence) {
		List<String> leftTokensList = new ArrayList<>();
		List<String> rightTokensList = new ArrayList<>();
		List<String> fromTerm1toTerm2List = new ArrayList<>();

		for (int i = leftStartOffset; i <= rightEndOffset; i++) {
			if(i>-1)
				fromTerm1toTerm2List.add(ttgSentence[i]);
		}
		this.fromTerm1ToTerm2PosAndLemmes = fromTerm1toTerm2List
				.toArray(new String[fromTerm1toTerm2List.size()]);
		this.tokensFromTerm1ToTerm2count = this.fromTerm1ToTerm2PosAndLemmes.length-2;

		// mettre les termes comme boite noire
		for (int i = leftStartOffset - 1; i >= leftStartOffset - windowSize; i--) {
			if (i >= 0) {
				leftTokensList.add(ttgSentence[i]);
			}
		}
		leftTokensList = Lists.reverse(leftTokensList);
		this.leftTokens = leftTokensList.toArray(new String[leftTokensList
				.size()]);
		// System.out.println("gauche = " + leftTokensList);
		for (int i = rightEndOffset + 1; i <= rightEndOffset + windowSize; i++) {
			if (i < ttgSentence.length && i >-1) {
				rightTokensList.add(ttgSentence[i]);
			}
		}
		// System.out.println("droite = " + rightTokensList);
		this.rightTokens = rightTokensList.toArray(new String[rightTokensList
				.size()]);
	}

	private String[] treetag(String sentence) throws IOException,
			TreeTaggerException {
//		System.setProperty("treetagger.home",
//				"/home/ghamnia/textToBabelnetValidation/TextToBabelNetValidation/TreeTagger2");
		 System.setProperty("treetagger.home", "C:\\Treetagger");
		TreeTaggerWrapper<String> tt = new TreeTaggerWrapper<String>();
//		tt.setModel("/home/ghamnia/textToBabelnetValidation/TextToBabelNetValidation/TreeTagger2/model/french.par");
		 tt.setModel("C:\\Treetagger\\models\\fr.par");
		tt.setHandler(new MyTokenHandler());
		String[] tokens = UsefulMethods.tokenize(sentence);
		tt.process(tokens);
		String[] ttgSentence = ((MyTokenHandler) tt.getHandler())
				.getTTGString();
		tt.destroy();
		return ttgSentence;
	}

	public String[] getSentencePosAndLemmesTags() {
		return sentencePosAndLemmesTags;
	}

	public void setSentencePosAndLemmesTags(String[] sentencePosAndLemmesTags) {
		this.sentencePosAndLemmesTags = sentencePosAndLemmesTags;
	}

	public String[] getLeftTokens() {
		return leftTokens;
	}

	public void setLeftTokens(String[] leftTokens) {
		this.leftTokens = leftTokens;
	}

	public String[] getRightTokens() {
		return rightTokens;
	}

	public void setRightTokens(String[] rightTokens) {
		this.rightTokens = rightTokens;
	}

	// Terme1 et Terme2 comme features + phrase
	// @Override
	// public String toString() {
	// String res = "";
	// for (String sentenceTtgToken : this.sentencePosAndLemmesTags) {
	// if (sentenceTtgToken.contains("Terme1") && !res.contains("Terme1"))
	// res += "Terme1" + " ";
	// else if (sentenceTtgToken.contains("Terme1")
	// && res.contains("Terme1")) {
	// } else if (sentenceTtgToken.contains("Terme2")
	// && !res.contains("Terme2"))
	// res += "Terme2" + " ";
	// else if (sentenceTtgToken.contains("Terme2")
	// && res.contains("Terme2")) {
	//
	// } else
	// res += sentenceTtgToken + " ";
	// }
	//
	// if (this.leftTokens != null && this.rightTokens != null) {
	// if (this.leftTokens.length == 0)
	// res += "null ";
	// else
	// for (String leftToken : this.leftTokens) {
	// res += leftToken + " ";
	// }
	//
	// if (this.rightTokens.length == 0)
	// res += "null ";
	// else
	// for (String rightToken : this.rightTokens) {
	// res += rightToken + " ";
	// }
	// }
	// res = res.replaceAll("  ", " ");
	// res = res.replaceAll("Terme1", "Terme");
	// res = res.replaceAll("Terme2", "Terme");
	// System.out.println(res);
	// return res;
	// }

	// from term1 to term2 features
//	@Override
//	public String toString() {
//		
////		List<String> sentenceTokensList = new ArrayList<>();
////		if(this.leftTokens != null)
////			sentenceTokensList.addAll(Arrays.asList(this.leftTokens));
////		if(this.fromTerm1ToTerm2PosAndLemmes != null)
////			sentenceTokensList.addAll(Arrays.asList(this.fromTerm1ToTerm2PosAndLemmes));
////		if(this.rightTokens != null)
////			sentenceTokensList.addAll(Arrays.asList(this.rightTokens));
////			
////		int terme1Index = sentenceTokensList.indexOf("Terme1");
////		int terme2Index = sentenceTokensList.indexOf("Terme2");
////		
////		String res ="";
////		String positions="";
////		for (int i = 0; i < sentenceTokensList.size(); i++) {
////			res += sentenceTokensList.get(i)+"/";
////			if(terme1Index != -1 && terme2Index != -1 ){
//////				if(i!=terme1Index && i!=terme2Index)
////					res += (i-terme1Index) +"/"+(i-terme2Index)+" ";
////			}
////			else{
////				if(terme1Index == -1)
////					res += (i-terme2Index) +"/"+(i-terme2Index)+" ";
////				else
////					res += (i-terme1Index) +"/"+(i-terme1Index)+" ";
////			}
////				
//////				positions += "("+(i-terme1Index)+","+(i-terme2Index)+")"+" ";
////		}
//////		res +=positions;
////		if(this.tokensFromTerm1ToTerm2count>=0)
////			res+=this.tokensFromTerm1ToTerm2count+" ";
////		else res+="0"+" ";
////		res+=this.sentenceTokensCount+" ";
////		boolean verb = res.contains("VER");
////		res+= Boolean.toString(verb)+" ";
////		res+= Boolean.toString(this.inclusion)+" ";
//		
//		String res = "";
//		if (this.leftTokens != null) {
//			if (this.leftTokens.length == 0)
//				res += "";
//			else
//				for (String leftToken : this.leftTokens) {
//					res += leftToken + " ";
//				}
//		}
//		if (this.fromTerm1ToTerm2PosAndLemmes != null)
//			for (String sentenceTtgToken : this.fromTerm1ToTerm2PosAndLemmes) {
//				if (sentenceTtgToken.contains("Terme1")
//						&& !res.contains("Terme1"))
//					res += "Terme1" + " ";
//				else if (sentenceTtgToken.contains("Terme1")
//						&& res.contains("Terme1")) {
//				} else if (sentenceTtgToken.contains("Terme2")
//						&& !res.contains("Terme2"))
//					res += "Terme2" + " ";
//				else if (sentenceTtgToken.contains("Terme2")
//						&& res.contains("Terme2")) {
//
//				} else
//					res += sentenceTtgToken + " ";
//			}
//
//		if (this.rightTokens != null) {
//			if (this.rightTokens.length == 0)
//				res += "";
//			else
//				for (String rightToken : this.rightTokens) {
//					res += rightToken + " ";
//				}
//		}
//		res = res.replaceAll("  ", " ");
//		res = res.replaceAll("Terme1", "Terme");
//		res = res.replaceAll("Terme2", "Terme");
////		System.out.println(res);
////		System.out.println(res.replaceAll("\\s+$", ""));
//		return res.replaceAll("\\s+$", "");
//	}

	
	@Override
	//traits riches 1
	public String toString() {
		
//		List<String> sentenceTokensList = new ArrayList<>();
//		if(this.leftTokens != null)
//			sentenceTokensList.addAll(Arrays.asList(this.leftTokens));
//		if(this.fromTerm1ToTerm2PosAndLemmes != null)
//			sentenceTokensList.addAll(Arrays.asList(this.fromTerm1ToTerm2PosAndLemmes));
//		if(this.rightTokens != null)
//			sentenceTokensList.addAll(Arrays.asList(this.rightTokens));
//			
//		int terme1Index = sentenceTokensList.indexOf("Terme1");
//		int terme2Index = sentenceTokensList.indexOf("Terme2");
//		
//		String res ="";
//		String positions="";
//		for (int i = 0; i < sentenceTokensList.size(); i++) {
//			res += sentenceTokensList.get(i)+" ";
////			if(terme1Index != -1 && terme2Index != -1 ){
//////				if(i!=terme1Index && i!=terme2Index)
////					res += (i-terme1Index) +"/"+(i-terme2Index)+" ";
////			}
////			else{
////				if(terme1Index == -1)
////					res += (i-terme2Index) +"/"+(i-terme2Index)+" ";
////				else
////					res += (i-terme1Index) +"/"+(i-terme1Index)+" ";
////			}
//				
//				positions += "("+(i-terme1Index)+","+(i-terme2Index)+")"+" ";
//		}
//		res +=positions;
//		if(this.tokensFromTerm1ToTerm2count>=0)
//			res+=this.tokensFromTerm1ToTerm2count+" ";
//		else res+="-1"+" ";
//		res+=this.sentenceTokensCount+" ";
//		boolean verb = res.contains("VER");
//		res+= Boolean.toString(verb)+" ";
//		res+= Boolean.toString(this.inclusion)+" ";
		
		String res = "";
		if (this.leftTokens != null) {
			if (this.leftTokens.length == 0)
				res += "";
			else
				for (String leftToken : this.leftTokens) {
					res += leftToken + " ";
				}
		}
		if (this.fromTerm1ToTerm2PosAndLemmes != null)
			for (String sentenceTtgToken : this.fromTerm1ToTerm2PosAndLemmes) {
				if (sentenceTtgToken.contains("Terme1")
						&& !res.contains("Terme1"))
					res += "Terme1" + " ";
				else if (sentenceTtgToken.contains("Terme1")
						&& res.contains("Terme1")) {
				} else if (sentenceTtgToken.contains("Terme2")
						&& !res.contains("Terme2"))
					res += "Terme2" + " ";
				else if (sentenceTtgToken.contains("Terme2")
						&& res.contains("Terme2")) {

				} else
					res += sentenceTtgToken + " ";
			}

		if (this.rightTokens != null) {
			if (this.rightTokens.length == 0)
				res += "";
			else
				for (String rightToken : this.rightTokens) {
					res += rightToken + " ";
				}
		}
		res = res.replaceAll("  ", " ");
		res = res.replaceAll("Terme1", "Terme");
		res = res.replaceAll("Terme2", "Terme");
//		System.out.println(res);
//		System.out.println(res.replaceAll("\\s+$", ""));
		return res.replaceAll("\\s+$", "");
	}
}