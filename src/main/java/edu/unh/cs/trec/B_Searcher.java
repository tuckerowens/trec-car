package edu.uh.cs.trec;

import java.io.File;
import java.io.IOException;

import edu.unh.cs.trec.B_MySimilarity;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


public class B_Searcher {

    public static void searchEngine(String[] queries) throws IOException, ParseException{
        Analyzer analyzer = new StandardAnalyzer();

        int modelNum = 1;
//      Query q = new QueryParser("Content", analyzer).parse(querystr);

        // 3. search
        int hitsPerPage = 10;
        String indexPath = "indexfile/";
        Directory directory = FSDirectory.open(new File (indexPath));
        DirectoryReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        if(modelNum == 1){
            searcher.setSimilarity(new BM25Similarity());
        }else if(modelNum ==2){
            B_MySimilarity similarity = new B_MySimilarity(new DefaultSimilarity());
            searcher.setSimilarity(similarity);
        }


        for(String s :queries){
            Query q = new QueryParser("text", analyzer).parse(s);
            TopDocs docs = searcher.search(q, hitsPerPage);
            ScoreDoc[] hits = docs.scoreDocs;

            // 4. display results
            System.out.println("query: " + s);
            System.out.println("Found " + hits.length + " hits.");
            for(int i=0;i<hits.length;++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                System.out.println((i + 1) + ". "+ hits[i]+" "+ d.get("text"));
            }

        }

        // reader can only be closed when there
        // is no need to access the documents any more.
        reader.close();
    }


    public static void main(String[] arg) throws IOException, ParseException{

        String[] queries={ "Green sea turtles","shrimp","Sweden","owl"};
        searchEngine(queries);
    }


}
