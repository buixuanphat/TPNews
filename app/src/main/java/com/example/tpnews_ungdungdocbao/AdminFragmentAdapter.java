package com.example.tpnews_ungdungdocbao;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AdminFragmentAdapter extends FragmentStateAdapter {

    public AdminFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new QuanLyBaiBaoFragment();
            case 1:
                return new QuanLyDanhMucFragment();
            case 2:
                return new QuanLyTrangBaoFragment();
            default:
                return new QuanLyBaiBaoFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // 3 tabs in total
    }
}
