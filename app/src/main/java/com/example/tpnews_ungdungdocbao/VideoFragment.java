package com.example.tpnews_ungdungdocbao;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public VideoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VideoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VideoFragment newInstance(String param1, String param2) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.video_fragment, container, false);

        final ViewPager2 videoViewPager = view.findViewById(R.id.videoViewPager);

        List<VideoItem> videoItems = new ArrayList<>();

        VideoItem videoItemKidsai = new VideoItem();
        videoItemKidsai.videoURL = "https://res.cloudinary.com/dq1oo3fod/video/upload/v1731379824/khi_gh%E1%BB%87_b%E1%BA%A1n_%C4%91i_ch%C6%A1i_v%E1%BB%9Bi_th%E1%BA%B1ng_kh%C3%A1c_kidsai_dangrangto_talagi_jjgw0o.mp4";
        videoItemKidsai.videoTitle = "TALAGI";
        videoItemKidsai.VideoDescription = "Kidsai và Dangrangto cháy hết mình cùng con beat mới!!!";
        videoItems.add(videoItemKidsai);

        VideoItem videoItemDangRangTo = new VideoItem();
        videoItemDangRangTo.videoURL = "https://res.cloudinary.com/dq1oo3fod/video/upload/v1731379997/Dangrangto_aka_tr%E1%BA%A7n_l%C3%A0_l%C6%B0%E1%BB%9Bt_rap_live_ng%E1%BB%B1a_%C3%B4_dangrangto_rapviet_rap_raplive_bangdianhacmaithuy_em7mll.mp4";
        videoItemDangRangTo.videoTitle = "Ngựa Ô";
        videoItemDangRangTo.VideoDescription = "DangRangTo live Ngựa Ô cực chất!!!";
        videoItems.add(videoItemDangRangTo);

        VideoItem videoItemLeftHand = new VideoItem();
        videoItemLeftHand.videoURL = "https://res.cloudinary.com/dq1oo3fod/video/upload/v1731380213/Ch%C6%B0a_%C4%91%E1%BB%81u_tay_l%E1%BA%AFm_c%E1%BA%A3_nh%C3%A0_th%E1%BB%A9_l%E1%BB%97i_vcclefthand_dangrangto_bigdaddy_rapvietmua4_linhanh_fyp_xg8zfj.mp4";
        videoItemLeftHand.videoTitle = "Phi Tiêu";
        videoItemLeftHand.VideoDescription = "LeftHand nhảy bài mới cùng bạn gái!!!";
        videoItems.add(videoItemLeftHand);

        videoViewPager.setAdapter(new VideoAdapter(videoItems));

        return view;
    }
}