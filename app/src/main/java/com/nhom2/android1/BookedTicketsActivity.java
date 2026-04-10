package com.nhom2.android1;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nhom2.android1.adapter.TicketAdapter;
import com.nhom2.android1.model.Movie;
import com.nhom2.android1.model.Showtime;
import com.nhom2.android1.model.Theater;
import com.nhom2.android1.model.Ticket;
import com.nhom2.android1.model.TicketDisplayItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BookedTicketsActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private final List<Ticket> tickets = new ArrayList<>();
    private final Map<String, String> movieTitleById = new HashMap<>();
    private final Map<String, String> theaterNameById = new HashMap<>();
    private final Map<String, Showtime> showtimeById = new HashMap<>();

    private TicketAdapter ticketAdapter;
    private TextView textEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booked_tickets);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, R.string.please_login_first, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        textEmpty = findViewById(R.id.textEmptyTickets);
        RecyclerView recyclerTickets = findViewById(R.id.recyclerTickets);
        recyclerTickets.setLayoutManager(new LinearLayoutManager(this));
        ticketAdapter = new TicketAdapter();
        recyclerTickets.setAdapter(ticketAdapter);

        findViewById(R.id.buttonBackToMoviesFromTickets).setOnClickListener(v -> finish());

        loadLookupsAndTickets(user.getUid());
    }

    private void loadLookupsAndTickets(String userId) {
        firestore.collection("movies").get()
                .addOnSuccessListener(snapshot -> {
                    movieTitleById.clear();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Movie movie = doc.toObject(Movie.class);
                        if (movie != null) {
                            String id = movie.getId() == null ? doc.getId() : movie.getId();
                            movieTitleById.put(id, movie.getTitle());
                        }
                    }
                    loadTheaters(userId);
                })
                .addOnFailureListener(ex -> Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void loadTheaters(String userId) {
        firestore.collection("theaters").get()
                .addOnSuccessListener(snapshot -> {
                    theaterNameById.clear();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Theater theater = doc.toObject(Theater.class);
                        if (theater != null) {
                            String id = theater.getId() == null ? doc.getId() : theater.getId();
                            theaterNameById.put(id, theater.getName());
                        }
                    }
                    loadShowtimes(userId);
                })
                .addOnFailureListener(ex -> Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void loadShowtimes(String userId) {
        firestore.collection("showtimes").get()
                .addOnSuccessListener(snapshot -> {
                    showtimeById.clear();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Showtime showtime = doc.toObject(Showtime.class);
                        if (showtime != null) {
                            String id = showtime.getId() == null ? doc.getId() : showtime.getId();
                            showtime.setId(id);
                            showtimeById.put(id, showtime);
                        }
                    }
                    loadTickets(userId);
                })
                .addOnFailureListener(ex -> Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void loadTickets(String userId) {
        firestore.collection("tickets")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    tickets.clear();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Ticket ticket = doc.toObject(Ticket.class);
                        if (ticket != null) {
                            if (ticket.getId() == null) {
                                ticket.setId(doc.getId());
                            }
                            tickets.add(ticket);
                        }
                    }
                    renderTickets();
                })
                .addOnFailureListener(ex -> Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void renderTickets() {
        List<TicketDisplayItem> displayItems = new ArrayList<>();
        for (Ticket ticket : tickets) {
            String movieTitle = movieTitleById.containsKey(ticket.getMovieId())
                    ? movieTitleById.get(ticket.getMovieId())
                    : ticket.getMovieId();
            String theaterName = theaterNameById.containsKey(ticket.getTheaterId())
                    ? theaterNameById.get(ticket.getTheaterId())
                    : ticket.getTheaterId();

            Showtime showtime = showtimeById.get(ticket.getShowtimeId());
            String showtimeText;
            if (showtime != null) {
                showtimeText = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        .format(new Date(showtime.getStartTimeMillis()));
            } else {
                showtimeText = ticket.getShowtimeId();
            }

            String bookedAtText = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    .format(new Date(ticket.getBookedAtMillis()));

            displayItems.add(new TicketDisplayItem(
                    movieTitle,
                    theaterName,
                    showtimeText,
                    ticket.getSeatCount(),
                    bookedAtText
            ));
        }

        ticketAdapter.submitList(displayItems);
        textEmpty.setVisibility(displayItems.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
