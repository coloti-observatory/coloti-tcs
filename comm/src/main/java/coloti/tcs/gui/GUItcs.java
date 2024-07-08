package coloti.tcs.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import coloti.tcs.ArrowPadFrame;
import coloti.tcs.TCS;

public class GUItcs  extends JFrame{

    private JButton showdialog;
    private FramePaddle padframe;

    public GUItcs(TCS tcs){
        initComponents(tcs);
    }

    private void initComponents(TCS tcs) {
        padframe = new FramePaddle(this, tcs);
        showdialog = new JButton("show");

        
        //this.showdialog.setBounds(0, 0, 100, 50);
        //this.showdialog.setBackground(Color.green);

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

    /*  MAIN
    public static void main(String[] argc){
        
        GUItcs gui = new GUItcs();

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                gui.showGui();
            }
            
            
        });
    }
    */

    
}
