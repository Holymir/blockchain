package com.softuni.blockchain.node;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NodeController {

    @GetMapping("/info")
    public Node nodeInfo() {

        return new Node();

//        return "\"about\": \"SoftUniChain/0.9-java\",\n" +
//                "  \"nodeName\": \"Sofia-01\",\n" +
//                "  \"peers\": 2,\n" +
//                "  \"blocks\": 25,\n" +
//                "  \"confirmedTransactions\": 208,\n" +
//                "  \"pendingTransactions\": 7,\n" +
//                "  \"addresses\": 12,\n" +
//                "  \"coins\": 18000000\n";
    }
}
