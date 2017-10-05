package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by If Chan on 2017/10/4.
 */

public class DBhelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "download.db";
    private static DBhelper sHelper=null;  //静态对象引用
    private static final int VERSION = 1;
    private static final String SQL_CREATE = "create table thread_info(_id integer primary key autoincrement," +
            "thread_id integer, url text, start integer, end integer, finished integer)";
    private static final String SQL_DROP = "drop table if exists thread_info";

    private DBhelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    /**
     * 获得对象
     */
    public static DBhelper getInstance(Context context){
        if(sHelper == null){
            sHelper = new DBhelper(context);
        }
        return sHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(SQL_DROP);
        db.execSQL(SQL_CREATE);
    }
}
