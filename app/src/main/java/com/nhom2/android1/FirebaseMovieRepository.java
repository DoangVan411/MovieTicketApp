package com.nhom2.android1;

import android.content.Context;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.nhom2.android1.model.Movie;
import com.nhom2.android1.model.Showtime;
import com.nhom2.android1.model.Theater;

import java.util.ArrayList;
import java.util.List;

public final class FirebaseMovieRepository {

    private FirebaseMovieRepository() {
    }

    public static void seedSampleData(FirebaseFirestore firestore) {
        List<Movie> demoMovies = buildMovies();
        List<Theater> demoTheaters = buildTheaters();
        List<Showtime> demoShowtimes = buildShowtimes(System.currentTimeMillis());

        for (Movie movie : demoMovies) {
            firestore.collection("movies")
                    .document(movie.getId())
                    .set(movie, SetOptions.merge());
        }
        for (Theater theater : demoTheaters) {
            firestore.collection("theaters")
                    .document(theater.getId())
                    .set(theater, SetOptions.merge());
        }
        for (Showtime showtime : demoShowtimes) {
            firestore.collection("showtimes")
                    .document(showtime.getId())
                    .set(showtime, SetOptions.merge());
        }
    }

    private static List<Movie> buildMovies() {
        List<Movie> movies = new ArrayList<>();
        movies.add(new Movie("m1", "Avengers: Endgame", "Action", 181));
        movies.add(new Movie("m2", "Interstellar", "Sci-Fi", 169));
        movies.add(new Movie("m3", "Spirited Away", "Animation", 125));
        movies.add(new Movie("m4", "Inception", "Thriller", 148));
        movies.add(new Movie("m5", "The Batman", "Action", 176));
        movies.add(new Movie("m6", "Dune: Part Two", "Sci-Fi", 166));
        movies.add(new Movie("m7", "Coco", "Animation", 105));
        movies.add(new Movie("m8", "Parasite", "Drama", 132));
        movies.add(new Movie("m9", "Your Name", "Romance", 107));
        movies.add(new Movie("m10", "Top Gun: Maverick", "Action", 131));
        return movies;
    }

    private static List<Theater> buildTheaters() {
        List<Theater> theaters = new ArrayList<>();
        theaters.add(new Theater("t1", "Galaxy Nguyen Du", "District 1"));
        theaters.add(new Theater("t2", "CGV Landmark", "Binh Thanh"));
        theaters.add(new Theater("t3", "Lotte Nowzone", "District 1"));
        theaters.add(new Theater("t4", "BHD Bitexco", "District 1"));
        return theaters;
    }

    private static List<Showtime> buildShowtimes(long now) {
        List<Showtime> showtimes = new ArrayList<>();
        showtimes.add(new Showtime("s1", "m1", "t1", now + 2 * 60 * 60 * 1000L));
        showtimes.add(new Showtime("s2", "m1", "t2", now + 5 * 60 * 60 * 1000L));
        showtimes.add(new Showtime("s3", "m2", "t1", now + 3 * 60 * 60 * 1000L));
        showtimes.add(new Showtime("s4", "m3", "t2", now + 6 * 60 * 60 * 1000L));
        showtimes.add(new Showtime("s5", "m4", "t3", now + 4 * 60 * 60 * 1000L));
        showtimes.add(new Showtime("s6", "m5", "t4", now + 7 * 60 * 60 * 1000L));
        showtimes.add(new Showtime("s7", "m6", "t2", now + 8 * 60 * 60 * 1000L));
        showtimes.add(new Showtime("s8", "m7", "t1", now + 9 * 60 * 60 * 1000L));
        showtimes.add(new Showtime("s9", "m8", "t3", now + 10 * 60 * 60 * 1000L));
        showtimes.add(new Showtime("s10", "m9", "t4", now + 11 * 60 * 60 * 1000L));
        showtimes.add(new Showtime("s11", "m10", "t2", now + 12 * 60 * 60 * 1000L));
        return showtimes;
    }

    public static void ensureNotificationChannel(Context context) {
        NotificationUtil.ensureChannel(context);
    }
}
