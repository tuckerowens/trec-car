package edu.unh.cs.trec.entity;

import java.nio.file.Files;
import java.nio.file.Paths;

public class EntityHelper {

  public static EntityDict entityDict;
  public static EntityGraph entityGraph;

  private static final String KB_INDEX_PATH = "/Users/Tucker/Desktop/tuckerowens/knowledgebasegraph";
  private static final String DICT_INDEX_PATH = "/Users/Tucker/Desktop/tuckerowens/knowledgebasedict";

  static {
    try {
      if (Files.notExists(Paths.get("kbGraph_index"))) {
        System.err.println("KB Index not found, building");
        EntityGraph.buildIndex( "kbGraph_index", KB_INDEX_PATH );
        System.err.println("KB Index built");
      }
    } catch(Exception e ) {
      throw new RuntimeException(e);
    }

    try {
      if ( Files.notExists(Paths.get("kbDict_index")) ) {
        System.err.println("Entity Dict Index not found, building");
        EntityDict.buildIndex("kbDict_index", DICT_INDEX_PATH);
        System.err.println("Entity Dict Index built");
      }
    } catch( Exception e) {
      throw new RuntimeException(e);
    }

    entityDict = new EntityDict("kbDict_index");
    entityGraph = new EntityGraph("kbGraph_index");

  }

}
