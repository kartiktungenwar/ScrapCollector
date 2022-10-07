package com.techflux.oyebhangarwala.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.techflux.oyebhangarwala.R;
import com.techflux.oyebhangarwala.adapter.CustomListProduct;

/**
 * Created by Lenovo on 04/05/2017.
 */
public class Product extends Fragment implements AdapterView.OnItemClickListener {

    private int [] product_Image = {R.drawable.i7, R.drawable.i8, R.drawable.i9, R.drawable.i10, R.drawable.i11, R.drawable.i12};
    private String [] product_Name = {"Glass","Rubber","Plastic","Paper","Cardboard","Metal"};
    private String [] product_Cost = {"10/-kg","10/-kg","10/-kg","10/-kg","10/-kg","10/-kg"};
    private ListView mListView;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_product, container, false);

    }

    @Nullable
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        fragmentManager = getActivity().getSupportFragmentManager();
        CustomListProduct customListProduct = new CustomListProduct(getContext(),product_Image,product_Name,product_Cost);
        mListView = (ListView) view.findViewById(R.id.listView_product);
        mListView.setAdapter(customListProduct);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Fragment fragment = new GroupProduct() ;
        Bundle args = new Bundle();
        args.putString("index", product_Name[i]);
        fragment.setArguments(args);
        fragmentTransaction = fragmentManager.beginTransaction();
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
}