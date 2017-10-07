package com.example.multiget;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import services.DownloadService;
import stl.FileInfo;

/**
 * 文件列表适配器
 * Created by If Chan on 2017/10/5.
 */

public class FileListAdapter extends BaseAdapter {

    private Context mContext=null;
    private List<FileInfo> mFileList=null;

    public FileListAdapter(Context mContext, List<FileInfo> mFileList) {
        this.mContext = mContext;
        this.mFileList = mFileList;
    }

    @Override
    public int getCount() {
        return mFileList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        final FileInfo fileInfo=mFileList.get(position);
        ViewHolder holder=null;
        if(view==null){
            view=LayoutInflater.from(mContext).inflate(R.layout.listitem,null);
            holder= new ViewHolder();
            holder.tvFileName=(TextView)view.findViewById(R.id.tvFileName);
            holder.btStop=(Button) view.findViewById(R.id.btStop);
            holder.btStart=(Button)view.findViewById(R.id.btStart);
            holder.pbProgress=(ProgressBar) view.findViewById(R.id.pbProgress);
            holder.percent=(TextView)view.findViewById(R.id.percent);
            holder.tvFileName.setText(fileInfo.getFileNane());
            holder.pbProgress.setMax(100);
            holder.btStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(mContext, DownloadService.class);
                    intent.setAction("ACTION_START");
                    intent.putExtra("fileInfo",fileInfo);
                    mContext.startService(intent);
                    //调试
                    Log.d("Test", "onClick: start");
                }
            });
            holder.btStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(mContext, DownloadService.class);
                    intent.setAction("ACTION_STOP");
                    intent.putExtra("fileInfo",fileInfo);
                    mContext.startService(intent);
                    Log.d("Test", "onClick: stop");
                }
            });
            view.setTag(holder);
        }else{
            holder=(ViewHolder)view.getTag();
        }
        holder.pbProgress.setProgress(fileInfo.getFinished());
        holder.percent.setText("已完成"+fileInfo.getFinished()+"%");

        return view;
    }

    /**
     * 更新progress Bar
     */
    public void updateProgress(int id,int progress){
        FileInfo fileInfo=mFileList.get(id);
        fileInfo.setFinished(progress);
        notifyDataSetChanged();  //调用getview
    }


    static class ViewHolder{
        TextView tvFileName,percent;
        Button btStop,btStart;
        ProgressBar pbProgress;
    }
}
