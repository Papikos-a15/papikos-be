package id.ac.ui.cs.advprog.papikosbe.service.booking;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.repository.booking.BookingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    private BookingStatusUpdateServiceImpl bookingStatusUpdateService;

    @Test
    void testUpdateExpiredBookings_ShouldChangeStatusToInactive() throws ExecutionException, InterruptedException {
        // Create mock bookings directly in the test method
        Booking expiredBooking = mock(Booking.class);
        when(expiredBooking.getCheckInDate()).thenReturn(LocalDate.now().minusMonths(3));
        when(expiredBooking.getDuration()).thenReturn(2);
        when(expiredBooking.getBookingId()).thenReturn(UUID.randomUUID());
        
        Booking validBooking = mock(Booking.class);
        when(validBooking.getCheckInDate()).thenReturn(LocalDate.now().minusMonths(1));
        when(validBooking.getDuration()).thenReturn(3);
        
        // Add both to the list of approved bookings
        List<Booking> approvedBookings = new ArrayList<>();
        approvedBookings.add(expiredBooking);
        approvedBookings.add(validBooking);
        
        // Configure repository mock
        when(bookingRepository.findByStatus(BookingStatus.APPROVED))
                .thenReturn(approvedBookings);
        
        // Execute the service method
        CompletableFuture<Integer> result = bookingStatusUpdateService.updateExpiredBookingsAsync();
        
        // Wait for the async operation to complete and get the result
        int updatedCount = result.get();
        
        // Verify only expired bookings were updated
        assertEquals(1, updatedCount);
        
        // Verify status was set to INACTIVE only on expired booking
        verify(expiredBooking).setStatus(BookingStatus.INACTIVE);
        
        // Make sure we didn't update the valid booking
        verify(validBooking, never()).setStatus(any(BookingStatus.class));
        
        // Verify booking was saved
        verify(bookingRepository).save(expiredBooking);
    }

    @Test
    void testUpdateExpiredBookings_NoExpiredBookings_ShouldReturnZero() throws ExecutionException, InterruptedException {
        // Setup only valid booking
        Booking validBooking = mock(Booking.class);
        when(validBooking.getCheckInDate()).thenReturn(LocalDate.now().minusMonths(1));
        when(validBooking.getDuration()).thenReturn(3);
        
        List<Booking> nonExpiredBookings = new ArrayList<>();
        nonExpiredBookings.add(validBooking);
        
        when(bookingRepository.findByStatus(BookingStatus.APPROVED))
                .thenReturn(nonExpiredBookings);
        
        // Execute the service method
        CompletableFuture<Integer> result = bookingStatusUpdateService.updateExpiredBookingsAsync();
        
        // Wait for async operation to complete
        int updatedCount = result.get();
        
        // Verify no bookings were updated
        assertEquals(0, updatedCount);
        verify(bookingRepository, never()).save(any(Booking.class));
    }
}