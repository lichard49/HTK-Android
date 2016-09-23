package com.lichard49.myapplication;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    private TextView tv;
    private Bundle data;
    private Handler handler = new Handler() {
        public void handleMessage(Message m) {
            tv.setText(System.currentTimeMillis() + ":\n" + m.getData().getString("text"));
        }
    };

    private Runnable jniThread = new Runnable() {
        @Override
        public void run() {
            try {
                String sampleFramePath = "/storage/emulated/legacy/hmm_data/silent_1";
                File file = new File(sampleFramePath);
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                List<String> vectors = new LinkedList<String>();
                int vectorSize = -1;
                while((line = reader.readLine()) != null) {
                    String[] columns = line.split(",");
                    if(vectorSize == -1) {
                        vectorSize = columns.length;
                    } else if(columns.length != vectorSize) {
                        System.out.println("Expected " + vectorSize + " at line " + vectors.size() + " but got " + columns.length);
                        return;
                    }
                    vectors.add(line);
                }

                ///////////////////////////////////////////////////////////////////////////////////
                // start measuring time
                long start = System.nanoTime();

                // create .ext file as required by HVite
                HTKService.createExtFile(vectors, vectorSize, sampleFramePath);

                // run HVite and get likelihoods
                Map<String, Float> likelihoods = HTKService.startForResultMap(
                        getApplicationContext(), sampleFramePath + ".ext");

                // show results
                setText(likelihoods.toString());

                ///////////////////////////////////////////////////////////////////////////////////
                // stop measuring time
                long end = System.nanoTime();

                Log.d("lichard49", "Time taken " + (end-start));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.text_view);
        setText("hi");

        Button button = (Button) findViewById(R.id.go_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(jniThread).start();
            }
        });
    }

    private void setText(String text) {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("text", text);
        message.setData(bundle);
        handler.sendMessage(message);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public static native int stringFromJNI(String testFramePathString);

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
}
