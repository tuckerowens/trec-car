package edu.unh.cs.trec;


public class Query {

  private String query;
  private String queryID;

  public Query( String query, String queryID ) {
    this.query = query;
    this.queryID = queryID;
  }


  public String getQuery() { return query; }

  public String getQueryID() { return queryID; }

  public String toString() {
    return "{ query=" + query + ",\n" +
            "queryid=" + queryID + "}\n";
  }

}
