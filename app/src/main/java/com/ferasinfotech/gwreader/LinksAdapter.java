package com.ferasinfotech.gwreader;

import android.app.Activity;
import android.widget.BaseAdapter;
import android.view.ViewGroup;
import android.view.View;
import android.view.View.*;
import android.view.LayoutInflater;

import android.content.Context;

import android.widget.TextView;
import android.widget.ImageView;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by jferas on 11/21/15.
 */
public class LinksAdapter extends BaseAdapter implements OnClickListener
{

    private static final String TAG_LINKS = "links";
    private static final String TAG_LINK_DATA = "linkData";
    private static final String TAG_LINK_TITLE = "title";
    private static final String TAG_LINK_DESCRIPTION = "description";
    private static final String TAG_LINK_TYPE = "type";
    private static final String TAG_LINK_TWEET = "tweet";
    private static final String TAG_TWEET_TEXT = "text";

    private Activity activity;
    private JSONArray links;
    private static LayoutInflater inflater=null;

    public LinksAdapter(Activity a, String json_story_string)
    {
        JSONObject jsonObj = null;

        activity = a;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        try
        {
            jsonObj = new JSONObject(json_story_string);
            links = jsonObj.getJSONArray(TAG_LINKS);
            Log.d("JSON links", "Array length:" + links.length());
        }
        catch (JSONException e)
        {
            links = null;
            Log.d("JSON links", "JSON parse failed");
        }
    }

    public long getItemId(int position)
    {
        return position;
    }

    public Object getItem(int position)
    {
        return position;
    }

    public boolean hasStableIds()
    {
        return true;
    }

    public boolean isEmpty()
    {
        return true;
    }

    public int getCount()
    {
        return links.length();
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public class ViewHolder{

        public TextView title;
        public TextView description;
        public ImageView image;
        public ImageView profile_image;
        public TextView profile_name;
        public TextView elapsed_time;

        public ViewHolder create(View vi)
        {
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

    private void handle_link_parse_exception()
    {
        Log.d("***DEBUG***", "JSON parse error on links");
    }

    private void do_tweet_link(JSONObject link, ViewHolder holder)
    {
        try
        {
            JSONObject tw = link.getJSONObject(TAG_LINK_TWEET);
            holder.description.setText(tw.getString(TAG_TWEET_TEXT));
        }
        catch (JSONException e)
        {
            handle_link_parse_exception();
        }
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        View vi = convertView;
        ViewHolder holder;

        if (vi == null)
        {
            vi = inflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder().create(vi);
            vi.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) vi.getTag();
        }

        if (links.length() == 0)
        {
            holder.title.setText("");
            holder.description.setText("");
        }
        else
        {
            String title = "Missing Title";
            String description = "Missing Description";
            try
            {
                JSONObject link = links.getJSONObject(position);
                String link_type = link.getString(TAG_LINK_TYPE);
                if (link_type.contains("TweetLinkJsonModel"))
                {
                    do_tweet_link(link, holder);
                }
                else if (link_type.contains("PlainLinkJsonModel"))
                {
                    JSONObject link_data = link.getJSONObject(TAG_LINK_DATA);

                    title = link_data.getString(TAG_LINK_TITLE);
                    description = link_data.getString(TAG_LINK_DESCRIPTION);
                }
                else if (link_type.contains("VideoLinkJsonModel"))
                {
                    JSONObject link_data = link.getJSONObject(TAG_LINK_DATA);

                    description = link_data.getString(TAG_LINK_DESCRIPTION);
                    title = "";
                }
            }
            catch (JSONException e)
            {
                handle_link_parse_exception();
            }
            holder.title.setText(title);
        }
        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }

}
