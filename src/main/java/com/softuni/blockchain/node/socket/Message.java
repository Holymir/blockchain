package com.softuni.blockchain.node.socket;

import com.softuni.blockchain.node.Block;
import com.softuni.blockchain.node.NodeInfo;

import java.util.List;

public class Message {
    private MessageType type;
    private Block block;
    private List<Block> blockchain;
    private NodeInfo nodeInfo;

    // NEW BLOCK MESSAGE
    public Message(MessageType type, Block block) {
        this.type = type;
        this.block = block;
    }

    // GET_CHAIN RESPONSE MESSAGE
    public Message(MessageType type, List<Block> blockchain) {
        this.type = type;
        this.blockchain = blockchain;
    }

    // STATUS_RESPONSE MESSAGE
    public Message(MessageType type, NodeInfo nodeInfo) {
        this.type = type;
        this.nodeInfo = nodeInfo;
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

    public List<Block> getBlockchain() {
        return blockchain;
    }

    public void setBlockchain(List<Block> blockchain) {
        this.blockchain = blockchain;
    }

    public NodeInfo getNodeInfo() {
        return nodeInfo;
    }

    public void setNodeInfo(NodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }
}
