package cn.a2end.todolist.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class Event {
    Date time;
    String timeString;
    String data;
    Boolean isCompleted;

    public Event(String time,String data) throws ParseException {
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm");
        timeString=time;
        try {
            this.time =dateFormat.parse(time);
        } catch (ParseException e) {
            throw e;
        }
        this.data =data;
        isCompleted=false;
    }

    public void addToDB(Context context){
        /**
         * 传入context，自动添加该event到数据库
         */
        DBHelper dbHelper=new DBHelper(context,"List.db",null,1);
        SQLiteDatabase sqLiteDatabase =dbHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("time",timeString);
        values.put("data",data);
        values.put("isCompleted",0);
    }
}
