package ru.comfortsoft.test.model;

import com.vladmihalcea.hibernate.type.basic.PostgreSQLHStoreType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TypeDef(name = "hstore", typeClass = PostgreSQLHStoreType.class)
@Getter
@Setter
public class Feature {
    @Id
    private UUID id;

    @Type(type = "hstore")
    @Column(columnDefinition = "hstore")
    private Map<String, Object> properties;

    @OneToMany(mappedBy = "feature", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Polygon> polygons;
}
