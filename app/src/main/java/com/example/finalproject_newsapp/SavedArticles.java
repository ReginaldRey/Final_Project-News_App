package com.example.finalproject_newsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class SavedArticles extends MainActivity {

    /* Declare necessary variables to display articles that the user has saved, including arraylists with saved
    * data (e.g. titles), a second adapter to load the second ListView, and a new LinearLayout for use with the
    * snackbar. */
    ListView saved_feed;

    ArrayList<String> saved_Titles;
    ArrayList<String> saved_Links;
    ArrayList<String> saved_Description;
    ArrayList<String> saved_Date;

    ArrayAdapter second_adapter;

    LinearLayout window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_articles);

        /* Instantiate new dialog builder, the linearlayout, arraylists, adapter, and database opener in order
        * to be able to retrieve the saved articles from the MYSQL database. */

        AlertDialog.Builder alertDIalogBuilder = new AlertDialog.Builder(this);

        saved_feed = (ListView) findViewById(R.id.saved_list);

        window = findViewById(R.id.window);

        saved_Titles = new ArrayList<String>();
        saved_Links = new ArrayList<String>();
        saved_Date = new ArrayList<String>();
        saved_Description = new ArrayList<String>();

        second_adapter = new ArrayAdapter<String>(SavedArticles.this, android.R.layout.simple_list_item_1, saved_Titles);

        dbOpener dbOpener = new dbOpener(SavedArticles.this);

        /* Create the same toolbar and nav bar as used in the main activity for display on the saved articles
        * layout. */

        Toolbar tBar = findViewById(R.id.toolbar);
        setSupportActionBar(tBar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tBar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /* Create new cursor for use in retrieving the data from the database. */

        Cursor cursor;
        Cursor c = dbOpener.readData();

        /* Using the cursor, retrieve the information from each of the database columns (e.g. title) and
        * load them into variables for storage, then converting those variables to new string variables, loading
        * them into the arraylists for use in the ListView display, then setting the adapter to update the
        * view. */

        cursor = dbOpener.readData();
        int titleColIndex = cursor.getColumnIndex(dbOpener.COL_TITLE);
        int linkColIndex = cursor.getColumnIndex(dbOpener.COL_LINKS);
        int descriptionColIndex = cursor.getColumnIndex(dbOpener.COL_DESCRIPTION);
        int dateColIndex = cursor.getColumnIndex(dbOpener.COL_DATE);
        int idColIndex = cursor.getColumnIndex(dbOpener.COL_ID);

        while(cursor.moveToNext()) {
            String title = cursor.getString(titleColIndex);
            String link = cursor.getString(linkColIndex);
            String description = cursor.getString(descriptionColIndex);
            String date = cursor.getString(dateColIndex);
            long id = cursor.getLong(idColIndex);

            saved_Titles.add(title);
            saved_Links.add(link);
            saved_Description.add(description);
            saved_Date.add(date);
        }

        saved_feed.setAdapter(second_adapter);

        /* Create snackbar message to advise the user that the saved articles have been loaded. */

        Snackbar snackbar = Snackbar.make(window,getString(R.string.snackbar_message),Snackbar.LENGTH_SHORT);
        snackbar.show();

        /* Create click listener for elements in the saved articles to be both opened and an option provided
        * to the user whether to delete them or not. If they are not deleted they are simply opened. */

        saved_feed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String delete_question = getString(R.string.delete_question);
                String pos_delete_button = getString(R.string.pos_button);
                String neg_delete_button = getString(R.string.neg_button);
                alertDIalogBuilder.setTitle(delete_question);
                alertDIalogBuilder.setPositiveButton(pos_delete_button, (click, arg) -> {
                    String article = (saved_Titles.get(position));
                    saved_Titles.remove(position);
                    dbOpener.deleteRow(article);
                    second_adapter.notifyDataSetChanged();

                });
                alertDIalogBuilder.setNegativeButton(neg_delete_button, (click, arg) -> {

                    Uri uri = Uri.parse(saved_Links.get(position));
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);


                });
                alertDIalogBuilder.create();
                alertDIalogBuilder.show();

            }
        });

        /* Print cursor for debugging purposes if necessary. */

        dbOpener.printCursor(c);

    }
}