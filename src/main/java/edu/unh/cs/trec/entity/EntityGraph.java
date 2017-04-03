package edu.unh.cs.trec.entity;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

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


public class EntityGraph {

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
                   doc.add(new StringField("node", s[0], Field.Store.YES));
                   doc.add(new TextField("edges", String.join("\t", Arrays.copyOfRange(s, 1, s.length)), Field.Store.YES));
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

    public EntityGraph( String indexdir ) {

      try {
        dr = DirectoryReader.open( FSDirectory.open( new File(indexdir) ) );
        searcher = new IndexSearcher(dr);
      } catch( Exception e ) {
        throw new IllegalArgumentException("Those parameters don't seem valid");
      }


    }

    public KBNode lookup( String s ) {
      Term t = new Term("node", s);
      Query q = new FuzzyQuery(t);
      try {
        TopDocs td = searcher.search(q, 1);
        for ( ScoreDoc sc : td.scoreDocs  ) {
          Document d = searcher.doc( sc.doc );
          return new KBNode( d.get( "node" ), Arrays.asList(d.get("edges").split("\t")), this );
        }
      } catch (Exception e){
        throw new RuntimeException(e);
      }
      return null;
    }


    public class KBNode {
      public String name;
      private EntityGraph parent;
      private List<String> edges;

      public KBNode( String name, List<String> edges, EntityGraph parent) {
        this.name = name;
        this.edges = edges;
        this.parent = parent;
      }

      public List<KBNode> getNeighborhood() {
          return edges.stream()
                      .map( e -> EntityGraph.this.lookup(e) )
                      .collect( Collectors.toList() );
      }

    }



}
