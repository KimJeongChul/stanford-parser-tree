import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
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

class ParserTreeSimliarity {
	public static void main(String[] args) {
		String parserModel = "edu/stanford/nlp/models/lexparser/englishRNN.ser.gz";
	    if (args.length > 0) {
	      parserModel = args[0];
	      String testFile = args[1];
	      LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);
	      parserTree(lp, testFile);
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

	    for(int i = 0; i < ls.size() ; i += 2) {
	    	List<HasWord> sentence1 = ls.get(i);
	    	List<HasWord> sentence2 = ls.get(i+1);

	    	Tree t1 = conversionTree(lp, gsf, sentence1);
    		Tree t2 = conversionTree(lp, gsf, sentence2);
    		
    		if(t1.equals(t2)) {
      			System.out.println("Tree1 and Tree 2 Simliarity: 100 % ");
      		}else {
				ArrayList<String> t1_value = new ArrayList<String>();
    			ArrayList<String> t2_value = new ArrayList<String>();
    			t1_value = bfs(t1);
    			t2_value = bfs(t2);
      		}
    		System.out.println();
	    }
 	}

 	public static Tree conversionTree(LexicalizedParser lp, GrammaticalStructureFactory gsf ,List<HasWord> sentence) {
	    System.out.println(sentence);
	    Tree parse = lp.apply(sentence);
	    parse.pennPrint();

	    GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
	    Collection tdl = gs.typedDependenciesCCprocessed();
	    System.out.println("Tree number of node : " + tdl.size());
	    System.out.println("Tree Depth : " + parse.depth());

	    System.out.println();
	    return parse;
	}

	public static void dfs(Tree node) {
		Tree[] child = node.children();
		int len = child.length;
		System.out.println(node.value());
		for(int i = 0; i < len; i++) dfs(child[i]);
	}

	public static ArrayList<String> bfs(Tree node){
		LinkedList<Tree> queue_value = new LinkedList<Tree>();
	    LinkedList<Integer> queue_level = new LinkedList<Integer>();
	    Tree present;
	    Tree child[];
	    int lv;
	    Integer lev = new Integer(0);
	    
	    ArrayList<String> result = new ArrayList<String>();
	    ArrayList<Integer> level = new ArrayList<Integer>();
	    queue_value.offer(node);
	    queue_level.offer(lev);

	    while(!queue_value.isEmpty()){
	      present = queue_value.poll();
	      lv = queue_level.poll().intValue();
	      System.out.println("level" + lv + " value : " + present.value());
	      result.add(present.value());
	      level.add(lv);
	      child = present.children();
	      lev = new Integer(lv + 1);

	      for(int i = 0; i < child.length; i++) { 
	        if(!child[i].isLeaf()) {
	          queue_value.offer(child[i]);
	          queue_level.offer(lev);
	        }
	      }
	    }

	    System.out.println();
	    return result;	
	}
}
