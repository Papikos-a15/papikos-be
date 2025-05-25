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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j  // Add this annotation
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

            // Use .join() to get result from async method synchronously
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
            // Use .join() to get result from async method synchronously
            Booking booking = bookingService.findBookingById(id).join()
                    .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

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
            // Use .join() to get result from async method synchronously
            Booking existingBooking = bookingService.findBookingById(id).join()
                    .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

            UUID requesterId = authUtils.getUserIdFromAuth(authentication);
            bookingAccessValidator.validateUserAccess(requesterId, existingBooking.getUserId());

            bookingService.updateBooking(booking);

            // Return updated booking using async method with .join()
            return bookingService.findBookingById(id).join()
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
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
            // Use .join() to get result from async method synchronously
            Booking booking = bookingService.findBookingById(id).join()
                    .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

            UUID requesterId = authUtils.getUserIdFromAuth(authentication);
            bookingAccessValidator.validateUserAccess(requesterId, booking.getUserId());

            bookingService.payBooking(id);

            // Return updated booking using async method with .join()
            return bookingService.findBookingById(id).join()
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
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
            // Use .join() to get result from async method synchronously
            Booking booking = bookingService.findBookingById(id).join()
                    .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

            Kos kos = kosService.getKosById(booking.getKosId())
                    .orElseThrow(() -> new EntityNotFoundException("Kos not found"));

            UUID requesterId = authUtils.getUserIdFromAuth(authentication);
            bookingAccessValidator.validateOwnerAccess(kos.getOwnerId(), requesterId);

            bookingService.approveBooking(id);

            // Return updated booking using async method with .join()
            return bookingService.findBookingById(id).join()
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
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
            // Log the exception and return an appropriate error response
            log.error("Error getting bookings for owner {}: {}", ownerId, e.getMessage());
            // Consider more specific exception handling if needed
            return ResponseEntity.status(500).build();
        }
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