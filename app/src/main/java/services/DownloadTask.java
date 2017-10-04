package services;

import android.content.Context;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

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

    public DownloadTask(Context mContext, FileInfo mFileInfo) {
        this.mContext = mContext;
        this.mFileInfo = mFileInfo;
        mDAO=new ThreadDAOImpl(mContext);
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
            try{
                URL url=new URL(mThreadInfo.getUrl());
                conn =(HttpURLConnection)url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("GET");
                int start=mThreadInfo.getStart()+mThreadInfo.getFinished();
                conn.setRequestProperty("Range", "bytes=" + start + "-" + mThreadInfo.getEnd());
                File file=new File(DownloadService.DOWNLOADED_PATH)
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
