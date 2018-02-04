package com.softuni.blockchain.wallet;

import org.springframework.stereotype.Component;

import java.security.*;
import java.security.spec.ECGenParameterSpec;


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
