package com.example.tpnews_ungdungdocbao.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tpnews_ungdungdocbao.R;
import com.example.tpnews_ungdungdocbao.Adapters.VideoAdapter;
import com.example.tpnews_ungdungdocbao.Models.VideoItem;

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

        VideoItem baoLaoDong = new VideoItem();
        baoLaoDong.videoURL = "https://res.cloudinary.com/dq1oo3fod/video/upload/v1734314671/%C4%90%C3%A3_m%E1%BA%AFt_v%E1%BB%9Bi_c%E1%BA%A3nh_drift_si%C3%AAu_xe_%C3%B4t%C3%B4_%C4%91i%C3%AAu_luy%E1%BB%87n___B%C3%A1o_Lao_%C4%90%E1%BB%99ng_shorts_tydi38.mp4";
        baoLaoDong.videoTitle = "Báo Lao Động";
        baoLaoDong.VideoDescription = "Ngày hội sự kiện ra mắt các siêu xe";
        videoItems.add(baoLaoDong);

        VideoItem tinNongYTe = new VideoItem();
        tinNongYTe.videoURL = "https://res.cloudinary.com/dq1oo3fod/video/upload/v1734314673/Tin_n%C3%B3ng_y_t%E1%BA%BF_12_12__17_tr%E1%BA%BB_em_%E1%BB%9F_Mexico_t%E1%BB%AD_vong_do_nhi%E1%BB%85m_vi_khu%E1%BA%A9n_t%E1%BB%AB_t%C3%BAi_truy%E1%BB%81n_d%E1%BB%8Bch___SK%C4%90S_shorts_tov8i5.mp4";
        tinNongYTe.videoTitle = "Tin Nóng Y Tế 24h";
        tinNongYTe.VideoDescription = "Tử vong vì nhiễm khuẩn lạ";
        videoItems.add(tinNongYTe);

        VideoItem BaMuoisNong = new VideoItem();
        BaMuoisNong.videoURL = "https://res.cloudinary.com/dq1oo3fod/video/upload/v1734314672/30s_N%C3%B3ng___K%E1%BA%BFt_lu%E1%BA%ADn_%C4%91i%E1%BB%81u_tra_v%E1%BB%A5_d%E1%BB%B1_%C3%A1n_%C4%90%E1%BA%A1i_Ninh_w8gx4f.mp4";
        BaMuoisNong.videoTitle = "30s Nóng";
        BaMuoisNong.VideoDescription = "Kết luận điều tra vụ án tại Đại Ninh";
        videoItems.add(BaMuoisNong);

        VideoItem BaMuoisNong_mot = new VideoItem();
        BaMuoisNong_mot.videoURL = "https://res.cloudinary.com/dq1oo3fod/video/upload/v1734314671/Th%E1%BB%9Di_ti%E1%BA%BFt_B%E1%BA%AFc_b%E1%BB%99_%C4%90%C3%AAm_nay_v%C3%A0_Ng%C3%A0y_mai_1_9___Truy%E1%BB%81n_H%C3%ACnh_Nh%C3%A2n_D%C3%A2n_pwy14d.mp4";
        BaMuoisNong_mot.videoTitle = "30s Nóng";
        BaMuoisNong_mot.VideoDescription = "Thời tiết Bắc Bộ đêm nay và ngày mai";
        videoItems.add(BaMuoisNong_mot);

        VideoItem baoLaoDong_mot = new VideoItem();
        baoLaoDong_mot.videoURL = "https://res.cloudinary.com/dq1oo3fod/video/upload/v1734314671/Ca_s%C4%A9_Ho%C3%A0_Minzy_c%E1%BB%95_v%C5%A9_tr%E1%BA%ADn_C%C3%B4ng_an_H%C3%A0_N%E1%BB%99i_v%C3%A0_B%C3%ACnh_%C4%90%E1%BB%8Bnh___B%C3%A1o_Lao_%C4%90%E1%BB%99ng_shorts_nvmskm.mp4";
        baoLaoDong_mot.videoTitle = "Báo Lao Động";
        baoLaoDong_mot.VideoDescription = "Chung kết thể thao bóng đá quốc gia";
        videoItems.add(baoLaoDong_mot);

        VideoItem BaMuoisNong_hai = new VideoItem();
        BaMuoisNong_hai.videoURL = "https://res.cloudinary.com/dq1oo3fod/video/upload/v1734314670/D%E1%BB%B1_b%C3%A1o_th%E1%BB%9Di_ti%E1%BA%BFt_Nam_B%E1%BB%99_v%C3%A0_T%C3%A2y_Nguy%C3%AAn_h%C3%B4m_nay_6_9___Truy%E1%BB%81n_H%C3%ACnh_Nh%C3%A2n_D%C3%A2n_ggss1z.mp4";
        BaMuoisNong_hai.videoTitle = "30s Nóng";
        BaMuoisNong_hai.VideoDescription = "Thời tiết Nam Bộ đêm nay và ngày mai";
        videoItems.add(BaMuoisNong_hai);

        VideoItem BaMuoisNong_ba = new VideoItem();
        BaMuoisNong_ba.videoURL = "https://res.cloudinary.com/dq1oo3fod/video/upload/v1734314671/30s_N%C3%B3ng___Xe_t%E1%BA%A3i_t%C3%B4ng_%C4%91u%C3%B4i_%C3%B4_t%C3%B4_r%E1%BB%93i_lao_v%C3%A0o_cabin_tr%E1%BA%A1m_thu_ph%C3%AD_%E1%BB%9F_Kh%C3%A1nh_H%C3%B2a_hi3r7b.mp4";
        BaMuoisNong_ba.videoTitle = "30s Nóng";
        BaMuoisNong_ba.VideoDescription = "Tai nạn thu phí Khánh Hòa";
        videoItems.add(BaMuoisNong_ba);

        VideoItem baoTuoiTre = new VideoItem();
        baoTuoiTre.videoURL = "https://res.cloudinary.com/dq1oo3fod/video/upload/v1734314670/30s_N%C3%B3ng__C%C3%B4_g%C3%A1i_t%E1%BB%AD_vong_d%C6%B0%E1%BB%9Bi_b%C3%A1nh_xe_b%E1%BB%93n_b%C3%AA_t%C3%B4ng_demw0g.mp4";
        baoTuoiTre.videoTitle = "Báo Tuổi Trẻ";
        baoTuoiTre.VideoDescription = "Tử vong dưới bánh xe bồn bê tông";
        videoItems.add(baoTuoiTre);

        videoViewPager.setAdapter(new VideoAdapter(videoItems));

        return view;
    }
}