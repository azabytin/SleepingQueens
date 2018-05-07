package com.example.azabytin.SleepingQueens;

import android.os.Handler;
import android.util.Log;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

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
            try {
                if( !serverHost.isEmpty() ) {
                    new ClientSocketSerializer(serverHost).writeCardsToPlay(cards);
                }
            }
            catch (Exception e){}
        }
    }

    protected String serverHost = "";
    protected Integer  loopCount = Integer.MAX_VALUE;
    protected GameLogic game;
    MainActivity parentmainActivity;

    public void stopThread(){
        synchronized (loopCount) {
            loopCount = 1;
        }
    }
    protected boolean isRunning(){
        synchronized (loopCount) {
            loopCount--;
            return loopCount >0;
        }
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
            udpSocketForNegotiation.setSoTimeout(200);

            NegotiationPkt broadcastRequestPkt = new NegotiationPkt( true );
            NegotiationPkt responsePkt = new NegotiationPkt( false);

            boolean waitingResponse = true;
            Log.i("NetworkGameThread", "Wainting for response " + getId());
            while ( waitingResponse && isRunning() ) {
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

    private void serverLoop(GameLogic gameLogic) throws java.io.IOException {

        Log.i("NetworkGameThread", "serverLoop::Start serverLoop" + getId());
        ServerSocketSerializer serverSerializer = new ServerSocketSerializer();

        while (isRunning()) {
            try {

                serverSerializer.accept();
                serverSerializer.writeGameLogic(gameLogic);

                ArrayList<Card> cardsToPlay = serverSerializer.readCardsToPlay();
                if (cardsToPlay != null) {
                    uiThreadHandler.post( parentmainActivity.new executePlayCards(cardsToPlay) );
                }
            } catch (Exception e) {
                Log.e("NetworkGameThread", "serverLoop::Exception");
            }
        }
        serverSerializer.close();
    }

    private void clientLoop(String host)throws java.lang.InterruptedException {
        serverHost = host;

        while (isRunning()) {
            try {
                Thread.sleep(500);
                ClientSocketSerializer clientSerializer = new ClientSocketSerializer( host );

                GameState gameState = new GameState(this, threadHandler);
                gameState.InitFromGameLogic( clientSerializer.readGameLogic() );

                uiThreadHandler.post(parentmainActivity.new executeUpdateClientGameState(gameState));

            } catch (Exception e) {
                Log.i("NetworkGameThread", "clientLoop::Exception");
                Thread.sleep(500);
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
            sChannel.socket().setSoTimeout(200);
            oos = new ObjectOutputStream(sChannel.socket().getOutputStream());
            ois = new ObjectInputStream(sChannel.socket().getInputStream());
        }

        public ArrayList<Card> readCardsToPlay()
        {
            try {
                return (ArrayList<Card>) ois.readObject();
            }catch (Exception e){
                Log.e("NetworkGameThread", "ServerSocketSerializer::fail to readCardsToPlay");
                return null;
            }
        }

        public void writeGameLogic(GameLogic gameLogic) throws java.io.IOException
        {
            oos.writeObject(gameLogic);
        }
    }

    private class ClientSocketSerializer
    {
        private SocketChannel sChannel;
        private ObjectOutputStream  oos;
        private ObjectInputStream   ois;

        public ClientSocketSerializer( ) throws java.io.IOException
        {

            sChannel = SocketChannel.open();
            sChannel.configureBlocking(true);
            sChannel.socket().setSoTimeout(200);
        }

        public ClientSocketSerializer(String host ) throws java.io.IOException
        {
            this();
            connect(host);
        }


        public boolean connect( String host) throws java.io.IOException
        {
            boolean res = sChannel.connect(new InetSocketAddress(host, 50000));
            oos = new ObjectOutputStream(sChannel.socket().getOutputStream());
            ois = new ObjectInputStream(sChannel.socket().getInputStream());
            return res;
        }

        public void writeCardsToPlay( ArrayList<Card> cardsToPlay) throws java.io.IOException {
                oos.writeObject(cardsToPlay);
        }

        public GameLogic readGameLogic() throws java.io.IOException, java.lang.ClassNotFoundException
        {
            return (GameLogic)ois.readObject();
        }
    }
}