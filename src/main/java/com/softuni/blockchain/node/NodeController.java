package com.softuni.blockchain.node;


import com.softuni.blockchain.utils.Utils;
import com.softuni.blockchain.wallet.WalletController;
import com.softuni.blockchain.wallet.crypto.HashUtil;
import org.spongycastle.util.encoders.Hex;
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

        this.blockChain = Collections.synchronizedList(new ArrayList<>());
        this.unconfirmedBlocks = Collections.synchronizedSet(new TreeSet<>());

        this.pendingTransactions = new ArrayList<>();
        this.statuses = new HashSet<>();
        this.blockChain.add(generateGenesisBlock());
    }

    public Transaction createTransaction(Transaction transaction) {
        if (!this.verifyTransaction(transaction)) {
            throw new IllegalArgumentException("NOT VALID TRANSACTION!");
        }
        this.pendingTransactions.add(transaction);
        transaction.setDateReceived(Instant.now().toEpochMilli());
        transaction.setTransactionHash("0x" + Hex.toHexString(HashUtil.sha256(Utils.serialize(transaction).getBytes())));

        return transaction;
    }

    private boolean verifyTransaction(Transaction transaction) {
        return this.walletController.verify(Utils.serialize(transaction.getCorePart()), transaction.getSenderSignature(), transaction.getSenderPubKey());
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

    public Block getCandidateBlock() {
        return candidateBlock;
    }

    // VerifyBlock
    public boolean verifyBlock(Block block) {

        boolean oldNextIndex = this.getLastBlock().getIndex() + 1 == block.getIndex();
        boolean oldNewHash = this.getLastBlock().getBlockHash().equals(block.getPrevBlockHash());

        boolean checkTransactionHash = transactionHash(block);
        boolean checkBlockHash = blockHashChecker(block);

        if (!oldNewHash) {
            return false;
        }
        if (!oldNextIndex) {
            return false;
        }
        if (!checkBlockHash) {
            return false;
        }
        if (!checkTransactionHash) {
            return false;
        }
        return true;
    }

    public boolean verifyBlock(Block previousBlock, Block block) {

        boolean oldNextIndex = previousBlock.getIndex() + 1 == block.getIndex();
        boolean oldNewHash = previousBlock.getBlockHash().equals(block.getPrevBlockHash());

        boolean checkTransactionHash = transactionHash(block);
        boolean checkBlockHash = blockHashChecker(block);

        if (!oldNewHash) {
            return false;
        }
        if (!oldNextIndex) {
            return false;
        }
        if (!checkBlockHash) {
            return false;
        }
        if (!checkTransactionHash) {
            return false;
        }

        return true;
    }

    private boolean blockHashChecker(Block block) {
        String blockHash = block.getIndex() + block.getBlockDataHash() + block.getPrevBlockHash() + block.getDateCreated() + block.getNonce();
        String hashToCheck = Hex.toHexString(HashUtil.sha256(blockHash.getBytes()));
        return hashToCheck.equals(block.getBlockHash());
    }

    private boolean transactionHash(Block block) {
        for (Transaction tr : block.getTransactions()) {
            if (!verifyTransaction(tr)) {
                return false;
            }
        }
        return true;
    }

    public List<Block> getBlockChain() {
        return blockChain;
    }

    private Block generateGenesisBlock() {

        Block block = new Block();
        block.setIndex(0);
        block.setBlockHash("0x" + Hex.toHexString(HashUtil.sha256(Utils.serialize(block).getBytes())));
        block.setDateCreated(0L);
        return block;
    }

    public void setCandidateBlock(Block candidateBlock) {
        this.candidateBlock = candidateBlock;
    }

    synchronized public Set<NodeInfo> getStatuses() {
        return statuses;
    }
}
