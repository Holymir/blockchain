package com.softuni.blockchain.node.model;


public class MiningJob {

    private String id;
    private String miner;
    private Block block;
    private Long expectedReward;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMiner() {
        return miner;
    }

    public void setMiner(String miner) {
        this.miner = miner;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public Long getExpectedReward() {
        return expectedReward;
    }

    public void setExpectedReward(Long expectedReward) {
        this.expectedReward = expectedReward;
    }
}
