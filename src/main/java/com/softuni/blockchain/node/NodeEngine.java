package com.softuni.blockchain.node;

import com.softuni.blockchain.miner.Miner;
import com.softuni.blockchain.miner.MinerEngine;
import com.softuni.blockchain.node.socket.Message;
import com.softuni.blockchain.node.socket.MessageType;
import com.softuni.blockchain.utils.Utils;
import com.softuni.blockchain.wallet.crypto.HashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.*;

@Component
public class NodeEngine {

    private static final Logger logger = LoggerFactory.getLogger("NODE_ENGINE");

    private final PeerController peerController;
    private final NodeController nodeController;
    private final MinerEngine minerEngine;
    private Miner miner;

    private final Object unconfirmedBlockMutex = new Object();

    @Autowired
    public NodeEngine(PeerController peerController, NodeController nodeController, MinerEngine minerEngine, Miner miner) {
        this.peerController = peerController;
        this.nodeController = nodeController;
        this.minerEngine = minerEngine;
        this.miner = miner;
    }

    @Scheduled(fixedDelay = 5_000)
    synchronized void run() {
        if (this.nodeController.getUnconfirmedBlocks().size() != 0) {
            this.synchronizeBlockChain();
        }

        if (this.nodeController.getCandidateBlock() == null) {
            this.createCandidateBlock();
        }

        if (this.peerController.getPeers().size() != 0) {
            this.askPeersForChainUpdates();
        }

        if (!this.nodeController.getStatuses().isEmpty()) {
            this.askPeersForBlocks();
        }
    }

    private void askPeersForBlocks() {
        this.nodeController.getStatuses().forEach(nodeInfo -> {
            logger.debug(String.format("PEER STATUS: %s", nodeInfo));
            if (nodeInfo.getBlocks() > this.nodeController.getBlockChain().size()) {
                Optional<Peer> peerWithId = peerController.getPeers().values().stream().findAny();
                if (peerWithId.isPresent()) {
                    Peer peer = peerWithId.get();
                    try {
                        peer.getSession()
                                .sendMessage(new TextMessage(Utils.serialize(new Message(MessageType.GET_CHAIN))));
                    } catch (IOException e) {
                        logger.debug(String.format("ERROR COMMUNICATING WITH PEER: %s ON ADDRESS: %s", peer.getUuid(), peer.getUrl()));
                    }
                }

            }
        });

        this.nodeController.getStatuses().clear();
    }

