package edu.unh.cs.trec.entity;


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


public class EntityGraph {

    public  static void buildIndex(String indexdir, String datafile) throws IOException, ParseException {

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
					fstream.close();
	        // Close the writer
	        w.close();
				}
    }
}
