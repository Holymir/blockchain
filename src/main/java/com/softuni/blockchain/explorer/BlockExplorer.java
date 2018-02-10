package com.softuni.blockchain.explorer;

import com.softuni.blockchain.node.model.Block;
import com.softuni.blockchain.node.core.NodeController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BlockExplorer {

    @Autowired
    private NodeController nodeController;

    @GetMapping("/blocks")
    public List<Block> block() {
        return this.nodeController.getBlockChain();
    }

    @GetMapping("/blocks/{index}")
    public Block block(@PathVariable Integer index) {
        return this.nodeController.getBlockChain().get(index);
    }

    @GetMapping("/blocks/last-block")
    public Block getLastBlock() {
        return this.nodeController.getBlockChain().get(this.nodeController.getBlockChain().size() - 1);
    }
}
