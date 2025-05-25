package id.ac.ui.cs.advprog.papikosbe.observer;

import id.ac.ui.cs.advprog.papikosbe.enums.NotificationType;
import id.ac.ui.cs.advprog.papikosbe.observer.handler.PaymentRefundedHandler;
import id.ac.ui.cs.advprog.papikosbe.service.notification.NotificationService;
import id.ac.ui.cs.advprog.papikosbe.observer.event.PaymentRefundedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentRefundedHandlerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private PaymentRefundedHandler paymentRefundedHandler;

    @Test
    void testHandleEvent_CreatesNotification() {
        UUID paymentId = UUID.randomUUID();
        PaymentRefundedEvent event = new PaymentRefundedEvent(this, paymentId);

        paymentRefundedHandler.handleEvent(event);

        verify(notificationService, times(1)).createNotification(
                eq(paymentId),
                eq("Payment Refunded"),
                eq("Sorry, your payment with id " + paymentId + " refunded and your booking is canceled."),
                eq(NotificationType.PAYMENT)
        );
    }
}
