package id.ac.ui.cs.advprog.papikosbe.validator.booking;

import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BookingAccessValidator {

    /**
     * TODO: Validates that the requester is the booking's user
     */
    public void validateUserAccess(UUID requesterId, UUID bookingUserId) {
        // TODO: Implement user access validation logic
    }

    /**
     * TODO: Validates that the requester is the kos owner
     */
    public void validateOwnerAccess(UUID ownerId, UUID requesterId) {
        // TODO: Implement owner access validation logic
    }

    /**
     * TODO: Validates the entire payment flow access rights
     */
    public void validateForPayment(Booking booking, UUID requesterId) {
        // TODO: Use validateUserAccess to ensure requester is booking user
    }

    /**
     * TODO: Validates the entire approval flow access rights
     */
    public void validateForApproval(Booking booking, Kos kos, UUID requesterId) {
        // TODO: Use validateOwnerAccess to ensure requester is kos owner
    }
}
