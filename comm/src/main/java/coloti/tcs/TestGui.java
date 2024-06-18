package coloti.tcs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TestGui {

    public void print(String string){
        System.out.println(string);
    }


    ActionListener action = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            print("Going up...");
        }
    };

    public static void main(String[] args) {
        TestGui tg = new TestGui();

        ArrowPadFrame apframe = new ArrowPadFrame();
        apframe.SetButtonUP(tg.action);
        apframe.Show();






    }
}
