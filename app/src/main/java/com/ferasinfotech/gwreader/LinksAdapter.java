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
    public static class ViewHolder{

        public TextView title;
        public TextView description;
        //public ImageView image;

    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        View vi = convertView;
        ViewHolder holder;

        if (vi == null)
        {
            vi = inflater.inflate(R.layout.list_item, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.title = (TextView) vi.findViewById(R.id.link_title);
            holder.description = (TextView)vi.findViewById(R.id.link_description);
            //holder.image=(ImageView)vi.findViewById(R.id.image);

            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else
        {
            holder=(ViewHolder)vi.getTag();
        }

        if (links.length() == 0)
        {
            holder.title.setText("");
            holder.description.setText("");
        }
        else
        {
            try
            {
                JSONObject link = links.getJSONObject(position);
                JSONObject link_data = link.getJSONObject(TAG_LINK_DATA);
                holder.title.setText(link_data.getString(TAG_LINK_TITLE));
                holder.description.setText(link_data.getString(TAG_LINK_DESCRIPTION));
            }
            catch (JSONException e)
            {
                holder.title.setText("Link Error");
                holder.description.setText("");
            }
        }
        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }

}
