package org.example.api.Factory;

import org.example.api.Dto.ParachuteDTO;
import org.example.persistence.Repositories.AbstractStorage;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ParachuteFactory extends AbstractStorage<ParachuteDTO> {

    private static ParachuteFactory instance;

    private ParachuteFactory() {}

    public static ParachuteFactory getInstance() {
        if (instance == null) {
            instance = new ParachuteFactory();
        }
        return instance;
    }

    @Override
    public void readFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String[] parts = line.split(",");
                    int cost = Integer.parseInt(parts[1]);
                    String name = parts[0];
                    String desc = parts[2];

                    ParachuteDTO parachute = new ParachuteDTO(name, cost, desc);
                    addToListStorage(parachute);
                    addToMapStorage(cost, parachute);
                }
                catch (Exception e1)
                {
                    continue;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeToFile(String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (ParachuteDTO bus : listStorage) {
                bw.write(bus.getName() + "," +
                        bus.getCost() + "," +
                        bus.getDescription()+"\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ParachuteDTO> readFromXml(String filename) {
        List<ParachuteDTO> list = new ArrayList<>();
        try {
            File xmlFile = new File(filename);
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(xmlFile);

            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("furniture");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    ParachuteDTO parachute = new ParachuteDTO();
                    parachute.setCost(Integer.parseInt(element.getElementsByTagName("cost").item(0).getTextContent()));
                    parachute.setName(element.getElementsByTagName("name").item(0).getTextContent());
                    parachute.setDescription(element.getElementsByTagName("description").item(0).getTextContent());

                    list.add(parachute);
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public void writeToXml(String filename, List<ParachuteDTO> list) {
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            Element root = document.createElement("fuenit");
            document.appendChild(root);

            for (ParachuteDTO vehicle : list) {
                Element parachute = document.createElement("furniture");

                Element type = document.createElement("name");
                type.appendChild(document.createTextNode(vehicle.getName()));
                parachute.appendChild(type);

                Element cost = document.createElement("cost");
                cost.appendChild(document.createTextNode(String.valueOf(vehicle.getCost())));
                parachute.appendChild(cost);



                Element model = document.createElement("description");
                model.appendChild(document.createTextNode(vehicle.getDescription()));
                parachute.appendChild(model);

                root.appendChild(parachute);
            }

            Transformer tr = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(document);
            FileOutputStream fos = new FileOutputStream("furniture.xml");
            StreamResult result = new StreamResult(new File(filename));

            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.transform(source, result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ParachuteDTO findByName(String name) {
        return listStorage.stream().filter(c -> c.getName().equals(name)).findFirst().orElse(new ParachuteDTO("",-1,""));
    }

    public List<ParachuteDTO> readDataFromJsonFile(String fileName) {
        List<ParachuteDTO> parachute = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            JSONArray jsonArray = new JSONArray(jsonContent.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ParachuteDTO parachute1 = new ParachuteDTO();
                parachute1.setName(jsonObject.getString("name"));
                parachute1.setCost(jsonObject.getInt("cost"));
                parachute1.setDescription(jsonObject.getString("description"));
                parachute.add(parachute1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parachute;
    }

    public void writeDataToJsonFile(String fileName, List<ParachuteDTO> parachutes) {
        JSONArray jsonArray = new JSONArray();
        for (ParachuteDTO parachute : parachutes) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", parachute.getName());
            jsonObject.put("cost", parachute.getCost());
            jsonObject.put("description", parachute.getDescription());
            jsonArray.put(jsonObject);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(jsonArray.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
