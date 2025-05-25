package id.ac.ui.cs.advprog.papikosbe.observer;

import id.ac.ui.cs.advprog.papikosbe.enums.NotificationType;
import id.ac.ui.cs.advprog.papikosbe.observer.event.BookingApprovedEvent;
import id.ac.ui.cs.advprog.papikosbe.observer.handler.BookingApprovedHandler;
import id.ac.ui.cs.advprog.papikosbe.service.notification.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BookingApprovedHandlerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private BookingApprovedHandler bookingApprovedHandler;

    @Test
    public void testHandleBookingApprovedEvent() {
        UUID userId = UUID.randomUUID();
        UUID bookingId = UUID.randomUUID();

        BookingApprovedEvent event = new BookingApprovedEvent(this, bookingId, userId);

        bookingApprovedHandler.handleEvent(event);

        verify(notificationService, times(1)).createNotification(
                eq(userId),
                eq("Booking Approved"),
                eq("Booking with booking id " + bookingId + " is approved"),
                eq(NotificationType.BOOKING)
        );
    }
}
