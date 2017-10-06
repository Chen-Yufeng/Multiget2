package utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.multiget.MainActivity;
import com.example.multiget.R;

import java.util.HashMap;
import java.util.Map;

import services.DownloadService;
import stl.FileInfo;

/**
 * Created by If Chan on 2017/10/6.
 */

public class NotificationUtil {
    private NotificationManager mNotificationManager = null;
    private Map<Integer,Notification> mNotifications = null;
    private Context mContext = null;

    public NotificationUtil(Context context){
        mContext = context;
        mNotificationManager =
                (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
        mNotifications = new HashMap<Integer, Notification>();
    }

    public void showNitification(FileInfo fileInfo){
        //判断通知是否存在
        if(!mNotifications.containsKey(fileInfo.getId())){
            Notification notification = new Notification();
            //设置属性
            notification.tickerText = fileInfo.getFileNane() + "开始下载";
            notification.when = System.currentTimeMillis();
            notification.icon = R.mipmap.ic_launcher_round;
            //点击以后会消失
            notification.flags=Notification.FLAG_AUTO_CANCEL;
            //点击跳转到Activity
            Intent intent=new Intent(mContext, MainActivity.class);
            PendingIntent pintent = PendingIntent.getActivity(mContext,0,intent,0);
            notification.contentIntent = pintent;

            RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(),R.layout.notification);
            //设置按钮点击操作
            Intent intentStart = new Intent(mContext, DownloadService.class);
            intentStart.setAction(DownloadService.ACTION_START);
            intentStart.putExtra("fileInfo",fileInfo);
            PendingIntent piStart = PendingIntent.getService(mContext,0,intentStart,0);
            remoteViews.setOnClickPendingIntent(R.id.btStart,piStart);

            Intent intentStop = new Intent(mContext, DownloadService.class);
            intentStop.setAction(DownloadService.ACTION_STOP);
            intentStop.putExtra("fileInfo",fileInfo);
            PendingIntent piStop = PendingIntent.getService(mContext,0,intentStop,0);
            remoteViews.setOnClickPendingIntent(R.id.btStop,piStop);
            //设置textview
            remoteViews.setTextViewText(R.id.tvFileName,fileInfo.getFileNane());
            //设置Notification的视图
            notification.contentView = remoteViews;
            //发出通知
            mNotificationManager.notify(fileInfo.getId(),notification);
            //加入集合
            mNotifications.put(fileInfo.getId(),notification);
        }
    }

    public void cancleNotification(int id){
        //取消通知
        mNotificationManager.cancel(id);
        mNotifications.remove(id);
    }

    /**
     * 更新进度条
     * @param id notification ID
     * @param progress 进度
     */
    public void updateNotification(int id,int progress){
        Notification notification = mNotifications.get(id);
        if(notification !=null){
            notification.contentView.setProgressBar(R.id.pbProgress,100,progress,false);
            mNotificationManager.notify(id,notification);
        }
    }
}
