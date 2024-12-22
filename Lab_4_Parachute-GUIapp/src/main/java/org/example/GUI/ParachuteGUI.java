package org.example.GUI;

import org.example.api.Dto.ParachuteDTO;
import org.example.api.Factory.ParachuteFactory;
import org.example.api.Misc.Archiver;
import org.example.persistence.Repositories.AbstractStorage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Comparator;

public class ParachuteGUI {

    private AbstractStorage<ParachuteDTO> storage;
    private JFrame frame;
    private JTextField costField, nameField, descriptionField;
    private JTable table;
    private DefaultTableModel tableModel;

    public ParachuteGUI() {
        storage = ParachuteFactory.getInstance();
    }

    public void createAndShowGUI() {
        frame = new JFrame("Мэнэджер завода");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 750);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(40, 1));



        inputPanel.add(new JLabel("Название:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Стоимость:"));
        costField = new JTextField();
        inputPanel.add(costField);

        inputPanel.add(new JLabel("Текст описания:"));
        descriptionField = new JTextField();
        inputPanel.add(descriptionField);

        JButton addButton = new JButton("добавить мебель");
        addButton.addActionListener(new AddButtonListener());

        inputPanel.add(addButton);

        tableModel = new DefaultTableModel(new Object[] {"Название", "Стоимость", "Текст описания"}, 0);
        table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);

        JPanel Panel = new JPanel();
        JButton readButton = new JButton("Открыть");
        readButton.addActionListener(new ReadButtonListener());

        JButton writeButton = new JButton("Сохранить");
        writeButton.addActionListener(new WriteButtonListener());

        Panel.add(readButton);
        Panel.add(writeButton);

        JButton sortButton = new JButton("Сортировать");
        sortButton.addActionListener(new SortButtonListener());

        Panel.add(sortButton);

        JButton archiveButton = new JButton("Создать Архив");
        archiveButton.addActionListener(new ArchiveButtonListener());

        Panel.add(archiveButton);

        frame.setLayout(new BorderLayout());
        frame.add(inputPanel, BorderLayout.WEST);
        frame.add(tableScrollPane, BorderLayout.CENTER);
        frame.add(Panel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private class AddButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String cost = costField.getText();
            String name = nameField.getText();
            String description = descriptionField.getText();

            if (!cost.isEmpty() && !name.isEmpty() && !description.isEmpty()) {
                try {
                    int costInt = Integer.parseInt(cost);
                    ParachuteDTO parachute = new ParachuteDTO(name, costInt,description);

                    boolean isDuplicate = storage.getList().stream()
                            .anyMatch(p -> p.getName().equalsIgnoreCase(name));

                    if (isDuplicate) {
                        JOptionPane.showMessageDialog(frame, "Мебель с таким названием уже существует", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    } else {
                        storage.addToListStorage(parachute);
                        storage.addToMapStorage(costInt, parachute);

                        tableModel.addRow(new Object[] {parachute.getName(), parachute.getCost(), parachute.getDescription()});
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Неверный формат стоимости", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Не все поля заполнены!", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class ReadButtonListener implements ActionListener {
        @Override

        public void actionPerformed(ActionEvent e) {
            String[] options = {"furniture.txt", "furniture.xml", "furniture.json"};
            String fileType = (String) JOptionPane.showInputDialog(frame,
                    "Выберете файл для чтения", "Выбрать",
                    JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

            if (fileType != null) {
                new Thread(() -> {
                    try {
                        tableModel.setRowCount(0);
                        storage.getList().clear();

                        switch (fileType) {
                            case "furniture.txt":
                                storage.readFromFile(fileType);
                                break;
                            case "furniture.xml":
                                storage.setListStorage(storage.readFromXml(fileType));
                                break;
                            case "furniture.json":
                                storage.setListStorage(storage.readDataFromJsonFile(fileType));
                                break;
                            default:
                                throw new IOException("Файл не поддерживается");
                        }

                        updateTable();

                        JOptionPane.showMessageDialog(frame, "Загружено из" + fileType,
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(frame, "Ошибка чтения файла: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }).start();
            }
        }
    }

    private class WriteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            new Thread(() -> {
                Thread txtWriter = new Thread(() -> storage.writeToFile("furniture.txt"));
                Thread xmlWriter = new Thread(() -> storage.writeToXml("furniture.xml", storage.getList()));
                Thread jsonWriter = new Thread(() -> storage.writeDataToJsonFile("furniture.json", storage.getList()));

                txtWriter.start();
                xmlWriter.start();
                jsonWriter.start();

                try {
                    txtWriter.join();
                    xmlWriter.join();
                    jsonWriter.join();
                } catch (InterruptedException ex) {
                    JOptionPane.showMessageDialog(frame, "Ошибка записи файла: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JOptionPane.showMessageDialog(frame, "Запись завершена", "Success", JOptionPane.INFORMATION_MESSAGE);
            }).start();
        }
    }

    private class SortButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String[] options = {"Стоимость", "Название", "Описание"};
            String field = (String) JOptionPane.showInputDialog(frame,
                    "Сортировка", "Сортировать по:",
                    JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

            if (field != null) {
                new Thread(() -> {
                    switch (field) {
                        case "Стоимость":
                            storage.getList().sort(Comparator.comparingInt(ParachuteDTO::getCost));
                            break;
                        case "Название":
                            storage.getList().sort(Comparator.comparing(ParachuteDTO::getName));
                            break;
                        case "Описание":
                            storage.getList().sort(Comparator.comparing(ParachuteDTO::getDescription));
                            break;
                    }
                    updateTable();
                }).start();
            }
        }
    }

    private class ArchiveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Archiver archiver = new Archiver();
            String[] files = {"furniture.txt", "furniture.json", "furniture.xml"};

            new Thread(() -> {
                try {
                    archiver.createZipArchive("FurnitureArchive.zip", files);
                    archiver.createJarArchive("FurnitureArchive.jar", files);
                    JOptionPane.showMessageDialog(frame, "Архивы созданы", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }).start();
        }
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (ParachuteDTO parachute : storage.getList()) {
            tableModel.addRow(new Object[] {parachute.getName(), parachute.getCost(), parachute.getDescription()});
        }
    }
}
