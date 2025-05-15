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

    /**
     * @deprecated Use the new constructor with full details instead.
     * This constructor is kept for backward compatibility and will be removed in future updates.
     */
    @Deprecated
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

        // Default values for new fields
        this.monthlyPrice = 0;
        this.fullName = "";
        this.phoneNumber = "";
    }

    /**
     * Full constructor with all required booking details including personal information
     * and pricing details.
     */
    public Booking(UUID bookingId, UUID userId, UUID kosId,
                   LocalDate checkInDate, int duration, double monthlyPrice,
                   String fullName, String phoneNumber, BookingStatus status) {
        // Validate duration
        if (duration < 1) {
            throw new IllegalArgumentException("Duration must be at least 1 month");
        }

        // Validate check-in date
        if (checkInDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }

        // Validate monthly price
        if (monthlyPrice <= 0) {
            throw new IllegalArgumentException("Monthly price must be greater than 0");
        }

        // Validate full name
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty");
        }

        // Validate phone number
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }

        // Set all fields
        this.bookingId    = bookingId;
        this.userId       = userId;
        this.kosId        = kosId;
        this.checkInDate  = checkInDate;
        this.duration     = duration;
        this.monthlyPrice = monthlyPrice;
        this.fullName     = fullName;
        this.phoneNumber  = phoneNumber;
        this.status       = status;
    }

    /**
     * @deprecated Use getTotalPrice() instead.
     * This method is kept for backward compatibility and will be removed in future updates.
     */
    @Deprecated
    public double calculateTotalPrice(double monthlyPrice) {
        return monthlyPrice * duration;
    }

    /**
     * Calculate the total price based on the monthly price and duration.
     * @return Total price for the entire booking period
     */
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
        if (monthlyPrice <= 0) {
            throw new IllegalArgumentException("Monthly price must be greater than 0");
        }
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty");
        }
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }
    }
}