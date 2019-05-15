package cn.a2end.todolist;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import cn.a2end.todolist.db.Event;

public class EventDetailActivity extends AppCompatActivity implements View.OnClickListener {


    Intent intent;
    Event event;
    EditText editTextData;
    EditText editTextTime;
    FloatingActionButton fab;
    Toolbar toolbar;
    ImageButton imageButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        intent=getIntent();
        event=(Event) intent.getSerializableExtra("event");

        editTextData=findViewById(R.id.detail_ed1);
        editTextTime=findViewById(R.id.detail_ed2);
        editTextData.setText(event.getData());
        editTextTime.setText(event.getTimeString());
        Button buttonUpdate=findViewById(R.id.detail_update);
        Button buttonDelete=findViewById(R.id.detail_delete);
        buttonDelete.setOnClickListener(this);
        buttonUpdate.setOnClickListener(this);
        imageButton=findViewById(R.id.detail_time);
        imageButton.setOnClickListener(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        if(event.getStatus()==0){
            toolbar.setTitle("未完成");
        }else{
            toolbar.setTitle("已完成");
        }
        if(event.getIsNotification()==0){
            fab.setImageResource(R.drawable.notification_off);
        }else {
            fab.setImageResource(R.drawable.notification);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.detail_update:
                if(event.getIsNotification()==1){
                    if(checkPermission()){
                        deleteCalendarEvent(this,event.getData());
                    }
                    event.setData(editTextData.getText().toString());
                    event.setTimeString(editTextTime.getText().toString());
                    event.setIsNotification(0);
                    fetchPermission();
                }
                event.setData(editTextData.getText().toString());
                event.setTimeString(editTextTime.getText().toString());
                event.updataToDB(view.getContext());
                Toast.makeText(this,"修改成功",Toast.LENGTH_LONG).show();
                finish();
                break;

            case R.id.detail_delete:
                String text="删除"+event.getData()+"成功";
                if(event.getIsNotification()==1){
                    if(checkPermission()){
                        boolean flag=deleteCalendarEvent(this,event.getData());
                        if(flag){
                            text+="，提醒也同步删除";
                        }else {
                            text+="，请手动在日历中删除提醒";
                        }
                    }else {
                        text+="，请手动在日历中删除提醒";
                    }

                }

                event.deleteFromDB(this);
                Toast.makeText(this,text,Toast.LENGTH_LONG).show();
                finish();
                break;

            case R.id.fab:
                fetchPermission();
                break;

            case R.id.detail_time:
                showSetTimeDlg();
                break;
        }

    }

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private int year,month,day,hour,min;
    private String timeString;

