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

public class ParserTreeSimilarity {
	public static class TreeSimilarity {
		private final Tree tree;
		private ArrayList<String> bfs_value;
	    private ArrayList<Integer> bfs_level;
		private ArrayList<String> dfs_value;
	    private ArrayList<Integer> dfs_index;

	    private boolean isPrint;

		public TreeSimilarity(Tree tree) {
			this.tree = tree;
			this.bfs_value = new ArrayList<String>();
			this.bfs_level =  new ArrayList<Integer>();
			this.dfs_value = new ArrayList<String>();
			this.dfs_index =  new ArrayList<Integer>();
			this.isPrint = false;
		}

		public ArrayList<String> getBFSValue() {
			return this.bfs_value;
		}

		public ArrayList<Integer> getBFSLevel() {
			return this.bfs_level;
		}

		public ArrayList<String> getDFSValue() {
			return this.dfs_value;
		}

		public void isPrint(boolean isPrint) {
			this.isPrint = isPrint;
		}
		/* 
			Recurrsive BFS Function.
		   	Breadth-first search (BFS) is an algorithm for traversing or searching tree or graph data structures. 
			It starts at the tree root and explores the neighbor nodes first, before moving to the next level neighbours.
		*/
		public void bfsTraverse(){
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
				if(this.isPrint == true) {
					System.out.println(" - value : " + present.value() + " level : " + lv );
				}
				this.bfs_value.add(present.value());
				this.bfs_level.add(lv);
				child = present.children();
				lev = new Integer(lv + 1);

				for(int i = 0; i < child.length; i++) { 
					queue_value.offer(child[i]);
					queue_level.offer(lev);
				}
		    }
		}

		/*
			DFS Function
			One starts at the root and explores as far as possible along each branch before backtracking.
		*/
		public void dfsTraverse(Tree node, int idx){
			Tree[] child = node.children();
			int len = child.length;
			if(!this.dfs_index.isEmpty()) {
				idx = this.dfs_index.get(this.dfs_index.size()-1);
			}
			idx += 1;
			if(this.isPrint == true) {
				System.out.println(" - value : " + node.value() + " index : " + idx);
			}
			this.dfs_index.add(idx);
			this.dfs_value.add(node.value());
			for(int i = 0; i < len; i++) {
				this.dfsTraverse(child[i], idx+i);
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
  
	    Map<Float, List<String>> hs = new HashMap<Float, List<String>>();
	     
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
    		
    		if(t1.equals(t2)) {
      			System.out.println("Tree1 and Tree 2 Simliarity: 100 % ");
      			float simliarity = 100.0f;
				hs.put(simliarity, sentenceList);
      		}else {
    			TreeSimilarity ts1 = new TreeSimilarity(t1);
    			TreeSimilarity ts2 = new TreeSimilarity(t2);
    			ts1.isPrint(true);
    			ts2.isPrint(true);

    			ArrayList<Integer> t1_dfs = new ArrayList<Integer>(); 
    			ArrayList<Integer> t2_dfs = new ArrayList<Integer>(); 
    			System.out.println("[*] DFS Tree 1");
    			ts1.dfsTraverse(t1, 0);

    			System.out.println("[*] DFS Tree 2");
    			ts2.dfsTraverse(t2, 0);
    			
    			System.out.println("[*] BFS Tree 1");
    			ts1.bfsTraverse();

    			System.out.println("[*] BFS Tree 2");
    			ts2.bfsTraverse();

				ArrayList<String> t1_value = ts1.getBFSValue();
				ArrayList<String> t2_value = ts2.getBFSValue();

				ArrayList<Integer> t1_level = ts1.getBFSLevel();
				ArrayList<Integer> t2_level = ts2.getBFSLevel();

    			System.out.println("\n[*] Compare to level for Tree 1 and Tree 2");

    			/*
    				Level Weight Algorithm
					 1) If both of nodes are same level and position, the weight will be 1.
					 2) If both of nodes are the same level and different position, the weight is 0.9 -> We think about that using BFS Algorithm.
					 3) If both of nodes are the different level, the weight will be 0.45.
    			*/
    			float equal = 0.0f;
    			boolean isEqual = false;
				for(int i = 0; i < t1_value.size(); i++ ) {
					int lv1 = t1_level.get(i);
					for(int j = 0; j < t2_value.size(); j++ ) {
						int lv2 = t2_level.get(j);
						if(lv1 == lv2) {
							if(i == j) {
								if(t1_value.get(i).equals(t2_value.get(j))) {
	              					System.out.println(" -  Equal level : "+ lv1 +" result 1 : [" + j + "] : "  + t1_value.get(i) + " result 2 : [" + i +"] : " + t2_value.get(j));
	              					equal +=  1.0f;
	              					break;
	            				}
            				}else {
            					if(t1_value.get(i).equals(t2_value.get(j))) {
	              					System.out.println(" -  Equal level : "+ lv1 +" result 1 : [" + j + "] : "  + t1_value.get(i) + " result 2 : [" + i +"] : " + t2_value.get(j));
	              					equal +=  0.9f;
	              					break;
	            				}
            				}
						} else if ((lv1+1) == lv2 || lv1 == (lv2+1)) {	
        					if(t1_value.get(i).equals(t2_value.get(j))) {
              					System.out.println(" -  Different level : "+ lv1 +" result 1 : [" + j + "] : "  + t1_value.get(i) + " result 2 : [" + i +"] : " + t2_value.get(j));
              					equal +=  0.45f;
              					break;
            				}
						}
					}
				}

				int mean_num_node = (t1_value.size() + t2_value.size()) / 2;
				int min_num_node = (t1_value.size() > t2_value.size()) ? t2_value.size() : t1_value.size();
				float simliarity = (equal * 100) / min_num_node;
				System.out.println("Tree1 and Tree 2 Simliarity: "+ simliarity +" % ");

				hs.put(simliarity, sentenceList);
      		}
      		
    		System.out.println();
	    }
		int index = 0;
		int top = 10;
	    
	    // Sorting Ascending.
	    Map<Float, List<String>> ascend = new TreeMap<Float, List<String>>(hs);
	    Set set = ascend.entrySet();
	    Iterator iterator = set.iterator();
	    System.out.println("[*] Similiarity Bottom " + top);
		while(iterator.hasNext()) {
			if(index == top) break;
			Map.Entry me = (Map.Entry)iterator.next();
			System.out.print(me.getKey() + ": ");
			System.out.println(me.getValue());
			index += 1;
		}

		// Sorting Descending.
		Map<Float, List<String>> descend = new TreeMap<Float, List<String>>(Collections.reverseOrder());
        descend.putAll(hs);
        Set set2 = descend.entrySet();
	    Iterator iterator2 = set2.iterator();
	    index = 0;
	    System.out.println("[*] Similiarity Top " + top);
		while(iterator2.hasNext()) {
			if(index == top) break;
			Map.Entry me2 = (Map.Entry)iterator2.next();
			System.out.print(me2.getKey() + ": ");
			System.out.println(me2.getValue());
			index += 1;
		}
 	}

 	// Conversion Tree
 	public static Tree conversionTree(LexicalizedParser lp, GrammaticalStructureFactory gsf, List<HasWord> sentence) {
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
}