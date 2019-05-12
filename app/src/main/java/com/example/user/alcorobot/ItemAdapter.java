package com.example.user.alcorobot;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;



public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemHolder> {
    private List<String> data = new ArrayList<>();
    private OnItemClickListener listener;


    public interface OnItemClickListener {
        void onItemClick(String item);
    }

    private final boolean isIngredient;
    ItemAdapter( boolean isIngredient, OnItemClickListener listener){
        this.listener = listener;
        this.isIngredient = isIngredient;
    }

    void setData(List<String> items) {
        data.clear();
        data.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ItemHolder(inflater.inflate(R.layout.item_view, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ItemHolder holder, int position) {
        holder.bind(data.get(position), listener, isIngredient);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ItemHolder extends RecyclerView.ViewHolder{
        private final TextView itemName;
        Button b = itemView.findViewById(R.id.cook_btn);
        private ItemHolder(View itemView){
            super(itemView);


            itemName = itemView.findViewById(R.id.item_name);
        }
        void bind(final String item, final OnItemClickListener listener, boolean isIngredient) {
                itemName.setText(item);
                if(isIngredient)
                b.setVisibility(Button.GONE);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        listener.onItemClick(item);
                    }
                });
            }
        }
}
