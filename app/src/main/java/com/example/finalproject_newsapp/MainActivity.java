package com.example.finalproject_newsapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.AsynchronousChannelGroup;
import java.sql.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView feed;
    ArrayList<String> titles;
    ArrayList<String> links;
    ArrayList<String> description;
    ArrayList<String> date;

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        feed = (ListView) findViewById(R.id.list);

        titles = new ArrayList<String>();
        links = new ArrayList<String>();
        date = new ArrayList<String>();
        description = new ArrayList<String>();

        adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, titles);

        AlertDialog.Builder alertDIalogBuilder = new AlertDialog.Builder(this);

        dbOpener dbOpener = new dbOpener(MainActivity.this);

        feed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String title = getString(R.string.alert_dialogue_title);
                String pos_button = getString(R.string.pos_button);
                String neg_button = getString(R.string.neg_button);
                alertDIalogBuilder.setTitle(title);
                alertDIalogBuilder.setPositiveButton(pos_button, (click, arg) -> {
                    String article_title = (titles.get(position));
                    String article_link = (links.get(position));
                    String article_description = (description.get(position));
                    String article_date = (date.get(position));
                    dbOpener.addItem(article_title, article_link, article_description, article_date);

                    Uri uri = Uri.parse(links.get(position));
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);

                });
                alertDIalogBuilder.setNegativeButton(neg_button, (click, arg) -> {

                    Uri uri = Uri.parse(links.get(position));
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);

                });
                alertDIalogBuilder.create();
                alertDIalogBuilder.show();

            }
        });

        HTTPRequest req = new HTTPRequest();
        req.execute();

    }

    public InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private class HTTPRequest extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... args) {

            try {
                URL url = new URL("https://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream response = urlConnection.getInputStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser parser = factory.newPullParser();

                parser.setInput(getInputStream(url), "UTF_8");

                boolean insideItem = false;

                int eventType = parser.getEventType();

                while(eventType != XmlPullParser.END_DOCUMENT) {
                    if(eventType == XmlPullParser.START_TAG) {
                        if(parser.getName().equalsIgnoreCase("item")) {
                            insideItem = true;
                        } else if(parser.getName().equalsIgnoreCase("title")) {
                            if(insideItem == true) {
                                titles.add(parser.nextText());

                            }
                        } else if(parser.getName().equalsIgnoreCase("pubDate")) {
                            if(insideItem == true) {
                                date.add(parser.nextText());
                            }
                        } else if(parser.getName().equalsIgnoreCase("description")) {
                            if(insideItem == true) {
                                description.add(parser.nextText());
                            }
                        } else if(parser.getName().equalsIgnoreCase("link")) {
                            if(insideItem == true) {
                                links.add((parser.nextText()));
                            }
                        }

                    } else if(eventType == XmlPullParser.END_TAG && parser.getName().equalsIgnoreCase("item")) {
                        insideItem = false;
                    }

                    eventType = parser.next();
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("Error occured: ", String.valueOf(e));
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            feed.setAdapter(adapter);
        }

    }
}