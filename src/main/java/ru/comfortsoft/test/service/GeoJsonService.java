package ru.comfortsoft.test.service;

import lombok.extern.slf4j.Slf4j;
import org.geotools.data.geojson.GeoJSONReader;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.MultiPolygon;
import org.opengis.feature.simple.SimpleFeature;
import org.springframework.stereotype.Service;
import ru.comfortsoft.test.model.Feature;
import ru.comfortsoft.test.model.Polygon;
import ru.comfortsoft.test.model.PolygonCoordinate;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@Slf4j
public class GeoJsonService {
    public List<Feature> parseFeatureCollection(InputStream io) throws IOException {
        GeoJSONReader reader = new GeoJSONReader(io);
        SimpleFeatureIterator iterator = reader.getFeatures().features();
        List<Feature> features = new ArrayList<>();
        while (iterator.hasNext()) {
            SimpleFeature geoJsonFeature = iterator.next();

            Feature feature = getFeatureFromJson(geoJsonFeature);
            fillFeatureProperties(geoJsonFeature, feature);
            fillFeatureCoordinates(geoJsonFeature, feature);

            features.add(feature);
        }
        return features;
    }

    private void fillFeatureCoordinates(SimpleFeature geoJsonFeature, Feature feature) {
        // todo несколько полигонов в фиче. Не сделано, так как в файле нет такого примера
        Coordinate[] coordinates = ((MultiPolygon) geoJsonFeature.getProperty("geometry").getValue())
                .getGeometryN(0).getCoordinates();

        List<PolygonCoordinate> polygonCoords = new ArrayList<>();
        Polygon polygon = Polygon.builder()
                .coordinates(polygonCoords)
                .exclusive(false)
                .feature(feature)
                .build();
        Coordinate firstCoordinate = null;
        for (int i = 0; i < coordinates.length; i++) {
            Coordinate coordinate = coordinates[i];
            if (polygonCoords.size() == 0) {
                firstCoordinate = coordinate;
            } else if (coordinate.equals(firstCoordinate)) {
                int lastCoordIndex = polygonCoords.size() - 1;
                for (int j = 0; j < lastCoordIndex; j++) {
                    polygonCoords.get(j).setNextCoordinate(polygonCoords.get(j + 1));
                }
                polygonCoords.get(0).setPreviousCoordinate(polygonCoords.get(lastCoordIndex));
                polygonCoords.get(lastCoordIndex).setNextCoordinate(polygonCoords.get(0));

                feature.getPolygons().add(polygon);
                polygonCoords = new ArrayList<>();
                polygon = Polygon.builder()
                        .coordinates(polygonCoords)
                        .exclusive(true)
                        .feature(feature)
                        .build();
                continue;
            }
            int coordsCount = polygonCoords.size();
            polygonCoords.add(PolygonCoordinate.builder()
                    .x(coordinate.getX())
                    .y(coordinate.getY())
                    .polygon(polygon)
                    .previousCoordinate(coordsCount == 0 ? null : polygonCoords.get(coordsCount - 1))
                    .build());
        }
    }

    private void fillFeatureProperties(SimpleFeature geoJsonFeature, Feature feature) {
        geoJsonFeature.getProperties()
                .stream()
                .filter(x ->
                        !Arrays.asList("GUID", "geometry").contains(x.getDescriptor().getName().toString())
                                && x.getValue() != null)
                .forEach(x -> feature
                        .getProperties()
                        .put(x.getDescriptor().getName().toString(), x.getValue()));
    }

    private Feature getFeatureFromJson(SimpleFeature geoJsonFeature) {
        return Feature.builder()
                .id(UUID.fromString(geoJsonFeature.getProperty("GUID").getValue().toString()))
                .properties(new HashMap<>())
                .polygons(new ArrayList<>())
                .build();
    }
}
