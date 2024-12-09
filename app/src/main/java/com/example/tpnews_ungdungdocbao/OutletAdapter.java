package com.example.tpnews_ungdungdocbao;

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

import java.util.ArrayList;

public class OutletAdapter extends BaseAdapter {

    Context context;
    int layout;
    ArrayList<Outlet> arrayList = new ArrayList<>();

    public OutletAdapter(Context context, int layout, ArrayList<Outlet> arrayList) {
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

        TextView txtName = convertView.findViewById(R.id.txtLayoutOutlet);
        txtName.setText(arrayList.get(position).getName());

        ImageView logo = convertView.findViewById(R.id.imgvLayoutOutlet);
        String base64String = arrayList.get(position).getLogo();
        if (base64String != null && !base64String.isEmpty()) {
            Bitmap base64Bitmap = convertBase64ToBitmap(base64String);
            logo.setImageBitmap(base64Bitmap);
        } else {
            logo.setImageResource(R.drawable.no_image);
        }

        return convertView;
    }

    private Bitmap convertBase64ToBitmap(String base64String) {
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
