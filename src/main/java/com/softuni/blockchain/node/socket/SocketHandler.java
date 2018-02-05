package com.softuni.blockchain.node.socket;

import com.softuni.blockchain.node.Peer;
import com.softuni.blockchain.node.PeerController;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

public class SocketHandler implements WebSocketHandler {


    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) {
        String url = webSocketSession.getRemoteAddress().toString().replace("/", "");
        Peer peer = new Peer(url);

        if (!PeerController.peers.containsKey(peer)) {
            PeerController.peers.put(peer, webSocketSession);
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

        PeerController.peers.remove(new Peer(url));
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
