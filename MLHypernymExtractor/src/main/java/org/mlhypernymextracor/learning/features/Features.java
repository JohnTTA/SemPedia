package org.mlhypernymextracor.learning.features;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.annolab.tt4j.TreeTaggerException;
import org.annolab.tt4j.TreeTaggerWrapper;
import org.mlhypernymextractor.core.MyTokenHandler;
import org.mlhypernymextractor.core.Pair;
import org.mlhypernymextractor.utils.UsefulMethods;

import com.google.common.collect.Lists;

public class Features {

	private String[] sentencePosAndLemmesTags;
	private String[] leftTokens;
	private String[] rightTokens;

	// window size: tokens count befor term1 and after term2
	public Features(Pair pair, int windowSize) {
		this.generateFeatures(pair, windowSize);
	}

	private void generateFeatures(Pair pair, int windowSize) {
		try {
			String sentence = pair.getSentence().getValue();
			String term1 = pair.getTerm1().getValue();
			String term2 = pair.getTerm2().getValue();

			String[] sentenceTokens = UsefulMethods.tokenize(sentence);

			int[] indexTerm1 = UsefulMethods.getTermIndexInSentenceTokens(
					term1, sentenceTokens);
			int[] indexTerm2 = UsefulMethods.getTermIndexInSentenceTokens(
					term2, sentenceTokens);

			this.sentencePosAndLemmesTags = treetag(sentence);

			if (indexTerm1 != null && indexTerm2 != null) {
				int startOffsetTerm1 = indexTerm1[0];
				int startOffsetTerm2 = indexTerm2[0];
				int endOffsetTerm1 = indexTerm1[1];
				int endOffsetTerm2 = indexTerm2[1];

				if (startOffsetTerm1 < startOffsetTerm2) {
					handleWindowTokens(startOffsetTerm1, endOffsetTerm2,
							windowSize, this.sentencePosAndLemmesTags);

				} else if (startOffsetTerm1 > startOffsetTerm2) {
					handleWindowTokens(startOffsetTerm2, endOffsetTerm1,
							windowSize, this.sentencePosAndLemmesTags);
				} else if (startOffsetTerm1 == startOffsetTerm2) {
					if (endOffsetTerm1 < endOffsetTerm2) {
						handleWindowTokens(startOffsetTerm1, endOffsetTerm2,
								windowSize, this.sentencePosAndLemmesTags);
					} else {
						handleWindowTokens(startOffsetTerm2, endOffsetTerm1,
								windowSize, this.sentencePosAndLemmesTags);
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (TreeTaggerException e) {
			e.printStackTrace();
		}
	}

	private void handleWindowTokens(int leftStartOffset, int rightEndOffset,
			int windowSize, String[] ttgSentence) {
		List<String> leftTokensList = new ArrayList<>();
		List<String> rightTokensList = new ArrayList<>();
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
			if (i < ttgSentence.length) {
				rightTokensList.add(ttgSentence[i]);
			}
		}
		// System.out.println("droite = " + rightTokensList);
		this.rightTokens = rightTokensList.toArray(new String[rightTokensList
				.size()]);
	}

	private String[] treetag(String sentence) throws IOException,
			TreeTaggerException {
		System.setProperty("treetagger.home",
				"/home/ghamnia/textToBabelnetValidation/TextToBabelNetValidation/TreeTagger2");
		// System.setProperty("treetagger.home", "C:\\Treetagger");
		TreeTaggerWrapper<String> tt = new TreeTaggerWrapper<String>();
		tt.setModel("/home/ghamnia/textToBabelnetValidation/TextToBabelNetValidation/TreeTagger2/model/french.par");
		// tt.setModel("C:\\Treetagger\\models\\fr.par");
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

	@Override
	public String toString() {
		String res = "";
		for (String sentenceTtgToken : this.sentencePosAndLemmesTags) {
			res += sentenceTtgToken + " ";
		}
		if (res.lastIndexOf(" ") == res.length() - 1)
			res = res.substring(0, res.lastIndexOf(" "));

		if (this.leftTokens != null && this.rightTokens != null) {
			res += " ";
			if (this.leftTokens.length == 0)
				res += "null ";
			else
				for (String leftToken : this.leftTokens) {
					res += leftToken + " ";
				}

			if (res.lastIndexOf(" ") == res.length() - 1)
				res = res.substring(0, res.lastIndexOf(" "));

			res += " ";
			if (this.rightTokens.length == 0)
				res += "null ";
			else
				for (String rightToken : this.rightTokens) {
					res += rightToken + " ";
				}
			if (res.lastIndexOf(" ") == res.length() - 1)
				res = res.substring(0, res.lastIndexOf(" "));
		}
		return res;
	}

}