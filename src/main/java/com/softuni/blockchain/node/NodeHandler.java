package com.softuni.blockchain.node;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class NodeHandler {

    @Autowired
    private PeerController peerController;

    @Autowired
    private NodeController nodeController;



    @GetMapping("/info")
    public NodeInfo nodeInfo() {
        return new NodeInfo(new Node(peerController, nodeController));
    }

    @GetMapping("/peers")
    public List<Peer> peer() {
        return new ArrayList<>(peerController.getPeers().values());
    }

    @PostMapping("/peers")
    public ResponseEntity peer(@RequestBody Peer peer) {
        this.peerController.addPeer(peer);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/peers")
    public ResponseEntity removePeer(@RequestBody Peer peer) throws IOException {
        this.peerController.remove(peer);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/balance")
    public Balance balance() {
        return new Balance();
    }

    @GetMapping("/node/transactions/{transactionHashId}/info")
    public Transaction transaction(@PathVariable String transactionHashId) {
        Transaction transaction = new Transaction();
        transaction.setTransactionHash(transactionHashId);

        return transaction;
    }

    @GetMapping("/node/transactions/pending")
    public List<Transaction> getPendingTransaction() {
        return this.nodeController.getPendingTransactions();
    }

    @PostMapping("/node/transactions")
    public Transaction createTransaction(@RequestBody Transaction transaction) {
        return this.nodeController.createTransaction(transaction);
    }

    @GetMapping("/miningJobs")
    public Block getBlockForMining() {
        return this.nodeController.getCandidateBlock();
    }

    @PostMapping("/mined")
    public ResponseEntity mined(@RequestBody Block block ) {
        this.nodeController.getUnconfirmedBlocks().add(block);
        this.nodeController.setCandidateBlock(null);

        return ResponseEntity.noContent().build();
    }
}
