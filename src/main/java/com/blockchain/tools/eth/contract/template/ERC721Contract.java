package com.blockchain.tools.eth.contract.template;

import com.blockchain.tools.eth.codec.EthAbiCodecTool;
import com.blockchain.tools.eth.contract.util.EthContractUtil;
import com.blockchain.tools.eth.contract.util.model.SendModel;
import com.blockchain.tools.eth.contract.util.model.SendResultModel;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

/**
 * Calling ERC721 contracts
 */
public class ERC721Contract {

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

    private ERC721Contract(Web3j web3j, String contractAddress) {
        this.contractAddress = contractAddress;
        this.ethContractUtil = ethContractUtil.builder(web3j);
    }

    public static ERC721Contract builder(Web3j web3j, String contractAddress) {
        return new ERC721Contract(web3j, contractAddress);
    }

    /**
     * Returns the number of tokens in ``owner``'s account.
     *
     * @param address
     * @return
     * @throws IOException
     */
    public BigInteger balanceOf(String address) throws Exception {
        return Commons.resultBigInteger(
                ethContractUtil,
                contractAddress,
                EthAbiCodecTool.getInputData(
                        "balanceOf",
                        new Address(address)
                )
        );
    }

    /**
     * Returns the owner of the `tokenId` token.
     *
     * @param tokenId
     * @return
     * @throws IOException
     */
    public String ownerOf(BigInteger tokenId) throws Exception {
        return Commons.resultAddress(ethContractUtil, contractAddress,
                EthAbiCodecTool.getInputData(
                        "ownerOf",
                        new Uint256(tokenId)
                )
        );
    }

    /**
     * Returns if the `operator` is allowed to manage all of the assets of `owner`.
     *
     * @param owner
     * @param spender
     * @return
     * @throws IOException
     */
    public Boolean isApprovedForAll(String owner, String spender) throws Exception {
        return Commons.resultBool(ethContractUtil, contractAddress,
                EthAbiCodecTool.getInputData(
                        "isApprovedForAll",
                        new Address(owner),
                        new Address(spender)
                )
        );
    }

    /**
     * Returns the account approved for `tokenId` token.
     *
     * @param tokenId
     * @return
     * @throws IOException
     */
    public String getApproved(BigInteger tokenId) throws Exception {
        return Commons.resultAddress(ethContractUtil, contractAddress,
                EthAbiCodecTool.getInputData(
                        "getApproved",
                        new Uint256(tokenId)
                )
        );
    }

    /**
     * Safely transfers `tokenId` token from `from` to `to`.
     *
     * @param from
     * @param to
     * @param tokenId
     * @param data
     * @param sendModel
     * @return
     * @throws Exception
     */
    public SendResultModel safeTransferFrom(String from, String to, BigInteger tokenId, byte[] data, SendModel sendModel) throws Exception {
        return otherTransaction(
                sendModel,
                EthAbiCodecTool.getInputData(
                        "safeTransferFrom",
                        new Address(from),
                        new Address(to),
                        new Uint256(tokenId),
                        new DynamicBytes(data)
                )
        );
    }

    /**
     * Safely transfers `tokenId` token from `from` to `to`, checking first that contract recipients
     * are aware of the ERC721 protocol to prevent tokens from being forever locked.
     *
     * @param from
     * @param to
     * @param tokenId
     * @param sendModel
     * @return
     * @throws Exception
     */
    public SendResultModel safeTransferFrom(String from, String to, BigInteger tokenId, SendModel sendModel) throws Exception {
        return otherTransaction(
                sendModel,
                EthAbiCodecTool.getInputData(
                        "safeTransferFrom",
                        new Address(from),
                        new Address(to),
                        new Uint256(tokenId)
                )
        );
    }

    /**
     * Transfers `tokenId` token from `from` to `to`.
     *
     * @param from
     * @param to
     * @param tokenId
     * @param sendModel
     * @return
     * @throws Exception
     */
    public SendResultModel transferFrom(String from, String to, BigInteger tokenId, SendModel sendModel) throws Exception {
        return otherTransaction(
                sendModel,
                EthAbiCodecTool.getInputData(
                        "transferFrom",
                        new Address(from),
                        new Address(to),
                        new Uint256(tokenId)
                )
        );
    }

    /**
     * Gives permission to `to` to transfer `tokenId` token to another account.
     * The approval is cleared when the token is transferred.
     *
     * Only a single account can be approved at a time, so approving the zero address clears previous approvals.
     *
     * @param to
     * @param tokenId
     * @param sendModel
     * @return
     * @throws Exception
     */
    public SendResultModel approve(String to, BigInteger tokenId, SendModel sendModel) throws Exception {
        return otherTransaction(
               sendModel,
                EthAbiCodecTool.getInputData(
                        "approve",
                        new Address(to),
                        new Uint256(tokenId)
                )
        );
    }

    /**
     * Approve or remove `operator` as an operator for the caller.
     * Operators can call {transferFrom} or {safeTransferFrom} for any token owned by the caller.
     *
     * @param to
     * @param approved
     * @param sendModel
     * @return
     * @throws Exception
     */
    public SendResultModel setApprovalForAll(String to, Boolean approved, SendModel sendModel) throws Exception {
        return otherTransaction(
                sendModel,
                EthAbiCodecTool.getInputData(
                        "setApprovalForAll",
                        new Address(to),
                        new Bool(approved)
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
