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

@Component
public class NodeEngine {

    private static final Logger logger = LoggerFactory.getLogger("NODE_ENGINE");

    private final PeerController peerController;
    private final NodeController nodeController;
    private final MinerEngine minerEngine;
    private Miner miner;

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
            this.nodeController.getUnconfirmedBlocks().removeIf(block -> {
                logger.info(String.format("VERIFY BLOCK: %s", block));
                if (this.nodeController.verifyBlock(block)) {
                    logger.info(String.format("ADDING BLOCK: %s TO BLOCKCHAIN", block));
                    this.nodeController.getBlockChain().add(block);
                    this.notifyPeersForNewBlock(block);
                }
                return true;
            });
        }

        if (this.nodeController.getCandidateBlock() == null) {
            logger.info("CREATING CANDIDATE...");
            this.createCandidateBlock();
        }

        if (!this.nodeController.getStatuses().isEmpty()) {
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

        if (this.peerController.getPeers().size() != 0) {
            askPeersForChainUpdates();
        }
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

    public void createCandidateBlock() {

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

    public Miner getMiner() {
        return miner;
    }

    public void setMiner(Miner miner) {
        this.miner = miner;
    }
}
