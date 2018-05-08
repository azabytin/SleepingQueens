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

class NetworkGameThread extends Thread {

    protected Integer loopCount = Integer.MAX_VALUE;
    protected GameLogic game;

    public void stopThread() {
        synchronized (loopCount) {
            loopCount = 1;
        }
    }

    protected boolean isRunning() {
        synchronized (loopCount) {
            loopCount--;
            return loopCount > 0;
        }
    }

    public NetworkGameThread(MainActivity _parentmainActivity, Handler uiThreadHandler_, GameLogic _game) {
        parentmainActivity = _parentmainActivity;
        game = _game;
        uiThreadHandler = uiThreadHandler_;
        Log.i("NetworkGameThread", "Created");
    }

    protected Handler uiThreadHandler;

    @Override
    public void run() {
        try {
            Log.i("NetworkGameThread", "Run " + getId());
            ClientServerNegotiator clientServerNegotiator = new ClientServerNegotiator();

            while( isRunning() && clientServerNegotiator.getGameType() == ClientServerNegotiator.GameType.UnknownGame ){
                Thread.sleep(10);
            }

            if (clientServerNegotiator.getGameType() == ClientServerNegotiator.GameType.ServerGame ) {
                serverLoop(game);
            } else {
                clientLoop(clientServerNegotiator.getPeerHostName());
            }

            Log.i("NetworkGameThread", "Thread terminated " + getId());
        } catch (Exception ex) {
            String s = ex.getMessage();
            Log.e("NetworkGameThread", "Thread terminated. Exception: " + s);
        }
    }

    private void serverLoop(GameLogic gameLogic) throws java.io.IOException {

        Log.i("NetworkGameThread", "serverLoop::Start serverLoop" + getId());

        uiThreadHandler.post(parentmainActivity.new executeInitServerGameLogic(game));
        ServerSocketSerializer serverSerializer = new ServerSocketSerializer();

        while (isRunning()) {
            try {

                serverSerializer.accept();
                serverSerializer.writeGameLogic(gameLogic);

                ArrayList<Card> cardsToPlay = serverSerializer.readCardsToPlay();
                if (cardsToPlay != null) {
                    uiThreadHandler.post(parentmainActivity.new executePlayCards(cardsToPlay));
                }
            } catch (Exception e) {
                Log.e("NetworkGameThread", "serverLoop::Exception");
            }
        }
        serverSerializer.close();
    }

    private void clientLoop(String host) throws java.lang.InterruptedException {
        Log.i("NetworkGameThread", "clientLoop::Start as client");
        while (isRunning()) {
            try {
                Thread.sleep(500);

                ClientSocketSerializer clientSerializer = new ClientSocketSerializer(host);

                GameState gameState = new GameState( host);
                gameState.InitFromGameLogic(clientSerializer.readGameLogic());

                uiThreadHandler.post(parentmainActivity.new executeUpdateClientGameState(gameState));

            } catch (Exception e) {
                Log.i("NetworkGameThread", "clientLoop::Exception");
                Thread.sleep(500);
                String s = e.getMessage();
            }
        }
    }

    private class ServerSocketSerializer {
        private ServerSocketChannel ssChannel;
        private SocketChannel sChannel;
        private ObjectOutputStream oos = null;
        private ObjectInputStream ois = null;

        public ServerSocketSerializer() throws java.io.IOException {

            ssChannel = ServerSocketChannel.open();
            ssChannel.configureBlocking(true);
            ssChannel.socket().bind(new InetSocketAddress(50000));
        }

        public void close() {
            try {
                ssChannel.close();
            } catch (Exception e) {
            }
        }

        public void accept() throws java.io.IOException {
            sChannel = ssChannel.accept();
            sChannel.socket().setSoTimeout(200);
            oos = new ObjectOutputStream(sChannel.socket().getOutputStream());
            ois = new ObjectInputStream(sChannel.socket().getInputStream());
        }

        public ArrayList<Card> readCardsToPlay() {
            try {
                return (ArrayList<Card>) ois.readObject();
            } catch (Exception e) {
                Log.e("NetworkGameThread", "ServerSocketSerializer::fail to readCardsToPlay");
                return null;
            }
        }

        public void writeGameLogic(GameLogic gameLogic) throws java.io.IOException {
            oos.writeObject(gameLogic);
        }
    }
}

