package com.fieldbook.tracker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.app.ListActivity;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

public class FileExploreActivity extends ListActivity {

    // Stores names of traversed directories
    ArrayList<String> str = new ArrayList<String>();

    // Check if the first level of the directory structure is the one showing
    private Boolean firstLvl = true;

    private Item[] fileList;
    private File path;

    private String chosenFile;
    private ListAdapter adapter;

    private String local;
    private String region;

    private SharedPreferences ep;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        String data = getIntent().getExtras().getString("path");
        path = new File(data);

        super.onCreate(savedInstanceState);
        ep = getSharedPreferences("Settings", 0);

        // Enforce internal language change
        local = ep.getString("language", "en");
        region = ep.getString("region", "");
        Locale locale2 = new Locale(local, region);
        Locale.setDefault(locale2);
        Configuration config2 = new Configuration();
        config2.locale = locale2;
        getBaseContext().getResources().updateConfiguration(config2, getBaseContext().getResources()
                .getDisplayMetrics());

        loadFileList();
        getListView().setAdapter(adapter);

        getListView().setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int which, long arg3) {
                chosenFile = fileList[which].file;

                File sel = new File(path + "/" + chosenFile);

                if (sel.isDirectory()) {
                    firstLvl = false;

                    // Adds chosen directory to list
                    str.add(chosenFile);
                    fileList = null;
                    path = new File(sel + "");

                    loadFileList();

                    getListView().setAdapter(adapter);
                }

                // Checks if 'up' was clicked
                else if (chosenFile.equalsIgnoreCase("up") && !sel.exists()) {

                    // present directory removed from list
                    String s = str.remove(str.size() - 1);

                    // path modified to exclude present directory
                    path = new File(path.toString().substring(0,
                            path.toString().lastIndexOf(s)));
                    fileList = null;

                    // if there are no more directories in the list, then
                    // its the first level
                    if (str.isEmpty()) {
                        firstLvl = true;
                    }
                    loadFileList();
                    getListView().setAdapter(adapter);
                }
                // File picked
                else {
                    try {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", sel.toString());
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void loadFileList() {
        try {
            path.mkdirs();
        } catch (SecurityException e) {
        }

        // Checks whether path exists
        if (path.exists()) {

            File[] filesList = path.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String filename) { //TODO add option to exclude certain filenames from search
                    File sel = new File(dir, filename);
                    if (sel.toString().contains(".fieldbook")||sel.toString().contains("severity.txt")||sel.toString().contains("sharedpref.xml")){
                        return false;
                    }
                    return true;
                }
            });

            Arrays.sort(filesList, comp);
            String[] fList = new String[filesList.length];

            for (int i = 0; i < filesList.length; ++i) {
                fList[i] = filesList[i].getName();
            }

            fileList = new Item[fList.length];

            for (int i = 0; i < fList.length; i++) {
                fileList[i] = new Item(fList[i], R.drawable.file_icon);

                // Convert into file path
                File sel = new File(path, fList[i]);

                // Set drawables
                if (sel.isDirectory()) {
                    fileList[i].icon = R.drawable.directory_icon;
                    Log.d("DIRECTORY", fileList[i].file);
                }
                if (sel.toString().toLowerCase().contains(".csv")) {
                    fileList[i].icon = R.drawable.csv;
                }

                if (sel.toString().toLowerCase().contains(".xls")) {
                    fileList[i].icon = R.drawable.xls;
                } else {
                    Log.d("FILE", fileList[i].file);
                }
            }

            if (!firstLvl) {
                Item temp[] = new Item[fileList.length + 1];
                for (int i = 0; i < fileList.length; i++) {
                    temp[i + 1] = fileList[i];
                }
                temp[0] = new Item("Up", R.drawable.directory_up);
                fileList = temp;
            }
        }

        adapter = new ArrayAdapter<Item>(this,
                android.R.layout.select_dialog_item, android.R.id.text1,
                fileList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // creates view
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view
                        .findViewById(android.R.id.text1);

                // put the image on the text view
                textView.setCompoundDrawablesWithIntrinsicBounds(
                        fileList[position].icon, 0, 0, 0);

                // add margin between image and text (support various screen
                // densities)
                int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
                textView.setCompoundDrawablePadding(dp5);

                return view;
            }
        };
    }

    Comparator comp = new Comparator() {
        public int compare(Object o1, Object o2) {
            File f1 = (File) o1;
            File f2 = (File) o2;
            if (f1.isDirectory() && !f2.isDirectory()) {
                // Directory before non-directory
                return -1;
            } else if (!f1.isDirectory() && f2.isDirectory()) {
                // Non-directory after directory
                return 1;
            } else {
                // Alphabetic order otherwise
                return f1.toString().compareToIgnoreCase(f2.toString());
            }
        }
    };

    // Wrapper class to hold file data
    private class Item {
        public String file;
        public int icon;

        public Item(String file, Integer icon) {
            this.file = file;
            this.icon = icon;
        }

        @Override
        public String toString() {
            return file;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}