package com.example.turgo;

import android.widget.Toast;

import com.example.turgo.fragments.PlacesFragment;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;

public class ScrapperApplication extends Thread {
    String url = "https://en.wikipedia.org/wiki/";

    public ScrapperApplication(String url) {
        this.url += url;
    }

    @Override
    public void run() {
        super.run();
        try {
            final Document document = Jsoup.connect(url).get();
            Element body = document.getElementById("mw-content-text");
            String paragraphs = body.getElementsByTag("p").toString();
            Boolean delete = false;
            for (int i = 0; i<paragraphs.length(); ++i) {
                if (paragraphs.charAt(i)=='<') delete = true;
                if (paragraphs.charAt(i)=='>')  {
                    delete = false;
                    paragraphs = paragraphs.substring(0,i)+"~"+paragraphs.substring(i+1);

                }
                if ( delete ) paragraphs = paragraphs.substring(0,i)+"~"+paragraphs.substring(i+1);
            }
            paragraphs = paragraphs.replace("~", "");
            PlacesFragment.summary = paragraphs.substring(0, paragraphs.indexOf("."));
            PlacesFragment.newSummary = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
