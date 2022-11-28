package com.blockchain.tools.eth.contract.template;

import com.blockchain.tools.eth.codec.EthAbiCodecTool;
import com.blockchain.tools.eth.contract.util.EthContractUtil;
import com.blockchain.tools.eth.contract.util.model.SendModel;
import com.blockchain.tools.eth.contract.util.model.SendResultModel;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

/**
 * Calling ERC20 contracts
 */
public class ERC20Contract {

    /**
     * Contract address
     */
    private String contractAddress;

    /**
     * Contracts Tools
     *
     * Query contract data and write data to the contract
     */
    private EthContractUtil ethContractUtil;

    private ERC20Contract(Web3j web3j, String contractAddress) {
        this.contractAddress = contractAddress;
        this.ethContractUtil = ethContractUtil.builder(web3j);
    }

    public static ERC20Contract builder(Web3j web3j, String contractAddress) {
        return new ERC20Contract(web3j, contractAddress);
    }

    /**
     * Returns the amount of tokens in existence.
     * @return
     * @throws IOException
     */
    public BigInteger totalSupply() throws Exception {
        return Commons.resultBigInteger(
                ethContractUtil,
                contractAddress,
                EthAbiCodecTool.getInputData(
                        "totalSupply"
                )
        );
    }

    /**
     * Returns the amount of tokens owned by `account`.
     * @param account
     * @return
     * @throws IOException
     */
    public BigInteger balanceOf(String account) throws Exception {
        return Commons.resultBigInteger(
                ethContractUtil,
                contractAddress,
                EthAbiCodecTool.getInputData(
                        "balanceOf",
                        new Address(account)
                )
        );
    }

    /**
     * Returns the remaining number of tokens that `spender` will be
     * allowed to spend on behalf of `owner` through {transferFrom}. This is
     * zero by default.
     *
     * This value changes when {approve} or {transferFrom} are called.
     * @param owner
     * @param spender
     * @return
     * @throws IOException
     */
    public BigInteger allowance(String owner, String spender) throws Exception {
        return Commons.resultBigInteger(ethContractUtil, contractAddress,
                EthAbiCodecTool.getInputData(
                        "allowance",
                        new Address(owner),
                        new Address(spender)
                )
        );
    }

    /**
     * Moves `amount` tokens from the caller's account to `to`.
     * Emits a {Transfer} event.
     *
     * @param to
     * @param amount
     * @param sendModel
     * @return
     * @throws Exception
     */
    public SendResultModel transfer(String to, BigInteger amount, SendModel sendModel) throws Exception {
        return otherTransaction(
                sendModel,
                EthAbiCodecTool.getInputData(
                        "transfer",
                        new Address(to),
                        new Uint256(amount)
                )
        );
    }

    /**
     * Moves `amount` tokens from `from` to `to` using the
     * allowance mechanism. `amount` is then deducted from the caller's
     * allowance.
     *
     * Emits a {Transfer} event.
     *
     * @param from
     * @param to
     * @param amount
     * @param sendModel
     * @return
     * @throws Exception
     */
    public SendResultModel transferFrom(String from, String to, BigInteger amount, SendModel sendModel) throws Exception {
        return otherTransaction(
                sendModel,
                EthAbiCodecTool.getInputData(
                        "transferFrom",
                        new Address(from),
                        new Address(to),
                        new Uint256(amount)
                )
        );
    }

    /**
     * Sets `amount` as the allowance of `spender` over the caller's tokens.
     *
     * @param spender
     * @param amount
     * @param sendModel
     * @return
     * @throws Exception
     */
    public SendResultModel approve(String spender, BigInteger amount, SendModel sendModel) throws Exception {
        return otherTransaction(
                sendModel,
                EthAbiCodecTool.getInputData(
                        "approve",
                        new Address(spender),
                        new Uint256(amount)
                )
        );
    }

    /**
     * Calling custom functions to query contracts
     *
     * @param inputData
     * @param outputTypes
     * @return
     * @throws IOException
     */
    public List<Type> otherSelect(String inputData, TypeReference... outputTypes) throws Exception {
        return Commons.otherSelect(ethContractUtil, contractAddress, inputData, outputTypes);
    }

    /**
     * Calling custom functions to write contracts
     *
     * @param sendModel
     * @param inputData
     * @return
     * @throws Exception
     */
    public SendResultModel otherTransaction(SendModel sendModel, String inputData) throws Exception {
        sendModel.setToAddress(contractAddress);
        return Commons.otherTransaction(ethContractUtil, sendModel, inputData);
    }
}
