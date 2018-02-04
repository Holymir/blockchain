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

}
