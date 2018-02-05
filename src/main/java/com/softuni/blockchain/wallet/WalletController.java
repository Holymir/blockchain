package com.softuni.blockchain.wallet;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.math.ec.ECPoint;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.ECGenParameterSpec;


@Component
public class WalletController {

    public Wallet generateWallet() {
        try {
            KeyPair keyPair = this.getKeyPair();

            String privateKey = "0x" + ((BCECPrivateKey) keyPair.getPrivate()).getS().toString(16);
            String publicKey = "0x" + Hex.encodeHexString(keyPair.getPublic().getEncoded());

            RIPEMD160Digest d = new RIPEMD160Digest();
            d.update(keyPair.getPublic().getEncoded(), 0, keyPair.getPublic().getEncoded().length);
            byte[] o = new byte[d.getDigestSize()];
            d.doFinal(o, 0);
            String address = "0x" + Hex.encodeHexString(o);

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

    public String signMessage(String message, String privateKey) {
        return Crypto.signMessage(message, new BigInteger(privateKey.replaceFirst("0x", ""), 16));
    }

    public boolean verify(String message, String signedMessage, String publicKey) {
        ECPoint signer = null;
        try {
            signer = Crypto.signedMessageToKey(message, signedMessage);
            return signer.equals(Crypto.decode(publicKey));
        } catch (SignatureException e) {

        }

        return false;
    }
}
