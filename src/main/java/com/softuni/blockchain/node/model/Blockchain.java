package com.softuni.blockchain.node.model;

import java.util.LinkedList;
import java.util.List;

public class Blockchain {
    private List<Block> blocks;


    public Blockchain(List<Block> blocks) {
        this.blocks = blocks;
    }

    public Blockchain(Block block) {
        this.blocks = new LinkedList<>();
        this.blocks.add(block);
    }

    public Blockchain() {
        this.blocks = new LinkedList<>();
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

    public void addBlock(Block block) {
        this.blocks.add(block);
    }

    public int size() {
        return this.blocks.size();
    }

    public Block getLastBlock() {
        if (blocks.size() != 0) {
            return this.blocks.get(size() - 1);
        }

        return null;
    }

    public Block getFirstBlock() {
        if (blocks.size() != 0) {
            return this.blocks.get(0);
        }

        return null;
    }
}
