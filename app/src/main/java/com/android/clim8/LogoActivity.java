package com.android.clim8;

import android.content.Intent;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LogoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#6dd5ed" ));
        Thread splash = new Thread(){
            public void run(){
                try {
                    StartAnimations();
                    sleep(1000);
                    Intent dashboard = new Intent(getBaseContext(),DashboardActivity.class);
                    startActivity(dashboard);
                    finish();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        splash.start();
    }


    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        ConstraintLayout l=(ConstraintLayout) findViewById(R.id.backGround);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.logo);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.imageView);
        TextView splashBanTxt = (TextView) findViewById(R.id.textView);
        TextView splashTitle = (TextView) findViewById(R.id.textView2);

        iv.clearAnimation();
        iv.startAnimation(anim);

        splashBanTxt.clearAnimation();
        splashBanTxt.startAnimation(anim);

        splashTitle.clearAnimation();
        splashTitle.startAnimation(anim);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}
