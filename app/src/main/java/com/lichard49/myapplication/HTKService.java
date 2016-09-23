package com.lichard49.myapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.Nullable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by richard on 9/23/16.
 */

public class HTKService extends Service {
    private final int HTK_SUCCESS = 123456789;
    public static final String RECOGNITION_RESULT_PATH =
            "/storage/emulated/legacy/hmm_data/recognition_result";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);

        // read given parameters for this call
        Bundle bundle = intent.getExtras();
        String path = bundle.getString("path");

        // run classifier
        int htkStatusCode = MainActivity.stringFromJNI(path);
        if(htkStatusCode != HTK_SUCCESS) {
            File file = new File(RECOGNITION_RESULT_PATH);
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write("Recognition failed");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        android.os.Process.killProcess(Process.myPid());

        return result;
    }

    public static void start(Context c, String testFramePath) {
        Intent runClassifier = new Intent(c, HTKService.class);
        runClassifier.putExtra("path", testFramePath + ".ext");
        c.startService(runClassifier);
    }

    // Blocking!!!
    public static File startForResult(Context c, String testFramePath) {
        start(c, testFramePath);

        File result = new File(RECOGNITION_RESULT_PATH);
        while(!result.exists()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}
