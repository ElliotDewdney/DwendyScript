package com.samjakob.dwendyscript.ide;

import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public class dSIDE extends Application {

    private RuntimeBridge runtimeBridge;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("dSIDE");

        // Initialize WebView.
        WebView renderer = new WebView();

        renderer.getEngine().load(getInterfaceResourcePath("index.html"));
        renderer.setContextMenuEnabled(false);
        renderer.setFontSmoothingType(FontSmoothingType.GRAY);

        // Initialize RuntimeBridge
        runtimeBridge = new RuntimeBridge(renderer);

        // Inject RuntimeBridge
        renderer.getEngine().getLoadWorker().stateProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if(newValue != Worker.State.SUCCEEDED) return;

                    // Get the window instance from the renderer engine.
                    JSObject window = (JSObject) renderer.getEngine().executeScript("window");
                    window.setMember("runtimeBridge", runtimeBridge);
                }
        );

        // Set up JavaFX window and present it to the user.
        Scene scene = new Scene(renderer, 1280, 720);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Returns the path of a child of the interface directory as specified by file parameter.
     *
     * @param file The file - the path of which is needed.
     * @return The path in external form.
     */
    private String getInterfaceResourcePath(String file){
        return getClass().getResource("/com/samjakob/dwendyscript/ide/interface/" + file).toExternalForm();
    }

    /**
     * Reads an interface file from the JAR and returns the file contents.
     * This is for HTML, assets or scripts. The method resolves relative to
     * /com/samjakob/dwendyscript/ide/interface/ as a root path.
     *
     * @param file The path of the file, relative to /com/samjakob/dwendyscript/ide/interface/
     * @return The contents of the file.
     */
    private String readInterfaceFile(String file) {
        InputStream fileStream = getClass().getClassLoader().getResourceAsStream("com/samjakob/dwendyscript/ide/interface/" + file);
        if(fileStream == null) return null;

        BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream));
        List<String> lines = reader.lines().collect(Collectors.toList());

        return String.join("\n", lines);
    }
}
