

package com.materialnotes.activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.materialnotes.R;
import com.materialnotes.util.FileRef;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import roboguice.activity.RoboExpandableListActivity;

public class ShowSpecificNoteActivity extends AppCompatActivity {



    private static final String appDataFolder = Environment.getExternalStorageDirectory().getPath()+"/Scriba Notes/files";
    // @InjectView(R.id.textViewPrueba)

    //public String fileName;
    public int startPos = 0;
    public int endPos=0;
    public int colorWay=0;

    private Context aContext;
    TextView textView;
    ScrollView scrollView;
    RelativeLayout relativeLayout;
    //Layout layout = textView.getLayout();
    private Button marc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_specific_note);

        aContext=getApplicationContext();
        relativeLayout=(RelativeLayout) findViewById(R.id.rl);
        scrollView= (ScrollView) findViewById(R.id.scrollViewSpecific);
        textView =(TextView) findViewById(R.id.textViewPrueba);


        //Capturing the text, start, end position and color for cardReference
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String fileName = intent.getStringExtra(ShowNotesActivity.FileText);
        Log.i("TAG","Nombre del archivo " + fileName.toString());
        int startPosition = intent.getIntExtra(ShowNotesActivity.StartPosition, 0);
        final int endPosition = intent.getIntExtra(ShowNotesActivity.EndtPosition, 0);
        int color = intent.getIntExtra(ShowNotesActivity.CardColor, 0);

        startPos = startPosition;
        endPos = endPosition;
        colorWay = color;

        textView.setTextSize(18);
        //textView.setText(text);

//            TextView  textView1 = (TextView) findViewById(R.id.numerosRango);
//            textView1.setText(String.valueOf(startPosition) + " " + (String.valueOf(endPosition) + " " + (String.valueOf(color))));


        String fileText ="";
        fileText = readFromTxtFile(fileName);
        // Capture the layout's TextView and set the string as its text
        final TextView textView = (TextView) findViewById(R.id.textViewPrueba);
        textView.setText(fileText);


        applyStylesFromRefFile();

       // ScrollView functionality.Scroll to highliht selection
        scrollView.post(new Runnable() {
            public void run() {
                Layout textlayout=textView.getLayout();
                scrollView.scrollTo(0,textlayout.getLineTop(textlayout.getLineForOffset(startPos)));
                        }
    });
        //Log.i("TAG", "The index is " + layout.getLineForOffset(endPos));

        //ScrollView functionality
//        scrollView.post(new Runnable() {
//            public void run() {
//                scrollView.
//                scrollView.scrollTo(startPos,startPos);
//                //scrollView.fullScroll(ScrollView.FOCUS_FORWARD);
//            }
//        });

    }

    //Read From File method
    private String readFromTxtFile(String filename){
        File file = new File(filename);
        if (!file.exists()){
            return "File not found.";
        }
        FileInputStream inputStream = null;
        byte[] buffer = new byte[(int)file.length()];

        try {
            inputStream = new FileInputStream(file);
            inputStream.read(buffer);
            inputStream.close();
        } catch (Exception e) {
            Log.d("PSL:createTxtFileFor...","Error loading test file");
            e.printStackTrace();
        }
        String fullText = new String(buffer);
        return fullText;

    }


    //Highlight Method
    public void highlightText(int start, int end, @ColorInt int color) {
        Spannable spanText = Spannable.Factory.getInstance().newSpannable(textView.getText());
        spanText.setSpan (new BackgroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spanText);
    }

    private boolean applyRef(FileRef fr) {
        //TODO Range validation
        switch (fr.style) {
            case FileRef.HIGHLIGHT:
                highlightText(startPos, endPos, colorWay);
                break;
        }

        return true;
    }

    private void applyStylesFromRefFile(){
        try {
            int refCount = FileRef.count();
            Log.d("PSL:applyStyles...","Numero de registros: "+String.valueOf(refCount));
            for (int i=1; i<=refCount; i++){
                FileRef fr = new FileRef();
                fr = FileRef.readId(i);
                fr.showRegInLog();
                applyRef(fr);
                //TODO Next code must be enabled in the future to apply not-deleted references only
//                if (fr.id!=0){
//                    applyRef(fr);
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}

