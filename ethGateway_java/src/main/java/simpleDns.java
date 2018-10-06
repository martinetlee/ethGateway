package ethdns;

import java.math.BigInteger;
import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Web3J to connect with blockchain
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;
import org.web3j.protocol.core.DefaultBlockParameter;

// JavaDns to serve as a basic dns
import org.xbill.DNS.*;

public class simpleDns{

	public static DatagramSocket udpSocket;

  	public static InetAddress op_IPAddr;
  	public static int op_port;
  	public static int serv_port;

  	public static DatagramPacket sendPacket;
	public static DatagramPacket receivePacket;

    public static byte [] sendData;
    public static byte [] receiveData;



  	public static void main(String[] args) {

		try{

			// Set up Socket
  			progLog("Setting Up UDP Socket");
			boolean tryport = true;
			int udpPort = 53; // start from a fixed number

			// dns special port -> 53

            while(tryport){
              	tryport = false;

                try{
                       	udpSocket = new DatagramSocket(udpPort);
               	}catch(SocketException se){
                	tryport = true;
                    udpPort += 1;
               	}
              }

			serv_port = udpSocket.getLocalPort();
			progLog("The service port is : " + serv_port);

        	receiveData = new byte[1024];
        	receivePacket = new DatagramPacket(receiveData, receiveData.length);


			// Using a existing wallet file
			String fileName = "UTC--2017-12-01T23-02-03.705000000Z--1e38338227b30d483dd9f74a3fe751329ca04be9.json";
			System.out.println("Opening Wallet : " + fileName);
		    Credentials credentials = WalletUtils.loadCredentials( "cs656fp", fileName);

		    // Since we're only reading, gas doesn't matter. So setting them arbitrarily.
		    BigInteger GAS_PRICE = new BigInteger("100");
		    BigInteger GAS_LIMIT = new BigInteger("100000");

			Web3j web3j = Web3j.build(new HttpService()); 
			BigInteger last_max_gaslimit = 
					web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf("latest"), true).send().getBlock().getGasLimit();

			System.out.println("Latest Gas Limit: " + last_max_gaslimit.toString());

					/*
							System.out.println("Creating Wallet");
							String fileName = WalletUtils.generateNewWalletFile( "cs656fp", new File("./"), false);
							System.out.println(fileName);
					*/


		    // Loading the deployed ENS contract
			MyENS2 myens = MyENS2.load("0xd5ab30a67003eec3480d28c7195df7eae91e37b6", web3j, credentials, GAS_PRICE, GAS_LIMIT );

			if(myens == null){
				System.out.println("contract returned is null");
			}


        	while(true){
				// Receive packet
	        	udpSocket.receive(receivePacket);

	        	// op stands for opponent
	        	op_IPAddr = receivePacket.getAddress();
	        	op_port = receivePacket.getPort();

	        	//#//progLog("DNS Packet intercepted");


	        	// Send into dnsjava to process
	        	Message dnsReq = new Message(receiveData);

				//#//progLog("\n\n\n==================================================");
	        	//#//progLog(dnsReq.toString());
				//#//progLog("\n==================================================\n\n\n");

	        	// Get what domain is the user querying
	        	Name queryName = dnsReq.getQuestion().getName();
	        	//progLog("" + queryName.getLabelString(0));
	        	//progLog("" + queryName.getLabelString(1));

	        	// labels() -1 is the one after the last . which is empty
	        	int lastLabel_id = queryName.labels()-2 ; 
	        	String lastLabel = queryName.getLabelString( lastLabel_id );

	       		// if we received meth domain, we'll use qnStr to perform query. 
	        	String qnStr = ""+dnsReq.getQuestion().getName();
	        	qnStr = qnStr.substring(0, qnStr.length()-1);
	        	//#//progLog("Question: " + qnStr );
		
				if(lastLabel.equals("meth")){

					//#//System.out.println("Oh, It's the meth domain!!");
					//#//System.out.println("Querying blockchain...");

					String qResult = myens.who_is(qnStr).send();

					//#//System.out.println("Who is '" + qnStr +"'? " + qResult );

					Message dnsResponse = new Message(dnsReq.getHeader().getID());

					//Let's pack a response message and send it back
					//#//progLog("\n\n\n==================================================");
	        		//#//progLog(dnsResponse.toString());
					//#//progLog("\n==================================================\n\n\n");

					//#//progLog("Setting Flag QR and AA of dnsResponse");
					dnsResponse.getHeader().setFlag(Flags.QR);
					dnsResponse.getHeader().setFlag(Flags.AA);
					//#//progLog("Setting response_ip to qResult of dnsResponse");
					InetAddress response_ip = InetAddress.getByName(qResult);


					ARecord answer = new ARecord( new Name(qnStr+"."), DClass.ANY, 100, response_ip );
					dnsResponse.addRecord(answer, Section.ANSWER);

					//#//progLog("\n\n\n==================================================");
	        		//#//progLog(dnsResponse.toString());
					//#//progLog("\n==================================================\n\n\n");

					// Let's send a response back to the Client
					byte [] sendData = dnsResponse.toWire();
	                sendPacket = new DatagramPacket(sendData, sendData.length, op_IPAddr, op_port);
    		    	udpSocket.send(sendPacket);

				}
				else{ // if it is not meth domain, let's pass it 

				}
			}
		}catch(Exception e){
			System.out.println("" + e);
		}

  	}


    public static void progLog(String logText){
		System.out.println("[ eDns ] " + logText);
    }


}