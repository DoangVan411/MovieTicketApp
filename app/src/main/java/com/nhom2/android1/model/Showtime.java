package com.nhom2.android1.model;

public class Showtime {
    private String id;
    private String movieId;
    private String theaterId;
    private long startTimeMillis;

    public Showtime() {
        // Needed for Firestore.
    }

    public Showtime(String id, String movieId, String theaterId, long startTimeMillis) {
        this.id = id;
        this.movieId = movieId;
        this.theaterId = theaterId;
        this.startTimeMillis = startTimeMillis;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getTheaterId() {
        return theaterId;
    }

    public void setTheaterId(String theaterId) {
        this.theaterId = theaterId;
    }

    public long getStartTimeMillis() {
        return startTimeMillis;
    }

    public void setStartTimeMillis(long startTimeMillis) {
        this.startTimeMillis = startTimeMillis;
    }

    @Override
    public String toString() {
        return id;
    }
}
