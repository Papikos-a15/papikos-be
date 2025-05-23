package id.ac.ui.cs.advprog.papikosbe.service.booking;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.repository.booking.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingStatusUpdateServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingStatusUpdateService bookingStatusUpdateService;

    private List<Booking> expiredBookings;

    @BeforeEach
    void setUp() {
        // Create list of expired bookings
        expiredBookings = new ArrayList<>();

        // Booking with end date in the past (3 month duration, started 4 months ago)
        Booking expiredBooking1 = new Booking(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDate.now().minusMonths(4), // Check-in date 4 months ago
                3, // 3 month duration
                1500000.0,
                "John Doe",
                "081234567890",
                BookingStatus.APPROVED
        );

        // Another expired booking (2 month duration, started 3 months ago)
        Booking expiredBooking2 = new Booking(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDate.now().minusMonths(3), // Check-in date 3 months ago
                2, // 2 month duration
                1800000.0,
                "Jane Smith",
                "082345678901",
                BookingStatus.APPROVED
        );

        expiredBookings.add(expiredBooking1);
        expiredBookings.add(expiredBooking2);
    }

    @Test
    void testUpdateExpiredBookings_ShouldChangeStatusToInactive() throws ExecutionException, InterruptedException {
        // Setup repository mock to return our expired bookings
        when(bookingRepository.findBookingsToDeactivate(any(LocalDate.class)))
                .thenReturn(expiredBookings);

        // Execute the service method
        CompletableFuture<Integer> result = bookingStatusUpdateService.updateExpiredBookingsAsync();

        // Wait for the async operation to complete and get the result
        int updatedCount = result.get();

        // Verify the correct number of bookings were updated
        assertEquals(2, updatedCount);

        // Capture the saved bookings to verify their status was changed
        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository, times(2)).save(bookingCaptor.capture());

        // Verify all captured bookings were set to INACTIVE
        List<Booking> capturedBookings = bookingCaptor.getAllValues();
        for (Booking booking : capturedBookings) {
            assertEquals(BookingStatus.INACTIVE, booking.getStatus());
        }
    }

    @Test
    void testUpdateExpiredBookings_NoExpiredBookings_ShouldReturnZero() throws ExecutionException, InterruptedException {
        // Setup repository to return empty list (no expired bookings)
        when(bookingRepository.findBookingsToDeactivate(any(LocalDate.class)))
                .thenReturn(new ArrayList<>());

        // Execute the service method
        CompletableFuture<Integer> result = bookingStatusUpdateService.updateExpiredBookingsAsync();

        // Wait for async operation to complete
        int updatedCount = result.get();

        // Verify no bookings were updated
        assertEquals(0, updatedCount);
        verify(bookingRepository, never()).save(any(Booking.class));
    }
}