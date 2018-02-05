package com.softuni.blockchain.node;

import com.softuni.blockchain.wallet.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class NodeHandler {

    @Autowired
    private PeerController peerController;

    @Autowired
    private NodeController nodeController;

    @GetMapping("/info")
    public Node nodeInfo() {
        return nodeController.getInfo();
    }

    @GetMapping("/blocks")
    public Block block() {

        return new Block();
    }

    @GetMapping("/blocks/{index}")
    public Block block(@PathVariable Integer index) {
        Block block = new Block();
        block.setIndex(index);

        return block;
    }

    @GetMapping("/peers")
    public List<Peer> peer() {

        return peerController.getPeers();
    }

    @PostMapping("/peers")
    public ResponseEntity peer(@RequestBody Peer peer) {
        this.peerController.addPeer(peer);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/balance")
    public Balance balance() {

        return new Balance();
    }

    @GetMapping("/transactions/{transactionHashId}/info")
    public Transaction transaction(@PathVariable String transactionHashId) {
        Transaction transaction = new Transaction();
        transaction.setTransactionHash(transactionHashId);

        return transaction;
    }

    @PostMapping("/transactions")
    public Transaction createTransaction(@RequestBody RequestTransactionDTO transactionDTO) {
        return this.nodeController.createTransaction(transactionDTO.getTransaction(), transactionDTO.getWallet());
    }
}
