package com.softuni.blockchain.node;

public class Transaction {

    private String from;            //address
    private String to;              //address
    private Double value;           //number
    private String senderPubKey;    //hex_number
    private String senderSignature; //hex_number[2]
    private String transactionHash; //hex_number
    private Long dateReceived;      //timestamp
    private Integer minedInBlockIndex;  //number
    private Boolean paid;           //bool

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getSenderPubKey() {
        return senderPubKey;
    }

    public void setSenderPubKey(String senderPubKey) {
        this.senderPubKey = senderPubKey;
    }

    public String getSenderSignature() {
        return senderSignature;
    }

    public void setSenderSignature(String senderSignature) {
        this.senderSignature = senderSignature;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public Long getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(Long dateReceived) {
        this.dateReceived = dateReceived;
    }

    public Integer getMinedInBlockIndex() {
        return minedInBlockIndex;
    }

    public void setMinedInBlockIndex(Integer minedInBlockIndex) {
        this.minedInBlockIndex = minedInBlockIndex;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }
}
