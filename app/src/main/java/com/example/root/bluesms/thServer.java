package com.example.root.bluesms;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by root on 09/09/15.
 * class that create a bluetooth server and wait client incoming connection
 */
public class thServer extends Thread {
    private BluetoothServerSocket sockServ;
    private List<thClient> lstClient = new ArrayList<thClient>();
    private Context context =null;

    public thServer(BluetoothAdapter adapter, UUID uuid, Context ctx) {
        super();
        context = ctx;
        Log.println(Log.ASSERT, "thServer", "Lancement d'un thread pour gerer le server");
        try {
            sockServ = adapter.listenUsingRfcommWithServiceRecord("BlueSms", uuid);
        } catch (IOException e) {
            Log.println(Log.ERROR, "thServer constructor", String.valueOf(e));
        }
    }

    @Override
    public void run() {
        BluetoothSocket sock = null;

        super.run();
        Log.println(Log.ASSERT, "launch server", "boucle sur le accept");
        while(!Thread.interrupted()) {
            try {
                sock = sockServ.accept();
                if (sock != null) {
                    Log.d("CONNECTED", "Connected bluetooth");
                    thClient client = new thClient(sock, context);

                    try {
                        Log.println(Log.ASSERT, "thClient : ", "envoie du message de bienvenue");
//                        {"header":{"size":"5","type":"message"},"content":"salut"}
//                        {"header":{"size":"5","type":"numero"},"content":{"num":"0677564892","name":"toto"}}

                        byte[] byteString = ("hello world !!!").getBytes();
                        sock.getOutputStream().write(byteString);
                    } catch (IOException e) {
                        Log.println(Log.ASSERT,"Erreur de bienvenue", e.getMessage());
                    }

                    lstClient.add(client);
                    client.start();
                }
            } catch (IOException e) {
                Log.println(Log.ERROR,"thServer run","erreur lors du accept");
            }
        }

        try {
            sockServ.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            sockServ.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * method that return the list of all client
     * @return List<thClient>
     */
    public List<thClient> getClients() {
        return lstClient;
    }
}
