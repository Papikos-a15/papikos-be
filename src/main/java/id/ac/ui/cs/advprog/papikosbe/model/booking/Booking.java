package id.ac.ui.cs.advprog.papikosbe.model.booking;

import jakarta.persistence.*;
import lombok.*;
import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "bookings")
@Getter @Setter
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)  // <— tambahkan ini
public class Booking {
    @Id
    @Column(name = "booking_id", nullable = false, updatable = false)
    @EqualsAndHashCode.Include                         // <— dan ini
    private UUID bookingId;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "kos_id", nullable = false, updatable = false)
    private UUID kosId;

    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private int duration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    public Booking(UUID bookingId, UUID userId, UUID kosId,
                   LocalDate checkInDate, int duration, BookingStatus status) {
        if (duration < 1) {
            throw new IllegalArgumentException("Duration must be at least 1 month");
        }
        if (checkInDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }
        this.bookingId   = bookingId;
        this.userId      = userId;
        this.kosId       = kosId;
        this.checkInDate = checkInDate;
        this.duration    = duration;
        this.status      = status;
    }

    public double calculateTotalPrice(double monthlyPrice) {
        return monthlyPrice * duration;
    }

    @PrePersist @PreUpdate
    private void validate() {
        if (duration < 1) {
            throw new IllegalArgumentException("Duration must be at least 1 month");
        }
        if (checkInDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }
    }
}
