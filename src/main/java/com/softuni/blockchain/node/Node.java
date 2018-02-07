package com.softuni.blockchain.node;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class Node {

    private String uuid;
    private String about;
    private Set<Peer> peers;
    private List<Block> blocks;
    private List<Transaction> transactions;
    private Balance balance;
    private int difficulty;
    private MiningJob miningJobs;

    public Node(PeerController peerController, NodeController nodeController) {
        this.uuid = DigestUtils.sha256Hex(UUID.randomUUID().toString());
        this.peers = peerController.getPeers().keySet();
        this.blocks = nodeController.getBlockChain();
        this.transactions = nodeController.getPendingTransactions();
    }

    public String getUuid() {
        return uuid;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public Set<Peer> getPeers() {
        return peers;
    }

    public void setPeers(Set<Peer> peers) {
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