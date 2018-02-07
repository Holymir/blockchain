package com.softuni.blockchain.node;


import com.softuni.blockchain.utils.Utils;
import com.softuni.blockchain.wallet.Crypto;
import com.softuni.blockchain.wallet.WalletController;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

@Component
public class NodeController {

    private List<Transaction> pendingTransactions;
    private Set<Block> unconfirmedBlocks;
    private List<Block> blockChain;
    private Block candidateBlock;
    private Set<NodeInfo> statuses;

    private final WalletController walletController;


    @Autowired
    public NodeController(WalletController walletController) {
        this.walletController = walletController;
        this.pendingTransactions = new ArrayList<>();
        this.unconfirmedBlocks = new TreeSet<>();
        this.statuses = new HashSet<>();
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

    synchronized public Set<Block> getUnconfirmedBlocks() {
        return this.unconfirmedBlocks;
    }

    public Block getCandidateBlock() {
        return candidateBlock;
    }

    public boolean verifyBlock(Block block) {

        if (this.getLastBlock().getIndex() + 1 == block.getIndex()
                && this.getLastBlock().getBlockHash().equals(block.getPrevBlockHash())) {
            return true;
        }
        return false;
    }

    public List<Block> getBlockChain() {
        return blockChain;
    }

    private Block generateGenesisBlock() {

        Block block = new Block();
        block.setIndex(0);
        block.setBlockHash("0x" + Hex.toHexString(Crypto.sha256(Utils.serialize(block).getBytes())));
        return block;
    }

    public void setCandidateBlock(Block candidateBlock) {
        this.candidateBlock = candidateBlock;
    }

    synchronized public Set<NodeInfo> getStatuses() {
        return statuses;
    }
}
