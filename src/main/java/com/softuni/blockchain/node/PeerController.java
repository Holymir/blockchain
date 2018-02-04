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

    public List<Peer> getPeers() {
        return new ArrayList<>(peers);
    }

    public List<Peer> addPeer(Peer peer) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            Node node = restTemplate.getForObject("http://" + peer.getUrl() + "/info", Node.class);

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


    }
}

