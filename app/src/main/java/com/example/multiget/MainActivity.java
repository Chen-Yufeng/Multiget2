package com.example.multiget;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import services.DownloadService;
import stl.FileInfo;
import utils.NotificationUtil;

public class MainActivity extends AppCompatActivity{

    private ListView mLvFile=null;
    private List<FileInfo> mFileList=null;
    private FileListAdapter mAdapter=null;
    private NotificationUtil mNotificationUtil = null;
    private ImageButton mImageButton = null;
    private FileInfo back_fileInfo = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if(resultCode==RESULT_OK){
                    back_fileInfo = (FileInfo) data.getSerializableExtra("fileInfo");
                    mFileList.add(back_fileInfo);
                    //添加下载条目
                    if(mFileList.size()==1){
                        mAdapter=new FileListAdapter(this,mFileList);
                        mLvFile.setAdapter(mAdapter);
                    }else{
                        mAdapter=new FileListAdapter(this,mFileList);
                        mLvFile.setAdapter(mAdapter);
                    }
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageButton=(ImageButton)findViewById(R.id.NewTask);
        mLvFile=(ListView)findViewById(R.id.LvFile);
        mFileList=new ArrayList<FileInfo>();
        mImageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,add.class);
                startActivityForResult(intent,1);
            }
        });
       /**
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
*/

        //注册广播接收器
        IntentFilter fliter=new IntentFilter();
        fliter.addAction(DownloadService.ACTION_UPDATE);
        fliter.addAction(DownloadService.ACTION_FINISHED);
        fliter.addAction(DownloadService.ACTION_START);
        registerReceiver(mReceiver,fliter);

        mNotificationUtil = new NotificationUtil(this);
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
                //调试
                Log.d("Test", "finished = "+finished);
                mAdapter.updateProgress(id,finished);
                //更新通知
                mNotificationUtil.updateNotification(id,finished);
            } else if(DownloadService.ACTION_FINISHED.equals(intent.getAction())){
                Log.d("Test", "MainActivity: 下载结束");
                //下载结束
                FileInfo fileInfo=(FileInfo) intent.getSerializableExtra("fileInfo");
                //更新进度为0
                mAdapter.updateProgress(fileInfo.getId(),0);
                Toast.makeText(MainActivity.this,mFileList.get(fileInfo.getId()).getFileNane()+"下载完毕",
                        Toast.LENGTH_SHORT).show();
                //删除通知
                mNotificationUtil.cancleNotification(fileInfo.getId());
                //调试
                Log.d("This", "结束");
            }else if(DownloadService.ACTION_START.equals(intent.getAction())){
                //显示通知
                mNotificationUtil.showNitification((FileInfo)intent.getSerializableExtra("fileInfo"));
            }
        }
    };

}
