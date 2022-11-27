package com.blockchain.tools.eth.contract.util;

import com.blockchain.tools.eth.codec.EthAbiCodecTool;
import com.blockchain.tools.eth.contract.util.model.SendResultModel;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.utils.Numeric;

import java.io.EOFException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

/**
 * Contracts Tools
 *
 * Query contract data and write data to the contract
 */
public class EthContractUtil {

    /**
     * native web3j
     */
    private Web3j web3j;

    /**
     * ABI codec
     */
    private EthAbiCodecTool ethAbiCodecTool = new EthAbiCodecTool();

    private EthContractUtil(Web3j web3j){
        this.web3j = web3j;
    }

    public static EthContractUtil builder(Web3j web3j){
        return new EthContractUtil(web3j);
    }

    /**
     * Query the data in the contract
     * @param contractAddress
     * @param inputData
     * @param outputTypes
     * @return
     * @throws IOException
     */
    public List<Type> select(String contractAddress, String inputData, TypeReference... outputTypes) throws IOException {

        Transaction transaction = Transaction.createEthCallTransaction(null, contractAddress, inputData);
        EthCall ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.PENDING).send();
        if(ethCall == null || ethCall.getValue() == null){
            if(ethCall.getError() != null){
                throw new EOFException(ethCall.getError().getMessage());
            }
            return null;
        }
        List<Type> result = ethAbiCodecTool.decoderInputData(ethCall.getValue(), outputTypes);

        return result;
    }

    /**
     * write data to the contract
     * @param fromAddress
     * @param toAddress
     * @param inputData
     * @return
     * @throws Exception
     */
    public SendResultModel sendRawTransaction(String fromAddress, String toAddress, String privateKey, String inputData) throws Exception {
        return sendRawTransaction(fromAddress, toAddress, privateKey, null, null, inputData);
    }

    /**
     * write data to the contract
     * @param fromAddress
     * @param toAddress
     * @param privateKey
     * @param gasPrice
     * @param gasLimit
     * @param inputData
     * @return
     * @throws Exception
     */
    public SendResultModel sendRawTransaction(String fromAddress, String toAddress, String privateKey, BigInteger gasPrice, BigInteger gasLimit, String inputData) throws Exception {
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(fromAddress, DefaultBlockParameterName.LATEST).send();

        BigInteger nonce = ethGetTransactionCount.getTransactionCount();

        if(gasPrice == null){
            gasPrice = web3j.ethGasPrice().send().getGasPrice();
        }
        if(gasLimit == null){
            gasLimit = new BigInteger("8000000");
        }

        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, toAddress, inputData);

        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, Credentials.create(privateKey));
        String hexValue = Numeric.toHexString(signedMessage);

        EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();

        if (ethSendTransaction.hasError()) {
            throw new Exception(ethSendTransaction.getError().getMessage());
        }

        SendResultModel sendResultModel = new SendResultModel();
        sendResultModel.setEthSendTransaction(ethSendTransaction);

        for (int i = 0; i < 60; i++) {
            EthGetTransactionReceipt ethGetTransactionReceipt = web3j.ethGetTransactionReceipt(ethSendTransaction.getTransactionHash()).send();
            sendResultModel.setEthGetTransactionReceipt(ethGetTransactionReceipt);

            if (ethGetTransactionReceipt != null && ethGetTransactionReceipt.getResult() != null) {
                break;
            }

            Thread.sleep(3000);
        }

        return sendResultModel;
    }
}
