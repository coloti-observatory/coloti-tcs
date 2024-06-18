package coloti.tcs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    public static void main(String[] args) {
        TestGui tg = new TestGui();

        ArrowPadFrame apframe = new ArrowPadFrame();
        apframe.SetButtonUP(tg.actionstart, tg.actionstop);
        apframe.SetButtonDOWN(tg.actionstart, tg.actionstop);
        apframe.SetButtonLEFT(tg.actionstart, tg.actionstop);
        apframe.SetButtonRIGHT(tg.actionstart, tg.actionstop);
        apframe.setSlowSpeed(tg.actionspeed);
        apframe.setMediumSpeed(tg.actionspeed);
        apframe.setFastSpeed(tg.actionspeed);
        apframe.Show();


        System.out.println("fine");





    }
}
