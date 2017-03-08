package edu.unh.cs.trec.search;

import java.io.File;
import java.io.IOException;



import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;

import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.util.List;
import java.util.ArrayList;


public class Searcher {

	private IndexSearcher searcher;
	private Analyzer analyzer;
	private DirectoryReader dr;

	public Searcher( File indexdir, Similarity s ) {
		try {
			dr = DirectoryReader.open( FSDirectory.open( indexdir ) );
			searcher = new IndexSearcher(dr);
			searcher.setSimilarity(s);
			analyzer = new StandardAnalyzer();
		} catch( Exception e ) {
			throw new IllegalArgumentException("Those parameters don't seem valid");
		}

	}

	public void setSimilarity( Similarity s ) {
		searcher.setSimilarity(s);
	}

	public void done() {
		try {
			dr.close();
		} catch( Exception e ){}
	}



	public List<SearchResult> search(edu.unh.cs.trec.Query q, String tag) {

		try {
			TopDocs docs = searcher.search(new QueryParser("text", analyzer).parse(q.getQuery()), 500);
			ScoreDoc[] hits = docs.scoreDocs;

			// 4. display results
			ArrayList<SearchResult> results = new ArrayList<SearchResult>();

			for(int i=0;i<hits.length;++i) {
					int docId = hits[i].doc;
					Document d = searcher.doc(docId);

					results.add( new SearchResult(
						q.getQueryID(),
						d.get("paraID"),
						i+1,
						hits[i].score,
						tag
					) );
			}
			return results;
		} catch (Exception e ) {
			System.err.println(e.getStackTrace()[0].toString());
			return new ArrayList<SearchResult>();
		}
	}





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
//      	searcher.setSimilarity(new BM25Similarity());
      	searcher.setSimilarity(new BM25Similarity());
      }else if(modelNum ==2){
      	MySimilarity similarity = new MySimilarity(new DefaultSimilarity());
        searcher.setSimilarity(similarity);
    }


      for(String s :queries){
       	org.apache.lucene.search.Query q = new QueryParser("text", analyzer).parse(s);
        TopDocs docs = searcher.search(q, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;

        // 4. display results
        System.out.printf("\nquery: %s \n",s);
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



}
