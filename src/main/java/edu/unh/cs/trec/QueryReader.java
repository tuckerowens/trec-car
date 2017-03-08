package edu.unh.cs.trec;


import edu.unh.cs.trec.cbor.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;


public class QueryReader {


  public static List<Query> getQueries_( Data.Section section ) {
    if ( section.getChildSections().size() == 0 ) {
      ArrayList out = new ArrayList<Query>();
      out.add( new Query(section.getHeading(), section.getHeadingId()) );
      return out;
    }

    ArrayList<Query> out = new ArrayList<>();
    for (Data.Section s : section.getChildSections()) {
      for ( Query q : getQueries_(s) ) {
        out.add( new Query( section.getHeading() + " " + q.getQuery(),
                            section.getHeadingId() + "/" + q.getQueryID()) );
      }
    }
    return out;
  }

  public static List<Query> getQueries( String outline ) {

    ArrayList<Query> out = new ArrayList<>();

    try {
      FileInputStream fstream = new FileInputStream( new File(outline) );
      for (Data.Page p : DeserializeData.iterableAnnotations(fstream)) {
          for (Data.Section s : p.getChildSections()) {
            for (Query q : getQueries_(s)) {
              out.add( new Query( p.getPageName() + " " + q.getQuery(),
                                  p.getPageId() + "/" + q.getQueryID())) ;
            }
          }
      }
    } catch (Exception e) {}
    return out;
  }


}
