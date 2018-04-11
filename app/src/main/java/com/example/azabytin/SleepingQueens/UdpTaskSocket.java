package com.example.azabytin.SleepingQueens;

import android.os.Handler;
import android.os.Message;

import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import lipermi.handler.CallHandler;
import lipermi.net.Client;
import lipermi.net.Server;

/**
 * Created by azabytin on 15.03.2018.
 */

class UdpTaskSocket extends Thread  {

    public UdpTaskSocket( Handler uiThreadHandler_ )
    {
        uiThreadHandler = uiThreadHandler_;

    }

    protected Handler uiThreadHandler;

    @Override
    public void run() {
        try {

            DatagramSocket s = new DatagramSocket(55555);
            s.setSoTimeout(100);

            NegotiationPkt broadcastPkt = new NegotiationPkt( true );
            NegotiationPkt incomingPkt = new NegotiationPkt( false);;

            boolean waitingResponse = true;
            String otherHost = "";
            while ( waitingResponse ) {
                s.send(broadcastPkt.Pkt());

                try{
                    while( true ) {
                        s.receive(incomingPkt.Pkt());
                        if( incomingPkt.isFromMyHost() ){
                            continue;
                        } else {
                            waitingResponse = false;
                            otherHost = incomingPkt.getOtherHost();
                            s.send(broadcastPkt.Pkt());
                        }
                    }
                }catch(Exception ex){
                    continue;
                }
            }
            byte[] data =  incomingPkt.Pkt().getData();
            byte otherSideSeed = data[ 0 ];
            otherSideSeed = otherSideSeed;

            int gameType = 1;

            if( otherSideSeed < broadcastPkt.getSeed() ){
                gameType = 1;
            }
            gameType = 0;

            CallHandler callHandler = new CallHandler();
            Client client = null;
            Server server;
            GameLogic gameLogic = null;

            //gameType = 0;
            if( gameType == 0 ){
                Thread.sleep(1000);
                clientLoop();

            } else {
                gameLogic = new GameLogic();
                // Sending a message back to the service via handler.
                Message message = uiThreadHandler.obtainMessage();
                message.obj = gameLogic;
                uiThreadHandler.sendMessage(message);
                serverLoop(gameLogic);
            }
        }
        catch(Exception ex)
        {
            String s = ex.getMessage();
        }
    }

    private void serverLoop(GameLogic gameLogic){
        try {
            ServerSocketChannel ssChannel = ServerSocketChannel.open();
            ssChannel.configureBlocking(true);
            ssChannel.socket().bind(new InetSocketAddress(50000));

            while (true) {
                SocketChannel sChannel = ssChannel.accept();

                ObjectOutputStream  oos = new
                        ObjectOutputStream(sChannel.socket().getOutputStream());
                oos.writeObject(gameLogic);
                oos.close();
            }

        }catch (Exception e){}
    }

    private void clientLoop(){

        while(true){
            try{
                SocketChannel sChannel = SocketChannel.open();
                sChannel.configureBlocking(true);
                if (sChannel.connect(new InetSocketAddress("localhost", 50000))) {

                    ObjectInputStream ois =
                            new ObjectInputStream(sChannel.socket().getInputStream());

                    GameLogic gameLogic = (GameLogic)ois.readObject();
                    Message message = uiThreadHandler.obtainMessage();
                    message.obj = new ClientGameLogic( gameLogic );
                    uiThreadHandler.sendMessage(message);
                    Thread.sleep(1000);

                }
            }catch (Exception e){}
        }
    }


}