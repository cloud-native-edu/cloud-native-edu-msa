package com.samsungsds.checkoutservice.order;

import com.samsungsds.checkoutservice.payment.CreditCardInfo;
import com.samsungsds.checkoutservice.shipping.Address;

public class OrderRequest {
    private String emailAddress;
    private Address address;
    private CreditCardInfo creditCardInfo;

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public CreditCardInfo getCreditCardInfo() {
        return creditCardInfo;
    }

    public void setCreditCardInfo(CreditCardInfo creditCardInfo) {
        this.creditCardInfo = creditCardInfo;
    }

    @Override
    public String toString() {
        return "OrderRequest{" +
                "emailAddress='" + emailAddress + '\'' +
                ", address=" + address +
                ", creditCardInfo=" + creditCardInfo +
                '}';
    }
}