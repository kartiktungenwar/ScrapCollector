package com.techflux.oyebhangarwala.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.techflux.oyebhangarwala.R;
import com.techflux.oyebhangarwala.dataModel.OrderModel;

import java.util.ArrayList;

/**
 * Created by Lenovo on 12/05/2017.
 */
public class CustomListOrder extends BaseAdapter {

    private static LayoutInflater inflater=null;
    public ArrayList<OrderModel> orders;
    Context mContext;
    public CustomListOrder(Context context, ArrayList<OrderModel> orders) {
        this.orders = orders;
        mContext = context;
    }

    @Override
    public int getCount() {
        return orders.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public class ViewHolder
    {
        TextView order;
        ImageView image;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        OrderModel model = orders.get(i);
        ViewHolder holder=new ViewHolder();
        View rowView;
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.listview_order_history, null);
        holder.order = (TextView) rowView.findViewById(R.id.textViewOrderHistory);
        holder.image = (ImageView)rowView.findViewById(R.id.iimageViewOrderHistory);
        holder.order.setText(model.getOrderId());
        if(model.getOrderAction().equals("true")){
            holder.image.setImageResource(R.drawable.verifiedorder);
        }
        if(model.getOrderAction().equals("false")){
            holder.image.setImageResource(R.drawable.pendingorder);
        }
        return rowView;
    }
}
