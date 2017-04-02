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

public class DBPediaEntry {

  private String uri;

  public DBPediaEntry(String uri) {
    this.uri = uri;

    // try {
    //
    //   URL url = new URL(uri);
    //   HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    //   conn.setDoOutput(true);
    //   conn.setRequestMethod("GET");
    //   conn.setRequestProperty("Accept", "application/json");
    //
    //
    //   BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    //
    //   String output;
    //   StringBuilder sb = new StringBuilder();
    //   while ((output = br.readLine()) != null) {
    //     sb.append(output);
    //   }
    //   conn.disconnect();
    //
    //   System.err.println(sb.toString());
    // } catch (Exception e) {
    //     System.err.println(e);
    // }
  }







}
