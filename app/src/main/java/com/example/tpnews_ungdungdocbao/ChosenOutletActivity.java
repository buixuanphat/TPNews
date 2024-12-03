package com.example.tpnews_ungdungdocbao;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChosenOutletActivity extends AppCompatActivity {

    private DatabaseReference myFireBaseDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chosen_outlet);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        myFireBaseDB = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();

        ListView lvChosenOutlet = findViewById(R.id.lvChosenOutlet);
        ArrayList<Article> arrlArticle = new ArrayList<>();
        ArrayList <Article> arrlChosen = new ArrayList<>();
        ChosenOutletAdapter adapter = new ChosenOutletAdapter(ChosenOutletActivity.this, R.layout.layout_article, arrlChosen, intent.getStringExtra("logo"));
        lvChosenOutlet.setAdapter(adapter);

        myFireBaseDB.child("Article").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Article article = dataSnapshot.getValue(Article.class);
                    if (article != null) {
                        arrlArticle.add(article);
                    }
                }
                for (Article article : arrlArticle)
                {
                    if(article.getOutlet().equals(intent.getStringExtra("outlet")))
                    {
                        arrlChosen.add(article);
                    }
                }
                if (arrlChosen.isEmpty())
                {
                    Toast.makeText(ChosenOutletActivity.this, "Không có nội dung", Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
                ProgressBar progressBar = findViewById(R.id.progressBarChosenOutlet);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        lvChosenOutlet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent2 = new Intent(ChosenOutletActivity.this, DetailsActivity.class);
                intent2.putExtra("title", arrlChosen.get(position).getTitle());
                intent2.putExtra("description", arrlChosen.get(position).getDescription());
                intent2.putExtra("content", arrlChosen.get(position).getContent());
                intent2.putExtra("imageurl", arrlChosen.get(position).getImageUrl());
                intent2.putExtra("logourl", intent.getStringExtra("logo"));
                startActivity(intent2);
            }
        });



    }
}