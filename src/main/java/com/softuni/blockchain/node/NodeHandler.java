package com.softuni.blockchain.node;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class NodeHandler {

    @Autowired
    private PeerControler pr;

    @GetMapping("/info")
    public Node nodeInfo() {

        return new Node();
    }

    @GetMapping("/blocks")
    public Block block() {

        return new Block();
    }

    @GetMapping("/blocks/{index}")
    public Block block(@PathVariable Integer index) {
        Block block =  new Block();
        block.setIndex(index);

        return block;
    }

    @GetMapping("/peers")
    public Peer peer() {

        return new Peer();
    }

    @PostMapping("/peers")
    public List<Peer> peer(@RequestBody Peer peer) {

        //this.pr.ping(peer);
        this.pr.addPeer(peer);
        return new ArrayList<>(PeerControler.peers);
    }

    @GetMapping("/balance")
    public Balance balance() {

        return new Balance();
    }

    @GetMapping("/transactions/{transactionHashId}/info")
    public Transaction transaction(@PathVariable String transactionHashId) {
        Transaction transaction =  new Transaction();
        transaction.setTransactionHash(transactionHashId);

        return transaction;
    }

}
