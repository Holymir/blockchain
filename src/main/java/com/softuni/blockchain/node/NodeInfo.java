package com.softuni.blockchain.node;

import java.util.Objects;

public class NodeInfo {
    private String uuid;
    private String about;
    private long peers;
    private long blocks;
    private long transactions;
    private Balance balance;
    private int difficulty;

    public NodeInfo(Node node) {
        this.uuid = node.getUuid();
        this.about = node.getAbout();
        this.peers = node.getPeers().size();
        this.blocks = node.getBlocks().size();
        this.transactions = node.getTransactions().size();
        this.balance = node.getBalance();
        this.difficulty = node.getDifficulty();
    }

    public NodeInfo() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public long getPeers() {
        return peers;
    }

    public void setPeers(long peers) {
        this.peers = peers;
    }

    public long getBlocks() {
        return blocks;
    }

    public void setBlocks(long blocks) {
        this.blocks = blocks;
    }

    public long getTransactions() {
        return transactions;
    }

    public void setTransactions(long transactions) {
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


    @Override
    public String toString() {
        return "NodeInfo{" +
                "uuid='" + uuid + '\'' +
                ", about='" + about + '\'' +
                ", peers=" + peers +
                ", blocks=" + blocks +
                ", transactions=" + transactions +
                ", balance=" + balance +
                ", difficulty=" + difficulty +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeInfo nodeInfo = (NodeInfo) o;
        return Objects.equals(uuid, nodeInfo.uuid);
    }

    @Override
    public int hashCode() {

        return Objects.hash(uuid);
    }
}
