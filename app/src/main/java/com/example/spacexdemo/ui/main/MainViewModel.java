package com.example.spacexdemo.ui.main;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.spacexdemo.api.ApiClient;
import com.example.spacexdemo.api.ApiInterface;
import com.example.spacexdemo.database.AppDatabase;
import com.example.spacexdemo.database.CrewDao;
import com.example.spacexdemo.pojos.CrewListModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends AndroidViewModel {

    private final CrewDao mDao;
    private final ApiInterface apiInterface;

    public MainViewModel(@NonNull Application application) {
        super(application);
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        AppDatabase db = AppDatabase.getInstance(application);
        mDao = db.dao();
    }

    public LiveData<List<CrewListModel>> getAllCrewData() {
        LiveData<List<CrewListModel>> data = mDao.getAllCrewData();
        makeApiRequest();
        return data;
    }

    public void setSpecficRow(CrewListModel model) {
        Log.e("TAG", "setSpecficRow: " + model.getName() + " \n " + model.getImageUrl());
        AppDatabase.databaseWriteExecutor.execute(() -> mDao.updateImageData(model));
    }

    public void makeApiRequest() {
        Call<List<CrewListModel>> call = apiInterface.getAllCrew();
        call.enqueue(new Callback<List<CrewListModel>>() {
            @Override
            public void onResponse(Call<List<CrewListModel>> call, Response<List<CrewListModel>> response) {
                if (response.body() != null) {
                    {
                        AppDatabase.databaseWriteExecutor.execute(() -> {
                            mDao.deleteAllCrewData();
                            mDao.insertAllCrewData(response.body());
                        });
                    }

                }

            }

            @Override
            public void onFailure(Call<List<CrewListModel>> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }
}