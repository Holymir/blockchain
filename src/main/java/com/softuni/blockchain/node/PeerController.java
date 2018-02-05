package com.softuni.blockchain.node;


import com.softuni.blockchain.node.socket.SocketHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PeerController {
    public static Map<Peer, WebSocketSession> peers = new HashMap<>();

    public List<Peer> getPeers() {
        return new ArrayList<>(peers.keySet());
    }

    public void addPeer(Peer peer) {
        if (!peers.containsKey(peer)) {
            this.connectToPeer(peer);
        }
    }

    private void connectToPeer(Peer peer) {
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketHandler sessionHandler = new SocketHandler();

        ListenableFuture<WebSocketSession> webSocketSessionListenableFuture =
                webSocketClient.doHandshake(sessionHandler, String.format("ws://%s", peer.getUrl()));
        webSocketSessionListenableFuture.addCallback(stompSession -> {
            peers.replace(peer, stompSession);
            System.out.println("on Success!");
        }, throwable -> {
            System.out.println("on Failure!");
        });
    }

//    private void addMeAsPeer(Peer peer) {
//        System.out.println(String.format("addMeAsPeer with Peer: %s", peer));
//        if (!peer.equals(me) && !peers.contains(peer)) {
//            RestTemplate restTemplate = new RestTemplate();
//            restTemplate.postForObject("http://" + peer.getUrl() + "/peers", this.me, Peer[].class);
//        }
//    }
}

