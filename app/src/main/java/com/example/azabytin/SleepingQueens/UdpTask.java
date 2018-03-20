package com.example.azabytin.SleepingQueens;

import android.os.Handler;
import android.os.Message;

import java.net.DatagramSocket;

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

            int gameType = 0;

            if( otherSideSeed < broadcastPkt.getSeed() ){
                gameType = 1;
            }

            // Sending a message back to the service via handler.
            Message message = uiThreadHandler.obtainMessage();
            message.obj = otherHost;
            message.arg1 = gameType;
            uiThreadHandler.sendMessage(message);
        }
        catch(Exception ex)
        {
            String s = ex.getMessage();
        }
    }
}