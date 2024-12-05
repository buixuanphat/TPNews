package com.example.tpnews_ungdungdocbao;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomDrawerFragment extends BottomSheetDialogFragment {

    private SeekBar seekBar;
    private TextView textView;

    // Các giá trị cỡ chữ cố định
    private static final int[] FONT_SIZES = {12, 16, 20, 24, 28, 32, 36};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_drawer, container, false);

        // Liên kết View
        seekBar = view.findViewById(R.id.seekBar);
        textView = view.findViewById(R.id.textView);

        // Khởi tạo SharedPreferences
        SharedPreferences preferences = getActivity().getSharedPreferences("FontSize", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Thiết lập SeekBar
        seekBar.setMax(FONT_SIZES.length - 1);

        // Lấy giá trị cỡ chữ lưu trữ và thiết lập ban đầu
        int savedFontSize = preferences.getInt("size", FONT_SIZES[1]); // Mặc định là 16
        textView.setTextSize(savedFontSize); // Cập nhật cỡ chữ ban đầu
        textView.setText("Cỡ chữ: " + savedFontSize + "sp");

        // Tìm vị trí của cỡ chữ đã lưu trong mảng FONT_SIZES
        int savedProgress = 0;
        for (int i = 0; i < FONT_SIZES.length; i++) {
            if (FONT_SIZES[i] == savedFontSize) {
                savedProgress = i;
                break;
            }
        }
        seekBar.setProgress(savedProgress); // Đặt vị trí SeekBar tương ứng

        // Xử lý sự kiện thay đổi giá trị SeekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Lấy cỡ chữ tương ứng
                int fontSize = FONT_SIZES[progress];

                // Cập nhật TextView
                textView.setTextSize(fontSize);
                textView.setText("Cỡ chữ: " + fontSize + "sp");

                // Lưu giá trị vào SharedPreferences
                editor.putInt("size", fontSize);
                editor.apply(); // Lưu thay đổi
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Không làm gì ở đây
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Không làm gì ở đây
            }
        });

        return view;
    }
}
