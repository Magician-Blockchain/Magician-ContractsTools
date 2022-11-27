package com.blockchain.tools.eth.contract.template;

import com.blockchain.tools.eth.contract.util.EthContractUtil;
import com.blockchain.tools.eth.contract.util.model.SendResultModel;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

/**
 * Public methods for contract templates
 */
public class Commons {

    /**
     * Query and return a data of type Uint256
     *
     * @param ethContractUtil
     * @param contractAddress
     * @param inputData
     * @return
     * @throws IOException
     */
    public static BigInteger resultBigInteger(EthContractUtil ethContractUtil, String contractAddress, String inputData) throws IOException {
        List<Type> result = ethContractUtil.select(contractAddress,
                inputData,
                new TypeReference<Uint256>() {}
        );

        if (result == null || result.size() < 1 || result.get(0) == null || result.get(0).getValue() == null) {
            return null;
        }

        return new BigInteger(result.get(0).getValue().toString());
    }

    /**
     * Query and return a data of type Address
     *
     * @param ethContractUtil
     * @param contractAddress
     * @param inputData
     * @return
     * @throws IOException
     */
    public static String resultAddress(EthContractUtil ethContractUtil, String contractAddress, String inputData) throws IOException {
        List<Type> result = ethContractUtil.select(contractAddress,
                inputData,
                new TypeReference<Address>() {}
        );

        if (result == null || result.size() < 1 || result.get(0) == null || result.get(0).getValue() == null) {
            return null;
        }

        return result.get(0).getValue().toString();
    }

    /**
     * Query and return a data of type Bool
     *
     * @param ethContractUtil
     * @param contractAddress
     * @param inputData
     * @return
     * @throws IOException
     */
    public static Boolean resultBool(EthContractUtil ethContractUtil, String contractAddress, String inputData) throws IOException {
        List<Type> result = ethContractUtil.select(contractAddress,
                inputData,
                new TypeReference<Bool>() {}
        );

        if (result == null || result.size() < 1 || result.get(0) == null || result.get(0).getValue() == null) {
            return null;
        }

        return new Boolean(result.get(0).getValue().toString());
    }

    /**
     * Calling custom functions to query contracts
     *
     * @param ethContractUtil
     * @param contractAddress
     * @param inputData
     * @param outputTypes
     * @return
     * @throws IOException
     */
    public static List<Type> otherSelect(EthContractUtil ethContractUtil, String contractAddress, String inputData, TypeReference... outputTypes) throws IOException {
        return ethContractUtil.select(contractAddress, inputData, outputTypes);
    }

    /**
     * Calling custom functions to write contracts
     *
     * @param ethContractUtil
     * @param contractAddress
     * @param originatorAddress
     * @param privateKey
     * @param gasPrice
     * @param gasLimit
     * @param inputData
     * @return
     * @throws Exception
     */
    public static SendResultModel otherTransaction(EthContractUtil ethContractUtil, String contractAddress, String originatorAddress, String privateKey, BigInteger gasPrice, BigInteger gasLimit, String inputData) throws Exception {
        return ethContractUtil.sendRawTransaction(
                originatorAddress,
                contractAddress,
                privateKey,
                gasPrice,
                gasLimit,
                inputData
        );
    }
}
