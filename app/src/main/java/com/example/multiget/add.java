package com.example.multiget;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import services.DownloadService;
import services.DownloadTask;
import stl.FileInfo;

import static services.DownloadService.MSG_INIT;


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
        Button btPaste = (Button)findViewById(R.id.paste);
        final TextView tvErrorUrl=(TextView)findViewById(R.id.tvErrorUrl);
        btPaste.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                ClipboardManager plaster  = (ClipboardManager) add.this.getSystemService(Context.CLIPBOARD_SERVICE);
                String content=plaster.getText().toString().trim();
                etUrl.setText(content);
            }
        });
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(etUrl.getText().toString().trim().substring(0,etUrl.getText().toString().trim().indexOf("/")).equals("http:")) {
                    FileInfo fileInfo = new FileInfo(fileId, etUrl.getText().toString().trim(), etName.getText().toString(), 0L, 0);
                    new InitThread(fileInfo).start();
                }else{
                    tvErrorUrl.setText("Illegal URL");
                }
            }
        });
    }

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_INIT:
                    FileInfo fileInfo=(FileInfo)msg.obj;
                    Intent intent = new Intent();
                    intent.putExtra("fileInfo",fileInfo);
                    setResult(RESULT_OK,intent);
                    fileId++;
                    finish();
                    break;
            }
        }
    };

    class InitThread extends Thread{
        private FileInfo mFileInfo=null;
        public InitThread(FileInfo mFileInfo){
            this.mFileInfo=mFileInfo;
        }

        @Override
        public void run() {
            HttpURLConnection conn=null;
            try{
                URL url=new URL(mFileInfo.getUrl());
                conn=(HttpURLConnection)url.openConnection();
                long length = 0;
                length=conn.getContentLength();
                mFileInfo.setLength(length);
                mHandler.obtainMessage(MSG_INIT,mFileInfo).sendToTarget();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
