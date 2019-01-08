package edu.real.async.android;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class SMSSenderThread extends Thread {

    private static final int STATE_G_TEST_SENT = 1;
    private static final int STATE_CONNECTED = 0;
    private static final int STATE_ID_SENT = 2;
    private Context ctx;
    private int state;
    private PrintWriter sOut;
    private BufferedReader sIn;
    private boolean working;

    public SMSSenderThread(Context _ctx) {
        ctx = _ctx;
    }

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

            sOut = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(ss.getOutputStream())),
                    true);

            sIn = new BufferedReader(
                    new InputStreamReader(ss.getInputStream()));

            state = STATE_CONNECTED;
            // sOut.println("G");
            // state = STATE_G_TEST_SENT;
            sOut.println("Iinnosd6000#1");
            state = STATE_ID_SENT;

            working = true;
            while (working) {
                String msg = sIn.readLine();

                if (msg == null) {
                    continue;
                }

                switch (state) {
                case STATE_G_TEST_SENT:
                    if (!"g".equals(msg)) {
                        Log.e("protocol", "Unexpected server response " + msg);
                    }
                    working = false;
                    break;
                case STATE_ID_SENT:
                    if (!"i".contentEquals(msg)) {
                        Log.e("protocol",
                                "Identification failed with message " + msg);
                        working = false;
                        break;
                    }
                    sendSMS();
                    working = false;
                    break;
                }
            }

            ss.close();
        } catch (IOException e) {
            Log.e("net", e.getMessage());
        }

    }

    private void sendSMS() {
        // Refs: https://stackoverflow.com/questions/848728/how-can-i-read-sms-messages-from-the-device-programmatically-in-android/32153303
        // public static final String INBOX = "content://sms/inbox";
        // public static final String SENT = "content://sms/sent";
        // public static final String DRAFT = "content://sms/draft";
        Cursor cursor = ctx.getContentResolver().query(
                Uri.parse("content://sms/inbox"), null, null, null, null);

        if (cursor.moveToFirst()) { // must check the result to prevent
                                    // exception
            do {
                JSONObject o_sms = new JSONObject();
                for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
                    try {
                        o_sms.put(cursor.getColumnName(idx),
                                cursor.getString(idx));
                    } catch (JSONException e) {
                        Log.e("sms-send", "Putting to JSONObject failed. "
                                + e.getMessage());
                        o_sms = null;
                        break;
                    }
                }
                if (o_sms == null) {
                    continue;
                }
                sOut.println("S" + o_sms.toString());
                String resp;
                try {
                    resp = sIn.readLine();
                } catch (IOException e) {
                    Log.e("sms-send", "Server response reading failed. "
                            + e.getMessage());
                    working = false;
                    break;
                }
                if (resp == null) {
                    Log.e("sms-send",
                            "Server responsed nothing after SMS sent. "
                            + resp);
                    working = false;
                    break;
                }
                if (!resp.equals("s")) {
                    Log.e("sms-send",
                            "Unexpected server response after SMS sent. "
                            + resp);
                    working = false;
                    break;
                }
            } while (cursor.moveToNext());
        } else {
            // empty box, no SMS
        }
    }
}
