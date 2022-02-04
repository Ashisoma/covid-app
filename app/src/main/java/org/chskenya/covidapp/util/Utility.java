package org.chskenya.covidapp.util;

import android.content.Context;

import org.chskenya.covidapp.model.InitialData;

import java.io.BufferedWriter;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Utility {
    private final Context context;

    private static final String TAG = "Utility";
    private final SimpleDateFormat sdf;
    boolean append = true;
    private BufferedWriter writer;

    public Utility(Context context) {
        this.context = context;
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
//        Constants constants = new Constants(context);
//        File rootPath = constants.DIRECTORY_PATH;
//        rootPath.mkdirs();
//        File logPath = constants.ERROR_LOG_PATH;
//        logPath.mkdirs();
//        try {
//            writer = new BufferedWriter(new FileWriter(logPath, append));
//        } catch (IOException e) {
//            Log.d(TAG, e.getMessage());
//        }
    }

//    public void writeErrorLog(String TAG, String error) {
//        try {
//            Date errortime = Calendar.getInstance().getTime();
//            writer.newLine();
//            writer.write(sdf.format(errortime) + " " + TAG + error);
//            writer.close();
//        } catch (IOException e) {
//            Log.d(TAG, e.getMessage());
//        }
//    }

    public void logout (Context context, InitialData initialData) {

    }
}
