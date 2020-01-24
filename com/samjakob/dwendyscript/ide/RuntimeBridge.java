package com.samjakob.dwendyscript.ide;

import io.github.elliotdewdney.dwendyscript.SimpleReversePolish;
import javafx.scene.web.WebView;

import java.util.ArrayDeque;
import java.util.Arrays;

public class RuntimeBridge {

    private WebView webView;

    public RuntimeBridge(WebView webView){
        this.webView = webView;
    }

    public void reloadApplication(){
        System.out.println("Application reloading...");
        webView.getEngine().reload();
    }

    public void execute(String program) {
        execute(program, false);
    }

    public void execute(String program, boolean debug){
        SimpleReversePolish.Pipe pipe = new SimpleReversePolish.Pipe();

        double answer = 0;
        answer = new SimpleReversePolish().AdvancedCalculation(new ArrayDeque<>(Arrays.asList(program.replaceAll("#.+#", "").split("\\s+"))), answer, debug, pipe);

        webView.getEngine().executeScript("Application.terminate(" + answer + ")");
    }

}
