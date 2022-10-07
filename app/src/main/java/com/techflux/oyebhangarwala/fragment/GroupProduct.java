package com.techflux.oyebhangarwala.fragment;

import android.app.Dialog;
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
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.techflux.oyebhangarwala.R;
import com.techflux.oyebhangarwala.adapter.RecyclerViewListGroupProduct;
import com.techflux.oyebhangarwala.dataModel.Product;
import com.techflux.oyebhangarwala.manager.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.techflux.oyebhangarwala.R.layout.fragment_group_product;

/**
 * Created by Lenovo on 04/05/2017.
 */
public class GroupProduct extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerViewListGroupProduct adapter;
    private TextView tv_Title;
    private Button btn_submit;
    List<Product> products;
    private String userName;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    String orderId;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        return inflater.inflate(fragment_group_product, container, false);

    }

    @Nullable
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        orderId = "OrderId_"+dateFormat.format(new Date());
        Bundle value = getArguments();
        final String name = value.getString("index");
        fragmentManager = getActivity().getSupportFragmentManager();

        if (name.equals("Glass")) {
            setProductGlass();
        }
        if (name.equals("Rubber")) {
            setProductRubber();
        }
        if (name.equals("Plastic")) {
            setProductPlastic();
        }
        if (name.equals("Paper")) {
            setProductPaper();
        }
        if (name.equals("Plastic")) {
            setProductPlastic();
        }
        if (name.equals("Cardboard")) {
            setProductCardboard();
        }
        if (name.equals("Metal")) {
            setProductMetal();
        }

        tv_Title = (TextView) view.findViewById(R.id.textView_title_group);
        tv_Title.setText(name + " Product");

        btn_submit = (Button) view.findViewById(R.id.buttonGroupClick);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.orderdialog);
                dialog.setCancelable(false);

                ImageView closewindow = (ImageView) dialog.findViewById(R.id.windowClose_Order);
                closewindow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                Button btn_send = (Button) dialog.findViewById(R.id.submit_Order);
                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SessionManager sessionManager = new SessionManager(getContext());
                        HashMap<String, String> user = sessionManager.getUserDetails();
                        userName = user.get(SessionManager.KEY_EMAIL);
                        Fragment fragment = new Order();
                        Bundle arg = new Bundle();
                        arg.putString("IndexName",userName);
                        arg.putString("IndexOrder",orderId);
                        arg.putString("Task","show");
                        fragment.setArguments(arg);
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.content_frame,fragment);
                        fragmentTransaction.commit();
                        dialog.dismiss();
                        Log.d("url", "Ok "+userName+" "+orderId);
                    }
                });
                dialog.show();
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        adapter = new RecyclerViewListGroupProduct(getContext(), products);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
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
                    fragmentTransaction.replace(R.id.content_frame,new com.techflux.oyebhangarwala.fragment.Product());
                    fragmentTransaction.commit();
                    return true;
                }
                return false;
            }
        });

    }

    public void setProductGlass() {
        Product product1 = new Product("Glass Product", "Glass", R.drawable.i13, "101");
        Product product2 = new Product("Glass Product", "Bottles", R.drawable.i14, "102");
        Product product3 = new Product("Glass Product", "Jars", R.drawable.i15, "103");
        Product product4 = new Product("Glass Product", "Cutlery", R.drawable.i16, "104");
        Product product5 = new Product("Glass Product", "Other", R.drawable.i17, "105");
        products = new ArrayList<Product>();
        products.add(product1);
        products.add(product2);
        products.add(product3);
        products.add(product4);
        products.add(product5);
    }

    public void setProductPlastic() {
        Product product2 = new Product("Plastic Product", "Bottles", R.drawable.i14, "202");
        Product product3 = new Product("Plastic Product", "Jars", R.drawable.i15, "203");
        Product product4 = new Product("Plastic Product", "Cutlery", R.drawable.i16, "204");
        Product product5 = new Product("Plastic Product", "Other", R.drawable.i17, "205");
        products = new ArrayList<Product>();
        products.add(product2);
        products.add(product3);
        products.add(product4);
        products.add(product5);
    }

    public void setProductRubber() {
        Product product5 = new Product("Rubber Product", "Other", R.drawable.i17, "305");
        products = new ArrayList<Product>();
        products.add(product5);
    }

    public void setProductPaper() {
        Product product5 = new Product("Paper Product", "Other", R.drawable.i17, "405");
        products = new ArrayList<Product>();
        products.add(product5);
    }

    public void setProductCardboard() {
        Product product5 = new Product("Cardboard Product", "Other", R.drawable.i17, "505");
        products = new ArrayList<Product>();
        products.add(product5);
    }

    public void setProductMetal() {
        Product product1 = new Product("Meatal Product", "Glass", R.drawable.i13, "601");
        Product product2 = new Product("Metal Product", "Bottles", R.drawable.i14, "602");
        Product product3 = new Product("Metal Product", "Jars", R.drawable.i15, "603");
        Product product4 = new Product("Metal Product", "Cutlery", R.drawable.i16, "604");
        Product product5 = new Product("Metal Product", "Other", R.drawable.i17, "605");
        products = new ArrayList<Product>();
        products.add(product1);
        products.add(product2);
        products.add(product3);
        products.add(product4);
        products.add(product5);
    }

}
