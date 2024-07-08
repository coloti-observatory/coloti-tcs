package coloti.tcs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class tcsGui  extends JFrame{

    private JButton showdialog;
    private ArrowPadFrame padframe;

    public TCS tcs;

    public tcsGui(TCS tcs){
        
        initComponents(tcs);
        showGui();

    }


    private void initComponents(TCS tcs) {
        setTitle("Esempio");
        padframe = new ArrowPadFrame(this, tcs);
        showdialog = new JButton("show");

        showdialog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("----");
                padframe.Show();
            }
        });

        getContentPane().add(showdialog);




    }

    public void showGui(){
        pack();
        setVisible(true);
    }


    public static void main(String[] argc){
        /* 
        tcsGui gui = new tcsGui();

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                gui.showGui();
            }
            
            
        });
        */

    }
}
