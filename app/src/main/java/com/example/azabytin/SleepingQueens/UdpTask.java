package com.example.azabytin.SleepingQueens;

import android.os.Handler;
import android.os.Message;

import java.net.DatagramSocket;

import lipermi.handler.CallHandler;
import lipermi.net.Client;
import lipermi.net.Server;

/**
 * Created by azabytin on 15.03.2018.
 */

class UdpTask extends Thread  {

    public UdpTask( Handler uiThreadHandler_ )
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
            iGameLogic gameLogic = null;

            gameType = 0;
            if( gameType == 0 ){
                Thread.sleep(1000);
                client = new Client(otherHost, 10000, callHandler);
                iGameLogic clientLogic = (iGameLogic) client.getGlobal(iGameLogic.class);
                gameLogic = new ClientGameLogic( clientLogic );

            } else {
                server = new Server();
                gameLogic = new GameLogic();
                callHandler.registerGlobal(iGameLogic.class, gameLogic);
                server.bind(10000, callHandler);
            }


            // Sending a message back to the service via handler.
            Message message = uiThreadHandler.obtainMessage();
            message.obj = gameLogic;
            uiThreadHandler.sendMessage(message);

            while(true){
                Thread.sleep(1000);
                if( gameType == 0){
                    message = uiThreadHandler.obtainMessage();
                    iGameLogic clientLogic = (iGameLogic) client.getGlobal(iGameLogic.class);
                    message.obj = new ClientGameLogic( clientLogic );
                    uiThreadHandler.sendMessage(message);
                }

            }
        }
        catch(Exception ex)
        {
            String s = ex.getMessage();
        }
    }
}