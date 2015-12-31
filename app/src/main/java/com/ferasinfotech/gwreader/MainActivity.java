package com.ferasinfotech.gwreader;


import android.os.AsyncTask;
import android.renderscript.Element;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


/**
 * Main activity class for the GWreader app.
 *
 * Queries the GrassWire API server for a JSON structure of current news mJsonStories, descriptive text,
 * image URLs and associated tweets, web links, and video URLs.
 *
 * The app parses the JSON and creates a "screen-slide" animation using a {@link ViewPager}.
 */
public class MainActivity extends FragmentActivity {

    private static final boolean DOING_JSON = false;

    private static final String TAG_STORIES = "stories";

    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private int mNumPages = 0;

    /**
     *
     * and next wizard steps.
     */
    private ViewPager mPager;        // pager widget handles animation and swiping horizontally

    JSONObject mJsonResponse = null; // JSON representation of web server response
    JSONArray mJsonStories = null;   // array of story elements parsed from JSON response

    Document mHtmlResponse = null;   // HTML document returned from web server
    Elements mHtmlStories = null;    // HTML story elements parse from the HTML response

    /** Puts up the splash screen and starts the JSON fetch from the GrassWire API server */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        if (DOING_JSON) {
            new DownloadTask().execute("https://api-prod.grasswire.com/v1/digests/current");
        }
        else {
            new DownloadTask().execute("https://www.grasswire.com");
        }
    }

    /** Back key handling on sliding pages */
    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() <= 1) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    /* Invoked upon reception of JSON data from GrassWire API - creates slideable story page fragments, 1 per story */
    private void build_screens_from_json(String json_str) {
        PagerAdapter mPagerAdapter;

        if (json_str != null) {
            try {
                mJsonResponse = new JSONObject(json_str);
                mJsonStories = mJsonResponse.getJSONArray(TAG_STORIES);

                mNumPages = mJsonStories.length();
                Toast.makeText(getApplicationContext(), "Swipe right for more stories, left for Help",
                        Toast.LENGTH_LONG).show();
                setContentView(R.layout.activity_screen_slide);
                mPager = (ViewPager) findViewById(R.id.pager);
                mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
                mPager.setAdapter(mPagerAdapter);
                mPager.setCurrentItem(1);
            }
            catch  (JSONException e) {
                Toast.makeText(getApplicationContext(), "JSON parsing exception", Toast.LENGTH_LONG).show();
            }
        }
    }

    /* Invoked upon reception of HTML data from GrassWire web site - creates slideable story page fragments, 1 per story */
    private void build_screens_from_html(String html_str) {
        PagerAdapter mPagerAdapter;

        if (html_str != null) {
            org.jsoup.nodes.Element body;
            org.jsoup.nodes.Element main;
            Elements element_list;

            mHtmlResponse = Jsoup.parse(html_str);
            body = mHtmlResponse.body();
            element_list = body.getElementsByClass("content-container");
            main = element_list.get(0);
            mHtmlStories = main.getElementsByClass("story__list");
            mNumPages = mHtmlStories.size();
            Toast.makeText(getApplicationContext(), "Swipe right for more stories, left for Help",
                    Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_screen_slide);
            mPager = (ViewPager) findViewById(R.id.pager);
            mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
            mPager.setAdapter(mPagerAdapter);
            mPager.setCurrentItem(1);
        }
    }

    /**
     * A simple pager adapter that represents 5 {@link ScreenSlidePageFragment} objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (DOING_JSON) {
                try {
                    if (position == 0) {
                        return ScreenSlidePageFragment.create(position, mNumPages, mJsonResponse);
                    } else {
                        return ScreenSlidePageFragment.create(position, mNumPages, mJsonStories.getJSONObject(position - 1));
                    }
                } catch (JSONException e) {
                    return ScreenSlidePageFragment.create(position, mNumPages, "JSON parsing problem");
                }
            }
            else {
                if (position == 0) {
                    return ScreenSlidePageFragment.create(position, mNumPages, mHtmlStories.get(0));
                }
                else {
                    return ScreenSlidePageFragment.create(position, mNumPages, mHtmlStories.get(position - 1));
                }

            }
        }

        @Override
        public int getCount() {
            return mNumPages+1;
        }
    }

    /**
     * Implementation of AsyncTask, to fetch the data in the background away from
     * the UI thread.
     */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return loadFromNetwork(urls[0]);
            }
            catch (IOException e) {
                return getString(R.string.connection_error);
            }
        }

        @Override
        protected void onPostExecute(String web_result) {
            if (DOING_JSON) {
                build_screens_from_json(web_result);
            }
            else {
                build_screens_from_html(web_result);
            }
        }

    }

    /** Initiates the fetch operation. */
    private String loadFromNetwork(String urlString) throws IOException {
        InputStream stream = null;
        String str = "";

        try {
            stream = downloadUrl(urlString);
            str = readIt(stream);
        }
        finally {
            if (stream != null) {
                stream.close();
            }
        }
        return str;
    }

    /**
     * Given a string representation of a URL, sets up a connection and gets
     * an input stream.
     * @param urlString A string representation of a URL.
     * @return An InputStream retrieved from a successful HttpURLConnection.
     * @throws java.io.IOException
     */
    private InputStream downloadUrl(String urlString) throws IOException {
        // BEGIN_INCLUDE(get_inputstream)
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        //   conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        //   conn.setRequestProperty("Data-Type", "json");
        conn.setDoInput(true);
        // Start the query
        conn.connect();
        return conn.getInputStream();
        // END_INCLUDE(get_inputstream)
    }

    /** Reads an InputStream and converts it to a String.
     * @param stream InputStream containing HTML from targeted site.
     * @return String concatenated according to len parameter.
     * @throws java.io.IOException
     * @throws java.io.UnsupportedEncodingException
     */
    private String readIt(InputStream stream) throws IOException {
        char[] buffer = new char[1000];
        StringBuilder s = new StringBuilder();
        int bytes_read;
        Reader reader = new InputStreamReader(stream, "UTF-8");
        while ((bytes_read = reader.read(buffer)) >= 0) {
            s.append(new String(buffer, 0, bytes_read));
        }
        return s.toString();
    }
}
