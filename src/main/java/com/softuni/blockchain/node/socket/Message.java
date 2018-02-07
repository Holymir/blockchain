package com.softuni.blockchain.node.socket;

import com.softuni.blockchain.node.Block;

public class Message {
    private MessageType type;
    private Block block;

    public Message(MessageType type, Block block) {
        this.type = type;
        this.block = block;
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
}
