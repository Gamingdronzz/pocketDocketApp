package com.example.hp.pocket_docket.activity;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.hp.pocket_docket.R;
import com.example.hp.pocket_docket.shared_preferences.SavedSharedPreference;

public class SplashActivity extends AppCompatActivity {
    Intent intent;

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        StartAnimations();

    }

    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        LinearLayout l = (LinearLayout) findViewById(R.id.activity_splash);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.logo);
        iv.clearAnimation();
        iv.startAnimation(anim);

        Thread thread = new Thread() {
            public void run() {
                super.run();
                try {
                    if (SavedSharedPreference.getUserName(SplashActivity.this).length() == 0) {
                        intent = new Intent(SplashActivity.this, LoginActivity.class);
                    } else {
                        if (SavedSharedPreference.getType(SplashActivity.this).equals("1")) {
                            intent = new Intent(SplashActivity.this, AdminDashboardActivity.class);
                        } else {
                            intent = new Intent(SplashActivity.this, DeveloperDashboardActivity.class);
                        }
                    }

                } catch (Exception e1) {
                    e1.printStackTrace();
                } finally {
                    startActivity(intent);
                }
            }
        };
        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
