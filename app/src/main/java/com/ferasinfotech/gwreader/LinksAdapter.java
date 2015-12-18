package com.ferasinfotech.gwreader;

import android.app.Activity;
import android.content.Context;
import android.widget.BaseAdapter;
import android.view.ViewGroup;
import android.view.View;
import android.view.View.*;
import android.view.LayoutInflater;

import android.content.Intent;
import android.net.Uri;

import android.widget.TextView;
import android.widget.ImageView;

import android.util.Log;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * LinksAdapter class delivers "story link" related data for the ListView of associated links on
 * each story page.
 *
 * It is instantiated with a single JSON 'link' element from the array of links in a story received from
 * the GrassWire API server, and it parses that link to construct an appropriate list item.  The
 * link could be a tweet, a plain link, or a video link.
 */
public class LinksAdapter extends BaseAdapter /* implements OnClickListener */ {

    // tag string constants for decoding JSON data

    private static final String TAG_LINKS = "links";
    private static final String TAG_LINKDATA = "linkData";
    private static final String TAG_LINKUSER = "user";
    private static final String TAG_LINKUSER_TWITTER_SCREEN_NAME = "twitterScreenName";
    private static final String TAG_LINKUSER_PROFILE_IMAGE_URL = "profileImageUrl";
    private static final String TAG_LINKDATA_TITLE = "title";
    private static final String TAG_LINKDATA_DESCRIPTION = "description";
    private static final String TAG_LINKDATA_THUMBNAIL = "thumbnail";
    private static final String TAG_LINKDATA_URL = "url";
    private static final String TAG_LINK_TYPE = "type";
    private static final String TAG_LINK_TWEET = "tweet";
    private static final String TAG_TWEET_ID_STR = "id_str";
    private static final String TAG_TWEET_TEXT = "text";
    private static final String TAG_TWEET_USER = "user";
    private static final String TAG_TWEET_USER_SCREEN_NAME = "screen_name";
    private static final String TAG_TWEET_USER_PROFILE_IMAGE_URL_HTTPS = "profile_image_url_https";
    private static final String TAG_TWEET_ENTITIES = "entities";
    private static final String TAG_TWEET_MEDIA = "media";
    private static final String TAG_TWEET_MEDIA_URL = "media_url";

    // Unicode string constants that map to FONT AWESOME symbols

    String PLAIN_LINK = "\uf0c1";
    String VIDEO_LINK = "\uf03d";
    String TWEET_LINK = "\uf099";

    String PLAY_ICON = "\uf04b";


    // Private data to the Links Adapter class

    private Activity  mActivity;
    private Context   mContext;
    private JSONArray mLinks;
    private static    LayoutInflater sInflator = null;

    public LinksAdapter(Activity a, String json_story_string) {
        JSONObject jsonObj = null;

        mActivity = a;
        mContext = mActivity.getApplicationContext();
        sInflator = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        try {
            jsonObj = new JSONObject(json_story_string);
            mLinks = jsonObj.getJSONArray(TAG_LINKS);
            Log.d("JSON links", "Array length:" + mLinks.length());
        }
        catch (JSONException e) {
            mLinks = null;
            Log.d("JSON links", "JSON parse failed");
        }
    }

    public long getItemId(int position) {
        return position;
    }

