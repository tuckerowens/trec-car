package edu.unh.cs.trec;

import edu.unh.cs.trec.indexing.Indexer;
import edu.unh.cs.trec.search.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import java.io.File;

/**
 * Hello world!
 *
 */
public class TrecCar {




    public static void main( String[] args ) {

      if ( args.length < 2 ) {
        System.out.println("Basic usage: trec-car <indexdir> <outline>");
      }

      boolean index = false;
      String indexDir = args[0];
      String datafile = args[1];


      boolean baseline = false;

      for (int i = 0; i < args.length; i++) {
          if ( args[i].equals("--index") ) {
            i++;
            if ( args.length < i+2 ){
              System.out.println("Index mode args: <indexdir> <datafile>" );
              System.exit(0);
            }
            indexDir = args[i++];
            datafile = args[i];
            index = true;
          }

          if (args[i].equals("--baseline")) {
            baseline = true; i++;
            indexDir = args[i++];
            datafile = args[i];
          }

        }

        if ( index ) {
          System.out.println("Indexing Paragraphs");
          try {
            Indexer.buildIndex(indexDir, datafile);
          } catch (Exception e) {
            System.out.println("Indexing Failed");
          }
          System.exit(0);
        }


        Searcher search;

        if (baseline) {
          search = new Searcher( new File(indexDir), new BM25Similarity() );
        } else {
          search = new Searcher(new File(indexDir), new MySimilarity(new DefaultSimilarity()));
        }
        for ( Query q : QueryReader.getQueries(datafile)) {
          for ( SearchResult sr : search.search(q, baseline ? "baseline" : "test")) {
            System.out.println(sr.toString());
          }
        }


    }



}
