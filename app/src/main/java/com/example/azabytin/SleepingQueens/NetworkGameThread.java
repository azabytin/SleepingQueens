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

class NetworkGameThread extends Thread  {

    public class executePlayCards implements Runnable
    {
        private ArrayList<Card> cards;
        public executePlayCards(ArrayList<Card> _cards){
            cards = _cards;
        }

        public void run() {
            cardsToPlay = new ArrayList<Card>();
            cardsToPlay.addAll(cards);
        }
    }

    private ArrayList<Card> cardsToPlay = null;
    protected int loopCount = Integer.MAX_VALUE;
    protected GameLogic game;
    MainActivity parentmainActivity;

    public void stopThread(){
        loopCount = 2;
    }
    public NetworkGameThread( MainActivity _parentmainActivity, Handler uiThreadHandler_, GameLogic _game )
    {
        parentmainActivity = _parentmainActivity;
        game = _game;
        uiThreadHandler = uiThreadHandler_;
        Log.i("NetworkGameThread", "Created" );
    }

    protected Handler uiThreadHandler;
    protected Handler threadHandler = new Handler();

    @Override
    public void run() {
        try {
            Log.i("NetworkGameThread", "Run " + getId());
            DatagramSocket udpSocketForNegotiation = new DatagramSocket(55555);
            udpSocketForNegotiation.setSoTimeout(100);

            NegotiationPkt broadcastRequestPkt = new NegotiationPkt( true );
            NegotiationPkt responsePkt = new NegotiationPkt( false);;

            boolean waitingResponse = true;
            Log.i("NetworkGameThread", "Wainting for response " + getId());
            while ( waitingResponse && (loopCount-- > 0)) {
                udpSocketForNegotiation.send(broadcastRequestPkt.Pkt());

                try{
                    udpSocketForNegotiation.receive(responsePkt.Pkt());
                    if( responsePkt.isFromMyHost() ){
                        continue;
                    } else {
                        waitingResponse = false;
                        Log.i("NetworkGameThread", responsePkt.getPaierInfo() + getId());
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

            udpSocketForNegotiation.close();
            if( workAsServer  ){
                uiThreadHandler.post( parentmainActivity.new executeInitServerGameLogic(game));
                Log.i("NetworkGameThread", "Start as server" );
                serverLoop(game);
            }
            else {
                Thread.sleep(1000);
                Log.i("NetworkGameThread", "Start as client" );
                clientLoop( responsePkt.getOtherHost() );
            }

            Log.i("NetworkGameThread", "Thread terminated " + getId());
        }
        catch(Exception ex)
        {
            String s = ex.getMessage();
            Log.e("NetworkGameThread", "Thread terminated. Exception: " + s);
        }
    }

    private void serverLoop(GameLogic gameLogic) {

        Log.i("NetworkGameThread::serverLoop", "Start serverLoop" + getId());
        ServerSocketSerializer serverSerializer = null;
        try {
            serverSerializer = new ServerSocketSerializer();

        }catch (java.io.IOException e){
            Log.i("NetworkGameThread::serverLoop", "Create ServerSocketSerializer fail"+ getId());
        }
        Log.i("NetworkGameThread::serverLoop", "Create ServerSocketSerializer "+ getId());

        while (loopCount-- > 0) {
            try {

                Log.i("NetworkGameThread::serverLoop", "Accepting connection");
                serverSerializer.accept();
                Log.i("NetworkGameThread::serverLoop", "Accepted connection");

                serverSerializer.writeGameLogic(gameLogic);
                Log.i("NetworkGameThread::serverLoop", "Write game logic done");

                ArrayList<Card> cardsToPlay = serverSerializer.readCardsToPlay();
                Log.i("NetworkGameThread::serverLoop", "readCardsToPlay done");

                if (cardsToPlay.size() > 0) {
                    Log.i("NetworkGameThread::serverLoop", "readCardsToPlay have cards");
                    uiThreadHandler.post( parentmainActivity.new executePlayCards(cardsToPlay) );
                }
            } catch (Exception e) {
                Log.e("NetworkGameThread::serverLoop", "Exception");
            }
        }
        serverSerializer.close();
    }

    private void clientLoop(String host)throws java.lang.InterruptedException, java.io.IOException{

        while (loopCount-- > 0) {
            try {
                Thread.sleep(300);

                GameState clientLogic = new GameState(this, threadHandler);
                Log.i("NetworkGameThread::clientLoop", "start");
                ClientSocketSerializer clientSerializer = new ClientSocketSerializer();
                Log.i("NetworkGameThread::clientLoop", "Created ");
                clientSerializer.connect(host);

                Log.i("NetworkGameThread::clientLoop", "connected done");

                GameLogic gameLogic = clientSerializer.readGameLogic();
                Log.i("NetworkGameThread::clientLoop", "readGameLogic done");

                clientLogic.Init(gameLogic);
                uiThreadHandler.post(parentmainActivity.new executeUpdateClientGameLogic(clientLogic));

                clientSerializer.writeCardsToPlay();
                Log.i("NetworkGameThread::clientLoop", "writeCardsToPlay done");

            } catch (Exception e) {
                Log.i("NetworkGameThread::clientLoop", "Exception");
                Thread.sleep(300);
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
        public void close(){
            try {
                ssChannel.close();
            }catch (Exception e){}
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
                Log.e("NetworkGameThread::ServerSocketSerializer", "fail to readCardsToPlay");
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
            if (oos == null)
                oos = new ObjectOutputStream(sChannel.socket().getOutputStream());

            if (cardsToPlay != null) {
                oos.writeObject(cardsToPlay);
                cardsToPlay = null;
            } else {
                oos.writeObject(new ArrayList<Card>());
            }
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