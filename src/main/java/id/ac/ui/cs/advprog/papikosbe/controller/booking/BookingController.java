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

//    @PatchMapping("/{id}/status")
//    public ResponseEntity<Booking> updateBookingStatus(
//            @PathVariable UUID id,
//            @RequestParam BookingStatus status) {
//        try {
//            bookingService.updateBookingStatus(id, status);
//            return bookingService.findBookingById(id)
//                    .map(ResponseEntity::ok)
//                    .orElse(ResponseEntity.notFound().build());
//        } catch (EntityNotFoundException e) {
//            return ResponseEntity.notFound().build();
//        } catch (IllegalStateException e) {
//            return ResponseEntity.status(403).build(); // Forbidden status transition
//        }
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable UUID id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }
}