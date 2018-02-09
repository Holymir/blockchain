package com.softuni.blockchain.node;

import java.util.List;

public class TestBlocks {
    private List<Block> unconfirmedBlocks;
    private List<Block> blockchain;

    public List<Block> getUnconfirmedBlocks() {
        return unconfirmedBlocks;
    }

    public void setUnconfirmedBlocks(List<Block> unconfirmedBlocks) {
        this.unconfirmedBlocks = unconfirmedBlocks;
    }

    public List<Block> getBlockchain() {
        return blockchain;
    }

    public void setBlockchain(List<Block> blockchain) {
        this.blockchain = blockchain;
    }
}
