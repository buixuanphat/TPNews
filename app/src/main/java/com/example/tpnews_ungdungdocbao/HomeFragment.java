package com.example.tpnews_ungdungdocbao;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    ArrayList<Category> arrlCategory;
    private DatabaseReference myFireBaseDB;

    ArrayList<Article> arrlArticle;
    ArrayList<Outlet> arrlOutlet;
    ArticleAdapter articleAdapter;

    ListView lvArticle;

    ProgressBar progressBarHome;

    HorizontalScrollView horizontalScrollView;

    LinearLayout linearScrollView;

    // Biến lưu item đã chọn
    private TextView selectedTextView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        // Khởi tạo các thành phần
        horizontalScrollView = view.findViewById(R.id.horizontalscrollview);
        linearScrollView = view.findViewById(R.id.linearScrollView);
        progressBarHome = view.findViewById(R.id.progressBarHome);
        lvArticle = view.findViewById(R.id.lvArticle);

        arrlArticle = new ArrayList<>();
        arrlOutlet = new ArrayList<>();
        arrlCategory = new ArrayList<>();

        myFireBaseDB = FirebaseDatabase.getInstance().getReference();

        // Đọc dữ liệu từ Firebase
        myFireBaseDB.child("Category").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrlCategory.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category cat = dataSnapshot.getValue(Category.class);
                    if (cat != null) {
                        arrlCategory.add(cat);
                    }
                }
                for (Category cat : arrlCategory) {
                    TextView textView = new TextView(getContext());
                    textView.setText(cat.getName());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    textView.setTextSize(20);
                    textView.setPadding(20, 10, 20, 10);
                    textView.setTextColor(Color.BLACK); // Màu chữ mặc định
                    textView.setLayoutParams(params);

                    linearScrollView.addView(textView);

                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Đổi màu chữ item được chọn
                            if (selectedTextView != null) {
                                selectedTextView.setTextColor(Color.BLACK); // Reset màu cũ
                            }
                            textView.setTextColor(Color.RED); // Đặt màu đỏ cho item được chọn
                            selectedTextView = textView;

                            // Cuộn item được chọn về giữa
                            int scrollToX = (int) (textView.getX()
                                    + textView.getWidth() / 2
                                    - horizontalScrollView.getWidth() / 2);
                            horizontalScrollView.smoothScrollTo(scrollToX, 0);

                            // Cập nhật danh sách bài viết liên quan
                            ArrayList<Article> articles = new ArrayList<>();
                            for (Article article : arrlArticle) {
                                if (article.getCategory() != null
                                        && article.getCategory().toString().equals(textView.getText().toString())) {
                                    articles.add(article);
                                }
                            }
                            articleAdapter = new ArticleAdapter(getContext(), R.layout.layout_article, articles, arrlOutlet);
                            lvArticle.setAdapter(articleAdapter);
                        }
                    });
                }
                View firstChoice = linearScrollView.getChildAt(0);
                if(firstChoice != null)
                {
                    firstChoice.performClick();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi kết nối với Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        myFireBaseDB.child("Outlet").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrlOutlet.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Outlet outlet = dataSnapshot.getValue(Outlet.class);
                    if (outlet != null) {
                        arrlOutlet.add(outlet);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi kết nối với Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        myFireBaseDB.child("Article").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrlArticle.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Article article = dataSnapshot.getValue(Article.class);
                    if (article != null) {
                        arrlArticle.add(article);
                    }
                }
                progressBarHome.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBarHome.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Lỗi kết nối với Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



        return view;
    }
}
