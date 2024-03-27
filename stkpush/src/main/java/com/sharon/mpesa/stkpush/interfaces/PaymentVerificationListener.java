package com.sharon.mpesa.stkpush.interfaces;

public interface PaymentVerificationListener {
    public void onPaymentVerified(String paymentId, boolean isPaid);
    public void onError(Throwable e);
}
