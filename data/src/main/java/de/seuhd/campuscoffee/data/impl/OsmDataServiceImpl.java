package de.seuhd.campuscoffee.data.impl;

import de.seuhd.campuscoffee.domain.exceptions.OsmNodeNotFoundException;
import de.seuhd.campuscoffee.domain.model.OsmNode;
import de.seuhd.campuscoffee.domain.ports.OsmDataService;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Production OSM import service: fetch OSM node XML and parse tags.
 */
@Service
@Slf4j
public class OsmDataServiceImpl implements OsmDataService {

    @Override
    public @NonNull OsmNode fetchNode(@NonNull Long nodeId) throws OsmNodeNotFoundException {
        try {
            // Fetch OSM data
            String apiUrl = "https://www.openstreetmap.org/api/0.6/node/" + nodeId;
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int status = connection.getResponseCode();
            if (status != 200) {
                throw new OsmNodeNotFoundException(nodeId);
            }

            // Parse XML
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new InputSource(connection.getInputStream()));

            NodeList nodes = doc.getElementsByTagName("node");
            if (nodes.getLength() == 0) throw new OsmNodeNotFoundException(nodeId);

            Element nodeElem = (Element) nodes.item(0);

            Map<String, String> tags = new HashMap<>();
            NodeList tagNodes = nodeElem.getElementsByTagName("tag");
            for (int i = 0; i < tagNodes.getLength(); i++) {
                Element tag = (Element) tagNodes.item(i);
                tags.put(tag.getAttribute("k"), tag.getAttribute("v"));
            }

            return OsmNode.builder()
                    .nodeId(nodeId)
                    .tags(tags)
                    .build();

        } catch (Exception e) {
            log.error("Failed to parse OSM node {}", nodeId, e);
            throw new OsmNodeNotFoundException(nodeId);
        }
    }
}