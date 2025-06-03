
package com.example.ebookreader.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ebookreader.R;
import java.util.List;

public class FavoriteJokeAdapter extends RecyclerView.Adapter<FavoriteJokeAdapter.FavoriteJokeViewHolder> {

    private List<String> favoriteJokesList;
    private OnFavoriteInteractionListener listener;

    public interface OnFavoriteInteractionListener {
        void onUnfavoriteClicked(int position, String joke);
        void onShareFavoriteClicked(String joke);
    }

    public FavoriteJokeAdapter(List<String> favoriteJokesList, OnFavoriteInteractionListener listener) {
        this.favoriteJokesList = favoriteJokesList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavoriteJokeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_favorite_joke, parent, false);
        return new FavoriteJokeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteJokeViewHolder holder, int position) {
        String joke = favoriteJokesList.get(position);
        holder.textViewFavoriteJokeText.setText(joke);

        holder.buttonUnfavoriteJoke.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUnfavoriteClicked(position, joke);
            }
        });
        holder.buttonShareFavoriteJoke.setOnClickListener(v -> {
            if (listener != null) {
                listener.onShareFavoriteClicked(joke);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoriteJokesList == null ? 0 : favoriteJokesList.size();
    }

    public void updateJokes(List<String> newJokes) {
        this.favoriteJokesList = newJokes;
        notifyDataSetChanged();
    }

    static class FavoriteJokeViewHolder extends RecyclerView.ViewHolder {
        TextView textViewFavoriteJokeText;
        ImageButton buttonUnfavoriteJoke;
        ImageButton buttonShareFavoriteJoke;

        FavoriteJokeViewHolder(View view) {
            super(view);
            textViewFavoriteJokeText = view.findViewById(R.id.textViewFavoriteJokeText);
            buttonUnfavoriteJoke = view.findViewById(R.id.buttonUnfavoriteJoke);
            buttonShareFavoriteJoke = view.findViewById(R.id.buttonShareFavoriteJoke);
        }
    }
}
