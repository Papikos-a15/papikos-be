package id.ac.ui.cs.advprog.papikosbe.observer.handler;

import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EventHandlerContext {

    private final Map<Class<? extends ApplicationEvent>, EventHandler> eventHandlers;

    public EventHandlerContext(Map<Class<? extends ApplicationEvent>, EventHandler> eventHandlers) {
        this.eventHandlers = eventHandlers;
    }

    public void handleEvent(ApplicationEvent event) {
        EventHandler handler = eventHandlers.get(event.getClass());
        if (handler != null) {
            handler.handleEvent(event);
        }
    }
}
