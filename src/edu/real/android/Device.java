package edu.real.android;

import android.os.Build;
import edu.real.string.StringTools;

public class Device {

    /* Based on
     * https://stackoverflow.com/questions/1995439/get-android-phone-model-programmatically
     * */
    // TODO: use https://github.com/jaredrummler/AndroidDeviceNames
    /** Returns the consumer friendly device name */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return StringTools.capitalize(model);
        }
        return StringTools.capitalize(manufacturer) + " " + model;
    }
}
