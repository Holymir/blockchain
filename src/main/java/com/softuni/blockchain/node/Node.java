package com.softuni.blockchain.node;

public class Node {

    private Peer peers;
    private Block blocks;
    private Transaction transactions;
    private Balance balance;
    private MiningJob miningJobs;

    public Peer getPeers() {
        return peers;
    }

    public void setPeers(Peer peers) {
        this.peers = peers;
    }

    public Block getBlocks() {
        return blocks;
    }

    public void setBlocks(Block blocks) {
        this.blocks = blocks;
    }

    public Transaction getTransactions() {
        return transactions;
    }

    public void setTransactions(Transaction transactions) {
        this.transactions = transactions;
    }

    public Balance getBalance() {
        return balance;
    }

    public void setBalance(Balance balance) {
        this.balance = balance;
    }

    public MiningJob getMiningJobs() {
        return miningJobs;
    }

    public void setMiningJobs(MiningJob miningJobs) {
        this.miningJobs = miningJobs;
    }
}
