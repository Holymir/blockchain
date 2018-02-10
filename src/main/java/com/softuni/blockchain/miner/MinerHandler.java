package com.softuni.blockchain.miner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/miner")
public class MinerHandler {

    private MinerController minerController;

    @Autowired
    public MinerHandler(MinerController minerController) {
        this.minerController = minerController;
    }

    @PostMapping("/mine")
    public ResponseEntity mine() {
        this.minerController.setShouldMine(true);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/mine")
    public ResponseEntity stopMine() {
        this.minerController.setShouldMine(false);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/config")
    public ResponseEntity config(@RequestBody Miner miner) {
        this.minerController.setMinerConfig(miner);
        return ResponseEntity.ok().build();
    }
}
