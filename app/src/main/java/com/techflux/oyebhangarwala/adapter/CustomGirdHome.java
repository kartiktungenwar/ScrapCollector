package com.techflux.oyebhangarwala.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.techflux.oyebhangarwala.R;

/**
 * Created by Lenovo on 03/05/2017.
 */
public class CustomGirdHome extends BaseAdapter {
    private Context mContext;
    private final int[] imageId;

    public CustomGirdHome(Context c,int[] imageId ) {
        mContext = c;
        this.imageId = imageId;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return imageId.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            grid = new View(mContext);
            grid = inflater.inflate(R.layout.gridview_home, null);
            ImageView imageView = (ImageView)grid.findViewById(R.id.imageView_home);
            imageView.setImageResource(imageId[position]);
        } else {

            grid = (View) convertView;
        }

        return grid;
    }
}


