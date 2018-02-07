package com.softuni.blockchain.node;

import com.softuni.blockchain.node.socket.Message;
import com.softuni.blockchain.node.socket.MessageType;
import com.softuni.blockchain.utils.Utils;
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

    private boolean newBlockIsFound = true;

    @Autowired
    public NodeEngine(PeerController peerController, NodeController nodeController) {
        this.peerController = peerController;
        this.nodeController = nodeController;
    }

    @Scheduled(fixedDelay = 5_000)
    void run() {
        if (newBlockIsFound) {
            this.newBlockIsFound = false;
            this.notifyPeersForNewBlock(this.nodeController.getLastBlock());
        }

        if (this.nodeController.getUnconfirmedBlocks().size() != 0) {
            this.nodeController.getUnconfirmedBlocks().removeIf(block -> {
                if (this.nodeController.verifyBlock(block)) {
                    this.nodeController.getBlockChain().add(block);
                    this.notifyPeersForNewBlock(block);
                }
                return true;
            });
        }
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
