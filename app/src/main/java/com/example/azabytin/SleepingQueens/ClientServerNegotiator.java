package com.example.azabytin.SleepingQueens;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Collections;

public class ClientServerNegotiator {

    public String getServerHostName() {
        return allPeersAddresses.get(0);
    }

    public enum GameType{
        UnknownGame,
        ServerGame,
        ClientGame
    }

    private final ArrayList<String> allPeersAddresses = new ArrayList();

    private DatagramSocket udpSocketForNegotiation;
    private DatagramPacket broadcastPkt;

    public ClientServerNegotiator()
    {
        allPeersAddresses.add( NetUtils.getMyIPAddress() );
    }

    private void InitSocket()throws java.net.UnknownHostException, java.net.SocketException{
        udpSocketForNegotiation = new DatagramSocket(55555);
        udpSocketForNegotiation.setSoTimeout(200);
        udpSocketForNegotiation.setBroadcast(true);

        broadcastPkt = new DatagramPacket(new byte[]{0}, 1, NetUtils.getBroadcastAddr(), 55555);
    }

    public int getClientId(){
        for (int i = 0; i < allPeersAddresses.size(); i++){
            if( allPeersAddresses.get(i).equals( NetUtils.getMyIPAddress() ) ){
                return i;
            }
        }
        return 0;
    }

    public int checkForNewPeer() {

        DatagramPacket broadcastPkFromPeer = new DatagramPacket(new byte[1], 1);
        boolean hasNewPeer = false;

        try {
            InitSocket();
            udpSocketForNegotiation.send(broadcastPkt);

            while (!hasNewPeer) {
                udpSocketForNegotiation.receive(broadcastPkFromPeer);
                if (isPktFromNewPeer(broadcastPkFromPeer)) {
                    hasNewPeer = true;
                    udpSocketForNegotiation.send(broadcastPkt);
                    Thread.sleep(100);
                }
            }
        }catch (Exception ignored)
        {
            String s = ignored.toString();
        }

        udpSocketForNegotiation.close();

         AddPeerToPeersList( broadcastPkFromPeer.getAddress().getHostName() );
         return allPeersAddresses.size();
   }

    private void AddPeerToPeersList(String peerAddr ){
        if( allPeersAddresses.contains(peerAddr) ) {
            return;
        }
        allPeersAddresses.add(peerAddr);
        Collections.sort(allPeersAddresses);
    }

    private boolean isPktFromNewPeer(DatagramPacket pkt){

        String peerAddr = pkt.getAddress().getHostAddress();
        return !allPeersAddresses.contains(peerAddr );
    }

    public String getPeersInfoAsString(){
        StringBuilder sb = new StringBuilder();
        sb.append("MyIP:");
        sb.append(NetUtils.getMyIPAddress());
        sb.append(" peerIP:");
        sb.append(getServerHostName());

        return sb.toString();
    }

    public int getPeersNumber(){
        return allPeersAddresses.size();
    }

    public GameType getGameType( ){

        if( allPeersAddresses.get(0).equals( NetUtils.getMyIPAddress() )){
            return GameType.ServerGame;
        }
        else{
            return GameType.ClientGame;
        }
    }
}
