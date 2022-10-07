package com.techflux.oyebhangarwala.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.techflux.oyebhangarwala.R;
import com.techflux.oyebhangarwala.dataModel.AddressModel;

import java.util.ArrayList;

/**
 * Created by Lenovo on 11/06/2017.
 */
public class RecyclerViewAddressList extends RecyclerView.Adapter<RecyclerViewAddressList.Holder> {

    String mobile,address1,address2,address3,city,state,country,id;
    Context context;
    ArrayList<AddressModel> addressModelArrayList;

    public RecyclerViewAddressList(Context context, ArrayList<AddressModel> addressModelArrayList) {
        this.context = context;
        this.addressModelArrayList = addressModelArrayList;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public class Holder extends RecyclerView.ViewHolder {
        public TextView userName,useraddress1,useraddress2,useraddress3,userWhereabout,userMobile;
        public CheckBox mRadioButton;
        public Holder(View view) {
            super(view);
            userName = (TextView) view.findViewById(R.id.view_FullName);
            useraddress1 = (TextView) view.findViewById(R.id.view_Address1);
            useraddress2 = (TextView) view.findViewById(R.id.view_Address2);
            useraddress3 = (TextView) view.findViewById(R.id.view_Address3);
            userWhereabout = (TextView) view.findViewById(R.id.view_City_State);
            userMobile = (TextView) view.findViewById(R.id.view_Mobile);
            mRadioButton = (CheckBox) view.findViewById(R.id.radioButton_selected);


        }
    }
    @Override
    public RecyclerViewAddressList.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_select_address, parent, false);
        return new Holder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewAddressList.Holder holder, final int position)  {
        final AddressModel addressModel = addressModelArrayList.get(position);
        holder.userName.setText(addressModel.getName());
        holder.useraddress1.setText(addressModel.getAddress1()+",");
        holder.useraddress2.setText(addressModel.getAddress2()+",");
        holder.useraddress3.setText(addressModel.getAddress3()+",");
        holder.userWhereabout.setText(addressModel.getCity()+", "+addressModel.getState()+",\n"+addressModel.getCountry());
        holder.userMobile.setText("Contact no.: "+addressModel.getMobile());
        holder.mRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMobile(addressModel.getMobile());
                setAddress1(addressModel.getAddress1());
                setAddress2(addressModel.getAddress2());
                setAddress3(addressModel.getAddress3());
                setCity(addressModel.getCity());
                setState(addressModel.getState());
                setCountry(addressModel.getCountry());
                setId(addressModel.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressModelArrayList.size();
    }
}
