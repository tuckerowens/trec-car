package edu.unh.cs.trec.search;

 public class SearchResult implements Comparable {
   private String entity;
   private String passage;
   private int rank;
   private double sim;
   private String runID;
   private String query;

   public SearchResult( String query, String passage, int rank, double sim, String tag ) {
     if (query == null ||
         passage == null ||
         tag == null)
         throw new IllegalArgumentException("Fields can't be null");
     this.entity = entity;
     this.passage = passage;
     this.rank = rank;
     this.sim = sim;
     this.runID = tag;
     this.query = query;
   }

   public int compareTo(SearchResult r) {
     return r.rank - rank;
   }

   public int compareTo(Object o) {
     if ( o instanceof SearchResult )
       return rank - ((SearchResult)o).rank;
     return 0; //why not...
   }

   public String toString() {
     return query + " Q0 " + passage + " " + rank + " " + sim + " " + runID;
   }

 }
