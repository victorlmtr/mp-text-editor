package com.victorlmtr.mptextedit;

import com.victorlmtr.mptextedit.model.EditableDataCell;
import com.victorlmtr.mptextedit.model.HexData;
import com.victorlmtr.mptextedit.model.HexFileUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class BatchEditScreen {

    private TableView<HexData> tableView;

    public void show(Stage stage) {
        VBox root = new VBox();
        Button loadFileButton = new Button("Load file");
        Button saveFileButton = new Button("Save file");

        tableView = new TableView<>();
        TableColumn<HexData, String> hexColumn = new TableColumn<>("Hex Bytes");
        hexColumn.setCellValueFactory(new PropertyValueFactory<>("hex"));

        TableColumn<HexData, String> dataColumn = new TableColumn<>("Data");
        dataColumn.setCellValueFactory(new PropertyValueFactory<>("data"));
        dataColumn.setCellFactory(new Callback<TableColumn<HexData, String>, TableCell<HexData, String>>() {
            @Override
            public TableCell<HexData, String> call(TableColumn<HexData, String> param) {
                return new EditableDataCell();
            }
        });

        tableView.getColumns().add(hexColumn);
        tableView.getColumns().add(dataColumn);

        // Enable table editing
        tableView.setEditable(true);

        loadFileButton.setOnAction(event -> loadFile(stage));
        saveFileButton.setOnAction(event -> saveFile(stage));

        root.getChildren().addAll(loadFileButton, saveFileButton, tableView);
        Scene scene = new Scene(root, 1600, 900);
        stage.setScene(scene);
        stage.show();
    }


    private void loadFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("DAT Files", "*.dat"));
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            Task<List<HexData>> task = new Task<List<HexData>>() {
                @Override
                protected List<HexData> call() throws IOException {
                    byte[] fileContent = Files.readAllBytes(file.toPath());
                    return parseFileContent(fileContent);
                }

                @Override
                protected void succeeded() {
                    List<HexData> hexDataList = getValue();
                    tableView.getItems().setAll(hexDataList);
                }

                @Override
                protected void failed() {
                    getException().printStackTrace();
                }
            };

            new Thread(task).start();
        }
    }

    private List<HexData> parseFileContent(byte[] fileContent) {
        List<HexData> hexDataList = new ArrayList<>();
        StringBuilder hexBuilder = new StringBuilder();
        StringBuilder dataBuilder = new StringBuilder();
        boolean isInEditableSegment = false;

        for (int i = 0; i < fileContent.length; i++) {
            byte b = fileContent[i];
            String hex = String.format("%02X", b);
            char mappedChar = HexFileUtils.getCharacterFromByte(b);

            if (b == 0x0B) { // Segment separator (non-editable)
                if (hexBuilder.length() > 0) {
                    hexDataList.add(new HexData(hexBuilder.toString().trim(), dataBuilder.toString().trim(), 0, isInEditableSegment));
                    hexBuilder.setLength(0);
                    dataBuilder.setLength(0);
                    isInEditableSegment = false;
                }
                // Add the separator line itself as non-editable
                hexDataList.add(new HexData(hex, "|", 0, false));
            } else if (b == 0x00) { // Null byte
                if (isInEditableSegment) {
                    hexBuilder.append(hex).append(" ");
                    dataBuilder.append("_");
                } else {
                    hexBuilder.append(hex).append(" ");
                    dataBuilder.append("_");
                }
            } else if (HexFileUtils.marioPartyMapping.containsKey(mappedChar)) { // Editable character
                if (!isInEditableSegment) {
                    if (hexBuilder.length() > 0) {
                        hexDataList.add(new HexData(hexBuilder.toString().trim(), dataBuilder.toString().trim(), 0, false));
                        hexBuilder.setLength(0);
                        dataBuilder.setLength(0);
                    }
                    isInEditableSegment = true;
                }
                hexBuilder.append(hex).append(" ");
                dataBuilder.append(mappedChar);
            } else { // Non-editable character
                if (isInEditableSegment) {
                    hexDataList.add(new HexData(hexBuilder.toString().trim(), dataBuilder.toString().trim(), 0, true));
                    hexBuilder.setLength(0);
                    dataBuilder.setLength(0);
                    isInEditableSegment = false;
                }
                hexBuilder.append(hex).append(" ");
                dataBuilder.append(mappedChar);
            }
        }
        // Add the last segment if any
        if (hexBuilder.length() > 0) {
            hexDataList.add(new HexData(hexBuilder.toString().trim(), dataBuilder.toString().trim(), 0, isInEditableSegment));
        }
        return hexDataList;
    }

    private void saveFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("DAT Files", "*.dat"));
        File file = fileChooser.showSaveDialog(stage);
        if (file!= null) {
            List<HexData> hexDataList = tableView.getItems();
            byte[] fileContent = new byte[0];
            try {
                fileContent = hexDataListToByteArray(hexDataList);
                Files.write(file.toPath(), fileContent);
                System.out.println("File saved successfully.");
            } catch (IOException e) {
                System.err.println("Error saving file: " + e.getMessage());
            }
        }
    }

    private byte[] hexDataListToByteArray(List<HexData> hexDataList) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        for (HexData hexData : hexDataList) {
            String hex = hexData.getHex();
            String[] hexBytes = hex.split(" ");
            for (String hexByte : hexBytes) {
                bos.write((byte) Integer.parseInt(hexByte, 16));
            }
        }
        return bos.toByteArray();
    }
}
