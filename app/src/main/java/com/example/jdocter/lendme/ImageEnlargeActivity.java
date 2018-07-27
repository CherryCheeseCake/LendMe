package com.example.jdocter.lendme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageEnlargeActivity extends AppCompatActivity {

    private ImageView ivItemImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_enlarge);

        ivItemImage = findViewById(R.id.ivItemImage);

        String ImageUrl = getIntent().getStringExtra("ImageUrl");
        Glide.with(ImageEnlargeActivity.this)
                .load(ImageUrl)
                .into(ivItemImage);


        ivItemImage.setClickable(true);
        ivItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
