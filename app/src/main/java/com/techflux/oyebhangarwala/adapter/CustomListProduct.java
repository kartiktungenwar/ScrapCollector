package com.techflux.oyebhangarwala.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.techflux.oyebhangarwala.R;

/**
 * Created by Lenovo on 04/05/2017.
 */
public class CustomListProduct extends BaseAdapter {

    private int[] product_image;
    private String[] product_name;
    private String[] product_cost;
    private static LayoutInflater inflater=null;
    Context mContext;

    public CustomListProduct(Context context, int[] product_image, String[] product_name, String[] product_cost) {
        mContext = context;
        this.product_image = product_image;
        this.product_name = product_name;
        this.product_cost = product_cost;
    }

    @Override
    public int getCount() {
        return product_image.length;

    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public class Holder
    {
        TextView title,cost;
        ImageView image,arrow;
    }
    @Override
    public View getView(int postion, View view, ViewGroup viewGroup) {

        Holder holder=new Holder();
        View rowView;
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.listview_product, null);
        holder.image = (ImageView) rowView.findViewById(R.id.imageView_product);
        holder.arrow = (ImageView) rowView.findViewById(R.id.imageView_arrow);
        holder.title = (TextView) rowView.findViewById(R.id.textView_product_name);
        holder.cost= (TextView) rowView.findViewById(R.id.textView_product_cost);
        holder.image.setImageResource(product_image[postion]);
        holder.title.setText(product_name[postion]);
        holder.cost.setText(product_cost[postion]);
        holder.arrow.setImageResource(R.drawable.arrow);
        return rowView;
    }
}
