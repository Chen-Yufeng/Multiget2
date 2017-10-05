package com.example.multiget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import services.DownloadService;
import stl.FileInfo;

public class MainActivity extends AppCompatActivity{

    private TextView tvFileName;
    private ProgressBar pbProgress;
    private Button btStart;
    private Button btStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvFileName=(TextView)findViewById(R.id.tvFileName);
        pbProgress=(ProgressBar) findViewById(R.id.pbProgress);
        btStart=(Button) findViewById(R.id.btStart);
        btStop=(Button) findViewById(R.id.btStop);
        pbProgress.setMax(100);
        final FileInfo fileInfo=new FileInfo(0,"http://gdown.baidu.com/data/wisegame/43e76ce22df64c52/QQ_730.apk\n"
                ,"test.apk",0,0);
        tvFileName.setText(fileInfo.getFileNane());
        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, DownloadService.class);
                intent.setAction("ACTION_START");
                intent.putExtra("fileInfo",fileInfo);
                startService(intent);
            }
        });

        btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, DownloadService.class);
                intent.setAction("ACTION_STOP");
                intent.putExtra("fileInfo",fileInfo);
                startService(intent);
            }
        });
        //注册广播接收器
        IntentFilter fliter=new IntentFilter();
        fliter.addAction(DownloadService.ACTION_UPDATE);
        registerReceiver(mReceiver,fliter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    //renew UI
    BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(DownloadService.ACTION_UPDATE.equals(intent.getAction())){
                int finished=intent.getIntExtra("finished",0);
                pbProgress.setProgress(finished);
            }
        }
    };

}
