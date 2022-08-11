package com.example.finalproject_newsapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class SavedArticles extends AppCompatActivity {

    ListView saved_feed;

    ArrayList<String> saved_Titles;
    ArrayList<String> saved_Links;
    ArrayList<String> saved_Description;
    ArrayList<String> saved_Date;

    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_articles);

        AlertDialog.Builder alertDIalogBuilder = new AlertDialog.Builder(this);

        saved_feed = (ListView) findViewById(R.id.saved_list);

        saved_Titles = new ArrayList<String>();
        saved_Links = new ArrayList<String>();
        saved_Date = new ArrayList<String>();
        saved_Description = new ArrayList<String>();

        adapter = new ArrayAdapter<String>(SavedArticles.this, android.R.layout.simple_list_item_1, saved_Titles);

        dbOpener dbOpener = new dbOpener(SavedArticles.this);

        Cursor cursor;
        Cursor c = dbOpener.readData();

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

        saved_feed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String delete_question = getString(R.string.alert_dialogue_title);
                String pos_delete_button = getString(R.string.pos_button);
                String neg_delete_button = getString(R.string.neg_button);
                alertDIalogBuilder.setTitle(delete_question);
                alertDIalogBuilder.setPositiveButton(pos_delete_button, (click, arg) -> {

                    saved_Titles.remove(position);
                    adapter.notifyDataSetChanged();

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

        dbOpener.printCursor(c);

    }
}