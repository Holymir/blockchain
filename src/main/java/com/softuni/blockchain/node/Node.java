package com.softuni.blockchain.node;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class Node {

    private static final String uuid = DigestUtils.sha256Hex(UUID.randomUUID().toString());
    private String about;
    private Collection<Peer> peers;
    private List<Block> blocks;
    private List<Transaction> transactions;
    private Balance balance;
    private int difficulty;
    private MiningJob miningJobs;

    public Node(PeerController peerController, NodeController nodeController) {
        this.peers = peerController.getPeers().values();
        this.blocks = nodeController.getBlockChain();
        this.transactions = nodeController.getPendingTransactions();
    }

    public static String getUuid() {
        return uuid;
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

    public MiningJob getMiningJobs() {
        return miningJobs;
    }

    public void setMiningJobs(MiningJob miningJobs) {
        this.miningJobs = miningJobs;
    }
}