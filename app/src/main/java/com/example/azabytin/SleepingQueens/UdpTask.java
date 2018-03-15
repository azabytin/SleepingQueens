package com.example.azabytin.SleepingQueens;

import android.os.Handler;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Random;

/**
 * Created by azabytin on 15.03.2018.
 */

class UdpTask implements Runnable {
    @Override
    public void run() {
        try {
            Random r = new Random();

            int server_port = 55555;
            String broadcast = NetUtils.getNetworkBroadcastAddress();
            InetAddress server_addr = InetAddress.getByName("192.168.0.255");
            int msg_length = 1;
            byte[] message = new byte[1];
            message[0] = (byte)r.nextInt(255);;
            DatagramSocket s = new DatagramSocket(55555);
            s.setSoTimeout(100);

            DatagramPacket broadcastPkt = new DatagramPacket(message, msg_length, server_addr, server_port);

            DatagramPacket incomingPkt;

            while ( true ) {
                s.send(broadcastPkt);
                byte[]  rxMessage = new byte[100];
                incomingPkt = new DatagramPacket(rxMessage, rxMessage.length );
                try{
                    s.receive( incomingPkt );
                }catch(Exception ex){
                    continue;
                }
                s.send(broadcastPkt);
                String otherSideAddr = incomingPkt.getAddress().getHostName();
                String myAddr = NetUtils.getIPAddress(true);
                if( otherSideAddr.equals(myAddr) ){
                    continue;
                } else {
                    break;
                }
            }
            byte[] data =  incomingPkt.getData();
            byte otherSideSeed = data[ 0 ];

        }
        catch(Exception ex)
        {

        }
    }
}