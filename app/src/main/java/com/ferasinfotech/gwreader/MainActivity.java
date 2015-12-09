package com.ferasinfotech.gwreader;


import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import android.util.DisplayMetrics;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Main activity class for the GWreader app.
 *
 * Queries the GrassWire API server for a JSON structure of current news mStories, descriptive text,
 * image URLs and associated tweets, web links, and video URLs.
 *
 * The app parses the JSON and creates a "screen-slide" animation using a {@link ViewPager}.
 */
public class MainActivity extends FragmentActivity {

    private static final String TAG_STORIES = "stories";
    private static final String TAG_CREATEDAT = "createdAt";

    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private int mNumPages = 0;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * Size of the cover photo in pixel
     */
    private int mScreenHeight;

    JSONArray mStories = null;

    /** Puts up the splash screen and starts the JSON fetch from the GrassWire API server */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenHeight = metrics.heightPixels;

        new DownloadTask().execute("https://api-prod.grasswire.com/v1/digests/current");
    }

    /** Back key handling on sliding pages */
    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    /* Invoked upon reception of JSON data from GrassWire API - creates slideable story page fragments, 1 per story */
    private void build_screens(String json_str) {
        PagerAdapter mPagerAdapter;

        //Toast.makeText(getApplicationContext(), "density:" + logicalDensity, Toast.LENGTH_LONG).show();

        if (json_str != null) {
            try {
                JSONObject jsonObj = new JSONObject(json_str);
                long createdAt = jsonObj.getLong(TAG_CREATEDAT);

                mStories = jsonObj.getJSONArray(TAG_STORIES);

                mNumPages = mStories.length();
                Toast.makeText(getApplicationContext(), "Digest created:" + Utility.getDate(createdAt, "MM/dd/yyyy hh:mm"),
                        Toast.LENGTH_LONG).show();

                //Toast.makeText(getApplicationContext(), "cover size:" + mCoverPhotoSize, Toast.LENGTH_LONG).show();

                // Set the View, then Instantiate a ViewPager and a PagerAdapter.
                setContentView(R.layout.activity_screen_slide);
                mPager = (ViewPager) findViewById(R.id.pager);
                mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
                mPager.setAdapter(mPagerAdapter);
                mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // When changing pages, reset the action bar actions since they are dependent
                        // on which page is currently active. An alternative approach is to have each
                        // fragment expose actions itself (rather than the activity exposing actions),
                        // but for simplicity, the activity provides the actions in this sample.
                        // Toast.makeText(getApplicationContext(), "Page " + position, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch  (JSONException e) {
                Toast.makeText(getApplicationContext(), "JSON parsing exception", Toast.LENGTH_LONG).show();
            }
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
            try {
                return ScreenSlidePageFragment.create(position, mNumPages, mScreenHeight,
                        mStories.getJSONObject(position));
            }
            catch  (JSONException e) {
                return ScreenSlidePageFragment.create(position, mNumPages, mScreenHeight,
                        "JSON parsing problem");
            }
        }

        @Override
        public int getCount() {
            return mNumPages;
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
        protected void onPostExecute(String json_result) {
            //Toast.makeText(getApplicationContext(), "Starting to build screens from JSON data", Toast.LENGTH_SHORT).show();
            build_screens(json_result);
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
