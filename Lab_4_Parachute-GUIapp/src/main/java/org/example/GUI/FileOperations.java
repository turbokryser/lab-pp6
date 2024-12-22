package org.example.GUI;

import org.example.api.Dto.ParachuteDTO;
import org.example.persistence.Repositories.AbstractStorage;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.*;
import javax.xml.parsers.*;

import java.io.*;
import java.util.List;

public class FileOperations {

    public static void readFromFile(AbstractStorage<ParachuteDTO> storage, String filename) throws IOException {
        if (filename.endsWith(".txt")) {
            storage.readFromFile(filename);
        } else if (filename.endsWith(".xml")) {
            storage.setListStorage(storage.readFromXml(filename));
        } else if (filename.endsWith(".json")) {
            storage.setListStorage(storage.readDataFromJsonFile(filename));
        }
    }

    public static void writeToFile(AbstractStorage<ParachuteDTO> storage, String filename) {
        if (filename.endsWith(".txt")) {
            storage.writeToFile(filename);
        } else if (filename.endsWith(".xml")) {
            storage.writeToXml(filename, storage.getList());
        } else if (filename.endsWith(".json")) {
            storage.writeDataToJsonFile(filename, storage.getList());
        }
    }
}
