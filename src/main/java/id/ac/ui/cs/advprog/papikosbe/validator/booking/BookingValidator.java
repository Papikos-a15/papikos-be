package id.ac.ui.cs.advprog.papikosbe.validator.booking;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BookingValidator {

    /**
     * Validates basic fields for booking creation or updating
     */
    public void validateBasicFields(Booking booking) {
        // TODO: Implement validation logic for basic fields
    }

    /**
     * Validates if a booking can be paid
     */
    public void validateForPayment(Booking booking, UUID requesterId) {
        // TODO: Implement validation logic for payment
    }

    /**
     * Validates if a booking can be approved
     */
    public void validateForApproval(Booking booking, Kos kos, UUID requesterId) {
        // TODO: Implement validation logic for approval
    }

    /**
     * Validates if a booking can be updated
     */
    public void validateForUpdate(Booking existingBooking) {
        // TODO: Implement validation logic for update
    }

    /**
     * Validates if a user can access bookings for a specific owner
     */
    public void validateOwnerAccess(UUID requestedOwnerId, UUID requesterId) {
        // TODO: Implement validation logic for owner access
    }
}