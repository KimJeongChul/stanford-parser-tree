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

public class ParserTreeSimliarity {
	public static class BFS {
		private final Tree tree;
		private ArrayList<String> value;
	    private ArrayList<Integer> level;

		public BFS(Tree tree) {
			this.tree = tree;
			this.value = new ArrayList<String>();
			this.level =  new ArrayList<Integer>();
		}

		public ArrayList<String> getValue() {
			return this.value;
		}

		public ArrayList<Integer> getLevel() {
			return this.level;
		}

		public void traverse(){
			LinkedList<Tree> queue_value = new LinkedList<Tree>();
			LinkedList<Integer> queue_level = new LinkedList<Integer>();
			
			Tree present;
		    Tree child[];
		    
		    int lv;
		    Integer lev = new Integer(0);

		    queue_value.offer(this.tree);
	    	queue_level.offer(lev);

	    	while(!queue_value.isEmpty()){
				present = queue_value.poll();
				lv = queue_level.poll().intValue();
				System.out.println("level" + lv + " value : " + present.value());
				this.value.add(present.value());
				this.level.add(lv);
				child = present.children();
				lev = new Integer(lv + 1);

				for(int i = 0; i < child.length; i++) { 
					if(!child[i].isLeaf()) {
					  queue_value.offer(child[i]);
					  queue_level.offer(lev);
					}
				}
		    }
		}
	} 
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

	    for(int idx = 0; idx < ls.size() ; idx += 2) {
	    	List<HasWord> sentence1 = ls.get(idx);
	    	List<HasWord> sentence2 = ls.get(idx + 1);

	    	Tree t1 = conversionTree(lp, gsf, sentence1);
    		Tree t2 = conversionTree(lp, gsf, sentence2);
    		
    		if(t1.equals(t2)) {
      			System.out.println("Tree1 and Tree 2 Simliarity: 100 % ");
      		}else {
    			BFS bfs1 = new BFS(t1);
    			BFS bfs2 = new BFS(t2);
    			
    			bfs1.traverse();
    			bfs2.traverse();
				ArrayList<String> t1_value = bfs1.getValue();
				ArrayList<String> t2_value = bfs2.getValue();

				ArrayList<Integer> t1_level = bfs1.getLevel();
				ArrayList<Integer> t2_level = bfs2.getLevel();

    			System.out.println("[*] Compare to level for Tree 1 and Tree 2");
    			int equal = 0;
				for(int i = 0; i < t1_value.size(); i++ ) {
					int lv1 = t1_level.get(i);
					for(int j = 0; j < t2_value.size(); j++ ) {
						int lv2 = t2_level.get(j);
						if(lv1 == lv2 || (lv1+1) == lv2 || lv1 == (lv2+1)) {
							if(t1_value.get(i).equals(t2_value.get(j))) {
              					System.out.println(" -  Equal level : "+ lv1 +" result 1 : [" + j + "] :"  + t1_value.get(i) + " result 2 : [" + i +"] :" + t2_value.get(j));
              					equal +=  1;
              					break;
            				}
						}
					}
				}
				int mean_num_node = (t1_value.size() + t2_value.size()) / 2;
				float simliarity = (equal * 100) / mean_num_node;
				System.out.println("Tree1 and Tree 2 Simliarity: "+ simliarity +" % ");
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
}
