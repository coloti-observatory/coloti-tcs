package coloti.tcs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

public class TestGui {

    public void print(String string){
        System.out.println(string);
    }


    ActionListener actionstart = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            print("Moving...");
        }
    };

    ActionListener actionstop = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            print("done.");
        }
    };

    ActionListener actionspeed = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Speed set");
        }
    };


    ActionListener actionTarget = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Target set");
        }
    };

    public static void main(String[] args) {
        TestGui tg = new TestGui();



        JFrame parentframe = new JFrame("Parent Frame");
        parentframe.setSize(400, 300);
        parentframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        parentframe.setVisible(true);

       
        ArrowPadFrame apframe = new ArrowPadFrame(parentframe);
        apframe.SetButtonTarget(tg.actionTarget);
        apframe.SetButtonHome(tg.actionstart);
        apframe.SetButtonUP(tg.actionstart, tg.actionstop);
        apframe.SetButtonDOWN(tg.actionstart, tg.actionstop);
        apframe.SetButtonLEFT(tg.actionstart, tg.actionstop);
        apframe.SetButtonRIGHT(tg.actionstart, tg.actionstop);
        apframe.SetSlowSpeed(tg.actionspeed);
        apframe.SetMediumSpeed(tg.actionspeed);
        apframe.SetFastSpeed(tg.actionspeed);
        apframe.SetButtonSTOP(tg.actionstop);
        apframe.Show();


        System.out.println("fine");





    }
}
