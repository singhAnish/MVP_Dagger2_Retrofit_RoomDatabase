package sampleproject.android.com.TestProject.ui.activity.wonder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import sampleproject.android.com.TestProject.R;
import sampleproject.android.com.TestProject.adapter.WonderAdapter;
import sampleproject.android.com.TestProject.app.MyApp;
import sampleproject.android.com.TestProject.contract.WonderContract.View;
import sampleproject.android.com.TestProject.database.AppDatabase;
import sampleproject.android.com.TestProject.model.WonderActivityModel;
import sampleproject.android.com.TestProject.model.WonderActivityModelData;
import sampleproject.android.com.TestProject.network.APIInterface;
import sampleproject.android.com.TestProject.presenter.WonderPresenter;
import sampleproject.android.com.TestProject.ui.activity.wonder.di.DaggerWonderComponent;
import sampleproject.android.com.TestProject.ui.activity.wonder.di.WonderComponent;
import sampleproject.android.com.TestProject.ui.activity.wonder.di.WonderModule;
import sampleproject.android.com.TestProject.util.ConnectionDetector;
import sampleproject.android.com.TestProject.util.Local;
import sampleproject.android.com.TestProject.util.base.BaseActivity;

public class WonderActivity extends BaseActivity implements View {

    private WonderPresenter mPresenter;
    private RecyclerView mRecycler;

    @Inject AppDatabase mDB;
    @Inject APIInterface mInterface;
    @Inject WonderAdapter mAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_wonder;
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);
        showBackTitle(Local.getString(R.string.app_name));
        mRecycler = findViewById(R.id.recyclerView);

        WonderComponent component = DaggerWonderComponent.builder()
                .wonderModule(new WonderModule(this)).appComponent(MyApp.get(this).getComponent()).build();
        component.inject(this);
        mPresenter = new WonderPresenter(this, mDB);
    }

    @Override
    public void onPause() {
        mPresenter.onViewInactive();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.onViewActive(this);
    }

    @Override
    public void loadContentFromAPI() {
        if (ConnectionDetector.isConnected()) {
            showDialog();
            Call<WonderActivityModel> call = mInterface.getMovieListData();
            call.enqueue(new Callback<WonderActivityModel>() {
                @Override
                public void onResponse(@NonNull Call<WonderActivityModel> call, @NonNull Response<WonderActivityModel> response) {
                    if (response.isSuccessful()) {
                        dismissDialog();
                        mDB.wonderDao().clearWonderData();
                        WonderActivityModel movieList = response.body();
                        if(movieList != null) {
                            ArrayList<WonderActivityModelData> mModel = new ArrayList<>(Arrays.asList(movieList.getData()));
                            for (int i = 0; i < mModel.size(); i++) {
                                mDB.wonderDao().insertWonderData(mModel.get(i));
                            }
                            mPresenter.loadGridView();
                        }else{
                            showToast(R.string.somethingWrong);
                        }
                    } else {
                        showToast(R.string.somethingWrong);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<WonderActivityModel> call, @NonNull Throwable t) {
                    dismissDialog();
                    t.printStackTrace();
                }
            });
        }
    }

    @Override
    public void updateGridView() {
        mRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecycler.setAdapter(mAdapter);
    }
}
