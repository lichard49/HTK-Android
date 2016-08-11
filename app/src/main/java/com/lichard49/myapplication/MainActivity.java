package com.lichard49.myapplication;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class MainActivity extends AppCompatActivity {

    private TextView tv;
    private Bundle data;
    private Handler handler = new Handler() {
        public void handleMessage(Message m) {
            tv.setText(m.getData().getString("text"));
        }
    };

    private Runnable jniThread = new Runnable() {
        @Override
        public void run() {
            try {
                int value = stringFromJNI("/storage/emulated/legacy/hmm_data/eating_0.ext");
                if(value == 123456789) {
                    File file = new File("/storage/emulated/legacy/hmm_data/recognition_result");
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while((line = reader.readLine()) != null) {
                        result.append(line);
                        result.append('\n');
                    }
                    setText(result.toString());
                } else {
                    setText("Recognition failed");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tv = new TextView(getApplicationContext());
        setContentView(tv);
        setText("hi");

        new Thread(jniThread).start();
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
    public native int stringFromJNI(String testFramePathString);

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
}
