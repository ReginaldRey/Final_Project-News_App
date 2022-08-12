package com.example.finalproject_newsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.AsynchronousChannelGroup;
import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
/* Declares necessary variables for the ListView feed used to display articles, as well as various ArrayLists which will
* be used to store data from the RSS feed. Additional declarations include the refresh button, sharedpreferences,
* and the ArrayAdapter */
    ListView feed;
    ArrayList<String> titles;
    ArrayList<String> links;
    ArrayList<String> description;
    ArrayList<String> date;

    Button refresh;

    ArrayAdapter<String> adapter;

    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Instantiating all necessary variables including a date object used for sharedpreferences,
        * sharedpreferences object itself, the ListView, the arraylists mentioned above, the button, the adapter,
        * */

        SimpleDateFormat simpleDate =  new SimpleDateFormat("dd/MM/yyyy");
        String strDt = simpleDate.toString();

        prefs = getSharedPreferences("FileName", Context.MODE_PRIVATE);

        feed = (ListView) findViewById(R.id.list);

        titles = new ArrayList<String>();
        links = new ArrayList<String>();
        date = new ArrayList<String>();
        description = new ArrayList<String>();

        refresh = findViewById(R.id.refresh);

        adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, titles);

        /* Execute AsyncTask to retrieve data from the BBC rss feed */

        HTTPRequest req = new HTTPRequest();
        req.execute();

        /* Create a sharedpreferences editor, and save the date object to it. */

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Date", strDt);
        editor.commit();

        /* Create an alertdialog, database opener, toolbar, navigation drawer, and apply them all
        * to the main activity layout */

        AlertDialog.Builder alertDIalogBuilder = new AlertDialog.Builder(this);

        dbOpener dbOpener = new dbOpener(MainActivity.this);

        Toolbar tBar = findViewById(R.id.toolbar);
        setSupportActionBar(tBar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tBar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /* Set on click listener on refresh button to execute a new AsyncTask object whenever clicked.
        * Added a toast message to advise user when page is properly refreshed. */

        refresh.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new HTTPRequest().execute();
                Toast.makeText(MainActivity.this,getString(R.string.toast_message) + strDt,Toast.LENGTH_SHORT).show();
            }
        });

        /* Set onItemClickListener to give the user the option to save an article when selecting it from
        * the main activity page. */

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

    }

    /* Create function InputStream to open a URL connection to the BBC rss feed. */

    public InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /* Create options menu functions to implement functionality to selecting the menu icons. Intents
    * are used to travel from one activity to another. */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.home:
                Intent intent2 = new Intent(MainActivity.this, MainActivity.class);
                startActivityForResult(intent2, 0);
                break;
            case R.id.saved:
                Intent intent = new Intent(MainActivity.this, SavedArticles.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.exit:
                finishAffinity();
                break;

        }
        return true;
    }

    /* Create navigation menu functions to do the same as above for the nav menu. */

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.Saved_articles:
                Intent intent = new Intent(MainActivity.this, SavedArticles.class);
                startActivityForResult(intent, 0);

                break;
            case R.id.Home:
                Intent intent2 = new Intent(MainActivity.this, MainActivity.class);
                startActivityForResult(intent2, 0);
                break;
            case R.id.exit:
                finishAffinity();

        }
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        return false;
    }

    /* Create AsyncTask class to perform http communications during doInBackground as well as
    * setting the adapter when background functions are completed to update with new ListView items.
    * Additionally implement parsing method to parse rss feed and upload relevant information (e.g. title)
    * to ArrayList to be used later when populating the ListView with the adapter. */

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