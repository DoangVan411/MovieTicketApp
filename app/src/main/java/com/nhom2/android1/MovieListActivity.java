package com.nhom2.android1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nhom2.android1.adapter.MovieAdapter;
import com.nhom2.android1.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieListActivity extends AppCompatActivity {

    private final List<Movie> movies = new ArrayList<>();

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private MovieAdapter movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, R.string.please_login_first, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupMovieList();
        findViewById(R.id.buttonLogoutFromMovies).setOnClickListener(v -> logout());
        findViewById(R.id.buttonBookedTickets).setOnClickListener(v -> openBookedTickets());
        loadMovies();
    }

    private void setupMovieList() {
        RecyclerView recyclerMovies = findViewById(R.id.recyclerMovies);
        recyclerMovies.setLayoutManager(new LinearLayoutManager(this));
        movieAdapter = new MovieAdapter(this::openBooking);
        recyclerMovies.setAdapter(movieAdapter);
    }

    private void loadMovies() {
        firestore.collection("movies").get().addOnSuccessListener(snapshot -> {
            movies.clear();
            for (QueryDocumentSnapshot doc : snapshot) {
                Movie movie = doc.toObject(Movie.class);
                if (movie != null) {
                    if (movie.getId() == null) {
                        movie.setId(doc.getId());
                    }
                    movies.add(movie);
                }
            }
            movieAdapter.submitList(movies);
        }).addOnFailureListener(ex -> Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void openBooking(Movie movie) {
        Intent intent = new Intent(this, BookingActivity.class);
        intent.putExtra(AppConstants.EXTRA_MOVIE_ID, movie.getId());
        intent.putExtra(AppConstants.EXTRA_MOVIE_TITLE, movie.getTitle());
        startActivity(intent);
    }

    private void openBookedTickets() {
        startActivity(new Intent(this, BookedTicketsActivity.class));
    }

    private void logout() {
        auth.signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
