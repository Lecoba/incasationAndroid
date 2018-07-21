package com.example.user.incasation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.user.incasation.http.OkHttpHandler;

public class ListActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recyclerView = findViewById(R.id.recycleView);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        loadTransactionList();
    }

    private void loadTransactionList() {
        OkHttpHandler okHttpHandler = new OkHttpHandler(this);
        okHttpHandler.execute(getString(R.string.apiGetTransactionListURL));
    }
}
