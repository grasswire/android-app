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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private static final String TAG_NAME = "name";

    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;

    /**
     * The fragments article title, which is set to the argument value for {@link #ARG_TITLE}
     */
    private String mTitle;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static ScreenSlidePageFragment create(int pageNumber, JSONObject story) {
        String name = "";

        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        try {
            name = story.getString(TAG_NAME);
        }
        catch  (JSONException e) {
            name = "Unknown";
        }

        args.putInt(ARG_PAGE, pageNumber);
        args.putString(ARG_TITLE, name);
        fragment.setArguments(args);
        return fragment;
    }
    /**
     * Alternate Factory method for this fragment class. Constructs a new fragment for the given page number,
     *  with page title given as a string parameter without a JSON object containing details.
     *  (used to construct and empty page when a JSON parsing error of a story occurs)
     */
    public static ScreenSlidePageFragment create(int pageNumber, String story_title) {

        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        args.putString(ARG_TITLE, story_title + "(" + pageNumber + ")");
        fragment.setArguments(args);
        return fragment;
    }

    public ScreenSlidePageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
        mTitle = getArguments().getString(ARG_TITLE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_screen_slide_page, container, false);

        // Set the title view to show the article title.
        ((TextView) rootView.findViewById(android.R.id.text1)).setText(mTitle);

        Log.d("Building page: ", " " + mPageNumber);

        return rootView;
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }
}
