package edu.real.async.android;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.util.Log;

public class SMSSenderThread extends Thread {

    @Override
    public void run() {
        /*
         * References:
         * https://stackoverflow.com/questions/38162775/really-simple-tcp-client
         * https://github.com/CatalinPrata/funcodetuts/blob/master/AndroidTCPClient/app/src/main/java/ro/kazy/tcpclient/TcpClient.java
         */

        try {
            InetAddress serverAddr = InetAddress.getByName("192.168.0.17");

            Socket ss = new Socket(serverAddr, 4321);

            // sends the message to the server
            PrintWriter sOut = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(ss.getOutputStream())),
                    true);

            // receives the message which the server sends back
            BufferedReader sIn = new BufferedReader(
                    new InputStreamReader(ss.getInputStream()));

            int state = 0;
            sOut.println("G");
            state = 1;

            boolean working = true;
            while (working) {
                String msg = sIn.readLine();

                if (msg == null) {
                    continue;
                }

                switch (state) {
                case 1:
                    if (!"g".equals(msg)) {
                        Log.e("protocol", "Unexpected server response " + msg);
                    }
                    working = false;
                    break;
                }
            }

            ss.close();
        } catch (IOException e) {
            Log.e("net", e.getMessage());
        }

    }
}
