package com.softuni.blockchain.node.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedList;
import java.util.List;

public class Block implements Comparable<Block> {

    private int index;
    private List<Transaction> transactions;
    private double expectedReward;
    private int difficulty;         //number
    private String prevBlockHash;   //hex_number
    private String minedBy;         //address
    private String blockDataHash;   //address


    private Long nonce;             //number
    private Long dateCreated;       //timestamp
    private String blockHash;       //hex_number

    public Block() {
        this.transactions = new LinkedList<>();
    }

    @JsonIgnore
    public Block getBlockForMining() {

        Block block = new Block();
        block.setIndex(this.index);
        block.setExpectedReward(this.expectedReward);
        block.setBlockDataHash(this.blockDataHash);
        block.setDifficulty(this.difficulty);

        return block;
    }

    public double getExpectedReward() {
        return expectedReward;
    }

    public void setExpectedReward(double expectedReward) {
        this.expectedReward = expectedReward;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getPrevBlockHash() {
        return prevBlockHash;
    }

    public void setPrevBlockHash(String prevBlockHash) {
        this.prevBlockHash = prevBlockHash;
    }

    public String getMinedBy() {
        return minedBy;
    }

    public void setMinedBy(String minedBy) {
        this.minedBy = minedBy;
    }

    public String getBlockDataHash() {
        return blockDataHash;
    }

    public void setBlockDataHash(String blockDataHash) {
        this.blockDataHash = blockDataHash;
    }

    public Long getNonce() {
        return nonce;
    }

    public void setNonce(Long nonce) {
        this.nonce = nonce;
    }

    public Long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    @Override
    public String toString() {
        return "Block{" +
                "index=" + index +
                ", minedBy='" + (minedBy == null ?  "null": minedBy.substring(minedBy.length() - 5)) + '\'' +
                ", nonce=" + nonce +
                ", dateCreated=" + dateCreated +
                ", blockHash='" + blockHash.substring(blockHash.length() - 5) + '\'' +
                ", Transactions='" + transactions.size() + '\'' +
                '}';
    }

    @Override
    public int compareTo(Block o) {
        int index = this.index - o.getIndex();
        if (index != 0) {
            return index;
        }

        int difficulty = this.difficulty - o.getDifficulty();
        if (difficulty != 0) {
            return difficulty;
        }

        int dateCreated = this.getDateCreated().compareTo(o.getDateCreated());
        if (dateCreated != 0) {
            return dateCreated;
        }

        return 0;
    }
}
