package com.samjakob.dwendyscript.ide;

import io.github.elliotdewdney.dwendyscript.SimpleReversePolish;
import javafx.scene.web.WebView;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;

public class RuntimeBridge {

    private static WebView webView;

    public RuntimeBridge(WebView webView){
        RuntimeBridge.webView = webView;
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
        pipe.DoubleOut = (Double i) -> webView.getEngine().executeScript("Application.addOutput(`" + i + "`)");
        pipe.IntOut = (int i) -> webView.getEngine().executeScript("Application.addOutput(`" + i + "`)");
        pipe.StringOut = (String i) -> webView.getEngine().executeScript("Application.addOutput(`" + i + "`)");
        pipe.Message = (String i) -> webView.getEngine().executeScript("Application.addOutput(`" + i + "`, 'red')");
        /*pipe.Wait = () -> {
            //AddToProgramOutput("Program Waiting for user (Submit to continue)", new Color(50, 120, 20));
            //WaitOnButton();
        };
        pipe.StringIn = () -> {
            AddToProgramOutput("Program Requesting String Input ... ");
            return WaitOnButton();
        };
        pipe.DoubleIn = () -> {
            AddToProgramOutput("Program Requesting Numerical Input ... ");
            String temp = WaitOnButton();
            return temp == null ? 0.0 : Double.parseDouble(temp);
        };*/

        double answer = 0;
        answer = new SimpleReversePolish().AdvancedCalculation(new ArrayDeque<>(Arrays.asList(program.replaceAll("#.+#", "").split("\\s+"))), answer, debug, pipe);

        RuntimeBridge.$executeScript("Application.terminate(" + answer + ")");
    }

    public String getSamplePrograms(){
        List<String> samplePrograms = new ArrayList<>();

        for(SimpleReversePolish.programs program : SimpleReversePolish.programs.values()) {
            samplePrograms.add("\"" + program.name() + "\"");
        }

        return Base64.getEncoder().encodeToString(samplePrograms.toString().getBytes(StandardCharsets.UTF_8));
    }

    public String loadSampleProgram(String name){
        for(SimpleReversePolish.programs program : SimpleReversePolish.programs.values()) {
            if(program.name().equals(name)) {
                return program.program;
            }
        }

        return null;
    }

    private static void $executeScript(String script) {
        webView.getEngine().executeScript(script);
    }

}
