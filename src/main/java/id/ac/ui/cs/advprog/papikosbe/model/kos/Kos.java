package id.ac.ui.cs.advprog.papikosbe.model.kos;

import lombok.Setter;
import lombok.Getter;

import java.util.UUID;

@Setter
@Getter
public class Kos {

    private UUID id;
    private UUID ownerId;
    private UUID tenantId;
    private String name;
    private String address;
    private String description;
    private Double price;
    private boolean isAvailable;


    // Constructors
    public Kos() {}

    public Kos(UUID id, String name, String address, String description, Double price, boolean isAvailable) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.description = description;
        this.price = price;
        this.isAvailable = isAvailable;
    }

    public Kos(UUID id, UUID ownerId, UUID tenantId, String name, String address, String description, Double price, boolean isAvailable) {
        this.id = id;
        this.ownerId = ownerId;
        this.tenantId = tenantId;
        this.name = name;
        this.address = address;
        this.description = description;
        this.price = price;
        this.isAvailable = isAvailable;
    }
}
