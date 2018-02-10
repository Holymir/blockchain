package com.softuni.blockchain.node.transport.socket;

import com.google.common.collect.Sets;
import com.softuni.blockchain.node.*;
import com.softuni.blockchain.node.core.NodeController;
import com.softuni.blockchain.node.core.PeerController;
import com.softuni.blockchain.node.model.Block;
import com.softuni.blockchain.node.model.Blockchain;
import com.softuni.blockchain.node.model.NodeInfo;
import com.softuni.blockchain.node.model.Peer;
import com.softuni.blockchain.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;

import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

public class SocketHandler implements WebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger("PEER");
    private ThreadLocal<StringBuilder> partialMessage = new ThreadLocal<>();
    private PeerController peerController;
    private NodeController nodeController;

    public SocketHandler(PeerController peerController, NodeController nodeController) {
        this.peerController = peerController;
        this.nodeController = nodeController;
        this.partialMessage.set(new StringBuilder());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) {
        logger.info("ESTABLISHED SOCKET CONNECTION TO: " + webSocketSession.getRemoteAddress());
        String url = webSocketSession.getRemoteAddress().toString().replace("/", "");

        Peer peer = new Peer(url);
        peer.setSessionId(webSocketSession.getId());
        peer.setSession(webSocketSession);

        if (!this.peerController.getPeers().containsKey(peer.getSessionId())) {
            this.peerController.getPeers().put(peer.getSessionId(), peer);
        }
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        StringBuilder stringBuilder = this.partialMessage.get();
        if (stringBuilder != null) {
            stringBuilder.append(webSocketMessage.getPayload());
        } else {
            stringBuilder = new StringBuilder();
            stringBuilder.append(webSocketMessage.getPayload());
            this.partialMessage.set(stringBuilder);
        }

        if (!webSocketMessage.isLast()) {
            return;
        }

        this.partialMessage.remove();

        Message message = Utils.deserialize(Message.class, stringBuilder.toString());
        switch (message.getType()) {
            case NEW_BLOCK:
                logger.debug(String.format("NEW BLOCK NOTIFICATION RECEIVED!!! BLOCK INDEX: %s", message.getBlock().getIndex()));
                if (this.nodeController.getLastBlock().getIndex() >= message.getBlock().getIndex()) {
                    logger.debug("DO NOTING, ALREADY WITH LONGEST CHAIN");
                    break;
                }
                this.nodeController.getUnconfirmedBlocks().add((message.getBlock()));
                break;
            case NEW_TRANSACTIONS:
                this.nodeController.getPendingTransactions().addAll(message.getTransactions());
                break;
            case GET_CHAIN:
                logger.debug("GET_CHAIN REQUEST RECEIVED");
                webSocketSession.sendMessage(new TextMessage(
                        Utils.serialize(new Message(MessageType.GET_CHAIN_RESPONSE,
                                new Blockchain(this.nodeController.getBlockChain())))));
                break;
            case GET_CHAIN_RESPONSE:
                logger.debug("GET_CHAIN_RESPONSE");
                List<Block> blockchain = message.getBlockchain().getBlocks();
                this.nodeController.getUnconfirmedBlocks()
                        .addAll(new TreeSet<>(Sets.difference(new HashSet<>(blockchain), new HashSet<>(this.nodeController.getBlockChain()))));
                break;
            case GET_STATUS:
                logger.debug("STATUS_REQUEST RECEIVED");
                webSocketSession.sendMessage(new TextMessage(Utils.serialize(new Message(MessageType.STATUS_RESPONSE,
                        new NodeInfo(new Node(this.peerController, this.nodeController))))));
                break;
            case STATUS_RESPONSE:
                logger.debug("STATUS_RESPONSE RECEIVED");
                Peer peer = this.peerController.getPeers().get(webSocketSession.getId());
                peer.setUuid(message.getNodeInfo().getUuid());
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
        this.peerController.getPeers().remove(webSocketSession.getId());
        logger.info(String.format("DISCONNECTED PEER: %s WITH REASON: %s", webSocketSession.getRemoteAddress(), closeStatus.getReason()));
        logger.debug(String.format("REASON: %s", closeStatus.getReason()));
    }

    @Override
    public boolean supportsPartialMessages() {
        return true;
    }
}
