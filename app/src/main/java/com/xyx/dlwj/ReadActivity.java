package com.xyx.dlwj;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.io.IOException;
import java.io.InputStream;

import static java.lang.Thread.sleep;

public class ReadActivity extends AppCompatActivity {

    int p = 0;
    private PDFView pdfView;
    private Toolbar toolbar;

    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       showAD();
                   }
               });
            }
        }).start();

        MobileAds.initialize(this,"ca-app-pub-7420611722821229~2932209548"); //单元格

        mInterstitialAd = new InterstitialAd(this);
//        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");//测试
         mInterstitialAd.setAdUnitId("ca-app-pub-7420611722821229/6289000629");//上线//插屏幕
        //ca-app-pub-7420611722821229/6352031066 横幅
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });


        pdfView = findViewById(R.id.read);
        loadPDF(pdfView, 0);


        Button save = findViewById(R.id.bt_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
                showAD();
            }
        });


        Button button = findViewById(R.id.bt_AD);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
                int userpage = sharedPreferences.getInt("page", 0);
                loadPDF(pdfView, userpage);
                showAD();
            }
        });


    }

    private void showAD() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
    }

    private void loadPDF(PDFView pdfView, int page) {

        pdfView.fromAsset("dlwj.pdf")   //设置pdf文件地址
//                .pages(0, 2, 1, 3, 3, 3)//只加载某页面
                .enableSwipe(true) // allows to block changing pages using swipe
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(page)
                // allows to draw something on the current page, usually visible in the middle of the screen
                .onDraw(null)
                // allows to draw something on all pages, separately for every page. Called only for visible pages
                .onDrawAll(null)
                .onLoad(null) // called after document is loaded and starts to be rendered
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(final int page, final int pageCount) {
                        toolbar.setSubtitle(String.format(" %s /%s", page + 1, pageCount));
                        p = page;
                        Log.v("-->", "" + page + "/" + pageCount);
                    }
                })
                .onPageScroll(null)
                .onError(null)
                .onPageError(null)
                .onRender(null) // called after document is rendered for the first time
                // called on single tap, return true if handled, false to toggle scroll handle visibility
                .onTap(null)
//                .onLongPress(onLongPressListener)
                .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                .password(null)
                .scrollHandle(null)
                .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                // spacing between pages in dp. To define spacing color, set view background
                .spacing(0)
//                .autoSpacing(false) // add dynamic spacing to fit each page on its own on the screen
//                .linkHandler(DefaultLinkHandler)
//                .pageFitPolicy(FitPolicy.WIDTH) // mode to fit pages in the view
//                .fitEachPage(false) // fit each page to the view, else smaller pages are scaled relative to largest page.
//                .pageSnap(false) // snap pages to screen boundaries
//                .pageFling(false) // make a fling change only a single page like ViewPager
//                .nightMode(false) // toggle night mode
                .load();
    }



    private void save() {
        //步骤1：创建一个SharedPreferences对象
        SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        //步骤2： 实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //步骤3：将获取过来的值放入文件
        // editor.putString("name", "Tom");
        editor.putInt("page", p);
        //editor.putBoolean("marrid",false);
        //步骤4：提交
        editor.commit();
    }


}



