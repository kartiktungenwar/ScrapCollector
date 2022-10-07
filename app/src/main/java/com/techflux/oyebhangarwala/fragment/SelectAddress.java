package com.techflux.oyebhangarwala.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
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
import android.widget.RadioButton;
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
import com.techflux.oyebhangarwala.adapter.RecyclerViewAddressList;
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
 * Created by Lenovo on 11/06/2017.
 */
public class SelectAddress extends Fragment implements View.OnClickListener {

    private RecyclerView viewRecycler;
    ArrayList<AddressModel> addressModelArrayList;
    RecyclerViewAddressList adapter;
    private ProgressDialog pd;
    private RelativeLayout doneOrder;
    private String userEmail;
    private String userOrderId;
    private String username;
    private String paymentMode;
    private Button submit,edit,detele;
    TextView emptyView;
    private ArrayList<String> dataCountry;
    private ArrayList<String> dataCity;
    private ArrayList<String> dataState;
    private ArrayAdapter<String> dataStateAdapter,dataCityAdapter,dataCountryAdapter;
    private Spinner country,state,city;
    private String countryName = null,stateName = null,cityName= null;
    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file;
        //change R.layout.yourlayoutfilename for each of your fragments
        return inflater.inflate(R.layout.fragment_select_address, container, false);

    }

    @Nullable
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Bundle value = getArguments();
        userOrderId = value.getString("IndexOrder");
        SessionManager manager = new SessionManager(getContext());
        HashMap<String,String> log = manager.getUserDetails();
        userEmail = log.get(SessionManager.KEY_EMAIL);
        username = log.get(SessionManager.KEY_NAME);
        SessionManager sessionManager = new SessionManager(getContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        userEmail = user.get(SessionManager.KEY_EMAIL);
        pd = new ProgressDialog(getContext());
        pd.setMessage("Loading...");
        addressModelArrayList = new ArrayList<>();
        emptyView = (TextView) view.findViewById(R.id.textView_Empty);
        viewRecycler = (RecyclerView) view.findViewById(R.id.listView_);
        viewRecycler.addItemDecoration(new DividerItemDecoration(getActivity(), null));
        submit = (Button) view.findViewById(R.id._done_address);
        submit.setOnClickListener(this);
        edit = (Button) view.findViewById(R.id._button_Edit);
        edit.setOnClickListener(this);
        detele =(Button) view.findViewById(R.id._button_Delete);
        detele.setOnClickListener(this);
        doneOrder = (RelativeLayout) view.findViewById(R.id.add_address);
        doneOrder.setOnClickListener(this);
        getAddress(Config.GETALLADDRESS+userEmail);
    }

    private void getAddress(String subGroupUrl){
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
                addressModelArrayList.add(new AddressModel(jsonObject.getString(Config.KEY_USERID), jsonObject.getString(Config.KEY_USERNAME), jsonObject.getString(Config.KEY_USERCONTACT), jsonObject.getString(Config.KEY_USERADDRESS1), jsonObject.getString(Config.KEY_USERADDRESS2), jsonObject.getString(Config.KEY_USERADDRESS3), jsonObject.getString(Config.KEY_USERCITY), jsonObject.getString(Config.KEY_USERSTATE), jsonObject.getString(Config.KEY_USERCOUNTRY)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        viewRecycler.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        viewRecycler.setLayoutManager(layoutManager);
        adapter = new RecyclerViewAddressList(getContext(), addressModelArrayList);
        viewRecycler.setAdapter(adapter);
        if (addressModelArrayList.isEmpty()){
            emptyView.setVisibility(View.VISIBLE);
        }else {
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id._done_address:
                if(adapter.getId()==null){
                    Toast.makeText(getContext(),"Select your Address", Toast.LENGTH_SHORT).show();
                }else {
                    final String address = adapter.getAddress1() +", "+ adapter.getAddress2() +", "+ adapter.getAddress3() +", "+ adapter.getCity() +", "+ adapter.getState() +", "+
                            adapter.getCountry();
                    final Dialog dialog = new Dialog(getContext());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.paymentdialogbox);
                    dialog.setCancelable(true);
                    TextView title = (TextView) dialog.findViewById(R.id._DialogTitle);
                    title.setText("Payment Mode");
                    ImageView close = (ImageView) dialog.findViewById(R.id._windowclose);
                    final RadioButton payTM = (RadioButton) dialog.findViewById(R.id.radioButtonPayTm);
                    final RadioButton bank = (RadioButton) dialog.findViewById(R.id.radioButtonBank);
                    final RadioButton payU = (RadioButton) dialog.findViewById(R.id.radioButtonPayU);
                    final EditText payTm = (EditText) dialog.findViewById(R.id._PayTM);
                    payTm.setVisibility(View.GONE);
                    final EditText payu = (EditText) dialog.findViewById(R.id._PayU);
                    payu.setVisibility(View.GONE);
                    final EditText bankName = (EditText) dialog.findViewById(R.id._BankName);
                    bankName.setVisibility(View.GONE);
                    final EditText bankAccountNo = (EditText) dialog.findViewById(R.id._BankAccountNo);
                    bankAccountNo.setVisibility(View.GONE);
                    final EditText bankCorrectAccountNo = (EditText) dialog.findViewById(R.id._BankCorrectAccountNo);
                    bankCorrectAccountNo.setVisibility(View.GONE);
                    final EditText bankIFSCCOde = (EditText) dialog.findViewById(R.id._BankIFSCCode);
                    bankIFSCCOde.setVisibility(View.GONE);
                    final Button submit =(Button) dialog.findViewById(R.id.SubmitBtn) ;
                    submit.setVisibility(View.GONE);
                    payTM.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bank.setVisibility(View.GONE);
                            payTM.setVisibility(View.GONE);
                            payU.setVisibility(View.GONE);
                            payTm.setVisibility(View.VISIBLE);
                            submit.setVisibility(View.VISIBLE);
                            setPaymentMode(payTM.getText().toString());
                        }
                    });
                    bank.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bank.setVisibility(View.GONE);
                            payTM.setVisibility(View.GONE);
                            payU.setVisibility(View.GONE);
                            bankName.setVisibility(View.VISIBLE);
                            bankAccountNo.setVisibility(View.VISIBLE);
                            bankCorrectAccountNo.setVisibility(View.VISIBLE);
                            bankIFSCCOde.setVisibility(View.VISIBLE);
                            submit.setVisibility(View.VISIBLE);
                            setPaymentMode(bank.getText().toString());
                        }
                    });
                    payU.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bank.setVisibility(View.GONE);
                            payTM.setVisibility(View.GONE);
                            payU.setVisibility(View.GONE);
                            payu.setVisibility(View.VISIBLE);
                            submit.setVisibility(View.VISIBLE);
                            setPaymentMode(payU.getText().toString());
                        }
                    });
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bank.setVisibility(View.VISIBLE);
                            payTM.setVisibility(View.VISIBLE);
                            payU.setVisibility(View.VISIBLE);
                            payTm.setVisibility(View.GONE);
                            payu.setVisibility(View.GONE);
                            bankName.setVisibility(View.GONE);
                            bankAccountNo.setVisibility(View.GONE);
                            bankCorrectAccountNo.setVisibility(View.GONE);
                            bankIFSCCOde.setVisibility(View.GONE);
                            submit.setVisibility(View.GONE);
                        }
                    });
                    submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            boolean flag = true;
                            if (getPaymentMode().equals("PayTM Wallet")){
                                Toast.makeText(getContext(),"Customer Payment Mode is "+ getPaymentMode(), Toast.LENGTH_SHORT).show();
                                if (!MyValidator.isValidMobile(payTm)) {
                                    flag = false;
                                }else {
                                    if (flag) {
                                        dialog.dismiss();
                                        orderRegistration(Config.ORDER_Registration_URL,adapter.getMobile(),address,adapter.getAddress3(),getPaymentMode(),payTm.getText().toString());
                                    }
                                }

                            }
                            if(getPaymentMode().equals("PayU Money Wallet")){
                                Toast.makeText(getContext(),"Customer Payment Mode is "+ getPaymentMode(), Toast.LENGTH_SHORT).show();
                                if (!MyValidator.isValidMobile(payu)) {
                                    flag = false;
                                }else {
                                    if (flag) {
                                        dialog.dismiss();
                                        orderRegistration(Config.ORDER_Registration_URL,adapter.getMobile(),address,adapter.getAddress3(),getPaymentMode(),payu.getText().toString());
                                    }
                                }
                            }
                            if(getPaymentMode().equals("Bank Transfer")){
                                Toast.makeText(getContext(),"Customer Payment Mode is "+ getPaymentMode(), Toast.LENGTH_SHORT).show();
                                if (!MyValidator.isValidRequired(bankIFSCCOde)) {
                                    flag = false;
                                }if (!MyValidator.isValidRequired(bankIFSCCOde)) {
                                    flag = false;
                                }if(!bankAccountNo.getText().toString().equals(bankCorrectAccountNo.getText().toString())){
                                    flag = false;
                                    Toast.makeText(getContext(), "Account Number Don't Match, Try again", Toast.LENGTH_SHORT).show();
                                }if(!MyValidator.isValidAccount(bankAccountNo)){
                                    flag = false;
                                }if(!MyValidator.isValidAccount(bankCorrectAccountNo)){
                                    flag = false;
                                }else {
                                    if (flag) {
                                        dialog.dismiss();
                                        String details = bankName.getHint().toString()+" : "+bankName.getText().toString()+"\n"+bankAccountNo.getHint().toString()+" : "+bankAccountNo.getText().toString()+"\n"+bankIFSCCOde.getHint().toString()+" : "+bankIFSCCOde.getText().toString();
                                        orderRegistration(Config.ORDER_Registration_URL,adapter.getMobile(),address,adapter.getAddress3(),getPaymentMode(),details);
                                    }
                                }
                            }
                        }
                    });
                    dialog.show();
                }
                break;
            case R.id._button_Edit:
                final Dialog dialog = new Dialog(getContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.addinfodialog);
                dialog.setCancelable(false);
                final EditText userName = (EditText) dialog.findViewById(R.id._UserName);
                userName.setText(username);
                final EditText userMobile = (EditText) dialog.findViewById(R.id._Contact);
                userMobile.setText(adapter.getMobile());
                final EditText userAddress1 = (EditText) dialog.findViewById(R.id._Address1);
                userAddress1.setText(adapter.getAddress1());
                final EditText userAddress2 = (EditText) dialog.findViewById(R.id._Address2);
                userAddress2.setText(adapter.getAddress2());
                final EditText userAddress3 = (EditText) dialog.findViewById(R.id._Address3);
                userAddress3.setText(adapter.getAddress3());
                countryName = adapter.getCountry();
                stateName = adapter.getState();
                cityName = adapter.getCity();
                dataCountry = new ArrayList<>();
                dataState = new ArrayList<>();
                dataCity = new ArrayList<>();
                country = (Spinner) dialog.findViewById(R.id.spinner_country);
                dataCountry.add("Select");
                dataState.add("Select");
                dataCity.add("Select");
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
                        SessionManager manager = new SessionManager(getContext());
                        HashMap<String,String> log = manager.getUserDetails();
                        userEmail = log.get(SessionManager.KEY_EMAIL);
                        updateAddress(userEmail,userName.getText().toString(),userMobile.getText().toString(),userAddress1.getText().toString(),userAddress2.getText().toString(),userAddress3.getText().toString(),String.valueOf(city.getSelectedItem()),String.valueOf(state.getSelectedItem()), String.valueOf(country.getSelectedItem()),adapter.getId());
                        dialog.dismiss();
                        addressModelArrayList.clear();
                        getAddress(Config.GETALLADDRESS+userEmail);
                    }
                });
                dialog.show();
                break;
            case  R.id._button_Delete:
                deleteAddress(adapter.getId());
                addressModelArrayList.clear();
                getAddress(Config.GETALLADDRESS+userEmail);
                break;
            case R.id.add_address:
                final Dialog mdialog = new Dialog(getContext());
                mdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mdialog.setContentView(R.layout.addinfodialog);
                mdialog.setCancelable(false);
                final EditText Name = (EditText) mdialog.findViewById(R.id._UserName);
                final EditText Mobile = (EditText) mdialog.findViewById(R.id._Contact);
                final EditText Address1 = (EditText) mdialog.findViewById(R.id._Address1);
                final EditText Address2 = (EditText) mdialog.findViewById(R.id._Address2);
                final EditText Address3 = (EditText) mdialog.findViewById(R.id._Address3);
                dataCountry = new ArrayList<>();
                dataState = new ArrayList<>();
                dataCity = new ArrayList<>();
                country = (Spinner) mdialog.findViewById(R.id.spinner_country);
                country();
                state = (Spinner) mdialog.findViewById(R.id.spinner_state);
                city = (Spinner) mdialog.findViewById(R.id.spinner_city);
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
                ImageView close_window = (ImageView) mdialog.findViewById(R.id.windowClose_Edit);
                close_window.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mdialog.cancel();
                    }
                });
                Button mbtn_send = (Button) mdialog.findViewById(R.id.submit_INFO);
                mbtn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean flag = true;
                        if(!MyValidator.isValidRequired(Name)){
                            flag = false;
                        }
                        if(!MyValidator.isValidMobile(Mobile)){
                            flag = false; ;
                        }
                        if(!MyValidator.isValidRequired(Address1)){
                            flag = false;
                        }
                        if(!MyValidator.isValidRequired(Address2)){
                            flag = false;
                        }
                        if(!MyValidator.isValidRequired(Address3)){
                            flag = false;
                        }
                        if(Address1.getText().toString().endsWith(",")){
                            flag = false;
                            Toast.makeText(getContext(), "Don't put Comma at end", Toast.LENGTH_SHORT).show();
                        }
                        if(Address2.getText().toString().endsWith(",")){
                            flag = false;
                            Toast.makeText(getContext(), "Don't put Comma at end", Toast.LENGTH_SHORT).show();
                        }
                        if(Address3.getText().toString().endsWith(",")){
                            flag = false;
                            Toast.makeText(getContext(), "Don't put Comma at end", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            if(flag) {
                                addAdreess(userEmail,Name.getText().toString(),Mobile.getText().toString(),Address1.getText().toString()+",",Address2.getText().toString()+",",Address3.getText().toString()+",",String.valueOf(city.getSelectedItem()),String.valueOf(state.getSelectedItem()), String.valueOf(country.getSelectedItem()));
                                mdialog.dismiss();
                                addressModelArrayList.clear();

                            }
                        }
                    }
                });
                mdialog.show();
                break;
        }

    }

    public class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private Drawable mDivider;
        private boolean mShowFirstDivider = false;
        private boolean mShowLastDivider = false;


        public DividerItemDecoration(Context context, AttributeSet attrs) {
            final TypedArray a = context
                    .obtainStyledAttributes(attrs, new int[]{android.R.attr.listDivider});
            mDivider = a.getDrawable(0);
            a.recycle();
        }

        public DividerItemDecoration(Context context, AttributeSet attrs, boolean showFirstDivider,
                                     boolean showLastDivider) {
            this(context, attrs);
            mShowFirstDivider = showFirstDivider;
            mShowLastDivider = showLastDivider;
        }

        public DividerItemDecoration(Drawable divider) {
            mDivider = divider;
        }

        public DividerItemDecoration(Drawable divider, boolean showFirstDivider,
                                     boolean showLastDivider) {
            this(divider);
            mShowFirstDivider = showFirstDivider;
            mShowLastDivider = showLastDivider;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            if (mDivider == null) {
                return;
            }
            if (parent.getChildPosition(view) < 1) {
                return;
            }

            if (getOrientation(parent) == LinearLayoutManager.VERTICAL) {
                outRect.top = mDivider.getIntrinsicHeight();
            } else {
                outRect.left = mDivider.getIntrinsicWidth();
            }
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            if (mDivider == null) {
                super.onDrawOver(c, parent, state);
                return;
            }

            // Initialization needed to avoid compiler warning
            int left = 0, right = 0, top = 0, bottom = 0, size;
            int orientation = getOrientation(parent);
            int childCount = parent.getChildCount();

            if (orientation == LinearLayoutManager.VERTICAL) {
                size = mDivider.getIntrinsicHeight();
                left = parent.getPaddingLeft();
                right = parent.getWidth() - parent.getPaddingRight();
            } else { //horizontal
                size = mDivider.getIntrinsicWidth();
                top = parent.getPaddingTop();
                bottom = parent.getHeight() - parent.getPaddingBottom();
            }

            for (int i = mShowFirstDivider ? 0 : 1; i < childCount; i++) {
                View child = parent.getChildAt(i);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                if (orientation == LinearLayoutManager.VERTICAL) {
                    top = child.getTop() - params.topMargin;
                    bottom = top + size;
                } else { //horizontal
                    left = child.getLeft() - params.leftMargin;
                    right = left + size;
                }
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }

            // show last divider
            if (mShowLastDivider && childCount > 0) {
                View child = parent.getChildAt(childCount - 1);
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                if (orientation == LinearLayoutManager.VERTICAL) {
                    top = child.getBottom() + params.bottomMargin;
                    bottom = top + size;
                } else { // horizontal
                    left = child.getRight() + params.rightMargin;
                    right = left + size;
                }
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

        private int getOrientation(RecyclerView parent) {
            if (parent.getLayoutManager() instanceof LinearLayoutManager) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
                return layoutManager.getOrientation();
            } else {
                throw new IllegalStateException(
                        "DividerItemDecoration can only be used with a LinearLayoutManager.");
            }
        }
    }

    private void deleteAddress(final String id) {
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.DELETE_ADDRESS_URL+id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        Toast.makeText(getContext().getApplicationContext(),response,Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pd.dismiss();
                        Toast.makeText(getContext().getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(getContext().getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void updateAddress(final String userEmail, final String userName, final String userMobile, final String address1, final String address2, final String address3, final String city, final String state, final String country, final String id) {

        pd.show();
        Log.d("url", Config.UPDATE_ADDRESS_URL);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.UPDATE_ADDRESS_URL,
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
                params.put(Config.KEY_USERADDRESS1,address1);
                params.put(Config.KEY_USERADDRESS2,address2);
                params.put(Config.KEY_USERADDRESS3,address3);
                params.put(Config.KEY_USERCITY,city);
                params.put(Config.KEY_USERSTATE,state);
                params.put(Config.KEY_USERCOUNTRY,country);
                params.put(Config.KEY_USERID,id);
                Log.d("url",params.toString());
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void orderRegistration(String url, String mobile, String address, final String landmark, final String paymentMode, final String paymentDetails){

        Log.d("url : ", "" + url);
        final String userName = username;
        final String useremail = userEmail;
        final String order = userOrderId;
        final String userContact = mobile;
        final String userAddress = address;
        Log.d("url",useremail+" "+order+" "+userContact+" "+userContact);
        pd.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pd.dismiss();
                        Toast.makeText(getContext(),response,Toast.LENGTH_LONG).show();
                        final Dialog dialog = new Dialog(getContext());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.orderiddialog);
                        dialog.setCancelable(false);
                        TextView show = (TextView) dialog.findViewById(R.id.text_show);
                        show.setText(order);
                        TextView btn_send = (TextView) dialog.findViewById(R.id.text_submit);
                        btn_send.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
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
                params.put(Config.KEY_USEREMAIL,useremail);
                params.put(Config.KEY_ORDERID,order);
                params.put(Config.KEY_USERCONTACT,userContact);
                params.put(Config.KEY_USERADDRESS,userAddress);
                params.put(Config.KEY_LANDMARK,landmark);
                params.put(Config.KEY_ORDERPAYMENTMODE,paymentMode);
                params.put(Config.KEY_ORDERPAYMENTDETAILS,paymentDetails);
                Log.d("url",params.toString());
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
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
        int pos = dataCityAdapter.getPosition(cityName);
        city.setSelection(pos);
    }
}

