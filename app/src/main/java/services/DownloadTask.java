package services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import db.ThreadDAO;
import db.ThreadDAOImpl;
import stl.FileInfo;
import stl.ThreadInfo;

/**
 * Created by If Chan on 2017/10/4.
 */

public class DownloadTask {
    private Context mContext=null;
    private FileInfo mFileInfo=null;
    private ThreadDAO mDAO=null;
    private int mFinished=0;
    public boolean isPause=false;

    public DownloadTask(Context mContext, FileInfo mFileInfo) {
        this.mContext = mContext;
        this.mFileInfo = mFileInfo;
        mDAO=new ThreadDAOImpl(mContext);
    }

    public void download(){
        List<ThreadInfo> threadInfos=mDAO.getThreads(mFileInfo.getUrl());
        ThreadInfo threadInfo=null;
        if(threadInfos.size()==0){
            threadInfo=new ThreadInfo(0,mFileInfo.getUrl(),0,mFileInfo.getLength(),0);
            Log.i("Task", "filelength="+mFileInfo.getLength());
       }else{
            threadInfo=threadInfos.get(0);
        }

        new DownloadThread(threadInfo).start();
    }


    class DownloadThread extends Thread{
        private ThreadInfo mThreadInfo=null;

        public DownloadThread(ThreadInfo mInfo)
        {
            this.mThreadInfo = mInfo;
        }

        @Override
        public void run() {
            if(!mDAO.isExists(mThreadInfo.getUrl(),mThreadInfo.getId())){
                mDAO.insertThread(mThreadInfo);
            }
            HttpURLConnection conn=null;
            RandomAccessFile raf=null;
            InputStream input=null;
            try{
                URL url=new URL(mThreadInfo.getUrl());
                conn =(HttpURLConnection)url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("GET");
                int start=mThreadInfo.getStart()+mThreadInfo.getFinished();
                conn.setRequestProperty("Range", "bytes=" + start + "-" + mThreadInfo.getEnd());
                File file=new File(DownloadService.DOWNLOADED_PATH,mFileInfo.getFileNane());
                raf=new RandomAccessFile(file,"rwd");
                raf.seek(start);

                Intent intent=new Intent(DownloadService.ACTION_UPDATE);
                mFinished+=mThreadInfo.getFinished();
                if(conn.getResponseCode()==206){
                    input=conn.getInputStream();
                    byte[] buffer=new byte[1024];
                    int len=-1;
                    long time=System.currentTimeMillis();
                    while((len=input.read(buffer))!=-1){
                        Log.i("Test", len+"");
                        raf.write(buffer,0,len);
                        mFinished+=len;
                        if(System.currentTimeMillis()-time>500) {
                            time=System.currentTimeMillis();
                            intent.putExtra("finished", mFinished * 100 / mFileInfo.getLength());
                            mContext.sendBroadcast(intent);
                        }
                        if(isPause){
                            mDAO.updateThread(mThreadInfo.getUrl(),mThreadInfo.getId(),mFinished);
                            return;
                        }
                    }
                    Log.i("Test", len+"");
                    //记得要删除线程
                    mDAO.deleteThread(mThreadInfo.getUrl(),mThreadInfo.getId());

                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    input.close();
                    raf.close();
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
