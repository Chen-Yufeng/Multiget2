package services;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.IntDef;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import stl.FileInfo;

import static android.content.ContentValues.TAG;

public class DownloadService extends Service {

    private static final String DOWNLOADED_PATH= Environment
            .getExternalStorageDirectory().getAbsolutePath()+"/downloaded/";

    private static final String ACTION_START="ACTION_START";
    private static final String ACTION_STOP="ACTION_STOP";
    private static final int MSG_INIT=0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(ACTION_START.equals(intent.getAction())){
            FileInfo fileInfo=(FileInfo)intent.getSerializableExtra("fileInfo");
            Log.i("Test", "Start:"+fileInfo.toString());
            new InitThread(fileInfo).start();
        }else if(ACTION_STOP.equals(intent.getAction())){
            FileInfo fileInfo=(FileInfo)intent.getSerializableExtra("fileInfo");
            Log.i("Test", "Stop:"+fileInfo.toString());
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_INIT:
                    FileInfo fileInfo=(FileInfo)msg.obj;
                    Log.i("Test", "Init"+fileInfo);
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
            RandomAccessFile raf=null;
            try{
                URL url=new URL(mFileInfo.getUrl());
                conn=(HttpURLConnection)url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("GET");
                int length = -1;
                if(conn.getResponseCode()==200)
                    length=conn.getContentLength();
                if(length<=0)
                    return;

                File dir=new File(DOWNLOADED_PATH);
                if(!dir.exists())
                {
                    dir.mkdirs();
                }

                File file=new File(dir,mFileInfo.getFileNane());
                raf=new RandomAccessFile(file,"rwd");
                raf.setLength(length);
                mFileInfo.setLength(length);
                mHandler.obtainMessage(MSG_INIT,mFileInfo).sendToTarget();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    raf.close();
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
