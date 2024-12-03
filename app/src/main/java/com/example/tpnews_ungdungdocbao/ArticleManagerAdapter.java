package com.example.tpnews_ungdungdocbao;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class ArticleManagerAdapter extends BaseAdapter {

    Context context;
    int layout;
    ArrayList<Article> arrayList = new ArrayList<>();

    public ArticleManagerAdapter(Context context, int layout, ArrayList<Article> arrayList) {
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

        TextView txtName = convertView.findViewById(R.id.txtLayoutArticleManager);
        txtName.setText(arrayList.get(position).getTitle());

        ImageView logo = convertView.findViewById(R.id.imgvLayoutArticleManager);
        Glide.with(context).load(arrayList.get(position).getImageUrl()).into(logo);

        return convertView;
    }
}