package id.ac.ui.cs.advprog.papikosbe.observer.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class BookingApprovedEvent extends ApplicationEvent {
    private final UUID bookingId;
    private final UUID userId;

    public BookingApprovedEvent(Object source, UUID kosId, UUID userId) {
        super(source);
        this.bookingId = kosId;
        this.userId = userId;
    }

}