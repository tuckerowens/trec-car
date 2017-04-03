package edu.unh.cs.trec;


import edu.unh.cs.trec.cbor.*;
import java.util.ArrayList;
import java.util.List;
import edu.unh.cs.trec.entity.*;
import java.io.*;
import net.sf.extjwnl.data.IndexWordSet;
import net.sf.extjwnl.dictionary.Dictionary;

public class QueryReader {


  private static Dictionary dict;


  static {
    try {
        System.err.println("Loading WordNet Dictionary");
        dict = Dictionary.getDefaultResourceInstance();
        System.err.println("WordNet Dictionary Loaded");
    } catch (Exception e) {
        dict = null;
    }

  }

  public static List<Query> getQueries_( Data.Section section) {
    if ( section.getChildSections().size() == 0 ) {
      IndexWordSet iws = null;
      try {
        iws = dict.lookupAllIndexWords(section.getHeading());
      } catch ( Exception e){}
      ArrayList out = new ArrayList<Query>();
      out.add( new Query(section.getHeading(), section.getHeadingId(), iws));
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

          // List<DBPediaEntry> dbes = SpotlightLinker.getEntities(p.getPageName());
          DBPediaEntry dbe = null; //dbes.size() > 0 ? dbes.get(0) : null;
          for (Data.Section s : p.getChildSections()) {
            for (Query q : getQueries_(s)) {
              out.add( new Query( p.getPageName() + " " + q.getQuery(),
                                  p.getPageId() + "/" + q.getQueryID(), dbe)) ;
            }
          }
      }
    } catch (Exception e) {}
      out.stream().parallel().forEach(q -> q.expandQuery());
    return out;
  }


}
