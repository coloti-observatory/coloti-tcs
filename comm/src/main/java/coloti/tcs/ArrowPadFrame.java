package coloti.tcs;

import java.awt.Color;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeListener;

import astri.astron.Target;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.io.IOException;


public class ArrowPadFrame extends JDialog{ //  implements KeyListener  implements ButtonModel    JFrame
  
  //Timer timerUP, timerDOWN, timerLEFT, timerRIGHT;
  JButton buttonHome;
  JButton buttonHomeTel;
  JButton buttonUP;
  JButton buttonDOWN;
  JButton buttonLEFT;
  JButton buttonRIGHT;
  JButton buttonSTOP;
  JButton buttonDomeEAST;
  JButton buttonDomeWEST;
  JLabel l1;
  String targetString;

  JRadioButton slowButton;
  JRadioButton mediumButton;
  JRadioButton fastButton;

  JLabel labelTimer;

  JTextField textTarget;
  JButton buttonTarget;
  JLabel labelTarget;
  JPanel panelTarget;
  TCS tcs;
 

  public ArrowPadFrame(JFrame parentFrame, TCS tcs) {
    super(parentFrame, "Speed Selector", true);
    parentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    this.setSize(1000, 500);
    this.setLayout(null);

    this.tcs = tcs; //, TCS tcs

    buttonHome = new JButton("Dome Home");
    buttonHome.setBounds(100, 30, 140, 30);
    buttonHome.setBackground(Color.pink);
    this.add(buttonHome);

    buttonHomeTel = new JButton("Tel Home");
    buttonHomeTel.setBounds(250, 30, 140, 30);
    buttonHomeTel.setBackground(Color.pink);
    this.add(buttonHomeTel);

    buttonUP = new JButton("UP");
    //buttonUP.setBounds(200, 100, 100, 100);
    buttonUP.setBounds(700, 100, 100, 100);
    this.add(buttonUP);

    buttonDOWN = new JButton("DOWN");
    //buttonDOWN.setBounds(200, 300, 100, 100);
    buttonDOWN.setBounds(700, 300, 100, 100);
    this.add(buttonDOWN);

    buttonLEFT = new JButton("LEFT");
    //buttonLEFT.setBounds(100, 200, 100, 100);
    buttonLEFT.setBounds(600, 200, 100, 100);
    this.add(buttonLEFT);

    buttonRIGHT = new JButton("RIGHT");
    //buttonRIGHT.setBounds(300, 200, 100, 100);
    buttonRIGHT.setBounds(800, 200, 100, 100);
    this.add(buttonRIGHT);

    buttonSTOP = new JButton("STOP");
    //buttonSTOP.setBounds(380, 400, 80, 30);
    buttonSTOP.setBounds(880, 400, 80, 30);
    buttonSTOP.setBackground(Color.RED);
    this.add(buttonSTOP);

    buttonDomeEAST = new JButton("East");
    //buttonDomeEAST.setBounds(300, 50, 80, 30);
    buttonDomeEAST.setBounds(800, 50, 80, 30);
    buttonDomeEAST.setBackground(Color.LIGHT_GRAY);
    this.add(buttonDomeEAST);

    buttonDomeWEST = new JButton("West");
    //buttonDomeWEST.setBounds(390, 50, 80, 30);
    buttonDomeWEST.setBounds(890, 50, 80, 30);
    buttonDomeWEST.setBackground(Color.LIGHT_GRAY);
    this.add(buttonDomeWEST);
      
    l1 = new JLabel("Dome");  
    //l1.setBounds(360, 20, 100, 30);
    l1.setBounds(860, 20, 100, 30);
    this.add(l1);

    slowButton = new JRadioButton("Slow");
    //slowButton.setBounds(50, 30, 100, 30);
    slowButton.setBounds(550, 30, 100, 30);
    mediumButton = new JRadioButton("Medium");
    //mediumButton.setBounds(50, 70, 100, 30);
    mediumButton.setBounds(550, 70, 100, 30);
    fastButton = new JRadioButton("Fast");
    //fastButton.setBounds(50, 110, 100, 30);
    fastButton.setBounds(550, 110, 100, 30);

    // Group the radio buttons
    ButtonGroup group = new ButtonGroup();
    group.add(slowButton);
    group.add(mediumButton);
    group.add(fastButton);

    this.add(slowButton);
    this.add(mediumButton);
    this.add(fastButton);



    labelTarget = new JLabel("Target: nothing entered");
    labelTarget.setBounds(150, 120, 250, 30);

    buttonTarget = new JButton("Submit Target");

    /* 
    buttonTarget.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        String targetString = textTarget.getText();
        labelTarget.setText("Target: "+targetString);
        Target src = new Target(textTarget.getText());
        System.out.println(src);
        textTarget.setText("");
      }
    });
    */
    
    buttonTarget.setBounds(300, 80, 160, 30);
    textTarget = new JTextField(16);
    textTarget.setBounds(50, 80, 230, 30);
    this.add(labelTarget);
    this.add(buttonTarget);
    this.add(textTarget);

    labelTimer = new JLabel("");
    labelTimer.setBounds(150, 200, 250, 30);
    this.add(labelTimer);

    //this.timer.start();





    //panelTarget = new JPanel();
    //panelTarget.add(textTarget);
    //panelTarget.add(buttonTarget);
    //panelTarget.add(labelTarget);
    //this.add(panelTarget);

    

    parentFrame.dispose();
    
  }

