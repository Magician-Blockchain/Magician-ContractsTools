package com.blockchain.tools.eth.contract.util;

import com.blockchain.tools.eth.codec.EthAbiCodecTool;
import com.blockchain.tools.eth.contract.util.model.SendModel;
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
    public List<Type> select(String contractAddress, String inputData, TypeReference... outputTypes) throws Exception {
        if(contractAddress == null || contractAddress.trim().equals("")){
            throw new Exception("toAddress must not be empty");
        }
        if(inputData == null || inputData.trim().equals("")){
            throw new Exception("inputData must not be empty");
        }

        Transaction transaction = Transaction.createEthCallTransaction(null, contractAddress, inputData);
        EthCall ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.PENDING).send();
        if(ethCall == null || ethCall.getValue() == null){
            if(ethCall.getError() != null){
                throw new Exception(ethCall.getError().getMessage());
            }
            return null;
        }
        List<Type> result = ethAbiCodecTool.decoderInputData(ethCall.getValue(), outputTypes);

        return result;
    }

    /**
     * write data to the contract
     * @param sendModel
     * @param inputData
     * @return
     * @throws Exception
     */
    public SendResultModel sendRawTransaction(SendModel sendModel, String inputData) throws Exception {
        validation(sendModel, inputData);

        if(sendModel.getNonce() == null){
            EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(sendModel.getSenderAddress(), DefaultBlockParameterName.LATEST).send();
            sendModel.setNonce(ethGetTransactionCount.getTransactionCount());
        }

        if(sendModel.getGasPrice() == null){
            sendModel.setGasPrice(web3j.ethGasPrice().send().getGasPrice());
        }

        RawTransaction rawTransaction =RawTransaction.createTransaction(
                sendModel.getNonce(),
                sendModel.getGasPrice(),
                sendModel.getGasLimit(),
                sendModel.getToAddress(),
                sendModel.getValue(),
                inputData
        );

        byte[] signedMessage = null;

        if(sendModel.getChainId() <= -1L){
            signedMessage = TransactionEncoder.signMessage(rawTransaction, Credentials.create(sendModel.getPrivateKey()));
        } else {
            signedMessage = TransactionEncoder.signMessage(rawTransaction, sendModel.getChainId(), Credentials.create(sendModel.getPrivateKey()));

        }

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

            Thread.sleep(1000);
        }

        return sendResultModel;
    }

    /**
     * Validation Parameters
     * @param sendModel
     * @param inputData
     * @throws Exception
     */
    private void validation(SendModel sendModel, String inputData) throws Exception {
        if(sendModel == null){
            throw new Exception("sendModel must not be null");
        }
        if(sendModel.getSenderAddress() == null || sendModel.getSenderAddress().trim().equals("")){
            throw new Exception("senderAddress must not be empty");
        }
        if(sendModel.getToAddress() == null|| sendModel.getToAddress().trim().equals("")){
            throw new Exception("toAddress must not be empty");
        }
        if(sendModel.getPrivateKey() == null|| sendModel.getPrivateKey().trim().equals("")){
            throw new Exception("privateKey must not be empty");
        }
        if(inputData == null || inputData.trim().equals("")){
            throw new Exception("inputData must not be empty");
        }
        if(sendModel.getValue() == null){
            sendModel.setValue(BigInteger.ZERO);
        }
        if(sendModel.getGasLimit() == null){
            sendModel.setGasLimit(new BigInteger("8000000"));
        }
    }
}
