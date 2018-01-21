package com.example.android.alarm;

import android.os.AsyncTask;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.io.*;


public class ReadRSS {

    public void run() {
        new Retrieve().execute("http://rss.cnn.com/rss/edition.rss");
    }

    class Retrieve extends AsyncTask<String, Void, Void> {

        protected Void doInBackground(String... urls) {
            try{
                URL rssUrl = new URL (urls[0]);
                BufferedReader in = new BufferedReader(new InputStreamReader(rssUrl.openStream()));
                String sourceCode = "";
                String line;
                while ((line = in.readLine()) != null) {
                    int titleEndIndex = 0;
                    int titleStartIndex = 0;
                    while (titleStartIndex >= 0) {
                        titleStartIndex = line.indexOf("<title><![CDATA[", titleEndIndex);
                        if (titleStartIndex >= 0) {
                            titleEndIndex = line.indexOf("]]></title>", titleStartIndex);
                            sourceCode += line.substring(titleStartIndex + "<title><![CDATA[".length(), titleEndIndex) + "\n";
                        }
                    }
                }
                in.close();
                Log.i("name", sourceCode);
            } catch (MalformedURLException ue){
                System.out.println("Malformed URL");
            } catch (IOException ioe){
                System.out.println("Something went wrong reading the contents");
            }
            return null;
        }
    }

}