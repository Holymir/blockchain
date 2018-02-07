package com.softuni.blockchain.node;


import com.softuni.blockchain.miner.MinerEngine;
import com.softuni.blockchain.utils.Utils;
import com.softuni.blockchain.wallet.Crypto;
import com.softuni.blockchain.wallet.WalletController;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class NodeController {

    private List<Transaction> pendingTransactions;
    private Set<Block> unconfirmedBlocks;
    private List<Block> blockChain;
    private Block candidateBlock;

    private final WalletController walletController;


    @Autowired
    public NodeController(WalletController walletController) {
        this.walletController = walletController;
        this.pendingTransactions = new ArrayList<>();
        this.unconfirmedBlocks = new HashSet<>();
        this.blockChain = new ArrayList<>();
        this.blockChain.add(generateGenesisBlock());
    }

    public Transaction createTransaction(Transaction transaction) {
        this.verifyTransaction(transaction.getCorePart());
        this.pendingTransactions.add(transaction);
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

    public Block getLastBlock() {
        return this.blockChain.get(this.blockChain.size() - 1);
    }

    public Set<Block> getUnconfirmedBlocks() {
        return this.unconfirmedBlocks;
    }

    public boolean verifyBlock(Block block) {
        return this.getLastBlock().getIndex() + 1 == block.getIndex();
    }

    public List<Block> getBlockChain() {
        return blockChain;
    }

    @Autowired
    private MinerEngine minerEngine;

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

    private Block generateGenesisBlock() {

        Block block = new Block();
        block.setIndex(0);
        block.setBlockHash("0x" + Hex.toHexString(Crypto.sha256(Utils.serialize(block).getBytes())));
        return block;
    }
}
