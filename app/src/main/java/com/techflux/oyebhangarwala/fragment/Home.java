package com.techflux.oyebhangarwala.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.techflux.oyebhangarwala.R;
import com.techflux.oyebhangarwala.adapter.CustomGirdHome;
import com.techflux.oyebhangarwala.manager.SessionManager;

/**
 * Created by Lenovo on 03/05/2017.
 */
public class Home extends Fragment implements AdapterView.OnItemClickListener {

    private int[] imageId = {R.drawable.i1, R.drawable.i2, R.drawable.i3, R.drawable.i4, R.drawable.i5, R.drawable.i6};
    private GridView grid;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    SessionManager manager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_home, container, false);

    }

    @Nullable
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        CustomGirdHome adapter = new CustomGirdHome(getContext(), imageId);
        fragmentManager = getActivity().getSupportFragmentManager();
        grid= (GridView) view.findViewById(R.id.gridViewHomeList);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        manager = new SessionManager(getContext());
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, new Product());
        fragmentTransaction.commit();
    }
}
