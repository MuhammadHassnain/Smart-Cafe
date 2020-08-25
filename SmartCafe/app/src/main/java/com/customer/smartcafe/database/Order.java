package com.customer.smartcafe.database;

public class Order {
    private String orderNo;
    private String itemName;
    private String customerName;
    private String price;
    private int paymentMethod;
    private int status;
    private String customerId;

    public Order(String orderNo, String itemName, String customerName, String price, int paymentMethod, int status, String customerId) {
        this.orderNo = orderNo;
        this.itemName = itemName;
        this.customerName = customerName;
        this.price = price;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.customerId = customerId;
    }

    public String getCustomerId() {
        return customerId;
    }





    public String getCustomerName() {
        return customerName;
    }


    public static final int PAYMENT_METHOD_CASH = 0;
    public static final int PAYMENT_METHOD_WALLET = 1;

    public static final int ORDER_STATUS_COMPLETED = 1;
    public static final int ORDER_STATUS_NOT_COMPLETED = 0;



    public Order() {
    }


    public int getStatus() {
        return status;
    }

    public int getPaymentMethod() {
        return paymentMethod;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getItemName() {
        return itemName;
    }

    public String getPrice() {
        return price;
    }


}
