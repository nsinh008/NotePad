package com.org.book.note;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/*
 * Reference : SQLite tutorials
 * https://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
 */
public class listNote extends AppCompatActivity {

    private ListView obj;
    NoteDB mydb;
    FloatingActionButton btnadd;
    ListView mylist;
    Menu menu;
    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;
    Context context = this;
    CoordinatorLayout coordinatorLayout;
    SimpleCursorAdapter adapter;
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_note);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout1);
        System.out.println("creating db instance");
        mydb = new NoteDB(this);
        System.out.println(" db instance created");
        btnadd = (FloatingActionButton) findViewById(R.id.add_note);
        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle dataBundle = new Bundle();
                dataBundle.putInt("id", 0);
                Intent intent = new Intent(getApplicationContext(),
                        EditNote.class);
                intent.putExtras(dataBundle);
                startActivity(intent);
                finish();
            }
        });

        Cursor c = mydb.fetchAll();

        String[] fieldNames = new String[] { NoteDB.name, NoteDB._id, NoteDB.dates };
        int[] display = new int[] { R.id.txtnamerow, R.id.txtidrow, R.id.txtdate };
        adapter = new SimpleCursorAdapter(this, R.layout.list_design, c, fieldNames,
                display, 0);
        Toast.makeText(listNote.this, "Welcome to NotePad!", Toast.LENGTH_SHORT).show();

        mylist = (ListView) findViewById(R.id.listView1);
        mylist.setAdapter(adapter);
        mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                LinearLayout linearLayoutParent = (LinearLayout) arg1;
                LinearLayout linearLayoutChild = (LinearLayout) linearLayoutParent
                        .getChildAt(0);
                TextView m = (TextView) linearLayoutChild.getChildAt(1);
                Bundle dataBundle = new Bundle();
                dataBundle.putInt("id",
                        Integer.parseInt(m.getText().toString()));
                Intent intent = new Intent(getApplicationContext(),
                        EditNote.class);
                intent.putExtras(dataBundle);
                startActivity(intent);
                finish();
            }
        });
    }

}
