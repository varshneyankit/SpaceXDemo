package com.example.spacexdemo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.TypeConverter;

import com.example.spacexdemo.R;
import com.example.spacexdemo.pojos.CrewListModel;
import com.example.spacexdemo.ui.main.MainViewModel;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class CrewListAdapter extends RecyclerView.Adapter<CrewListAdapter.ViewHolder> {
    private final Context context;
    private final List<CrewListModel> crewListResult;
    private final MainViewModel viewModel;

    public CrewListAdapter(Context context, List<CrewListModel> crewListResult, MainViewModel viewModel) {
        this.context = context;
        this.crewListResult = crewListResult;
        this.viewModel = viewModel;
    }

    @TypeConverter
    public static Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;

        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    @TypeConverter
    public static String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.crew_list_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CrewListModel crewList = crewListResult.get(position);
        holder.crewNameText.setText(crewList.getName());
        holder.crewAgencyText.setText(crewList.getAgency());
        holder.crewStatusText.setText(crewList.getStatus());
        if (crewList.getImageUrl().contains("http")) {
            Picasso.get().load(crewList.getImageUrl()).into(holder.crewImage);
            saveOfflineImage(crewList.getImageUrl(), crewList);
        } else
            holder.crewImage.setImageBitmap(StringToBitMap(crewList.getImageUrl()));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(crewList.getWikipediaUrl()));
            context.startActivity(intent);
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void saveOfflineImage(String imageUrl, CrewListModel crewList) {
        AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(String... strings) {

                //Your code goes here
                try {
                    InputStream inputStream = new URL(strings[0]).openStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    String s = BitMapToString(bitmap);
                    crewList.setImageUrl(s);
                    viewModel.setSpecficRow(crewList);
                    //response.body().setImageUrl();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        };
        task.execute(imageUrl);
    }

    @Override
    public int getItemCount() {
        return crewListResult.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView crewNameText, crewAgencyText, crewStatusText, crewWikiText;
        ImageView crewImage;
        View parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView;
            crewNameText = itemView.findViewById(R.id.crew_member_name);
            crewAgencyText = itemView.findViewById(R.id.crew_member_agency);
            crewStatusText = itemView.findViewById(R.id.crew_member_status);
            //crewWikiText = itemView.findViewById(R.id.crew_member_wiki);
            crewImage = itemView.findViewById(R.id.crew_member_image);
        }
    }
}
