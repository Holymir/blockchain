package com.softuni.blockchain.node;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Set;

@Component
public class PeerControler {

    private static Set<Peer> peers;
    private String ip = "192.168.0.20:8080";



    public Node ping(Peer peer) {
        RestTemplate restTemplate = new RestTemplate();
        Node node = restTemplate.postForObject("http://" + peer.getUrl() + "/peers", new Peer(ip), Node.class);
        //int x = 2;
        return node;
    }


    public void addPeer(Peer peer) {
        List<Peer> nodePeers = this.ping(peer).getPeers();
        if (nodePeers != null && !nodePeers.isEmpty()) {
            peers.addAll(nodePeers);
        }
        peers.add(peer);
    }
}
