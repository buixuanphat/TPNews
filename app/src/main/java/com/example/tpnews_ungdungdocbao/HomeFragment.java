package com.example.tpnews_ungdungdocbao;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

    private ArrayList<Category> arrlCategory;
    private ArrayList<Article> arrlArticle;
    private ArrayList<Outlet> arrlOutlet;

    private DatabaseReference myFireBaseDB;
    private ValueEventListener categoryListener, outletListener, articleListener;

    private ArticleAdapter articleAdapter;

    private ListView lvArticle;
    private ProgressBar progressBarHome;
    private HorizontalScrollView horizontalScrollView;
    private LinearLayout linearScrollView;

    private TextView selectedTextView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        // Khởi tạo các thành phần UI
        horizontalScrollView = view.findViewById(R.id.horizontalscrollview);
        linearScrollView = view.findViewById(R.id.linearScrollView);
        progressBarHome = view.findViewById(R.id.progressBarHome);
        lvArticle = view.findViewById(R.id.lvArticle);

        arrlArticle = new ArrayList<>();
        arrlOutlet = new ArrayList<>();
        arrlCategory = new ArrayList<>();

        myFireBaseDB = FirebaseDatabase.getInstance().getReference();

        loadCategories();
        loadOutlets();
        loadArticles();

        return view;
    }

    private void loadCategories() {
        categoryListener = myFireBaseDB.child("Category").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null || linearScrollView == null) return;

                arrlCategory.clear();
                linearScrollView.removeAllViews();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category cat = dataSnapshot.getValue(Category.class);
                    if (cat != null) {
                        arrlCategory.add(cat);
                        addCategoryToScrollView(cat);
                    }
                }

                if (linearScrollView.getChildCount() > 0) {
                    linearScrollView.getChildAt(0).performClick(); // Chọn item đầu tiên
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi kết nối với Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addCategoryToScrollView(Category category) {
        TextView textView = new TextView(getContext());
        textView.setText(category.getName());
        textView.setTextColor(Color.GRAY);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        textView.setLayoutParams(params);
        textView.setTextSize(20);
        textView.setPadding(20, 10, 20, 10);

        textView.setOnClickListener(v -> {
            if (selectedTextView != null) {
                selectedTextView.setTextColor(Color.GRAY); // Reset màu cũ
            }
            textView.setTextColor(getResources().getColor(R.color.tab_selected_color));
            selectedTextView = textView;

            // Cuộn item được chọn về giữa
            int scrollToX = (int) (textView.getX()
                    + textView.getWidth() / 2
                    - horizontalScrollView.getWidth() / 2);
            horizontalScrollView.smoothScrollTo(scrollToX, 0);

            // Cập nhật danh sách bài viết liên quan
            ArrayList<Article> articles = new ArrayList<>();
            for (Article article : arrlArticle) {
                if (article.getCategory() != null && article.getCategory().equals(category.getName())) {
                    articles.add(article);
                }
            }
            updateArticleList(articles);
        });

        linearScrollView.addView(textView);
    }

    private void loadOutlets() {
        outletListener = myFireBaseDB.child("Outlet").addValueEventListener(new ValueEventListener() {
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
    }

    private void loadArticles() {
        progressBarHome.setVisibility(View.VISIBLE);
        articleListener = myFireBaseDB.child("Article").addValueEventListener(new ValueEventListener() {
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
    }

    private void updateArticleList(ArrayList<Article> articles) {
        if (getContext() == null || lvArticle == null) return;

        articleAdapter = new ArticleAdapter(getContext(), R.layout.layout_article, articles, arrlOutlet);
        lvArticle.setAdapter(articleAdapter);
        lvArticle.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getContext(), DetailsActivity.class);
            Article article = articles.get(position);
            intent.putExtra("title", article.getTitle());
            intent.putExtra("description", article.getDescription());
            intent.putExtra("content", article.getContent());
            intent.putExtra("imageurl", article.getImageUrl());
            for (Outlet outlet : arrlOutlet) {
                if (outlet.getName().equals(article.getOutlet())) {
                    intent.putExtra("logourl", outlet.getLogoLink());
                    break;
                }
            }
            startActivity(intent);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (myFireBaseDB != null) {
            if (categoryListener != null) myFireBaseDB.child("Category").removeEventListener(categoryListener);
            if (outletListener != null) myFireBaseDB.child("Outlet").removeEventListener(outletListener);
            if (articleListener != null) myFireBaseDB.child("Article").removeEventListener(articleListener);
        }
    }
}
