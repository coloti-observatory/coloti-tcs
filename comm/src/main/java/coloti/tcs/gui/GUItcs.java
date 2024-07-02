package coloti.tcs.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import coloti.tcs.ArrowPadFrame;

public class GUItcs  extends JFrame{

    
    private ArrowPadFrame padframe;

    public GUItcs(){
        initComponents();
    }

    private void initComponents() {
        padframe = new ArrowPadFrame(this);
    }

    public void showGui(){
        pack();
        setVisible(true);
        padframe.Show();
    }


    public static void main(String[] argc){
        GUItcs gui = new GUItcs();

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                gui.showGui();
            }
            
            
        });

    }
}
