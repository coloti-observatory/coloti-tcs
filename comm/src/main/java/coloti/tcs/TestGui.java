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
            print("Going up...");
        }
    };

    ActionListener actionstop = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            print("done.");
        }
    };

    public static void main(String[] args) {
        TestGui tg = new TestGui();

        ArrowPadFrame apframe = new ArrowPadFrame();
        apframe.SetButtonUP(tg.actionstart, tg.actionstop);
        apframe.SetButtonDOWN(tg.actionstart, tg.actionstop);
        apframe.SetButtonLEFT(tg.actionstart, tg.actionstop);
        apframe.SetButtonRIGHT(tg.actionstart, tg.actionstop);
        apframe.Show();






    }
}
