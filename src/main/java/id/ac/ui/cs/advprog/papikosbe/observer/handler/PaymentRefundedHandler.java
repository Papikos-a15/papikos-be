package id.ac.ui.cs.advprog.papikosbe.observer.handler;

import id.ac.ui.cs.advprog.papikosbe.enums.NotificationType;
import id.ac.ui.cs.advprog.papikosbe.model.transaction.Payment;
import id.ac.ui.cs.advprog.papikosbe.observer.event.PaymentRefundedEvent;
import id.ac.ui.cs.advprog.papikosbe.observer.event.PaymentRefundedEvent;
import id.ac.ui.cs.advprog.papikosbe.service.notification.NotificationService;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

@Component
public class PaymentRefundedHandler implements EventHandler {

    private final NotificationService notificationService;

    public PaymentRefundedHandler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void handleEvent(ApplicationEvent event) {
        PaymentRefundedEvent paymentRefundedEvent = (PaymentRefundedEvent) event;

        notificationService.createNotification(
                paymentRefundedEvent.getId(),
                "Payment Refunded",
                "Sorry, your payment with id " + paymentRefundedEvent.getId() + " refunded and your booking is canceled.",
                NotificationType.PAYMENT
        );

    }
}
