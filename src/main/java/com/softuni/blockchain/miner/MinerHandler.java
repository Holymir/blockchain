package com.softuni.blockchain.miner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MinerHandler {

    private MinerController minerController;

    @Autowired
    public MinerHandler(MinerController minerController) {
        this.minerController = minerController;
    }

    @PostMapping("/minerAddress")
    public void addressToAdd(@RequestBody String address) {
        this.minerController.getMiner().setAddress(address);
    }

    @GetMapping("/minerAddress")
    public String showAddress() {
        return this.minerController.getMiner().getAddress();
    }

    @PostMapping("/startMine")
    public ResponseEntity startMine() {
        this.minerController.isMinerStarted = true;
        return  ResponseEntity.ok().build();
    }

    @PostMapping("/stopMine")
    public ResponseEntity stopMine() {
        this.minerController.isMinerStarted = false;
        return  ResponseEntity.ok().build();
    }

    @PostMapping("/mineFor")
    public void mineFor(@RequestBody String address) {
        this.minerController.address = address;
    }
}
