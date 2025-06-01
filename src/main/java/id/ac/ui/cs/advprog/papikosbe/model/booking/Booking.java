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
    @EqualsAndHashCode.Include
    private UUID bookingId;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID kosId;

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private int duration;

    @Column(nullable = false)
    private double monthlyPrice;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    public Booking(UUID bookingId, UUID userId, UUID kosId,
                   LocalDate checkInDate, int duration, double monthlyPrice,
                   String fullName, String phoneNumber, BookingStatus status) {

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        if (checkInDate.isBefore(tomorrow)) {
            throw new IllegalArgumentException("Booking must be made at least 1 day in advance to allow owner approval time");
        }

        if (duration < 1) {
            throw new IllegalArgumentException("Duration must be at least 1 month");
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

        this.bookingId = bookingId != null ? bookingId : UUID.randomUUID();
        this.userId = userId;
        this.kosId = kosId;
        this.checkInDate = checkInDate;
        this.duration = duration;
        this.monthlyPrice = monthlyPrice;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.status = status != null ? status : BookingStatus.PENDING_PAYMENT;
    }


    public double getTotalPrice() {
        return monthlyPrice * duration;
    }
    @PreUpdate
    private void validateUpdate() {
        // No H+1 validation for updates - allows scheduler to work

        if (duration < 1) {
            throw new IllegalArgumentException("Duration must be at least 1 month");
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

    @PrePersist
    private void validateNewBooking() {
        // H+1 validation only for NEW bookings
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        if (checkInDate.isBefore(tomorrow)) {
            throw new IllegalArgumentException("Booking must be made at least 1 day in advance to allow owner approval time");
        }

        // Call the same validation as update
        validateUpdate();
    }
}