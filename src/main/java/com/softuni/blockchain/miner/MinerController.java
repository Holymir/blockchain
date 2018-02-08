package com.softuni.blockchain.miner;

import org.springframework.stereotype.Component;

@Component
public class MinerController {
    private Miner miner = new Miner();

    public Miner getMiner() {
        return miner;
    }

    public void setMiner(Miner miner) {
        this.miner = miner;
    }
}