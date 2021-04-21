package com.samsungsds.cartservice;

public class OrderPlaced {
    private String orderId;
   
    protected OrderPlaced() {
      // do nothing
    }
     
    public OrderPlaced(String orderId) {
        this.orderId = orderId;
    }
   
    public String getOrderId() {
        return orderId;
    }
   
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
   
    @Override
    public String toString() {
        return "OrderPlaced{" +
                "orderId='" + orderId + '\'' +
                '}';
    }
}