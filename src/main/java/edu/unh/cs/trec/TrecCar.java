package edu.unh.cs.trec;

import edu.unh.cs.trec.indexing.Indexer;
import edu.unh.cs.trec.search.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import java.io.File;

import java.util.stream.*;

/**
 * TrecCar.java - The main class
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
        boolean bm25 = false; //  using BM25 similarities

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
            search = new Searcher( new File(indexDir), new DefaultSimilarity() );
        } else if(bm25) {
            search = new Searcher(new File(indexDir), new BM25Similarity());
        } else {
            search = new Searcher(new File(indexDir), new MySimilarity(new DefaultSimilarity()));
        }

        final String tag = baseline ? "baseline" : "test";

        QueryReader.getQueries( datafile )
                .parallelStream()
                .forEach( q -> {
                    System.err.println(q.toString());
                    search.search(q, tag)
                            .stream()
                            .forEach(sr -> System.out.println(sr.toString()) );
                });

    }

}
