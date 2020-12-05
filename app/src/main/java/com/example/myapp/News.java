package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class News extends AppCompatActivity {

    LinearLayout job,cricket,weather,movie,food,agriculture,real_state,technology,world,latest,health,stock;
    Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        toolbar=findViewById(R.id.news_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        agriculture=(LinearLayout) findViewById(R.id.agriculture);
        agriculture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(News.this,News_list.class);
                i.putExtra("key","Agriculture");
                startActivity(i);

            }
        });
        job=(LinearLayout)findViewById(R.id.job);
        job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(    News.this,News_list.class);
                i.putExtra("key","Job");
                startActivity(i);
            }
        });
        cricket=(LinearLayout)findViewById(R.id.cricket);
        cricket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(News.this,News_list.class);
                i.putExtra("key","Cricket");
                startActivity(i);
            }
        });
        weather=(LinearLayout)findViewById(R.id.weather);
        weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(News.this,News_list.class);
                i.putExtra("key","Weather");
                startActivity(i);
            }
        });
        movie=(LinearLayout)findViewById(R.id.movie);
        movie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(News.this,News_list.class);
                i.putExtra("key","Movie");
                startActivity(i);
            }
        });
        food=(LinearLayout)findViewById(R.id.food);
        food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(News.this,News_list.class);
                i.putExtra("key","Food");
                startActivity(i);
            }
        });

        technology=(LinearLayout)findViewById(R.id.technology);
        technology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(News.this,News_list.class);
                i.putExtra("key","Technology");
                startActivity(i);
            }
        });

        real_state=(LinearLayout)findViewById(R.id.real_state);
        real_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(News.this,News_list.class);
                i.putExtra("key","Real state");
                startActivity(i);
            }
        });

        stock=(LinearLayout)findViewById(R.id.stock_market);
        stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(News.this,News_list.class);
                i.putExtra("key","stock market");
                startActivity(i);
            }
        });
        latest=(LinearLayout)findViewById(R.id.latest);
        latest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(News.this,News_list.class);
                i.putExtra("key","latest");
                startActivity(i);
            }
        });

        health=(LinearLayout)findViewById(R.id.health);
        health.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(News.this,News_list.class);
                i.putExtra("key","health");
                startActivity(i);
            }
        });

        world=(LinearLayout)findViewById(R.id.world);
        world.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i=new Intent(News.this,News_list.class);
                i.putExtra("key","world");
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}
