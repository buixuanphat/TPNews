package com.example.tpnews_ungdungdocbao.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tpnews_ungdungdocbao.Fragments.QuanLyBaiBaoFragment;
import com.example.tpnews_ungdungdocbao.Fragments.QuanLyDanhMucFragment;
import com.example.tpnews_ungdungdocbao.Fragments.QuanLyTrangBaoFragment;
import com.example.tpnews_ungdungdocbao.Fragments.QuanLyUserFragment;

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
            case 3:
                return new QuanLyUserFragment();
            default:
                return new QuanLyBaiBaoFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
