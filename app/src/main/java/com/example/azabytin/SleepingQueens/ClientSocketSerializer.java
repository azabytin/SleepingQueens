package com.example.azabytin.SleepingQueens;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

public class ClientSocketSerializer
{
    private SocketChannel sChannel;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

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

