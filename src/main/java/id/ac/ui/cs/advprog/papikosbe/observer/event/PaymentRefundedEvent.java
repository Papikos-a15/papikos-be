package id.ac.ui.cs.advprog.papikosbe.observer.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class PaymentRefundedEvent extends ApplicationEvent {
    private final UUID id;

    public PaymentRefundedEvent(Object source, UUID id) {
        super(source);
        this.id = id;
    }

}