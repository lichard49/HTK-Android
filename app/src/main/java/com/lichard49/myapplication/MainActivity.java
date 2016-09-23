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

                createExtFile(vectors, vectorSize, sampleFramePath);

                File file2 = HTKService.startForResult(getApplicationContext(),
                        sampleFramePath + ".ext");
                BufferedReader reader2 = new BufferedReader(new FileReader(file2));
                StringBuilder result = new StringBuilder();
                String line2;
                while((line2 = reader2.readLine()) != null) {
                    result.append(line2);
                    result.append('\n');
                }
                setText(result.toString());

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

    private void createExtFile(List<String> fVectors, int numColumns, String fileName) throws IOException {

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
