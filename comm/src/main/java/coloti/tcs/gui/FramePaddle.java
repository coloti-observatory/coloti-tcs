package coloti.tcs.gui;

import java.awt.Color;
import java.awt.event.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ChangeListener;

import astri.astron.Target;
import coloti.tcs.TCS;

public class FramePaddle extends JDialog{ //  implements KeyListener  implements ButtonModel    JFrame
  
  //Timer timerUP, timerDOWN, timerLEFT, timerRIGHT;
  JButton buttonHomeDome;
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



  ActionListener actionMoveUP;
  ActionListener actionMoveDOWN;
  ActionListener actionMoveLEFT;
  ActionListener actionMoveRIGHT;
  ActionListener actionstopEL;
  ActionListener actionstopAZ;
  ActionListener actionSTOP;
  ActionListener actionStopDome;
  ActionListener actionSlowSpeed;
  ActionListener actionMediumSpeed;
  ActionListener actionFastSpeed;
  ActionListener actionDomeEAST;
  ActionListener actionDomeWEST;
  ActionListener actionDomeStop;
  ActionListener actionHomeDome;
  ActionListener actionHomeTel;
  ActionListener actionTarget;

 

  public FramePaddle(JFrame parentFrame, TCS tcs) {
    super(parentFrame, "Speed Selector", true);
    parentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    this.setSize(1000, 500);
    this.setLayout(null);

    this.tcs = tcs; //, TCS tcs

    configure();

    parentFrame.dispose();
  }

  public void configure(){
    appearance();
  }

  public void appearance(){

    this.buttonHomeDome = new JButton("Dome Home");
    this.buttonHomeDome.setBounds(100, 30, 140, 30);
    this.buttonHomeDome.setBackground(Color.pink);
    this.add(buttonHomeDome);

    this.buttonHomeTel = new JButton("Tel Home");
    this.buttonHomeTel.setBounds(250, 30, 140, 30);
    this.buttonHomeTel.setBackground(Color.pink);
    this.add(buttonHomeTel);

    this.buttonUP = new JButton("UP");
    //buttonUP.setBounds(200, 100, 100, 100);
    this.buttonUP.setBounds(700, 100, 100, 100);
    this.add(buttonUP);

    this.buttonDOWN = new JButton("DOWN");
    //buttonDOWN.setBounds(200, 300, 100, 100);
    this.buttonDOWN.setBounds(700, 300, 100, 100);
    this.add(buttonDOWN);

    this.buttonLEFT = new JButton("LEFT");
    //buttonLEFT.setBounds(100, 200, 100, 100);
    this.buttonLEFT.setBounds(600, 200, 100, 100);
    this.add(buttonLEFT);

    this.buttonRIGHT = new JButton("RIGHT");
    //buttonRIGHT.setBounds(300, 200, 100, 100);
    this.buttonRIGHT.setBounds(800, 200, 100, 100);
    this.add(buttonRIGHT);

    this.buttonSTOP = new JButton("STOP");
    //buttonSTOP.setBounds(380, 400, 80, 30);
    this.buttonSTOP.setBounds(880, 400, 80, 30);
    this.buttonSTOP.setBackground(Color.RED);
    this.add(buttonSTOP);

    this.buttonDomeEAST = new JButton("East");
    //buttonDomeEAST.setBounds(300, 50, 80, 30);
    this.buttonDomeEAST.setBounds(800, 50, 80, 30);
    this.buttonDomeEAST.setBackground(Color.LIGHT_GRAY);
    this.add(buttonDomeEAST);

    this.buttonDomeWEST = new JButton("West");
    //buttonDomeWEST.setBounds(390, 50, 80, 30);
    this.buttonDomeWEST.setBounds(890, 50, 80, 30);
    this.buttonDomeWEST.setBackground(Color.LIGHT_GRAY);
    this.add(buttonDomeWEST);
      
    this.l1 = new JLabel("Dome");  
    //l1.setBounds(360, 20, 100, 30);
    this.l1.setBounds(860, 20, 100, 30);
    this.add(l1);

    this.slowButton = new JRadioButton("Slow");
    //slowButton.setBounds(50, 30, 100, 30);
    this.slowButton.setBounds(550, 30, 100, 30);
    this.mediumButton = new JRadioButton("Medium");
    //mediumButton.setBounds(50, 70, 100, 30);
    this.mediumButton.setBounds(550, 70, 100, 30);
    this.fastButton = new JRadioButton("Fast");
    //fastButton.setBounds(50, 110, 100, 30);
    this.fastButton.setBounds(550, 110, 100, 30);

    // Group the radio buttons
    //ButtonGroup group = new ButtonGroup();
    //group.add(slowButton);
    //group.add(mediumButton);
    //group.add(fastButton);

    this.add(slowButton);
    this.add(mediumButton);
    this.add(fastButton);



    this.labelTarget = new JLabel("Target: nothing entered");
    this.labelTarget.setBounds(150, 120, 250, 30);

    this.buttonTarget = new JButton("Submit Target");
    
    this.buttonTarget.setBounds(300, 80, 160, 30);
    this.textTarget = new JTextField(16);
    this.textTarget.setBounds(50, 80, 230, 30);
    this.add(labelTarget);
    this.add(buttonTarget);
    this.add(textTarget);

    labelTimer = new JLabel("");
    labelTimer.setBounds(150, 200, 250, 30);
    this.add(labelTimer);
    
  }





