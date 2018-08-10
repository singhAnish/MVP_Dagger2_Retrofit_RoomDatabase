package sampleproject.android.com.TestProject.base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import sampleproject.android.com.TestProject.R;
import sampleproject.android.com.TestProject.util.Local;

public abstract class BaseActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        FrameLayout frame = findViewById(R.id.baseFrameLayout);
        getLayoutInflater().inflate(getContentView(), frame);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Local.hideSoftKeys(BaseActivity.this);
                onBackPressed();
            }
        });

        onViewReady(savedInstanceState, getIntent());
    }

    @CallSuper
    protected void onViewReady(Bundle savedInstanceState, Intent intent){
        //This should be implemented by child class
    }

    protected abstract int getContentView();

    protected void showDialog(){
        if(mDialog == null){
            mDialog = new ProgressDialog(this);
            mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mDialog.setCancelable(true);
            mDialog.setCanceledOnTouchOutside(false);
        }
        mDialog.show();
    }

    protected void dismissDialog(){
        if(mDialog != null && mDialog.isShowing()){
            mDialog.dismiss();
        }
    }

    protected void showBackTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }
        toolbar.setTitle(title);
    }
}
