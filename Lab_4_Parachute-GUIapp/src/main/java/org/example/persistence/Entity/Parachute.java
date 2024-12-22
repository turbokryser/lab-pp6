package org.example.persistence.Entity;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class Parachute {
    Integer cost;
    String name;
    String description;


    public Parachute(Integer cost, String name, String Desc) {
        this.name = name;
        this.cost = cost;
        this.description = Desc;
    }


    @Override
    public String toString() {
        return "Parachute{" +
                "cost=" + cost +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
