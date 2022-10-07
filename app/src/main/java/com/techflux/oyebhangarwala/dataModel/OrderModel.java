package com.techflux.oyebhangarwala.dataModel;

/**
 * Created by Lenovo on 12/05/2017.
 */
public class OrderModel {

    String orderId,orderAction,order;

    public OrderModel(String orderId, String orderAction) {
        this.orderId = orderId;
        this.orderAction = orderAction;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderAction() {
        return orderAction;
    }

    public void setOrderAction(String orderAction) {
        this.orderAction = orderAction;
    }
}
