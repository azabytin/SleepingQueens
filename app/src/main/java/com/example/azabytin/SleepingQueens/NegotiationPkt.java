package com.example.azabytin.SleepingQueens;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * Created by azabytin on 20.03.2018.
 */

public class NegotiationPkt {

    protected DatagramPacket pkt;


    public NegotiationPkt( boolean sendPkt )throws SocketException, UnknownHostException {
        if( sendPkt ){

            Random r = new Random();

            int server_port = 55555;
            //InetAddress server_addr = NetUtils.getBroadcast();
            InetAddress server_addr =  InetAddress.getByName( NetUtils.getNetworkBroadcastAddress() );
            int msg_length = 1;
            byte[] message = new byte[1];
            r.nextBytes (message);

            pkt = new DatagramPacket(message, msg_length, server_addr, server_port);
        } else {
            pkt = new DatagramPacket(new byte[100], 100);
        }
    }

    public DatagramPacket Pkt(){
        return pkt;
    }

    public byte getSeed(){
        return pkt.getData()[0];
    }

    public boolean isFromMyHost(){

        String otherSideAddr = pkt.getAddress().getHostName();
        String myAddr = NetUtils.getIPAddress(true);
        return otherSideAddr.equals(myAddr);
    }

    public String getOtherHost(){

        return pkt.getAddress().getHostName();
    }
}
