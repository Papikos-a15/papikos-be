package id.ac.ui.cs.advprog.papikosbe.model;

import java.time.LocalDate;
import java.util.UUID;
import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;

public class Booking {
    private final UUID bookingId;
    private final UUID userId;
    private final UUID kosId; // Hanya referensi ke model Kos
    private final LocalDate checkInDate;
    private final int duration;
    private BookingStatus status;

    // Constructor dengan validasi
    public Booking(UUID bookingId, UUID userId, UUID kosId, LocalDate checkInDate, int duration, BookingStatus status) {
        if (duration < 1) {
            throw new IllegalArgumentException("Duration must be at least 1 month");
        }
        if (checkInDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }
        this.bookingId = bookingId;
        this.userId = userId;
        this.kosId = kosId;
        this.checkInDate = checkInDate;
        this.duration = duration;
        this.status = status;
    }

    // Getter untuk semua field
    public UUID getBookingId() {
        return bookingId;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getKosId() {
        return kosId;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public int getDuration() {
        return duration;
    }

    public BookingStatus getStatus() {
        return status;
    }

    // Setter untuk memfasilitasi perubahan status
    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    // Menghitung total harga sewa berdasarkan harga per bulan dan durasi sewa
    public double calculateTotalPrice(double monthlyPrice) {
        return monthlyPrice * duration;
    }
}
