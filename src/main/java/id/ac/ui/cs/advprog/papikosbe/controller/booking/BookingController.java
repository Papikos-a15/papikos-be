package id.ac.ui.cs.advprog.papikosbe.controller.booking;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.service.booking.BookingService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking) {
        Booking created = bookingService.createBooking(booking);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> list = bookingService.findAllBookings();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable UUID id) {
        Optional<Booking> b = bookingService.findBookingById(id);
        return b.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(@PathVariable UUID id, @RequestBody Booking booking) {
        // Ensure the path ID matches the booking ID
        if (!id.equals(booking.getBookingId())) {
            return ResponseEntity.badRequest().build();
        }

        try {
            // Try to update the booking (this will fail if already approved/paid)
            bookingService.updateBooking(booking);
            return ResponseEntity.ok(booking);
        } catch (IllegalStateException e) {
            // This happens when trying to edit an already approved booking
            return ResponseEntity.status(403).build(); // Forbidden
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<Booking> payBooking(@PathVariable UUID id) {
        try {
            // Call service to pay the booking
//            bookingService.payBooking(id);

            // Fetch the updated booking to return in response
            return bookingService.findBookingById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (EntityNotFoundException e) {
            // Booking not found
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            // Invalid status transition (e.g., trying to pay an already paid booking)
            return ResponseEntity.status(403).build(); // Forbidden
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Booking> approveBooking(@PathVariable UUID id) {
        try {
            // Call service to approve the booking
//            bookingService.approveBooking(id);

            // Fetch the updated booking to return in response
            return bookingService.findBookingById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (EntityNotFoundException e) {
            // Booking not found
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            // Invalid status transition (e.g., trying to approve unpaid booking)
            return ResponseEntity.status(403).build(); // Forbidden
        }
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Booking>> getBookingsByOwnerId(@PathVariable UUID ownerId) {
        return ResponseEntity.status(403).build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable UUID id) {
        try {
            // Call service to cancel the booking
            bookingService.cancelBooking(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            // Booking not found
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            // If cancellation is restricted under certain conditions
            return ResponseEntity.status(403).build(); // Forbidden
        }
    }
}