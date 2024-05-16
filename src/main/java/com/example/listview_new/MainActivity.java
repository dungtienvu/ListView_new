package com.example.listview_new;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.listview_new.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    ListView lv;
    ArrayList<String> arraywork;
    ArrayAdapter<String> arrayAdapter;
    EditText edtwork, edthour, edtmi;
    TextView txtdate;
    Button btnwork;

    private static final String PREFS_NAME = "WORK_PREFS";
    private static final String WORK_LIST_KEY = "WORK_LIST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo các biến sau khi setContentView
        lv = findViewById(R.id.Listview1);
        edthour = findViewById(R.id.edthour);
        edtmi = findViewById(R.id.edtmi);
        edtwork = findViewById(R.id.edtwork);
        btnwork = findViewById(R.id.btnadd);
        txtdate = findViewById(R.id.txtdate);

        // Đặt lắng nghe cho ViewCompat
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        arraywork = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arraywork);
        lv.setAdapter(arrayAdapter);

        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        txtdate.setText("Hôm nay: " + simpleDateFormat.format(currentDate));

        loadData();  // Load data when the activity is created

        btnwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtwork.getText().toString().equals("") ||
                        edthour.getText().toString().equals("") ||
                        edtmi.getText().toString().equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Info missing");
                    builder.setMessage("Please enter all information of the work");
                    builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // No action needed
                        }
                    });
                    builder.show();
                } else {
                    String str = edtwork.getText().toString() + " - " + edthour.getText().toString() +
                            ":" + edtmi.getText().toString();
                    arraywork.add(str); // Add new work item to the list
                    arrayAdapter.notifyDataSetChanged();
                    saveData();  // Save data when a new item is added
                    edthour.setText("");
                    edtmi.setText("");
                    edtwork.setText("");
                }
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                arraywork.remove(position);
                arrayAdapter.notifyDataSetChanged();
                saveData();
            }
        });
    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> workSet = new HashSet<>(arraywork);
        editor.putStringSet(WORK_LIST_KEY, workSet);
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> workSet = sharedPreferences.getStringSet(WORK_LIST_KEY, new HashSet<>());
        arraywork.clear();
        arraywork.addAll(workSet);
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveData();  // Save data when the activity is paused
    }
}
