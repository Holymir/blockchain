package com.softuni.blockchain.node.socket;

import com.softuni.blockchain.node.NodeController;
import com.softuni.blockchain.node.Peer;
import com.softuni.blockchain.node.PeerController;
import com.softuni.blockchain.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

public class SocketHandler implements WebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger("PEER");
    private PeerController peerController;
    private NodeController nodeController;

    public SocketHandler(PeerController peerController, NodeController nodeController) {
        this.peerController = peerController;
        this.nodeController = nodeController;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) {
        logger.info("ESTABLISHED SOCKET CONNECTION TO: " + webSocketSession.getRemoteAddress());
        String url = webSocketSession.getRemoteAddress().toString().replace("/", "");
        Peer peer = new Peer(url);

        if (!this.peerController.getPeers().containsKey(peer)) {
            this.peerController.getPeers().put(peer, webSocketSession);
        }
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) {
        Message message = Utils.deserialize(Message.class, webSocketMessage.getPayload().toString());
        switch (message.getType()) {
            case NEW_BLOCK:
                logger.debug(String.format("NEW BLOCK NOTIFICATION RECEIVED!!! BLOCK INDEX: %s", message.getBlock().getIndex()));

                if (this.nodeController.getLastBlock().getIndex() >= message.getBlock().getIndex()) {
                    logger.debug("DO NOTING, ALREADY WITH LONGEST CHAIN");
                    break;
                }

                this.nodeController.getUnconfirmedBlocks().add((message.getBlock()));
                break;
        }
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) {
        logger.error(String.format("ERROR COMMUNICATING WITH %s", webSocketSession.getRemoteAddress()));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) {
        String url = webSocketSession.getRemoteAddress().toString().replace("/", "");
        this.peerController.getPeers().remove(new Peer(url));
        logger.info(String.format("DISCONNECTED PEER: %s", webSocketSession.getRemoteAddress()));
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
