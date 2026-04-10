package com.nhom2.android1.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom2.android1.R;
import com.nhom2.android1.model.TicketDisplayItem;

import java.util.ArrayList;
import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private final List<TicketDisplayItem> items = new ArrayList<>();

    public void submitList(List<TicketDisplayItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        TicketDisplayItem item = items.get(position);
        holder.textMovieTitle.setText(item.getMovieTitle());
        holder.textTheater.setText(item.getTheaterName());
        holder.textShowtime.setText(item.getShowtimeText());
        holder.textSeats.setText(holder.itemView.getContext().getString(R.string.seat_count_text, item.getSeatCount()));
        holder.textBookedAt.setText(holder.itemView.getContext().getString(R.string.booked_at_text, item.getBookedAtText()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView textMovieTitle;
        TextView textTheater;
        TextView textShowtime;
        TextView textSeats;
        TextView textBookedAt;

        TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            textMovieTitle = itemView.findViewById(R.id.textTicketMovieTitle);
            textTheater = itemView.findViewById(R.id.textTicketTheater);
            textShowtime = itemView.findViewById(R.id.textTicketShowtime);
            textSeats = itemView.findViewById(R.id.textTicketSeats);
            textBookedAt = itemView.findViewById(R.id.textTicketBookedAt);
        }
    }
}
