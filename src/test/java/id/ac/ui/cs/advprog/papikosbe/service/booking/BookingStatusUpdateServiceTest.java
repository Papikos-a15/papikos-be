package id.ac.ui.cs.advprog.papikosbe.service.booking;

import id.ac.ui.cs.advprog.papikosbe.enums.BookingStatus;
import id.ac.ui.cs.advprog.papikosbe.model.booking.Booking;
import id.ac.ui.cs.advprog.papikosbe.repository.booking.BookingRepository;
import id.ac.ui.cs.advprog.papikosbe.validator.booking.BookingValidator;
import id.ac.ui.cs.advprog.papikosbe.service.kos.KosService;
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

    @Mock
    private BookingValidator stateValidator;

    @Mock
    private KosService kosService;

    @InjectMocks
    private BookingStatusUpdateServiceImpl bookingStatusUpdateService;

    // ========== EXPIRED BOOKINGS TESTS (ACTIVE -> INACTIVE) ==========

    @Test
    void updateExpiredBookingsAsync_shouldFindActiveBookings_notApprovedBookings() throws ExecutionException, InterruptedException {
        // Setup: Create ACTIVE bookings (not APPROVED) that are expired
        LocalDate today = LocalDate.now();

        Booking expiredActiveBooking = mock(Booking.class);
        when(expiredActiveBooking.getCheckInDate()).thenReturn(today.minusMonths(3));
        when(expiredActiveBooking.getDuration()).thenReturn(2); // Should have ended 1 month ago
        when(expiredActiveBooking.getBookingId()).thenReturn(UUID.randomUUID());
        UUID kosId1 = UUID.randomUUID();
        when(expiredActiveBooking.getKosId()).thenReturn(kosId1);

        Booking validActiveBooking = mock(Booking.class);
        when(validActiveBooking.getCheckInDate()).thenReturn(today.minusMonths(1));
        when(validActiveBooking.getDuration()).thenReturn(3); // Still has 2 months left

        List<Booking> activeBookings = List.of(expiredActiveBooking, validActiveBooking);

        // Configure repository mock to return ACTIVE bookings (not APPROVED)
        when(bookingRepository.findByStatus(BookingStatus.ACTIVE))
                .thenReturn(activeBookings);

        // Execute
        CompletableFuture<Integer> result = bookingStatusUpdateService.updateExpiredBookingsAsync();
        int updatedCount = result.get();

        // Verify
        assertEquals(1, updatedCount);
        verify(stateValidator).validateForDeactivation(expiredActiveBooking);
        verify(expiredActiveBooking).setStatus(BookingStatus.INACTIVE);
        verify(kosService).addAvailableRoom(kosId1);
        verify(bookingRepository).save(expiredActiveBooking);

        // Valid booking should not be touched
        verify(validActiveBooking, never()).setStatus(any());
        verify(bookingRepository, never()).save(validActiveBooking);
    }

    @Test
    void updateExpiredBookingsAsync_noExpiredBookings_shouldReturnZero() throws ExecutionException, InterruptedException {
        // Setup only valid ACTIVE booking
        LocalDate today = LocalDate.now();
        Booking validActiveBooking = mock(Booking.class);
        when(validActiveBooking.getCheckInDate()).thenReturn(today.minusMonths(1));
        when(validActiveBooking.getDuration()).thenReturn(3); // Still has 2 months left

        List<Booking> activeBookings = List.of(validActiveBooking);

        when(bookingRepository.findByStatus(BookingStatus.ACTIVE))
                .thenReturn(activeBookings);

        // Execute
        CompletableFuture<Integer> result = bookingStatusUpdateService.updateExpiredBookingsAsync();
        int updatedCount = result.get();

        // Verify no bookings were updated
        assertEquals(0, updatedCount);
        verify(stateValidator, never()).validateForDeactivation(any());
        verify(kosService, never()).addAvailableRoom(any());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void updateExpiredBookingsAsync_validationFailure_shouldHandleGracefully() throws ExecutionException, InterruptedException {
        // Setup: Create expired ACTIVE booking
        LocalDate today = LocalDate.now();

        Booking expiredActiveBooking = mock(Booking.class);
        when(expiredActiveBooking.getCheckInDate()).thenReturn(today.minusMonths(2));
        when(expiredActiveBooking.getDuration()).thenReturn(1); // Should have ended 1 month ago
        when(expiredActiveBooking.getBookingId()).thenReturn(UUID.randomUUID());

        // Mock validator to throw exception
        doThrow(new IllegalStateException("Cannot deactivate booking"))
                .when(stateValidator).validateForDeactivation(expiredActiveBooking);

        when(bookingRepository.findByStatus(BookingStatus.ACTIVE))
                .thenReturn(List.of(expiredActiveBooking));

        // Execute - should not throw exception, should handle gracefully
        CompletableFuture<Integer> result = bookingStatusUpdateService.updateExpiredBookingsAsync();
        int updatedCount = result.get();

        // Verify
        assertEquals(0, updatedCount); // No updates due to validation failure
        verify(stateValidator).validateForDeactivation(expiredActiveBooking);
        verify(bookingRepository, never()).save(any(Booking.class));
        verify(kosService, never()).addAvailableRoom(any(UUID.class));
    }

    @Test
    void updateExpiredBookingsAsync_emptyActiveBookings_shouldReturnZero() throws ExecutionException, InterruptedException {
        // Setup with no ACTIVE bookings
        when(bookingRepository.findByStatus(BookingStatus.ACTIVE))
                .thenReturn(List.of());

        // Execute
        CompletableFuture<Integer> result = bookingStatusUpdateService.updateExpiredBookingsAsync();
        int updatedCount = result.get();

        // Verify
        assertEquals(0, updatedCount);
        verify(stateValidator, never()).validateForDeactivation(any());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    // ========== STARTED BOOKINGS TESTS (APPROVED -> ACTIVE) ==========

    @Test
    void updateStartedBookingsAsync_approvedBookingsShouldBecomeActive() throws ExecutionException, InterruptedException {
        // Setup: Create APPROVED bookings with check-in dates today or in the past
        LocalDate today = LocalDate.now();

        Booking approvedBookingPast = mock(Booking.class);
        when(approvedBookingPast.getCheckInDate()).thenReturn(today.minusDays(5));
        when(approvedBookingPast.getBookingId()).thenReturn(UUID.randomUUID());

        Booking approvedBookingToday = mock(Booking.class);
        when(approvedBookingToday.getCheckInDate()).thenReturn(today);
        when(approvedBookingToday.getBookingId()).thenReturn(UUID.randomUUID());

        Booking approvedBookingFuture = mock(Booking.class);
        when(approvedBookingFuture.getCheckInDate()).thenReturn(today.plusDays(5));

        List<Booking> approvedBookings = List.of(
                approvedBookingPast,
                approvedBookingToday,
                approvedBookingFuture
        );

        when(bookingRepository.findByStatus(BookingStatus.APPROVED))
                .thenReturn(approvedBookings);
        when(bookingRepository.findByStatus(BookingStatus.PENDING_PAYMENT))
                .thenReturn(List.of());
        when(bookingRepository.findByStatus(BookingStatus.PAID))
                .thenReturn(List.of());

        // Execute
        CompletableFuture<Integer> result = bookingStatusUpdateService.updateStartedBookingsAsync();
        int updatedCount = result.get();

        // Verify
        assertEquals(2, updatedCount); // Only past and today bookings should be updated
        verify(stateValidator).validateForActivation(approvedBookingPast);
        verify(stateValidator).validateForActivation(approvedBookingToday);
        verify(stateValidator, never()).validateForActivation(approvedBookingFuture);

        verify(approvedBookingPast).setStatus(BookingStatus.ACTIVE);
        verify(approvedBookingToday).setStatus(BookingStatus.ACTIVE);
        verify(approvedBookingFuture, never()).setStatus(any());

        verify(bookingRepository).save(approvedBookingPast);
        verify(bookingRepository).save(approvedBookingToday);
        verify(bookingRepository, never()).save(approvedBookingFuture);
    }

    @Test
    void updateStartedBookingsAsync_validationFailure_shouldHandleGracefully() throws ExecutionException, InterruptedException {
        // Setup: Create APPROVED booking
        LocalDate today = LocalDate.now();

        Booking approvedBooking = mock(Booking.class);
        when(approvedBooking.getCheckInDate()).thenReturn(today.minusDays(1));
        when(approvedBooking.getBookingId()).thenReturn(UUID.randomUUID());

        // Mock validator to throw exception
        doThrow(new IllegalStateException("Cannot activate booking"))
                .when(stateValidator).validateForActivation(approvedBooking);

        when(bookingRepository.findByStatus(BookingStatus.APPROVED))
                .thenReturn(List.of(approvedBooking));
        when(bookingRepository.findByStatus(BookingStatus.PENDING_PAYMENT))
                .thenReturn(List.of());
        when(bookingRepository.findByStatus(BookingStatus.PAID))
                .thenReturn(List.of());

        // Execute - should not throw exception, should handle gracefully
        CompletableFuture<Integer> result = bookingStatusUpdateService.updateStartedBookingsAsync();
        int updatedCount = result.get();

        // Verify
        assertEquals(0, updatedCount); // No updates due to validation failure
        verify(stateValidator).validateForActivation(approvedBooking);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    // ========== MISSED BOOKINGS TESTS (PENDING/PAID -> CANCELLED) ==========

    @Test
    void updateStartedBookingsAsync_pendingAndPaidBookingsShouldBeCancelled() throws ExecutionException, InterruptedException {
        // Setup: Create PENDING_PAYMENT and PAID bookings with check-in dates in the past
        LocalDate today = LocalDate.now();

        Booking pendingBookingPast = mock(Booking.class);
        when(pendingBookingPast.getCheckInDate()).thenReturn(today.minusDays(2));
        when(pendingBookingPast.getBookingId()).thenReturn(UUID.randomUUID());
        UUID kosId1 = UUID.randomUUID();
        when(pendingBookingPast.getKosId()).thenReturn(kosId1);

        Booking paidBookingPast = mock(Booking.class);
        when(paidBookingPast.getCheckInDate()).thenReturn(today.minusDays(1));
        when(paidBookingPast.getBookingId()).thenReturn(UUID.randomUUID());
        UUID kosId2 = UUID.randomUUID();
        when(paidBookingPast.getKosId()).thenReturn(kosId2);

        Booking pendingBookingFuture = mock(Booking.class);
        when(pendingBookingFuture.getCheckInDate()).thenReturn(today.plusDays(3));

        Booking paidBookingFuture = mock(Booking.class);
        when(paidBookingFuture.getCheckInDate()).thenReturn(today.plusDays(1));

        List<Booking> pendingBookings = List.of(pendingBookingPast, pendingBookingFuture);
        List<Booking> paidBookings = List.of(paidBookingPast, paidBookingFuture);

        when(bookingRepository.findByStatus(BookingStatus.APPROVED))
                .thenReturn(List.of());
        when(bookingRepository.findByStatus(BookingStatus.PENDING_PAYMENT))
                .thenReturn(pendingBookings);
        when(bookingRepository.findByStatus(BookingStatus.PAID))
                .thenReturn(paidBookings);

        // Execute
        CompletableFuture<Integer> result = bookingStatusUpdateService.updateStartedBookingsAsync();
        int updatedCount = result.get();

        // Verify
        assertEquals(2, updatedCount); // Only past date bookings should be cancelled
        verify(stateValidator).validateForCancellation(pendingBookingPast);
        verify(stateValidator).validateForCancellation(paidBookingPast);

        verify(pendingBookingPast).setStatus(BookingStatus.CANCELLED);
        verify(paidBookingPast).setStatus(BookingStatus.CANCELLED);

        verify(kosService).addAvailableRoom(kosId1);
        verify(kosService).addAvailableRoom(kosId2);

        // These shouldn't be cancelled (future dates)
        verify(pendingBookingFuture, never()).setStatus(any());
        verify(paidBookingFuture, never()).setStatus(any());

        verify(bookingRepository).save(pendingBookingPast);
        verify(bookingRepository).save(paidBookingPast);
        verify(bookingRepository, never()).save(pendingBookingFuture);
        verify(bookingRepository, never()).save(paidBookingFuture);
    }

    @Test
    void updateStartedBookingsAsync_cancellationValidationFailure_shouldHandleGracefully() throws ExecutionException, InterruptedException {
        // Setup: Create PENDING_PAYMENT booking with past check-in date
        LocalDate today = LocalDate.now();

        Booking pendingBookingPast = mock(Booking.class);
        when(pendingBookingPast.getCheckInDate()).thenReturn(today.minusDays(2));
        when(pendingBookingPast.getBookingId()).thenReturn(UUID.randomUUID());

        // Mock validator to throw exception for cancellation
        doThrow(new IllegalStateException("Cannot cancel booking"))
                .when(stateValidator).validateForCancellation(pendingBookingPast);

        when(bookingRepository.findByStatus(BookingStatus.APPROVED))
                .thenReturn(List.of());
        when(bookingRepository.findByStatus(BookingStatus.PENDING_PAYMENT))
                .thenReturn(List.of(pendingBookingPast));
        when(bookingRepository.findByStatus(BookingStatus.PAID))
                .thenReturn(List.of());

        // Execute - should not throw exception, should handle gracefully
        CompletableFuture<Integer> result = bookingStatusUpdateService.updateStartedBookingsAsync();
        int updatedCount = result.get();

        // Verify
        assertEquals(0, updatedCount); // No updates due to validation failure
        verify(stateValidator).validateForCancellation(pendingBookingPast);
        verify(bookingRepository, never()).save(any(Booking.class));
        verify(kosService, never()).addAvailableRoom(any(UUID.class));
    }

    // ========== COMBINED SCENARIOS TESTS ==========

    @Test
    void updateStartedBookingsAsync_combinedScenario_shouldHandleAllTransitions() throws ExecutionException, InterruptedException {
        // Setup: Mixed scenario with all types of bookings
        LocalDate today = LocalDate.now();

        // APPROVED booking that should become ACTIVE
        Booking approvedBooking = mock(Booking.class);
        when(approvedBooking.getCheckInDate()).thenReturn(today.minusDays(1));
        when(approvedBooking.getBookingId()).thenReturn(UUID.randomUUID());

        // PENDING_PAYMENT booking that should be CANCELLED
        Booking pendingBooking = mock(Booking.class);
        when(pendingBooking.getCheckInDate()).thenReturn(today.minusDays(2));
        when(pendingBooking.getBookingId()).thenReturn(UUID.randomUUID());
        UUID kosId = UUID.randomUUID();
        when(pendingBooking.getKosId()).thenReturn(kosId);

        // PAID booking that should be CANCELLED
        Booking paidBooking = mock(Booking.class);
        when(paidBooking.getCheckInDate()).thenReturn(today.minusDays(1));
        when(paidBooking.getBookingId()).thenReturn(UUID.randomUUID());
        UUID kosId2 = UUID.randomUUID();
        when(paidBooking.getKosId()).thenReturn(kosId2);

        when(bookingRepository.findByStatus(BookingStatus.APPROVED))
                .thenReturn(List.of(approvedBooking));
        when(bookingRepository.findByStatus(BookingStatus.PENDING_PAYMENT))
                .thenReturn(List.of(pendingBooking));
        when(bookingRepository.findByStatus(BookingStatus.PAID))
                .thenReturn(List.of(paidBooking));

        // Execute
        CompletableFuture<Integer> result = bookingStatusUpdateService.updateStartedBookingsAsync();
        int updatedCount = result.get();

        // Verify
        assertEquals(3, updatedCount); // 1 activation + 2 cancellations

        // Verify activation
        verify(stateValidator).validateForActivation(approvedBooking);
        verify(approvedBooking).setStatus(BookingStatus.ACTIVE);

        // Verify cancellations
        verify(stateValidator).validateForCancellation(pendingBooking);
        verify(stateValidator).validateForCancellation(paidBooking);
        verify(pendingBooking).setStatus(BookingStatus.CANCELLED);
        verify(paidBooking).setStatus(BookingStatus.CANCELLED);

        // Verify room restoration for cancelled bookings
        verify(kosService).addAvailableRoom(kosId);
        verify(kosService).addAvailableRoom(kosId2);

        verify(bookingRepository).save(approvedBooking);
        verify(bookingRepository).save(pendingBooking);
        verify(bookingRepository).save(paidBooking);
    }

    @Test
    void updateStartedBookingsAsync_noBookingsToUpdate_shouldReturnZero() throws ExecutionException, InterruptedException {
        // Setup with no bookings meeting criteria (all future dates)
        LocalDate today = LocalDate.now();

        Booking approvedFutureBooking = mock(Booking.class);
        when(approvedFutureBooking.getCheckInDate()).thenReturn(today.plusDays(5));

        Booking pendingFutureBooking = mock(Booking.class);
        when(pendingFutureBooking.getCheckInDate()).thenReturn(today.plusDays(3));

        Booking paidFutureBooking = mock(Booking.class);
        when(paidFutureBooking.getCheckInDate()).thenReturn(today.plusDays(1));

        when(bookingRepository.findByStatus(BookingStatus.APPROVED))
                .thenReturn(List.of(approvedFutureBooking));
        when(bookingRepository.findByStatus(BookingStatus.PENDING_PAYMENT))
                .thenReturn(List.of(pendingFutureBooking));
        when(bookingRepository.findByStatus(BookingStatus.PAID))
                .thenReturn(List.of(paidFutureBooking));

        // Execute
        CompletableFuture<Integer> result = bookingStatusUpdateService.updateStartedBookingsAsync();
        int updatedCount = result.get();

        // Verify
        assertEquals(0, updatedCount); // No updates should occur
        verify(stateValidator, never()).validateForActivation(any());
        verify(stateValidator, never()).validateForCancellation(any());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    // ========== EXCEPTION HANDLING TESTS ==========

    @Test
    void updateStartedBookingsAsync_databaseException_shouldContinueProcessing() throws ExecutionException, InterruptedException {
        // Setup bookings where one will cause database exception
        LocalDate today = LocalDate.now();

        Booking problematicBooking = mock(Booking.class);
        when(problematicBooking.getCheckInDate()).thenReturn(today.minusDays(1));
        when(problematicBooking.getBookingId()).thenReturn(UUID.randomUUID());
        doThrow(new RuntimeException("Database error"))
                .when(bookingRepository).save(problematicBooking);

        Booking normalBooking = mock(Booking.class);
        when(normalBooking.getCheckInDate()).thenReturn(today.minusDays(2));
        when(normalBooking.getBookingId()).thenReturn(UUID.randomUUID());

        when(bookingRepository.findByStatus(BookingStatus.APPROVED))
                .thenReturn(List.of(problematicBooking, normalBooking));
        when(bookingRepository.findByStatus(BookingStatus.PENDING_PAYMENT))
                .thenReturn(List.of());
        when(bookingRepository.findByStatus(BookingStatus.PAID))
                .thenReturn(List.of());

        // Execute - should not throw exception despite one booking failing
        CompletableFuture<Integer> result = bookingStatusUpdateService.updateStartedBookingsAsync();
        int updatedCount = result.get();

        // Verify
        assertEquals(1, updatedCount); // Only the normal booking should be counted
        verify(stateValidator).validateForActivation(problematicBooking);
        verify(stateValidator).validateForActivation(normalBooking);
        verify(problematicBooking).setStatus(BookingStatus.ACTIVE);
        verify(normalBooking).setStatus(BookingStatus.ACTIVE);
        verify(bookingRepository).save(normalBooking);
    }

    // ========== SCHEDULER TESTS ==========

    @Test
    void scheduledBookingStatusUpdate_shouldCallBothUpdateMethods() {
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
    void cancelExpiredPendingPaymentsAsync_pendingPaymentPassedCheckIn_shouldBeCancelled() throws ExecutionException, InterruptedException {
        // Setup: Create PENDING_PAYMENT booking with check-in date in the past
        LocalDate today = LocalDate.now();

        Booking pendingBookingPast = mock(Booking.class);
        when(pendingBookingPast.getCheckInDate()).thenReturn(today.minusDays(1)); // Yesterday
        when(pendingBookingPast.getBookingId()).thenReturn(UUID.randomUUID());
        UUID kosId1 = UUID.randomUUID();
        when(pendingBookingPast.getKosId()).thenReturn(kosId1);


        Booking pendingBookingFuture = mock(Booking.class);
        when(pendingBookingFuture.getCheckInDate()).thenReturn(today.plusDays(1)); // Tomorrow

        List<Booking> pendingBookings = List.of(pendingBookingPast, pendingBookingFuture);

        when(bookingRepository.findByStatus(BookingStatus.PENDING_PAYMENT))
                .thenReturn(pendingBookings);
        // Add this to prevent strict stubbing error
        when(bookingRepository.findByStatus(BookingStatus.PAID))
                .thenReturn(List.of());

        // Execute
        CompletableFuture<Integer> result = bookingStatusUpdateService.cancelExpiredPendingPaymentsAsync();
        int cancelledCount = result.get();

        // Verify
        assertEquals(1, cancelledCount); // Only past booking should be cancelled
        verify(stateValidator).validateForCancellation(pendingBookingPast);
        verify(pendingBookingPast).setStatus(BookingStatus.CANCELLED);
        verify(kosService).addAvailableRoom(kosId1);
        verify(bookingRepository).save(pendingBookingPast);

        // Future booking should not be touched
        verify(pendingBookingFuture, never()).setStatus(any());
        verify(bookingRepository, never()).save(pendingBookingFuture);
    }

    @Test
    void cancelExpiredPendingPaymentsAsync_paidBookingPassedCheckIn_shouldBeCancelled() throws ExecutionException, InterruptedException {
        // Setup: Create PAID booking with check-in date in the past
        LocalDate today = LocalDate.now();

        Booking paidBookingPast = mock(Booking.class);
        when(paidBookingPast.getCheckInDate()).thenReturn(today.minusDays(2)); // 2 days ago
        when(paidBookingPast.getBookingId()).thenReturn(UUID.randomUUID());
        UUID kosId2 = UUID.randomUUID();
        when(paidBookingPast.getKosId()).thenReturn(kosId2);
        // Remove this line
        // when(paidBookingPast.getStatus()).thenReturn(BookingStatus.PAID);

        List<Booking> paidBookings = List.of(paidBookingPast);

        // Add both stubbing to avoid strict stubbing error
        when(bookingRepository.findByStatus(BookingStatus.PENDING_PAYMENT))
                .thenReturn(List.of());
        when(bookingRepository.findByStatus(BookingStatus.PAID))
                .thenReturn(paidBookings);

        // Execute
        CompletableFuture<Integer> result = bookingStatusUpdateService.cancelExpiredPendingPaymentsAsync();
        int cancelledCount = result.get();

        // Verify
        assertEquals(1, cancelledCount);
        verify(stateValidator).validateForCancellation(paidBookingPast);
        verify(paidBookingPast).setStatus(BookingStatus.CANCELLED);
        verify(kosService).addAvailableRoom(kosId2);
        verify(bookingRepository).save(paidBookingPast);
    }

    @Test
    void cancelExpiredPendingPaymentsAsync_validationFailure_shouldHandleGracefully() throws ExecutionException, InterruptedException {
        // Setup: Create booking that fails validation
        LocalDate today = LocalDate.now();

        Booking problematicBooking = mock(Booking.class);
        when(problematicBooking.getCheckInDate()).thenReturn(today.minusDays(1));
        when(problematicBooking.getBookingId()).thenReturn(UUID.randomUUID());
        // Remove this line
        // when(problematicBooking.getStatus()).thenReturn(BookingStatus.PENDING_PAYMENT);

        // Mock validator to throw exception
        doThrow(new IllegalStateException("Cannot cancel this booking"))
                .when(stateValidator).validateForCancellation(problematicBooking);

        when(bookingRepository.findByStatus(BookingStatus.PENDING_PAYMENT))
                .thenReturn(List.of(problematicBooking));
        // Add this to prevent strict stubbing error
        when(bookingRepository.findByStatus(BookingStatus.PAID))
                .thenReturn(List.of());

        // Execute
        CompletableFuture<Integer> result = bookingStatusUpdateService.cancelExpiredPendingPaymentsAsync();
        int cancelledCount = result.get();

        // Verify - should handle error gracefully
        assertEquals(0, cancelledCount);
        verify(stateValidator).validateForCancellation(problematicBooking);
        verify(problematicBooking, never()).setStatus(any());
        verify(bookingRepository, never()).save(problematicBooking);
    }

    @Test
    void scheduledBookingStatusUpdate_shouldCallAllUpdateMethods() {
        // Create a spy from the service
        BookingStatusUpdateServiceImpl serviceSpy = spy(bookingStatusUpdateService);

        // Mock the async methods to return completed futures
        doReturn(CompletableFuture.completedFuture(0))
                .when(serviceSpy).updateExpiredBookingsAsync();
        doReturn(CompletableFuture.completedFuture(0))
                .when(serviceSpy).updateStartedBookingsAsync();
        doReturn(CompletableFuture.completedFuture(0))
                .when(serviceSpy).cancelExpiredPendingPaymentsAsync();

        // Execute
        serviceSpy.scheduledBookingStatusUpdate();

        // Verify that all update methods are called
        verify(serviceSpy).updateExpiredBookingsAsync();
        verify(serviceSpy).updateStartedBookingsAsync();
        verify(serviceSpy).cancelExpiredPendingPaymentsAsync();
    }
}