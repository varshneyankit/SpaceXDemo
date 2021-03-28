package com.example.spacexdemo;

import android.database.CursorWindow;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacexdemo.adapter.CrewListAdapter;
import com.example.spacexdemo.api.ApiClient;
import com.example.spacexdemo.api.ApiInterface;
import com.example.spacexdemo.pojos.CrewListModel;
import com.example.spacexdemo.ui.main.MainViewModel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final List<CrewListModel> crewListResults = new ArrayList<>();
    private RecyclerView crewListRV;
    private CrewListAdapter crewListAdapter;
    private ApiInterface apiInterface;
    private MainViewModel viewModel;
    private RelativeLayout progressBarLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        init();

        progressBarLayout.setVisibility(View.VISIBLE);
        viewModel.getAllCrewData().observe(this, crewListModels -> {
            crewListResults.clear();
            crewListResults.addAll(crewListModels);
            crewListAdapter.notifyDataSetChanged();
            progressBarLayout.setVisibility(View.GONE);
        });
        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 100 * 1024 * 1024); //the 100MB is the new size
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        crewListRV = findViewById(R.id.crew_recycler_view);
        progressBarLayout = findViewById(R.id.fullscreen_progress_layout);
        progressBarLayout.setVisibility(View.GONE);
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        crewListAdapter = new CrewListAdapter(this, crewListResults, viewModel);
        crewListRV.setAdapter(crewListAdapter);

    }

    public void refreshData(View view) {
        progressBarLayout.setVisibility(View.VISIBLE);
        viewModel.makeApiRequest();
    }
}