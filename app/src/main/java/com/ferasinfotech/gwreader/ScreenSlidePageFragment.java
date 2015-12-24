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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.widget.ListView;

import android.view.ViewGroup.LayoutParams;

import android.widget.ImageView;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import com.squareup.picasso.Picasso;

/**
 * The ScreenSlidePageFragment class to represents a single page in the sliding display of pages.
 *
 * It extends a class from the support library so as to be usable on older Android devices.
 *
 * It is instantiated with a single 'story' element from the array of mStories received from the
 * GrassWire API server, and constructs and delivers a 'Bundle' of the story parameters to populate
 * its 'View' with a variety of data from the story.
 *
 * It makes use of the Picasso image loading and caching library to asynchronously load images URLs,
 * and manage a cache of those images.
 *
 */
public class ScreenSlidePageFragment extends android.support.v4.app.Fragment {
    /**
     * The argument key for the data given to this fragment.
     */
    public static final String ARG_PAGE = "page";
    public static final String ARG_TITLE = "title";
    public static final String ARG_HEADLINE = "headline";
    public static final String ARG_SUMMARY = "summary";
    public static final String ARG_COVER_PHOTO = "coverPhoto";
    public static final String ARG_STORY_STRING = "storyString";
    public static final String ARG_STORY_ID = "storyID";

    private static final String TAG_STORY_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_SUMMARY = "summary";
    private static final String TAG_HEADLINE = "headline";
    private static final String TAG_COVER_PHOTO = "coverPhoto";
    private static final String TAG_CREATEDAT = "createdAt";

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
     * The fragment's URL for the cover photo, which is set to the argument value for {@link #ARG_COVER_PHOTO}
     */
    private String mCoverPhoto;

    /**
     * The fragment's Story ID string, expressed as a Int converted from the Story's JSONarray
     */
    private int mStoryID;

    /**
     * The fragment's list of Story JSON string, expressed as a String converted from the Story's JSONarray
     */
    private String mStoryString;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static ScreenSlidePageFragment create(int pageNumber, int numPages, JSONObject storyOrResponse) {
        int    story_id = -1;
        String name = "";
        String summary = "";
        String headline = "";
        String cover_photo_url = "";
        String story_string = "";
        long createdAt;

        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        if (pageNumber == 0) {

            // doing help page.. JSONobject parameter is server reponse

            try {
                 createdAt = storyOrResponse.getLong(TAG_CREATEDAT);
            }
            catch (JSONException e) {
                createdAt = 0;
            }
            story_id = 0;
            name = "Grasswire Help";
            headline = "Usage Instructions";
            cover_photo_url = "android.resource://com.ferasinfotech.gwreader/" + R.drawable.gw_logo;
            summary = "Swipe right and left to read each story.\n\n"
                    + "Scroll down to read facts and associated news items (tweets and links) for each story.\n\n"
                    + "Tap on a news items within a story and you'll be able to follow web links, view tweets via the Twitter app, or watch videos.\n\n"
                    + "A long press on a story's cover photo will launch the device browser to view or edit the story on the Grasswire mobile site.\n\n"
                    + "A long press on the image above will launch the Grasswire main page.\n\n"
                    + "News Feed Date: " + Utility.getDate(createdAt, "MM/dd/yyyy hh:mm") + "\n\n"
                    + "App Version: " + BuildConfig.VERSION_NAME + "\n\n";
        }
        else {

            // doing a story page, JSONobject parameter is the story data

            try {
                story_id = storyOrResponse.getInt(TAG_STORY_ID);
                name = storyOrResponse.getString(TAG_NAME)  + " (" + pageNumber + "/" + numPages + ")";
                summary = storyOrResponse.getString(TAG_SUMMARY);
                headline = storyOrResponse.getString(TAG_HEADLINE);
                cover_photo_url = storyOrResponse.getString(TAG_COVER_PHOTO);
                story_string = storyOrResponse.toString();
            } catch (JSONException e) {
                name = "Unknown";
            }
        }

        args.putInt(ARG_PAGE, pageNumber);
        args.putInt(ARG_STORY_ID, story_id);
        args.putString(ARG_TITLE, name);
        args.putString(ARG_SUMMARY, summary);
        args.putString(ARG_HEADLINE, headline);
        args.putString(ARG_COVER_PHOTO, cover_photo_url);
        args.putString(ARG_STORY_STRING, story_string);
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
        args.putInt(ARG_STORY_ID, 0);
        args.putString(ARG_TITLE, story_title + " (" + (pageNumber + 1) + "/" + numPages + ")");
        args.putString(ARG_SUMMARY, "");
        args.putString(ARG_HEADLINE, "");
        args.putString(ARG_COVER_PHOTO, "");
        args.putString(ARG_STORY_STRING, "");
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
        mStoryID = getArguments().getInt(ARG_STORY_ID);
        mTitle = " " + getArguments().getString(ARG_TITLE) + " ";
        mSummary = getArguments().getString(ARG_SUMMARY);
        mHeadline = getArguments().getString(ARG_HEADLINE);
        mCoverPhoto = getArguments().getString(ARG_COVER_PHOTO);
        mStoryString = getArguments().getString(ARG_STORY_STRING);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View.OnLongClickListener click_listener = new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                String s = "https://www.grasswire.com";

                if (mStoryID != 0) {
                    s = s + "/story/" + mStoryID + "/x";
                }
                i.setData(Uri.parse(s));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

                return true;
            }
        };

        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_screen_slide_page, container, false);


//      Picasso.with(getActivity()).setLoggingEnabled(true);
//      Picasso.with(getActivity()).setIndicatorsEnabled(true);
        ImageView cover_image = (ImageView) rootView.findViewById(R.id.story_image);
        Picasso.with(getActivity()).load(mCoverPhoto).into(cover_image);

        cover_image.setOnLongClickListener(click_listener);


        ((TextView) rootView.findViewById(R.id.story_title)).setText(mTitle);
        ((TextView) rootView.findViewById(R.id.story_headline)).setText(mHeadline);
        ((TextView) rootView.findViewById(R.id.story_summary)).setText(mSummary);

        Log.d("***DEBUG***", "Building page:" + mPageNumber);

        if (mStoryID != 0) {
            LinksAdapter adapter = new LinksAdapter(getActivity(), mStoryString);
            LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.story_layout);
            for (int i = 0; i < adapter.getCount(); i++) {
                View listItem = adapter.getView(i, null, ll);
                ll.addView(listItem);
            }
        }

        return rootView;
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }
}


