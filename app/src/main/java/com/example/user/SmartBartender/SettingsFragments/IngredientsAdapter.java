package com.example.user.SmartBartender.SettingsFragments;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.user.SmartBartender.R;

import java.util.ArrayList;
import java.util.List;


public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ItemHolder> {
    private final List<String> data = new ArrayList<>();
    private final OnItemClickListener listener;


    public interface OnItemClickListener {
        void onItemClick(String item);
        void onButtonClick(String item);
    }

    public IngredientsAdapter(OnItemClickListener listener){
        this.listener = listener;
    }

    public void setData(List<String> items) {
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
    public void onBindViewHolder(@NonNull IngredientsAdapter.ItemHolder holder, int position) {
        holder.bind(data.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ItemHolder extends RecyclerView.ViewHolder{
        private final TextView itemName;
        final Button c = itemView.findViewById(R.id.cook_btn);
        final Button d = itemView.findViewById(R.id.delete_btn);

        private ItemHolder(View itemView){
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
        }

        void bind(final String item, final OnItemClickListener listener) {
                itemName.setText(item);
                c.setVisibility(Button.GONE);
                itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                    }
                });
                d.setOnClickListener(new View.OnClickListener(){
                @Override public void onClick(View v) {
                    listener.onButtonClick(item);
                }
            });
            }
        }

}
