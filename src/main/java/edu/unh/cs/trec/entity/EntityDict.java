package edu.unh.cs.trec.entity;


import java.util.HashMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.Arrays;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class EntityDict {


  public static void buildIndex(String indexdir, String datafile) throws IOException, ParseException {

      Analyzer analyzer = new StandardAnalyzer();
      FSDirectory directory = FSDirectory.open(new File(indexdir));
      IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_1, analyzer);
      config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
      config.setRAMBufferSizeMB(43);
      config.setUseCompoundFile(false);
      final IndexWriter w = new IndexWriter(directory, config);


      try ( Stream<String> dStream = Files.lines(Paths.get(datafile)) ) {
        dStream.map( String::trim )
               .filter( l -> l.contains("\t") )
               .map( l -> l.split("\t") )
               .forEach( s -> {
                 Document doc = new Document();
                 doc.add(new StringField("surface_name", s[0], Field.Store.YES));
                 doc.add(new StringField("entity", s[1], Field.Store.YES));
                 try { w.addDocument(doc); } catch(Exception e ){}
               });

      } catch (Exception e){
      } finally {

        // Close the writer
        w.close();
      }
  }


  private IndexSearcher searcher;
	private Analyzer analyzer;
	private DirectoryReader dr;

  public EntityDict( String indexdir ) {

    try {
			dr = DirectoryReader.open( FSDirectory.open( new File(indexdir) ) );
			searcher = new IndexSearcher(dr);
		} catch( Exception e ) {
			throw new IllegalArgumentException("Those parameters don't seem valid");
		}


  }

  public EntityResult lookup( String s ) {
    Term t = new Term("surface_name", s);
    Query q = new FuzzyQuery(t);
    try {
      TopDocs td = searcher.search(q, 1);
      for ( ScoreDoc sc : td.scoreDocs  ) {
        Document d = searcher.doc( sc.doc );
        return new EntityResult( d.get( "entity" ), sc.score );
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return null;
  }


  public class EntityResult {
    public String name;
    public float error;
    public EntityResult( String name, float error ) {
      this.name = name;
      this.error = error;
    }

    public String toString() {
      return name + " -> " + error;
    }
  }



}
