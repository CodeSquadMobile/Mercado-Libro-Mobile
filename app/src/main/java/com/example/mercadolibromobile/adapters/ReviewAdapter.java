package com.example.mercadolibromobile.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mercadolibromobile.R;
import com.example.mercadolibromobile.models.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> reviewList;

    public ReviewAdapter(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.textViewLibro.setText(review.getLibro());
        holder.textViewUsuario.setText(review.getUsuario());
        holder.textViewComentario.setText(review.getComentario());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewLibro;
        public TextView textViewUsuario;
        public TextView textViewComentario;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            textViewLibro = itemView.findViewById(R.id.textViewLibro);
            textViewUsuario = itemView.findViewById(R.id.textViewUsuario);
            textViewComentario = itemView.findViewById(R.id.textViewComentario);
        }
    }
}
