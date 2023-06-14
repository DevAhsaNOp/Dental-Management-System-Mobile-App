package com.csharpui.myloginform;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
    private List<Doctor> data;
    private static OnItemClickListener listener;

    public void setData(List<Doctor> filteredList) {
        this.data = filteredList;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public MyAdapter(List<Doctor> data, OnItemClickListener listener) {
        this.data = data;
        this.listener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemlayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Doctor item = data.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView nameTextView;
        private TextView SpecializtionTextView, AboutMe, YearOfExperience;
        private Button button;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            SpecializtionTextView = itemView.findViewById(R.id.SpecializtionTextView);
            AboutMe = itemView.findViewById(R.id.AboutMeTextView);
            YearOfExperience = itemView.findViewById(R.id.YearOfExperienceTextView);
            button = itemView.findViewById(R.id.ViewDoctor);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }

        public void bind(Doctor item, OnItemClickListener listener) {
            // Load image using a library like Picasso, Glide, or Coil
            String imageurl = item.getImageUrl();
            Picasso.get().load(imageurl).into(imageView);

            nameTextView.setText(item.getName());
            int desiredLength = 100;
            String aboutme = item.getAboutme();
            aboutme = aboutme.substring(0, Math.min(aboutme.length(), desiredLength));
            aboutme = aboutme.concat("...");
            AboutMe.setText(aboutme);
            SpecializtionTextView.setText(item.getSpecializtion());
            YearOfExperience.setText("Year of Experience: " + item.getYearOfExperience());
        }
    }
}
