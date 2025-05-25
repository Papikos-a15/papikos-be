package id.ac.ui.cs.advprog.papikosbe.observer.handler;

import id.ac.ui.cs.advprog.papikosbe.enums.NotificationType;
import id.ac.ui.cs.advprog.papikosbe.observer.event.BookingApprovedEvent;
import id.ac.ui.cs.advprog.papikosbe.service.notification.NotificationService;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

@Component
public class BookingApprovedHandler implements EventHandler {

    private final NotificationService notificationService;

    public BookingApprovedHandler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void handleEvent(ApplicationEvent event) {
        BookingApprovedEvent bookingApprovedEvent = (BookingApprovedEvent) event;

        notificationService.createNotification(
                bookingApprovedEvent.getUserId(),
                "Booking Approved",
                "Booking with booking id " + bookingApprovedEvent.getBookingId() + " is approved",
                NotificationType.BOOKING
        );

    }
}
