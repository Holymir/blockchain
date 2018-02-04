package com.softuni.blockchain.node;

import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class PeerController {
    static Set<Peer> peers = new HashSet<>();
    private Peer me;

    public PeerController() {
        this.me = new Peer("192.168.0.11:8008");
    }

    public List<Peer> addPeer(Peer peer) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getForObject("http://" + peer.getUrl() + "/info", Node.class);

            this.addMeAsPeer(peer);

            peers.add(peer);

            return new ArrayList<>(peers);
        } catch (final HttpClientErrorException e) {
            System.out.println("Node not available");
            System.out.println(e.getMessage());
        }

        return null;
    }

    private void addMeAsPeer(Peer peer) {
        if (!peer.equals(me) || peers.contains(peer)) {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForObject("http://" + peer.getUrl() + "/peer", this.me, Peer[].class);
        }
    }
}

