package cn.a2end.todolist.UI;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.a2end.todolist.MainActivity;
import cn.a2end.todolist.R;
import cn.a2end.todolist.db.Event;

public class AddEventDialog extends Dialog implements View.OnClickListener {
    public AddEventDialog(@NonNull Context context, List<Event> eventList,ListAdapter adapter,List<Event> EventListDefault) {
        super(context);
        mEventList=eventList;
        mAdapter=adapter;
        mEventListDefault=EventListDefault;
    }
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private int year,month,day,hour,min;
    private EditText t1;
    private EditText t2;

    private List<Event> mEventList;
    private ListAdapter mAdapter;
    private List<Event> mEventListDefault;

    private String timeString;
    private SimpleDateFormat dateFormat;
    private AlertDialog.Builder alertDialog;
    private Event event;
    Date time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event_dialog_main);
        ImageButton btnTime = findViewById(R.id.dialog_time);
        Button btnSubmit = findViewById(R.id.dialog_submit);
        Button btnCancel = findViewById(R.id.dialog_cancel);
        t1=findViewById(R.id.dialog_ed1);
        t2=findViewById(R.id.dialog_ed2);
        btnCancel.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnTime.setOnClickListener(this);

        alertDialog=new AlertDialog.Builder(getContext());
        alertDialog.setTitle("提示");
        alertDialog.setPositiveButton("确定", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //啥也不做
            }
        });

        timeString=null;
        dateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_time:
                showSetTimeDlg();
                break;
            case R.id.dialog_cancel:
                this.dismiss();
                break;
            case R.id.dialog_submit:
                if(t1.length()==0){
                    //alertDialog.setMessage("请输入内容");
                    //alertDialog.show();
                    Snackbar.make(view,"请设置内容",Snackbar.LENGTH_SHORT).show();
                    break;
                }
                if(timeString==null){
                    //alertDialog.setMessage("请设置时间");
                    //alertDialog.show();
                    Snackbar.make(view,"请设置时间",Snackbar.LENGTH_SHORT).show();
                    break;
                }
                Event event=new Event(timeString,t1.getText().toString(),0,0);
                event.addToDB(getContext());//添加到数据库
                mEventList.add(event);
                mAdapter.notifyItemInserted(mEventList.size()-1);
                if(mEventListDefault!=mEventList){
                    mEventListDefault.add(event);
                }
                this.dismiss();
                Snackbar.make(view,"添加成功",Snackbar.LENGTH_LONG).show();
                //alertDialog.setMessage("添加成功");
                //alertDialog.show();
                break;
        }
    }

    private void showSetTimeDlg() {
        /**
         * 显示设置时间的对话框
         */
        Toast.makeText(getContext(), "dfdf", Toast.LENGTH_LONG);;
        datePickerDialog=new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                year=i;
                month=i1+1;
                day=i2;
                timePickerDialog=new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        hour=i;
                        min=i1;
                        timeString =year+"-"+month+"-"+day+" ";
                        if(hour<=9)timeString+="0";
                        timeString+=hour+":";
                        if(min<=9)timeString+="0";
                        timeString+=min;
                        try {
                            time= dateFormat.parse(timeString);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        t2.setText(timeString);
                        //String.valueOf(time.getTime());

                    }
                },0,0,true);
                timePickerDialog.show();
            }
        },2019,0,1);
        datePickerDialog.show();
    }

}
