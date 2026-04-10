package com.nhom2.android1.model;

public class Ticket {
    private String id;
    private String userId;
    private String movieId;
    private String theaterId;
    private String showtimeId;
    private int seatCount;
    private long bookedAtMillis;

    public Ticket() {
        // Needed for Firestore.
    }

    public Ticket(String id, String userId, String movieId, String theaterId, String showtimeId,
                  int seatCount, long bookedAtMillis) {
        this.id = id;
        this.userId = userId;
        this.movieId = movieId;
        this.theaterId = theaterId;
        this.showtimeId = showtimeId;
        this.seatCount = seatCount;
        this.bookedAtMillis = bookedAtMillis;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(String showtimeId) {
        this.showtimeId = showtimeId;
    }

    public int getSeatCount() {
        return seatCount;
    }

    public void setSeatCount(int seatCount) {
        this.seatCount = seatCount;
    }

    public long getBookedAtMillis() {
        return bookedAtMillis;
    }

    public void setBookedAtMillis(long bookedAtMillis) {
        this.bookedAtMillis = bookedAtMillis;
    }
}
