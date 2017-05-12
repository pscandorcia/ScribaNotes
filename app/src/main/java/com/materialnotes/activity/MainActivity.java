package com.materialnotes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.materialnotes.R;
import com.materialnotes.util.FilenameUtils;
import com.materialnotes.widget.AboutNoticeDialog;

import java.io.File;
import java.util.ArrayList;

import no.nordicsemi.android.scriba.hrs.HRSActivity;
import roboguice.activity.RoboActionBarActivity;

public class MainActivity extends RoboActionBarActivity implements AdapterView.OnItemClickListener {

    //public static final String APP_DATA_FOLDER = Environment.getExternalStorageDirectory().getPath()+"/Scriba Notes/files";
    //public static final String PROJECT_EXTENSION = ".ref";

    public final static String FILENAME = "com.materialnotes.activity.MainActivity.FILENAME";

    static final int OPEN_FILE_REQUEST = 1;

    private ArrayList<String> projects; //Project files in app folder
    ArrayAdapter<String> adapter;
    ListView lvMainFiles;

    /** {@inheritDoc} */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create application folders
        File pathAppDataFolder = new File(Cfg.APP_DATA_FOLDER);
        if (!pathAppDataFolder.exists())
            pathAppDataFolder.mkdirs();

        projects = new ArrayList<String>();
        lvMainFiles = (ListView) findViewById(R.id.LvMainFiles);
        lvMainFiles.setOnItemClickListener(this);

        //prompt the user to connect their Scriba device via dialog
        Intent intent = new Intent(MainActivity.this, HRSActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        projects.clear();

        projects = getProjects();

        //Convert elements on the ArrayList to items on the ListView using an ArrayAdapter
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, projects);
        adapter.notifyDataSetChanged();

        lvMainFiles.setAdapter(adapter);
    }

    private ArrayList<String> getProjects(){
        //TODO Load all ref files

        ArrayList<String> folders = new ArrayList<String>();

        ArrayList<String> files = new ArrayList<String>();

        File[] allEntries = new File(Cfg.APP_DATA_FOLDER).listFiles();

        for (int i = 0; i < allEntries.length; i++) {
            if (allEntries[i].isDirectory()) {
                folders.add(allEntries[i].getName());
            } else if (allEntries[i].isFile() &&
                    FilenameUtils.getExtension(allEntries[i].getName()).equalsIgnoreCase(Cfg.PROJECT_EXTENSION)) {
                files.add(allEntries[i].getName());
            }
        }

        return files;
    }

    /** {@inheritDoc} */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about_info:
                new AboutNoticeDialog().show(getSupportFragmentManager(), "dialog_about_notice");
                return true;
            case R.id.bluetooth:
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    public void bluetooth(MenuItem item) {
        Intent intent= new Intent(this, HRSActivity.class);
        startActivity(intent);
    }

    public void showSettings(View v) {
        Intent intent = new Intent(this, SpinnerActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String entryName = (String)parent.getItemAtPosition(position);

        Cfg.currentProjectFilename = Cfg.APP_DATA_FOLDER+"/"+entryName;

        Intent intent = new Intent(this, CurrentProjectFilesActivity.class);
        //intent.putExtra(FILENAME, Cfg.currentProjectFilename);
        startActivity(intent);
    }

    //TODO
    public void addNewProject (View view){
//        Intent openFileIntent = new Intent(this, OpenFileActivity.class);
//        startActivityForResult(openFileIntent, OPEN_FILE_REQUEST);
    }

//    //Check activity result to load new file
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case OPEN_FILE_REQUEST:
//                if (resultCode == RESULT_OK) {
//                    String fileName = data.getStringExtra("fileName");
//                    String shortFileName = data.getStringExtra("shortFileName");
//                    Toast.makeText(this, "Selected File: " + fileName, Toast.LENGTH_SHORT).show();
//
//                    Intent intent = new Intent(this, EditNoteActivity.class);
//                    intent.putExtra(FILENAME, fileName);
//                    startActivity(intent);
//
//                } else {
//                    //Do nothing!
//                    Toast.makeText(this,"No File Selected"/*, Cancel Or Back Pressed"*/,Toast.LENGTH_SHORT).show();
//                }
//                break;
//        }
//    }
}