package com.example.tpnews_ungdungdocbao.Fragments;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
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

import com.example.tpnews_ungdungdocbao.Models.Article;
import com.example.tpnews_ungdungdocbao.Models.Category;
import com.example.tpnews_ungdungdocbao.Data.DatabaseHelper;
import com.example.tpnews_ungdungdocbao.Views.DetailsActivity;
import com.example.tpnews_ungdungdocbao.Data.NetworkReceiver;
import com.example.tpnews_ungdungdocbao.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import com.example.tpnews_ungdungdocbao.Adapters.ArticleAdapter;

public class HomeFragment extends Fragment {

    private HorizontalScrollView scrollView;
    private LinearLayout categoryContainer;
    private ProgressBar progressBar;
    private ListView lvArticle;
    private DatabaseReference firebaseDB;
    private ArrayList<Category> categories = new ArrayList<>();
    private ArrayList<Article> articles = new ArrayList<>();
    private TextView selectedCategoryView = null;
    private NetworkReceiver networkReceiver;
    private ArticleAdapter articleAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        scrollView = view.findViewById(R.id.horizontalscrollview);
        categoryContainer = view.findViewById(R.id.linearScrollView);
        progressBar = view.findViewById(R.id.progressBarHome);
        lvArticle = view.findViewById(R.id.lvArticle);
        progressBar.setVisibility(View.GONE);

        // Khởi tạo và đăng ký NetworkReceiver
        networkReceiver = new NetworkReceiver(new NetworkReceiver.NetworkListener() {
            @Override
            public void onNetworkAvailable() {
                scrollView.setVisibility(View.VISIBLE);
                loadCategories();
                saveArticlesForOffline();
            }

            @Override
            public void onNetworkLost() {
                scrollView.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Mất kết nối Internet", Toast.LENGTH_SHORT).show();
                loadArticlesFromLocal();
            }
        });

        // Đăng ký BroadcastReceiver để lắng nghe thay đổi trạng thái mạng
        requireContext().registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));






        return view;
    }

    // Hàm tải danh mục và bài viết từ Firebase
    private void loadCategories() {
        articles.clear();
        progressBar.setVisibility(View.VISIBLE);
        firebaseDB = FirebaseDatabase.getInstance().getReference();
        firebaseDB.child("Category").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categories.clear();
                categoryContainer.removeAllViews();

                if (!snapshot.exists()) {
                    Toast.makeText(requireContext(), "Không tìm thấy danh mục.", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category category = dataSnapshot.getValue(Category.class);
                    if (category != null) {
                        categories.add(category);

                        TextView textView = createCategoryTextView(category);
                        textView.setOnClickListener(v -> onCategoryClick(category, textView));

                        categoryContainer.addView(textView);
                    }
                }

                if (!categories.isEmpty()) {
                    TextView firstCategoryView = (TextView) categoryContainer.getChildAt(0);
                    if (firstCategoryView != null) {
                        firstCategoryView.performClick();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Tạo TextView cho danh mục
    private TextView createCategoryTextView(Category category) {
        TextView textView = new TextView(requireContext());
        textView.setText(category.getName());
        textView.setTextColor(Color.GRAY);
        textView.setTextSize(20);
        textView.setPadding(30, 10, 30, 10);
        Typeface typeface = ResourcesCompat.getFont(requireContext(), R.font.roboto_medium);
        textView.setTypeface(typeface != null ? typeface : Typeface.DEFAULT);
        return textView;
    }

    // Xử lý sự kiện khi người dùng nhấn vào một danh mục
    private void onCategoryClick(Category category, TextView textView) {
        if (selectedCategoryView != null) {
            selectedCategoryView.setTextColor(Color.GRAY);
        }
        textView.setTextColor(requireContext().getResources().getColor(R.color.tab_selected_color));
        selectedCategoryView = textView;

        int scrollToX = (int) (textView.getX() + textView.getWidth() / 2 - scrollView.getWidth() / 2);
        scrollView.smoothScrollTo(scrollToX, 0);
        articles.clear();
        //articleAdapter.notifyDataSetChanged();
        loadArticlesByCategory(category.getName());
    }

    // Tải bài viết theo danh mục
    private void loadArticlesByCategory(String categoryName) {
        progressBar.setVisibility(View.VISIBLE);
        firebaseDB.child("Article").orderByChild("category").equalTo(categoryName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    articles.clear();
                    updateArticleList();
                    progressBar.setVisibility(View.GONE);
                }

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Article article = dataSnapshot.getValue(Article.class);
                    if (article != null) {
                        articles.add(article);
                    }
                }
                updateArticleList();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Cập nhật danh sách bài viết
    private void updateArticleList() {

            articleAdapter = new ArticleAdapter(requireContext(), R.layout.layout_article, articles);
            lvArticle.setAdapter(articleAdapter);


        lvArticle.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(requireContext(), DetailsActivity.class);
            intent.putExtra("id", articles.get(position).getId());
            startActivity(intent);
        });
    }

    // Lưu bài viết khi mất kết nối
    private void loadArticlesFromLocal() {
        DatabaseHelper databaseHelper = new DatabaseHelper(requireContext());
        ArrayList<Article> offlineArticles = databaseHelper.getNewsByCategory("Trang chủ");
        articleAdapter = new ArticleAdapter(requireContext(), R.layout.layout_article, offlineArticles);
        lvArticle.setAdapter(articleAdapter);
        articleAdapter.notifyDataSetChanged();
        lvArticle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(requireContext(), DetailsActivity.class);
                intent.putExtra("id", offlineArticles.get(position).getId());
                startActivity(intent);
            }
        });
    }

    // Hàm lưu bài viết xuống SQLite cho offline
    private void saveArticlesForOffline() {
        ArrayList<Article> offlineArticles = new ArrayList<>();
        firebaseDB.child("Article").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Article article = dataSnapshot.getValue(Article.class);
                    offlineArticles.add(article);
                }
                DatabaseHelper databaseHelper = new DatabaseHelper(requireContext());
                databaseHelper.clearAllNews();
                databaseHelper.insertNews(offlineArticles);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
