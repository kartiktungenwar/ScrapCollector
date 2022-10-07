package com.techflux.oyebhangarwala.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.techflux.oyebhangarwala.R;
import com.techflux.oyebhangarwala.activity.MainActivity;
import com.techflux.oyebhangarwala.cogfigData.Config;
import com.techflux.oyebhangarwala.dataModel.AddressModel;
import com.techflux.oyebhangarwala.fragment.AddressBook;
import com.techflux.oyebhangarwala.manager.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lenovo on 05/06/2017.
 */
public class RecyclerViewListAddress extends RecyclerView.Adapter<RecyclerViewListAddress.ViewHolder> {
    private ArrayList<AddressModel> bookArrayList;
    Context context;
    private String userEmail;
    private ProgressDialog pd;
    private ArrayList<String> dataCountry;
    private ArrayList<String> dataCity;
    private ArrayList<String> dataState;
    private ArrayAdapter<String> dataStateAdapter,dataCityAdapter,dataCountryAdapter;
    private Spinner country,state,city;
    private String countryName,stateName,cityName;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView userName,useraddress1,useraddress2,useraddress3,userWhereabout,userMobile;
        public Button btnEdit,btnDelete;
        public ViewHolder(View view) {
            super(view);
            pd = new ProgressDialog(context);
            pd.setMessage("Loading...");
            userName = (TextView) view.findViewById(R.id.textView_FullName);
            useraddress1 = (TextView) view.findViewById(R.id.textView_Address1);
            useraddress2 = (TextView) view.findViewById(R.id.textView_Address2);
            useraddress3 = (TextView) view.findViewById(R.id.textView_Address3);
            userWhereabout = (TextView) view.findViewById(R.id.textView_City_State);
            userMobile = (TextView) view.findViewById(R.id.textView_Mobile);
            btnEdit = (Button) view.findViewById(R.id.button_Edit);
            btnDelete = (Button) view.findViewById(R.id.button_Delete);
        }
    }

    public RecyclerViewListAddress(Context context, ArrayList<AddressModel> bookArrayList) {
        this.context = context;
        this.bookArrayList = bookArrayList;
    }

    @Override
    public RecyclerViewListAddress.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listview_address_book, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewListAddress.ViewHolder holder, final int position) {
        final AddressModel addressModel = bookArrayList.get(position);
        holder.userName.setText(addressModel.getName());
        holder.useraddress1.setText(addressModel.getAddress1()+",");
        holder.useraddress2.setText(addressModel.getAddress2()+",");
        holder.useraddress3.setText(addressModel.getAddress3()+",");
        holder.userWhereabout.setText(addressModel.getCity()+", "+addressModel.getState()+",\n"+addressModel.getCountry());
        holder.userMobile.setText("Contact no.: "+addressModel.getMobile());
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.addinfodialog);
                dialog.setCancelable(false);
                final EditText userName = (EditText) dialog.findViewById(R.id._UserName);
                userName.setText(addressModel.getName());
                final EditText userMobile = (EditText) dialog.findViewById(R.id._Contact);
                userMobile.setText(addressModel.getMobile());
                final EditText userAddress1 = (EditText) dialog.findViewById(R.id._Address1);
                userAddress1.setText(addressModel.getAddress1());
                final EditText userAddress2 = (EditText) dialog.findViewById(R.id._Address2);
                userAddress2.setText(addressModel.getAddress2());
                final EditText userAddress3 = (EditText) dialog.findViewById(R.id._Address3);
                userAddress3.setText(addressModel.getAddress3());
                countryName = addressModel.getCountry();
                stateName = addressModel.getState();
                cityName = addressModel.getCity();
                dataCountry = new ArrayList<>();
                dataState = new ArrayList<>();
                dataCity = new ArrayList<>();
                country = (Spinner) dialog.findViewById(R.id.spinner_country);
                country();
                state = (Spinner) dialog.findViewById(R.id.spinner_state);
                city = (Spinner) dialog.findViewById(R.id.spinner_city);
                country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String dataCountryName = String.valueOf(country.getSelectedItem());
                        dataState.clear();
                        state(dataCountryName);
                        if(String.valueOf(country.getSelectedItem()).equals("India")){

                        }
                        else {

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String dataStateName = String.valueOf(state.getSelectedItem());
                        dataCity.clear();
                        city(dataStateName);
                        if(String.valueOf(state.getSelectedItem()).equals("Maharashtra")){

                        }
                        else {

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if(String.valueOf(state.getSelectedItem()).equals("Nashik")){

                        }
                        else {

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                dataState.add("Select");
                dataStateAdapter = new ArrayAdapter<String>(context,
                        android.R.layout.simple_spinner_item, dataState);
                dataStateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dataStateAdapter.notifyDataSetChanged();
                state.setAdapter(dataStateAdapter);
                dataCity.add("Select");
                dataCityAdapter = new ArrayAdapter<String>(context,
                        android.R.layout.simple_spinner_item, dataCity);
                dataCityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dataCityAdapter.notifyDataSetChanged();
                city.setAdapter(dataCityAdapter);
                ImageView closewindow = (ImageView) dialog.findViewById(R.id.windowClose_Edit);
                closewindow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                Button btn_send = (Button) dialog.findViewById(R.id.submit_INFO);
                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SessionManager manager = new SessionManager(context);
                        HashMap<String,String> log = manager.getUserDetails();
                        userEmail = log.get(SessionManager.KEY_EMAIL);
                        updateAddreess(userEmail,userName.getText().toString(),userMobile.getText().toString(),userAddress1.getText().toString(),userAddress2.getText().toString(),userAddress3.getText().toString(),String.valueOf(city.getSelectedItem()),String.valueOf(state.getSelectedItem()), String.valueOf(country.getSelectedItem()),addressModel.getId());
                        dialog.dismiss();
                        fragmentJump();
                    }
                });
                dialog.show();
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        deleteAddress(addressModel.getId());
                        fragmentJump();
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookArrayList.size();
    }

    private void deleteAddress(final String id) {
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.DELETE_ADDRESS_URL+id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        Toast.makeText(context.getApplicationContext(),response,Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.dismiss();
                        Toast.makeText(context.getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(Config.KEY_USERID,id);
                Log.d("url",params.toString());
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void updateAddreess(final String userEmail, final String userName, final String userMobile, final String address1, final String address2, final String address3, final String city, final String state, final String country, final String id) {

        pd.show();
        Log.d("url", Config.UPDATE_ADDRESS_URL);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.UPDATE_ADDRESS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        Toast.makeText(context,response.toString(),Toast.LENGTH_LONG).show();
                        fragmentJump();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.dismiss();
                        Toast.makeText(context,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(Config.KEY_USERNAME,userName);
                params.put(Config.KEY_USEREMAIL,userEmail);
                params.put(Config.KEY_USERCONTACT,userMobile);
                params.put(Config.KEY_USERADDRESS1,address1);
                params.put(Config.KEY_USERADDRESS2,address2);
                params.put(Config.KEY_USERADDRESS3,address3);
                params.put(Config.KEY_USERCITY,city);
                params.put(Config.KEY_USERSTATE,state);
                params.put(Config.KEY_USERCOUNTRY,country);
                Log.d("url",id);
                params.put(Config.KEY_USERID,id);
                Log.d("url",params.toString());
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }


    private void fragmentJump() {
        Fragment mFragment = new AddressBook();
        switchContent(R.id.content_frame, mFragment);
    }

    public void switchContent(int id, Fragment fragment) {
        if (context == null)
            return;
        if (context instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) context;
            Fragment frag = fragment;
            mainActivity.switchContent(id, frag);
        }
    }
    private void country(){
        Log.d("response :", Config.COUNTRY_URL);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Config.COUNTRY_URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                Log.d("response : ", "" + jsonArray);
                displayCountry(jsonArray);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", String.valueOf(error));
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonArrayRequest);
    }

    private void displayCountry(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                dataCountry.add(jsonObject.getString(Config.KEY_NAME));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        dataCountryAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, dataCountry);
        dataCountryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataCountryAdapter.notifyDataSetChanged();
        country.setAdapter(dataCountryAdapter);
        int pos = dataCountryAdapter.getPosition(countryName);
        country.setSelection(pos);
    }

    private void state(String name){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Config.STATE_URL+name, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                Log.d("response : ", "" + jsonArray);
                displayState(jsonArray);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", String.valueOf(error));
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonArrayRequest);
    }

    private void displayState(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                dataState.add(jsonObject.getString(Config.KEY_NAME));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        dataStateAdapter.notifyDataSetChanged();
        int pos = dataStateAdapter.getPosition(stateName);
        state.setSelection(pos);
    }

    private void city(String name){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Config.CITY_URL+name, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                Log.d("response : ", "" + jsonArray);
                displayCity(jsonArray);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error", String.valueOf(error));
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonArrayRequest);
    }

    private void displayCity(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                dataCity.add(jsonObject.getString(Config.KEY_NAME));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        dataCityAdapter.notifyDataSetChanged();
        int pos = dataCityAdapter.getPosition(cityName);
        city.setSelection(pos);
    }
}
