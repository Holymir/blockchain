package com.softuni.blockchain.wallet;


import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

@RestController
public class WalletHandler {

    @Autowired
    private WalletController walletController;


    @GetMapping("/wallet")
    public Wallet generateWallet() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
        KeyPair keyPair = this.walletController.getKeyPair();

        String privateKey = DigestUtils.sha256Hex(keyPair.getPrivate().getEncoded());
        String publicKey = DigestUtils.sha256Hex(keyPair.getPublic().getEncoded()) + ((BCECPublicKey) keyPair.getPublic()).getParams().getCofactor();


        RIPEMD160Digest d = new RIPEMD160Digest();
        d.update(keyPair.getPublic().getEncoded(), 0, keyPair.getPublic().getEncoded().length);
        byte[] o = new byte[d.getDigestSize()];
        d.doFinal(o, 0);
        String address = Hex.encodeHexString(o);

        return new Wallet(privateKey, publicKey, address);
    }
}
