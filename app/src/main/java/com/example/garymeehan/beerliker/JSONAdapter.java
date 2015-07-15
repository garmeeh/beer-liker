package com.example.garymeehan.beerliker;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by garymeehan on 14/07/2015.
 */
public class JSONAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater mInflater;
    JSONArray mJsonArray;

    public JSONAdapter(Context context, LayoutInflater inflater) {
        mContext = context;
        mInflater = inflater;
        mJsonArray = new JSONArray();
    }

    @Override
    public int getCount() {
        return mJsonArray.length();
    }

    @Override
    public Object getItem(int position) {

        return mJsonArray.optJSONObject(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        // check if the view already exists
        // if so, no need to inflate
        if (convertView == null) {

            // Inflate the custom row layout from your XML.
            convertView = mInflater.inflate(R.layout.row_beer, null);

            // create a new "Holder" with subviews
            holder = new ViewHolder();
            holder.thumbnailImageView = (ImageView) convertView.findViewById(R.id.img_thumbnail);
            holder.nameTextView = (TextView) convertView.findViewById(R.id.text_name);
            holder.organicTextView = (TextView) convertView.findViewById(R.id.text_organic);

            // hang onto this holder for future recycling
            convertView.setTag(holder);
        } else {
            //skip creation and use created holder
            holder = (ViewHolder) convertView.getTag();
        }

        // Get the current beer's data in JSON form
        JSONObject jsonObject = (JSONObject) getItem(position);

        // See if there is labels in the Object
        if (jsonObject.has("labels")) {
            JSONObject newLabels = null;
            try {
                newLabels = jsonObject.getJSONObject("labels");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String iconLabel = null;
            //get the icon
            if(newLabels.has("icon")){
                Log.d("labels string", String.valueOf(newLabels));
                try {

                    iconLabel = newLabels.getString("medium");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Picasso.with(mContext).load(iconLabel).placeholder(R.drawable.ic_beer).into(holder.thumbnailImageView);
        } else {

            // If there is no labels in the object, use a placeholder
            holder.thumbnailImageView.setImageResource(R.drawable.ic_beer);
        }

        // Grab the title and author from the JSON
        String beerName = "";
        String beerOrg = "";

        if (jsonObject.has("name")) {
            beerName = jsonObject.optString("name");
        }

        if (jsonObject.has("isOrganic")) {

            String orgCheck = jsonObject.optString("isOrganic");

            if(orgCheck.equals("N")){
                beerOrg = "Not Organic";
            }else{
                beerOrg = "Organic";
            }
            Log.d("Organic", beerOrg);
        }

        // Send these Strings to the TextViews for display
        holder.nameTextView.setText(beerName);
        holder.organicTextView.setText(beerOrg);

        return convertView;
    }

    private static class ViewHolder {
        public ImageView thumbnailImageView;
        public TextView nameTextView;
        public TextView organicTextView;
    }

    public void updateData(JSONArray jsonArray) {
        // update the adapter's dataset
        mJsonArray = jsonArray;
        notifyDataSetChanged();
    }
}
