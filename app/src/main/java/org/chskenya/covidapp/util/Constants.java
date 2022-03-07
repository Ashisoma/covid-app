package org.chskenya.covidapp.util;

import android.content.Context;
import android.os.Environment;

import org.chskenya.covidapp.R;

import java.io.File;

public class Constants {
    public static final String UUID = "{5C536241-D67E-4393-BA26-87E198E215B5}";
    public static final String BASE_URL = "https://tibatekelezi.chskenya.org/";//
    public static final String MY_PREFS_NAME = "TibaTekeleziPrefs";

    public File DIRECTORY_PATH;
    public File ERROR_LOG_PATH;

    public Constants(Context context) {
        DIRECTORY_PATH = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                + context.getString(R.string.app_name));

//        CACHE_PATH = context.getFilesDir();
        ERROR_LOG_PATH = new File(DIRECTORY_PATH + File.separator + ".logs");

//        ERROR_LOG_PATH = new File(CACHE_PATH, context.getResources().getString(R.string.error_log_file));
    }
}