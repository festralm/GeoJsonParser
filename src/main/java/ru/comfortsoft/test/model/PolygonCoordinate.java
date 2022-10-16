package ru.comfortsoft.test.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PolygonCoordinate {
    @Id
    @GeneratedValue
    private Long id;

    private double x;

    private double y;

    @OneToOne
    private PolygonCoordinate nextCoordinate;

    @OneToOne
    private PolygonCoordinate previousCoordinate;

    @ManyToOne
    private Polygon polygon;
}
