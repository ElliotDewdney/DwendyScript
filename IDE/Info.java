package IDE;

import javax.swing.*;
import java.awt.event.*;
import java.util.Base64;

public class Info extends JDialog {
    private JPanel contentPane;
    private JButton buttonCancel;
    private JList Ops;
    private JButton addButton;
    private JList Func;
    public static final String ICON = "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";
    public static WAITFOR Waiting = new WAITFOR();

    static class WAITFOR{
        String output;
        public synchronized String GetOutput(){
            while(output == null) {
                try {
                    this.wait();
                } catch (InterruptedException e) { System.out.println("Waiting for info");}
            }
            return output;
        }
        public synchronized void SendOutput(String output){
            this.output = output;
            this.notify();
        }
    }

    public Info() {
        try {
            System.out.println();
            setIconImage(new ImageIcon(Base64.getDecoder().decode(ICON)).getImage());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Icon not Found");
        }
        setTitle("Add Instruction ... ");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonCancel);
        Ops.setListData(SimpleReversePolish.Operations.values());
        Func.setListData(SimpleReversePolish.Functions.values());

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onAdd();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onCancel() {
        Waiting.SendOutput("");
        dispose();
    }

    private void onAdd() {
        final String[] out = {""};
        Ops.getSelectedValuesList().forEach((temp) -> out[0] = out[0].concat(" " + ((SimpleReversePolish.action) temp).getDefaultSymbol()));
        Func.getSelectedValuesList().forEach((temp) -> out[0] = out[0].concat(" " + ((SimpleReversePolish.action) temp).getDefaultSymbol()));
        Waiting.SendOutput(out[0]);
        dispose();
    }

    public static String Show() {
        Info dialog = new Info();
        dialog.pack();
        dialog.setVisible(true);
        return Waiting.GetOutput();
    }
}
