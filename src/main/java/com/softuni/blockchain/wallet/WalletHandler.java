package com.softuni.blockchain.wallet;

import org.apache.commons.codec.digest.DigestUtils;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

@RestController
public class WalletHandler {

    @Autowired
    private WalletController walletController;


    @GetMapping("/wallet")
    public Wallet generateWallet() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        KeyPair keyPair = this.walletController.getKeyPair();

        String privateKey = DigestUtils.sha256Hex(keyPair.getPrivate().getEncoded());
        String publicKey = DigestUtils.sha256Hex(keyPair.getPublic().getEncoded()) + ((BCECPublicKey) keyPair.getPublic()).getParams().getCofactor();

//        String address =

        return new Wallet(privateKey, publicKey, null);
    }
}
