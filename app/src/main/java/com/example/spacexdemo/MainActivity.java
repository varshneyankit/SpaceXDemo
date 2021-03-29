package com.example.spacexdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacexdemo.adapter.CrewListAdapter;
import com.example.spacexdemo.pojos.CrewListModel;
import com.example.spacexdemo.ui.main.MainViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 101;
    private final List<CrewListModel> crewListResults = new ArrayList<>();
    List<File> fileList = new ArrayList<>();
    private RecyclerView crewListRV;
    private CrewListAdapter crewListAdapter;
    private MainViewModel viewModel;
    private RelativeLayout progressBarLayout;
    private File spacexCrewFolder;

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
            saveImages(crewListModels);
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void saveImages(List<CrewListModel> crewListModels) {
        if (crewListModels.isEmpty()) {
            return;
        }
        spacexCrewFolder = new File(Environment.getExternalStorageDirectory() + "/Offline Images");
        if (!spacexCrewFolder.exists()) {
            if (spacexCrewFolder.mkdirs()) {
                Log.e("TAG", "dir made ");
            }
        }
        fileList.clear();
        fileList.addAll(getImages(spacexCrewFolder));
        for (CrewListModel crewListModel : crewListModels) {
            AtomicBoolean found = new AtomicBoolean(false);
            fileList.forEach(file -> {
                if (file.getName().contains(crewListModel.getId())) {
                    found.set(true);
                }
            });
            if (!found.get()) {
                new AsyncTask<String, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(String... strings) {
                        boolean error = false;
                        URL url = null;
                        try {
                            url = new URL(strings[0]);
                        } catch (MalformedURLException e) {
                            error = true;
                            Log.e("TAG", "onResponse: " + e.getMessage());
                            e.printStackTrace();
                        }
                        InputStream is;

                        File f = new File(spacexCrewFolder.getAbsolutePath() + "/" + strings[1] + ".jpg");
                        try {
                            is = url.openStream();
                            OutputStream os = new FileOutputStream(f.getAbsolutePath());

                            byte[] b = new byte[2048];
                            int length;

                            while ((length = is.read(b)) != -1) {
                                os.write(b, 0, length);
                            }
                        } catch (IOException e) {
                            error = true;
                            Log.e("TAG", "onResponse: " + e.getMessage());
                            e.printStackTrace();
                        }

                        Log.e("TAG", "onResponse: " + strings[0] + " done");
                        return error;

                    }

                }.execute(crewListModel.getImageUrl(), crewListModel.getId());

            }
        }
    }

    private void init() {
        crewListRV = findViewById(R.id.crew_recycler_view);
        progressBarLayout = findViewById(R.id.fullscreen_progress_layout);
        progressBarLayout.setVisibility(View.GONE);
        crewListAdapter = new CrewListAdapter(this, crewListResults, fileList);
        crewListRV.setAdapter(crewListAdapter);
        if (!checkPermissions()) {
            requestPermissions();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void refreshData(View view) {
        if (isNetworkConnected()) {
            progressBarLayout.setVisibility(View.VISIBLE);
            deleteFile(spacexCrewFolder);
            viewModel.makeApiRequest();
        } else {
            Toast.makeText(this, "Cannot refresh due to no internet \nPlease check your internet settings", Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Permission Denied ! \n Please allow permissions to enable offline mode", Toast.LENGTH_LONG).show();
                new Handler().postDelayed(this::requestPermissions, 2000);
            }
        }
    }

    private List<File> getImages(File spacexCrewFolder) {
        File[] allImages = spacexCrewFolder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.endsWith(".jpg"));
            }
        });
        List<File> files = new ArrayList<>();
        if (allImages != null) {
            files.addAll(Arrays.asList(allImages));
        }
        return files;

    }

    private void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                for (File child : Objects.requireNonNull(file.listFiles()))
                    deleteFile(child);
                file.delete();
            }
        }
    }
   /* private class AsyncTaskForSavingFiles extends AsyncTask<String, Void, Boolean> {


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (!aBoolean) {
                makeImagesFiles(bigOHealthFolder);
                crewListAdapter.notifyDataSetChanged();
            }
            if (count == totalCount) {
                progressBarLayout.setVisibility(View.GONE);
            }

        }

        @Override
        protected Boolean doInBackground(String... strings) {
            boolean error = false;
            URL url = null;
            try {
                url = new URL(strings[0]);
            } catch (MalformedURLException e) {
                error = true;
                Log.e(TAG, "onResponse: " + e.getMessage());
                e.printStackTrace();
            }
            InputStream is;

            File f = new File(bigOHealthFolder.getAbsolutePath() + "/" + getFileName() + ".jpg");
            try {
                is = url.openStream();

                OutputStream os = new FileOutputStream(f.getAbsolutePath());

                byte[] b = new byte[2048];
                int length;

                while ((length = is.read(b)) != -1) {
                    os.write(b, 0, length);
                }
            } catch (IOException e) {
                error = true;
                Log.e(TAG, "onResponse: " + e.getMessage());
                e.printStackTrace();
            }
            return error;
        }
    }*/
}