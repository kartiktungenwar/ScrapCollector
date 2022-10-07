package com.techflux.oyebhangarwala.adapter;

import android.content.Context;
import android.icu.util.Calendar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.techflux.oyebhangarwala.R;
import com.techflux.oyebhangarwala.dataModel.CommentModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lenovo on 04/05/2017.
 */
public class CustomListComment extends BaseAdapter {

    public List<CommentModel> parkingList;
    ArrayList<CommentModel> arraylist;
    private static LayoutInflater inflaterComment;
    Date date1, date2;
    Context mContext;
    public CustomListComment(Context context, ArrayList<CommentModel> apps) {
        mContext = context;
        this.parkingList = apps;
        arraylist = new ArrayList<CommentModel>();
        arraylist.addAll(parkingList);
    }

    @Override
    public int getCount() {
        return parkingList.size();
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
        TextView userName,userComment,userDate;
        de.hdodenhof.circleimageview.CircleImageView userImage;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Holder holder=new Holder();
        View rowView;

        inflaterComment = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflaterComment.inflate(R.layout.listview_comment, null);
        holder.userImage = (de.hdodenhof.circleimageview.CircleImageView) rowView.findViewById(R.id.imageView_comment);
        holder.userName = (TextView) rowView.findViewById(R.id.textView_comment_name);
        holder.userComment = (TextView) rowView.findViewById(R.id.textView_comment_write);
        holder.userDate = (TextView) rowView.findViewById(R.id.textView_comment_date);
        Log.d("Tag",parkingList.get(i).getUser_Comment());
        holder.userName.setText(parkingList.get(i).getUser_Name());
        holder.userComment.setText(parkingList.get(i).getUser_Comment());
        try
        {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date past = format.parse(parkingList.get(i).getUser_Date());
            Calendar cal = Calendar.getInstance();
            cal.setTime(past);
            String monthName = new SimpleDateFormat("MMMM").format(cal.getTime());
            String dateFormat = new SimpleDateFormat("dd").format(cal.getTime());
            Date now = new Date();
            long seconds= TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
            long minutes=TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
            long hours=TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
            long days=TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

            if(seconds<60)
            {
                holder.userDate.setText(seconds+" seconds ago");
            }
            else if(minutes<60)
            {
                holder.userDate.setText(minutes+" minutes ago");
            }
            else if(hours<24)
            {
                holder.userDate.setText(hours+" hours ago");
            }
            else if(days<5)
            {
                holder.userDate.setText(days+" days ago");
            }
            else if(days>5)
            {
                holder.userDate.setText("On "+dateFormat+" "+monthName);
            }
        }
        catch (Exception j){
            j.printStackTrace();
        }
        Glide
                .with( mContext )
                .load(parkingList.get(i).getUser_Image())
                .error( R.drawable.user )
                .into( holder.userImage);
        return rowView;
    }
}
