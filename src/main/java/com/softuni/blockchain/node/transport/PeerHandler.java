package com.softuni.blockchain.node.transport;

import com.softuni.blockchain.node.model.Peer;
import com.softuni.blockchain.node.core.PeerController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class PeerHandler {

    @Autowired
    private PeerController peerController;

    @GetMapping("/peers")
    public List<Peer> peer() {
        return new ArrayList<>(peerController.getPeers().values());
    }

    @PostMapping("/peers")
    public ResponseEntity peer(@RequestBody List<Peer> peers) {
        this.peerController.addPeers(peers);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/peers")
    public ResponseEntity removePeer(@RequestBody Peer peer) throws IOException {
        this.peerController.remove(peer);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/peers/discovery")
    public ResponseEntity<List<Peer>> discovery(String host) throws Exception {
        return ResponseEntity.ok(this.peerController.discoverPeers(host));
    }
}
