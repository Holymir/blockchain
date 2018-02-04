package com.softuni.blockchain.node;

import java.util.List;

public class Block {

    private int index;              //number
    private List<Transaction> transactions;   //Transaction[]
    private int difficulty;         //number
    private String prevBlockHash;   //hex_number
    private String minedBy;         //address
    private String blockDataHash;   //address


    private Long nonce;             //number
    private Long dateCreated;       //timestamp
    private String blockHash;       //hex_number

}