    private void synchronizeBlockChain() {
        List<Block> unconfirmedBlocks;
        synchronized (unconfirmedBlockMutex) {
            unconfirmedBlocks = new ArrayList<>(this.nodeController.getUnconfirmedBlocks());
            this.nodeController.getUnconfirmedBlocks().clear();
        }

        if (unconfirmedBlocks.size() == 1) {
            Block block = unconfirmedBlocks.get(0);
            logger.info(String.format("VERIFY BLOCK: %s", block));
            if (this.nodeController.verifyBlock(block)) {
                logger.info(String.format("ADDING BLOCK: %s TO BLOCKCHAIN", block));
                this.nodeController.getBlockChain().add(block);
                this.notifyPeersForNewBlock(block);
            }

            return;
        }

        Map<String, List<Blockchain>> allBlockChainsByHead = new HashMap<>();
        for (int i = 0; i < unconfirmedBlocks.size() - 1; i++) {
            Block block = unconfirmedBlocks.get(i);
            Block nextBlock = unconfirmedBlocks.get(i + 1);

            if (i == 0) {
                allBlockChainsByHead.put(block.getBlockHash(), new ArrayList<>(Arrays.asList(new Blockchain(block))));
                continue;
            }

            if (!block.getBlockHash().equals(nextBlock.getPrevBlockHash())) {
                if (allBlockChainsByHead.get(block.getBlockHash()) != null) {
                    allBlockChainsByHead.get(block.getBlockHash()).add(new Blockchain(block));
                } else {
                    allBlockChainsByHead.put(block.getBlockHash(), new ArrayList<>(Arrays.asList(new Blockchain(block))));
                }
            }
        }

        int i = 0;
        while (i < unconfirmedBlocks.size()) {
            Block block = unconfirmedBlocks.get(i);
            if (this.nodeController.verifyBlockIntegrity(block)) {
                allBlockChainsByHead.keySet().forEach(head -> {
                    allBlockChainsByHead.get(head).forEach(blockchain -> {
                        if (blockchain.getLastBlock().getBlockHash().equals(block.getPrevBlockHash())) {
                            blockchain.addBlock(block);
                        }
                    });
                });
            }
            i++;
        }

        // Merge all chains into one! Almighty, the 'da si ebe' one!
        List<Block> blockChain = this.nodeController.getBlockChain();
        allBlockChainsByHead.keySet().forEach(headHash -> {
            allBlockChainsByHead.get(headHash).forEach(subchain -> {
                Optional<Block> blockInMyChain = blockChain.stream()
                        .filter(block -> block.getBlockHash().equals(subchain.getFirstBlock().getPrevBlockHash())).findAny();
                if (blockInMyChain.isPresent()) {
                    Block block = blockInMyChain.get();
                    int newLength = subchain.size() + block.getIndex();
                    if (newLength >= blockChain.size()) {
                        List<Block> forRemove = blockChain.subList(block.getIndex(), blockChain.size() - 1);
                        blockChain.removeAll(forRemove);
                        blockChain.addAll(subchain.getBlocks());
                    }
                } else {
                    if (subchain.size() > blockChain.size()) {
                        blockChain.clear();
                        blockChain.addAll(subchain.getBlocks());
                    }
                }
            });
        });
    }

    private void askPeersForChainUpdates() {
        logger.info("ASK PEERS FOR STATUS UPDATE");
        this.peerController.getPeers()
                .values().parallelStream()
                .forEach(peer -> {
                    WebSocketSession session = peer.getSession();
                    TextMessage textMessage = new TextMessage(Utils.serialize(new Message(MessageType.GET_STATUS)));
                    try {
                        session.sendMessage(textMessage);
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                    }
                });
    }

    private void createCandidateBlock() {
        logger.info("CREATING CANDIDATE...");

        double fee = 5;
        Transaction rewardToMiner = new Transaction();
        rewardToMiner.setFrom("coinBased");
        rewardToMiner.setTransactionHash("coinBased");
        rewardToMiner.setTo(miner.getAddress());
        rewardToMiner.setValue(fee);
        List<Transaction> transactionsPlusReward = new ArrayList<>(this.nodeController.getPendingTransactions());
        transactionsPlusReward.add(rewardToMiner);


        Block candidateBlock = new Block();
        candidateBlock.setIndex(this.nodeController.getBlockChain().size());
        candidateBlock.setExpectedReward(fee);
        candidateBlock.setDifficulty(5);
        candidateBlock.setBlockDataHash("0x" + Hex.toHexString(HashUtil.sha256(Utils.serialize(transactionsPlusReward).getBytes())));
        candidateBlock.setPrevBlockHash(nodeController.getLastBlock().getBlockHash());
        candidateBlock.setTransactions(transactionsPlusReward);
        this.nodeController.getPendingTransactions().clear();
        this.nodeController.setCandidateBlock(candidateBlock);

        Block minedBlock = minerEngine.mine(candidateBlock);
        this.nodeController.getUnconfirmedBlocks().add(minedBlock);
        this.nodeController.setCandidateBlock(null);
    }

    private void notifyPeersForNewBlock(Block block) {
        this.peerController.getPeers()
                .values().parallelStream()
                .forEach((peer) -> {
                    TextMessage textMessage = new TextMessage(Utils.serialize(new Message(MessageType.NEW_BLOCK, block)));
                    try {
                        logger.info(String.format("NOTIFY: PEER WITH ADDRESS: '%s', NEW_BLOCK FOUND!", peer.getUrl()));
                        peer.getSession().sendMessage(textMessage);
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                    }
                });
    }
}
