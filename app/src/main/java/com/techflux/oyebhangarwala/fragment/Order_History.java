package com.techflux.oyebhangarwala.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.techflux.oyebhangarwala.R;
import com.techflux.oyebhangarwala.adapter.CustomListOrder;
import com.techflux.oyebhangarwala.cogfigData.Config;
import com.techflux.oyebhangarwala.dataModel.OrderModel;
import com.techflux.oyebhangarwala.manager.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lenovo on 10/05/2017.
 */
public class Order_History extends Fragment implements AdapterView.OnItemClickListener {

    private ListView mListView;
    ArrayList<OrderModel> orders;
    SessionManager manager;
    private ProgressDialog pd;
    String name;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file;
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_order_history, container, false);

    }

    @Nullable
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        orders = new ArrayList<>();
        manager = new SessionManager(getContext());
        pd = new ProgressDialog(getContext());
        pd.setMessage("Loading...");
        mListView = (ListView) view.findViewById(R.id.listView_order_history);
        HashMap<String, String> user = manager.getUserDetails();
        name = user.get(SessionManager.KEY_EMAIL);
        getData(Config.ORDER_HISTORY_URL+name);
        mListView.setEmptyView(view.findViewById(R.id.empty_view));
        mListView.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Fragment fragment = new Order();
        Bundle arg = new Bundle();
        arg.putString("IndexOrder",orders.get(i).getOrderId());
        arg.putString("Task","Hide");
        fragment.setArguments(arg);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame,fragment);
        fragmentTransaction.commit();

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

    private void getData(String subGroupUrl){
        Log.d("url : ", "" + subGroupUrl);
        pd.show();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(subGroupUrl, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                Log.d("response : ", "" + jsonArray);
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
                orders.add(new OrderModel(jsonObject.getString(Config.KEY_ORDERID),jsonObject.getString(Config.KEY_ORDERACTION)));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        CustomListOrder customList = new CustomListOrder(getContext(),orders);
        mListView.setAdapter(customList);

    }

}
