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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Booking {
    @Id
    @Column(name = "booking_id", nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private UUID bookingId;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "kos_id", nullable = false, updatable = false)
    private UUID kosId;

    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private int duration;

    @Column(name = "monthly_price", nullable = false)
    private double monthlyPrice;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    // Original constructor kept for backward compatibility in tests
    // Will be removed in the GREEN phase
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

        // Default values for new fields (skeleton only)
        this.monthlyPrice = 0;
        this.fullName = "";
        this.phoneNumber = "";
    }

    // New constructor with additional parameters
    public Booking(UUID bookingId, UUID userId, UUID kosId,
                   LocalDate checkInDate, int duration, double monthlyPrice,
                   String fullName, String phoneNumber, BookingStatus status) {
        // Skeleton implementation - will be filled in GREEN phase
        this(bookingId, userId, kosId, checkInDate, duration, status);
        this.monthlyPrice = monthlyPrice;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    // Original method kept for backward compatibility
    // Will be deprecated in the GREEN phase
    public double calculateTotalPrice(double monthlyPrice) {
        return monthlyPrice * duration;
    }

    // New method for total price calculation using stored monthly price
    public double getTotalPrice() {
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
        // Validation for new fields will be added in GREEN phase
    }
}