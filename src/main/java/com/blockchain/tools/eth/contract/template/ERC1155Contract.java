package com.blockchain.tools.eth.contract.template;

import com.blockchain.tools.eth.codec.EthAbiCodecTool;
import com.blockchain.tools.eth.contract.util.EthContractUtil;
import com.blockchain.tools.eth.contract.util.model.SendResultModel;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Calling ERC1155 contracts
 */
public class ERC1155Contract {

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

    /**
     * Calling ERC721 contracts
     */
    private ERC721Contract erc721Contract;

    private ERC1155Contract(Web3j web3j, String contractAddress) {
        this.contractAddress = contractAddress;
        this.ethContractUtil = ethContractUtil.builder(web3j);
        this.erc721Contract = ERC721Contract.builder(web3j, contractAddress);
    }

    public static ERC1155Contract builder(Web3j web3j, String contractAddress) {
        return new ERC1155Contract(web3j, contractAddress);
    }

    /**
     * Returns the amount of tokens of token type `id` owned by `account`.
     *
     * @param address
     * @param tokenId
     * @return
     * @throws IOException
     */
    public BigInteger balanceOf(String address, BigInteger tokenId) throws IOException {
        return Commons.resultBigInteger(
                ethContractUtil,
                contractAddress,
                EthAbiCodecTool.getInputData(
                        "balanceOf",
                        new Address(address),
                        new Uint256(tokenId)
                )
        );
    }

    /**
     * xref:ROOT:erc1155.adoc#batch-operations[Batched] version of {balanceOf}.
     *
     * @param addresses
     * @param tokenIds
     * @return
     * @throws Exception
     */
    public List<BigInteger> balanceOfBatch(List<String> addresses, List<BigInteger> tokenIds) throws Exception {
        if (addresses == null || tokenIds == null || addresses.size() != tokenIds.size()) {
            throw new Exception("ERC1155: accounts and ids length mismatch");
        }

        Address[] addressArray = new Address[addresses.size()];
        Uint256[] tokenIdArray = new Uint256[tokenIds.size()];

        for (int i = 0; i < addresses.size(); i++) {
            addressArray[i] = new Address(addresses.get(i));
        }

        for (int i = 0; i < tokenIds.size(); i++) {
            tokenIdArray[i] = new Uint256(tokenIds.get(i));
        }

        List<Type> result = ethContractUtil.select(contractAddress,
                EthAbiCodecTool.getInputData(
                        "balanceOfBatch",
                        new DynamicArray(Address.class, addressArray),
                        new DynamicArray(Uint256.class, tokenIdArray)
                ),
                new TypeReference<DynamicArray<Uint256>>() {}
        );

        if (result == null || result.size() < 1 || result.get(0) == null || result.get(0).getValue() == null) {
            return null;
        }

        List<BigInteger> resultArray = new ArrayList<>();

        List<Type> resultList = (List<Type>) result.get(0).getValue();
        if (resultList == null || resultList.size() < 1) {
            return resultArray;
        }

        for (Type obj : resultList) {
            if (obj == null) {
                continue;
            }

            if(obj.getValue() != null){
                resultArray.add(new BigInteger(obj.getValue().toString()));
            } else {
                resultArray.add(BigInteger.ZERO);
            }
        }

        return resultArray;
    }

    /**
     * Returns true if `operator` is approved to transfer ``account``'s tokens.
     *
     * @param owner
     * @param spender
     * @return
     * @throws IOException
     */
    public Boolean isApprovedForAll(String owner, String spender) throws IOException {
        return erc721Contract.isApprovedForAll(owner, spender);
    }

    /**
     * Grants or revokes permission to `operator` to transfer the caller's tokens, according to `approved`,
     *
     * Emits an {ApprovalForAll} event.
     *
     * @param to
     * @param approved
     * @param senderAddress
     * @param privateKey
     * @param gasPrice
     * @param gasLimit
     * @return
     * @throws Exception
     */
    public SendResultModel setApprovalForAll(String to, Boolean approved, String senderAddress, String privateKey, BigInteger gasPrice, BigInteger gasLimit) throws Exception {
        return erc721Contract.setApprovalForAll(to, approved, senderAddress, privateKey, gasPrice, gasLimit);
    }

    /**
     * Transfers `amount` tokens of token type `id` from `from` to `to`.
     *
     * Emits a {TransferSingle} event.
     *
     * @param from
     * @param to
     * @param tokenId
     * @param amount
     * @param data
     * @param senderAddress
     * @param privateKey
     * @param gasPrice
     * @param gasLimit
     * @return
     * @throws Exception
     */
    public SendResultModel safeTransferFrom(String from, String to, BigInteger tokenId, BigInteger amount, byte[] data, String senderAddress, String privateKey, BigInteger gasPrice, BigInteger gasLimit) throws Exception {
        return otherTransaction(
                senderAddress,
                privateKey,
                gasPrice,
                gasLimit,
                EthAbiCodecTool.getInputData(
                        "safeTransferFrom",
                        new Address(from),
                        new Address(to),
                        new Uint256(tokenId),
                        new Uint256(amount),
                        new DynamicBytes(data)
                )
        );
    }

    /**
     * xref:ROOT:erc1155.adoc#batch-operations[Batched] version of {safeTransferFrom}.
     *
     * Emits a {TransferBatch} event.
     *
     * @param from
     * @param to
     * @param tokenIds
     * @param amounts
     * @param data
     * @param senderAddress
     * @param privateKey
     * @param gasPrice
     * @param gasLimit
     * @return
     * @throws Exception
     */
    public SendResultModel safeBatchTransferFrom(String from, String to, List<BigInteger> tokenIds, List<BigInteger> amounts, byte[] data, String senderAddress, String privateKey, BigInteger gasPrice, BigInteger gasLimit) throws Exception {
        if (tokenIds == null || amounts == null || tokenIds.size() != amounts.size()) {
            throw new Exception("ERC1155: ids and amounts length mismatch");
        }

        Uint256[] tokenIdArray = new Uint256[tokenIds.size()];
        Uint256[] amountArray = new Uint256[amounts.size()];

        for (int i = 0; i < tokenIds.size(); i++) {
            tokenIdArray[i] = new Uint256(tokenIds.get(i));
        }

        for (int i = 0; i < amounts.size(); i++) {
            amountArray[i] = new Uint256(amounts.get(i));
        }

        return otherTransaction(
                senderAddress,
                privateKey,
                gasPrice,
                gasLimit,
                EthAbiCodecTool.getInputData(
                        "safeBatchTransferFrom",
                        new Address(from),
                        new Address(to),
                        new DynamicArray(Uint256.class, tokenIdArray),
                        new DynamicArray(Uint256.class, amountArray),
                        new DynamicBytes(data)
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
    public List<Type> otherSelect(String inputData, TypeReference... outputTypes) throws IOException {
        return Commons.otherSelect(ethContractUtil, contractAddress, inputData, outputTypes);
    }

    /**
     * Calling custom functions to write contracts
     *
     * @param senderAddress
     * @param privateKey
     * @param gasPrice
     * @param gasLimit
     * @param inputData
     * @return
     * @throws Exception
     */
    public SendResultModel otherTransaction(String senderAddress, String privateKey, BigInteger gasPrice, BigInteger gasLimit, String inputData) throws Exception {
        return Commons.otherTransaction(ethContractUtil, contractAddress, senderAddress, privateKey, gasPrice, gasLimit, inputData);
    }
}
