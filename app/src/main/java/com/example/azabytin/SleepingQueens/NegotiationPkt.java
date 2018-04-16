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


    public NegotiationPkt( boolean isRequestPkt )throws SocketException, UnknownHostException {
        if( isRequestPkt ){

            Random r = new Random();

            int server_port = 55555;
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
    public static boolean isIamServer(NegotiationPkt broadcastRequestPkt, NegotiationPkt responsePkt){

        byte[] data =  responsePkt.Pkt().getData();
        byte otherSideSeed = data[ 0 ];
        otherSideSeed = otherSideSeed;
        return otherSideSeed < broadcastRequestPkt.getSeed();
    }

    public String getOtherHost(){

        return pkt.getAddress().getHostName();
    }
    public String getMyHost(){

        return NetUtils.getIPAddress(true);
    }
}
