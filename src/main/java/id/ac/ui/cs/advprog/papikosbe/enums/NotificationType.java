package id.ac.ui.cs.advprog.papikosbe.enums;

import lombok.Getter;

@Getter
public enum NotificationType {
    BOOKING("BOOKING"),
    PAYMENT("PAYMENT"),
    PROMOTION("PROMOTION"),
    SYSTEM("SYSTEM"),
    OTHER("OTHER");

    private final String value;

    private NotificationType(String value) {
        this.value = value;
    }

    public static boolean contains(String param) {
        for (NotificationType type : NotificationType.values()) {
            if (type.getValue().equals(param)) {
                return true;
            }
        }
        return false;
    }
}