    private void showSetTimeDlg() {
        /**
         * 显示设置时间的对话框
         */
        Toast.makeText(this, "dfdf", Toast.LENGTH_LONG);;
        datePickerDialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                year=i;
                month=i1+1;
                day=i2;
                timePickerDialog=new TimePickerDialog(EventDetailActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        hour=i;
                        min=i1;
                        timeString =year+"-"+month+"-"+day+" ";
                        if(hour<=9)timeString+="0";
                        timeString+=hour+":";
                        if(min<=9)timeString+="0";
                        timeString+=min;
                        editTextTime.setText(timeString);
                        event.setTimeString(timeString);
                        //String.valueOf(time.getTime());

                    }
                },0,0,true);
                timePickerDialog.show();
            }
        },2019,0,1);
        datePickerDialog.show();
    }

    public boolean checkPermission(){
        int checkSelfPermission;
        try {
            checkSelfPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }

        // 如果有授权，走正常插入日历逻辑
        if (checkSelfPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public void fetchPermission() {
        int checkSelfPermission;
        try {
            checkSelfPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return;
        }

        // 如果有授权，走正常插入日历逻辑
        if (checkSelfPermission == PackageManager.PERMISSION_GRANTED) {
            insertCalendar();
            return;
        } else {
            // 如果没有授权，就请求用户授权
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_CALENDAR,
                    Manifest.permission.READ_CALENDAR}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户同意的授权请求
                insertCalendar();
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_CALENDAR)) {
                    // 如果用户不是点击了拒绝就跳转到系统设置页
                    //gotoSettings();
                    Toast.makeText(this,"请在设置中允许权限",Toast.LENGTH_LONG).show();
                }
                Snackbar.make(getWindow().getDecorView(), "未获得权限，无法设置提醒", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }
    }

    private static String CALENDAR_URL = "content://com.android.calendar/calendars";
    private static String CALENDAR_EVENT_URL = "content://com.android.calendar/events";
    private static String CALENDAR_REMINDER_URL = "content://com.android.calendar/reminders";
    private static String CALENDARS_NAME = "2end";
    private static String CALENDARS_ACCOUNT_NAME = "todolist@2end.cn";
    private static String CALENDARS_ACCOUNT_TYPE = "cn.a2end.todolist";
    private static String CALENDARS_DISPLAY_NAME = "ToDoList";

    private static int checkAndAddCalendarAccount(Context context) {
        int oldId = checkCalendarAccount(context);
        if (oldId >= 0) {
            return oldId;
        } else {
            long addId = addCalendarAccount(context);
            if (addId >= 0) {
                return checkCalendarAccount(context);
            } else {
                return -1;
            }
        }
    }


    private static int checkCalendarAccount(Context context) {
        Cursor userCursor = context.getContentResolver().query(Uri.parse(CALENDAR_URL),
                null, null, null, null);
        try {
            if (userCursor == null) { // 查询返回空值
                return -1;
            }
            int count = userCursor.getCount();
            if (count > 0) { // 存在现有账户，取第一个账户的id返回
                userCursor.moveToFirst();
                return userCursor.getInt(userCursor.getColumnIndex(CalendarContract.Calendars._ID));
            } else {
                return -1;
            }
        } finally {
            if (userCursor != null) {
                userCursor.close();
            }
        }
    }

    private static long addCalendarAccount(Context context) {
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(CalendarContract.Calendars.NAME, CALENDARS_NAME);
        value.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE);
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDARS_DISPLAY_NAME);
        value.put(CalendarContract.Calendars.VISIBLE, 1);
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE);
        value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
                CalendarContract.Calendars.CAL_ACCESS_OWNER);
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = Uri.parse(CALENDAR_URL);
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME,
                        CALENDARS_ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE,
                        CALENDARS_ACCOUNT_TYPE)
                .build();

        Uri result = context.getContentResolver().insert(calendarUri, value);
        long id = result == null ? -1 : ContentUris.parseId(result);
        return id;
    }

    public static boolean insertCalendarEvent(Context context, String title, String description,
                                              long beginTimeMillis, long endTimeMillis) {

        if (context == null || TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
            return false;
        }

        int calId = checkAndAddCalendarAccount(context); // 获取日历账户的id
        if (calId < 0) { // 获取账户id失败直接返回，添加日历事件失败
            return false;
        }

        // 如果起始时间为零，使用当前时间
        if (beginTimeMillis == 0) {
            Calendar beginCalendar = Calendar.getInstance();
            beginTimeMillis = beginCalendar.getTimeInMillis();
        }
        // 如果结束时间为零，使用起始时间+30分钟
        if (endTimeMillis == 0) {
            endTimeMillis = beginTimeMillis + 30 * 60 * 1000;
        }

        try {
            /** 插入日程 */
            ContentValues eventValues = new ContentValues();
            eventValues.put(CalendarContract.Events.DTSTART, beginTimeMillis);
            eventValues.put(CalendarContract.Events.DTEND, endTimeMillis);
            eventValues.put(CalendarContract.Events.TITLE, title);
            eventValues.put(CalendarContract.Events.DESCRIPTION, description);
            eventValues.put(CalendarContract.Events.CALENDAR_ID, 1);
            eventValues.put(CalendarContract.Events.EVENT_LOCATION, "ToDoList");

            TimeZone tz = TimeZone.getDefault(); // 获取默认时区
            eventValues.put(CalendarContract.Events.EVENT_TIMEZONE, tz.getID());

            Uri eUri = context.getContentResolver().insert(Uri.parse(CALENDAR_EVENT_URL), eventValues);
            long eventId = ContentUris.parseId(eUri);
            if (eventId == 0) { // 插入失败
                return false;
            }

            /** 插入提醒 - 依赖插入日程成功 */
            ContentValues reminderValues = new ContentValues();
            // uri.getLastPathSegment();
            reminderValues.put(CalendarContract.Reminders.EVENT_ID, eventId);
            reminderValues.put(CalendarContract.Reminders.MINUTES, 10); // 提前10分钟提醒
            reminderValues.put(CalendarContract.Reminders.METHOD,
                    CalendarContract.Reminders.METHOD_ALERT);
            Uri rUri = context.getContentResolver().insert(Uri.parse(CALENDAR_REMINDER_URL),
                    reminderValues);
            if (rUri == null || ContentUris.parseId(rUri) == 0) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Deprecated
    public static boolean deleteCalendarEvent(Context context, String title) {
        if (context == null) {
            return false;
        }
        Cursor eventCursor = context.getContentResolver().query(Uri.parse(CALENDAR_EVENT_URL),
                null, null, null, null);
        try {
            if (eventCursor == null) { // 查询返回空值
                return false;
            }
            if (eventCursor.getCount() > 0) {
                // 遍历所有事件，找到title跟需要查询的title一样的项
                for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                    String eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
                    if (!TextUtils.isEmpty(title) && title.equals(eventTitle)) {
                        int id = eventCursor.getInt(eventCursor
                                .getColumnIndex(CalendarContract.Calendars._ID)); // 取得id
                        Uri deleteUri = ContentUris.withAppendedId(Uri.parse(CALENDAR_EVENT_URL), id);
                        int rows = context.getContentResolver().delete(deleteUri, null, null);
                        if (rows == -1) { // 事件删除失败
                            return false;
                        }
                    }
                }
            }
            return true;
        } finally {
            if (eventCursor != null) {
                eventCursor.close();
            }
        }
    }

    private void insertCalendar(){
        if(event.getIsNotification()==0){
            boolean flag=insertCalendarEvent(this,event.getData(),"由ToDoList自动创建",event.getTime().getTime(),0);
            if(flag){
                fab.setImageResource(R.drawable.notification);
                event.setIsNotification(1);
                event.updataToDB(this);
                Snackbar.make(getWindow().getDecorView(), "成功设置提醒", Snackbar.LENGTH_LONG).setAction("Action", null).show();;
            }else {
                Snackbar.make(getWindow().getDecorView(), "设置提醒失败", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }

        }else {
            boolean flag=deleteCalendarEvent(this,event.getData());
            fab.setImageResource(R.drawable.notification_off);
            event.setIsNotification(0);
            event.updataToDB(this);
            if(flag){
                Snackbar.make(getWindow().getDecorView(), "成功取消提醒", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }else {
                Snackbar.make(getWindow().getDecorView(), "取消提醒失败，但数据库已成功更新", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }

        }
    }
}
