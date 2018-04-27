package com.example.azabytin.SleepingQueens;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import lipermi.handler.CallHandler;
import lipermi.net.Client;
import lipermi.net.Server;

/**
 * Created by azabytin on 15.03.2018.
 */

class UdpTaskSocket extends Thread  {

    public class executePlayCards implements Runnable
    {
        private ArrayList<Card> cards;
        public executePlayCards(ArrayList<Card> _cards){
            cards = _cards;
        }
        public void run(){
            cardsToPlay = cards;
        }
    }

    private ArrayList<Card> cardsToPlay = null;
    protected int loopCount = Integer.MAX_VALUE;
    protected GameLogic game;

    public void stopThread(){
        loopCount = 2;
    }
    public UdpTaskSocket( Handler uiThreadHandler_, GameLogic _game )
    {
        game = _game;
        uiThreadHandler = uiThreadHandler_;

    }

    protected Handler uiThreadHandler;
    protected Handler threadHandler = new Handler();

    @Override
    public void run() {
        try {
            DatagramSocket udpSocketForNegotiation = new DatagramSocket(55555);
            udpSocketForNegotiation.setSoTimeout(100);

            NegotiationPkt broadcastRequestPkt = new NegotiationPkt( true );
            NegotiationPkt responsePkt = new NegotiationPkt( false);;

            boolean waitingResponse = true;
            while ( waitingResponse ) {
                udpSocketForNegotiation.send(broadcastRequestPkt.Pkt());

                try{
                    udpSocketForNegotiation.receive(responsePkt.Pkt());
                    if( responsePkt.isFromMyHost() ){
                        continue;
                    } else {
                        waitingResponse = false;
                        udpSocketForNegotiation.send(broadcastRequestPkt.Pkt());
                    }

                }catch(Exception ex){
                    continue;
                }
            }

            boolean workAsServer = false;
            if( NegotiationPkt.isIamServer( broadcastRequestPkt, responsePkt) ){
                workAsServer = true;
            }

            GameLogic gameLogic = null;
            if( workAsServer  ){
                uiThreadHandler.post(new MainActivity().new executeInitServerGameLogic(game));
                serverLoop(gameLogic);
            }
            else {
                Thread.sleep(1000);
                clientLoop( responsePkt.getOtherHost() );
            }
        }
        catch(Exception ex)
        {
            String s = ex.getMessage();
        }
    }

    private void serverLoop(GameLogic gameLogic) {

        Log.i("serverLoop", "Start serverLoop");
        ServerSocketSerializer serverSerializer = null;
        try {
            serverSerializer = new ServerSocketSerializer();

        }catch (java.io.IOException e){
            Log.i("serverLoop", "Create ServerSocketSerializer fail");
        }
        Log.i("serverLoop", "Create ServerSocketSerializer ");

        while (loopCount-- != 0) {
            try {

                Log.i("serverLoop", "Accepting connection");
                serverSerializer.accept();
                Log.i("serverLoop", "Accepted connection");

                serverSerializer.writeGameLogic(gameLogic);
                Log.i("serverLoop", "Write game logic done");

                ArrayList<Card> cardsToPlay = serverSerializer.readCardsToPlay();
                Log.i("serverLoop", "readCardsToPlay done");

                if (cardsToPlay.size() > 0) {
                    Log.i("serverLoop", "readCardsToPlay have cards");
                    uiThreadHandler.post( new MainActivity().new executePlayCards(cardsToPlay) );
                }
            } catch (Exception e) {
                Log.e("serverLoop", "Exception");
            }
        }
    }

    private void clientLoop(String host)throws java.lang.InterruptedException, java.io.IOException{

        while (loopCount-- != 0) {
            try {
                Thread.sleep(300);

                GameState clientLogic = new GameState(threadHandler);
                Log.i("clientLogic", "start");
                ClientSocketSerializer clientSerializer = new ClientSocketSerializer();
                Log.i("clientLogic", "Created ");
                clientSerializer.connect(host);

                Log.i("clientLogic", "connected done");

                GameLogic gameLogic = clientSerializer.readGameLogic();
                Log.i("clientLogic", "readGameLogic done");

                clientLogic.Init(gameLogic);
                uiThreadHandler.post(new MainActivity().new executeUpdateClientGameLogic(clientLogic));

                clientSerializer.writeCardsToPlay();
                Log.i("clientLogic", "writeCardsToPlay done");

            } catch (Exception e) {
                Log.i("clientLogic", "Exception");
                Thread.sleep(1000);
                String s = e.getMessage();
            }
        }
    }

    private class ServerSocketSerializer
    {
        private ServerSocketChannel ssChannel;
        private SocketChannel sChannel;
        private ObjectOutputStream  oos = null;
        private ObjectInputStream ois = null;

        public ServerSocketSerializer() throws java.io.IOException
        {

            ssChannel = ServerSocketChannel.open();
            ssChannel.configureBlocking(true);
            ssChannel.socket().bind(new InetSocketAddress(50000));
        }

        public void accept()throws java.io.IOException
        {
            sChannel = ssChannel.accept();
            sChannel.socket().setSoTimeout(300);
            oos = null;
            ois = null;
        }

        public ArrayList<Card> readCardsToPlay()
        {
            try {
                if (ois == null)
                    ois = new ObjectInputStream(sChannel.socket().getInputStream());

                Object o = ois.readObject();
                return (ArrayList<Card>) o;
            }catch (Exception e){
                Log.e("ServerSocketSerializer", "fail to readCardsToPlay");
                return null;
            }
        }

        public void writeGameLogic(GameLogic gameLogic) throws java.io.IOException
        {
            if( oos == null )
                oos = new ObjectOutputStream(sChannel.socket().getOutputStream());
            oos.writeObject(gameLogic);
        }
    }

    private class ClientSocketSerializer
    {
        private SocketChannel sChannel = null;
        private ObjectOutputStream  oos = null;
        private ObjectInputStream ois = null;

        public ClientSocketSerializer( ) throws java.io.IOException
        {

            sChannel = SocketChannel.open();
            sChannel.configureBlocking(true);
            sChannel.socket().setSoTimeout(300);
        }

        public boolean connect( String host) throws java.io.IOException
        {
            boolean res = sChannel.connect(new InetSocketAddress(host, 50000));
            return res;
        }

        public void writeCardsToPlay() throws java.io.IOException, java.lang.ClassNotFoundException
        {
            if( oos == null )
                oos = new ObjectOutputStream(sChannel.socket().getOutputStream());

            ArrayList<Card> cardsToPlayToSend = new ArrayList<Card>();
            if( cardsToPlay != null ){
                cardsToPlayToSend = cardsToPlay;
                cardsToPlay = null;
                Log.i("ClientSocketSerializer", "Have cards to play: " + cardsToPlay.size());
            }
            oos.writeObject(cardsToPlay);
        }

        public GameLogic readGameLogic() throws java.io.IOException, java.lang.ClassNotFoundException
        {
            if(ois == null)
                ois = new ObjectInputStream(sChannel.socket().getInputStream());

            GameLogic o = (GameLogic)ois.readObject();
            return o;
        }
    }
}