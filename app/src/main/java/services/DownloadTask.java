package services;

import android.content.Context;

import stl.FileInfo;
import stl.ThreadInfo;

/**
 * Created by If Chan on 2017/10/4.
 */

public class DownloadTask {
    private Context mContext=null;
    private FileInfo mFileInfo=null;

    public DownloadTask(Context mContext, FileInfo mFileInfo) {
        this.mContext = mContext;
        this.mFileInfo = mFileInfo;
    }
    class DownloadThread extends Thread{
        private ThreadInfo mThreadInfo=null;

        public DownloadThread(ThreadInfo mInfo)
        {
            this.mThreadInfo = mInfo;
        }

        @Override
        public void run() {

        }
    }
}
