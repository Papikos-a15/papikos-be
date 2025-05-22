package id.ac.ui.cs.advprog.papikosbe.observer;

import id.ac.ui.cs.advprog.papikosbe.enums.NotificationType;
import id.ac.ui.cs.advprog.papikosbe.model.wishlist.Wishlist;
import id.ac.ui.cs.advprog.papikosbe.repository.wishlist.WishlistRepository;
import id.ac.ui.cs.advprog.papikosbe.service.notification.NotificationService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KosStatusChangedListener {
    private final WishlistRepository wishlistRepository;
    private final NotificationService notificationService;

    public KosStatusChangedListener(WishlistRepository wishlistRepository,
                                     NotificationService notificationService) {
        this.wishlistRepository = wishlistRepository;
        this.notificationService = notificationService;
    }

    @EventListener
    public void handleRoomStatusChanged(KosStatusChangedEvent event) {

        List<Wishlist> wishlists = wishlistRepository.findByKosId(event.getKosId());
        for (Wishlist wishlist : wishlists) {
            notificationService.createNotification(
                    wishlist.getUserId(),
                    "Kos Available",
                    "Kos "+event.getKosName() + " is now available. Go book it now!",
                    NotificationType.WISHLIST);
        }
    }
}