package com.softuni.blockchain.wallet;

import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

import static org.apache.commons.codec.digest.DigestUtils.sha256;


@Component
public class WalletController {

    public KeyPair getKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
        ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
        keyGen.initialize(ecSpec, new SecureRandom());
        return keyGen.generateKeyPair();
    }


//    public static string getHashSha256(string text)
//    {
//        byte[] bytes = Encoding.Unicode.GetBytes(text);
//
//        SHA256Managed hashstring = new SHA256Managed();
//
//        byte[] hash = hashstring.ComputeHash(bytes);
//        string hashString = null;
//
//        foreach (byte x in hash)
//        {
//            hashString += String.Format("{0:x2}", x);
//        }
//
//        return hashString;
//    }
}
