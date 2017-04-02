package edu.unh.cs.trec;

import edu.unh.cs.trec.entity.*;
import java.lang.StringBuilder;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Random;

public class Query {

  private String query;
  private String queryID;
  private DBPediaEntry dbpe;
  private net.sf.extjwnl.data.IndexWordSet leafWordSet;

  private static Random rand = new Random();

  public Query( String query, String queryID, DBPediaEntry entry) {
    this.query = query;
    this.queryID = queryID;
    this.dbpe = entry;
  }

  public Query( String query, String queryID) {
    this.query = query;
    this.queryID = queryID;
  }

  public Query( String query, String queryID, net.sf.extjwnl.data.IndexWordSet wordSet) {
    this.query = query;
    this.queryID = queryID;

    HashSet<String> terms = new HashSet<String>();

    StringBuilder sb = new StringBuilder();
    terms.add(query);

    for (net.sf.extjwnl.data.IndexWord word : wordSet.getIndexWordCollection()) {
      for ( net.sf.extjwnl.data.Synset syn : word.getSenses() ) {
        for (net.sf.extjwnl.data.Word w : syn.getWords()) {
          terms.add( w.getLemma().toLowerCase() );
        }
      }
    }

    for (String s : terms) {
      if ( rand.nextDouble() < 0.2 ) {
        sb.append(s); sb.append(" ");
      }
    }

    this.query = sb.toString();
    //System.err.println(this.query);

  }


  public void expandQuery( EntityDict edict ) {
    String parts[] = query.split("\\s+");
    // for ( int i = 0; i < parts.length - 3; i ++) {
    //   StringBuilder sb = new StringBuilder(parts[i]);
    //   EntityDict.EntityResult unigram = edict.lookup(sb.toString());
    //   sb.append(" " + parts[i+1]);
    //   EntityDict.EntityResult bigram = edict.lookup(sb.toString());
    //   sb.append(" " + parts[i+2]);
    //   EntityDict.EntityResult trigram = edict.lookup(sb.toString());
    //
    //   if ( trigram.name != null ) {
    //     parts[i] = trigram.name;
    //     parts[i+1] = "";
    //     parts[i+2] = "";
    //     i += 2;
    //   } else if ( bigram.name != null ) {
    //     parts[i] = bigram.name;
    //     parts[i+1] = "";
    //     i ++;
    //   } else if ( unigram.name != null ) {
    //     parts[i] = unigram.name;
    //   }
    // }

    query = String.join( " ", parts ).replaceAll("[\\(\\)\\\\/#]|:\\w+:", "");
    System.err.println( query );
  }

  public String getQuery() { return query; }

  public String getQueryID() { return queryID; }

  public String toString() {
    return "{ query=" + query + ",\n" +
            "queryid=" + queryID + "}\n";
  }

}
