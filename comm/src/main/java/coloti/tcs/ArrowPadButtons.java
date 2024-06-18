package coloti.tcs;

import java.awt.Color;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeListener;

public class ArrowPadButtons extends JFrame{ //  implements KeyListener  implements ButtonModel
  
  Timer timer, timer2;
  JButton buttonUP;
  JButton buttonDOWN;
  JButton buttonLEFT;
  JButton buttonRIGHT;

  ArrowPadButtons() {
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setSize(500, 500);
    this.setLayout(null);

    buttonUP = new JButton("UP");
    buttonUP.setBounds(200, 100, 100, 100);
    this.add(buttonUP);

    buttonDOWN = new JButton("DOWN");
    buttonDOWN.setBounds(200, 300, 100, 100);
    this.add(buttonDOWN);

    buttonLEFT = new JButton("LEFT");
    buttonLEFT.setBounds(100, 200, 100, 100);
    this.add(buttonLEFT);

    buttonRIGHT = new JButton("RIGHT");
    buttonRIGHT.setBounds(300, 200, 100, 100);
    this.add(buttonRIGHT);

    /* 
    buttonUP.addActionListener(new ActionListener(){  
      public void actionPerformed(ActionEvent e){  
                  System.out.println("UP pressed");  
              }  
          });  
    */

    ActionListener action = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
          System.out.println("Button is being held down");
      }
    };

    ActionListener action2 = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
          System.out.println("1");
      }
    };

    timer = new Timer(100, action);
    timer2 = new Timer(100, action2);
        
    buttonUP.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            timer.start();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            timer.stop();
        }
    });

    buttonDOWN.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            timer2.start();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
          timer2.stop();
          System.out.println("done");
        }
    });

    buttonLEFT.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            timer.start();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            timer.stop();
        }
    });

    buttonRIGHT.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
          timer.start();
      }

      @Override
      public void mouseReleased(MouseEvent e) {
          timer.stop();
      }
  });
    
  }




  public void ShowArrowPad(){
    this.setVisible(true);
  }
  

}