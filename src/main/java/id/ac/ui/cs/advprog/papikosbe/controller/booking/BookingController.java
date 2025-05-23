package id.ac.ui.cs.advprog.papikosbe.controller.booking;

import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import id.ac.ui.cs.advprog.papikosbe.service.booking.BookingService;
import id.ac.ui.cs.advprog.papikosbe.service.kos.KosService;
import id.ac.ui.cs.advprog.papikosbe.util.AuthenticationUtils;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.BookingValidator;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.BookingAccessValidator;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    @Autowired
    private AuthenticationUtils authUtils;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private KosService kosService;

    @Autowired
    private BookingAccessValidator bookingAccessValidator;

    @Autowired
    private BookingValidator stateValidator;

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking, Authentication authentication) {
        try {
            // Validate authenticated user is the booking user
            UUID requesterId = authUtils.getUserIdFromAuth(authentication);
            if (booking.getUserId() != null && !booking.getUserId().equals(requesterId)) {
                return ResponseEntity.status(403).build(); // Cannot create booking for someone else
            }

            // Set userId from authentication if not provided
            if (booking.getUserId() == null) {
                booking.setUserId(requesterId);
            }

            // Create booking (service handles data validation)
            Booking createdBooking = bookingService.createBooking(booking);
            return ResponseEntity.ok(createdBooking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings(Authentication authentication) {
        UUID userId = authUtils.getUserIdFromAuth(authentication);
        List<Booking> bookings = bookingService.findBookingsByUserId(userId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable UUID id, Authentication authentication) {
        try {
            // Get the booking
            Booking booking = bookingService.findBookingById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

            // Get user ID from authentication
            UUID requesterId = authUtils.getUserIdFromAuth(authentication);

            // Check if requester is the booking user or the kos owner
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
                    // Neither booking user nor kos owner
                    return ResponseEntity.status(403).build();
                }
            }

            return ResponseEntity.ok(booking);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(@PathVariable UUID id,
                                                 @RequestBody Booking booking,
                                                 Authentication authentication) {
        // Ensure ID matches
        if (!id.equals(booking.getBookingId())) {
            return ResponseEntity.badRequest().build();
        }

        try {
            // Get existing booking
            Booking existingBooking = bookingService.findBookingById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

            // PERMISSION CHECK: Only validate WHO can perform this action
            UUID requesterId = authUtils.getUserIdFromAuth(authentication);
            bookingAccessValidator.validateUserAccess(requesterId, existingBooking.getUserId());

            // Call service for business logic and data validation
            bookingService.updateBooking(booking);

            return bookingService.findBookingById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<Booking> payBooking(@PathVariable UUID id, Authentication authentication) {
        try {
            // Get the booking
            Booking booking = bookingService.findBookingById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

            // Extract requester ID
            UUID requesterId = authUtils.getUserIdFromAuth(authentication);

            // Permission validation (WHO can pay)
            bookingAccessValidator.validateUserAccess(requesterId, booking.getUserId());

            // State validation (WHEN payment is allowed) - handled by service

            // Process payment
            bookingService.payBooking(id);

            // Return updated booking
            return bookingService.findBookingById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Booking> approveBooking(@PathVariable UUID id, Authentication authentication) {
        try {
            // Get the booking
            Booking booking = bookingService.findBookingById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

            // Get the kos
            Kos kos = kosService.getKosById(booking.getKosId())
                    .orElseThrow(() -> new EntityNotFoundException("Kos not found"));

            // Extract requester ID
            UUID requesterId = authUtils.getUserIdFromAuth(authentication);

            // Permission validation (WHO can approve)
            bookingAccessValidator.validateOwnerAccess(kos.getOwnerId(), requesterId);

            // State validation (WHEN approval is allowed) - handled by service

            // Process approval
            bookingService.approveBooking(id);

            // Return updated booking
            return bookingService.findBookingById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).build();
        }
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Booking>> getBookingsByOwnerId(@PathVariable UUID ownerId) {
        List<Booking> bookings = bookingService.findBookingsByOwnerId(ownerId);
        return ResponseEntity.ok(bookings);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable UUID id) {
        try {
            // State validation - handled by service
            bookingService.cancelBooking(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).build();
        }
    }

}