package id.ac.ui.cs.advprog.papikosbe.validator.booking;

import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
public class BookingAccessValidator {


    public void validateUserAccess(UUID requesterId, UUID bookingUserId) {
        if (!requesterId.equals(bookingUserId)) {
            throw new IllegalStateException("Only the tenant who made the booking can perform this action");
        }
    }
    public void validateOwnerAccess(UUID ownerId, UUID requesterId) {
        if (!requesterId.equals(ownerId)) {
            throw new IllegalStateException("Only the kos owner can perform this action");
        }
    }

    public void validateForPayment(Booking booking, UUID requesterId) {
        // Check requester is the booking user
        validateUserAccess(requesterId, booking.getUserId());
    }

    public void validateForApproval(Booking booking, Kos kos, UUID requesterId) {
        // Check requester is the kos owner
        validateOwnerAccess(kos.getOwnerId(), requesterId);
    }
}