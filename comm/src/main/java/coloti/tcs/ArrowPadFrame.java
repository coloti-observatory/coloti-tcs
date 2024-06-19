package coloti.tcs;

import java.awt.Color;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeListener;

public class ArrowPadFrame extends JDialog{ //  implements KeyListener  implements ButtonModel    JFrame
  
  //Timer timerUP, timerDOWN, timerLEFT, timerRIGHT;
  JButton buttonUP;
  JButton buttonDOWN;
  JButton buttonLEFT;
  JButton buttonRIGHT;
  JButton buttonSTOP;
  JButton buttonDomeEAST;
  JButton buttonDomeWEST;
  JLabel l1;

  JRadioButton slowButton;
  JRadioButton mediumButton;
  JRadioButton fastButton;

  ArrowPadFrame(JFrame parentFrame) {
    super(parentFrame, "Speed Selector", true);
    parentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
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

    buttonDomeEAST = new JButton("East");
    buttonDomeEAST.setBounds(300, 50, 80, 30);
    buttonDomeEAST.setBackground(Color.LIGHT_GRAY);
    this.add(buttonDomeEAST);

    buttonDomeWEST = new JButton("West");
    buttonDomeWEST.setBounds(390, 50, 80, 30);
    buttonDomeWEST.setBackground(Color.LIGHT_GRAY);
    this.add(buttonDomeWEST);

      
    l1 = new JLabel("Dome");  
    l1.setBounds(360, 20, 100, 30);
    this.add(l1);

    slowButton = new JRadioButton("Slow");
    slowButton.setBounds(50, 30, 100, 30);
    mediumButton = new JRadioButton("Medium");
    mediumButton.setBounds(50, 70, 100, 30);
    fastButton = new JRadioButton("Fast");
    fastButton.setBounds(50, 110, 100, 30);

    // Group the radio buttons
    ButtonGroup group = new ButtonGroup();
    group.add(slowButton);
    group.add(mediumButton);
    group.add(fastButton);

    this.add(slowButton);
    this.add(mediumButton);
    this.add(fastButton);

    parentFrame.dispose();
    
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
    this.buttonSTOP.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            StartAction.actionPerformed(null);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }
        });
  }

  public void SetButtonDomeEAST(ActionListener StartAction, ActionListener StopAction){
    this.buttonDomeEAST.addMouseListener(new MouseAdapter() {
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
  
  public void SetButtonDomeWEST(ActionListener StartAction, ActionListener StopAction){
    this.buttonDomeWEST.addMouseListener(new MouseAdapter() {
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

  /* 
  new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Speed set to Slow");
        // Set speed logic here
    }
};*/

  public void SetSlowSpeed(ActionListener action){
    this.slowButton.addActionListener(action);
  }

  public void SetMediumSpeed(ActionListener action){
    this.mediumButton.addActionListener(action);
  }

  public void SetFastSpeed(ActionListener action){
    this.fastButton.addActionListener(action);
  }






  public void Show(){
    this.setVisible(true);
  }


  

}