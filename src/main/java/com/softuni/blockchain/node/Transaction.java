package com.softuni.blockchain.node;

public class Transaction {

    private String from;            //address
    private String to;              //address
    private double value;           //number
    private String senderPubKey;    //hex_number
    private String senderSignature; //hex_number[2]
    private String transactionHash; //hex_number
    private long dateReceived;      //timestamp
    private int minedInBlockIndex;  //number
    private boolean paid;           //bool

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

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
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

    public long getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(long dateReceived) {
        this.dateReceived = dateReceived;
    }

    public int getMinedInBlockIndex() {
        return minedInBlockIndex;
    }

    public void setMinedInBlockIndex(int minedInBlockIndex) {
        this.minedInBlockIndex = minedInBlockIndex;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }
}
