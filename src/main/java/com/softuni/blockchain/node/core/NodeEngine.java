package com.softuni.blockchain.node.core;

import com.google.common.collect.Sets;
import com.softuni.blockchain.node.model.Block;
import com.softuni.blockchain.node.model.Peer;
import com.softuni.blockchain.node.transport.socket.Message;
import com.softuni.blockchain.node.transport.socket.MessageType;
import com.softuni.blockchain.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;

@Component
public class NodeEngine {

    private static final Logger logger = LoggerFactory.getLogger("NODE_ENGINE");

    private final PeerController peerController;
    private final NodeController nodeController;

    private final Object unconfirmedBlockMutex = new Object();
    private final Object blockChainMutex = new Object();

    @Autowired
    public NodeEngine(PeerController peerController, NodeController nodeController) {
        this.peerController = peerController;
        this.nodeController = nodeController;
    }

    @Scheduled(fixedDelay = 1_000)
    synchronized void run() {
        if (this.nodeController.getUnconfirmedBlocks().size() != 0) {
            this.synchronizeBlockChain();
        }

        if (this.peerController.getPeers().size() != 0) {
            this.askPeersForChainUpdates();
        }

        if (!this.nodeController.getStatuses().isEmpty()) {
            this.askPeersForBlocks();
        }

        if (!this.nodeController.getPendingTransactions().isEmpty() && !this.peerController.getPeers().isEmpty()) {
            this.broadcastTransactions();
        }
    }

    private void broadcastTransactions() {
        logger.info("BROADCAST PENDING TRANSACTIONS TO PEERS");
        this.peerController.getPeers()
                .values().parallelStream()
                .forEach(peer -> {
                    WebSocketSession session = peer.getSession();
                    TextMessage textMessage = new TextMessage(Utils.serialize(new Message(MessageType.NEW_TRANSACTIONS, this.nodeController.getPendingTransactions())));
                    try {
                        session.sendMessage(textMessage);
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                    }
                });
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

        List<Block> oldChain;
        synchronized (blockChainMutex) {
            oldChain = new ArrayList<>(this.nodeController.getBlockChain());
            this.nodeController.setBlockChain(this.synchronizeBlockChain(unconfirmedBlocks, new ArrayList<>(oldChain)));
        }

        new TreeSet<>(Sets.difference(new HashSet<>(this.nodeController.getBlockChain()), new HashSet<>(oldChain)))
                .forEach(this::notifyPeersForNewBlock);
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


    public List<Block> synchronizeBlockChain(List<Block> unconfirmedBlocks, List<Block> blockchain) {
        List<Block> tempChain = new LinkedList<>();
        for (Block block : unconfirmedBlocks) {
            logger.info(String.format("VERIFY BLOCK: %s", block));
            if (this.nodeController.verifyBlock(block, blockchain.get(blockchain.size() - 1))) {
                logger.info(String.format("ADDING BLOCK: %s TO BLOCKCHAIN", block));
                blockchain.add(block);
            } else {
                tempChain.add(block);
            }
        }

        if (!this.checkConsistency(tempChain)) {
            this.synchronizeBlockChain(tempChain.subList(1, tempChain.size() - 1), new ArrayList<>(Arrays.asList(tempChain.get(0))));
        }

        if (this.getDifficulty(tempChain) < this.getDifficulty(blockchain)) {
            return blockchain;
        }

        return tempChain;
    }

    private int getDifficulty(List<Block> blocks) {
        int difficulty = 0;
        for (Block block : blocks) {
            difficulty += block.getDifficulty();
        }

        return difficulty;
    }

    private boolean checkConsistency(List<Block> blockchain) {
        for (int i = 0; i < blockchain.size() - 1; i++) {
            Block block = blockchain.get(i);
            Block nextBlock = blockchain.get(i + 1);
            if (!nextBlock.getPrevBlockHash().equals(block.getBlockHash())) {
                return false;
            }
        }

        return true;
    }
}
