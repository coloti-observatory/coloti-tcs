package coloti.tcs.gui;

import java.awt.Color;
import java.awt.event.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.event.ChangeListener;

import astri.astron.Target;
import coloti.tcs.TCS;

public class FramePaddle extends JDialog{ //  implements KeyListener  implements ButtonModel    JFrame
  DecimalFormat format = new DecimalFormat("#.###");
  //Timer timerUP, timerDOWN, timerLEFT, timerRIGHT;
  JButton buttonHomeDome;
  JButton buttonHomeTel;
  JButton buttonConnect;
  JButton buttonDisconnect;
  JButton buttonUP;
  JButton buttonDOWN;
  JButton buttonLEFT;
  JButton buttonRIGHT;
  JButton buttonSTOP;
  JButton buttonDomeEAST;
  JButton buttonDomeWEST;
  JLabel l1;
  String targetString;
  double commandedVelocity;

  JRadioButton slowButton;
  JRadioButton mediumButton;
  JRadioButton fastButton;
  
  JRadioButton slewButton;
  JRadioButton jogButton;

  
  JLabel labelVelocity;
  JLabel labelVelocity2;
  JLabel labelTargetRa;
  JLabel labelTargetDec;
  JLabel labelCurrentPosition;

  JTextField textTarget;
  JTextField textSetVel;
  JButton buttonSetVel;
  JButton buttonTarget;
  JButton buttonPoint;
  JLabel labelTarget;
  JPanel panelTarget;

  JLabel labelVelDescr;
 
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
  ActionListener actionJogMode;
  ActionListener actionSlewMode;
  ActionListener actionDomeEAST;
  ActionListener actionDomeWEST;
  ActionListener actionDomeStop;
  ActionListener actionHomeDome;
  ActionListener actionHomeTel;
  ActionListener actionTarget;
  ActionListener actionVelocity;
  ActionListener actionConnect;
  ActionListener actionDisconnect;
  ActionListener actionPoint;

 

  public FramePaddle(JFrame parentFrame, TCS tcs) {
    super(parentFrame, "Speed Selector", true);
    parentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    this.setSize(1200, 700);
    this.setLayout(null);

    this.tcs = tcs; //, TCS tcs

    configure();

    parentFrame.dispose();
  }

  public void configure(){
    appearance();
    defineActions();
    setInteractions();
  }

