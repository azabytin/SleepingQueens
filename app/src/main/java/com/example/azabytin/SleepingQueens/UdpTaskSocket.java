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

    BlockingQueue<Message> messaQequeue = new LinkedBlockingQueue<Message>();

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

                        s.receive(incomingPkt.Pkt());
                        if( incomingPkt.isFromMyHost() ){
                            continue;
                        } else {
                            waitingResponse = false;
                            otherHost = incomingPkt.getOtherHost();
                            s.send(broadcastPkt.Pkt());
                        }

                }catch(Exception ex){
                    continue;
                }
            }
            byte[] data =  incomingPkt.Pkt().getData();
            byte otherSideSeed = data[ 0 ];
            otherSideSeed = otherSideSeed;

            int gameType;

            if( otherSideSeed < broadcastPkt.getSeed() ){
                gameType = 1;
            }else{
                gameType = 0;
            }

            CallHandler callHandler = new CallHandler();
            Client client = null;
            Server server;
            GameLogic gameLogic = null;

            if( gameType == 0 ){
                Thread.sleep(1000);
                clientLoop(otherHost);

            } else {
                gameLogic = new GameLogic();

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
            ServerSocketSerializer ssChannel = new ServerSocketSerializer();

            while (true) {
                ssChannel.accept();
                ssChannel.writeGameLogic(gameLogic);
                ArrayList<Card> cardsToPlay = ssChannel.readCardsToPlay();

                if( cardsToPlay.size() > 0 ){
                    Message message = uiThreadHandler.obtainMessage();
                    message.obj = cardsToPlay;
                    uiThreadHandler.sendMessage(message);
                }
            }

        }catch (Exception e){}
    }

    private void clientLoop(String host){

        ClientGameLogic clientLogic = new ClientGameLogic( messaQequeue);
        while(true){
            try{
                ClientSocketSerializer sChannel = new ClientSocketSerializer();
                if (sChannel.connect( host)) {

                    GameLogic gameLogic = sChannel.readGameLogic();
                    Message message = uiThreadHandler.obtainMessage();
                    clientLogic.Init(gameLogic);
                    message.obj = clientLogic;
                    uiThreadHandler.sendMessage(message);

                    sChannel.writeCardsToPlay( messaQequeue.poll(100, TimeUnit.MILLISECONDS) );
                    Thread.sleep(1000);
                }
            }catch (Exception e){
                String s = e.getMessage();
            }
        }
    }

    private class ServerSocketSerializer
    {
        private ServerSocketChannel ssChannel;
        private SocketChannel sChannel;

        public ServerSocketSerializer() throws java.io.IOException
        {

            ssChannel = ServerSocketChannel.open();
            ssChannel.configureBlocking(true);
            ssChannel.socket().bind(new InetSocketAddress(50000));
        }

        public void accept()throws java.io.IOException
        {
            sChannel = ssChannel.accept();
        }

        public ArrayList<Card> readCardsToPlay() throws java.io.IOException, java.lang.ClassNotFoundException
        {
            ObjectInputStream ois = new ObjectInputStream(sChannel.socket().getInputStream());
            Object o = ois.readObject();
            ois.close();
            return (ArrayList<Card>) o;
        }

        public void writeGameLogic(GameLogic gameLogic) throws java.io.IOException
        {
            ObjectOutputStream oos = new
                    ObjectOutputStream(sChannel.socket().getOutputStream());
            oos.writeObject(gameLogic);
            oos.close();
        }
    }

    private class ClientSocketSerializer
    {
        private SocketChannel sChannel;
        public ClientSocketSerializer() throws java.io.IOException
        {

            sChannel = SocketChannel.open();
            sChannel.configureBlocking(true);


        }
        public boolean connect(String host)throws java.io.IOException
        {
            return sChannel.connect(new InetSocketAddress(host, 50000));
        }
        public void writeCardsToPlay(Message msg) throws java.io.IOException, java.lang.ClassNotFoundException
        {
            ObjectOutputStream  oos = new ObjectOutputStream(sChannel.socket().getOutputStream());
            ArrayList<Card> cardsToPlay = new ArrayList<Card>();
            if( msg != null ){
                cardsToPlay = (ArrayList<Card>)msg.obj;
            }
            oos.writeObject(cardsToPlay);
            oos.close();
        }

        public GameLogic readGameLogic() throws java.io.IOException, java.lang.ClassNotFoundException
        {
            ObjectInputStream ois = new ObjectInputStream(sChannel.socket().getInputStream());
            GameLogic o = (GameLogic)ois.readObject();
            ois.close();
            return o;
        }
    }
}