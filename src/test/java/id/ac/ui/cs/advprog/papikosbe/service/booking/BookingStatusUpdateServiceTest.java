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
import org.mockito.Mockito;
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
        // Use mocked bookings instead of real constructor
        expiredBookings = new ArrayList<>();
        
        // Create mocked bookings
        Booking expiredBooking1 = Mockito.mock(Booking.class);
        Booking expiredBooking2 = Mockito.mock(Booking.class);
        
        // Configure the mocks to return appropriate data
        when(expiredBooking1.getBookingId()).thenReturn(UUID.randomUUID());
        when(expiredBooking1.getStatus()).thenReturn(BookingStatus.APPROVED);
        
        when(expiredBooking2.getBookingId()).thenReturn(UUID.randomUUID());
        when(expiredBooking2.getStatus()).thenReturn(BookingStatus.APPROVED);
        
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
        
        // Verify status was set on each booking
        for (Booking booking : expiredBookings) {
            verify(booking).setStatus(BookingStatus.INACTIVE);
        }
        
        // Verify each booking was saved
        verify(bookingRepository, times(2)).save(any(Booking.class));
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