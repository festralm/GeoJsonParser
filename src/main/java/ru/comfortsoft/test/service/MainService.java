package ru.comfortsoft.test.service;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.comfortsoft.test.model.Feature;
import ru.comfortsoft.test.repository.FeatureRepository;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@Slf4j
public class MainService {
    private static final String FILE_PATH = "1.geojson";
    @Setter(onMethod_ = @Autowired)
    private GeoJsonService geoJsonService;

    @Setter(onMethod_ = @Autowired)
    private FileService fileService;

    @Setter(onMethod_ = @Autowired)
    private FeatureRepository featureRepository;

    @PostConstruct
    private void init() {
        try (InputStream io = fileService.getFileInputStream(FILE_PATH)) {
            List<Feature> features = geoJsonService.parseFeatureCollection(io);
            featureRepository.saveAll(features);
        } catch (FileNotFoundException e) {
            log.error("Exception while reading file", e);
        } catch (IOException e) {
            log.error("Exception while parsing GeoJSON", e);
        }
    }
}
