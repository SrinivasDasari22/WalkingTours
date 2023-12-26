package com.example.walkingtours;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {


    private Typeface myCustomFont;
    private TextView detail_title;
    private ImageView detail_image;
    private TextView detail_address;
    private TextView detail_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable color
                = new ColorDrawable(Color.parseColor("#20B2AA"));
        actionBar.setBackgroundDrawable(color);

        actionBar.setDisplayShowTitleEnabled(false);

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setLogo(R.drawable.bus_icon);


        actionBar.setHomeAsUpIndicator(R.drawable.home_image);


        detail_title = findViewById(R.id.detail_title);
        detail_address = findViewById(R.id.detail_address);
        detail_image = findViewById(R.id.detail_image);
        detail_description = findViewById(R.id.detail_descrption);



        String id="";


        if (getIntent().hasExtra("FENCE_ID")) {
            id = getIntent().getStringExtra("FENCE_ID");
            detail_title.setText(id);
        }

        FenceData fenceData =FenceVolley.fenceDataHashMap.get(id);
        detail_address.setText(fenceData.getAddress());
        detail_description.setText(fenceData.getDescription());


        System.out.println(fenceData.getImage());


        Picasso.get().load(fenceData.getImage())
                .into(detail_image);



        setFonts();




    }
    private void customizeActionBar() {

        // This function sets the font of the title in the app bar

        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null)
            return;

        String t = getTitle().toString();
        TextView tv = new TextView(this);

        tv.setText(t);
        tv.setTextSize(24);
        tv.setTextColor(Color.WHITE);
        tv.setTypeface(myCustomFont, Typeface.NORMAL);
//        actionBar.setDisplayUseLogoEnabled(true);

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(tv);

    }

    private void setFonts() {
        // Fonts go in the "assets" folder, with java and res
        myCustomFont = Typeface.createFromAsset(getAssets(), "fonts/Acme-Regular.ttf");

        detail_title.setTypeface(myCustomFont);
        detail_address.setTypeface(myCustomFont);
        detail_description.setTypeface(myCustomFont);


//        customizeActionBar();
    }

}