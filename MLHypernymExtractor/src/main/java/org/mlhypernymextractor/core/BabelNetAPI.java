package org.mlhypernymextractor.core;

import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelSynset;
import it.uniroma1.lcl.babelnet.BabelSynsetIDRelation;
import it.uniroma1.lcl.babelnet.data.BabelPOS;
import it.uniroma1.lcl.babelnet.data.BabelPointer;
import it.uniroma1.lcl.jlt.util.Language;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.ListUtils;



public class BabelNetAPI {

	   BabelNet bn;
	   public BabelNetAPI(){
		   bn = BabelNet.getInstance();
	   }
	   
	  
	  /**
	   * 
	   * @param H
	   * @return
	   */
	  public List<String> getSynsets(String H) {
		     // Recupere les synsets du terme H 
		     List<BabelSynset> synsets = bn.getSynsets(H, Language.FR);
		     ArrayList<String> ids = new ArrayList<String>();
		     for (BabelSynset s : synsets) {
		    	  ids.add(s.getId().getID());
		     }
		     return ids;
	   }

	  /**
	   * 
	   * @param h
	   * @param rang
	   * @return
	   */
	  public List<List<String>> getSuperHyperonyms(String h, int rang, List<String> synsetH) {
		   // Recupere les synsets du terme 
		   List<BabelSynset> synsets = bn.getSynsets(h, Language.FR, BabelPOS.NOUN);
		   List <List<String>> paths = new ArrayList<List<String>>();
		   int n = 0;
		   for (BabelSynset synset : synsets) {
			    System.out.println("Synset " + ++n);
			    retrieveSuper(synset, paths, new ArrayList<String>(),rang, synsetH);
		   }
		   
		   for (List<String> list : paths) {     
		    	System.out.println("Path --------------------------");
		        for (String terme : list) {
		            System.out.println(terme);
		        }
		        System.out.println();
		    }
		    return paths;	 
	   }
		
	  /**
	   *  
	   * @param synset
	   * @param paths 
	   * @param rang
	   * @param synsetH 
	   * @return
	   */
      private void retrieveSuper(BabelSynset synset, List<List<String>> paths, ArrayList<String> path, int rang, List<String> synsetH) {
    	      path.add(synset.getId().getID());
    	      // Recupere les hyperonymes du synset
    	      List<BabelSynsetIDRelation> edges = synset.getEdges(BabelPointer.ANY_HYPERNYM);
    	    
    	      // Arrete dès que intersection non vide
    	      if (!(ListUtils.intersection(path, synsetH).isEmpty())) {
    	    	  paths.add(new ArrayList<String>(path));
    	    	  return;
    	      }
    	      
    	      // Ne cherche plus haut si plus d'hyper ou rang atteint  
    	      if (edges.isEmpty() || path.size() == rang) {
    	    	  //System.out.println("Added path " + path.toString());
    	    	  paths.add(new ArrayList<String>(path));
    	      } else {
    	          // On ne cherche plus de path des qu'il existe une intersection 
  	        	//  if (ListUtils.intersection(path, synsetH).isEmpty()) {
    	    	      for (BabelSynsetIDRelation edge : edges) {
    	    	    	   BabelSynset synsetHyperHypo = bn.getSynset(Arrays.asList(Language.FR),edge.getBabelSynsetIDTarget());
    	    	    	   //System.out.println("Hyper " + synsetHyperHypo.getId().getID());
    	                   retrieveSuper(synsetHyperHypo,paths,path,rang,synsetH);
    	              }
    	    	 // }
    	      }
    	      //System.out.println("Current path " + path.toString());
    	      //System.out.println("Remove " + synset.getId().getID());
    	      path.remove(synset.getId().getID());
    	      //System.out.println("Current path " + path.toString());
    	      
     }
}