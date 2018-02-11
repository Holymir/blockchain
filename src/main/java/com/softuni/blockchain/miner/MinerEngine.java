package com.softuni.blockchain.miner;

import com.softuni.blockchain.node.model.Block;
import com.softuni.blockchain.node.model.MiningJob;
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

    private String lastMiningJobId;

    @Autowired
    public MinerEngine(MinerController minerController) {
        this.minerController = minerController;
    }

    @Scheduled(fixedDelay = 1_000)
    void run() {
        if (this.minerController.getShouldMine()) {
            RestTemplate restTemplate = new RestTemplate();
            MiningJob miningJob = restTemplate.getForObject("http://" + minerController.getMinerConfig().getNode() +
                    "/mining/get-block/" + this.minerController.getMinerConfig().getWallet(), MiningJob.class);

            if (miningJob != null) {
                if (miningJob.getId().equals(lastMiningJobId)) {
                    return;

                }
                Block mined = this.mine(miningJob.getBlock());
                restTemplate = new RestTemplate();
                restTemplate.postForObject("http://" + minerController.getMinerConfig().getNode()
                        + "/mining/submit-block/" + miningJob.getId(), mined, Block.class);
                this.lastMiningJobId = miningJob.getId();
            }
        }
    }

    private Block mine(Block block) {
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

        validBlock.setDateCreated(nextTimestamp);
        validBlock.setNonce(nonce);
        validBlock.setBlockHash(hashToCheck);

        return validBlock;
    }

    private String generateRepeatingString(Integer n) {
        StringBuilder b = new StringBuilder();
        for (Integer x = 0; x < n; x++)
            b.append("0");
        return b.toString();
    }
}
