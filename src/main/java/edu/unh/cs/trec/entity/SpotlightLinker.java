package edu.unh.cs.trec.entity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.*;
import java.lang.StringBuilder;


public class SpotlightLinker {


  public static List<DBPediaEntry> getEntities(String input) {

    try {

      String uri = "http://model.dbpedia-spotlight.org/en/annotate?text=" + URLEncoder.encode(input);

   		URL url = new URL(uri);
   		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
   		conn.setDoOutput(true);
   		conn.setRequestMethod("GET");
      conn.setRequestProperty("Accept", "application/json");


   		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

   		String output;
      StringBuilder sb = new StringBuilder();
   		while ((output = br.readLine()) != null) {
   			sb.append(output);
   		}
   		conn.disconnect();


      JSONObject response = new JSONObject( sb.toString() );

      ArrayList<DBPediaEntry> results = new ArrayList<>();
      for ( Object obj : response.getJSONArray("Resources") ) {
        JSONObject entry = (JSONObject) obj;
        results.add( new DBPediaEntry( entry.optString("@URI") ) );
      }

      return results;

   	  } catch (MalformedURLException e) {

   		return new ArrayList<DBPediaEntry>();

   	  } catch (IOException e) {

   		return new ArrayList<DBPediaEntry>();

    } catch( Exception e ) {
      return new ArrayList<DBPediaEntry>();
    }

  }

}
