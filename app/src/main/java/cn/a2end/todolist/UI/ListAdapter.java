package cn.a2end.todolist.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.a2end.todolist.EventDetailActivity;
import cn.a2end.todolist.MainActivity;
import cn.a2end.todolist.R;
import cn.a2end.todolist.db.Event;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private List<Event> mEventList;
    public ListAdapter(List<Event> eventList){
        mEventList=eventList;
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView itemTime;
        TextView itemName;
        CardView cardView;
        CheckBox checkBox;
        ImageView imageView;
        public ViewHolder(View view) {
            super(view);
            cardView=view.findViewById(R.id.cardViewMain);
            itemName= view.findViewById(R.id.itemText);
            itemTime=view.findViewById(R.id.itemTime);
            checkBox=view.findViewById(R.id.itemCheckBox);
            imageView=view.findViewById(R.id.notification);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_main,parent,false);
        final ViewHolder holder= new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position=holder.getAdapterPosition();
                Intent intent=new Intent(view.getContext(), EventDetailActivity.class);
                intent.putExtra("position",position);
                intent.putExtra("event",mEventList.get(position));
                ((Activity)view.getContext()).startActivityForResult(intent,0);

            }
        });
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                Event event=mEventList.get(holder.getAdapterPosition());
                if(isChecked){
                    event.setStatus(1);
                }else {
                    event.setStatus(0);
                }
                event.updataToDB(view.getContext());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event e=mEventList.get(position);
        if(e.getStatus()==0){
            holder.checkBox.setChecked(false);
        }else {
            holder.checkBox.setChecked(true);
        }

        holder.itemTime.setText(e.getTimeString());
        holder.itemName.setText(e.getData());

        if(e.getIsNotification()==0){
            holder.imageView.setImageResource(R.drawable.notification_off_grey);
        }else {
            holder.imageView.setImageResource(R.drawable.notification_yellow);
        }

    }


    @Override
    public int getItemCount() {
        return mEventList.size();
    }
}
