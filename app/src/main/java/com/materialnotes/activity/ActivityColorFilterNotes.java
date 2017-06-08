**
 * New activity and uses
 *
 * by Pablo Sotelo
 */

package com.materialnotes.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.materialnotes.R;
import com.materialnotes.util.FileRef;
import com.materialnotes.util.FileRefHeader;
import com.materialnotes.util.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import roboguice.activity.event.OnPauseEvent;

public class ActivityColorFilterNotes extends AppCompatActivity {

    //Variables that we send to the next Activity
    public static final String FileText ="com.example.ScribaNotes.FileText";
    public static final String StartPosition ="com.example.ScribaNotes.StartPosition";
    public static final String EndtPosition ="com.example.ScribaNotes.EndtPosition";
    public static final String CardColor="com.example.ScribaNotes.Color";
    public static final String CardId = "com.example.ScribaNotes.CardId";

    String colorType;
    int idCard=0;
    int counterColorFilter;
    private Context mContext;
    private ScrollView scrollView;
    private Button colorButton;
    TextView noNotes;

    LinearLayout lLinearLayout;
    ArrayList<CardView> list;
    ArrayList<FileRef> references= new ArrayList<FileRef>();


    /**
     * New activity and uses
     *
     * by Pablo Sotelo
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_filter_notes);


        //Here begins our code
        scrollView = (ScrollView) findViewById(R.id.scrollViewColorFilter);

        // Get the application context
        mContext = getApplicationContext();

        // Get the widgets reference from XML layout
        lLinearLayout = (LinearLayout) findViewById(R.id.cardSelectedLayout);

        //Get the NoNotes reference
        noNotes=(TextView) findViewById(R.id.noNotes);

        //Create a new cardView Array
        list = new ArrayList<CardView>();

        createCardsFromFile();

        //getSupportActionBar().setSubtitle(FilenameUtils.getShortFilenameWithoutExtension(Cfg.currentProjectFilename));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //ScrollView functionality
        scrollView.post(new Runnable() {
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        list.clear();
        references.clear();
        lLinearLayout.removeAllViews();
        createCardsFromFile();
        counterColorFilter=1;
    }
    // Method that load every cardview, filter color, headers....
    private void createCardsFromFile(){
        counterColorFilter=1;
        try {
            int refCount = FileRef.count();
            Log.d("PSL:applyStyles...","Numero de registros: "+String.valueOf(refCount));
            for (int i=1; i<=refCount; i++) {
                FileRef fr = new FileRef();
                fr = FileRef.readId(i);
                fr.showRegInLog();
                if (fr.id != 0) {
                    //Filter (only merge colored selections, delete if you want bold, italic...... selections)
                    if ((fr.style == FileRef.HIGHLIGHT) && (fr.color == ShowNotesActivity.typeColor)) {
                        createCardFromReference(fr);
                        references.add(fr);
                        if (fr.color == -256) {
                            colorType="Yellow";
                            getSupportActionBar().setSubtitle(FileRefHeader.getCurrentProjectYellowMeaning());
                        } else if (fr.color == -16711936) {
                            colorType="Green";
                            getSupportActionBar().setSubtitle(FileRefHeader.getCurrentProjectGreenMeaning());
                        } else if (fr.color == -16711681) {
                            colorType = "Blue";
                            getSupportActionBar().setSubtitle(FileRefHeader.getCurrentProjectBlueMeaning());

                        }else{
                            colorButton.setBackgroundColor(Color.WHITE);
                        }
                        //Title depends on a color selection
                        getSupportActionBar().setTitle(colorType+" Notes");
                        //Side color
                        colorButton = (Button) findViewById(R.id.colorButton);
                        colorButton.setBackgroundColor((fr.color));
                    }
                }
            }
            if (list.size()==0) {
                noNotes.setVisibility(View.VISIBLE);
                colorButton.setVisibility(View.INVISIBLE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //Method that transforms px into dp
    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }


    //Method to create cardViews from references
    private void createCardFromReference(final FileRef fr) {
        // Initialize a new CardView
        final CardView card = new CardView(mContext);


        // Set the CardView layoutParams
        CardView.LayoutParams params = new CardView.LayoutParams(
                CardView.LayoutParams.MATCH_PARENT,
                CardView.LayoutParams.WRAP_CONTENT
        );

        //Turn on Padding setting
        card.setUseCompatPadding(true);


        //Cardview params (margins.....)
        params.setMargins(dpToPx(12), dpToPx(16), dpToPx(10), dpToPx(8));
        params.height =dpToPx(160);
        card.setLayoutParams(params);
        card.setPaddingRelative(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));

        //Set CardView corner radius
        card.setRadius(dpToPx(5));

        //Set the CardView maximum elevation
        card.setMaxCardElevation(dpToPx(6));

        //Set CardView elevation
        card.setCardElevation(dpToPx(5));

        //CardView Header params
        RelativeLayout cabecera = new RelativeLayout(getApplicationContext());

        RelativeLayout.LayoutParams ParamsCabecera=
                (new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT)

                );
        ParamsCabecera.height=dpToPx(20);

        //Switch color from FileRef
        switch (fr.style){
            case FileRef.HIGHLIGHT:
                cabecera.setBackgroundColor(fr.color);
                break;
            case FileRef.BOLD:
                break;
            case FileRef.ITALIC:
                break;
            case FileRef.NO_STYLE:
                break;
            case FileRef.UNDERLINE:
                break;
        }

        //Upload parameters
        cabecera.setLayoutParams(ParamsCabecera);

        //Add the layout into the cardview
        card.addView(cabecera);

        // TODO resize with dp
        // Initialize a card number in the CardView
        TextView cardNumber = new TextView(mContext);
        cardNumber.setLayoutParams(params);

        cardNumber.setText("Note " + String.valueOf(counterColorFilter));
        cardNumber.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        cardNumber.setPadding(dpToPx(6), dpToPx(8), dpToPx(0), dpToPx(0));
        cardNumber.setTextColor(Color.BLACK);
        // Put the TextView in the CardView
        card.addView(cardNumber);

        //Substring the last "/" and take File name
        int nameIndex= fr.filename.lastIndexOf("/");
        String str= "";

        if (nameIndex == -1){
            str=fr.filename;
        }else{
            str= fr.filename.substring(nameIndex+1);
        }

        // Initialize a card Title (from file) in the CardView
        TextView cardTitle = new TextView(mContext);
        cardTitle.setLayoutParams(params);
        cardTitle.setText(str);
        cardTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        cardTitle.setPadding(dpToPx(80),dpToPx(10),dpToPx(0),dpToPx(0));
        cardTitle.setTextColor(Color.BLACK);
        // Put the TextView in CardView
        card.addView(cardTitle);

        // Initialize a card date (from file) in the CardView
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        String newFormat = formatter.format(fr.date);

        TextView date = new TextView(mContext);
        date.setLayoutParams(params);
        date.setText(newFormat);
        date.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        date.setPadding(dpToPx(241), dpToPx(10), dpToPx(0), dpToPx(0));
        date.setTextColor(Color.GRAY);

        // Put date in the CardView
        card.addView(date);

        //Initialize the text Selection
        TextView textSelection = new TextView(mContext);
        textSelection.setLayoutParams(params);
        String textNote = readTextRangeFromFile(fr.start, fr.end, fr.filename.trim());
        textSelection.setText(textNote);
        textSelection.setPadding(dpToPx(20), dpToPx(37), dpToPx(6), dpToPx(6));
        textSelection.setTextColor(Color.GRAY);

        // Put the TextView in CardView
        card.addView(textSelection);

        //Set an own Id to every single cardView
        idCard++;
        card.setId(idCard);


        //Convert a cardview into a clickable object
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CardView c = (CardView)view;
                Log.i("TAG", "The index is " + c.getId());

                //send the "messages" with the variables who we need to set the text (color, start position, end position and text)
                Intent intent=new Intent(mContext.getApplicationContext(), ShowSpecificNoteActivity.class);
                String message=(fr.filename.trim());
                int message02=(fr.start);
                int message03=(fr.end);
                int message04=(fr.color);
                int message05 = (fr.id);

                Log.i("TAG","Name " + message.toString());

                intent.putExtra(FileText, message);
                intent.putExtra(StartPosition, message02);
                intent.putExtra(EndtPosition, message03);
                intent.putExtra(CardColor,message04);
                intent.putExtra(CardId, message05);
                startActivity(intent);
            }

        });

        // Finally, add the CardView in root layout
        lLinearLayout.addView(card);

        //Add the card into the array list and plus counter
        Log.i("TAG", "contador" + counterColorFilter);
        counterColorFilter++;
        list.add(card);

        //Delete cardview by longclick
        card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                card.setVisibility(View.VISIBLE);
                card.setAlpha(0.0f);

                // Animation
                card.animate()
                        .translationX(card.getWidth())
                        .alpha(1.0f);
                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        lLinearLayout.removeView(card);
                        FileRef.deleteId(fr.id);
                        list.remove(list.get(list.size() - 1));

                        if (list.size()==0) {
                            noNotes.setVisibility(View.VISIBLE);
                            colorButton.setVisibility(View.INVISIBLE);
                        }
                    }

                }, 500);

                return true;
            }
        });
    }

    //ActionBar uses
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.show_notes, menu);
        return true;
    }

    //Clickable usages in ActionBar.../
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //Export into an email
            case R.id.export:
                if (list.size()==0) {
                    Toast.makeText(getApplicationContext(), "No "+colorType+ " notes to Export", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        sendEmail(FileRef.exportRefs(references));
                        //Toast.makeText(getApplicationContext(), "File Export Done!", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
            //delete function, only colored selection cards
            case R.id.delete:
                if (list.size()==0) {
                    Toast.makeText(getApplicationContext(), "No "+colorType+ " Notes to Delete", Toast.LENGTH_SHORT).show();
                } else {

                    new AlertDialog.Builder(this)
                            .setMessage("Are you sure to delete permanently all the " + colorType + " references?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    lLinearLayout.removeAllViews();
                                    list.removeAll(list);
                                    for (int i = 0; i < references.size(); i++) {
                                        FileRef.deleteId(references.get(i).id);

                                    }
                                    //FileRef.deleteRefsFile(fr.id);
                                    noNotes.setVisibility(View.VISIBLE);
                                    colorButton.setVisibility(View.INVISIBLE);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                    break;
                }
        }
                return true;
    }


    //Read from File method
    private String readTextRangeFromFile(int start, int end, String filename) {

        File file = new File(filename);
        if (!file.exists()){
            return "File not found: "+ filename;
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
        //TODO range validation
        fullText=fullText.substring(start,end);
        return fullText;
    }

    //Export
    private void sendEmail(String body) {
        Log.d("eMail", "Coming sendEmail");
        if (body.equals("ERROR")) {
            Toast.makeText(mContext, "ERROR SENDING MESSAGE", Toast.LENGTH_SHORT).show();
            Log.d("eMail", "ERROR");
        } else {
            Log.d("eMail", "Body different to ERROR");
            String[] types = {
                    "message/rfc822",
                    "text/html",
                    "text/plain"};

            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.setType(types[1]);
            File file = new File(Cfg.APP_DATA_FOLDER, "ScribaExportedNotes.html");
            try {
                FileWriter fw = new FileWriter(file);
                Log.d("html", "Escribiendo fichero: "+body);
                fw.write(body);
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (!file.exists() || !file.canRead()) {
                Toast.makeText(this, "Attachment Error", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            Uri uri = Uri.parse("file://" + file);
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

            Log.d("gMail", body);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Exported Notes");

            try {
                Log.d("eMail", "Starting the new activity.");
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


