package com.csharpui.myloginform;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter1 extends RecyclerView.Adapter<MyAdapter1.ViewHolder> {
    private List<PracticeDetails> data;


    public MyAdapter1(List<PracticeDetails> data) {
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemlayout1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PracticeDetails item = data.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView Days, Timings, Fees;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.PlacNameTextView);
            Days = itemView.findViewById(R.id.DaysTextView);
            Timings = itemView.findViewById(R.id.TimingsTextView);
            Fees = itemView.findViewById(R.id.FeesTextView);

        }

        public void bind(PracticeDetails item) {
            // Load image using a library like Picasso, Glide, or Coil
            nameTextView.setText(item.getPracticeName());
            String days = Days.getText() + item.getPracticeDays();
            Days.setText(days);
            String timings = Timings.getText() + item.getTimings();
            Timings.setText(timings);
            String fees = item.getPracticeFees();
            Fees.setText("Rs. "+fees);

        }
    }
}
