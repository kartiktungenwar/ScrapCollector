package com.techflux.oyebhangarwala.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.techflux.oyebhangarwala.adapter.RecyclerViewListAddress;
import com.techflux.oyebhangarwala.cogfigData.Config;
import com.techflux.oyebhangarwala.dataModel.AddressModel;
import com.techflux.oyebhangarwala.manager.SessionManager;
import com.techflux.oyebhangarwala.validation.MyValidator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lenovo on 05/06/2017.
 */
public class AddressBook extends Fragment implements View.OnClickListener {

    private ProgressDialog pd;
    private RelativeLayout mrRelativeLayout;
    private RecyclerView mRecyclerView;
    private String userEmail;
    public RecyclerViewListAddress mAdapter;
    ArrayList<AddressModel> bookArrayList;
    TextView emptyList;
    ArrayList<String> dataCountry;
    ArrayList<String> dataCity;
    ArrayList<String> dataState;
    ArrayAdapter<String> dataStateAdapter,dataCityAdapter,dataCountryAdapter;
    Spinner country,state,city;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_address, container, false);

    }

    @Nullable
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        SessionManager sessionManager = new SessionManager(getContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        userEmail = user.get(SessionManager.KEY_EMAIL);
        pd = new  ProgressDialog(getContext());
        pd.setMessage("Loading...");
        bookArrayList = new ArrayList<>();
        emptyList = (TextView) view.findViewById(R.id.textView_ListEmpty);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_address);
        getAllAddress(Config.GETALLADDRESS+userEmail);
        mrRelativeLayout = (RelativeLayout) view.findViewById(R.id._submit);
        mrRelativeLayout.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    // handle back button's click listener
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame,new Home());
                    fragmentTransaction.commit();
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id._submit:
                final Dialog dialog = new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.addinfodialog);
                dialog.setCancelable(false);
                final EditText userName = (EditText) dialog.findViewById(R.id._UserName);
                final EditText userMobile = (EditText) dialog.findViewById(R.id._Contact);
                final EditText userAddress1 = (EditText) dialog.findViewById(R.id._Address1);
                final EditText userAddress2 = (EditText) dialog.findViewById(R.id._Address2);
                final EditText userAddress3 = (EditText) dialog.findViewById(R.id._Address3);
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
                dataStateAdapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_item, dataState);
                dataStateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dataStateAdapter.notifyDataSetChanged();
                state.setAdapter(dataStateAdapter);
                dataCity.add("Select");
                dataCityAdapter = new ArrayAdapter<String>(getContext(),
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
                        boolean flag = true;
                        if(!MyValidator.isValidRequired(userName)){
                            flag = false;
                        }
                        if(!MyValidator.isValidMobile(userMobile)){
                            flag = false; ;
                        }
                        if(!MyValidator.isValidRequired(userAddress1)){
                            flag = false;
                        }
                        if(!MyValidator.isValidRequired(userAddress2)){
                            flag = false;
                        }
                        if(!MyValidator.isValidRequired(userAddress3)){
                            flag = false;
                        }
                        if(userAddress1.getText().toString().endsWith(",")){
                            flag = false;
                            Toast.makeText(getContext(), "Don't put Comma at end", Toast.LENGTH_SHORT).show();
                        }
                        if(userAddress2.getText().toString().endsWith(",")){
                            flag = false;
                            Toast.makeText(getContext(), "Don't put Comma at end", Toast.LENGTH_SHORT).show();
                        }
                        if(userAddress3.getText().toString().endsWith(",")){
                            flag = false;
                            Toast.makeText(getContext(), "Don't put Comma at end", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if(flag) {
                                addAdreess(userEmail,userName.getText().toString(),userMobile.getText().toString(),userAddress1.getText().toString()+",",userAddress2.getText().toString()+",",userAddress3.getText().toString()+",",String.valueOf(city.getSelectedItem())+",".toString(),String.valueOf(state.getSelectedItem())+",", String.valueOf(country.getSelectedItem()));
                                dialog.dismiss();
                                getAllAddress(Config.GETALLADDRESS+userEmail);
                            }
                        }
                    }
                });
                dialog.show();
                break;
        }
    }

    private void getAllAddress(String subGroupUrl){
        Log.d("url : ", "" + subGroupUrl);
        pd.show();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(subGroupUrl, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                Log.d("url", "" + jsonArray);
                displayData(jsonArray);
                pd.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pd.dismiss();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonArrayRequest);
    }

    private void displayData(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                bookArrayList.add(new AddressModel(jsonObject.getString(Config.KEY_USERID), jsonObject.getString(Config.KEY_USERNAME), jsonObject.getString(Config.KEY_USERCONTACT), jsonObject.getString(Config.KEY_USERADDRESS1), jsonObject.getString(Config.KEY_USERADDRESS2), jsonObject.getString(Config.KEY_USERADDRESS3), jsonObject.getString(Config.KEY_USERCITY), jsonObject.getString(Config.KEY_USERSTATE), jsonObject.getString(Config.KEY_USERCOUNTRY)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new RecyclerViewListAddress(getContext(), bookArrayList);
        mRecyclerView.setAdapter(mAdapter);
        if(bookArrayList.isEmpty()){
            emptyList.setVisibility(View.VISIBLE);
        }
        else{
            emptyList.setVisibility(View.GONE);
        }
    }

    private void addAdreess(final String userEmail, final String userName, final String userMobile, final String userAddress1, final String userAddress2, final String userAddress3, final String userCity, final String userState, final String userCountry) {
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.ADDADDRESS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                            Toast.makeText(getContext(),response.toString(),Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.dismiss();
                        Toast.makeText(getContext(),error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(Config.KEY_USERNAME,userName);
                params.put(Config.KEY_USEREMAIL,userEmail);
                params.put(Config.KEY_USERCONTACT,userMobile);
                params.put(Config.KEY_USERADDRESS1,userAddress1);
                params.put(Config.KEY_USERADDRESS2,userAddress2);
                params.put(Config.KEY_USERADDRESS3,userAddress3);
                params.put(Config.KEY_USERCITY,userCity);
                params.put(Config.KEY_USERSTATE,userState);
                params.put(Config.KEY_USERCOUNTRY,userCountry);
                Log.d("url",params.toString());
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void country(){
        Log.d("response :",Config.COUNTRY_URL);
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

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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
        dataCountryAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, dataCountry);
        dataCountryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dataCountryAdapter.notifyDataSetChanged();
        country.setAdapter(dataCountryAdapter);
        int pos = dataCountryAdapter.getPosition("India");
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

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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
        int pos = dataStateAdapter.getPosition("Maharashtra");
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

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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
        int pos = dataCityAdapter.getPosition("Nashik");
        city.setSelection(pos);
    }
}
