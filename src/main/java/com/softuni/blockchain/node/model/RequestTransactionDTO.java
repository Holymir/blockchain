package com.softuni.blockchain.node.model;

import com.softuni.blockchain.wallet.Wallet;

public class RequestTransactionDTO {
    private Transaction transaction;
    private Wallet wallet;

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
}
