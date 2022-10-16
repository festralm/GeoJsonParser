package ru.comfortsoft.test.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Service
public class FileService {
    public InputStream getFileInputStream(String path) throws FileNotFoundException {
        File initialFile = new File(path);
        return new FileInputStream(initialFile);
    }
}