  public void setactions(){

    this.actionMoveUP = action -> {
      print("Going up...");
      if (tcs.yAxisConnection)
          tcs.CmdElMoveUp(true);
      else
          print("EL not connected");};

    this.actionMoveDOWN = action -> {
      print("Going down...");
      if (tcs.yAxisConnection)
        tcs.CmdElMoveDown(true);
      else
          print("EL not connected");
      };

      this.actionMoveLEFT = action -> {
        print("Going left...");
        if (tcs.xAxisConnection)
          tcs.CmdAzMoveLeft(true);
        else
            print("AZ not connected");
      };

      this.actionMoveRIGHT = action -> {
        print("Going right...");
        if (tcs.xAxisConnection)
          tcs.CmdAzMoveRight(true);
        else
            print("AZ not connected");
      };

      this.actionstopEL = action -> {
        if (tcs.yAxisConnection)
          tcs.CmdStopElMotion(true);
        print("done.");
      };

      this.actionstopAZ = action -> {
        if (tcs.xAxisConnection)
              tcs.CmdStopAzMotion(true);
            print("done.");
      };

      this.actionSTOP = action -> {
        print("Stop movements...");
        tcs.CmdEmergencyStop(true);
      };

      this.actionSlowSpeed = action -> {
        print("Set slow speed");
        tcs.SetAbsJogVelocity(60);
      };

      this.actionMediumSpeed = action -> {
        print("Set medium speed");
        tcs.SetAbsJogVelocity(150);
      };

      this.actionFastSpeed = action -> {
        print("Set fast speed");
        tcs.SetAbsJogVelocity(180);
      };

      this.actionDomeEAST = action -> {
        print("Dome going east...");
        if (tcs.domeAxisConnection)
          tcs.CmdCupolaEst(true);
        else
            print("DOME not connected");
      };

      this.actionDomeEAST = action -> {
        print("Dome going west...");
        if (tcs.domeAxisConnection)
          tcs.CmdCupolaOvest(true);
        else
            print("DOME not connected");
      };

      this.actionDomeStop = action -> {
        if (tcs.domeAxisConnection)
          tcs.CmdStopCupola(true);
        print("done.");
      };
      
      this.actionHomeDome = action -> {
        tcs.CmdHomeCupola(true);
        print("Dome home position procedure...");

      };
      
      this.actionHomeTel = action -> {
        tcs.CmdHomeTel(true);
        print("Telescope home position procedure...");
      };
      
      this.actionTarget = action -> tcs.SetTarget(targetString);


      

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



  public void print(String string){
    System.out.println(string);
  }

  public void settingTarget(String string){
    this.targetString = string;
  }

  public void SetButtonTarget(){
    this.buttonTarget.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            settingTarget(textTarget.getText());
            labelTarget.setText("Target: "+targetString);
            textTarget.setText("");
            actionTarget.actionPerformed(null);
        }
        });
  }



  public void SetButtonHome(){
    this.buttonHomeDome.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            actionHomeDome.actionPerformed(null);
        }
        });
  }

  public void SetButtonHomeTel(){
    this.buttonHomeTel.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            actionHomeTel.actionPerformed(null);
        }
        });
  }


  public void SetButtonUP(){
    
    this.buttonUP.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            actionMoveUP.actionPerformed(null);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            actionstopEL.actionPerformed(null);
        }
        });
  }

  public void SetButtonDOWN(){
    this.buttonDOWN.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            actionMoveDOWN.actionPerformed(null);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            actionstopEL.actionPerformed(null);
        }
        });
  }

  public void SetButtonLEFT(){
    this.buttonLEFT.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            actionMoveLEFT.actionPerformed(null);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            actionstopAZ.actionPerformed(null);
        }
        });
  }

  public void SetButtonRIGHT(){
    this.buttonRIGHT.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            actionMoveRIGHT.actionPerformed(null);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            actionstopAZ.actionPerformed(null);
        }
        });
  }


  public void SetButtonSTOP(){
    this.buttonSTOP.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            actionSTOP.actionPerformed(null);
        }
        });
  }

  public void SetButtonDomeEAST(){
    this.buttonDomeEAST.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            actionDomeEAST.actionPerformed(null);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            actionDomeStop.actionPerformed(null);
        }
        });
  }
  
  public void SetButtonDomeWEST(){
    this.buttonDomeWEST.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            actionDomeWEST.actionPerformed(null);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            actionDomeStop.actionPerformed(null);
        }
        });
  }

  public void SetSlowSpeed(){
    this.slowButton.addActionListener(actionSlowSpeed);
  }

  public void SetMediumSpeed(){
    this.mediumButton.addActionListener(actionMediumSpeed);
  }

  public void SetFastSpeed(){
    this.fastButton.addActionListener(actionFastSpeed);
  }

  public void Show(){
    this.setVisible(true);
  }


  

}