package id.ac.ui.cs.advprog.papikosbe.model.kos;

import lombok.Setter;
import lombok.Getter;

@Setter
@Getter
public class Kos {

    private String id;
    private String name;
    private String address;
    private String description;
    private Double price;


    // Constructors
    public Kos() {}

    public Kos(String id, String name, String address, String description, Double price) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.description = description;
        this.price = price;
    }

}
