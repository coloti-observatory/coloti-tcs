package coloti.tcs;

import java.awt.Color;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeListener;

public class ArrowPadFrame extends JFrame{ //  implements KeyListener  implements ButtonModel
  
  //Timer timerUP, timerDOWN, timerLEFT, timerRIGHT;
  JButton buttonUP;
  JButton buttonDOWN;
  JButton buttonLEFT;
  JButton buttonRIGHT;
  JButton buttonSTOP;

  ArrowPadFrame() {
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

    buttonSTOP = new JButton("STOP");
    buttonSTOP.setBounds(380, 400, 80, 30);
    buttonSTOP.setBackground(Color.RED);
    this.add(buttonSTOP);
    
  }

  //public void SetTimer(ActionListener action){
  //  this.timerUP = new 
  // }

  public void SetButtonUP(ActionListener StartAction, ActionListener StopAction){
    this.buttonUP.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            StartAction.actionPerformed(null);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            StopAction.actionPerformed(null);
        }
        });
  }

  public void SetButtonDOWN(ActionListener StartAction, ActionListener StopAction){
    this.buttonDOWN.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            StartAction.actionPerformed(null);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            StopAction.actionPerformed(null);
        }
        });
  }

  public void SetButtonLEFT(ActionListener StartAction, ActionListener StopAction){
    this.buttonLEFT.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            StartAction.actionPerformed(null);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            StopAction.actionPerformed(null);
        }
        });
  }

  public void SetButtonRIGHT(ActionListener StartAction, ActionListener StopAction){
    this.buttonRIGHT.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            StartAction.actionPerformed(null);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            StopAction.actionPerformed(null);
        }
        });
  }


  public void SetButtonSTOP(ActionListener StartAction){
    this.buttonUP.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            StartAction.actionPerformed(null);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            
        }
        });
  }





  public void Show(){
    this.setVisible(true);
  }


  

}