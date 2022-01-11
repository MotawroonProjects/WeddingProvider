package com.e_co.weddingprovider.uis.activity_language;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;


import com.e_co.weddingprovider.R;
import com.e_co.weddingprovider.databinding.ActivityLanguageBinding;
import com.e_co.weddingprovider.uis.activity_base.BaseActivity;

import io.paperdb.Paper;

public class LanguageActivity extends BaseActivity {
    private ActivityLanguageBinding binding;
    private String lang = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_language);

        initView();
    }

    private void initView() {
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");

        if (lang.equals("ar")) {
            binding.flArabic.setBackgroundResource(R.drawable.small_rounded_stroke_primary);
            binding.flEnglish.setBackgroundResource(0);
        } else {
            binding.flArabic.setBackgroundResource(0);
            binding.flEnglish.setBackgroundResource(R.drawable.small_rounded_stroke_primary);
        }
        binding.btnNext.setVisibility(View.VISIBLE);

        binding.cardArabic.setOnClickListener(view -> {
            lang = "ar";
            binding.flArabic.setBackgroundResource(R.drawable.small_rounded_stroke_primary);
            binding.flEnglish.setBackgroundResource(0);
            binding.btnNext.setVisibility(View.VISIBLE);

        });

        binding.cardEnglish.setOnClickListener(view -> {
            lang = "en";
            binding.flArabic.setBackgroundResource(0);
            binding.flEnglish.setBackgroundResource(R.drawable.small_rounded_stroke_primary);
            binding.btnNext.setVisibility(View.VISIBLE);
        });

        binding.btnNext.setOnClickListener(view -> {
            Intent intent = getIntent();
            intent.putExtra("lang", lang);
            setResult(RESULT_OK, intent);
            finish();
        });

    }
}