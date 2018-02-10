package com.softuni.blockchain.node.model;

import com.softuni.blockchain.node.Node;

import java.util.Objects;

public class NodeInfo {
    private String address;
    private String about;
    private long peers;
    private long blocks;
    private long transactions;
    private long minningJobs;
    private int difficulty;

    public NodeInfo(Node node) {
        this.address = node.getAddress();
        this.about = node.getAbout();
        this.peers = node.getPeers().size();
        this.blocks = node.getBlocks().size();
        this.transactions = node.getTransactions().size();
        this.minningJobs = node.getMiningJobs().size();
        this.difficulty = node.getDifficulty();
    }

    public NodeInfo() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public long getMinningJobs() {
        return minningJobs;
    }

    public void setMinningJobs(long minningJobs) {
        this.minningJobs = minningJobs;
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
                "address='" + address + '\'' +
                ", about='" + about + '\'' +
                ", peers=" + peers +
                ", blocks=" + blocks +
                ", transactions=" + transactions +
                ", miningJobs=" + minningJobs +
                ", difficulty=" + difficulty +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeInfo nodeInfo = (NodeInfo) o;
        return Objects.equals(address, nodeInfo.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }
}
