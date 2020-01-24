package io.github.elliotdewdney.dwendyscript;

import javax.swing.*;
import java.awt.event.*;

public class UserPrompt extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JTextField Value;
    private JLabel Title;
    public static Info.WAITFOR Waiting = new Info.WAITFOR();

    public UserPrompt(String title) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle(title);
        Title.setText(title);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    private void onOK() {
        Waiting.SendOutput(Value.getText());
        dispose();
    }

    public static String Show(String title) {
        UserPrompt dialog = new UserPrompt(title);
        dialog.pack();
        dialog.setVisible(true);
        return Waiting.GetOutput();
    }
}
