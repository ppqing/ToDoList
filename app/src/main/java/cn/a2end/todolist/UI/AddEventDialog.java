package cn.a2end.todolist.UI;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import cn.a2end.todolist.R;

public class AddEventDialog extends Dialog {
    public AddEventDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event_dialog_main);
    }
}
