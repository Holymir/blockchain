package com.softuni.blockchain.node;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

@Component
public class PeerControler {

    static Set<Peer> peers;
    private String ip = "192.168.0.20:8080";

    public void addPeer(Peer peer) {

        RestTemplate restTemplate = new RestTemplate();
        Peer[] requestedPeers = restTemplate.postForObject("http://" + peer.getUrl() + "/peers", new Peer(ip), Peer[].class);

        if (requestedPeers != null && requestedPeers.length > 0) {
            peers.addAll(new ArrayList<>(Arrays.asList(requestedPeers)));
        }
        peers.add(peer);
    }
}
