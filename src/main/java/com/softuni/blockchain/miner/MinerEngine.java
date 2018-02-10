package com.softuni.blockchain.miner;

import com.softuni.blockchain.node.model.Block;
import com.softuni.blockchain.node.Node;
import com.softuni.blockchain.wallet.crypto.HashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Controller
public class MinerEngine {

    private static final Logger logger = LoggerFactory.getLogger("MINER_ENGINE");
    private MinerController minerController;

    @Autowired
    public MinerEngine(MinerController minerController) {
        this.minerController = minerController;
    }

    @Scheduled(fixedDelay = 5000)
    void run() {
        if (minerController.isMinerStarted) {
            RestTemplate restTemplate = new RestTemplate();
            Block miningJob = restTemplate.getForObject("http://" + minerController.address + "/miningJobs", Block.class);

            if (miningJob != null) {
                Block mined = this.mine(miningJob);

                restTemplate = new RestTemplate();
                restTemplate.postForObject("http://" + minerController.address + "/mined", mined, Block.class);

            }
        }
    }

    public Block mine(Block block) {

        long nonce = 0;
        int difficulty = block.getDifficulty();

        int index = block.getIndex();
        String previousBlockHash = block.getPrevBlockHash();
        String thisDataBlockHash = block.getBlockDataHash();
        Long nextTimestamp = new Date().getTime();

        String nextHash = index + thisDataBlockHash + previousBlockHash + nextTimestamp + nonce;
        String hashToCheck = Hex.toHexString(HashUtil.sha256(nextHash.getBytes()));

        while (!hashToCheck.substring(0, difficulty).equals(generateRepeatingString(difficulty))) {

            nextTimestamp = new Date().getTime();
            nonce++;
            nextHash = index + thisDataBlockHash + previousBlockHash + nextTimestamp + nonce;
            hashToCheck = Hex.toHexString(HashUtil.sha256(nextHash.getBytes()));
        }

        logger.info("HashFound: " + hashToCheck);

        Block validBlock = new Block();
        validBlock.setTransactions(block.getTransactions());
        validBlock.setIndex(index);
        validBlock.setPrevBlockHash(previousBlockHash);
        validBlock.setBlockDataHash(thisDataBlockHash);
        validBlock.setDateCreated(nextTimestamp);
        validBlock.setDifficulty(difficulty);
        validBlock.setNonce(nonce);
        //validBlock.setExpectedReward(miner.getAddress());
        validBlock.setBlockHash(hashToCheck);
        validBlock.setMinedBy(Node.getUuid());
        return validBlock;

    }

    private String generateRepeatingString(Integer n) {
        StringBuilder b = new StringBuilder();
        for (Integer x = 0; x < n; x++)
            b.append("0");
        return b.toString();
    }
}
