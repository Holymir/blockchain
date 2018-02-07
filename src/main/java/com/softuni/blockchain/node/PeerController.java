package com.softuni.blockchain.node;


import com.softuni.blockchain.node.socket.SocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.util.HashMap;
import java.util.Map;

@Component
public class PeerController {
    private static final Logger logger = LoggerFactory.getLogger("PEER");

    @Autowired
    private NodeController nodeController;

    private Map<Peer, WebSocketSession> peers = new HashMap<>();

    public Map<Peer, WebSocketSession> getPeers() {
        return peers;
    }

    public void addPeer(Peer peer) {
        logger.debug(String.format("Peer with address: '%s' received: ", peer.getUrl()));
        if (!peers.containsKey(peer)) {
            this.connectToPeer(peer);
        }
    }

    private void connectToPeer(Peer peer) {
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketHandler sessionHandler = new SocketHandler(this, this.nodeController);

        ListenableFuture<WebSocketSession> webSocketSessionListenableFuture =
                webSocketClient.doHandshake(sessionHandler, String.format("ws://%s", peer.getUrl()));
        webSocketSessionListenableFuture.addCallback(stompSession -> {
            logger.debug(String.format("Peer with address: '%s' added to system.", peer.getUrl()));
            this.getPeers().replace(peer, stompSession);
        }, throwable -> logger.error("Error connecting to peer."));
    }

//    private void addMeAsPeer(Peer peer) {
//        System.out.println(String.format("addMeAsPeer with Peer: %s", peer));
//        if (!peer.equals(me) && !peers.contains(peer)) {
//            RestTemplate restTemplate = new RestTemplate();
//            restTemplate.postForObject("http://" + peer.getUrl() + "/peers", this.me, Peer[].class);
//        }
//    }
}

