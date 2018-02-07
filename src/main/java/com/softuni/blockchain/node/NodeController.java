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

    private List<Transaction> pendingTransactions = new ArrayList<>();
    private Set<Block> unconfirmedBlocks = new HashSet<>();
    private List<Block> blockChain = new ArrayList<>();

    private final WalletController walletController;

    @Autowired
    public NodeController(WalletController walletController) {
        this.walletController = walletController;
        this.blockChain.add(new Block() {{
            setIndex(1);
        }});
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
}
