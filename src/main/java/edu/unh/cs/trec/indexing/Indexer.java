package edu.unh.cs.trec.indexing;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

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

import edu.unh.cs.trec.cbor.*;

public class Indexer {

    public  static void buildIndex(String indexdir, String datafile) throws IOException, ParseException {
        // 0. Specify the analyzer for tokenizing text.
        //    The same analyzer should be used for indexing and searching
         //0 is for tfidf, 1 is for BM25
    		Analyzer analyzer = new StandardAnalyzer();

        // 1. create the index
        FSDirectory directory = FSDirectory.open(new File(indexdir));

        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_1, analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        config.setRAMBufferSizeMB(64);
        config.setMaxBufferedDocs(4000);
        IndexWriter w = new IndexWriter(directory, config);



        //2.read the data file
        FileInputStream fstream = new FileInputStream(datafile);

				try {

	        for (Data.Paragraph p : DeserializeData.iterableParagraphs( fstream )) {
						Document doc = new Document();

		        doc.add(new TextField("text", p.getTextOnly(), Field.Store.YES));
		        doc.add(new StringField("paraID", p.getParaId(), Field.Store.YES));
		        doc.add(new StringField("entities", String.join(" ", p.getEntitiesOnly()), Field.Store.YES));
		        w.addDocument(doc);
					}
				} catch (Exception e){
				} finally {
					fstream.close();
	        // Close the writer
	        w.close();
				}
    }
}
