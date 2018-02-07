package com.softuni.blockchain.node;

import com.softuni.blockchain.miner.MinerEngine;
import com.softuni.blockchain.node.socket.Message;
import com.softuni.blockchain.node.socket.MessageType;
import com.softuni.blockchain.utils.Utils;
import com.softuni.blockchain.wallet.Crypto;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Component
public class NodeEngine {

    private static final Logger logger = LoggerFactory.getLogger("NODE_ENGINE");

    private final PeerController peerController;
    private final NodeController nodeController;
    private final MinerEngine minerEngine;

    @Autowired
    public NodeEngine(PeerController peerController, NodeController nodeController, MinerEngine minerEngine) {
        this.peerController = peerController;
        this.nodeController = nodeController;
        this.minerEngine = minerEngine;
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
                    //TODO: get block from that peer
                }
            });

            this.nodeController.getStatuses().clear();
//            webSocketSession.sendMessage(new TextMessage(Utils.serialize(new Message(MessageType.GET_CHAIN))));
        }

        askPeersForChainUpdates();
    }

    private void askPeersForChainUpdates() {
        logger.info("ASK PEERS FOR STATUS UPDATE");
        this.peerController.getPeers()
                .entrySet().parallelStream()
                .forEach((entry) -> {
                    WebSocketSession session = entry.getValue();
                    TextMessage textMessage = new TextMessage(Utils.serialize(new Message(MessageType.GET_STATUS)));
                    try {
                        session.sendMessage(textMessage);
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                    }
                });
    }

    public void createCandidateBlock() {

        int fee = 5;
        Block candidateBlock = new Block();
        candidateBlock.setIndex(this.nodeController.getBlockChain().size());
        candidateBlock.setExpectedReward(fee);
        candidateBlock.setDifficulty(5);
        candidateBlock.setBlockDataHash("0x" + Hex.toHexString(Crypto.sha256(Utils.serialize(this.nodeController.getPendingTransactions()).getBytes())));
        candidateBlock.setPrevBlockHash(nodeController.getLastBlock().getBlockHash());
        candidateBlock.setTransactions(this.nodeController.getPendingTransactions());
        this.nodeController.getPendingTransactions().clear();
        this.nodeController.setCandidateBlock(candidateBlock);

        Block minedBlock = minerEngine.mine(candidateBlock);
        this.nodeController.getUnconfirmedBlocks().add(minedBlock);
        this.nodeController.setCandidateBlock(null);
    }

    private void notifyPeersForNewBlock(Block block) {
        this.peerController.getPeers()
                .entrySet().parallelStream()
                .forEach((entry) -> {
                    Peer peer = entry.getKey();
                    WebSocketSession session = entry.getValue();

                    TextMessage textMessage = new TextMessage(Utils.serialize(new Message(MessageType.NEW_BLOCK, block)));
                    try {
                        logger.info(String.format("NOTIFY: PEER WITH ADDRESS: '%s', NEW_BLOCK FOUND!", peer.getUrl()));
                        session.sendMessage(textMessage);
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                    }
                });
    }
}