  public void appearance(){

    this.buttonHomeDome = new JButton("Dome Home");
    this.buttonHomeDome.setBounds(200, 230, 140, 30);
    this.buttonHomeDome.setBackground(Color.pink);
    this.add(buttonHomeDome);

    this.buttonHomeTel = new JButton("Tel Home");
    this.buttonHomeTel.setBounds(350, 230, 140, 30);
    this.buttonHomeTel.setBackground(Color.pink);
    this.add(buttonHomeTel);


    this.buttonConnect = new JButton("CONNECT");
    this.buttonConnect.setBounds(200, 30, 140, 50);
    this.buttonConnect.setBackground(Color.green);
    this.add(buttonConnect);

    this.buttonDisconnect = new JButton("DISCONNECT");
    this.buttonDisconnect.setBounds(350, 30, 140, 50);
    this.buttonDisconnect.setBackground(Color.magenta);
    this.add(buttonDisconnect);





    this.labelTarget = new JLabel("Target: nothing entered");
    this.labelTarget.setBounds(150, 335, 250, 30);
    this.add(labelTarget);
    
    this.buttonTarget = new JButton("Submit Target");
    this.buttonTarget.setBounds(400, 280, 160, 30);
    this.add(buttonTarget);

    this.textTarget = new JTextField(16);
    this.textTarget.setBounds(150, 280, 230, 30);    
    this.add(textTarget);


    this.buttonPoint = new JButton("Point Target");
    this.buttonPoint.setBounds(400, 325, 160, 50);
    this.buttonPoint.setBackground(Color.orange);
    this.add(buttonPoint);



    this.labelVelDescr = new JLabel("velocity (arcs/s)");
    this.labelVelDescr.setBounds(900, 150, 150, 30);
    this.add(labelVelDescr);
    
    this.buttonSetVel = new JButton("Set Velocity");
    this.buttonSetVel.setBounds(900, 220, 150, 30);
    this.add(buttonSetVel);

    this.textSetVel = new JTextField(16);
    this.textSetVel.setBounds(900, 180, 150, 30);    
    this.add(textSetVel);



    labelVelocity = new JLabel("");
    labelVelocity.setBounds(750, 70, 400, 30);
    this.add(labelVelocity);
    this.timerVelocity.start();

    labelVelocity2 = new JLabel("");
    labelVelocity2.setBounds(750, 110, 400, 30);
    this.add(labelVelocity2);
    this.timerVelocity2.start();

    labelTargetRa = new JLabel("");
    labelTargetRa.setBounds(150, 375, 300, 30);
    this.add(labelTargetRa);
    this.timerTargetRa.start();

    labelTargetDec = new JLabel("");
    labelTargetDec.setBounds(150, 395, 300, 30);
    this.add(labelTargetDec);
    this.timerTargetDec.start();

    labelCurrentPosition = new JLabel("");
    labelCurrentPosition.setBounds(150, 440, 300, 30);
    this.add(labelCurrentPosition);
    this.timerCurrentPosition.start();











    this.buttonUP = new JButton("UP");
    //buttonUP.setBounds(200, 100, 100, 100);
    this.buttonUP.setBounds(900, 300, 100, 100);
    this.add(buttonUP);

    this.buttonDOWN = new JButton("DOWN");
    //buttonDOWN.setBounds(200, 300, 100, 100);
    this.buttonDOWN.setBounds(900, 500, 100, 100);
    this.add(buttonDOWN);

    this.buttonLEFT = new JButton("LEFT");
    //buttonLEFT.setBounds(100, 200, 100, 100);
    this.buttonLEFT.setBounds(800, 400, 100, 100);
    this.add(buttonLEFT);

    this.buttonRIGHT = new JButton("RIGHT");
    //buttonRIGHT.setBounds(300, 200, 100, 100);
    this.buttonRIGHT.setBounds(1000, 400, 100, 100);
    this.add(buttonRIGHT);

    this.buttonSTOP = new JButton("STOP");
    //buttonSTOP.setBounds(380, 400, 80, 30);
    this.buttonSTOP.setBounds(1080, 600, 80, 30);
    this.buttonSTOP.setBackground(Color.RED);
    this.add(buttonSTOP);

    this.buttonDomeEAST = new JButton("East");
    //buttonDomeEAST.setBounds(300, 50, 80, 30);
    //this.buttonDomeEAST.setBounds(1000, 250, 80, 30);
    this.buttonDomeEAST.setBounds(630, 320, 80, 30);
    this.buttonDomeEAST.setBackground(Color.LIGHT_GRAY);
    this.add(buttonDomeEAST);

    this.buttonDomeWEST = new JButton("West");
    //buttonDomeWEST.setBounds(390, 50, 80, 30);
    //this.buttonDomeWEST.setBounds(1090, 250, 80, 30);
    this.buttonDomeWEST.setBounds(720, 320, 80, 30);
    this.buttonDomeWEST.setBackground(Color.LIGHT_GRAY);
    this.add(buttonDomeWEST);
      
    this.l1 = new JLabel("Dome");  
    //l1.setBounds(360, 20, 100, 30);
    //this.l1.setBounds(1060, 220, 100, 30);
    this.l1.setBounds(690, 290, 100, 30);
    this.add(l1);

    this.slowButton = new JRadioButton("Slow");
    //slowButton.setBounds(50, 30, 100, 30);
    this.slowButton.setBounds(750, 150, 100, 30);
    this.mediumButton = new JRadioButton("Medium");
    //mediumButton.setBounds(50, 70, 100, 30);
    this.mediumButton.setBounds(750, 190, 100, 30);
    this.fastButton = new JRadioButton("Fast");
    //fastButton.setBounds(50, 110, 100, 30);
    this.fastButton.setBounds(750, 230, 100, 30);

    // Group the radio buttons
    ButtonGroup group = new ButtonGroup();
    group.add(slowButton);
    group.add(mediumButton);
    group.add(fastButton);


    this.add(slowButton);
    this.add(mediumButton);
    this.add(fastButton);




    this.slewButton = new JRadioButton("Slewing mode");
    this.slewButton.setBounds(180, 100, 150, 30);
    this.jogButton = new JRadioButton("Jogging mode");
    this.jogButton.setBounds(350, 100, 150, 30);
    // Group the radio buttons
    ButtonGroup group2 = new ButtonGroup();
    group2.add(slewButton);
    group2.add(jogButton);

    this.add(slewButton);
    this.add(jogButton);


    
  }


