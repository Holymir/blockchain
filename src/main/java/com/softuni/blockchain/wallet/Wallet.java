package com.softuni.blockchain.wallet;


public class Wallet {
    private String privateKey;
    private String publicKey;
    private String address;

    public Wallet(String privateKey, String publicKey, String address) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.address = address;
    }

    public Wallet() {
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
