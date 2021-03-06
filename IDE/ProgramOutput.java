package IDE;

import jdk.nashorn.internal.runtime.regexp.RegExp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Base64;
import java.util.Stack;
import java.util.regex.Pattern;

public class ProgramOutput extends JDialog {
    public static double answer = 0;
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextField input;
    private JLabel programOutput;
    private JPanel Pane;
    private final int TEXTSIZE = 15;
    private Stack<String> inputs= new Stack<>();
    public static final String ICON = "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";

    public ProgramOutput(String program, boolean debug) {
        try {
            System.out.println();
            setIconImage(new ImageIcon(Base64.getDecoder().decode(ICON)).getImage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Icon not Found");
        }
        setTitle("Output from program");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        Pane.setLayout(new BoxLayout(Pane, BoxLayout.Y_AXIS));

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SendCode();
            }
        });

        SimpleReversePolish.Pipe pipe = new SimpleReversePolish.Pipe();
        pipe.DoubleOut = (Double i) -> AddToProgramOutput("Numerical Output : "+ i);
        pipe.IntOut = (int i) -> AddToProgramOutput("Integer Output : " + i);
        pipe.StringOut = (String i) -> AddToProgramOutput("Text Output : " + i);
        pipe.Message = (String i) -> AddToProgramOutput("Message from Interpreter : "+i, Color.RED);
        pipe.Wait = () -> {
            AddToProgramOutput("Program Waiting for user (Submit to continue)", new Color(50, 120, 20));
            WaitOnButton();
        };
        pipe.StringIn = () -> {
            AddToProgramOutput("Program Requesting String Input ... ");
            return WaitOnButton();
        };
        pipe.DoubleIn = () -> {
            AddToProgramOutput("Program Requesting Numerical Input ... ");
            String temp = WaitOnButton();
            return temp == null ? 0.0 : Double.parseDouble(temp);
        };
        pipe.SocketGetter = new SimpleReversePolish.GetSocket() {
            @Override
            public Socket getClientSocket() throws IOException {
                return new Socket(UserPrompt.Show("Input Ip Address for Socket"), 6969);
            }

            @Override
            public ServerSocket getSeverSocket() throws IOException {
                return new ServerSocket(6969);
            }
        };
        String out = program.replaceAll("\\s+" , " ");
        System.out.println("Running Program : " + out);
        AddToProgramOutput("Running Program : " + (out.length() >= 24 ?out.substring(0, 20) + " ..." : out ), Color.BLUE);
        Thread calculation = new Thread(() -> {
            answer = new SimpleReversePolish().AdvancedCalculation(new ArrayDeque<>(Arrays.asList(program.replaceAll("#.+#", "").split("\\s+"))),answer,debug, pipe);
            if(answer != 0)AddToProgramOutput("Result of program = " + answer, Color.BLUE);
            else AddToProgramOutput("Program Ran Successfully", Color.BLUE);
            WaitOnButton();
            dispose();
        });
        calculation.start();
    }

    public String WaitOnButton(){
        System.out.println("Waiting On Button");
        synchronized (inputs) {
            while (inputs.empty()) {
                System.out.println("Checking input");
                try {
                    inputs.wait();
                } catch (InterruptedException e) {
                    System.out.println("Wait Failed");
                }
            }
            System.out.println("input taken of " + inputs.peek());
            return inputs.pop();
        }
    }

    private void AddToProgramOutput(String data){AddToProgramOutput(data, Color.BLACK);}
    private void AddToProgramOutput(String data, Color color){
        SwingUtilities.invokeLater(() -> {
            JLabel label = new JLabel(data);
            label.setFont(new Font(Font.MONOSPACED, Font.PLAIN, TEXTSIZE));
            label.setForeground(color);
            Pane.add(label);
            System.out.println("created pane");
            Pane.validate();
            validate();
        });
    }

    private void SendCode() {
        synchronized (inputs) {
            inputs.push(input.getText());
            input.setText("");
            inputs.notify();
        }
    }
}
