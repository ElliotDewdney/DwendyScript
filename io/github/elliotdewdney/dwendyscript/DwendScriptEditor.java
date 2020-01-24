package io.github.elliotdewdney.dwendyscript;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Base64;

public class DwendScriptEditor extends JDialog {
    private JPanel contentPane;
    private JButton RunProgram;
    private JButton info;
    private JTextArea ProgramArea;
    private JCheckBox debug;
    private JComboBox ProgramLoader;
    public static final String ICON = "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";

    public DwendScriptEditor() {
        try {
            System.out.println();
            setIconImage(new ImageIcon(Base64.getDecoder().decode(ICON)).getImage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Icon not Found");
        }
        setTitle("Dwendy Script editor");
        setContentPane(contentPane);
        getRootPane().setDefaultButton(RunProgram);
        for(SimpleReversePolish.programs program : SimpleReversePolish.programs.values())ProgramLoader.addItem(program);
        ProgramArea.setFont(new Font(Font.MONOSPACED,Font.PLAIN, 18 ));
        ProgramLoader.addItemListener((item) -> {
            SimpleReversePolish.programs program = (SimpleReversePolish.programs) item.getItem();
            ProgramArea.setText(program.program);
        });

        RunProgram.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onRun();
            }
        });

        info.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ShowInfo();
            }
        });

    }

    private void onRun() {
        ProgramOutput dialog = new ProgramOutput(ProgramArea.getText().trim(), debug.isSelected());
        dialog.pack();
        dialog.setVisible(true);
    }

    private void ShowInfo() {
        ProgramArea.setText(ProgramArea.getText() + Info.Show());
    }

    public static void Show() {
        DwendScriptEditor dialog = new DwendScriptEditor();
        dialog.pack();
        dialog.setVisible(true);
    }
}
