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
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
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
        fstream.close();
        // Close the writer
        w.close();
      }
  }

  public EntityDict( String file ) {

    root = new TrieNode();
    data = new HashMap<>();
    try ( Stream<String> dStream = Files.lines( Paths.get(file) ) ) {

      dStream.map(String::trim)
             .filter( l -> l.contains("\t"))
             .map( l -> l.split("\t") )
             .forEach( s -> {
               data.put(s[0].toLowerCase(), s[1]);
             });

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  public EntityResult lookup( String s ) {
    return new EntityResult(data.containsKey( s.toLowerCase() ) ? data.get( s.toLowerCase() ) : null, 0);
  }


  public class EntityResult {
    public String name;
    public int error;
    public EntityResult( String name, int error ) {
      this.name = name;
      this.error = error;
    }

    public String toString() {
      return name + " -> " + error;
    }
  }


  public class TrieNode {

    private HashMap<Character, TrieNode> kids;
    private boolean leaf;
    private String entityName;

    public TrieNode(String base) {
      kids = new HashMap<>();
      entityName = base;
      leaf = true;
    }

    public TrieNode() {
      kids = new HashMap<>();
      leaf = false;
    }


    public void insert( String s, String base) {
      if ( s.length() == 0 ) {
        leaf = true;
        entityName = base;
        return;
      }

      Character c = new Character( s.charAt(0) );

      if ( !kids.containsKey( c ) ) {
        kids.put( c, new TrieNode() );
      }

      kids.get( c ).insert( s.substring(1), base );
    }


    // iterative deepening on allowable edits.
    public EntityResult contains( String s ) {
      String temp;
      for ( int i = 0; i < s.length(); i++) {
        if ( (temp = contains_(s, i)) != null )
          return new EntityResult(temp, i);
      }
      return null;
    }

    private String contains_( String s, int tolrence ) {
      // End of the line case
      if ( s.length() == 0 && tolrence == 0 )
        return leaf ? entityName : null;

      // We can only insert
      if ( s.length() == 0 ) {
        if ( leaf )
          return entityName;
        for ( TrieNode tn : kids.values() ) {
          String temp = tn.contains_( s, tolrence-1 );
          if ( temp != null )
            return temp;
        }
        return null;
      }

      Character c = new Character(s.charAt(0));

      // No changes allowed, must be perfect from here on
      if ( tolrence == 0 ) {
        if ( kids.containsKey( c ) )
          return kids.get( c ).contains_( s.substring(1), tolrence );
        else
          return null;
      }

      // Do we have enough deletions to make this node work
      if ( s.length() <= tolrence && leaf )
        return entityName;

      // No modification case
      if ( kids.containsKey( c ) ) {
        String temp = kids.get( c ).contains_( s.substring(1), tolrence );
        if (temp != null)
          return temp;
      }

      // Delete
      String temp = contains_( s.substring(1), tolrence-1 );
      if ( temp != null ) return temp;

      for ( TrieNode tn : kids.values() ) {
          //Insert
          temp = tn.contains_( s, tolrence-1 );
          if ( temp != null ) return temp;

          // Swap case
          temp = tn.contains_( s.substring(1), tolrence-1 );
          if ( temp != null ) return temp;


        }

        return null;

    }





  }


}
