package com.techflux.oyebhangarwala.adapter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
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
import com.techflux.oyebhangarwala.activity.MainActivity;
import com.techflux.oyebhangarwala.cogfigData.Config;
import com.techflux.oyebhangarwala.dataModel.Product;
import com.techflux.oyebhangarwala.fragment.Order;
import com.techflux.oyebhangarwala.manager.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lenovo on 08/05/2017.
 */
public class RecyclerViewListOrderProduct extends RecyclerView.Adapter<RecyclerViewListOrderProduct.MyViewHolder> {

    private Context mContext;
    ArrayList<Product> products;
    ProgressDialog pd;
    SessionManager manager;
    String name;
    Order mOrder;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewEdit,imageViewDelete,imageViewImage;
        public TextView ordeeType,orderName,orderQuantity;

        public MyViewHolder(View view) {
            super(view);
            manager = new SessionManager(mContext);
            HashMap<String,String> user = manager.getUserDetails();
            name = user.get(manager.KEY_EMAIL);
            imageViewImage = (ImageView) view.findViewById(R.id.imageViewOrder);
            imageViewEdit = (ImageView) view.findViewById(R.id.imageViewEdit);
            imageViewDelete = (ImageView) view.findViewById(R.id.imageViewDelete);
            ordeeType = (TextView) view.findViewById(R.id.orderType);
            orderName = (TextView) view.findViewById(R.id.orderName);
            orderQuantity = (TextView) view.findViewById(R.id.orderQuantity);
            pd = new ProgressDialog(mContext);
            pd.setMessage("Loading...");

        }
    }

    public RecyclerViewListOrderProduct(Context mContext, ArrayList<Product> products) {
        this.mContext = mContext;
        this.products = products;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gridview_order_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final Product product = products.get(position);
        Log.d("Tag",""+product.getProductType()+" "+product.getProductName()+" "+product.getProductQuantity()+" "+product.getOrderId() );
        holder.ordeeType.setText(product.getProductType());
        holder.orderName.setText(product.getProductName());
        holder.orderQuantity.setText(product.getProductQuantity());
        if(product.getProductName().equals("Bottles"))
        {
            holder.imageViewImage.setImageResource(R.drawable.i23);
        }
        else if(product.getProductName().equals("Jars")){
            holder.imageViewImage.setImageResource(R.drawable.i22);
        }
        else if(product.getProductName().equals("Cutlery")){
            holder.imageViewImage.setImageResource(R.drawable.i20);
        }
        else {
            holder.imageViewImage.setImageResource(R.drawable.i21);
        }
        if(product.getProductEdit().equals("true")){
            holder.imageViewEdit.setVisibility(View.GONE);
            holder.imageViewDelete.setVisibility(View.GONE);
        }
        holder.imageViewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(mContext);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.order_edit_dialogbox);
                dialog.setCancelable(false);

                ImageView closewindow = (ImageView) dialog.findViewById(R.id.windowClose_Edit);
                closewindow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                TextView tv_DialogTitle = (TextView) dialog.findViewById(R.id._EditDialogTitle);
                tv_DialogTitle.setText(product.getProductName());
                final EditText et_ProductQuantity = (EditText) dialog.findViewById(R.id._EditProductQuantity);;
                et_ProductQuantity.setText(product.getProductQuantity());
                EditText et_ProductId = (EditText) dialog.findViewById(R.id._EditProductId);
                et_ProductId.setText(product.getProductId());

                Button btn_send = (Button) dialog.findViewById(R.id.submit_Edit);
                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        orderEdit(name,product.getProductId(),et_ProductQuantity.getText().toString(),product.getOrderId());
                        dialog.dismiss();
                        fragmentJump(product.getOrderId());
                    }
                });
                dialog.show();
            }
        });
        holder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(mContext);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.order_delete_dialogbox);
                dialog.setCancelable(false);

                ImageView closewindow = (ImageView) dialog.findViewById(R.id.windowClose_Delete);
                closewindow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });

                Button btn_send = (Button) dialog.findViewById(R.id.submit_Delete);
                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        orderDelete(name,product.getProductId(),product.getOrderId());
                        dialog.dismiss();
                        fragmentJump(product.getOrderId());
                    }
                });
                dialog.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }


    public void orderEdit(final String useremail, final String productId, final String productQuantity, final String orderId){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.ORDER_EDIT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(mContext.getApplicationContext(),response,Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext.getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(Config.KEY_USEREMAIL,useremail);
                params.put(Config.KEY_PRODUCTID,productId);
                params.put(Config.KEY_PRODUCTQUANTITY,productQuantity);
                params.put(Config.KEY_ORDERID,orderId);
                Log.d("url",params.toString());
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void orderDelete(final String useremail,final String productId, final String orderId){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.ORDER_DELETE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("url",Config.ORDER_DELETE_URL);
                        Toast.makeText(mContext.getApplicationContext(),response,Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext.getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put(Config.KEY_USEREMAIL,useremail);
                params.put(Config.KEY_PRODUCTID,productId);
                params.put(Config.KEY_ORDERID,orderId);
                Log.d("url",params.toString());
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void fragmentJump(String orderId) {
        Fragment mFragment = new Order();
        Bundle value = new Bundle();
        value.putString("IndexOrder",orderId);
        value.putString("Task","show");
        mFragment.setArguments(value);
        switchContent(R.id.content_frame, mFragment);
    }

    public void switchContent(int id, Fragment fragment) {
        if (mContext == null)
            return;
        if (mContext instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) mContext;
            Fragment frag = fragment;
            mainActivity.switchContent(id, frag);
        }
    }


}
