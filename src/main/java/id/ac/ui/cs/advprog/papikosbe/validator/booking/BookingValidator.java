package id.ac.ui.cs.advprog.papikosbe.validator.booking;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
public class BookingValidator {

    /**
     * Validates basic fields for booking creation or updating
     */
    public void validateBasicFields(Booking booking) {
        if (booking.getDuration() < 1) {
            throw new IllegalArgumentException("Duration must be at least 1 month");
        }

        if (booking.getCheckInDate() == null || booking.getCheckInDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
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

    /**
     * Validates if a booking can be paid
     */
    public void validateForPayment(Booking booking, UUID requesterId) {
        if (booking.getStatus() != BookingStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("Only bookings in PENDING_PAYMENT status can be paid");
        }

        if (!booking.getUserId().equals(requesterId)) {
            throw new IllegalStateException("Only the tenant who made the booking can pay for it");
        }
    }

    /**
     * Validates if a booking can be approved
     */
    public void validateForApproval(Booking booking, Kos kos, UUID requesterId) {
        if (booking.getStatus() != BookingStatus.PAID) {
            throw new IllegalStateException("Only PAID bookings can be approved");
        }

        if (!kos.getOwnerId().equals(requesterId)) {
            throw new IllegalStateException("Only the kos owner can approve this booking");
        }
    }

    /**
     * Validates if a booking can be updated
     */
    public void validateForUpdate(Booking existingBooking) {
        if (existingBooking.getStatus() != BookingStatus.PENDING_PAYMENT &&
                existingBooking.getStatus() != BookingStatus.PAID) {
            throw new IllegalStateException("Cannot edit booking after it has been approved or cancelled");
        }
    }

    /**
     * Validates if a user can access bookings for a specific owner
     */
    public void validateOwnerAccess(UUID requestedOwnerId, UUID requesterId) {
        if (!requestedOwnerId.equals(requesterId)) {
            throw new IllegalStateException("You can only view bookings for kos you own");
        }
    }
}