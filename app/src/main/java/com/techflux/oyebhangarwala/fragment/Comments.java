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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.techflux.oyebhangarwala.R;
import com.techflux.oyebhangarwala.activity.MainActivity;
import com.techflux.oyebhangarwala.adapter.CustomListComment;
import com.techflux.oyebhangarwala.cogfigData.Config;
import com.techflux.oyebhangarwala.dataModel.CommentModel;
import com.techflux.oyebhangarwala.manager.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lenovo on 04/05/2017.
 */
public class Comments extends Fragment {

    ArrayList<CommentModel> postArrayList;
    private ListView mCommentlist;
    private EditText et_comments;
    private ImageView send;
    SessionManager session;
    String name,email,bitmap;
    private ProgressDialog pd;
    private de.hdodenhof.circleimageview.CircleImageView circleImageView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_comments, container, false);

    }

    @Nullable
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        pd = new ProgressDialog(getContext());
        pd.setMessage("Loading...");
        session = new SessionManager(getContext());
        HashMap<String, String> user = session.getUserDetails();
        email= user.get(SessionManager.KEY_EMAIL);
        name = user.get(SessionManager.KEY_NAME);
        bitmap = user.get(SessionManager.KEY_ID);
        Log.d("url","Image"+bitmap);
        mCommentlist = (ListView) view.findViewById(R.id.listView_comment);
        postArrayList = new ArrayList<>();
        requestData(Config.COMMENT_GET_URL);
        circleImageView = (de.hdodenhof.circleimageview.CircleImageView) view.findViewById(R.id.imageView_user);
        Glide.with(getActivity())
                .load(user.get(SessionManager.KEY_ID))
                .centerCrop()
                .error(R.mipmap.ic_launcher).into(circleImageView);
        et_comments = (EditText) view.findViewById(R.id.et_comment);
        send = (ImageView) view.findViewById(R.id.imageViewSend);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commentAdd();
                fragmentJump();
            }
        });
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

    private void commentAdd(){
        final String userName = name;
        final String userEmail = email;
        final String comments = et_comments.getText().toString().trim();
        final String userImage = bitmap;
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.COMMENT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        requestData(Config.COMMENT_GET_URL);
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
                params.put(Config.KEY_COMMENTS,comments);
                params.put(Config.KEY_USERIMAGE,userImage);
                Log.d("url" , String.valueOf(params));
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);

    }


    private void requestData(String Url) {
        Log.d("url : ", "" + Url);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                Log.d("response : ", "" + jsonArray);
                displayData(jsonArray);
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

    private void displayData(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                postArrayList.add(new CommentModel(jsonObject.getString(Config.KEY_USERID),jsonObject.getString(Config.KEY_USERNAME),jsonObject.getString(Config.KEY_COMMENTS),jsonObject.getString(Config.KEY_DATE),jsonObject.getString(Config.KEY_USERIMAGE)));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        final CustomListComment customListComment = new CustomListComment(getContext(),postArrayList);
        mCommentlist.setAdapter(customListComment);
    }

    private void fragmentJump() {
        Fragment mFragment = new AddressBook();
        switchContent(R.id.content_frame, mFragment);
    }

    public void switchContent(int id, Fragment fragment) {
        if (getContext() == null)
            return;
        if (getContext() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getContext();
            Fragment frag = fragment;
            mainActivity.switchContent(id, frag);
        }
    }
}