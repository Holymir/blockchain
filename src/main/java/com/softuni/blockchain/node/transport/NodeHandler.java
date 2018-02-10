package com.softuni.blockchain.node.transport;

import com.softuni.blockchain.node.*;
import com.softuni.blockchain.node.core.NodeController;
import com.softuni.blockchain.node.core.PeerController;
import com.softuni.blockchain.node.model.*;
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

    @GetMapping("/node/transactions/pending")
    public List<Transaction> getPendingTransaction() {
        return this.nodeController.getPendingTransactions();
    }

    @PostMapping("/node/transactions")
    public Transaction createTransaction(@RequestBody Transaction transaction) {
        return this.nodeController.createTransaction(transaction);
    }

    @GetMapping("/mining/get-block/{minerAddress}")
    public MiningJob getBlockForMining(@PathVariable String minerAddress) {
        return this.nodeController.createMiningJob(minerAddress);
    }

    @PostMapping("/mining/submit-block/{miningJob}")
    public ResponseEntity submitBlock(@PathVariable String miningJob, @RequestBody Block block) {
        this.nodeController.acceptBlockFromMiner(miningJob, block);
        return ResponseEntity.ok().build();
    }
}
