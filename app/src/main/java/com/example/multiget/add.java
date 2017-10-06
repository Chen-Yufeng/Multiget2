package com.example.multiget;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import stl.FileInfo;


/**
 * Created by If Chan on 2017/10/6.
 */

public class add extends AppCompatActivity{
    private static int fileId = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add);
        final EditText etName = (EditText)findViewById(R.id.back_fileName);
        final EditText etUrl=(EditText)findViewById(R.id.back_url);
        Button button = (Button)findViewById(R.id.back_fileinfo);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                FileInfo fileInfo = new FileInfo(fileId,etUrl.getText().toString().trim(),etName.getText().toString(),0L,0);
                Intent intent = new Intent();
                intent.putExtra("fileInfo",fileInfo);
                setResult(RESULT_OK,intent);
                fileId++;
                finish();
            }
        });
    }
}
