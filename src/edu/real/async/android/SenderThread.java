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

public abstract class SenderThread extends Thread {

    protected static final int STATE_G_TEST_SENT = 1;
    protected static final int STATE_CONNECTED = 0;
    protected static final int STATE_ID_SENT = 2;
    protected Context ctx;
    protected int state;
    protected PrintWriter sOut;
    protected BufferedReader sIn;
    protected boolean working;
    protected String device_id;

    public SenderThread(Context _ctx, String _device_id) {
        ctx = _ctx;
        device_id = _device_id;
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
            sOut.println("I" + device_id);
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
                    send();
                    working = false;
                    break;
                }
            }

            ss.close();
        } catch (IOException e) {
            Log.e("net", e.getMessage());
        }

    }

    protected abstract void send();

    protected void sendCursor(
            Cursor cursor,
            String packet_prefix,
            String expected_response,
            String data_description,
            String log_tag)
    {
        if (cursor.moveToFirst()) { // must check the result to prevent
            // exception
            do {
                JSONObject o_item = new JSONObject();
                for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
                    try {
                        o_item.put(cursor.getColumnName(idx),
                                cursor.getString(idx));
                    } catch (JSONException e) {
                        Log.e(log_tag, "Putting to JSONObject failed. "
                                + e.getMessage());
                        o_item = null;
                        break;
                    }
                }
                if (o_item == null) {
                    continue;
                }
                sOut.println(packet_prefix + o_item.toString());
                String resp;
                try {
                    resp = sIn.readLine();
                } catch (IOException e) {
                    Log.e(log_tag, "Server response reading failed. "
                            + e.getMessage());
                    working = false;
                    break;
                }
                if (resp == null) {
                    Log.e(log_tag,
                            "Server responsed nothing after sending of "
                                    + data_description + ": "
                                    + resp);
                    working = false;
                    break;
                }
                if (!resp.equals(expected_response)) {
                    Log.e(log_tag,
                            "Unexpected server response after sending of "
                                    + data_description + ": "
                                    + resp);
                    working = false;
                    break;
                }
            } while (cursor.moveToNext());
        } else {
            // empty box, no SMS
        }
    }

    protected void sendAllContent(Uri uri,
            String packet_prefix,
            String expected_response,
            String data_description,
            String log_tag)
    {
        Cursor cursor = ctx.getContentResolver().query(
               uri, null, null, null, null);
        sendCursor(cursor, packet_prefix, expected_response, data_description,
                    log_tag);
    }

    protected void sendAllContent(String uri_str,
            String packet_prefix,
            String expected_response,
            String data_description,
            String log_tag)
    {
        Uri uri = Uri.parse(uri_str);
        sendAllContent(uri, packet_prefix, expected_response, data_description,
                    log_tag);
    }
}
