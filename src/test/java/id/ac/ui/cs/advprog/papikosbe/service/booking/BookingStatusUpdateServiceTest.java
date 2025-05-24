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


    @Test
    void testUpdateStartedBookings_ApprovedBookingsShouldBecomeActive() throws ExecutionException, InterruptedException {
        // Setup: Create APPROVED bookings with check-in dates today or in the past
        LocalDate today = LocalDate.now();

        Booking approvedBookingPastDate = mock(Booking.class);
        when(approvedBookingPastDate.getCheckInDate()).thenReturn(today.minusDays(5));
        when(approvedBookingPastDate.getBookingId()).thenReturn(UUID.randomUUID());

        Booking approvedBookingToday = mock(Booking.class);
        when(approvedBookingToday.getCheckInDate()).thenReturn(today);
        when(approvedBookingToday.getBookingId()).thenReturn(UUID.randomUUID());

        Booking approvedBookingFuture = mock(Booking.class);
        when(approvedBookingFuture.getCheckInDate()).thenReturn(today.plusDays(5));

        List<Booking> approvedBookings = List.of(
                approvedBookingPastDate,
                approvedBookingToday,
                approvedBookingFuture
        );

        when(bookingRepository.findByStatus(BookingStatus.APPROVED)).thenReturn(approvedBookings);

        // Execute
        CompletableFuture<Integer> result = bookingStatusUpdateService.updateStartedBookingsAsync();
        int updatedCount = result.get();

        // Verify
        assertEquals(2, updatedCount); // Only past and today bookings should be updated
        verify(approvedBookingPastDate).setStatus(BookingStatus.ACTIVE);
        verify(approvedBookingToday).setStatus(BookingStatus.ACTIVE);
        verify(approvedBookingFuture, never()).setStatus(any());

        verify(bookingRepository).save(approvedBookingPastDate);
        verify(bookingRepository).save(approvedBookingToday);
        verify(bookingRepository, never()).save(approvedBookingFuture);
    }

    @Test
    void testUpdateStartedBookings_PendingAndPaidBookingsShouldBeCancelled() throws ExecutionException, InterruptedException {
        // Setup: Create PENDING_PAYMENT and PAID bookings with check-in dates in the past
        LocalDate today = LocalDate.now();

        Booking pendingBookingPast = mock(Booking.class);
        when(pendingBookingPast.getCheckInDate()).thenReturn(today.minusDays(2));
        when(pendingBookingPast.getBookingId()).thenReturn(UUID.randomUUID()); // Needed for logging cancelled booking

        Booking paidBookingPast = mock(Booking.class);
        when(paidBookingPast.getCheckInDate()).thenReturn(today.minusDays(1));
        when(paidBookingPast.getBookingId()).thenReturn(UUID.randomUUID()); // Needed for logging cancelled booking

        Booking pendingBookingToday = mock(Booking.class);
        // Use lenient stubbing as getCheckInDate() is called in filter, but booking is then discarded
        lenient().when(pendingBookingToday.getCheckInDate()).thenReturn(today);
        // Removed: when(pendingBookingToday.getBookingId()).thenReturn(UUID.randomUUID()); // This was unnecessary

        Booking paidBookingFuture = mock(Booking.class);
        // Use lenient stubbing as getCheckInDate() is called in filter, but booking is then discarded
        lenient().when(paidBookingFuture.getCheckInDate()).thenReturn(today.plusDays(3));
        // Removed: when(paidBookingFuture.getBookingId()).thenReturn(UUID.randomUUID()); // This was unnecessary

        List<Booking> pendingBookings = List.of(pendingBookingPast, pendingBookingToday);
        List<Booking> paidBookings = List.of(paidBookingPast, paidBookingFuture);

        when(bookingRepository.findByStatus(BookingStatus.PENDING_PAYMENT)).thenReturn(pendingBookings);
        when(bookingRepository.findByStatus(BookingStatus.PAID)).thenReturn(paidBookings);
        when(bookingRepository.findByStatus(BookingStatus.APPROVED)).thenReturn(List.of());

        // Execute
        CompletableFuture<Integer> result = bookingStatusUpdateService.updateStartedBookingsAsync();
        int updatedCount = result.get();

        // Verify
        assertEquals(2, updatedCount); // Only past date bookings should be cancelled
        verify(pendingBookingPast).setStatus(BookingStatus.CANCELLED);
        verify(paidBookingPast).setStatus(BookingStatus.CANCELLED);

        // These shouldn't be cancelled (today's or future dates)
        verify(pendingBookingToday, never()).setStatus(any());
        verify(paidBookingFuture, never()).setStatus(any());

        verify(bookingRepository).save(pendingBookingPast);
        verify(bookingRepository).save(paidBookingPast);
        verify(bookingRepository, never()).save(pendingBookingToday);
        verify(bookingRepository, never()).save(paidBookingFuture);
    }

    @Test
    void testScheduledBookingStatusUpdate_ShouldCallBothUpdateMethods() {
        // Setup a spy to verify method calls
        BookingStatusUpdateServiceImpl serviceSpy = spy(bookingStatusUpdateService);

        // Mock the async methods to return completed futures
        doReturn(CompletableFuture.completedFuture(0))
                .when(serviceSpy).updateExpiredBookingsAsync();
        doReturn(CompletableFuture.completedFuture(0))
                .when(serviceSpy).updateStartedBookingsAsync();

        // Execute
        serviceSpy.scheduledBookingStatusUpdate();

        // Verify both methods were called
        verify(serviceSpy).updateExpiredBookingsAsync();
        verify(serviceSpy).updateStartedBookingsAsync();
    }

    @Test
    void testUpdateStartedBookings_NoBookingsToUpdate_ShouldReturnZero() throws ExecutionException, InterruptedException {
        // Setup with no bookings meeting criteria
        LocalDate today = LocalDate.now();

        Booking approvedFutureBooking = mock(Booking.class);
        when(approvedFutureBooking.getCheckInDate()).thenReturn(today.plusDays(5));

        Booking pendingFutureBooking = mock(Booking.class);
        when(pendingFutureBooking.getCheckInDate()).thenReturn(today.plusDays(3));

        Booking paidFutureBooking = mock(Booking.class);
        when(paidFutureBooking.getCheckInDate()).thenReturn(today.plusDays(1));

        when(bookingRepository.findByStatus(BookingStatus.APPROVED)).thenReturn(List.of(approvedFutureBooking));
        when(bookingRepository.findByStatus(BookingStatus.PENDING_PAYMENT)).thenReturn(List.of(pendingFutureBooking));
        when(bookingRepository.findByStatus(BookingStatus.PAID)).thenReturn(List.of(paidFutureBooking));

        // Execute
        CompletableFuture<Integer> result = bookingStatusUpdateService.updateStartedBookingsAsync();
        int updatedCount = result.get();

        // Verify
        assertEquals(0, updatedCount); // No updates should occur
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void testUpdateStartedBookings_ExceptionHandling() throws ExecutionException, InterruptedException {
        // Setup a booking that will throw an exception when saved
        LocalDate today = LocalDate.now();

        Booking problematicBooking = mock(Booking.class);
        when(problematicBooking.getCheckInDate()).thenReturn(today.minusDays(1));
        when(problematicBooking.getBookingId()).thenReturn(UUID.randomUUID());
        doThrow(new RuntimeException("Database error")).when(bookingRepository).save(problematicBooking);

        Booking normalBooking = mock(Booking.class);
        when(normalBooking.getCheckInDate()).thenReturn(today.minusDays(2));
        when(normalBooking.getBookingId()).thenReturn(UUID.randomUUID());

        when(bookingRepository.findByStatus(BookingStatus.APPROVED))
                .thenReturn(List.of(problematicBooking, normalBooking));
        when(bookingRepository.findByStatus(BookingStatus.PENDING_PAYMENT)).thenReturn(List.of());
        when(bookingRepository.findByStatus(BookingStatus.PAID)).thenReturn(List.of());

        // Execute - should not throw exception despite one booking failing
        CompletableFuture<Integer> result = bookingStatusUpdateService.updateStartedBookingsAsync();
        int updatedCount = result.get();

        // Verify
        assertEquals(1, updatedCount); // Only the normal booking should be updated
        verify(problematicBooking).setStatus(BookingStatus.ACTIVE);
        verify(normalBooking).setStatus(BookingStatus.ACTIVE);
        verify(bookingRepository).save(normalBooking);
    }
}