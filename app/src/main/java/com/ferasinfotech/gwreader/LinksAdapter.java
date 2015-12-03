package com.ferasinfotech.gwreader;

import android.app.Activity;
import android.content.Context;
import android.widget.BaseAdapter;
import android.view.ViewGroup;
import android.view.View;
import android.view.View.*;
import android.view.LayoutInflater;

import android.content.Context;

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
public class LinksAdapter extends BaseAdapter implements OnClickListener {

    private static final String TAG_LINKS = "links";
    private static final String TAG_LINKDATA = "linkData";
    private static final String TAG_LINKUSER = "user";
    private static final String TAG_LINKUSER_TWITTER_SCREEN_NAME = "twitterScreenName";
    private static final String TAG_LINKUSER_PROFILE_IMAGE_URL = "profileImageUrl";
    private static final String TAG_LINKDATA_TITLE = "title";
    private static final String TAG_LINKDATA_DESCRIPTION = "description";
    private static final String TAG_LINKDATA_THUMBNAIL = "thumbnail";
    private static final String TAG_LINK_TYPE = "type";
    private static final String TAG_LINK_TWEET = "tweet";
    private static final String TAG_TWEET_TEXT = "text";
    private static final String TAG_TWEET_USER = "user";
    private static final String TAG_TWEET_USER_SCREEN_NAME = "screen_name";
    private static final String TAG_TWEET_USER_IMAGE_URL = "profile_image_url";
    private static final String TAG_TWEET_ENTITIES = "entities";
    private static final String TAG_TWEET_MEDIA = "media";
    private static final String TAG_TWEET_MEDIA_URL = "media_url";

    private Activity activity;
    private Context the_context;
    private JSONArray links;
    private static LayoutInflater inflater=null;

    public LinksAdapter(Activity a, String json_story_string) {
        JSONObject jsonObj = null;

        activity = a;
        the_context = activity.getApplicationContext();
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        try {
            jsonObj = new JSONObject(json_story_string);
            links = jsonObj.getJSONArray(TAG_LINKS);
            Log.d("JSON links", "Array length:" + links.length());
        }
        catch (JSONException e) {
            links = null;
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
        return links.length();
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public class ViewHolder {

        public TextView title;
        public TextView description;
        public ImageView image;
        public ImageView profile_image;
        public TextView profile_name;
        public TextView elapsed_time;

        public ViewHolder create(View vi) {
            ViewHolder holder;

            holder = new ViewHolder();
            holder.profile_image = (ImageView) vi.findViewById(R.id.link_profile_image);
            holder.profile_name = (TextView) vi.findViewById(R.id.link_profile_name);
            holder.elapsed_time = (TextView) vi.findViewById(R.id.link_elapsed_time);
            holder.title = (TextView) vi.findViewById(R.id.link_title);
            holder.image = (ImageView) vi.findViewById(R.id.link_image);
            holder.description = (TextView)vi.findViewById(R.id.link_description);

            return holder;
        }
    }

    private void handle_link_parse_exception(JSONException e) {
        Log.d("***DEBUG***", "JSON parse error on links");
    }

    private void do_tweet_link(JSONObject link, ViewHolder holder) {
        String s;
        JSONObject tw;
        JSONObject user;
        JSONObject entities;
        JSONArray medias;
        JSONObject media;

        Log.d("***DEBUG***", "doing tweet link");
        try {
            tw = link.getJSONObject(TAG_LINK_TWEET);
            user = tw.getJSONObject(TAG_TWEET_USER);
            entities = tw.getJSONObject(TAG_TWEET_ENTITIES);
            s = tw.getString(TAG_TWEET_TEXT);
            Log.d("***DEBUG***", "Got tweet text:" + s);
            holder.description.setText(s);
            holder.elapsed_time.setText("Tweet");
            s = user.getString(TAG_TWEET_USER_SCREEN_NAME);
            Log.d("***DEBUG***", "Got tweet username:" + s);
            holder.profile_name.setText("@" + s);
            s = user.getString(TAG_TWEET_USER_IMAGE_URL);
            Log.d("***DEBUG***", "Got tweet profile image url:" + s);
            Picasso.with(the_context).load(s).into(holder.profile_image);
            medias = entities.getJSONArray(TAG_TWEET_MEDIA);
            if (medias.length() > 0) {
                media = medias.getJSONObject(0);
                s = media.getString(TAG_TWEET_MEDIA_URL);
                if (s.length() > 0) {
                    Log.d("***DEBUG***", "Got tweet media url:" + s);
                    Picasso.with(the_context).load(s).into(holder.image);
                }
            }
        }
        catch (JSONException e) {
            handle_link_parse_exception(e);
        }
    }

    private void do_link_user_info(JSONObject link, ViewHolder holder) {
        String s;
        try {
            JSONObject user = link.getJSONObject(TAG_LINKUSER);
            s = user.getString(TAG_LINKUSER_TWITTER_SCREEN_NAME);
            Log.d("***DEBUG***", "Got linkuser screen name:" + s);
            holder.profile_name.setText("@" + s);
            s = user.getString(TAG_LINKUSER_PROFILE_IMAGE_URL);
            Log.d("***DEBUG***", "Got linkuser profile image url:" + s);
            Picasso.with(the_context).load(s).into(holder.profile_image);
        }
        catch (JSONException e) {
            handle_link_parse_exception(e);
        }

    }
    private void do_linkdata_info(JSONObject link, ViewHolder holder, String kind) {
        String s;
        try {
            do_link_user_info(link, holder);
            JSONObject link_data = link.getJSONObject(TAG_LINKDATA);
            s = link_data.getString(TAG_LINKDATA_TITLE);
            Log.d("***DEBUG***", "Got linkdata title:" + s);
            holder.title.setText(s);
            s = link_data.getString(TAG_LINKDATA_DESCRIPTION);
            Log.d("***DEBUG***", "Got linkdata description:" + s);
            holder.description.setText(s);
            holder.elapsed_time.setText(kind);
            s = link_data.getString(TAG_LINKDATA_THUMBNAIL);
            if (s.length() > 0) {
                Log.d("***DEBUG***", "Got linkdata thumbnail url:" + s);
                Picasso.with(the_context).load(s).into(holder.image);
            }
        }
        catch (JSONException e) {
            handle_link_parse_exception(e);
        }
    }

    private void do_plain_link(JSONObject link, ViewHolder holder) {
        Log.d("***DEBUG***", "doing plain link");
        do_linkdata_info(link, holder, "Link");
    }

    private void do_video_link(JSONObject link, ViewHolder holder) {
        Log.d("***DEBUG***", "doing video link");
        do_linkdata_info(link, holder, "video");
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;

        if (vi == null) {
            vi = inflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder().create(vi);
            vi.setTag(holder);
            Log.d("***DEBUG***", "Creating listitem for position:" + position);
        }
        else {
            holder = (ViewHolder) vi.getTag();
            Log.d("***DEBUG***", "Finding listitem for position:" + position);
        }

        if (links.length() == 0) {
            holder.title.setText("");
            holder.description.setText("");
        }
        else {
            String title = "Missing Title";
            String description = "Missing Description";
            try {
                JSONObject link = links.getJSONObject(position);
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

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }

}
