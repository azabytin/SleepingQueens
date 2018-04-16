package com.example.azabytin.SleepingQueens;

import android.os.Handler;
import android.os.Message;

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

    protected BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<Message>();

    public UdpTaskSocket( Handler uiThreadHandler_ )
    {
        uiThreadHandler = uiThreadHandler_;

    }

    protected Handler uiThreadHandler;

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
                gameLogic = new GameLogic();

                Message message = uiThreadHandler.obtainMessage();
                message.obj = gameLogic;
                uiThreadHandler.sendMessage(message);

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

    private void serverLoop(GameLogic gameLogic){
        try {
            ServerSocketSerializer serverSerializer = new ServerSocketSerializer();

            while (true) {
                serverSerializer.accept();
                serverSerializer.writeGameLogic(gameLogic);
                ArrayList<Card> cardsToPlay = serverSerializer.readCardsToPlay();

                if( cardsToPlay.size() > 0 ){
                    Message message = uiThreadHandler.obtainMessage();
                    message.obj = cardsToPlay;
                    uiThreadHandler.sendMessage(message);
                }
            }
        }
        catch (Exception e){
            String s = e.getMessage();
        }
    }

    private void clientLoop(String host){

        ClientGameLogic clientLogic = new ClientGameLogic(messageQueue);
        while(true){
            try{
                ClientSocketSerializer clientSerializer = new ClientSocketSerializer();
                if( clientSerializer.connect(host) ) {
                    GameLogic gameLogic = clientSerializer.readGameLogic();
                    Message message = uiThreadHandler.obtainMessage();
                    clientLogic.Init(gameLogic);
                    message.obj = clientLogic;
                    uiThreadHandler.sendMessage(message);

                    clientSerializer.writeCardsToPlay(messageQueue.poll(100, TimeUnit.MILLISECONDS));
                }
                Thread.sleep(300);
                clientSerializer.clean();
            }catch (Exception e){
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
            try{
                clean();
            } catch (Exception e){}

            sChannel = ssChannel.accept();
        }

        public ArrayList<Card> readCardsToPlay() throws java.io.IOException, java.lang.ClassNotFoundException
        {
            if(ois == null)
                ois = new ObjectInputStream(sChannel.socket().getInputStream());

            Object o = ois.readObject();
            return (ArrayList<Card>) o;
        }

        public void writeGameLogic(GameLogic gameLogic) throws java.io.IOException
        {
            if( oos == null )
                oos = new ObjectOutputStream(sChannel.socket().getOutputStream());
            oos.writeObject(gameLogic);
        }
        public void clean() throws java.io.IOException
        {
            if( oos != null ) {
                oos.close();
            }
            if( ois != null ){
                ois.close();
            }
            if(sChannel != null){
                sChannel.close();
            }
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
        }

        public boolean connect( String host) throws java.io.IOException
        {
            return  sChannel.connect(new InetSocketAddress(host, 50000));
        }

        public void writeCardsToPlay(Message msg) throws java.io.IOException, java.lang.ClassNotFoundException
        {
            if( oos == null )
                oos = new ObjectOutputStream(sChannel.socket().getOutputStream());

            ArrayList<Card> cardsToPlay = new ArrayList<Card>();
            if( msg != null ){
                cardsToPlay = (ArrayList<Card>)msg.obj;
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

        public void clean() throws java.io.IOException
        {
            if( oos != null ) {
                oos.close();
            }
            if( ois != null ){
                ois.close();
            }
            if(sChannel != null){
                sChannel.close();
            }
        }
    }
}