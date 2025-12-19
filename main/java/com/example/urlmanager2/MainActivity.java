package com.example.urlmanager2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    Button btnAdd;

    ArrayList<String> urlList = new ArrayList<>();
    ArrayAdapter<String> adapter;

    SharedPreferences prefs;
    String PREF_NAME = "MyURLs";
    String KEY_URLS = "urls_set";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listViewUrls);
        btnAdd = findViewById(R.id.btnAdd);

        prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        loadUrls();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, urlList);
        listView.setAdapter(adapter);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUrlDialog(-1);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = urlList.get(position);

                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "http://" + url;
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                showOptionsDialog(position);

                return true;
            }
        });
    }

    private void showUrlDialog(int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(index == -1 ? "افزودن آدرس" : "ویرایش آدرس");

        final EditText input = new EditText(this);
        if (index != -1) {
            input.setText(urlList.get(index));
        }

        builder.setView(input);

        builder.setPositiveButton("ذخیره", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String url = input.getText().toString().trim();

                if (url.isEmpty()) {
                    Toast.makeText(MainActivity.this, "آدرس نمی‌تواند خالی باشد", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (index == -1) {
                    urlList.add(url);
                } else {
                    urlList.set(index, url);
                }

                adapter.notifyDataSetChanged();
                saveUrls();
            }
        });

        builder.setNegativeButton("انصراف", null);

        builder.show();
    }

    private void showOptionsDialog(int index) {
        String[] options = {"ویرایش", "حذف"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0) {
                    showUrlDialog(index);
                }
                else if (which == 1) {
                    urlList.remove(index);
                    adapter.notifyDataSetChanged();
                    saveUrls();
                }
            }
        });

        builder.show();
    }

    private void saveUrls() {
        SharedPreferences.Editor editor = prefs.edit();

        Set<String> set = new HashSet<>(urlList);
        editor.putStringSet(KEY_URLS, set);
        editor.apply();
    }

    private void loadUrls() {
        Set<String> set = prefs.getStringSet(KEY_URLS, null);

        if (set != null) {
            urlList.clear();
            urlList.addAll(set);
        }
    }
}
