package com.sharon.sample.mpesa;

public class Payment {
    private String paymentCode;
    private String paymentAmount;
    private String paymentPurpose;

    public void setPaymentCode(String paymentCode) {

        this.paymentCode = paymentCode;
    }

    public void setPaymentAmount(String paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public void setPaymentPurpose(String paymentPurpose) {
        this.paymentPurpose = paymentPurpose;
    }

    public Payment() {
        // Default constructor required for Firebase
    }

    public Payment(String paymentCode, String paymentAmount, String paymentPurpose) {
        this.paymentCode = paymentCode;
        this.paymentAmount = paymentAmount;
        this.paymentPurpose = paymentPurpose;
    }

    // Getter methods
    public String getPaymentCode() {
        return paymentCode;
    }

    public String getPaymentAmount() {
        return paymentAmount;
    }

    public String getPaymentPurpose() {
        return paymentPurpose;
    }
}
