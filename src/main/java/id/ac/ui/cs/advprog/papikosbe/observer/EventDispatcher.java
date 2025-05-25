package id.ac.ui.cs.advprog.papikosbe.observer;

import id.ac.ui.cs.advprog.papikosbe.observer.handler.EventHandlerContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EventDispatcher {

    private final EventHandlerContext eventHandlerContext;

    public EventDispatcher(EventHandlerContext eventHandlerContext) {
        this.eventHandlerContext = eventHandlerContext;
    }

    @EventListener
    public void onApplicationEvent(ApplicationEvent event) {
        eventHandlerContext.handleEvent(event);
    }
}
