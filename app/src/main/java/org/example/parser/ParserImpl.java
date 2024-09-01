package org.example.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.example.model.City;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class ParserImpl implements ParserInterface{

    private static Logger logger = LoggerFactory.getLogger(ParserImpl.class);
    @Override
    public void toXML(String file) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            City city = objectMapper.readValue(new File(file), City.class);
            logger.info("Read value from ObjectMapper: {}", city.toString());
            String xmlOutput = new XmlMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(city);
            logger.info("XML output: {}", xmlOutput);
            saveToFile("output-" + file.replace(".json", ".xml"), xmlOutput);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void saveToFile(String file, String xml) {
        try {
            Files.write(Paths.get(file), xml.getBytes());
            logger.info("XML saved to file: {}", file);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
