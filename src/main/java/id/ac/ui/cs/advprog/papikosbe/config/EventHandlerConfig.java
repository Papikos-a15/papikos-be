package id.ac.ui.cs.advprog.papikosbe.config;

import id.ac.ui.cs.advprog.papikosbe.observer.event.PaymentRefundedEvent;
import id.ac.ui.cs.advprog.papikosbe.observer.handler.*;
import id.ac.ui.cs.advprog.papikosbe.observer.event.KosStatusChangedEvent;
import id.ac.ui.cs.advprog.papikosbe.observer.event.BookingApprovedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class EventHandlerConfig {

    @Bean
    public EventHandlerContext eventHandlerContext(
            KosStatusChangedHandler kosStatusChangedHandler,
            BookingApprovedHandler bookingApprovedHandler,
            PaymentRefundedHandler paymentRefundedHandler

    ) {
        Map<Class<? extends ApplicationEvent>, EventHandler> eventHandlerMap = new HashMap<>();
        eventHandlerMap.put(KosStatusChangedEvent.class, kosStatusChangedHandler);
        eventHandlerMap.put(BookingApprovedEvent.class, bookingApprovedHandler);
        eventHandlerMap.put(PaymentRefundedEvent.class, paymentRefundedHandler);

        return new EventHandlerContext(eventHandlerMap);
    }
}
