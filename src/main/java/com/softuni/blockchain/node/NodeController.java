package com.softuni.blockchain.node;


import com.softuni.blockchain.miner.MinerEngine;
import com.softuni.blockchain.utils.Utils;
import com.softuni.blockchain.wallet.Crypto;
import com.softuni.blockchain.wallet.WalletController;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class NodeController {

    private List<Transaction> pendingTransactions;
    private List<Block> blockChain;
    private Block candidateBlock;

    public NodeController() {
        pendingTransactions = new ArrayList<>();
        this.blockChain = new ArrayList<>();
        this.blockChain.add(generateGenesisBlock());

    }

    private Block generateGenesisBlock() {

        Block block = new Block();
        block.setIndex(0);
        block.setBlockHash("0x" + Hex.toHexString(Crypto.sha256(Utils.serialize(block).getBytes())));
        return block;
    }

    @Scheduled(fixedDelay = 5000)
    private void checkForNewCandidateBlock() {

        createCandidateBlock();
    }

    @Autowired
    private WalletController walletController;

    @Autowired
    private MinerEngine minerEngine;

    public Transaction createTransaction(Transaction transaction) {
        // 1. validate Transaction
        this.verifyTransaction(transaction.getCorePart());

        // 3. add to pending transactions
        pendingTransactions.add(transaction);

        transaction.setDateReceived(Instant.now().toEpochMilli());
        transaction.setTransactionHash("0x" + Hex.toHexString(Crypto.sha256(Utils.serialize(transaction).getBytes())));

        return transaction;
    }

    private void verifyTransaction(Transaction transaction) {
        this.walletController.verify(Utils.serialize(transaction), transaction.getSenderSignature(), transaction.getSenderPubKey());
    }

    public List<Transaction> getPendingTransactions() {
        return this.pendingTransactions;
    }

    public void createCandidateBlock() {

        if (candidateBlock == null) {
            int fee = 5;
            candidateBlock = new Block();
            candidateBlock.setIndex(blockChain.size());
            candidateBlock.setExpectedReward(fee);
            candidateBlock.setBlockDataHash("0x" + Hex.toHexString(Crypto.sha256(Utils.serialize(pendingTransactions).getBytes())));
            candidateBlock.setTransactions(pendingTransactions);
            pendingTransactions.clear();

            minerEngine.mine(candidateBlock);
        }
    }

    public List<Block> getBlockChain() {
        return blockChain;
    }
}
