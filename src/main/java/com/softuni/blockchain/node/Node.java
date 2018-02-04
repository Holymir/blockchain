package com.softuni.blockchain.node;

import java.util.List;

public class Node {

    private List<Peer> peers;
    private List<Block> blocks;
    private List<Transaction> transactions;
    private Balance balance;
    private int difficulty;
    private MiningJob miningJobs;

    public List<Peer> getPeers() {
        return peers;
    }

    public void setPeers(List<Peer> peers) {
        this.peers = peers;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Balance getBalance() {
        return balance;
    }

    public void setBalance(Balance balance) {
        this.balance = balance;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public MiningJob getMiningJobs() {
        return miningJobs;
    }

    public void setMiningJobs(MiningJob miningJobs) {
        this.miningJobs = miningJobs;
    }
}
