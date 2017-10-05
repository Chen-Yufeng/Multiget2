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
import java.util.ArrayList;
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
    private int mThreadCount=1;  //线程数
    private List<DownloadThread> mThreadList=null;

    public DownloadTask(Context mContext, FileInfo mFileInfo,int mThreadCount) {
        this.mContext = mContext;
        this.mFileInfo = mFileInfo;
        this.mThreadCount=mThreadCount;
        mDAO=new ThreadDAOImpl(mContext);
    }

    public void download(){
        List<ThreadInfo> threads=mDAO.getThreads(mFileInfo.getUrl());

        if(threads.size()==0){
            int length=mFileInfo.getLength() / mThreadCount;
            for(int i=0;i<mThreadCount;i++){
                ThreadInfo threadInfo=new ThreadInfo(i,mFileInfo.getUrl(),length*i,(i+1)*length-1,0);
                if(i==mThreadCount-1){
                    threadInfo.setEnd(mFileInfo.getLength());
                }
                threads.add(threadInfo);
            }
        }
        mThreadList = new ArrayList<DownloadThread>();
        //Start multy download
        for(ThreadInfo info:threads){
            DownloadThread thread=new DownloadThread(info);
            thread.start();
            //使用线程集合管理
            mThreadList.add(thread);
        }
    }

    /**
     * 同步方法
     * 判断所有线程
     */
    private synchronized void checkAllThreadsFinished(){
        boolean allFinished= true;
        //遍历集合
        for(DownloadThread thread:mThreadList){
            if(thread.isFinished){
                allFinished=false;
                break;
            }
        }
        if(allFinished){
            //Send Broadcast
            Intent intent=new Intent(DownloadService.ACTION_FINISHED);
            intent.putExtra("fileInfo",mFileInfo);
            mContext.sendBroadcast(intent);
        }
    }


    class DownloadThread extends Thread{
        private ThreadInfo mThreadInfo=null;
        public boolean isFinished=false;

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
                        mThreadInfo.setFinished(mThreadInfo.getFinished()+len);
                        if(System.currentTimeMillis()-time>500) {
                            time=System.currentTimeMillis();
                            intent.putExtra("finished", mFinished * 100 / mFileInfo.getLength());
                            intent.putExtra("id",mFileInfo.getId());
                            mContext.sendBroadcast(intent);
                        }
                        if(isPause){
                            mDAO.updateThread(mThreadInfo.getUrl(),mThreadInfo.getId(),mThreadInfo.getFinished());
                            return;
                        }
                    }
                    isFinished=true;
                    mDAO.deleteThread(mThreadInfo.getUrl(),mThreadInfo.getId());
                    //检查是否下完
                    checkAllThreadsFinished();
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
