package com.vimalroxy.booklistroxy;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Create an empty ArrayList that we can start adding earthquakes to
    private static ArrayList<Book> bookList = new ArrayList<>();

    //Log Tag for Messages
    private static final String TAG = "MainActivity";

    //Original URL
    private static final String URL_ORIGINAL = "https://www.googleapis.com/books/v1/volumes?q=";

    Adapter booksAdapter;
    String searchURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isNetworkConnected()) {

            final LinearLayout noResult = (LinearLayout) findViewById(R.id.empty_list);
            noResult.setVisibility(View.VISIBLE);

            final Button button = (Button) findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    noResult.setVisibility(View.GONE);

                    //Clear Old List
                    bookList.clear();

                    //Get Text From SearchField
                    EditText search = (EditText) findViewById(R.id.search_field);
                    String searchString = search.getText().toString();

                    //Concatinate String To Search
                    searchURL = URL_ORIGINAL + urlEncode(searchString);
                    onSearch();
                }
            });
        }else{
            LinearLayout noNetwork = (LinearLayout) findViewById(R.id.no_connection);
            noNetwork.setVisibility(View.VISIBLE);
        }
    }

    // Check all connectivities whether available or not
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public static String urlEncode(String original) {
        try {
            //return URLEncoder.encode(original, "utf-8");
            //fixed: to comply with RFC-3986
            return URLEncoder.encode(original, "utf-8");
        } catch (UnsupportedEncodingException e) {
            //  Logger.e(e.toString());
        }
        return null;
    }

    private void onSearch() {

        booksAsyncTask booksAsyncTask = new booksAsyncTask();
        booksAsyncTask.execute();
    }

    private class booksAsyncTask extends AsyncTask<String, Void, ArrayList<Book>> {
        @Override
        protected ArrayList<Book> doInBackground(String... params) {

            // Create URL object
            URL url = createUrl(searchURL);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(TAG, "doInBackground: ERROR ", e);
            }
            bookList = extractBooksJSON(jsonResponse);
            return bookList;
        }

        @Override
        protected void onPostExecute(ArrayList<Book> bookArrayList) {
            super.onPostExecute(bookArrayList);
            updateUi();
        }
    }

    private void updateUi() {

        if (bookList.isEmpty()) {
            final LinearLayout noResult = (LinearLayout) findViewById(R.id.empty_list);
            noResult.setVisibility(View.VISIBLE);
        } else {
            booksAdapter = new Adapter(getApplicationContext(), bookList);
            ListView listView = (ListView) findViewById(R.id.list);
            listView.setAdapter(booksAdapter);
        }
    }
    private URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    private String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {

                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(TAG, "ERROR Response Code" + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(TAG, "Problem with IO Exception", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static ArrayList<Book> extractBooksJSON(String booksJSON) {

        if (TextUtils.isEmpty(booksJSON)) {
            return null;
        }

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            JSONObject root = new JSONObject(booksJSON);
            JSONArray items = root.getJSONArray("items");

            for (int i = 0; i < items.length(); i++) {
                JSONObject books = items.getJSONObject(i);
                JSONObject volumeInfo = books.getJSONObject("volumeInfo");

                // ----
                // TITLE
                // ----
                String title = volumeInfo.getString("title");

                // ----
                // AUTHORS
                // ----
                JSONArray bookAuthors = null;
                try {
                    bookAuthors = volumeInfo.getJSONArray("authors");
                } catch (JSONException ignored) {
                }
                //Convert Authors to String
                String bookAuthorsString = "";
                //Unknown if Empty
                if (bookAuthors == null) {
                    bookAuthorsString = "Unknown";
                } else {
                    int countAuthors = bookAuthors.length();
                    for (int a = 0; a < countAuthors; a++) {
                        String author = bookAuthors.getString(a);
                        if (bookAuthorsString.isEmpty()) {
                            bookAuthorsString = author;
                        } else if (a == countAuthors - 1) {
                            bookAuthorsString = bookAuthorsString + " and " + author;
                        } else {
                            bookAuthorsString = bookAuthorsString + ", " + author;
                        }
                    }
                }

                // ----
                // Rating
                // ----

                String rating = null;

                try {
                    rating = volumeInfo.getString("averageRating");
                } catch (JSONException ignored) {
                }

                String ratingString = "";

                if (rating == null) {
                    ratingString = "Na";
                } else {
                    ratingString = rating;
                }
                // Adding to Array
                bookList.add(new Book(title, bookAuthorsString, ratingString));
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }
        return bookList;
    }

}