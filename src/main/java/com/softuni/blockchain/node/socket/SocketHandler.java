package com.softuni.blockchain.node.socket;

import com.softuni.blockchain.node.Peer;
import com.softuni.blockchain.node.PeerController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

public class SocketHandler implements WebSocketHandler {

    @Autowired
    private PeerController peerController;

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) {
        String url = webSocketSession.getRemoteAddress().toString().replace("/", "");
        Peer peer = new Peer(url);

        if (!peerController.getPeers().containsKey(peer)) {
            peerController.getPeers().put(peer, webSocketSession);
        }
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) {
        int s = 3;
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) {
        int s = 3;
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) {
        String url = webSocketSession.getRemoteAddress().toString().replace("/", "");

        peerController.getPeers().remove(new Peer(url));
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
