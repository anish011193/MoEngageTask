package com.example.moengagetask.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moengagetask.R;

import java.sql.Array;
import java.util.ArrayList;

public class Task extends AppCompatActivity {


    EditText et1, et2, et3;
    Button btSubmit;
    Integer target, length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        et1 = findViewById(R.id.etText1);
        et2 = findViewById(R.id.etText2);
        et3 = findViewById(R.id.etText3);
        TextView textView = findViewById(R.id.tv1);
        btSubmit = findViewById(R.id.btSubmit);
        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                target = Integer.valueOf(et1.getText().toString());
                length = Integer.valueOf(et2.getText().toString());
                Integer mdata[] = new Integer[length];
                String values = et3.getText().toString();
                String dummy[] = new String[length];
                dummy = values.split(",");
                int k = 0;
                for (String a : dummy) {
                    mdata[k] = Integer.valueOf(a);
                    k++;
                }
                try {
                    String pair = checkPair(mdata);
                    textView.setText("Pair is: "+pair);
                } catch (Exception e) {
                    Log.e("Error: ", e.getLocalizedMessage());
                }
            }
        });

    }

    private String checkPair(Integer[] mdata) {
        boolean isPairFound = false;
        String pair = "";
        for (int i = 0; i < mdata.length; i++) {
            if (!isPairFound) {
                int x = mdata[i];
                for (int j = 0; j < i; j++) {
                    if (!isPairFound) {
                        int y = mdata[j];
                        int sum = x + y;
                        if (sum == target) {
                            Log.e("Pair Value", "Pairs are: " + x + " and " + y);
                            pair = x + "," + y;
                            isPairFound = true;
                        }
                    } else {
                        break;
                    }
                }
            } else {
                break;
            }
        }
        return pair;
    }
}