package com.nhom2.android1.model;

public class TicketDisplayItem {
    private final String movieTitle;
    private final String theaterName;
    private final String showtimeText;
    private final int seatCount;
    private final String bookedAtText;

    public TicketDisplayItem(String movieTitle, String theaterName, String showtimeText,
                             int seatCount, String bookedAtText) {
        this.movieTitle = movieTitle;
        this.theaterName = theaterName;
        this.showtimeText = showtimeText;
        this.seatCount = seatCount;
        this.bookedAtText = bookedAtText;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public String getTheaterName() {
        return theaterName;
    }

    public String getShowtimeText() {
        return showtimeText;
    }

    public int getSeatCount() {
        return seatCount;
    }

    public String getBookedAtText() {
        return bookedAtText;
    }
}
