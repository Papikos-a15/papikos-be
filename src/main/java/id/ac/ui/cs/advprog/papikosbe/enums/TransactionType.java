package id.ac.ui.cs.advprog.papikosbe.enums;

import lombok.Getter;

@Getter
public enum TransactionType {
    TOP_UP("TOP_UP"),
    PAYMENT("PAYMENT");

    private final String value;

    private TransactionType(String value) {
        this.value = value;
    }
}
