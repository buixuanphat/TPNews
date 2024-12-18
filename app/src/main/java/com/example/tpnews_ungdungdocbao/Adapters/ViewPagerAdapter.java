package com.example.tpnews_ungdungdocbao.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tpnews_ungdungdocbao.Fragments.HomeFragment;
import com.example.tpnews_ungdungdocbao.Fragments.MenuFragment;
import com.example.tpnews_ungdungdocbao.Fragments.VideoFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new HomeFragment();
            case 1: return new VideoFragment();
            case 2: return new MenuFragment();
            default: return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4; // Số lượng Fragment
    }
}