  public void defineActions(){

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
      else
        print("EL not connected");
      print("done.");
    };

    this.actionstopAZ = action -> {
      if (tcs.xAxisConnection)
            tcs.CmdStopAzMotion(true);
      else
        print("AZ not connected");
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
      tcs.SetAbsJogVelocity(1000); //1000
    };

    this.actionFastSpeed = action -> {
      print("Set fast speed");
      tcs.SetAbsJogVelocity(1500); //180
    };

    this.actionJogMode = action -> {
      print("Set jogging mode");
      tcs.SetTrackingMode();
    };

    this.actionSlewMode = action -> {
      print("Set slewing mode");
      tcs.SetPointingMode();
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
        tcs.CmdCupolaEst(true);
      else
          print("DOME not connected");
    };

    this.actionDomeWEST = action -> {
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

    this.actionVelocity = action -> tcs.SetAbsJogVelocity(commandedVelocity);

    this.actionConnect = action -> tcs.connect();

    this.actionDisconnect = action -> tcs.disconnect();

    this.actionPoint = action -> tcs.CmdStartPointing(true);


  }

  public void setInteractions(){
    setButtonTarget();
    setButtonHomeDome();
    setButtonHomeTel();
    setButtonUP();
    setButtonDOWN();
    setButtonLEFT();
    setButtonRIGHT();
    setButtonSTOP();
    setButtonDomeEAST();
    setButtonDomeWEST();
    setSlowSpeed();
    setMediumSpeed();
    setFastSpeed();
    setJogMode();
    setSlewMode();
    setButtonConnect();
    setButtonDisconnect();
    setButtonPoint();
    setButtonVelocity();
  
  }




  //public void SetTimer(ActionListener action){
  //  this.timerUP = new 
  // }


