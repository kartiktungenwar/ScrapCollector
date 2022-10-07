package com.techflux.oyebhangarwala.dataModel;

/**
 * Created by Lenovo on 06/05/2017.
 */
public class Product {

    String userName;
    String productType;
    String orderId;
    String productId;
    String productName;
    String productQuantity;

    String productEdit;
    int productImage;

    public Product() {
        super();
    }

    public Product(String productType, String productName, int productImage, String productQuantity) {
        super();
        this.productType = productType;
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.productImage = productImage;
    }

    public Product(String userName, String orderId, String productId, String productType, String productName, String productQuantity,String productEdit) {
        this.userName = userName;
        this.productType = productType;
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.productEdit = productEdit;
    }


    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductImage() {
        return productImage;
    }

    public void setProductImage(int productImage) {
        this.productImage = productImage;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductEdit() {
        return productEdit;
    }

    public void setProductEdit(String productEdit) {
        this.productEdit = productEdit;
    }
}

