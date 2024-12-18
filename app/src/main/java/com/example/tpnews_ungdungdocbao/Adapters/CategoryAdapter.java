package com.example.tpnews_ungdungdocbao.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.tpnews_ungdungdocbao.Models.Category;
import com.example.tpnews_ungdungdocbao.R;

import java.util.ArrayList;

public class CategoryAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Category> arrayList;

    public CategoryAdapter(Context context, int layout, ArrayList<Category> arrayList) {
        this.context = context;
        this.layout = layout;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(layout, null);

        TextView txtCat = convertView.findViewById(R.id.txtLayoutCategory);
        txtCat.setText(arrayList.get(position).getName());

        return convertView;
    }
}
