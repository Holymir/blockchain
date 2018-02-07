package com.softuni.blockchain.miner;

import com.softuni.blockchain.node.Block;
import com.softuni.blockchain.wallet.Crypto;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.util.Date;

@Controller
public class MinerEngine {

    private static final Logger logger = LoggerFactory.getLogger("MINER_ENGINE");

    public Block mine(Block block) {

        long nonce = 0;
        int difficulty = block.getDifficulty();

        int index = block.getIndex();
        String previousBlockHash = block.getPrevBlockHash();
        String thisBlockHash = block.getBlockDataHash();
        Long nextTimestamp = new Date().getTime();

        String nextHash = index + thisBlockHash + previousBlockHash + nextTimestamp + nextTimestamp + nonce;
        String hashToCheck = Hex.toHexString(Crypto.sha256(nextHash.getBytes()));

        while (!hashToCheck.substring(0, difficulty).equals(generateRepeatingString(difficulty))) {

            nextTimestamp = new Date().getTime();
            nextHash = index + thisBlockHash + previousBlockHash + nextTimestamp + nextTimestamp + nonce;
            hashToCheck = Hex.toHexString(Crypto.sha256(nextHash.getBytes()));
            nonce++;
        }

        logger.info("HashFound: " + hashToCheck);

        Block validBlock = new Block();
        validBlock.setIndex(index);
        validBlock.setPrevBlockHash(previousBlockHash);
        validBlock.setBlockDataHash(thisBlockHash);
        validBlock.setDateCreated(nextTimestamp);
        validBlock.setDifficulty(difficulty);
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
