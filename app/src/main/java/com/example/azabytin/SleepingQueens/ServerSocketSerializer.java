package com.example.azabytin.SleepingQueens;

import android.util.Log;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

class ServerSocketSerializer {
    private ServerSocketChannel ssChannel;
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
        } catch (Exception ignored) {
        }
    }

    public void accept() throws java.io.IOException {
        SocketChannel sChannel = ssChannel.accept();
        sChannel.socket().setSoTimeout(300);
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

    public void writeGameLogic(iGame gameLogic) throws java.io.IOException {
        // TODO: Maybe oos.writeObject(gameLogic);???
        oos.writeObject(gameLogic);
    }
}

