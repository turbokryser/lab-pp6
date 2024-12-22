package org.example.api.Factory;

import org.example.api.Dto.FurnitureDTO;
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

public class FurnitureFactory extends AbstractStorage<FurnitureDTO> {

    private static FurnitureFactory instance;

    private FurnitureFactory() {}

    public static FurnitureFactory getInstance() {
        if (instance == null) {
            instance = new FurnitureFactory();
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

                    FurnitureDTO Furniture = new FurnitureDTO(name, cost, desc);
                    addToListStorage(Furniture);
                    addToMapStorage(cost, Furniture);
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
            for (FurnitureDTO bus : listStorage) {
                bw.write(bus.getName() + "," +
                        bus.getCost() + "," +
                        bus.getDescription()+"\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<FurnitureDTO> readFromXml(String filename) {
        List<FurnitureDTO> list = new ArrayList<>();
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

                    FurnitureDTO Furniture = new FurnitureDTO();
                    Furniture.setCost(Integer.parseInt(element.getElementsByTagName("cost").item(0).getTextContent()));
                    Furniture.setName(element.getElementsByTagName("name").item(0).getTextContent());
                    Furniture.setDescription(element.getElementsByTagName("description").item(0).getTextContent());

                    list.add(Furniture);
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public void writeToXml(String filename, List<FurnitureDTO> list) {
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            Element root = document.createElement("fuenit");
            document.appendChild(root);

            for (FurnitureDTO vehicle : list) {
                Element Furniture = document.createElement("furniture");

                Element type = document.createElement("name");
                type.appendChild(document.createTextNode(vehicle.getName()));
                Furniture.appendChild(type);

                Element cost = document.createElement("cost");
                cost.appendChild(document.createTextNode(String.valueOf(vehicle.getCost())));
                Furniture.appendChild(cost);



                Element model = document.createElement("description");
                model.appendChild(document.createTextNode(vehicle.getDescription()));
                Furniture.appendChild(model);

                root.appendChild(Furniture);
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
    public FurnitureDTO findByName(String name) {
        return listStorage.stream().filter(c -> c.getName().equals(name)).findFirst().orElse(new FurnitureDTO("",-1,""));
    }

    public List<FurnitureDTO> readDataFromJsonFile(String fileName) {
        List<FurnitureDTO> Furniture = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            JSONArray jsonArray = new JSONArray(jsonContent.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                FurnitureDTO Furniture1 = new FurnitureDTO();
                Furniture1.setName(jsonObject.getString("name"));
                Furniture1.setCost(jsonObject.getInt("cost"));
                Furniture1.setDescription(jsonObject.getString("description"));
                Furniture.add(Furniture1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Furniture;
    }

    public void writeDataToJsonFile(String fileName, List<FurnitureDTO> Furnitures) {
        JSONArray jsonArray = new JSONArray();
        for (FurnitureDTO Furniture : Furnitures) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", Furniture.getName());
            jsonObject.put("cost", Furniture.getCost());
            jsonObject.put("description", Furniture.getDescription());
            jsonArray.put(jsonObject);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(jsonArray.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
