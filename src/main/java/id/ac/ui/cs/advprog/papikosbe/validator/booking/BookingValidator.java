package id.ac.ui.cs.advprog.papikosbe.validator.booking;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class BookingValidator {

    /**
     * TODO: Validates if a booking can be updated based on its current status
     */
    public void validateForUpdate(Booking booking) {
        // TODO: Throw exception if booking status disallows update
    }

    /**
     * TODO: Validates if a booking can be paid based on its current status
     */
    public void validateForPayment(Booking booking) {
        // TODO: Throw exception if booking status is not PENDING_PAYMENT
    }

    /**
     * TODO: Validates if a booking can be approved based on its current status
     */
    public void validateForApproval(Booking booking) {
        // TODO: Throw exception if booking status is not PAID
    }

    /**
     * TODO: Validates if a booking can be cancelled based on its current status
     */
    public void validateForCancellation(Booking booking) {
        // TODO: Throw exception if booking status disallows cancellation
    }

    /**
     * TODO: Validates basic booking fields - non-null and valid data checks
     */
    public void validateBasicFields(Booking booking) {
        // TODO: Validate bookingId, userId, kosId are not null
        // TODO: Validate checkInDate is today or future
        // TODO: Validate duration >= 1
        // TODO: Validate monthlyPrice > 0
        // TODO: Validate fullName and phoneNumber are not empty
    }
}
