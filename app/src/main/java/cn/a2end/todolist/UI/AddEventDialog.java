package cn.a2end.todolist.UI;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Toast;

import cn.a2end.todolist.MainActivity;
import cn.a2end.todolist.R;

public class AddEventDialog extends Dialog implements View.OnClickListener {
    public AddEventDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event_dialog_main);
        ImageButton btnTime = findViewById(R.id.dialog_time);
        Button btnSubmit = findViewById(R.id.dialog_submit);
        Button btnCancel = findViewById(R.id.dialog_cancel);
        btnCancel.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnTime.setOnClickListener(this);
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
                break;
        }
    }

    private void showSetTimeDlg() {
        Toast.makeText(getContext(), "dfdf", Toast.LENGTH_LONG);
    }
}
