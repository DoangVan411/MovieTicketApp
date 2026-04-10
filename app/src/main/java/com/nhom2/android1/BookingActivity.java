package com.nhom2.android1;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nhom2.android1.model.Showtime;
import com.nhom2.android1.model.Theater;
import com.nhom2.android1.model.Ticket;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity {

    private final List<Theater> theaters = new ArrayList<>();
    private final List<Showtime> movieShowtimes = new ArrayList<>();

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private String movieId;
    private String movieTitle;

    private Spinner spinnerShowtimes;
    private EditText editSeatCount;
    private ArrayAdapter<String> showtimeAdapter;

    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    Toast.makeText(this, R.string.notification_permission_denied, Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, R.string.please_login_first, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        movieId = getIntent().getStringExtra(AppConstants.EXTRA_MOVIE_ID);
        movieTitle = getIntent().getStringExtra(AppConstants.EXTRA_MOVIE_TITLE);
        if (TextUtils.isEmpty(movieId) || TextUtils.isEmpty(movieTitle)) {
            Toast.makeText(this, R.string.select_movie_first, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView textSelectedMovie = findViewById(R.id.textSelectedMovie);
        textSelectedMovie.setText(getString(R.string.selected_movie_text, movieTitle));

        spinnerShowtimes = findViewById(R.id.spinnerShowtimes);
        editSeatCount = findViewById(R.id.editSeatCount);

        showtimeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        showtimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerShowtimes.setAdapter(showtimeAdapter);

        askNotificationPermissionIfNeeded();

        findViewById(R.id.buttonBookTicket).setOnClickListener(v -> bookTicket());
        findViewById(R.id.buttonBackToMovies).setOnClickListener(v -> finish());

        loadTheaters();
        loadShowtimesByMovie(movieId);
    }

    private void loadTheaters() {
        firestore.collection("theaters").get().addOnSuccessListener(snapshot -> {
            theaters.clear();
            for (QueryDocumentSnapshot doc : snapshot) {
                Theater theater = doc.toObject(Theater.class);
                if (theater != null) {
                    if (theater.getId() == null) {
                        theater.setId(doc.getId());
                    }
                    theaters.add(theater);
                }
            }
        });
    }

    private void loadShowtimesByMovie(String movieIdValue) {
        firestore.collection("showtimes")
                .whereEqualTo("movieId", movieIdValue)
                .get()
                .addOnSuccessListener(snapshot -> {
                    movieShowtimes.clear();
                    List<String> labels = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Showtime showtime = doc.toObject(Showtime.class);
                        if (showtime != null) {
                            if (showtime.getId() == null) {
                                showtime.setId(doc.getId());
                            }
                            movieShowtimes.add(showtime);
                            labels.add(formatShowtime(showtime));
                        }
                    }
                    showtimeAdapter.clear();
                    showtimeAdapter.addAll(labels);
                    showtimeAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(ex -> Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show());
    }

    private String formatShowtime(Showtime showtime) {
        String theaterName = findTheaterName(showtime.getTheaterId());
        String time = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
                .format(new Date(showtime.getStartTimeMillis()));
        return theaterName + " - " + time;
    }

    private String findTheaterName(String theaterId) {
        for (Theater theater : theaters) {
            if (theaterId.equals(theater.getId())) {
                return theater.getName();
            }
        }
        return theaterId;
    }

    private void bookTicket() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, R.string.please_login_first, Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedPosition = spinnerShowtimes.getSelectedItemPosition();
        if (selectedPosition < 0 || selectedPosition >= movieShowtimes.size()) {
            Toast.makeText(this, R.string.select_showtime_first, Toast.LENGTH_SHORT).show();
            return;
        }

        String seatsInput = editSeatCount.getText().toString().trim();
        if (TextUtils.isEmpty(seatsInput)) {
            Toast.makeText(this, R.string.enter_seat_count, Toast.LENGTH_SHORT).show();
            return;
        }

        int seatCount;
        try {
            seatCount = Integer.parseInt(seatsInput);
            if (seatCount <= 0) {
                Toast.makeText(this, R.string.invalid_seat_count, Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException ex) {
            Toast.makeText(this, R.string.invalid_seat_count, Toast.LENGTH_SHORT).show();
            return;
        }

        Showtime selectedShowtime = movieShowtimes.get(selectedPosition);
        String ticketId = firestore.collection("tickets").document().getId();

        Ticket ticket = new Ticket(
                ticketId,
                user.getUid(),
                movieId,
                selectedShowtime.getTheaterId(),
                selectedShowtime.getId(),
                seatCount,
                System.currentTimeMillis()
        );

        firestore.collection("tickets")
                .document(ticketId)
                .set(ticket)
                .addOnSuccessListener(unused -> {
                    firestore.collection("users").document(user.getUid())
                            .update("ticketIds", FieldValue.arrayUnion(ticketId));
                    scheduleReminder(ticketId, movieTitle, selectedShowtime);
                    Toast.makeText(this, R.string.book_ticket_success, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(ex -> Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void scheduleReminder(String ticketId, String movieTitleValue, Showtime showtime) {
        long remindAtMillis = showtime.getStartTimeMillis() - 30 * 60 * 1000L;
        if (remindAtMillis <= System.currentTimeMillis()) {
            return;
        }

        Intent reminderIntent = new Intent(this, ShowtimeReminderReceiver.class);
        reminderIntent.putExtra(AppConstants.EXTRA_MOVIE_TITLE, movieTitleValue);
        reminderIntent.putExtra(AppConstants.EXTRA_THEATER_NAME, findTheaterName(showtime.getTheaterId()));
        reminderIntent.putExtra(AppConstants.EXTRA_SHOWTIME_MILLIS, showtime.getStartTimeMillis());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                ticketId.hashCode(),
                reminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, remindAtMillis, pendingIntent);
        }
    }

    private void askNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
    }
}
