package com.techflux.oyebhangarwala.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.techflux.oyebhangarwala.R;
import com.techflux.oyebhangarwala.adapter.RecyclerViewListOrderProduct;
import com.techflux.oyebhangarwala.cogfigData.Config;
import com.techflux.oyebhangarwala.dataModel.Product;
import com.techflux.oyebhangarwala.manager.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lenovo on 08/05/2017.
 */
public class Order  extends Fragment implements View.OnClickListener {

    public RecyclerView recyclerView;
    public RecyclerViewListOrderProduct adapter;
    ArrayList<Product> products;
    private ImageView done,cancel;
    private ProgressDialog pd;
    String name,orderId,configUrl,email,getValue;
    private RelativeLayout mRelativeLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_order, container, false);

    }

    @Nullable
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        Bundle value = getArguments();
        orderId = value.getString("IndexOrder");
        getValue = value.getString("Task");
        SessionManager sessionManager = new SessionManager(getContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        email = user.get(SessionManager.KEY_EMAIL);
        name = user.get(SessionManager.KEY_NAME);
        Log.d("Url","Email "+name+",Order "+orderId);
        mRelativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayout_Show);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        products = new ArrayList<>();
        pd = new ProgressDialog(getContext());
        pd.setMessage("Loading...");
        configUrl = Config.ORDER_URL+"?customer_email="+email+"&customer_order_id="+orderId;
        getData(configUrl);
        done = (ImageView) view.findViewById(R.id.imageViewDone);
        cancel = (ImageView) view.findViewById(R.id.imageViewCancel);
        done.setOnClickListener(this);
        cancel.setOnClickListener(this);
        if(getValue.equals("Hide")){
            mRelativeLayout.setVisibility(View.GONE);
        }

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
        pd.show();
        Log.d("url : ", "" + subGroupUrl);
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
                Toast.makeText(getContext(), (CharSequence) error, Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonArrayRequest);
    }

    private void displayData(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Log.d("url", "" +jsonObject.getString(Config.KEY_PRODUCTID));
                Log.d("url", "" +jsonObject.getString(Config.KEY_PRODUCTYPE));
                Log.d("url", "" +jsonObject.getString(Config.KEY_PRODUCTNAME));
                Log.d("url", "" +jsonObject.getString(Config.KEY_PRODUCTQUANTITY));
                products.add(new Product(name,orderId,jsonObject.getString(Config.KEY_PRODUCTID),jsonObject.getString(Config.KEY_PRODUCTYPE),jsonObject.getString(Config.KEY_PRODUCTNAME),jsonObject.getString(Config.KEY_PRODUCTQUANTITY),jsonObject.getString(Config.KEY_ORDEREDIT)));
            } catch (JSONException e){
                e.printStackTrace();
            }

        }
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        adapter = new RecyclerViewListOrderProduct(getContext(), products);
        recyclerView.setAdapter(adapter);
    }



    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.imageViewDone:
                Fragment fragment = new SelectAddress();
                Bundle args = new Bundle();
                args.putString("IndexOrder",orderId);
                fragment.setArguments(args);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame,fragment);
                fragmentTransaction.commit();
                break;
            case R.id.imageViewCancel:
                FragmentManager mfragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction mfragmentTransaction = mfragmentManager.beginTransaction();
                mfragmentTransaction.replace(R.id.content_frame,new com.techflux.oyebhangarwala.fragment.Product());
                mfragmentTransaction.commit();
                break;
        }
    }


}

