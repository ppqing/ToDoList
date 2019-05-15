package cn.a2end.todolist.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class Event implements Serializable {
    int id=-1;
    Date time;
    String timeString;
    String data;
    int isCompleted;
    int isNotification;

    public Event(String time,String data,int status,int isNotification)  {
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm");
        timeString=time;
        try {
            this.time =dateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.data =data;
        isCompleted=status;
        this.isNotification=isNotification;
    }

    public Event(int id,String time,String data,int status,int isNotification)  {
        this.id=id;
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm");
        timeString=time;
        try {
            this.time =dateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.data =data;
        isCompleted=status;
        this.isNotification=isNotification;
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
        values.put("isCompleted",isCompleted);
        values.put("isNotification",isNotification);
        sqLiteDatabase.insert("List",null,values);
        Cursor cursor =sqLiteDatabase.rawQuery("select last_insert_rowid() from List",null);
        if (cursor.moveToFirst()) id = cursor.getInt(0);//将生成的id返回
    }

    public void updataToDB(Context context){
        DBHelper dbHelper=new DBHelper(context,"List.db",null,1);
        SQLiteDatabase sqLiteDatabase =dbHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put("time",timeString);
        values.put("data",data);
        values.put("isCompleted",isCompleted);
        values.put("isNotification",isNotification);
        sqLiteDatabase.update("List",values,"id=?",new String[]{String.valueOf(id)});
    }

    public void deleteFromDB(Context context){
        DBHelper dbHelper=new DBHelper(context,"List.db",null,1);
        SQLiteDatabase sqLiteDatabase =dbHelper.getWritableDatabase();
        sqLiteDatabase.delete("List","id=?",new String[]{String.valueOf(id)});
    }


    public String getTimeString(){
        return timeString;
    }
    public String getData(){
        return data;
    }
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id=id;
    }
    public int getStatus(){
        return isCompleted;
    }
    public void setData(String data){
        this.data=data;
    }

    public void setStatus(int isCompleted) {
        this.isCompleted = isCompleted;
    }

    public void setTimeString(String timeString) {
        this.timeString=timeString;
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm");
        try {
            time =dateFormat.parse(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Date getTime() {
        return time;
    }

    public void setIsNotification(int isNotification) {
        this.isNotification = isNotification;
    }

    public int getIsNotification() {
        return isNotification;
    }
}
