package com.example.multiget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import services.DownloadService;
import stl.FileInfo;

public class MainActivity extends AppCompatActivity{

    private ListView mLvFile=null;
    private List<FileInfo> mFileList=null;
    private FileListAdapter mAdapter=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLvFile=(ListView)findViewById(R.id.LvFile);
        mFileList=new ArrayList<FileInfo>();
        final FileInfo fileInfo0=new FileInfo(0,"http://gdown.baidu.com/data/wisegame/43e76ce22df64c52/QQ_730.apk\n"
                ,"test1.apk",0,0);
        final FileInfo fileInfo1=new FileInfo(1,"http://gdown.baidu.com/data/wisegame/43e76ce22df64c52/QQ_730.apk\n"
                ,"test2.apk",0,0);
        final FileInfo fileInfo2=new FileInfo(2,"http://gdown.baidu.com/data/wisegame/43e76ce22df64c52/QQ_730.apk\n"
                ,"test3.apk",0,0);
        mFileList.add(fileInfo0);
        mFileList.add(fileInfo1);
        mFileList.add(fileInfo2);

        mAdapter=new FileListAdapter(this,mFileList);
        mLvFile.setAdapter(mAdapter);


        //注册广播接收器
        IntentFilter fliter=new IntentFilter();
        fliter.addAction(DownloadService.ACTION_UPDATE);
        fliter.addAction(DownloadService.ACTION_FINISHED);
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
                int id=intent.getIntExtra("id",0);
                mAdapter.updateProgress(id,finished);
            } else if(DownloadService.ACTION_FINISHED.equals(intent.getAction())){
                //下载结束
                FileInfo fileInfo=(FileInfo) intent.getSerializableExtra("fileInfo");
                //更新进度为0
                mAdapter.updateProgress(fileInfo.getId(),0);
                Toast.makeText(MainActivity.this,mFileList.get(fileInfo.getId()).getFileNane()+"下载完毕",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

}
