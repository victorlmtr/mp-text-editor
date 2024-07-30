package com.victorlmtr.mptextedit;

import com.victorlmtr.mptextedit.model.HexFileUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class EditCharNamesScreen {

    private Stage stage;
    private File boardFile;
    private File miniFile;

    private ListView<String> characterListView;
    private ObservableList<String> characterList;
    private TextField newNameField;

    public void show(Stage primaryStage) {
        this.stage = primaryStage;

        VBox root = new VBox();
        root.setSpacing(10);

        // Create a list of editable characters
        characterList = FXCollections.observableArrayList(HexFileUtils.Mp4PlayableCharactersMapping.keySet());
        characterListView = new ListView<>(characterList);

        // Display the maximum number of bytes/characters
        Label maxLengthLabel = new Label("Max Length: ");
        newNameField = new TextField();
        newNameField.setPromptText("Enter new name");

        characterListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                int maxLength = getMaxLength(newVal);
                maxLengthLabel.setText("Max Length: " + maxLength);
                newNameField.setText(hexToText(HexFileUtils.Mp4PlayableCharactersMapping.get(newVal)).trim()); // Show current name
            }
        });

        Button saveButton = new Button("Save Changes");
        saveButton.setOnAction(event -> saveChanges());

        Button loadFolderButton = new Button("Load Folder");
        loadFolderButton.setOnAction(event -> loadFilesFromFolder());

        Button saveFilesButton = new Button("Save Files");
        saveFilesButton.setOnAction(event -> saveFiles());

        root.getChildren().addAll(new Label("Select a character:"), characterListView, maxLengthLabel, newNameField, saveButton, loadFolderButton, saveFilesButton);

        Scene scene = new Scene(root, 400, 300);
        Stage editStage = new Stage();
        editStage.setTitle("Edit Character Names");
        editStage.setScene(scene);
        editStage.show();
    }

    private int getMaxLength(String character) {
        String originalHex = HexFileUtils.Mp4PlayableCharactersMapping.get(character).replaceAll(" ", "");
        return originalHex.length() / 2;
    }

    private void saveChanges() {
        String selectedCharacter = characterListView.getSelectionModel().getSelectedItem();
        if (selectedCharacter != null && !newNameField.getText().trim().isEmpty()) {
            String newName = newNameField.getText().trim();
            int maxLength = getMaxLength(selectedCharacter);

            if (newName.length() > maxLength) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error: New name exceeds the maximum allowed length of " + maxLength + " characters.");
                alert.showAndWait();
            } else {
                String newHex = convertToHex(newName);

                // Update the dictionary with the new hex value
                HexFileUtils.Mp4PlayableCharactersMapping.put(selectedCharacter, newHex);

                // Update the ListView
                characterListView.getItems().set(characterListView.getSelectionModel().getSelectedIndex(), selectedCharacter);

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Character name updated successfully!");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a character and enter a new name.");
            alert.showAndWait();
        }
    }

    private String convertToHex(String text) {
        StringBuilder hex = new StringBuilder();
        for (char c : text.toCharArray()) {
            Integer hexValue = HexFileUtils.marioPartyMapping.get(c);
            if (hexValue != null) {
                hex.append(String.format("%02X", hexValue));
            } else {
                hex.append("00"); // Default padding for unmapped characters
            }
        }
        // Padding with 0x00 if necessary
        int length = hex.length();
        int targetLength = getMaxLength(text); // Adjust this according to your needs
        while (length < targetLength * 2) {
            hex.append("00");
            length += 2;
        }
        return hex.toString();
    }

    private void loadFilesFromFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Folder");
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            File boardFileCandidate = new File(selectedDirectory, "board_f.dat");
            File miniFileCandidate = new File(selectedDirectory, "mini_f.dat");

            if (boardFileCandidate.exists() && miniFileCandidate.exists()) {
                boardFile = boardFileCandidate;
                miniFile = miniFileCandidate;
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Files loaded successfully!");
                alert.showAndWait();
                // You can now use boardFile and miniFile as needed
                try {
                    // Example: Read file contents (if needed)
                    byte[] boardFileContent = Files.readAllBytes(boardFile.toPath());
                    byte[] miniFileContent = Files.readAllBytes(miniFile.toPath());
                    // Process files as needed
                } catch (IOException e) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Error reading file contents: " + e.getMessage());
                    errorAlert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "The required files (board_f.dat and mini_f.dat) were not found in the selected folder.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "No folder was selected.");
            alert.showAndWait();
        }
    }

    private void saveFiles() {
        if (boardFile == null || miniFile == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please load the files before saving.");
            alert.showAndWait();
            return;
        }

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Output Folder");
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            File outputDir = new File(selectedDirectory, "modded");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            try {
                // Read file contents
                byte[] boardFileContent = Files.readAllBytes(boardFile.toPath());
                byte[] miniFileContent = Files.readAllBytes(miniFile.toPath());

                // Replace character names
                String boardHex = bytesToHex(boardFileContent);
                String miniHex = bytesToHex(miniFileContent);

                for (Map.Entry<String, String> entry : HexFileUtils.Mp4PlayableCharactersMapping.entrySet()) {
                    String oldHex = entry.getValue().replaceAll(" ", "");
                    String newHex = convertToHex(entry.getKey());

                    // Use original hex if new hex is empty or invalid
                    if (newHex.isEmpty()) {
                        newHex = oldHex;
                    }

                    boardHex = boardHex.replace(oldHex, newHex);
                    miniHex = miniHex.replace(oldHex, newHex);
                }

                // Save modified files
                Files.write(Paths.get(outputDir.toPath().toString(), "board_f.dat"), hexToBytes(boardHex));
                Files.write(Paths.get(outputDir.toPath().toString(), "mini_f.dat"), hexToBytes(miniHex));

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Files saved successfully in " + outputDir.getAbsolutePath());
                alert.showAndWait();
            } catch (IOException e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Error processing files: " + e.getMessage());
                errorAlert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "No output folder was selected.");
            alert.showAndWait();
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02X", b));
        }
        return hexString.toString();
    }

    private byte[] hexToBytes(String hexString) {
        int length = hexString.length();
        byte[] bytes = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return bytes;
    }

    private String hexToText(String hex) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < hex.length(); i += 2) {
            String str = hex.substring(i, i + 2);
            char ch = (char) Integer.parseInt(str, 16);
            text.append(ch);
        }
        return text.toString();
    }
}
