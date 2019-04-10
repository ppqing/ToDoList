package cn.a2end.todolist.UI;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.a2end.todolist.R;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView itemName;
        public ViewHolder(View view) {
            super(view);
            itemName= view.findViewById(R.id.itemText);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_main,parent,false);
        ViewHolder holder= new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemName.setText("test");
    }


    @Override
    public int getItemCount() {
        return 50;
    }
}
