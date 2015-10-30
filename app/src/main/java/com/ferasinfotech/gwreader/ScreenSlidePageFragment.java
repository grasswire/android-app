/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ferasinfotech.gwreader;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;
import java.net.MalformedURLException;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy title indicating
 * the page number, along with some dummy text.
 *
 */
public class ScreenSlidePageFragment extends android.support.v4.app.Fragment {
    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";
    public static final String ARG_TITLE = "title";
    public static final String ARG_HEADLINE = "headline";
    public static final String ARG_SUMMARY = "summary";
    public static final String ARG_COVER_PHOTO = "coverPhoto";

    private static final String TAG_NAME = "name";
    private static final String TAG_SUMMARY = "summary";
    private static final String TAG_HEADLINE = "headline";
    private static final String TAG_COVER_PHOTO = "coverPhoto";

    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;

    /**
     * The fragments article title, which is set to the argument value for {@link #ARG_TITLE}
     */
    private String mTitle;

    /**
     * The fragments article summary, which is set to the argument value for {@link #ARG_SUMMARY}
     */
    private String mSummary;

    /**
     * The fragments article headline, which is set to the argument value for {@link #ARG_HEADLINE}
     */
    private String mHeadline;

    /**
     * The fragment's LinearLayout, saved so the async task can set it's background after fetching
     * the image.
     */
    private RelativeLayout lay;

    /**
     * The fragment's URL for the cover photo, which is set to the argument value for {@link #ARG_COVER_PHOTO}
     */
    private String mCoverPhoto;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static ScreenSlidePageFragment create(int pageNumber, int numPages, JSONObject story) {
        String name = "";
        String summary = "";
        String headline = "";
        String cover_photo_url = "";

        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        try {
            name = story.getString(TAG_NAME);
            summary = story.getString(TAG_SUMMARY);
            headline = story.getString(TAG_HEADLINE);
            cover_photo_url = story.getString(TAG_COVER_PHOTO);
        }
        catch  (JSONException e) {
            name = "Unknown";
        }

        args.putInt(ARG_PAGE, pageNumber);
        args.putString(ARG_TITLE, name + " (" + (pageNumber + 1) + "/" + numPages + ")");
        args.putString(ARG_SUMMARY, summary);
        args.putString(ARG_HEADLINE, headline);
        args.putString(ARG_COVER_PHOTO, cover_photo_url);
        fragment.setArguments(args);
        return fragment;
    }
    /**
     * Alternate Factory method for this fragment class. Constructs a new fragment for the given page number,
     *  with page title given as a string parameter without a JSON object containing details.
     *  (used to construct and empty page when a JSON parsing error of a story occurs)
     */
    public static ScreenSlidePageFragment create(int pageNumber, int numPages, String story_title) {

        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        args.putString(ARG_TITLE, story_title + " (" + (pageNumber + 1) + "/" + numPages + ")");
        args.putString(ARG_SUMMARY, "");
        args.putString(ARG_HEADLINE, "");
        args.putString(ARG_COVER_PHOTO, "");
        fragment.setArguments(args);
        return fragment;
    }

    public ScreenSlidePageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
        mTitle = " " + getArguments().getString(ARG_TITLE) + " ";
        mSummary = getArguments().getString(ARG_SUMMARY);
        mHeadline = getArguments().getString(ARG_HEADLINE);
        mCoverPhoto = getArguments().getString(ARG_COVER_PHOTO);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_screen_slide_page, container, false);

        // Get the linear layout and then start the background task to fetch an image for it
        lay = ((RelativeLayout) rootView.findViewById(R.id.story_layout));
        new LoadBackground(mCoverPhoto, "androidfigure").execute();

        // Set the title view to show the article title.
        ((TextView) rootView.findViewById(R.id.story_title)).setText(mTitle);

        // Set the headline view to show the article headline.
        ((TextView) rootView.findViewById(R.id.story_headline)).setText(mHeadline);

        // Set the summary view to show the article title.
        ((TextView) rootView.findViewById(R.id.story_summary)).setText(mSummary);

        Log.d("Building page: ", " " + mPageNumber);

        return rootView;
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }

    private class LoadBackground extends AsyncTask<String, Void, Drawable> {

        private String imageUrl , imageName;

        public LoadBackground(String url, String file_name) {
            this.imageUrl = url;
            this.imageName = file_name;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Drawable doInBackground(String... urls) {

            try {
                InputStream is = (InputStream) this.fetch(this.imageUrl);
                Drawable d = Drawable.createFromStream(is, this.imageName);
                return d;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        private Object fetch(String address) throws MalformedURLException,IOException {
            URL url = new URL(address);
            Object content = url.getContent();
            return content;
        }

        @SuppressWarnings("deprecation")
        @Override
        protected void onPostExecute(Drawable result) {
            super.onPostExecute(result);
            lay.setBackgroundDrawable(result);
        }
    }
}
