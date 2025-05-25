package id.ac.ui.cs.advprog.papikosbe.observer.handler;

import id.ac.ui.cs.advprog.papikosbe.enums.NotificationType;
import id.ac.ui.cs.advprog.papikosbe.model.wishlist.Wishlist;
import id.ac.ui.cs.advprog.papikosbe.observer.event.KosStatusChangedEvent;
import id.ac.ui.cs.advprog.papikosbe.repository.wishlist.WishlistRepository;
import id.ac.ui.cs.advprog.papikosbe.service.notification.NotificationService;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KosStatusChangedHandler implements EventHandler {

    private final WishlistRepository wishlistRepository;
    private final NotificationService notificationService;

    public KosStatusChangedHandler(WishlistRepository wishlistRepository,
                                   NotificationService notificationService) {
        this.wishlistRepository = wishlistRepository;
        this.notificationService = notificationService;
    }

    @Override
    public void handleEvent(ApplicationEvent event) {
        KosStatusChangedEvent kosStatusChangedEvent = (KosStatusChangedEvent) event;

        List<Wishlist> wishlists = wishlistRepository.findByKosId(kosStatusChangedEvent.getKosId());
        for (Wishlist wishlist : wishlists) {
            notificationService.createNotification(
                    wishlist.getUserId(),
                    "Kos Available",
                    "Kos " + kosStatusChangedEvent.getKosName() + " is now available. Go book it now!",
                    NotificationType.WISHLIST
            );
        }
    }
}
