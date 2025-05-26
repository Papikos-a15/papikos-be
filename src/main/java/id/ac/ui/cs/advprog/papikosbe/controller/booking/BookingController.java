package id.ac.ui.cs.advprog.papikosbe.controller.booking;

import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import id.ac.ui.cs.advprog.papikosbe.service.booking.BookingService;
import id.ac.ui.cs.advprog.papikosbe.service.kos.KosService;
import id.ac.ui.cs.advprog.papikosbe.util.AuthenticationUtils;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.BookingValidator;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.BookingAccessValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j  // Add this annotation
@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final AuthenticationUtils authUtils;
    private final BookingService bookingService;
    private final KosService kosService;
    private final BookingAccessValidator bookingAccessValidator;

    @Autowired
    public BookingController(
            AuthenticationUtils authUtils,
            BookingService bookingService,
            KosService kosService,
            BookingAccessValidator bookingAccessValidator
    ){
        this.authUtils = authUtils;
        this.bookingService = bookingService;
        this.kosService = kosService;
        this.bookingAccessValidator = bookingAccessValidator;
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking, Authentication authentication) {
        try {
            UUID requesterId = authUtils.getUserIdFromAuth(authentication);
            if (booking.getUserId() != null && !booking.getUserId().equals(requesterId)) {
                return ResponseEntity.status(403).build(); // Cannot create booking for someone else
            }

            if (booking.getUserId() == null) {
                booking.setUserId(requesterId);
            }

            Booking createdBooking = bookingService.createBooking(booking);
            return ResponseEntity.ok(createdBooking);
        } catch (IllegalArgumentException e) {
            System.out.println("Validation error: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings(Authentication authentication) {
        try {
            UUID userId = authUtils.getUserIdFromAuth(authentication);

            List<Booking> bookings = bookingService.findBookingsByUserId(userId).join();
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            log.error("Error getting bookings for user: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable UUID id, Authentication authentication) {
        try {
            Booking booking = bookingService.findBookingById(id).join()
                    .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

            UUID requesterId = authUtils.getUserIdFromAuth(authentication);

            try {
                bookingAccessValidator.validateUserAccess(requesterId, booking.getUserId());
                // User is the booking owner, allow access
            } catch (IllegalStateException e) {
                // Not the booking user, check if kos owner
                Kos kos = kosService.getKosById(booking.getKosId())
                        .orElseThrow(() -> new EntityNotFoundException("Kos not found"));

                try {
                    bookingAccessValidator.validateOwnerAccess(kos.getOwnerId(), requesterId);
                    // User is the kos owner, allow access
                } catch (IllegalStateException ex) {
                    return ResponseEntity.status(403).build();
                }
            }

            return ResponseEntity.ok(booking);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error getting booking {}: {}", id, e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(@PathVariable UUID id,
                                                 @RequestBody Booking booking,
                                                 Authentication authentication) {
        if (!id.equals(booking.getBookingId())) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Booking existingBooking = bookingService.findBookingById(id).join()
                    .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

            UUID requesterId = authUtils.getUserIdFromAuth(authentication);
            bookingAccessValidator.validateUserAccess(requesterId, existingBooking.getUserId());

            bookingService.updateBooking(booking);

            return bookingService.findBookingById(id).join()
                    .map(b -> ResponseEntity.status(HttpStatus.OK).body(b))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating booking {}: {}", id, e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<Booking> payBooking(@PathVariable UUID id, Authentication authentication) {
        try {
            Booking booking = bookingService.findBookingById(id).join()
                    .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

            UUID requesterId = authUtils.getUserIdFromAuth(authentication);
            bookingAccessValidator.validateUserAccess(requesterId, booking.getUserId());

            bookingService.payBooking(id);

            return bookingService.findBookingById(id).join()
                    .map(b -> ResponseEntity.status(HttpStatus.OK).body(b))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            log.error("Error paying booking {}: {}", id, e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Booking> approveBooking(@PathVariable UUID id, Authentication authentication) {
        try {
            Booking booking = bookingService.findBookingById(id).join()
                    .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

            Kos kos = kosService.getKosById(booking.getKosId())
                    .orElseThrow(() -> new EntityNotFoundException("Kos not found"));

            UUID requesterId = authUtils.getUserIdFromAuth(authentication);
            bookingAccessValidator.validateOwnerAccess(kos.getOwnerId(), requesterId);

            bookingService.approveBooking(id);

            return bookingService.findBookingById(id).join()
                    .map(b -> ResponseEntity.status(HttpStatus.OK).body(b)) // ← FIXED: use 'b' not 'booking'
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            log.error("Error approving booking {}: {}", id, e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Booking>> getBookingsByOwnerId(@PathVariable UUID ownerId) {
        try {
            List<Booking> bookings = bookingService.findBookingsByOwnerId(ownerId).join();
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            log.error("Error getting bookings for owner {}: {}", ownerId, e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable UUID id) {
        try {
            bookingService.cancelBooking(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).build();
        }
    }

}