    public Object getItem(int position) {
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isEmpty() {
        return true;
    }

    public int getCount() {
        return mLinks.length();
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public class ViewHolder {

        public TextView title;
        public TextView description;
        public TextView link_type;
        public ImageView image;
        public ImageView profile_image;
        public TextView profile_name;
        public TextView elapsed_time;
        public String target_url;
        public TextView play_icon;

        public ViewHolder create(View vi) {
            ViewHolder holder;

            holder = new ViewHolder();
            holder.profile_image = (ImageView) vi.findViewById(R.id.link_profile_image);
            holder.profile_name = (TextView) vi.findViewById(R.id.link_profile_name);
            holder.link_type = (TextView) vi.findViewById(R.id.link_type);
            holder.elapsed_time = (TextView) vi.findViewById(R.id.link_elapsed_time);
            holder.title = (TextView) vi.findViewById(R.id.link_title);
            holder.image = (ImageView) vi.findViewById(R.id.link_image);
            holder.description = (TextView) vi.findViewById(R.id.link_description);
            holder.play_icon = (TextView)vi.findViewById(R.id.link_image_play_icon);

            holder.link_type.setTypeface(FontManager.getTypeface(mContext, FontManager.FONTAWESOME));
            holder.play_icon.setTypeface(FontManager.getTypeface(mContext, FontManager.FONTAWESOME));
            holder.target_url = "";
            holder.elapsed_time.setText("");
            holder.play_icon.setText("");
            return holder;
        }
    }

    private void handle_link_parse_exception(JSONException e) {
        Log.d("***DEBUG***", "JSON parse error on links");
    }

    private void do_link_user_info(JSONObject link, ViewHolder holder, String link_kind) {
        String s;
        try {
            JSONObject user = link.getJSONObject(TAG_LINKUSER);
            s = user.getString(TAG_LINKUSER_TWITTER_SCREEN_NAME);
            holder.profile_name.setText("@" + s);
            holder.link_type.setText(link_kind);
            s = user.getString(TAG_LINKUSER_PROFILE_IMAGE_URL);
            Picasso.with(mContext).load(s).transform(new CircleTransform()).into(holder.profile_image);

        }
        catch (JSONException e) {
            handle_link_parse_exception(e);
        }

    }

    private void do_tweet_link(JSONObject link, ViewHolder holder) {
        String     s;
        String     tweet_id;
        JSONObject tw;
        JSONObject user;
        JSONObject entities;
        JSONArray  medias;
        JSONObject media;

        Log.d("***DEBUG***", "doing tweet link");
        try {
            Boolean no_image = true;

            tw = link.getJSONObject(TAG_LINK_TWEET);
            entities = tw.getJSONObject(TAG_TWEET_ENTITIES);
            s = tw.getString(TAG_TWEET_TEXT);
            holder.description.setText(s);
            medias = entities.getJSONArray(TAG_TWEET_MEDIA);
            if (medias.length() > 0) {
                media = medias.getJSONObject(0);
                s = media.getString(TAG_TWEET_MEDIA_URL);
                if (s.length() > 0) {
                    Picasso.with(mContext).load(s).into(holder.image);
                    no_image = false;
                }
            }

            if (no_image) {
                ViewGroup.LayoutParams params = holder.image.getLayoutParams();
                params.height = 0;
            }

            tweet_id = tw.getString(TAG_TWEET_ID_STR);
            user = tw.getJSONObject(TAG_TWEET_USER);
            s = user.getString(TAG_TWEET_USER_SCREEN_NAME);
            holder.target_url = "https://twitter.com/" + s + "/status/" + tweet_id;
            Log.d("***DEBUG***", "set target url:" + holder.target_url);
            holder.profile_name.setText("@" + s);
            holder.link_type.setText(TWEET_LINK);
            s = user.getString(TAG_TWEET_USER_PROFILE_IMAGE_URL_HTTPS);
            Picasso.with(mContext).load(s).transform(new CircleTransform()).into(holder.profile_image);

        }
        catch (JSONException e) {
            handle_link_parse_exception(e);
        }
    }

    private void do_linkdata_info(JSONObject link, ViewHolder holder, String kind) {
        String s;
        try {
            Boolean no_image = true;

            do_link_user_info(link, holder, kind);
            JSONObject link_data = link.getJSONObject(TAG_LINKDATA);
            s = link_data.getString(TAG_LINKDATA_TITLE);
            holder.title.setText(s);
            s = link_data.getString(TAG_LINKDATA_DESCRIPTION);
            holder.description.setText(s);
            holder.link_type.setText(kind);
            holder.elapsed_time.setText("");
            s = link_data.getString(TAG_LINKDATA_THUMBNAIL);
            if (s.length() > 0) {
                Picasso.with(mContext).load(s).into(holder.image);
                no_image = false;
                if (kind == VIDEO_LINK) {
                    holder.play_icon.setText(PLAY_ICON);
                }
            }
            if (no_image) {
                ViewGroup.LayoutParams params = holder.image.getLayoutParams();
                params.height = 0;
            }
            holder.target_url = link_data.getString(TAG_LINKDATA_URL);
            Log.d("***DEBUG***", "set target url:" + holder.target_url);
        }
        catch (JSONException e) {
            handle_link_parse_exception(e);
        }
    }

    private void do_plain_link(JSONObject link, ViewHolder holder) {
        Log.d("***DEBUG***", "doing plain link");
        do_linkdata_info(link, holder, PLAIN_LINK);
    }

    private void do_video_link(JSONObject link, ViewHolder holder) {
        Log.d("***DEBUG***", "doing video link");
        do_linkdata_info(link, holder, VIDEO_LINK);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;

        OnClickListener click_listener = new OnClickListener() {
            public void onClick(View v) {
                ViewHolder holder = (ViewHolder) v.getTag();
                String s = holder.target_url;

                if (s.length() > 0) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(s));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(i);
                    Log.d("***DEBUG***", "Launching activity");
                }
                else {
                    Log.d("***DEBUG***", "Row clicked but no launch");
                }
            }
        };

        if (vi == null) {
            vi = sInflator.inflate(R.layout.list_item, null);
            holder = new ViewHolder().create(vi);
            vi.setTag(holder);
            vi.setOnClickListener(click_listener);
        }
        else {
            holder = (ViewHolder) vi.getTag();
        }

        if (mLinks.length() == 0) {
            holder.title.setText("");
            holder.description.setText("");
        }
        else {
            String title = "Missing Title";
            String description = "Missing Description";
            try {
                JSONObject link = mLinks.getJSONObject(position);
                String link_type = link.getString(TAG_LINK_TYPE);
                if (link_type.contains("TweetLinkJsonModel")) {
                    do_tweet_link(link, holder);
                }
                else if (link_type.contains("PlainLinkJsonModel")) {
                    do_plain_link(link, holder);
                }
                else if (link_type.contains("VideoLinkJsonModel")) {
                    do_video_link(link, holder);
                }
            }
            catch (JSONException e) {
                handle_link_parse_exception(e);
            }
        }
        return vi;
    }
}
