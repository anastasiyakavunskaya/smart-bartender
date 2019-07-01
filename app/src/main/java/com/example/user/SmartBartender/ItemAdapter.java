package com.example.user.SmartBartender;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;



public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemHolder> {
    private final ArrayList<Pair<String,Integer>> data = new ArrayList<>();
    private final OnItemClickListener listener;


    public interface OnItemClickListener {
        void onItemClick(String item);
        void onButtonClick(String item);
    }

    public ItemAdapter(OnItemClickListener listener){
        this.listener = listener;
    }

    public void setData(ArrayList<Pair<String,Integer>> items) {
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
        holder.bind(data.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ItemHolder extends RecyclerView.ViewHolder {


        private final TextView itemName;
        final Button cookBtn = itemView.findViewById(R.id.cook_btn);
        final Button deleteBtn = itemView.findViewById(R.id.delete_btn);

        private ItemHolder(View itemView){
            super(itemView);
            itemName = itemView.findViewById(R.id.item_name);
        }

        @SuppressLint("ResourceAsColor")
        void bind(final Pair<String,Integer> item, final OnItemClickListener listener) {

            final String itemString = item.first;
                itemName.setText(itemString);
                    deleteBtn.setVisibility(Button.GONE);
                    if(item.second.equals(0)){
                        cookBtn.setEnabled(true);
                        cookBtn.setBackgroundColor(R.color.colorPrimaryDark);
                    }
                itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(itemString);
                    }
                });

                cookBtn.setOnClickListener(new View.OnClickListener(){
                    @Override public void onClick(View v) {
                        listener.onButtonClick(itemString);
                    }
                });

                deleteBtn.setOnClickListener(new View.OnClickListener(){
                @Override public void onClick(View v) {
                    listener.onButtonClick(itemString);
                }
            });
            }

    }
}
