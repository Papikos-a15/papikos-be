package id.ac.ui.cs.advprog.papikosbe.observer;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.EventObject;
import java.util.UUID;

@Getter
public class KosStatusChangedEvent extends ApplicationEvent {
    private final UUID kosId;
    private final String kosName;
    private final boolean newStatus;

    public KosStatusChangedEvent(Object source, UUID kosId, String kosName, boolean newStatus) {
        super(source);
        this.kosId = kosId;
        this.kosName = kosName;
        this.newStatus = newStatus;
    }

}