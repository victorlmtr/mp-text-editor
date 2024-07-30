package com.victorlmtr.mptextedit;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import java.awt.*;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MpTextEditApp extends Application {

	private ConfigurableApplicationContext springContext;
	private BatchEditScreen batchEditScreen;
	private EditCharNamesScreen editCharNamesScreen;

	public static void main(String[] args) {
		launch(MpTextEditApp.class, args);
	}

	@Override
	public void init() {
		springContext = SpringApplication.run(MpTextEditApp.class);
		batchEditScreen = new BatchEditScreen();
		editCharNamesScreen = new EditCharNamesScreen();
	}

	@Override
	public void start(Stage primaryStage) {
		VBox root = new VBox();
		Button batchEditButton = new Button("Batch edit DAT file");
		batchEditButton.setOnAction(event -> batchEditScreen.show(primaryStage));
		Button editCharNamesButton = new Button("Edit character names");
		editCharNamesButton.setOnAction(event -> editCharNamesScreen.show(primaryStage));
		root.getChildren().addAll(batchEditButton, editCharNamesButton);
		Scene scene = new Scene(root, 1600, 900);
		primaryStage.setTitle("Mario Party Text Editor");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	@Override
	public void stop() {
		springContext.close();
	}
}