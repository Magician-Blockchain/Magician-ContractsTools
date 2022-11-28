<h1> 
    <a href="https://magician-io.com">Magician-ContractsTools</a> ·
    <img src="https://img.shields.io/badge/licenes-MIT-brightgreen.svg"/>
    <img src="https://img.shields.io/badge/jdk-8+-brightgreen.svg"/>
    <img src="https://img.shields.io/badge/maven-3.5.4+-brightgreen.svg"/>
    <img src="https://img.shields.io/badge/release-master-brightgreen.svg"/>
</h1>

Magician-ContractsTools is a toolkit for calling smart contracts , you can very easily call smart contracts in Java programs for query and write operations .

There are three built-in standard contract templates, ERC20, ERC721, and ERC1155, which can help you get things done very quickly if you need to call the standard functions in these three contracts. In addition to the built-in contract templates, it is also easy to call custom contract functions if you need to do so, and we will continue to add standard templates later.

In addition, there are tools for InputData decoding and ETH query and transfer

It is planned to support three chains, ETH (BSC, POLYGON, etc.), SOL and TRON

## Running environment

JDK8+

## Documentation

[https://magician-io.com/chain](https://magician-io.com/chain)

## Example

### Importing dependencies
```xml
<dependency>
    <groupId>com.github.yuyenews</groupId>
    <artifactId>Magician-ContractsTools</artifactId>
    <version>1.0.1</version>
</dependency>

<!-- This is the logging package, you must have it or the console will not see anything, any logging package that can bridge with slf4j is supported -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-jdk14</artifactId>
    <version>1.7.12</version>
</dependency>
```

### Calling custom functions for contracts

Read Contract

```java
Web3j web3j = Web3j.build(new HttpService("https://data-seed-prebsc-2-s1.binance.org:8545"));
String contractAddress = "";

EthContractUtil ethContractUtil = EthContractUtil.builder(web3j);
        
// Query
List<Type> result = ethContractUtil.select(
        contractAddress, // Contract Address
        EthAbiCodecTool.getInputData(
            "balanceOf", // Name of the method to be called
            new Address(toAddress) // method, if there are multiple parameters, you can continue to pass the next parameter
        ),  // The inputData of the method to be called
        new TypeReference<Uint256>() {} // The return type of the method, if there is more than one return value, you can continue to pass the next parameter
);
```

Write Contract

```java
Web3j web3j = Web3j.build(new HttpService("https://data-seed-prebsc-2-s1.binance.org:8545"));
String contractAddress = "";

EthContractUtil ethContractUtil = EthContractUtil.builder(web3j);

// Write data to the contract
// gasPrice, gasLimit two parameters, if you want to use the default value can not pass, or pass null
// If not, don't pass both parameters, if you want to pass them, pass them together, if set to null, one can be null and one can have a value
SendResultModel sendResultModel = ethContractUtil.sendRawTransaction(
        SendModel.builder()
            .setSenderAddress("0xb4e32492E9725c3215F1662Cf28Db1862ed1EE84") // Address of the caller
            .setPrivateKey("")// Private key of senderAddress
            .setToAddress("0x428862f821b1A5eFff5B258583572451229eEeA6") // Contract Address
            .setValue(new BigInteger("1000000000")) // coin amount, If you want to use the default value, you can pass null directly or leave this parameter out.
            .setGasPrice(new BigInteger("1000")) // gasPrice，If you want to use the default value, you can pass null directly or leave this parameter out.
            .setGasLimit(new BigInteger("800000")) // gasLimit，If you want to use the default value, you can pass null directly or leave this parameter out.
        EthAbiCodecTool.getInputData(
            "transfer", // Name of the method to be called
            new Address(toAddress), // Parameter 1
            new Uint256(new BigInteger("1000000000000000000")) // Parameter 2，If there are other parameters, you can go ahead and pass in the next
        ) // The inputData of the method to be called
);

sendResultModel.getEthSendTransaction(); // Results after sending a transaction
sendResultModel.getEthGetTransactionReceipt(); // Results after the transaction is broadcast
```

### Contract Template

To save space, only some of the functions of ERC20 are used here as examples, you can visit the official website for details

Initialization Contract Template
```java
BigDecimal decimal = new BigDecimal("1000000000000000000");

Web3j web3j = Web3j.build(new HttpService("https://data-seed-prebsc-2-s1.binance.org:8545"));

ERC20Contract erc20Contract = ERC20Contract.builder(web3j, "0x428862f821b1A5eFff5B258583572451229eEeA6");
```

Read contract
```java
// Calling the totalSupply function
BigInteger total = erc20Contract.totalSupply();
System.out.println(new BigDecimal(total).divide(decimal, 2, BigDecimal.ROUND_UP));

// Calling the balanceOf function
BigInteger amount = erc20Contract.balanceOf("0xb4e32492E9725c3215F1662Cf28Db1862ed1EE84");
System.out.println(new BigDecimal(amount).divide(decimal, 2, BigDecimal.ROUND_UP));
```

Write contract
```java
// Calling the transfer function
SendResultModel sendResultModel = erc20Contract.transfer(
        "0x552115849813d334C58f2757037F68E2963C4c5e",
        new BigInteger("1000000000000000000"),
        SendModel.builder()
            .setSenderAddress("0xb4e32492E9725c3215F1662Cf28Db1862ed1EE84") // Address of the caller
            .setPrivateKey("")// Private key of senderAddress
            .setValue(new BigInteger("1000000000")) // coin amount, If you want to use the default value, you can pass null directly or leave this parameter out.
            .setGasPrice(new BigInteger("1000")) // gasPrice，If you want to use the default value, you can pass null directly or leave this parameter out.
            .setGasLimit(new BigInteger("800000")) // gasLimit，If you want to use the default value, you can pass null directly or leave this parameter out.
);
sendResultModel.getEthSendTransaction(); // Results after sending a transaction
sendResultModel.getEthGetTransactionReceipt(); // Results after the transaction is broadcast

// Calling the approve function
SendResultModel sendResultModel = erc20Contract.approve(
        "0x552115849813d334C58f2757037F68E2963C4c5e",
        new BigInteger("1000000000000000000"),
        SendModel.builder()
            .setSenderAddress("0xb4e32492E9725c3215F1662Cf28Db1862ed1EE84") // Address of the caller
            .setPrivateKey("")// Private key of senderAddress
            .setValue(new BigInteger("1000000000")) // coin amount, If you want to use the default value, you can pass null directly or leave this parameter out.
            .setGasPrice(new BigInteger("1000")) // gasPrice，If you want to use the default value, you can pass null directly or leave this parameter out.
            .setGasLimit(new BigInteger("800000")) // gasLimit，If you want to use the default value, you can pass null directly or leave this parameter out.
);
sendResultModel.getEthSendTransaction(); // Results after sending a transaction
sendResultModel.getEthGetTransactionReceipt(); // Results after the transaction is broadcast

// Calling the transferFrom function
SendResultModel sendResultModel = erc20Contract.transferFrom(
        "0xb4e32492E9725c3215F1662Cf28Db1862ed1EE84",
        "0x552115849813d334C58f2757037F68E2963C4c5e",
        new BigInteger("1000000000000000000"),
        SendModel.builder()
            .setSenderAddress("0xb4e32492E9725c3215F1662Cf28Db1862ed1EE84") // Address of the caller
            .setPrivateKey("")// Private key of senderAddress
            .setValue(new BigInteger("1000000000")) // coin amount, If you want to use the default value, you can pass null directly or leave this parameter out.
            .setGasPrice(new BigInteger("1000")) // gasPrice，If you want to use the default value, you can pass null directly or leave this parameter out.
            .setGasLimit(new BigInteger("800000")) // gasLimit，If you want to use the default value, you can pass null directly or leave this parameter out.
);

sendResultModel.getEthSendTransaction(); // Results after sending a transaction
sendResultModel.getEthGetTransactionReceipt(); // Results after the transaction is broadcast
```
