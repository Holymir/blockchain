package com.softuni.blockchain.node.transport;

import com.softuni.blockchain.node.*;
import com.softuni.blockchain.node.core.NodeController;
import com.softuni.blockchain.node.core.PeerController;
import com.softuni.blockchain.node.model.Balance;
import com.softuni.blockchain.node.model.Block;
import com.softuni.blockchain.node.model.NodeInfo;
import com.softuni.blockchain.node.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class NodeHandler {

    @Autowired
    private NodeController nodeController;

    @Autowired
    private PeerController peerController;

    @GetMapping("/info")
    public NodeInfo nodeInfo() {
        return new NodeInfo(new Node(peerController, nodeController));
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
    public ResponseEntity mined(@RequestBody Block block) {
        this.nodeController.getUnconfirmedBlocks().add(block);
        this.nodeController.setCandidateBlock(null);

        return ResponseEntity.noContent().build();
    }
}
