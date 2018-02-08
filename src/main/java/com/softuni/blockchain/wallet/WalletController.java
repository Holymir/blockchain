package com.softuni.blockchain.wallet;

import com.softuni.blockchain.node.Transaction;
import com.softuni.blockchain.utils.Utils;
import com.softuni.blockchain.wallet.crypto.ECKey;
import com.softuni.blockchain.wallet.crypto.HashUtil;
import org.spongycastle.util.encoders.Hex;
import org.springframework.stereotype.Component;


@Component
public class WalletController {

    public Wallet generateWallet() {
        ECKey keyPair = new ECKey();
        String privKeyHex = Hex.toHexString(keyPair.getPrivKeyBytes());
        String publicKeyHex = Hex.toHexString(keyPair.getPubKey());
        String addressHex = Hex.toHexString(keyPair.getAddress());

        return new Wallet(privKeyHex, publicKeyHex, addressHex);
    }

    private String signMessage(String message, String privateKey) {
        ECKey privateKeyPoint = ECKey.fromPrivate(Hex.decode(privateKey));
        ECKey.ECDSASignature ecdsaSignature = privateKeyPoint.doSign(HashUtil.sha3(message.getBytes()));
        return ecdsaSignature.toHex();
    }

    public boolean verify(String message, String signedMessage, String publicKey) {
      return true;
    }

    public Transaction createTransaction(Transaction transaction, Wallet wallet) {
        String signMessage = signMessage(Utils.serialize(transaction.getCorePart()), wallet.getPrivateKey());
        transaction.setSenderPubKey(wallet.getPublicKey());
        transaction.setSenderSignature(signMessage);

        return transaction;
    }
}
