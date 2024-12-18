package com.example.tpnews_ungdungdocbao.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.tpnews_ungdungdocbao.Models.Article;
import com.example.tpnews_ungdungdocbao.R;

import java.util.ArrayList;

public class ArticleAdapter extends BaseAdapter {

    Context context;
    int layout;
    ArrayList<Article> arrayList;

    public ArticleAdapter(Context context, int layout, ArrayList<Article> arrayList) {
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
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(layout, null);


        TextView txtTitle = convertView.findViewById(R.id.txtLayoutArticleTitle);
        txtTitle.setText(arrayList.get(position).getTitle());
        TextView txtDescription = convertView.findViewById(R.id.txtLayoutArticleDescription);
        txtDescription.setText(arrayList.get(position).getDescription());

        ImageView imgvImage = convertView.findViewById(R.id.imgvLayoutArticleImage);
        String base64Image = arrayList.get(position).getImage();
        Bitmap bitmap = convertBase64ToBitmap(base64Image);
        Glide.with(context)
                .load(bitmap)
                .apply(RequestOptions.bitmapTransform(new MultiTransformation<>(
                        new CenterCrop(),
                        new RoundedCorners(30)))
                )
                .into(imgvImage);

        ImageView imgvLogo = convertView.findViewById(R.id.imgvLayoutOutletImage);
        String base64logo = arrayList.get(position).getOutletLogo();
        Bitmap bitmapLogo = convertBase64ToBitmap(base64logo);
        imgvLogo.setImageBitmap(bitmapLogo);
        return convertView;
    }
    private Bitmap convertBase64ToBitmap(String base64String) {
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
