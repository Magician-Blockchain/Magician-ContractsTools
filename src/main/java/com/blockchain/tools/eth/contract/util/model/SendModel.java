package com.blockchain.tools.eth.contract.util.model;

import java.math.BigInteger;

/**
 * Basic parameters required for sending transactions
 */
public class SendModel {

    /**
     * Address of the caller
     */
    private String senderAddress;

    /**
     * to Address or Contract Address
     */
    private String toAddress;

    /**
     * Private key of senderAddress
     */
    private String privateKey;

    /**
     * Coin amount
     */
    private BigInteger value;

    /**
     * gasPrice
     *
     * If you want to use the default value, you can pass null directly or leave this parameter out.
     */
    private BigInteger gasPrice;

    /**
     * gasLimit
     *
     * If you want to use the default value, you can pass null directly or leave this parameter out.
     */
    private BigInteger gasLimit;

    /**
     * nonce
     */
    private BigInteger nonce;

    /**
     * chain id
     */
    private long chainId = -1;

    private SendModel(){
        value = BigInteger.ZERO;
    }

    public static SendModel builder(){
        return new SendModel();
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public SendModel setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
        return this;
    }

    public String getToAddress() {
        return toAddress;
    }

    public SendModel setToAddress(String toAddress) {
        this.toAddress = toAddress;
        return this;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public SendModel setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
        return this;
    }

    public BigInteger getValue() {
        return value;
    }

    public SendModel setValue(BigInteger value) {
        this.value = value;
        return this;
    }

    public BigInteger getGasPrice() {
        return gasPrice;
    }

    public SendModel setGasPrice(BigInteger gasPrice) {
        this.gasPrice = gasPrice;
        return this;
    }

    public BigInteger getGasLimit() {
        return gasLimit;
    }

    public SendModel setGasLimit(BigInteger gasLimit) {
        this.gasLimit = gasLimit;
        return this;
    }

    public BigInteger getNonce() {
        return nonce;
    }

    public SendModel setNonce(BigInteger nonce) {
        this.nonce = nonce;
        return this;
    }

    public long getChainId() {
        return chainId;
    }

    public SendModel setChainId(long chainId) {
        this.chainId = chainId;
        return this;
    }
}
