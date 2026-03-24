package com.vigil.security.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.vigil.security.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View logoContainer = findViewById(R.id.logo_container);
        View bottomContainer = findViewById(R.id.bottom_container);
        ProgressBar progressBar = findViewById(R.id.splash_progress);

        // 1. Initial Logo Animation (Fade In + Scale Up)
        logoContainer.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(1000)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        // 2. Bottom Content Animation (Fade In after a short delay)
        bottomContainer.animate()
                .alpha(1f)
                .setDuration(800)
                .setStartDelay(500)
                .start();

        // 3. Progress Bar Animation (0 to 100)
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", 0, 100);
        progressAnimator.setDuration(3000); // 3 seconds to complete
        progressAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        progressAnimator.setStartDelay(800);
        
        progressAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Final exit animation for the logo before moving to MainActivity
                logoContainer.animate()
                        .alpha(0f)
                        .scaleX(1.1f)
                        .scaleY(1.1f)
                        .setDuration(500)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                finish();
                            }
                        })
                        .start();
            }
        });

        progressAnimator.start();
    }
}