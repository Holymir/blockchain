package com.softuni.blockchain.miner;

import org.springframework.stereotype.Component;

@Component
public class MinerController {

    private boolean shouldMine = false;
    private Miner minerConfig;


    public boolean getShouldMine() {
        return shouldMine;
    }

    public void setShouldMine(boolean shouldMine) {
        this.shouldMine = shouldMine;
    }

    public Miner getMinerConfig() {
        return minerConfig;
    }

    public void setMinerConfig(Miner minerConfig) {
        this.minerConfig = minerConfig;
    }
}
