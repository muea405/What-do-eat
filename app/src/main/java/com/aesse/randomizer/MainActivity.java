package com.aesse.randomizer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Context context;
    private TextView selection;
    String location = "";
    ArrayList<String> home = new ArrayList<String>();
    ArrayList<String> out = new ArrayList<String>();
    ArrayList<String> any = new ArrayList<String>();
    Random r;
    private static final String TAG = "MainActivity";
    SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        selection = (TextView) findViewById(R.id.selection);

        //Import saved lists
        prefs = getPreferences(MODE_PRIVATE);
        Gson gson1 = new Gson();
        String json1 = prefs.getString("Home", "");
        try {
            home.addAll(Arrays.asList(gson1.fromJson(json1, String[].class)));
        }
        catch (NullPointerException e) {
            Log.d(TAG, "List Home is empty");
        }

        Gson gson2 = new Gson();
        String json2 = prefs.getString("Out", "");
        try {
            out.addAll(Arrays.asList(gson2.fromJson(json2, String[].class)));
        }
        catch (NullPointerException e) {
            Log.d(TAG, "List Out is empty");
        }


        String[] places = {"Home", "Out", "Anything"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Stay home or go out?");
        builder.setItems(places, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { //don't allow click out
                switch (which) {
                    case 0:
                        location = "home";
                        if (home.size() == 0)
                            selection.setText("Enter some options");
                        else {
                            r = new Random();
                            String select = home.get(r.nextInt(home.size()));
                            selection.setText(select);
                        }
                        Log.d(TAG, "Location = " + location);
                        break;
                    case 1:
                        location = "out";
                        if (out.size() == 0)
                            selection.setText("Enter some options");
                        else {
                            r = new Random();
                            String select = out.get(r.nextInt(out.size()));
                            selection.setText(select);
                        }
                        Log.d(TAG, "Location = " + location);
                        break;
                    case 2:
                        location = "any";
                        any.addAll(home);
                        any.addAll(out);
                        if (any.size() == 0)
                            selection.setText("Enter some options");
                        else {
                            r = new Random();
                            String select = any.get(r.nextInt(any.size()));
                            selection.setText(select);
                        }
                        Log.d(TAG, "Location = " + location);
                        break;
                }
            }
        });
        builder.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_one) {
            addItem();
        }

        if (id == R.id.action_two) { //ToDo Add delete function
            Toast.makeText(MainActivity.this, "Coming soon, live with your mistakes for now (or delete data)", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void addItem() {
        if (location =="any"){
            Toast.makeText(MainActivity.this, "Please enter in either 'Home' or 'Out'", Toast.LENGTH_SHORT).show(); //ToDO add ability to add to either list
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Item");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { //ToDo Don't let this be empty (it crashes)
                if (location == "home") {
                    home.add(input.getText().toString());
                    saveArray("Home", home);
                    Log.d(TAG, "User entry: " + input.getText().toString());
                }
                else if (location == "out") {
                    out.add(input.getText().toString());
                    saveArray("Out", out);
                    Log.d(TAG, "User entry: " + input.getText().toString());
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void saveArray(String key, ArrayList<String> array) {
        SharedPreferences.Editor prefsEditor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(array);
        prefsEditor.putString(key, json);
        prefsEditor.commit();
    }
}