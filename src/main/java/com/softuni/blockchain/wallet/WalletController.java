package com.softuni.blockchain.wallet;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.springframework.stereotype.Component;

import java.security.*;
import java.security.spec.ECGenParameterSpec;


@Component
public class WalletController {

    public Wallet generateWallet() {
        try {
            KeyPair keyPair = this.getKeyPair();


            String privateKey = DigestUtils.sha256Hex(keyPair.getPrivate().getEncoded());
            String publicKey = DigestUtils.sha256Hex(keyPair.getPublic().getEncoded()) + ((BCECPublicKey) keyPair.getPublic()).getParams().getCofactor();


            RIPEMD160Digest d = new RIPEMD160Digest();
            d.update(keyPair.getPublic().getEncoded(), 0, keyPair.getPublic().getEncoded().length);
            byte[] o = new byte[d.getDigestSize()];
            d.doFinal(o, 0);
            String address = Hex.encodeHexString(o);

            return new Wallet(privateKey, publicKey, address);

        } catch (InvalidAlgorithmParameterException | NoSuchProviderException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    private KeyPair getKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
        ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
        keyGen.initialize(ecSpec, new SecureRandom());
        return keyGen.generateKeyPair();
    }
}
