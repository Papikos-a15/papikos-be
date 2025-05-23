package id.ac.ui.cs.advprog.papikosbe.service.booking;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.repository.booking.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingStatusUpdateServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingStatusUpdateServiceImpl bookingStatusUpdateService;

    private List<Booking> mockApprovedBookings;
    private List<Booking> expiredBookings;

    @BeforeEach
    void setUp() {
        // Create mock approved bookings
        mockApprovedBookings = new ArrayList<>();
        
        // Create a mock booking that's expired (created 3 months ago with 2 month duration)
        Booking expiredBooking = mock(Booking.class);
        when(expiredBooking.getBookingId()).thenReturn(UUID.randomUUID());
        when(expiredBooking.getStatus()).thenReturn(BookingStatus.APPROVED);
        when(expiredBooking.getCheckInDate()).thenReturn(LocalDate.now().minusMonths(3));
        when(expiredBooking.getDuration()).thenReturn(2);
        
        // Create a mock booking that's not expired yet (created 1 month ago with 3 month duration)
        Booking validBooking = mock(Booking.class);
        when(validBooking.getBookingId()).thenReturn(UUID.randomUUID());
        when(validBooking.getStatus()).thenReturn(BookingStatus.APPROVED);
        when(validBooking.getCheckInDate()).thenReturn(LocalDate.now().minusMonths(1));
        when(validBooking.getDuration()).thenReturn(3);
        
        mockApprovedBookings.add(expiredBooking);
        mockApprovedBookings.add(validBooking);
        
        // Pre-filter the expired bookings for our test expectations
        expiredBookings = mockApprovedBookings.stream()
            .filter(booking -> {
                LocalDate endDate = booking.getCheckInDate().plusMonths(booking.getDuration());
                return endDate.isBefore(LocalDate.now());
            })
            .collect(Collectors.toList());
    }

    @Test
    void testUpdateExpiredBookings_ShouldChangeStatusToInactive() throws ExecutionException, InterruptedException {
        // Setup repository mock to return our approved bookings
        when(bookingRepository.findByStatus(BookingStatus.APPROVED))
                .thenReturn(mockApprovedBookings);
        
        // Execute the service method
        CompletableFuture<Integer> result = bookingStatusUpdateService.updateExpiredBookingsAsync();
        
        // Wait for the async operation to complete and get the result
        int updatedCount = result.get();
        
        // Verify only expired bookings were updated (should be 1)
        assertEquals(expiredBookings.size(), updatedCount);
        
        // Verify status was set to INACTIVE only on expired booking
        verify(expiredBookings.get(0)).setStatus(BookingStatus.INACTIVE);
        
        // Make sure we didn't update the valid booking
        verify(mockApprovedBookings.get(1), never()).setStatus(any(BookingStatus.class));
        
        // Verify booking was saved
        verify(bookingRepository, times(expiredBookings.size())).save(any(Booking.class));
    }

    @Test
    void testUpdateExpiredBookings_NoExpiredBookings_ShouldReturnZero() throws ExecutionException, InterruptedException {
        // Setup all bookings as non-expired
        List<Booking> nonExpiredBookings = new ArrayList<>();
        Booking validBooking = mock(Booking.class);
        when(validBooking.getCheckInDate()).thenReturn(LocalDate.now().minusMonths(1));
        when(validBooking.getDuration()).thenReturn(3);
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