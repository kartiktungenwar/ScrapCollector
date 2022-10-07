package com.techflux.oyebhangarwala.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.techflux.oyebhangarwala.R;
import com.techflux.oyebhangarwala.cogfigData.Config;
import com.techflux.oyebhangarwala.dataModel.Product;
import com.techflux.oyebhangarwala.manager.SessionManager;
import com.techflux.oyebhangarwala.validation.MyValidator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lenovo on 04/05/2017.
 */
public class RecyclerViewListGroupProduct extends RecyclerView.Adapter<RecyclerViewListGroupProduct.MyViewHolder> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    String orderId = "OrderId_"+dateFormat.format(new Date());
    private Context mContext;
    List<Product> products;
    SessionManager sessionManager;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public CheckBox thumbnailCheck;
        public Button mLinearLayout;

        public MyViewHolder(View view) {
            super(view);
            thumbnail = (ImageView) view.findViewById(R.id.imageView_group);
            thumbnailCheck = (CheckBox) view.findViewById(R.id.imageView_check);
            mLinearLayout = (Button) view.findViewById(R.id.groupLinearLayout);
        }
    }

    public RecyclerViewListGroupProduct(Context mContext, List<Product> products) {
        this.mContext = mContext;
        this.products = products;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gridview_group_product, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Product product =  products.get(position);
        holder.thumbnail.setImageResource(product.getProductImage());
        holder.thumbnailCheck.setVisibility(View.GONE);
        holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Product product =  products.get(position);
                final Dialog dialogView = new Dialog(mContext);
                sessionManager = new SessionManager(mContext);
                HashMap<String, String> user = sessionManager.getUserDetails();
                final String name = user.get(SessionManager.KEY_EMAIL);
                dialogView.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogView.setContentView(R.layout.groupdialogbox);
                dialogView.setCancelable(false);
                TextView productTitle = (TextView) dialogView.findViewById(R.id.textViewDialogTitle);
                productTitle.setText(product.getProductName());
                final EditText productId = (EditText) dialogView.findViewById(R.id.editTextProductId);
                productId.setText(product.getProductQuantity());
                final EditText productQuantity = (EditText) dialogView.findViewById(R.id.editTextProductQuantity);
                ImageView cancel = (ImageView)dialogView.findViewById(R.id.dialog_windowclose);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogView.dismiss();
                    }
                });
                Button btnSubmit = (Button) dialogView.findViewById(R.id.submitDialog);
                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean flag = true;
                        if (!MyValidator.isValidRequired(productQuantity)) {
                            flag = false;

                        }
                        else {
                            if (flag) {
                                orderUser(orderId, name, product.getProductType(), product.getProductName(), productQuantity.getText().toString().trim(), productId.getText().toString());
                                dialogView.dismiss();
                                holder.thumbnailCheck.setVisibility(View.VISIBLE);
                                holder.thumbnailCheck.setChecked(true);
                            }
                        }
                    }
                });
                dialogView.show();
            }
        });

    }


    @Override
    public int getItemCount() {
        return products.size();
    }


    public void orderUser(final String orderId, final String username, final String productType, final String productName, final String productQuantity, final String bitmap) {
        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.ORDER_ADD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Tag",response);
                        progressDialog.dismiss();
                        Toast.makeText(mContext.getApplicationContext(), response, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(mContext.getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Config.KEY_ORDERID,orderId);
                params.put(Config.KEY_USEREMAIL, username);
                params.put(Config.KEY_PRODUCTYPE,productType);
                params.put(Config.KEY_PRODUCTNAME,productName);
                params.put(Config.KEY_PRODUCTQUANTITY,productQuantity);
                params.put(Config.KEY_PRODUCTID, bitmap);
                Log.d("url", String.valueOf(params));
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        requestQueue.add(stringRequest);
    }

}
