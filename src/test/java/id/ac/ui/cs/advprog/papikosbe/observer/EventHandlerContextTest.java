package id.ac.ui.cs.advprog.papikosbe.observer;

import static org.mockito.Mockito.*;

import id.ac.ui.cs.advprog.papikosbe.observer.handler.EventHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.context.ApplicationEvent;
import id.ac.ui.cs.advprog.papikosbe.observer.handler.EventHandlerContext;
import id.ac.ui.cs.advprog.papikosbe.observer.event.KosStatusChangedEvent;
import id.ac.ui.cs.advprog.papikosbe.observer.event.BookingApprovedEvent;
import id.ac.ui.cs.advprog.papikosbe.observer.handler.KosStatusChangedHandler;
import id.ac.ui.cs.advprog.papikosbe.observer.handler.BookingApprovedHandler;
import java.util.Map;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class EventHandlerContextTest {

    @Mock
    private KosStatusChangedHandler kosStatusChangedHandler;

    @Mock
    private BookingApprovedHandler bookingApprovedHandler;

    @Mock
    private EventHandlerContext eventHandlerContext;

    @BeforeEach
    public void setUp() {
        Map<Class<? extends ApplicationEvent>, EventHandler> eventHandlers = Map.of(
                KosStatusChangedEvent.class, kosStatusChangedHandler,
                BookingApprovedEvent.class, bookingApprovedHandler
        );
        eventHandlerContext = new EventHandlerContext(eventHandlers);
    }

    @Test
    public void testHandleKosStatusChangedEvent() {
        UUID kosId = UUID.randomUUID();
        String kosName = "Kos 1";
        KosStatusChangedEvent event = new KosStatusChangedEvent(this, kosId, kosName, true);

        eventHandlerContext.handleEvent(event);

        verify(kosStatusChangedHandler, times(1)).handleEvent(event);
    }

    @Test
    public void testHandleBookingApprovedEvent() {
        UUID bookingId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        BookingApprovedEvent event = new BookingApprovedEvent(this, bookingId, userId);

        eventHandlerContext.handleEvent(event);

        verify(bookingApprovedHandler, times(1)).handleEvent(event);
    }

    @Test
    public void testHandleUnknownEvent() {
        ApplicationEvent unknownEvent = mock(ApplicationEvent.class);

        eventHandlerContext.handleEvent(unknownEvent);

        verify(kosStatusChangedHandler, times(0)).handleEvent(any());
        verify(bookingApprovedHandler, times(0)).handleEvent(any());
    }
}
