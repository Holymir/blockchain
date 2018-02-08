package com.softuni.blockchain.miner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MinerHandler {

    @Autowired
    MinerController minerController;

    @PostMapping("/minerAddress")
    public void addressToAdd(@RequestBody String address) {
        this.minerController.getMiner().setAddress(address);
    }

    @GetMapping("/minerAddress")
    public String showAddress() {
        return this.minerController.getMiner().getAddress();
    }
}
