package org.example.persistence.Repositories;

import lombok.Getter;
import lombok.Setter;
import org.example.api.Dto.FurnitureDTO;

import java.util.*;

@Getter
@Setter
public abstract class AbstractStorage<T> {
    //CRUD
    protected List<T> listStorage=new ArrayList<T>();
    protected Map<Integer,T> mapStorage=new HashMap<Integer,T>();
    //R
    public abstract void readFromFile(String filename);
    //U
    public abstract void writeToFile(String filename);
    public abstract List<FurnitureDTO> readFromXml(String filename);
    public abstract void writeToXml(String filename, List<FurnitureDTO> cakes);
    public abstract void writeDataToJsonFile(String filename,List<FurnitureDTO> cakes);
    public abstract List<FurnitureDTO> readDataFromJsonFile(String filename);
    //A
    //C/U
    public void addToListStorage(T item) {
        listStorage.add(item);
    }
    //D
    public void removeFromListStorage(T item) {
        listStorage.remove(item);
    }
    //C2/U2
    public void addToMapStorage(Integer itemId, T item) {
        mapStorage.put(itemId, item);
    }
    //D2
    public void removeFromMapStorage(Long itemId) {
        mapStorage.remove(itemId);
    }
    //findById
    public T getFromMapStorage(Long itemId) {
        return mapStorage.get(itemId);
    }
    //findById2
    public T getFromListStorage(Long itemId) {
        return listStorage.get(Math.toIntExact(itemId));
    }
    public abstract T findByName(String name);

    public List<T> getList() {
        return listStorage;
    }
}
