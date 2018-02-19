import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.*;
import java.io.StringReader;

import edu.stanford.nlp.process.LexedTokenFactory;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;

import edu.stanford.nlp.util.*;

public class ParserTreeCondition {
	public static class TreeSimilarity {
		private final Tree tree;
		private ArrayList<String> dfs_value;
	    private ArrayList<Integer> dfs_index;

	    private String condition;
	    private String consequence;
	    private String temp;

	    private boolean isPrint;
	    private boolean isCondition;
	    private boolean isPre;
	    private boolean isPost;
	    private boolean isLeaf;
	    private boolean isComma;

		public TreeSimilarity(Tree tree) {
			this.tree = tree;
			this.dfs_value = new ArrayList<String>();
			this.dfs_index =  new ArrayList<Integer>();

			this.condition = "";
			this.consequence = "";
			this.temp = "";

			this.isPrint = false;
			this.isCondition = false;
			this.isPre = false;
			this.isPost = false;
			this.isLeaf = false;
			this.isComma = false;
		}
		public String getCondition() {
			return this.condition;
		}

		public String getConsequence() {
			return this.consequence;
		}

		public void isPrint(boolean isPrint) {
			this.isPrint = isPrint;
		}

		public void dfsConditionTraverse(Tree node, int idx){
			Tree[] child = node.children();
			int len = child.length;
			if(!this.dfs_index.isEmpty()) {
				idx = this.dfs_index.get(this.dfs_index.size()-1);
			}
			idx += 1;
			if(this.isLeaf == true)
				temp += node.value() + " ";

			if(node.value().equals("SBAR")) {
				this.isCondition = true;
				if(idx == 3) this.isPre = true;
				else {
					this.isPost = true;
					this.consequence = temp;
				}
			}
			if(this.isCondition == true && this.isPre == true && this.isLeaf == true) {
				if(this.isComma == true && !node.value().equals("then")) this.consequence += node.value() + " ";
				else if(this.isComma == false && !node.value().equals("When") && (!node.value().equals("If")) && (!node.value().equals(","))) this.condition += node.value() + " ";
				if(node.value().equals(",")) this.isComma = true;
			}
			if(this.isCondition == true && this.isPost == true && this.isLeaf == true) {
				if(!node.value().equals("when") && (!node.value().equals("if"))) this.condition += node.value() + " ";
			}
			this.dfs_index.add(idx);
			this.dfs_value.add(node.value());
			for(int i = 0; i < len; i++) {
				if(child[i].isLeaf()) this.isLeaf = true;
				else this.isLeaf = false;
				this.dfsConditionTraverse(child[i], idx+i);
			} 
		}
	} 
	public static void main(String[] args) {
		String parserModel = "edu/stanford/nlp/models/lexparser/englishRNN.ser.gz";
	    if (args.length > 0) {
		  long startTime = System.currentTimeMillis();
	      parserModel = args[0];
	      String testFile = args[1];
	      LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);
	      parserTree(lp, testFile);
	      long endTime = System.currentTimeMillis();
	      long duration = endTime - startTime;
	      System.out.println("Program Execution Duration  : " + duration + "(ms)");
	    }
 	}

 	public static void parserTree(LexicalizedParser lp, String filename) {
 		TreebankLanguagePack tlp = lp.treebankLanguagePack(); // a PennTreebankLanguagePack for English
	    GrammaticalStructureFactory gsf = null;
	    if (tlp.supportsGrammaticalStructures()) {
	      gsf = tlp.grammaticalStructureFactory();
	    }

	    List<List<HasWord>> ls = new ArrayList<List<HasWord>>();
	    for (List<HasWord> sentence : new DocumentPreprocessor(filename)) {
	      ls.add(sentence);
	    }
	     
	    for(int idx = 0; idx < ls.size() ; idx += 2) {
	    	List<HasWord> sentence1 = ls.get(idx);
	    	List<HasWord> sentence2 = ls.get(idx + 1);
	    	List<String> sentenceList = new ArrayList<String>();

	    	String s1 = SentenceUtils.listToString(sentence1);
	    	String s2 = SentenceUtils.listToString(sentence2);
	    	sentenceList.add(s1);
	    	sentenceList.add(s2);

	    	Tree t1 = conversionTree(lp, gsf, sentence1);
    		Tree t2 = conversionTree(lp, gsf, sentence2);
    		
			TreeSimilarity ts1 = new TreeSimilarity(t1);
			TreeSimilarity ts2 = new TreeSimilarity(t2);
			ts1.isPrint(false);
			ts2.isPrint(false);

			ts1.dfsConditionTraverse(t1, 0);
			ts2.dfsConditionTraverse(t2, 0);

			System.out.println("[*] " + s1);
			System.out.println("Condition : " + ts1.getCondition());
			System.out.println("Consequence : " + ts1.getConsequence());
  		
			System.out.println("[*] " + s2);
			System.out.println("Condition : " + ts2.getCondition());
			System.out.println("Consequence : " + ts2.getConsequence());

    		System.out.println();
	    }
 	}

 	public static Tree conversionTree(LexicalizedParser lp, GrammaticalStructureFactory gsf, List<HasWord> sentence) {
	    //System.out.println(sentence);
	    Tree parse = lp.apply(sentence);
	    //parse.pennPrint();

	    GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
	    Collection tdl = gs.typedDependenciesCCprocessed();
	    //System.out.println("Tree number of node : " + tdl.size());
	    //System.out.println("Tree Depth : " + parse.depth());

	    //System.out.println();
	    return parse;
	}

	public static void sentenceCondition(List<HasWord> sentence) {
		String condition = "";
		String consequence = "";
		boolean isCondition = false;
		boolean isConsequence = false;
		for(HasWord w : sentence) {
			String str = w.word();
			if(str.equals("If")) {
				isCondition = true;
				continue;
			}
			if(isCondition == true && str.equals(",")) {
				isCondition = false;
				isConsequence = true;
				continue;
			}
			if(isCondition == true) {
				condition += str + " ";
			}
			
			if(isConsequence == true) {
				consequence += str + " ";
			}
		}
		System.out.println("Condition : " + condition);
		System.out.println("Consequence : " + consequence);
	}
}