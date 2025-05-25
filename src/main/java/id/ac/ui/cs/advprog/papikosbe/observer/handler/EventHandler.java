package id.ac.ui.cs.advprog.papikosbe.observer.handler;

import org.springframework.context.ApplicationEvent;

public interface EventHandler {
    void handleEvent(ApplicationEvent event);
}
