package com.softuni.blockchain.miner;

import com.softuni.blockchain.node.Block;
import com.softuni.blockchain.node.NodeController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
public class MinerEngine {

    @Autowired
    private NodeController nodeController;

    public Block mine(Block block) {
        return null;
    }


    @Scheduled(fixedRate = 10_000)
    void setNewBlockFound() {
        this.nodeController.setNewBlockIsFound(Math.random() < 0.5);
    }
}