  Timer timerVelocity = new Timer(2000, new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      if (tcs.xAxisConnection && tcs.tcsConnection)
        labelVelocity.setText("AZ vel. - commanded: " + format.format(tcs.getcommandedvelAZ()) + " ,  current: "+ format.format(tcs.getactualvelAZ()) );
      else
        labelVelocity.setText("AZ vel. - commanded: 0,  current: 0");
    }
  });

  Timer timerVelocity2 = new Timer(2000, new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      if (tcs.yAxisConnection && tcs.tcsConnection)
        labelVelocity2.setText("EL vel. - commanded: " + format.format(tcs.getcommandedvelEL()) + " ,  current: "+ format.format(tcs.getactualvelEL()) );
      else
        labelVelocity2.setText("EL vel. - commanded: 0,  current: 0");
      }
  });

  Timer timerTargetRa = new Timer(2000, new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        // Update the JTextField with current time (for example)
        labelTargetRa.setText("Target RA:    " + format.format(tcs.gettargetRA()) + "     (AZ: "+ format.format(tcs.gettargetAZ())+")");
    }
  });

  Timer timerTargetDec = new Timer(2000, new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        // Update the JTextField with current time (for example)
        labelTargetDec.setText("Target DEC:  " + format.format(tcs.gettargetDEC()) + "     (EL: "+ format.format(tcs.gettargetEL())+")");
    }
  });

  Timer timerCurrentPosition = new Timer(2000, new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        // Update the JTextField with current time (for example)
        if (tcs.xAxisConnection && tcs.yAxisConnection && tcs.tcsConnection)
          labelCurrentPosition.setText("Current Posizion (AZ,EL) (deg):  " + format.format(tcs.getcurrentposAZ()/3600) + " , "+ format.format(tcs.getcurrentposEL()/3600));
        else if (tcs.xAxisConnection && tcs.tcsConnection)
          labelCurrentPosition.setText("Current Posizion (AZ,EL) (deg):  " + format.format(tcs.getcurrentposAZ()/3600) + " , 0");
        else if (tcs.yAxisConnection && tcs.tcsConnection)
          labelCurrentPosition.setText("Current Posizion (AZ,EL) (deg):  0 , " + format.format(tcs.getcurrentposAZ()/3600));
        else
          labelCurrentPosition.setText("Current Posizion (AZ,EL):  0, 0");
    }
  });

  public void print(String string){
    System.out.println(string);
  }

  public void writeTarget(String string){
    this.targetString = string;
  }

  public void writeVelocity(double value){
    this.commandedVelocity = value;
  }



  public void setButtonTarget(){
    this.buttonTarget.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseReleased(MouseEvent e) {
            writeTarget(textTarget.getText());
            labelTarget.setText("Target: "+targetString);
            textTarget.setText("");
            actionTarget.actionPerformed(null);
        }
        });
  }

  public void setButtonVelocity(){
    this.buttonSetVel.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseReleased(MouseEvent e) {
            writeVelocity(Double.parseDouble(textSetVel.getText()));
            actionVelocity.actionPerformed(null);
        }
        });
  }

  public void setButtonPoint(){
    this.buttonPoint.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseReleased(MouseEvent e) {
            actionPoint.actionPerformed(null);
        }
        });
  }

  public void setButtonHomeDome(){
    this.buttonHomeDome.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseReleased(MouseEvent e) {
            actionHomeDome.actionPerformed(null);
        }
        });
  }

  public void setButtonHomeTel(){
    this.buttonHomeTel.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseReleased(MouseEvent e) {
            actionHomeTel.actionPerformed(null);
        }
        });
  }

  public void setButtonUP(){
    
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

  public void setButtonDOWN(){
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

  public void setButtonLEFT(){
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

  public void setButtonRIGHT(){
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

  public void setButtonSTOP(){
    this.buttonSTOP.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            actionSTOP.actionPerformed(null);
        }
        });
  }

  public void setButtonDomeEAST(){
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
  
  public void setButtonDomeWEST(){
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

  public void setSlowSpeed(){
    this.slowButton.addActionListener(actionSlowSpeed);
  }

  public void setMediumSpeed(){
    this.mediumButton.addActionListener(actionMediumSpeed);
  }

  public void setFastSpeed(){
    this.fastButton.addActionListener(actionFastSpeed);
  }

  public void setJogMode(){
    this.jogButton.addActionListener(actionJogMode);
  }

  public void setSlewMode(){
    this.slewButton.addActionListener(actionSlewMode);
  }

  public void setButtonConnect(){
    this.buttonConnect.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseReleased(MouseEvent e) {
          actionConnect.actionPerformed(null);
      }
      });
  }

  public void setButtonDisconnect(){
    this.buttonDisconnect.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseReleased(MouseEvent e) {
          actionDisconnect.actionPerformed(null);
      }
      });
  }


  public void Show(){
    this.setVisible(true);
  }


  

}