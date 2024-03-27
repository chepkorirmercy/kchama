package com.sharon.mpesa.stkpush.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Fredrick Ochieng on 02/02/2018.
 */

public class STKQuery {

    @SerializedName("CommandID")
    @Expose
    private String commandId = "TransactionStatusQuery";
    @SerializedName("PartyA")
    @Expose
    private String partyA;
    @SerializedName("BusinessShortCode")
    @Expose
    private String businessShortCode;
    @SerializedName("CheckoutRequestID")
    @Expose
    private String checkoutRequestId;
    @SerializedName("Password")
    @Expose
    private String password;
    @SerializedName("Timestamp")
    @Expose
    private String timestamp;
    @SerializedName("ResultURL")
    @Expose
    private String resultUrl = "https://example.com";
    @SerializedName("QueueTimeOutURL")
    @Expose
    private String queueTimeoutUrl = "https://example.com";
    @SerializedName("OriginatorConversationID")
    @Expose
    private String originatorConversationId;


    @SerializedName("TransactionID")
    @Expose
    private String transactionId;
    @SerializedName("IdentifierType")
    @Expose
    private String identifierType = "4";
    @SerializedName("Initiator")
    @Expose
    private String initiator = "testapiuser";
    @SerializedName("Remarks")
    @Expose
    private String remarks = "Ok";
    @SerializedName("Occasion")
    @Expose
    private String occasion = "Ok";

    public String getPartyA() {
        return partyA;
    }

    public void setPartyA(String partyA) {
        this.partyA = partyA;
    }

    public String getBusinessShortCode() {
        return businessShortCode;
    }

    public void setBusinessShortCode(String businessShortCode) {
        this.businessShortCode = businessShortCode;
    }

    public String getCheckoutRequestId(){
        return checkoutRequestId;
    }
    public void setCheckoutRequestId(String checkoutRequestId){
        this.checkoutRequestId = checkoutRequestId;
    }

    public void setTransactionId(String transactionId){
        this.transactionId = transactionId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getOriginatorConversationId() {
        return originatorConversationId;
    }

    public void setOriginatorConversationId(String originatorConversationId) {
        this.originatorConversationId = originatorConversationId;
    }

}