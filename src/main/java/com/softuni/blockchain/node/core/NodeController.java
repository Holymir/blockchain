package com.softuni.blockchain.node.core;


import com.softuni.blockchain.node.model.Block;
import com.softuni.blockchain.node.model.MiningJob;
import com.softuni.blockchain.node.model.NodeInfo;
import com.softuni.blockchain.node.model.Transaction;
import com.softuni.blockchain.utils.Utils;
import com.softuni.blockchain.wallet.Wallet;
import com.softuni.blockchain.wallet.WalletController;
import com.softuni.blockchain.wallet.crypto.HashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

@Component
public class NodeController {

    private static final Logger logger = LoggerFactory.getLogger("NODE");

    private List<Transaction> pendingTransactions = new ArrayList<>();
    private Set<Block> unconfirmedBlocks = Collections.synchronizedSet(new TreeSet<>());
    private List<Block> blockChain = Collections.synchronizedList(new ArrayList<>());
    private Set<NodeInfo> statuses = new HashSet<>();
    private Wallet nodeWallet;
    private Map<String, Block> miningJobs = new HashMap<>();

    private final WalletController walletController;

    private long reward;
    private int difficulty;

    @Autowired
    public NodeController(WalletController walletController) {
        this.walletController = walletController;
        this.nodeWallet = walletController.generateWallet();
        this.blockChain.add(generateGenesisBlock());

        this.reward = 50;
        this.difficulty = 5;
    }

    public Transaction createTransaction(Transaction transaction) {
        if (!this.verifyTransaction(transaction)) {
            throw new IllegalArgumentException("NOT VALID TRANSACTION!");
        }
        this.pendingTransactions.add(transaction);
        transaction.setDateReceived(Instant.now().toEpochMilli());
        transaction.setTransactionHash(this.getTransactionHash(transaction));

        return transaction;
    }

    private String getTransactionHash(Transaction transaction) {
        return "0x" + Hex.toHexString(HashUtil.sha256(Utils.serialize(transaction).getBytes()));
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

    public MiningJob createMiningJob(String minerAddress) {
        MiningJob miningJob = new MiningJob();
        miningJob.setMiner(minerAddress);
        Block candidateBlock = this.createCandidateBlock(minerAddress);
        miningJob.setBlock(candidateBlock.getBlockForMiner());
        miningJob.setExpectedReward(this.reward);
        String id = Hex.toHexString(HashUtil.sha256(Utils.serialize(miningJob).getBytes()));
        miningJob.setId(id);
        this.miningJobs.put(id, candidateBlock);

        return miningJob;
    }

    public void acceptBlockFromMiner(String miningJob, Block blockReceivedFromMiner) {
        Block blockSentToMiner = this.miningJobs.getOrDefault(miningJob, null);
        if (blockSentToMiner == null) {
            throw new RuntimeException("Job not found!");
        }

        this.unconfirmedBlocks.add(this.generateBlock(blockSentToMiner, blockReceivedFromMiner));
        this.miningJobs.remove(miningJob);
    }

    public List<String> getMiningJobs() {
        return new ArrayList<>(miningJobs.keySet());
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

    // VerifyBlock
    public boolean verifyBlock(Block block, Block previousBlock) {

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
            if (tr.getTransactionHash().equals("coinBased")) {
                return true;
            }
            if (!verifyTransaction(tr)) {
                return false;
            }
        }
        return true;
    }

    public List<Block> getBlockChain() {
        return blockChain;
    }

    public void setBlockChain(List<Block> blockChain) {
        this.blockChain = blockChain;
    }

    private Block generateGenesisBlock() {
        Block block = new Block();
        block.setIndex(0);
        block.setBlockHash("0x" + Hex.toHexString(HashUtil.sha256(Utils.serialize(block).getBytes())));
        block.setDateCreated(0L);
        block.setPrevBlockHash("NONE");
        return block;
    }

    public Set<NodeInfo> getStatuses() {
        return statuses;
    }

    public String getAddress() {
        return this.nodeWallet.getAddress();
    }

    private Block createCandidateBlock(String minerAddress) {
        logger.info("Create candidate block for miner: " + minerAddress);

        ArrayList<Transaction> transactions = new ArrayList<>(this.pendingTransactions);
        transactions.add(this.createCoinbaseTransaction(minerAddress));

        Block candidateBlock = new Block();
        candidateBlock.setIndex(this.getBlockChain().size());
        candidateBlock.setReward(this.reward);
        candidateBlock.setDifficulty(this.difficulty);
        candidateBlock.setBlockDataHash("0x" + Hex.toHexString(HashUtil.sha256(Utils.serialize(transactions).getBytes())));
        candidateBlock.setPrevBlockHash(this.getLastBlock().getBlockHash());
        candidateBlock.setTransactions(transactions);
        candidateBlock.setMinedBy(this.nodeWallet.getAddress());

        return candidateBlock;
    }

    private Transaction createCoinbaseTransaction(String minerAddress) {
        Transaction transaction = new Transaction();
        transaction.setFrom(this.nodeWallet.getAddress());
        transaction.setTo(minerAddress);
        transaction.setValue(this.reward);

        transaction = this.walletController.signTransaction(transaction, this.nodeWallet);
        transaction.setTransactionHash(this.getTransactionHash(transaction));

        return transaction;
    }

    private Block generateBlock(Block blockSentToMiner, Block blockReceivedFromMiner) {
        blockSentToMiner.setDateCreated(blockReceivedFromMiner.getDateCreated());
        blockSentToMiner.setNonce(blockReceivedFromMiner.getNonce());
        blockSentToMiner.setBlockHash(blockReceivedFromMiner.getBlockHash());

        return blockSentToMiner;
    }
}
