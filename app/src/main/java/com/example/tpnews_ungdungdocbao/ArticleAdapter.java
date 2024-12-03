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
import java.util.Objects;

public class ArticleAdapter extends BaseAdapter {

    Context context;
    int layout;
    ArrayList<Article> arrayList;
    ArrayList<Outlet> outletList;


    public ArticleAdapter(Context context, int layout, ArrayList<Article> arrayList, ArrayList<Outlet> outletList) {
        this.context = context;
        this.layout = layout;
        this.arrayList = arrayList;
        this.outletList = outletList;
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

        ImageView imgvImage = convertView.findViewById(R.id.imgvLayoutArticleImage);
        Glide.with(context).load(arrayList.get(position).getImageUrl()).into(imgvImage);
        ImageView imgvLogo = convertView.findViewById(R.id.imgvLayoutOutletImage);
        String logoLink="";
        for(Outlet ol : outletList)
        {
            if (Objects.equals(ol.getName(), arrayList.get(position).getOutlet()))
            {
                logoLink = ol.getLogoLink();
                break;
            }
        }
        Glide.with(context).load(logoLink).into(imgvLogo);

        TextView txtTitle = convertView.findViewById(R.id.txtLayoutArticleTitle);
        txtTitle.setText(arrayList.get(position).getTitle());
        TextView txtDescription = convertView.findViewById(R.id.txtLayoutArticleDescription);
        txtDescription.setText(arrayList.get(position).getDescription());

        return convertView;
    }
}
