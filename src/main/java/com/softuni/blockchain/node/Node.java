package com.softuni.blockchain.node;

import com.softuni.blockchain.node.core.NodeController;
import com.softuni.blockchain.node.core.PeerController;
import com.softuni.blockchain.node.model.*;

import java.util.Collection;
import java.util.List;

public class Node {

    private String address;
    private String about;
    private Collection<Peer> peers;
    private List<Block> blocks;
    private List<Transaction> transactions;
    private Balance balance;
    private int difficulty;
    private List<String> miningJobs;

    public Node(PeerController peerController, NodeController nodeController) {
        this.address = nodeController.getAddress();
        this.peers = peerController.getPeers().values();
        this.blocks = nodeController.getBlockChain();
        this.transactions = nodeController.getPendingTransactions();
        this.miningJobs = nodeController.getMiningJobs();
    }

    public String getAddress() {
        return address;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public Collection<Peer> getPeers() {
        return peers;
    }

    public void setPeers(Collection<Peer> peers) {
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

    public List<String> getMiningJobs() {
        return miningJobs;
    }

    public void setMiningJobs(List<String> miningJobs) {
        this.miningJobs = miningJobs;
    }
}