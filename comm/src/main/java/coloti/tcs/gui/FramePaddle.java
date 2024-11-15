package coloti.tcs.gui;

import java.awt.Color;
import java.awt.Graphics;
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
import coloti.tcs.Position;
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
  JLabel labelDome;
  String targetString;
  double Xvalue = -999;
  double Yvalue = -999;
  double commandedVelocity;

  JRadioButton slowButton;
  JRadioButton mediumButton;
  JRadioButton fastButton;
  JRadioButton customVelButton;

  JRadioButton radecButton;
  JRadioButton azelButton;
  String coordinates = "radec";
  
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

  JLabel AZconnection;
  JLabel ELconnection;
  JLabel DOMEconnection;

  JLabel AZconnectionState;
  JLabel ELconnectionState;
  JLabel DOMEconnectionState;

  JTextField Xcoord;
  JTextField Ycoord;

  JButton SubmitCoord;


  JButton PointTrack;

  JLabel CurrentVelocity;
  JLabel CurrentDomePos;

  JToggleButton Pad;
  
  JButton MotorOn;
  JButton MotorOff;

  JLabel labelAzOnOff;
  JLabel labelElOnOff;




 
  TCS tcs;
  Position pos;



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
  ActionListener actionCustomSpeed;
  ActionListener actionRaDec;
  ActionListener actionAzEl;
  ActionListener actionJogMode;
  ActionListener actionSlewMode;
  ActionListener actionPadEnabler;
  ActionListener actionMotorOn;
  ActionListener actionMotorOff;
  ActionListener actionDomeEAST;
  ActionListener actionDomeWEST;
  ActionListener actionDomeStop;
  ActionListener actionHomeDome;
  ActionListener actionHomeTel;
  ActionListener actionTarget;
  ActionListener actionCoordinates;
  ActionListener actionVelocity;
  ActionListener actionConnect;
  ActionListener actionDisconnect;
  ActionListener actionPoint;
  ActionListener actionPointTrack;

 

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

  //#region appearance
  public void appearance(){

    this.buttonHomeDome = new JButton("Dome Home");
    this.buttonHomeDome.setBounds(170, 150, 140, 30);
    this.buttonHomeDome.setBackground(Color.pink);
    this.add(buttonHomeDome);

    this.buttonHomeTel = new JButton("Tel Home");
    this.buttonHomeTel.setBounds(320, 150, 140, 30);
    this.buttonHomeTel.setBackground(Color.pink);
    this.add(buttonHomeTel);


    this.buttonConnect = new JButton("CONNECT");
    this.buttonConnect.setBounds(170, 30, 140, 50);
    this.buttonConnect.setBackground(Color.green);
    this.add(buttonConnect);

    this.buttonDisconnect = new JButton("DISCONNECT");
    this.buttonDisconnect.setBounds(320, 30, 140, 50);
    this.buttonDisconnect.setBackground(Color.magenta);
    this.add(buttonDisconnect);





    this.labelTarget = new JLabel("Target: nothing entered");
    this.labelTarget.setBounds(180, 290, 300, 30);
    this.add(labelTarget);
    
    labelTargetRa = new JLabel("Target RA");
    labelTargetRa.setBounds(180, 325, 300, 30);
    this.add(labelTargetRa);
    this.timerTargetRa.start();

    labelTargetDec = new JLabel("Target DEC");
    labelTargetDec.setBounds(180, 360, 300, 30);
    this.add(labelTargetDec);
    this.timerTargetDec.start();


    this.buttonTarget = new JButton("Submit Target");
    this.buttonTarget.setBounds(110, 250, 200, 30);
    this.add(buttonTarget);

    this.textTarget = new JTextField(16);
    this.textTarget.setBounds(110, 210, 200, 30);    
    this.add(textTarget);

    
    this.SubmitCoord = new JButton("Submit Coordinates");
    this.SubmitCoord.setBounds(320, 250, 200, 30);
    this.add(SubmitCoord);

    this.Xcoord = new JTextField(16);
    this.Xcoord.setBounds(320, 210, 95, 30);    
    this.add(Xcoord);

    this.Ycoord = new JTextField(16);
    this.Ycoord.setBounds(425, 210, 95, 30);
    this.add(Ycoord);




    this.buttonPoint = new JButton("Point");
    this.buttonPoint.setBounds(110, 400, 180, 50);
    this.buttonPoint.setBackground(Color.orange);
    this.add(buttonPoint);

    this.PointTrack = new JButton("Point and Track");
    this.PointTrack.setBounds(330, 400, 180, 50);
    this.PointTrack.setBackground(Color.yellow);
    this.add(PointTrack);







    this.labelVelDescr = new JLabel("velocity (arcs/s)");
    this.labelVelDescr.setBounds(960, 170, 160, 30);
    this.add(labelVelDescr);
    
    this.buttonSetVel = new JButton("Set Velocity");
    this.buttonSetVel.setBounds(960, 240, 160, 30);
    this.add(buttonSetVel);

    this.textSetVel = new JTextField(16);
    this.textSetVel.setBounds(960, 200, 160, 30);    
    this.add(textSetVel);



    labelVelocity = new JLabel("Commanded Vel (AZ,EL) (arcs/s):");
    labelVelocity.setBounds(820, 120, 400, 30);
    this.add(labelVelocity);
    this.timerVelocity.start();

    //labelVelocity2 = new JLabel("");
    //labelVelocity2.setBounds(750, 110, 400, 30);
    //this.add(labelVelocity2);
    //this.timerVelocity2.start();

    Pad = new JToggleButton("");
    this.Pad.setBounds(890, 30, 140, 50);
    //this.Pad.setBackground(Color.decode("#ff8d8d")); // Color.decode("#fff")
    this.Pad.setText("Pad OFF");
    this.add(Pad);

    MotorOn = new JButton("ON");
    this.MotorOn.setBounds(600, 30, 70, 50);
    this.MotorOn.setBackground(Color.decode("#2b9af2")); // Color.decode("#fff")
    this.add(MotorOn);

    MotorOff = new JButton("OFF");
    this.MotorOff.setBounds(680, 30, 70, 50);
    this.MotorOff.setBackground(Color.decode("#f29e2b")); // Color.decode("#fff")
    this.add(MotorOff);
    this.timerMotors.start();

    labelAzOnOff = new JLabel("Motor AZ: ");
    this.labelAzOnOff.setBounds(600, 100, 200, 30);
    this.add(labelAzOnOff);

    labelElOnOff = new JLabel("Motor EL: ");
    this.labelElOnOff.setBounds(600, 130, 200, 30);
    this.add(labelElOnOff);



    labelCurrentPosition = new JLabel("Current Tel Pos (AZ,EL) (deg): ");
    labelCurrentPosition.setBounds(80, 540, 500, 30);
    this.add(labelCurrentPosition);
    this.timerCurrentPosition.start();

    CurrentDomePos = new JLabel("Current Dome Pos (deg): ");
    CurrentDomePos.setBounds(80, 500, 500, 30);
    this.add(CurrentDomePos);
    this.timerCurrentDomePosition.start();

    CurrentVelocity = new JLabel("Current Tel Vel (AZ,EL) (arcs/s):");
    CurrentVelocity.setBounds(80, 580, 500, 30);
    this.add(CurrentVelocity);
    this.timerCurrentVelocity.start();


    this.buttonDomeEAST = new JButton("East (L)");
    //buttonDomeEAST.setBounds(300, 50, 80, 30);
    //this.buttonDomeEAST.setBounds(1000, 250, 80, 30);
    this.buttonDomeEAST.setBounds(530, 490, 100, 30);
    this.buttonDomeEAST.setBackground(Color.LIGHT_GRAY);
    this.add(buttonDomeEAST);

    this.buttonDomeWEST = new JButton("West (R)");
    //buttonDomeWEST.setBounds(390, 50, 80, 30);
    //this.buttonDomeWEST.setBounds(1090, 250, 80, 30);
    this.buttonDomeWEST.setBounds(640, 490, 100, 30);
    this.buttonDomeWEST.setBackground(Color.LIGHT_GRAY);
    this.add(buttonDomeWEST);
      
    this.labelDome = new JLabel("Dome");  
    //labelDome.setBounds(360, 20, 100, 30);
    //this.labelDome.setBounds(1060, 220, 100, 30);
    this.labelDome.setBounds(610, 460, 100, 30);
    this.add(labelDome);


    this.buttonSTOP = new JButton("STOP");
    //buttonSTOP.setBounds(380, 400, 80, 30);
    this.buttonSTOP.setBounds(580, 560, 110, 50);
    this.buttonSTOP.setBackground(Color.RED);
    this.add(buttonSTOP);








    this.buttonUP = new JButton("UP");
    //buttonUP.setBounds(200, 100, 100, 100);
    this.buttonUP.setBounds(920, 310, 100, 100);
    this.add(buttonUP);

    this.buttonDOWN = new JButton("DOWN");
    //buttonDOWN.setBounds(200, 300, 100, 100);
    this.buttonDOWN.setBounds(920, 510, 100, 100);
    this.add(buttonDOWN);

    this.buttonLEFT = new JButton("LEFT");
    //buttonLEFT.setBounds(100, 200, 100, 100);
    this.buttonLEFT.setBounds(820, 410, 100, 100);
    this.add(buttonLEFT);

    this.buttonRIGHT = new JButton("RIGHT");
    //buttonRIGHT.setBounds(300, 200, 100, 100);
    this.buttonRIGHT.setBounds(1020, 410, 100, 100);
    this.add(buttonRIGHT);


    this.slowButton = new JRadioButton("Slow (60)");
    //slowButton.setBounds(50, 30, 100, 30);
    this.slowButton.setBounds(800, 170, 120, 30);
    this.mediumButton = new JRadioButton("Medium (150)");
    //mediumButton.setBounds(50, 70, 100, 30);
    this.mediumButton.setBounds(800, 200, 120, 30);
    this.fastButton = new JRadioButton("Fast (180)");
    //fastButton.setBounds(50, 110, 100, 30);
    this.fastButton.setBounds(800, 230, 120, 30);
    

    this.customVelButton = new JRadioButton("Custom Vel");
    this.customVelButton.setBounds(800, 280, 120, 30);

    // Group the radio buttons
    ButtonGroup group = new ButtonGroup();
    group.add(slowButton);
    group.add(mediumButton);
    group.add(fastButton);
    group.add(customVelButton);


    this.add(slowButton);
    this.add(mediumButton);
    this.add(fastButton);
    this.add(customVelButton);




    this.radecButton = new JRadioButton("RA - DEC  (h, deg)");
    this.radecButton.setBounds(540, 200, 120, 30);
    this.azelButton = new JRadioButton("AZ - EL  (deg, deg)");
    this.azelButton.setBounds(540, 230, 120, 30);

    // Group the radio buttons
    ButtonGroup group2 = new ButtonGroup();
    group.add(radecButton);
    group.add(azelButton);

    this.add(radecButton);
    this.add(azelButton);
    radecButton.doClick();




    this.AZconnection = new JLabel("AZ:");
    this.AZconnection.setBounds(150, 90, 150, 30);

    this.ELconnection = new JLabel("EL:");
    this.ELconnection.setBounds(310, 90, 150, 30);
    
    this.DOMEconnection = new JLabel("DOME:");
    this.DOMEconnection.setBounds(440, 90, 150, 30);
    // Group the radio buttons



    this.AZconnectionState = new JLabel("NOT connected");
    this.AZconnectionState.setBounds(120, 110, 150, 30);

    this.ELconnectionState = new JLabel("NOT connected");
    this.ELconnectionState.setBounds(270, 110, 150, 30);
    
    this.DOMEconnectionState = new JLabel("NOT connected");
    this.DOMEconnectionState.setBounds(430, 110, 150, 30);
    // Group the radio buttons

    this.add(AZconnection);
    this.add(ELconnection);
    this.add(DOMEconnection);

    this.add(AZconnectionState);
    this.add(ELconnectionState);
    this.add(DOMEconnectionState);

    
  }

  //#region actions
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
      tcs.SetAbsJogVelocity(150); //1000
    };

    this.actionFastSpeed = action -> {
      print("Set fast speed");
      tcs.SetAbsJogVelocity(180); //180
    };

    this.actionCustomSpeed = action -> print("Set custom speed");

    this.actionRaDec = action -> this.coordinates = "radec";

    this.actionAzEl = action -> this.coordinates = "azel";

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

    this.actionDomeWEST = action -> {
      print("Dome going west...");
      if (!tcs.domeAxisConnection)
        tcs.CmdCupolaOvest(true);
      else
          print("DOME not connected");
    };

    this.actionDomeStop = action -> {
      if (!tcs.domeAxisConnection)
        tcs.CmdStopCupola(true);
      print("done.");
    };
    
    this.actionHomeDome = action -> {
      if (tcs.domeAxisConnection){
        tcs.CmdHomeCupola(true);
        print("Dome home position procedure...");
      }
      else
        print("DOME not connected");

    };
    
    this.actionHomeTel = action -> {
      if (!tcs.xAxisConnection && !tcs.yAxisConnection){
        tcs.CmdHomeTel(true);
        print("Telescope home position procedure...");
      }
      else
        print("Axis not connected");
    };
    
    this.actionTarget = action -> tcs.SetTarget(targetString);

    this.actionCoordinates = action -> {
      print("");
      if (Xvalue > -999){
        tcs.SetTargetAz(Xvalue);
      }
      if (Yvalue > -999){
        tcs.SetTargetEl(Yvalue);
      }

    };

    this.actionVelocity = action -> {
      tcs.SetAbsJogVelocity(commandedVelocity);
      customVelButton.doClick();
    };

    this.actionConnect = action -> {
      tcs.connect();
      if(tcs.xAxisConnection)
        AZconnectionState.setText("Connected");
      else
        AZconnectionState.setText("NOT connected");

      if(tcs.yAxisConnection)
        ELconnectionState.setText("Connected");
      else
        ELconnectionState.setText("NOT connected");

      if(tcs.domeAxisConnection)
        DOMEconnectionState.setText("Connected");
      else
        DOMEconnectionState.setText("NOT connected");


    };

    this.actionDisconnect = action -> {
      this.timerCurrentDomePosition.stop();
      this.timerCurrentPosition.stop();
      this.timerCurrentVelocity.stop();
      this.timerMotors.stop();
      this.timerVelocity.stop();
      this.timerTargetDec.stop();
      this.timerTargetRa.stop();
      tcs.disconnect();
    };

    this.actionPoint = action -> {
      tcs.SetPointingMode();
      tcs.CmdStartPointing(true);
    };

    this.actionPointTrack = action -> {
      tcs.SetPointingMode();
      tcs.CmdPointTrack(true);
    };

    this.actionPadEnabler = action -> {

      boolean padSelection = this.Pad.isSelected();

      if (!padSelection) {
        //this.Pad.setBackground(Color.decode("#ff8d8d"));
        this.Pad.setText("Pad OFF");
        print("Set slewing mode");
        tcs.SetPointingMode();
      }
      else{
        //this.Pad.setBackground(Color.decode("#99ce3e"));
        this.Pad.setText("Pad ON");
        print("Set jogging mode");
        tcs.SetTrackingMode();
      }
    };

    this.actionMotorOn = action -> {
        int status = tcs.MotorOn();
        System.out.println("Setting motors on (1 AZ, 2 EL, 3 both, 0 none):");
        System.out.println(status);


    };

    this.actionMotorOff = action -> {
        int status = tcs.MotorOff();
        System.out.println("Setting motors off (1 AZ, 2 EL, 3 both, 0 none):");
        System.out.println(status);
    };


  }

  //#region interactions
  public void setInteractions(){
    setButtonTarget();
    setSubmitCoordinates();
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
    setCustomSpeed();
    //setJogMode();
    //setSlewMode();
    setPadEnabler();
    setButtonMotorOn();
    setButtonMotorOff();
    setButtonConnect();
    setButtonDisconnect();
    setButtonPoint();
    setButtonVelocity();
    setPointTrack();
  
  }




  //public void SetTimer(ActionListener action){
  //  this.timerUP = new 
  // }

  //#region timers
  Timer timerMotors = new Timer(5000, new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      
      if (tcs.xAxisConnection && tcs.yAxisConnection && tcs.tcsConnection){
        labelAzOnOff.setText("Motor AZ: " + format.format(tcs.GetAzMotorInfo()));
        labelElOnOff.setText("Motor EL: " + format.format(tcs.GetElMotorInfo()));
      }
      else if (tcs.xAxisConnection && tcs.tcsConnection){
        labelAzOnOff.setText("Motor AZ: " + format.format(tcs.GetAzMotorInfo()));
        labelElOnOff.setText("Motor EL: OFF");
      }
      else if (tcs.yAxisConnection && tcs.tcsConnection){
        labelAzOnOff.setText("Motor AZ: OFF");
        labelElOnOff.setText("Motor EL: " + format.format(tcs.GetElMotorInfo()));
      }
      else{
        labelAzOnOff.setText("Motor AZ: OFF");
        labelElOnOff.setText("Motor EL: OFF");
      }      
    }
  });


  Timer timerVelocity = new Timer(3000, new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      
      if (tcs.xAxisConnection && tcs.yAxisConnection && tcs.tcsConnection)
        labelVelocity.setText("Commanded Vel (AZ,EL) (arcs/s): " + format.format(tcs.getcommandedvelAZ()) + " , "+ format.format(tcs.getcommandedvelEL()) );
      else if (tcs.xAxisConnection && tcs.tcsConnection)
        labelVelocity.setText("Commanded Vel (AZ,EL) (arcs/s): " + format.format(tcs.getcommandedvelAZ()) + " , 0");
      else if (tcs.yAxisConnection && tcs.tcsConnection)
        labelVelocity.setText("Commanded Vel (AZ,EL) (arcs/s): 0 , "+ format.format(tcs.getcommandedvelEL()) );
      else
        labelVelocity.setText("Commanded Vel (AZ,EL) (arcs/s): 0 , 0 ");

    }
  });

  /*Timer timerVelocity2 = new Timer(2000, new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
      if (tcs.yAxisConnection && tcs.tcsConnection)
        labelVelocity2.setText("EL vel. - commanded: " + format.format(tcs.getcommandedvelEL()) + " ,  current: "+ format.format(tcs.getactualvelEL()) );
      else
        labelVelocity2.setText("EL vel. - commanded: 0,  current: 0");
      }
  });*/

  Timer timerTargetRa = new Timer(3000, new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        // Update the JTextField with current time (for example)
        labelTargetRa.setText("Target RA:    " + format.format(tcs.gettargetRA()) + "     (AZ: "+ format.format(tcs.gettargetAZ())+")");
    }
  });

  Timer timerTargetDec = new Timer(3000, new ActionListener() {
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
          labelCurrentPosition.setText("Current Tel Position (AZ,EL) (deg):  " + format.format(tcs.getcurrentposAZ()/3600) + " , "+ format.format(tcs.getcurrentposEL()/3600));
        else if (tcs.xAxisConnection && tcs.tcsConnection)
          labelCurrentPosition.setText("Current Tel Position (AZ,EL) (deg):  " + format.format(tcs.getcurrentposAZ()/3600) + " , 0");
        else if (tcs.yAxisConnection && tcs.tcsConnection)
          labelCurrentPosition.setText("Current Tel Position (AZ,EL) (deg):  0 , " + format.format(tcs.getcurrentposEL()/3600));
        else
          labelCurrentPosition.setText("Current Tel Position (AZ,EL) (deg):  0 , 0");
    }
  });

  Timer timerCurrentVelocity = new Timer(2000, new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        // Update the JTextField with current time (for example)
        if (tcs.xAxisConnection && tcs.yAxisConnection && tcs.tcsConnection)
          CurrentVelocity.setText("Current Tel Velocity (AZ,EL) (arcs/s):  " + format.format(tcs.getactualvelAZ()) + " , "+ format.format(tcs.getactualvelEL()));
        else if (tcs.xAxisConnection && tcs.tcsConnection)
          CurrentVelocity.setText("Current Tel Velocity (AZ,EL) (arcs/s):  " + format.format(tcs.getactualvelAZ()) + " , 0");
        else if (tcs.yAxisConnection && tcs.tcsConnection)
          CurrentVelocity.setText("Current Tel Velocity (AZ,EL) (arcs/s):  0 , " + format.format(tcs.getactualvelEL()));
        else
          CurrentVelocity.setText("Current Tel Velocity (AZ,EL) (arcs/s):  0 , 0");
    }
  });

  Timer timerCurrentDomePosition = new Timer(2000, new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        // Update the JTextField with current time (for example)
        if (tcs.xAxisConnection && tcs.yAxisConnection && tcs.tcsConnection)
          CurrentDomePos.setText("Current Dome Position (deg):  " + format.format(tcs.getcurrentposDome()));
        else
          CurrentDomePos.setText("Current Dome Position (deg):  0 ");
    }
  });

  public void print(String string){
    System.out.println(string);
  }

  public void writeTarget(String string){
    this.targetString = string;
  }

  public void writeX(double value){
    this.Xvalue = value;
  }

  public void writeY(double value){
    this.Yvalue = value;
  }

  public void writeVelocity(double value){
    this.commandedVelocity = value;
  }


  
  //#region setters

  public void setButtonTarget(){
    this.buttonTarget.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseReleased(MouseEvent e) {
            writeTarget(textTarget.getText());
            labelTarget.setText("Target: "+targetString);
            textTarget.setText("");
            if (!targetString.equals(""))
              actionTarget.actionPerformed(null);
        }
        });
  }


  public void setSubmitCoordinates(){
    this.SubmitCoord.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseReleased(MouseEvent e) {
            String xString = Xcoord.getText();
            String yString = Ycoord.getText();

            if (coordinates.equals("radec")){
              double xNumber = Double.parseDouble(xString);
              double yNumber = Double.parseDouble(yString);

              //String str = Double.toString(num);  // or String str = String.valueOf(num);
              pos.RaDecToAzAlt(yNumber, xNumber, 0); // 

              xString = Double.toString(pos.AZ);
              yString = Double.toString(pos.ALT);
            }

            if (!xString.equals("") && !yString.equals("")){
              writeX(Double.parseDouble(xString));
              writeY(Double.parseDouble(yString));
              Xcoord.setText("");
              Ycoord.setText("");
              actionCoordinates.actionPerformed(null);
            }
            else if(!xString.equals("") && yString.equals("")){
              writeX(Double.parseDouble(xString));
              Xcoord.setText("");
              actionCoordinates.actionPerformed(null);
            }
            else if(xString.equals("") && !yString.equals("")){
              writeY(Double.parseDouble(yString));
              Ycoord.setText("");
              actionCoordinates.actionPerformed(null);
            }
        }
        });
  }

  public void setButtonVelocity(){
    this.buttonSetVel.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseReleased(MouseEvent e) {
            String absvelString = textSetVel.getText();
            if (!absvelString.equals("")){
              double absvel = Double.parseDouble(absvelString);
              writeVelocity(absvel);
              textSetVel.setText("");
              actionVelocity.actionPerformed(null);
            }
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

  public void setPointTrack(){
    this.PointTrack.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseReleased(MouseEvent e) {
            actionPointTrack.actionPerformed(null);
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

  public void setCustomSpeed(){
    this.customVelButton.addActionListener(actionCustomSpeed);
  }

  public void setRaDec(){
    this.radecButton.addActionListener(actionRaDec);
  }

  public void setAzEl(){
    this.azelButton.addActionListener(actionAzEl);
  }

  /* 
  public void setJogMode(){
    this.jogButton.addActionListener(actionJogMode);
  }

  public void setSlewMode(){
    this.slewButton.addActionListener(actionSlewMode);
  }
  */

  public void setPadEnabler(){
    this.Pad.addActionListener(actionPadEnabler);
  }

  public void setButtonMotorOn(){
    this.MotorOn.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseReleased(MouseEvent e) {
          actionMotorOn.actionPerformed(null);
      }
      });
  }

  public void setButtonMotorOff(){
    this.MotorOff.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseReleased(MouseEvent e) {
          actionMotorOff.actionPerformed(null);
      }
      });
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