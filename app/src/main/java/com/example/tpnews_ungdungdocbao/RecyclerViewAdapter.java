package com.example.tpnews_ungdungdocbao;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.TextViewHolder> {

    private ArrayList<Category> itemList;
    private OnItemClickListener listener; // Interface listener

    // Interface để lắng nghe sự kiện click
    public interface OnItemClickListener {
        void onItemClick(int position); // Phương thức callback khi click vào item
    }

    // Constructor nhận listener
    public RecyclerViewAdapter(ArrayList<Category> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_recycler_view, parent, false);
        return new TextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TextViewHolder holder, int position) {
        // Get the category object at the current position
        Category category = itemList.get(position);
        // Set the category name to the TextView
        holder.textView.setText(category.getName());

        // Xử lý sự kiện click cho item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position); // Gọi phương thức listener
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class TextViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public TextViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.txtRecyclerView);
        }
    }
}
