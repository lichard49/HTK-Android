package com.lichard49.myapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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

        // if classifier was successful, file was already written
        // otherwise, write a failure message to the file
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
        runClassifier.putExtra("path", testFramePath);
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

    public static Map<String, Float> startForResultMap(Context c, String testFramePath) {
        File result = startForResult(c, testFramePath);

        // parse results
        try {
            BufferedReader reader = new BufferedReader(new FileReader(result));
            reader.readLine(); // drop header: #!MLF!#
            reader.readLine(); // drop header: "./data/down_left/downleft1_1.rec"

            Map<String, Float> likelihoods = new HashMap<>();
            String line;

            while ((line = reader.readLine()) != null) {
                String[] lineParts = line.split(" ");
                if (lineParts.length == 1) {
                    // drop dividers: ///
                } else {
                    String label = lineParts[2];
                    Float likelihood = Float.parseFloat(lineParts[3]);

                    likelihoods.put(label, likelihood);
                }
            }
            reader.close();
            result.delete();

            return likelihoods;
        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }
    }

    // borrowed from GART (GT2K 2.0)
    public static void createExtFile(List<String> fVectors, int numColumns, String fileName)
            throws IOException {
        int n_samples = fVectors.size();
        int sampPeriod = 2000; // default is 2000ns
        int sampSize = 4;
        int sizeOfVec = numColumns;
        int parmKind = 9; // user defined

		/*creates a file and DataOutputStream*/
        FileOutputStream fout = new FileOutputStream(fileName + ".ext");
        DataOutputStream dos = new DataOutputStream(fout);

		/*writes 4-byte header*/
        dos.writeInt(n_samples);	// number of samples
        dos.writeInt(sampPeriod);	// period

		/*writes 2-byte header*/
        dos.writeChar(sampSize * sizeOfVec); // size = size of data x size of vector
        dos.writeChar(parmKind); // type of sample

		/*writes 8-byte feature fectors*/
        for(int i=0; i<n_samples; i++){
            String vecs = fVectors.get(i);
            StringTokenizer st = new StringTokenizer(vecs);

            while(st.hasMoreTokens()){
                String vec = st.nextToken();

                float f = (new Float(vec)).floatValue();

				/*writes 4-byte vector value*/
                dos.writeFloat(f);
            }
        }

        //close file
        fout.close();
    }
}
