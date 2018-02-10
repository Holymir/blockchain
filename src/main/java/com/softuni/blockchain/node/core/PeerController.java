package com.softuni.blockchain.node.core;


import com.softuni.blockchain.node.model.Peer;
import com.softuni.blockchain.node.transport.socket.SocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class PeerController {
    private static final Logger logger = LoggerFactory.getLogger("PEER");

    @Autowired
    private NodeController nodeController;

    private Map<String, Peer> peers = new HashMap<>();
    private CompletableFuture<List<Peer>> discoveryFuture;

    public Map<String, Peer> getPeers() {
        return peers;
    }

    public void addPeers(List<Peer> peers) {
        peers.stream().map(a -> new Peer(a.getUrl() + ":8080")).forEach(this::connectToPeer);
    }

    private void connectToPeer(Peer peer) {
        logger.debug(String.format("Peer with address: '%s' received: ", peer.getUrl()));
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        WebSocketHandler sessionHandler = new SocketHandler(this, this.nodeController);

        ListenableFuture<WebSocketSession> webSocketSessionListenableFuture =
                webSocketClient.doHandshake(sessionHandler, String.format("ws://%s", peer.getUrl()));
        webSocketSessionListenableFuture.addCallback(webSocketSession -> {
            logger.debug(String.format("Session with id '%s' is open to Peer with address: '%s' added to system.", webSocketSession.getId(), peer.getUrl()));

            peer.setSession(webSocketSession);
            peer.setSessionId(webSocketSession.getId());

            this.getPeers().put(webSocketSession.getId(), peer);

        }, throwable -> logger.error("Error connecting to peer."));
    }

    public void remove(Peer peer) throws IOException {
        peer.getSession().close();
        this.getPeers().remove(peer.getSessionId());
    }


    public List<Peer> discoverPeers(String host) throws Exception {
        if (this.discoveryFuture != null) {
            if (this.discoveryFuture.isDone()) {
                List<Peer> peers = this.discoveryFuture.get();
                this.discoveryFuture = null;
                return peers;
            } else {
                throw new RuntimeException("Discovery is running...");
            }
        }

        synchronized (this) {
            this.discoveryFuture = CompletableFuture.supplyAsync(() -> {
                return this.discovery(host);
            });
        }

        return new ArrayList<>();
    }

    private List<Peer> discovery(String host) {
        InetAddress localhost = null;
        try {
            localhost = InetAddress.getByName(host);

            byte[] ip = localhost.getAddress();

            List<Peer> addresses = new ArrayList<>();

            for (int i = 1; i <= 254; i++) {
                try {
                    ip[3] = (byte) i;
                    InetAddress address = InetAddress.getByAddress(ip);
                    if (address.isReachable(100)) {
                        String output = address.toString().substring(1);
                        logger.info(output + " is on the network");
                        addresses.add(new Peer(output));
                    }
                } catch (Exception ex) {
                    logger.error(ex.getMessage());
                }
            }

            return addresses;

        } catch (UnknownHostException e) {
            logger.error(e.getMessage());
        }

        return new ArrayList<>();
    }

//    private void addMeAsPeer(Peer peer) {
//        System.out.println(String.format("addMeAsPeer with Peer: %s", peer));
//        if (!peer.equals(me) && !peers.contains(peer)) {
//            RestTemplate restTemplate = new RestTemplate();
//            restTemplate.postForObject("http://" + peer.getUrl() + "/peers", this.me, Peer[].class);
//        }
//    }
}

