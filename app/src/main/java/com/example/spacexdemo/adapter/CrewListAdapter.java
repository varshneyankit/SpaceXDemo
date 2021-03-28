package com.example.spacexdemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacexdemo.R;
import com.example.spacexdemo.pojos.CrewListModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CrewListAdapter extends RecyclerView.Adapter<CrewListAdapter.ViewHolder> {
    private final Context context;
    private final List<CrewListModel> crewListResult;

    public CrewListAdapter(Context context, List<CrewListModel> crewListResult) {
        this.context = context;
        this.crewListResult = crewListResult;
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
        Picasso.get().load(crewList.getImageUrl()).into(holder.crewImage);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(crewList.getWikipediaUrl()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return crewListResult.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView crewNameText, crewAgencyText, crewStatusText;
        ImageView crewImage;
        View parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView;
            crewNameText = itemView.findViewById(R.id.crew_member_name);
            crewAgencyText = itemView.findViewById(R.id.crew_member_agency);
            crewStatusText = itemView.findViewById(R.id.crew_member_status);
            crewImage = itemView.findViewById(R.id.crew_member_image);
        }
    }
}
