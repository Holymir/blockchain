package com.softuni.blockchain.node.socket;

import com.softuni.blockchain.node.Block;
import com.softuni.blockchain.node.Blockchain;
import com.softuni.blockchain.node.NodeInfo;
import com.softuni.blockchain.node.Transaction;

import java.util.List;

public class Message {
    private List<Transaction> transactions;
    private MessageType type;
    private Block block;
    private Blockchain blockchain;
    private NodeInfo nodeInfo;

    // NEW BLOCK MESSAGE
    public Message(MessageType type, Block block) {
        this.type = type;
        this.block = block;
    }

    // NEW TRANSACTIONS MESSAGE
    public Message(MessageType type, List<Transaction> transactions) {
        this.type = type;
        this.transactions = transactions;
    }

    // GET_CHAIN RESPONSE MESSAGE
    public Message(MessageType type, Blockchain blockchain) {
        this.type = type;
        this.blockchain = blockchain;
    }

    // STATUS_RESPONSE MESSAGE
    public Message(MessageType type, NodeInfo nodeInfo) {
        this.type = type;
        this.nodeInfo = nodeInfo;
    }

    public Message(MessageType type, int from, int to) {
        this.type = type;
    }

    public Message(MessageType type) {
        this.type = type;
    }

    public Message() {
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public Blockchain getBlockchain() {
        return blockchain;
    }

    public void setBlockchain(Blockchain blockchain) {
        this.blockchain = blockchain;
    }

    public NodeInfo getNodeInfo() {
        return nodeInfo;
    }

    public void setNodeInfo(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
