package com.example.user.incasation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NavigationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
    }

    public void createNewTransaction(View view) {
        Intent intent = new Intent(NavigationActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void listTransactions(View view) {
        Intent intent = new Intent(NavigationActivity.this, ListActivity.class);
        startActivity(intent);
    }

    public void logout(View view) {
        Intent intent = new Intent(NavigationActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
