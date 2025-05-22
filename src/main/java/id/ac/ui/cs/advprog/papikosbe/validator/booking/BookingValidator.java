package id.ac.ui.cs.advprog.papikosbe.validator.booking;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class BookingValidator {

    /**
     * Validates if a booking can be updated based on its current state
     */
    public void validateForUpdate(Booking booking) {
        if (booking.getStatus() == BookingStatus.APPROVED || booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot edit booking after it has been approved or cancelled");
        }
    }

    /**
     * Validates if a booking can be paid based on its current state
     */
    public void validateForPayment(Booking booking) {
        if (booking.getStatus() != BookingStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("Only bookings in PENDING_PAYMENT status can be paid");
        }
    }

    /**
     * Validates if a booking can be approved based on its current state
     */
    public void validateForApproval(Booking booking) {
        if (booking.getStatus() != BookingStatus.PAID) {
            throw new IllegalStateException("Only PAID bookings can be approved");
        }
    }

    /**
     * Validates if a booking can be cancelled based on its current state
     */
    public void validateForCancellation(Booking booking) {
        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new IllegalStateException("Cannot cancel an already approved booking");
        }
    }

    /**
     * Validates basic booking fields - general data validation regardless of state
     */
    public void validateBasicFields(Booking booking) {
        if (booking.getBookingId() == null) {
            throw new IllegalArgumentException("Booking ID cannot be null");
        }

        if (booking.getUserId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        if (booking.getKosId() == null) {
            throw new IllegalArgumentException("Kos ID cannot be null");
        }

        if (booking.getCheckInDate() == null || booking.getCheckInDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date must be today or in the future");
        }

        if (booking.getDuration() < 1) {
            throw new IllegalArgumentException("Duration must be at least 1 month");
        }

        if (booking.getMonthlyPrice() <= 0) {
            throw new IllegalArgumentException("Monthly price must be greater than 0");
        }

        if (booking.getFullName() == null || booking.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty");
        }

        if (booking.getPhoneNumber() == null || booking.getPhoneNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }
    }
}