package com.vrnitsolution.healthapp.model;

import com.google.firebase.Timestamp;

public class PaymentIntentModel {
    String pay_id;
    String pay_type;
    Timestamp pay_date;
    Double pay_amount;
    String doc_id;
    String cust_id;
    String pay_status;

    public PaymentIntentModel() {
    }

    public PaymentIntentModel(String pay_id, String pay_type, Timestamp pay_date, Double pay_amount, String doc_id, String cust_id, String pay_status) {
        this.pay_id = pay_id;
        this.pay_type = pay_type;
        this.pay_date = pay_date;
        this.pay_amount = pay_amount;
        this.doc_id = doc_id;
        this.cust_id = cust_id;
        this.pay_status = pay_status;
    }

    public String getPay_id() {
        return pay_id;
    }

    public void setPay_id(String pay_id) {
        this.pay_id = pay_id;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }

    public Timestamp getPay_date() {
        return pay_date;
    }

    public void setPay_date(Timestamp pay_date) {
        this.pay_date = pay_date;
    }

    public Double getPay_amount() {
        return pay_amount;
    }

    public void setPay_amount(Double pay_amount) {
        this.pay_amount = pay_amount;
    }

    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }

    public String getCust_id() {
        return cust_id;
    }

    public void setCust_id(String cust_id) {
        this.cust_id = cust_id;
    }

    public String getPay_status() {
        return pay_status;
    }

    public void setPay_status(String pay_status) {
        this.pay_status = pay_status;
    }
}