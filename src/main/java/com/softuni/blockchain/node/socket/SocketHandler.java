package com.softuni.blockchain.node.socket;

import com.softuni.blockchain.node.*;
import com.softuni.blockchain.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;

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
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
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
            case GET_CHAIN:
                logger.debug("GET_CHAIN RECEIVED");
                webSocketSession.sendMessage(new TextMessage(
                                Utils.serialize(new Message(MessageType.GET_CHAIN_RESPONSE, this.nodeController.getBlockChain()))));
                break;
            case GET_CHAIN_RESPONSE:
                logger.debug("GET_CHAIN_RESPONSE");
                this.nodeController.getUnconfirmedBlocks().addAll(message.getBlockchain());
                break;
            case GET_STATUS:
                logger.debug("STATUS_REQUEST RECEIVED");
                webSocketSession.sendMessage(new TextMessage(Utils.serialize(new Message(MessageType.STATUS_RESPONSE,
                        new NodeInfo(new Node(this.peerController, this.nodeController))))));
                break;
            case STATUS_RESPONSE:
                logger.debug("STATUS_RESPONSE RECEIVED");
                this.nodeController.getStatuses().add(message.getNodeInfo());
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
