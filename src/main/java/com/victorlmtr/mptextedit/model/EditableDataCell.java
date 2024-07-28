package com.victorlmtr.mptextedit.model;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

public class EditableDataCell extends TableCell<HexData, String> {
    private TextField textField;

    @Override
    public void startEdit() {
        if (!isEmpty() && getTableRow().getItem().isEditable()) {
            super.startEdit();
            createTextField();
            setText(null);
            setGraphic(textField);
            textField.selectAll();
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(getItem());
        setGraphic(null);
    }

    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (textField != null) {
                    textField.setText(getString());
                }
                setText(null);
                setGraphic(textField);
            } else {
                setText(getString());
                setGraphic(null);
                if (!getTableRow().getItem().isEditable()) {
                    setStyle("-fx-background-color: #CCCCCC;"); // Set text color to gray for non-editable cells
                } else {
                    setStyle(""); // Reset style for editable cells
                }
            }
        }
    }

    private void createTextField() {
        textField = new TextField(getString());
        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
                if (!arg2) {
                    commitEdit(textField.getText());
                }
            }
        });
        textField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                commitEdit(textField.getText());
            }
        });
        // Limit the number of characters that can be entered

        HexData hexData = getTableView().getItems().get(getIndex());
        String originalHex = hexData.getHex();
        String[] originalHexBytes = originalHex.split(" ");
        int originalLength = originalHexBytes.length;

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.length() > originalLength) {
                change.setText(change.getControlNewText().substring(0, originalLength));
            }
            return change;
        };
        TextFormatter<String> formatter = new TextFormatter<>(filter);
        textField.setTextFormatter(formatter);
    }

    private String getString() {
        return getItem() == null ? "" : getItem();
    }

    @Override
    public void commitEdit(String newValue) {
        HexData hexData = getTableView().getItems().get(getIndex());
        String originalHex = hexData.getHex();
        String[] originalHexBytes = originalHex.split(" ");
        int originalLength = originalHexBytes.length;

        String newHex = "";
        int newLength = 0;
        for (char c : newValue.toCharArray()) {
            if (HexFileUtils.marioPartyMapping.containsKey(c)) {
                newHex += String.format("%02X ", HexFileUtils.marioPartyMapping.get(c).byteValue());
                newLength += 2;
            } else {
                // Handle invalid character
                System.out.println("Invalid character: " + c);
                return;
            }
        }

        // Check if new data is too long
        if (newLength > originalLength * 2) {
            // Revert to original data
            textField.setText(getString());
            return;
        }

        // Null padding if shorter
        while (newHex.split(" ").length < originalLength) {
            newHex += "00 ";
        }

        hexData.setData(newValue);
        hexData.setHex(newHex.trim());

        super.commitEdit(newValue);
    }
}