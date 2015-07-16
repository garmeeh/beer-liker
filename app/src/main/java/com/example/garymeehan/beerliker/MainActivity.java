package com.example.garymeehan.beerliker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {
    //View Elements
    TextView textName;
    ImageView imageView;
    //welcome variables
    private static final String PREFS = "prefs";
    private static final String PREF_NAME = "name";
    //Shared Preferences
    SharedPreferences mSharedPreferences;
    //Simple counter
    Integer beerCount;
    //Store Names of Liked Beers
    List<String> beerList = new ArrayList<>();

    //query string, randomly gets a beer.
    private static final String QUERY_URL = "http://api.brewerydb.com/v2/beer/random/?key=274b296b2dcfa366021a438566bcff44&format=json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Kick off welcome message/dialog
        displayWelcome();
        textName = (TextView)findViewById(R.id.text_name);
        imageView = (ImageView) findViewById(R.id.img_thumbnail);
        //counter used for likes
        beerCount = 0;
        //Kick off app with first beer
        queryBeer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Click listener
    @Override
    public void onClick(View v) {

    }

    public void dislike(View v){
        queryBeer();
    }

    public void like(View v) throws JSONException {

        if(beerCount <11) {
            beerCount++;
            beerList.add(textName.getText().toString());
        }

        if(beerCount == 10){
            Toast.makeText(getApplicationContext(), "Nice! 10 Beers Liked!", Toast.LENGTH_LONG).show();

            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Your Beers!");

            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            builder.setPositiveButton(R.string.cool, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                    dialog.cancel();
                }
            });

            ListView beerDList = new ListView(this);

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_list_item_1,
                    beerList);
            beerDList.setAdapter(arrayAdapter);
            builder.setView(beerDList);
            final Dialog dialog = builder.create();
            dialog.show();
        }
        //Get another beer
        queryBeer();
    }

    private void queryBeer() {
        // Create client
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(QUERY_URL,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(JSONObject jsonObject) {
                        String beerName = null;
                        //show success
//                        Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();

                        JSONObject beerObject;
                        //Get beer object from result
                        beerObject = jsonObject.optJSONObject("data");
                        // See if there is labels in the Object
                        if (beerObject.has("labels")) {
                            JSONObject newLabels = null;
                            try {
                                newLabels = beerObject.getJSONObject("labels");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String iconLabel = null;
                            //get the icon
                            if(newLabels.has("icon")){
                                try {
                                    iconLabel = newLabels.getString("medium");
                                    Log.d("@@@@", iconLabel);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            Picasso.with(getApplicationContext()).load(iconLabel).placeholder(R.drawable.img_beer_loading).into(imageView);
                        } else {
                            // If there is no labels in the object, use a placeholder
                            imageView.setImageResource(R.drawable.img_beer_large);
                        }
                        if (beerObject.has("nameDisplay")) {
                            beerName = beerObject.optString("nameDisplay");
                            textName.setText(beerName);
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Throwable throwable, JSONObject error) {
                        //show failure
                        Toast.makeText(getApplicationContext(), "Error: " + statusCode + " " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                        // Log error message
                        Log.e("Query Error", statusCode + " " + throwable.getMessage());
                    }
                });
    }

    public void displayWelcome() {
        // Access the device's key-value storage
        mSharedPreferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        // Read the user's name,
        // or an empty string if nothing found
        String name = mSharedPreferences.getString(PREF_NAME, "");

        if (name.length() > 0) {
            // If the name is valid, display Toast
            Toast.makeText(this, "Welcome back, " + name + "!", Toast.LENGTH_LONG).show();
        }else {
            // otherwise, show a dialog to ask for their name
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Welcome");
            alert.setMessage("What is your name?");

            // Create EditText for entry
            final EditText input = new EditText(this);
            alert.setView(input);

            // Make an "OK" button to save the name
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {

                    // Grab the EditText's input
                    String inputName = input.getText().toString();
                    // Put it into memory (don't forget to commit!)
                    SharedPreferences.Editor e = mSharedPreferences.edit();
                    e.putString(PREF_NAME, inputName);
                    e.commit();
                    // Welcome the new user
                    Toast.makeText(getApplicationContext(), "Welcome, " + inputName + "!", Toast.LENGTH_LONG).show();
                }
            });
            // Make a "Cancel" button
            // that simply dismisses the alert
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {}
            });
            alert.show();
        }
    }
}
