package ru.comfortsoft.test.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Polygon {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Feature feature;

    private Boolean exclusive;

    @OneToMany(mappedBy = "polygon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PolygonCoordinate> coordinates;
}