  //public void SetTimer(ActionListener action){
  //  this.timerUP = new 
  // }

  Timer timer = new Timer(1000, new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        // Update the JTextField with current time (for example)
        labelTimer.setText("Current Time: " + System.currentTimeMillis());
    }
  });


  public void SetButtonTarget(ActionListener startAction){
    this.buttonTarget.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            targetString = textTarget.getText();

              try {
                Files.write(Paths.get("target.txt"), targetString.getBytes(StandardCharsets.UTF_8));
              } catch (IOException e1) {
                e1.printStackTrace();
              }

            labelTarget.setText("Target: "+targetString);
            //Target src = new Target(textTarget.getText());
            //System.out.println(src);
            textTarget.setText("");
            startAction.actionPerformed(null);
        }
        });
  }



  public void SetButtonHome(ActionListener startAction){
    this.buttonHome.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            startAction.actionPerformed(null);
        }
        @Override
        public void mouseReleased(MouseEvent e) {
        }
        });
  }

  public void SetButtonHomeTel(ActionListener startAction){
    this.buttonHomeTel.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            startAction.actionPerformed(null);
        }
        @Override
        public void mouseReleased(MouseEvent e) {
        }
        });
  }


  public void SetButtonUP(ActionListener startAction, ActionListener StopAction){
    this.buttonUP.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            startAction.actionPerformed(null);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            StopAction.actionPerformed(null);
        }
        });
  }

  public void SetButtonDOWN(ActionListener startAction, ActionListener StopAction){
    this.buttonDOWN.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            startAction.actionPerformed(null);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            StopAction.actionPerformed(null);
        }
        });
  }

  public void SetButtonLEFT(ActionListener startAction, ActionListener StopAction){
    this.buttonLEFT.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            startAction.actionPerformed(null);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            StopAction.actionPerformed(null);
        }
        });
  }

  public void SetButtonRIGHT(ActionListener startAction, ActionListener StopAction){
    this.buttonRIGHT.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            startAction.actionPerformed(null);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            StopAction.actionPerformed(null);
        }
        });
  }


  public void SetButtonSTOP(ActionListener startAction){
    this.buttonSTOP.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            startAction.actionPerformed(null);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }
        });
  }

  public void SetButtonDomeEAST(ActionListener startAction, ActionListener StopAction){
    this.buttonDomeEAST.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            startAction.actionPerformed(null);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            StopAction.actionPerformed(null);
        }
        });
  }
  
  public void SetButtonDomeWEST(ActionListener startAction, ActionListener StopAction){
    this.buttonDomeWEST.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            startAction.actionPerformed(null);
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