package id.ac.ui.cs.advprog.papikosbe.enums;

import lombok.Getter;

@Getter
public enum NotificationType {
    BOOKING("BOOKING"),
    PAYMENT("PAYMENT"),
    SYSTEM("SYSTEM"),
    OTHER("OTHER"),
    WISHLIST("WISHLIST"),
    ADMIN("ADMIN");


    private final String value;

    NotificationType(String value) {
        this.value = value;
    }
}
