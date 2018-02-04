package com.softuni.blockchain.node;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NodeController {

    @GetMapping("/info")
    public Node nodeInfo() {

        return new Node();

    }

    @GetMapping("/blocks")
    public Block block() {

        return new Block();
    }

    @GetMapping("/blocks/{index}")
    public Block block(@PathVariable Integer index) {
        Block block =  new Block();
        block.setIndex(index);

        return block;
    }

}
