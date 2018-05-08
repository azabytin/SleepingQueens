package com.example.azabytin.SleepingQueens;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ClientServerNegotiator {

    public String getPeerHostName() {
        return peerHostName;
    }

    public enum GameType{
        UnknownGame,
        ServerGame,
        ClientGame
    }

    private DatagramSocket udpSocketForNegotiation;
    private GameType gameType = GameType.UnknownGame;
    private String peerHostName = "";

    public ClientServerNegotiator() throws java.net.UnknownHostException, java.net.SocketException
    {
        udpSocketForNegotiation = new DatagramSocket(55555);
        udpSocketForNegotiation.setSoTimeout(200);
        udpSocketForNegotiation.setBroadcast(true);
    }

    public GameType getGameType() {

        if( gameType !=  GameType.UnknownGame){
            return gameType;
        }

        DatagramPacket broadcastPkFromPeer = new DatagramPacket(new byte[1], 1);
        boolean hasPktFromPeer = false;
        try {
            DatagramPacket broadcastPkt = new DatagramPacket(new byte[]{0}, 1, NetUtils.getBroadcastAddr(), 55555);
            udpSocketForNegotiation.send(broadcastPkt);

            while (!hasPktFromPeer) {
                udpSocketForNegotiation.receive(broadcastPkFromPeer);
                if (isPktFromPeer(broadcastPkFromPeer)) {
                    hasPktFromPeer = true;
                    udpSocketForNegotiation.send(broadcastPkt);
                } else {
                    continue;
                }
            }
        }
        catch (Exception ex) {
        }

        if( !hasPktFromPeer ){
            gameType = GameType.UnknownGame;
            return gameType;
        }
        udpSocketForNegotiation.close();

        if( isIamServer( broadcastPkFromPeer ) ){
            gameType = GameType.ServerGame;
        }
        else {
            gameType = GameType.ClientGame;
        }
        peerHostName = broadcastPkFromPeer.getAddress().getHostName();
        return gameType;
    }

    public boolean isPktFromPeer(DatagramPacket pkt){

        String otherSideAddr = pkt.getAddress().getHostName();
        String myAddr = NetUtils.getIPAddress();
        return !otherSideAddr.equals(myAddr);
    }
    public String getPeerInfo(){
        StringBuilder sb = new StringBuilder();
        sb.append("MyIP:");
        sb.append(NetUtils.getIPAddress());
        sb.append(" peerIP:");
        sb.append(getPeerHostName());

        return sb.toString();
    }

    public boolean isIamServer( DatagramPacket responsePkt){

        String myhost = NetUtils.getIPAddress();
        String otherhost = responsePkt.getAddress().getHostName();
        return Integer.parseInt(myhost.split("\\.")[3] ) < Integer.parseInt(otherhost.split("\\.")[3] ) ;
    }
}
