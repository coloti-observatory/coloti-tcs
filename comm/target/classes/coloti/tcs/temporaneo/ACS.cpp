// ACS.cpp: implementation of the ACS class.
//
//////////////////////////////////////////////////////////////////////

#include "stdafx.h"
#include "tcs.h"
#include "ACS.h"
#include <stdio.h>
#include <stdarg.h>
#include <string.h>
#include <conio.h>
#include <math.h>
#ifdef _DEBUG
#undef THIS_FILE
static char THIS_FILE[]=__FILE__;
#define new DEBUG_NEW
#endif

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////
/*
	SB202_Comando_Tell(ComPort,"T0");
	if (((Tell_0.T_Stato_MotorX)&(1<<3))==0)
		frm4_val.v_string="Off";
	else frm4_val.v_string="On ";
		tcu_put_field(&form4,ab_x,&frm4_val);
	if (((Tell_0.T_Stato_MotorY)&(1<<3))==0)
		frm4_val.v_string="Off";
	else frm4_val.v_string="On ";
		tcu_put_field(&form4,ab_y,&frm4_val);

	if (((MotoX=Tell_0.T_Stato_MotorX),MotoX=(MotoX&(1<<0))),MotoX==0)
		frm4_val.v_string="Off";
	else
		frm4_val.v_string="On";
	tcu_put_field(&form4,ab_y+1,&frm4_val);
	if (((MotoY=Tell_0.T_Stato_MotorY),MotoY=(MotoY&(1<<0))),MotoY==0)
		frm4_val.v_string="Off";
	else
		frm4_val.v_string="On";
	tcu_put_field(&form4,ab_y+2,&frm4_val);
	frm4_val.v_string="";
	if ((MotoX==0)&&(MotoY==0))
	{
		frm4_val.v_string="Fermo";
		Moto_Orario=0;
		tcu_put_field(&form4,ab_y+3,&frm4_val);
	}*/




//#define dnint(A) ((A)<0.0?ceil((A)-0.5):floor((A)+0.5))
//#define D2PI 6.2831853071795864769252867665590057683943387987502

//#include "seriale.h"
#define mod(i,j)  (i-(int)(i/j)*j)

//extern Tell0        Tell_0;
//extern Tell1        Tell_1;
//extern Tell2        Tell_2;

//unsigned char Risposta_Seriale[256];  /*Risposta Spedita dall'SB202*/
//LONG Conversione_Dati_Rx(unsigned char D_Trasf[],unsigned char D_Ntrasf[]);

//CCommClass ACS::CommPort;
//int ACS::CommStatus;
ACS::ACS()
{
	axes[0]='X';
	axes[1]='Y';
	axes[2]='Z';
	CONVFACTOR[X]=1.;
	CONVFACTOR[Y]=1.;
	CONVFACTOR[Z]=1.;
	GEARRATIO[X]=1.;
	GEARRATIO[Y]=1.;
	GEARRATIO[Z]=1.;
	MAXMIS[RAD]=D2PI;
	MAXMIS[GRAD]=360.;
	MAXMIS[HOUR]=24.;
	MAXMIS[ENC]=1.;
	MAXMIS[ARCSECS]=1296000.0;
	UM=ENC;
	MAXINP=8;
	NAXES=1;
	CommStatus=0;
}


ACS::ACS(int nax, TCHAR *IdSeriale,DWORD baud,BYTE bytesize,BYTE stop,BYTE parity,int Timeout )
{
	//Port=Po;
	//: CCommClass(IdSeriale,baud,bytesize,stop,parity,Timeout)
	LONG temp;
	int i;
	double val, val1;
	axes[0]='X';
	axes[1]='Y';
	axes[2]='Z';
	CONVFACTOR[X]=1.;
	CONVFACTOR[Y]=1.;
	CONVFACTOR[Z]=1.;
	GEARRATIO[X]=1.;
	GEARRATIO[Y]=1.;
	GEARRATIO[Z]=1.;
	MAXMIS[RAD]=D2PI;
	MAXMIS[GRAD]=360.;
	MAXMIS[HOUR]=24.;
	MAXMIS[ENC]=1.;
	MAXMIS[ARCSECS]=1296000.0;
	UM=ENC;
	MAXINP=8;
	NAXES=nax;
	MAXSYSINP=nax*3+1;
	
	if (CommPort.Status==0)
		CommPort.Open("\\\\.\\COM2",baud,bytesize,stop,parity,Timeout);
	if(CommPort.Port==NULL) AfxMessageBox("Impossibile connettersi alla COM2");
	else
	{
	SetHostMode();
	for (i=0;i<nax;i++)
	{
	GetEncoderRes((AXES)i,&temp);
	ENCODERRES[i]=(double)temp;
	GEARRATIO[i]=1.;
	CONVFACTOR[i]=1.;
	GetMotionMode((AXES) i,&temp);
	if(temp==0)MOTION_MODE[i]=0;
	if(temp==10)MOTION_MODE[i]=1;
	MOTOR_STATUS[i]=0;
	MAX_ABS_VEL[i]=MAX_VEL[i]=ENCODERRES[i];
	MIN_ABS_VEL[i]=MIN_VEL[i]=0;
	MAX_ABS_ACC[i]=MAX_ACC[i]=ENCODERRES[i];
	MIN_ABS_ACC[i]=MIN_ACC[i]=1000;
	GetMotMaxMinPos((AXES)i,&val,&val1);
	//printf("\n res enco %d :%lf",i, ENCODERRES[i]);
	}
	CommStatus=1;
	}
}

ACS::ACS(int nax, TCHAR *IdSeriale,DWORD baud)
{
	//Port=Po;
	//: CCommClass(IdSeriale)
	LONG temp;
	int i;
	double val, val1;
	
	axes[0]='X';
	axes[1]='Y';
	axes[2]='Z';
	CONVFACTOR[X]=1.;
	CONVFACTOR[Y]=1.;
	CONVFACTOR[Z]=1.;
	GEARRATIO[X]=1.;
	GEARRATIO[Y]=1.;
	GEARRATIO[Z]=1.;
	NAXES=nax;
	MAXSYSINP=nax*3+1;
	MAXINP=8;
	
	MAXMIS[RAD]=D2PI;
	MAXMIS[GRAD]=360.;
	MAXMIS[HOUR]=24.;
	MAXMIS[ENC]=1.;
	MAXMIS[ARCSECS]=1296000.0;
	UM=ENC;
	if (CommPort.Status==0)
		CommPort.Open("\\\\.\\COM2",baud);
	if(CommPort.Port==NULL) AfxMessageBox("Impossibile connettersi alla COM2");
	else
	{
	SetHostMode();
	for (i=0;i<nax;i++)
	{
	GetEncoderRes((AXES)i,&temp);
	
	ENCODERRES[i]=(double)temp;
	GEARRATIO[i]=1.;
	CONVFACTOR[i]=1.;
	GetMotionMode((AXES) i,&temp);
	if(temp==0)MOTION_MODE[i]=0;
	if(temp==10)MOTION_MODE[i]=1;
	MOTOR_STATUS[i]=0;
	MAX_ABS_VEL[i]=MAX_VEL[i]=ENCODERRES[i];
	MIN_ABS_VEL[i]=MIN_VEL[i]=-MAX_VEL[i];
	MAX_ABS_ACC[i]=MAX_ACC[i]=ENCODERRES[i];
	MIN_ABS_ACC[i]=MIN_ACC[i]=1000;
	GetMotMaxMinPos((AXES)i,&val,&val1);
	//printf("\n res enco %d :%lf",i, ENCODERRES[i]);
	}
	CommStatus=1;
	}
}



ACS::~ACS()
{

	if(CommStatus==1)
	{
	for (int i=0;i<NAXES;i++)
		SetMotorOff((AXES)i);

	CommPort.Close();
	}
}
/* ACS::
{

}*/
//public function

int ACS::OpenComm(int port,DWORD baud)
{
	char IdSeriale[30];
	LONG temp;
	sprintf(IdSeriale,"\\\\.\\COM%d",port);
	if (CommPort.Status==0)
		CommPort.Open(IdSeriale,baud);
	if(CommPort.Port==NULL) 
	{
		//AfxMessageBox("Impossibile Comunicare con il Controllore");
		return -1;
	}
	CommStatus=1;
	//AfxMessageBox("Connessione riuscita");
	SetHostMode();
	for (int i=0;i<NAXES;i++)
	{
	GetEncoderRes((AXES)i,&temp);
	ENCODERRES[i]=(double)temp;
	GEARRATIO[i]=1.;
	CONVFACTOR[i]=1.;
	GetMotionMode((AXES) i,&temp);
	if(temp==0)MOTION_MODE[i]=0;
	if(temp==10)MOTION_MODE[i]=1;
	MOTOR_STATUS[i]=0;
	MAX_ABS_VEL[i]=MAX_VEL[i]=ENCODERRES[i];
	MIN_ABS_VEL[i]=MIN_VEL[i]=0;
	MAX_ABS_ACC[i]=MAX_ACC[i]=ENCODERRES[i];
	MIN_ABS_ACC[i]=MIN_ACC[i]=1000;
	//GetMotMaxMinPos((AXES)i,&val,&val1);
	//printf("\n res enco %d :%lf",i, ENCODERRES[i]);
	}

	return 0;
}

int ACS::OpenComm()
{
	char IdSeriale[30];
	sprintf(IdSeriale,"\\\\.\\COM%d",m_Port);
	//AfxMessageBox(IdSeriale);
	if (CommPort.Status==0)
		CommPort.Open(IdSeriale,(DWORD)m_Baud);
	if(CommPort.Port==NULL) 
	{
		//AfxMessageBox("Impossibile Comunicare con il Controllore");
		return -1;
	}
	else
	{
	CommStatus=1;
	//AfxMessageBox("Connessione riuscita");
	SetHostMode();
	//AfxMessageBox("SetHost Mode");
	return 0;
	}

}

int ACS::InitAxes()
{
	double  val=0.,val1=0.;
	LONG temp;
	int err;
	
	
	if(CommStatus==1){
		
		for (int i=0;i<NAXES;i++){
			err=GetEncoderRes((AXES)i,&temp);
			
			ENCODERRES[i]=(double)temp;
			GEARRATIO[i]=1.;
			CONVFACTOR[i]=1.;
			err=GetMotionMode((AXES) i,&temp);
			
			if(temp==0)MOTION_MODE[i]=0;
			if(temp==10)MOTION_MODE[i]=1;
			MOTOR_STATUS[i]=0;
			MAX_ABS_VEL[i]=MAX_VEL[i]=ENCODERRES[i];
			MIN_ABS_VEL[i]=MIN_VEL[i]=0;
			MAX_ABS_ACC[i]=MAX_ACC[i]=ENCODERRES[i];
			MIN_ABS_ACC[i]=MIN_ACC[i]=1000;
			
			//printf("\n res enco %d :%lf",i, ENCODERRES[i]);
		}
		return err;
	}
	return 1;
}

void ACS::CloseComm()
{
	if(CommPort.Status)
		CommPort.Close();
	CommStatus=0;
}

int ACS::Move(AXES ax,double pos)
{
		if (MOTOR_STATUS[ax]==0)
		  if (SetMotorOn(ax)!=ACSOK)
			return ACS_MOTOR_ERROR;
     	if(SetAbsTargPos(ax,pos)!=ACSOK)
			return ACS_MOTOR_ERROR;
		if(StartMove(ax)!=ACSOK)
			return ACS_MOTOR_ERROR;
		return ACSOK;
}

int ACS::Move(AXES ax,double pos, double vel)
{
		if(SetMotVel(ax,vel)!=ACSOK)
			return ACS_MOTOR_ERROR;
		if(Move(ax,pos)!=ACSOK)
			return ACS_MOTOR_ERROR;
		return ACSOK;
}

int ACS::Move(AXES ax,double pos,double vel,double acc)
{
	if(SetMotAcc(ax,acc)!=ACSOK)
			return ACS_MOTOR_ERROR;
	if(Move(ax,pos,vel)!=ACSOK)
			return ACS_MOTOR_ERROR;
	return ACSOK;
}

int ACS::Move(AXES ax,double pos,double vel,double acc, double dec)
{
	if(SetMotDec(ax,dec)!=ACSOK)
			return ACS_MOTOR_ERROR;
	if(Move(ax,pos,vel,acc)!=ACSOK)
			return ACS_MOTOR_ERROR;
	return ACSOK;
}

int ACS::MoveTrack(AXES ax,double pos, double trackvel)
{
	MSG message;
	if (SetSlewMode(ax)!=ACSOK)
		return ACS_MOTOR_ERROR;
	if(Move(ax,pos)!=ACSOK)
		return ACS_MOTOR_ERROR;
	if (SetTrackMode(ax)!=ACSOK)
		return ACS_MOTOR_ERROR;
	while(GetEndMotionStatus(ax)!=1)
	{
		
		if(::PeekMessage(&message,NULL,0,0,PM_REMOVE)){
			::TranslateMessage(&message);
			::DispatchMessage(&message);
		}
	}; //sostituire con un thread
	if(SetMotVel(ax,trackvel)!=ACSOK)
		return ACS_MOTOR_ERROR;
	if(StartMove(ax)!=ACSOK)
		return ACS_MOTOR_ERROR;
	return ACSOK;
}

void ACS::SetMaxMinVel(AXES ax,double maval,double mival)
{
		MAX_VEL[(int)ax]= maval;
		if(MAX_VEL[ax]>MAX_ABS_VEL[ax])
			MAX_VEL[ax]=MAX_ABS_VEL[ax];
		MIN_VEL[(int)ax]= mival;
		if(MIN_VEL[ax]>MIN_ABS_VEL[ax])
			MIN_VEL[ax]=MIN_ABS_VEL[ax];
		//char fff[50];
		//sprintf(fff,"%ld",temp);
	    //AfxMessageBox(fff);
}

void ACS::SetMaxMinAcc(AXES ax,double maval,double mival)
{
		MAX_ACC[(int)ax]= maval;
		if(MAX_ACC[ax]>MAX_ABS_ACC[ax])
			MAX_ACC[ax]=MAX_ABS_ACC[ax];
		MIN_ACC[(int)ax]= mival;
		if(MIN_ACC[ax]>MIN_ABS_ACC[ax])
			MIN_ACC[ax]=MIN_ABS_ACC[ax];
}

void ACS::SetMaxMinPos(AXES ax,double maval,double mival)
{
		MAX_POS[(int)ax]= maval;
		MIN_POS[(int)ax]= mival;
}

// AsseX.SetUserUnit(X,ARCSECS, Telescopio.m_Ridaz*Motori.m_RidMot1);
void ACS::SetUserUnit(AXES ax,UNITM umm, double gr)
{
	switch(umm)
	{ case RAD:
		UM=RAD;
		CONVFACTOR[ax]=gr*ENCODERRES[ax]/MAXMIS[RAD];
		break;
	  case GRAD:
		  UM=GRAD;
		  CONVFACTOR[ax]=gr*ENCODERRES[ax]/MAXMIS[GRAD];
		  //printf("\n conv fact:%lf",CONVFACTOR[ax]);
		break;
	  case HOUR:
		  UM=HOUR;
		  CONVFACTOR[ax]=gr*ENCODERRES[ax]/MAXMIS[HOUR];
		case ARCSECS:
		  UM=ARCSECS;
		  CONVFACTOR[ax]=gr*ENCODERRES[ax]/MAXMIS[ARCSECS];
		break;
	  default:
		  break;
	}
	/*MAX_VEL[ax]=ENCODERRES[ax]/CONVFACTOR[ax];
	if(MAX_VEL[ax]>MAX_ABS_VEL[ax])
		MAX_VEL[ax]=MAX_ABS_VEL[ax];
	MAX_ACC[ax]=MAX_VEL[ax];
	MIN_VEL[ax]=-MAX_VEL[ax];
	MIN_ACC[ax]=-MAX_ACC[ax];*/
	
}

void ACS::MotConfig(AXES ax,UNITM um, double gr, double rev)
{
	SetUserUnit(ax,um,gr);
	MAX_VEL[ax]=MAX_ABS_VEL[ax]=0.85*(rev/60.)*ENCODERRES[ax]/CONVFACTOR[ax];
	MAX_ABS_ACC[ax]=MAX_ACC[ax]=MAX_VEL[ax];
	MIN_ABS_VEL[ax]=MIN_VEL[ax]=-MAX_VEL[ax];
	MIN_ABS_ACC[ax]=1000/CONVFACTOR[ax];
//	printf("\n conv fact:%lf",CONVFACTOR[ax]);
}

int ACS::SetTermMode()
{
	ErrorCode=Comando_Set("SHT",0);
	ErrorCode=Comando_Set("SHT",1);
	//Write_Comm_Char(CR);
	return ErrorCode;
}

int ACS::SetHostMode()
{
	ErrorCode=Comando_Set("SHT",0);
	//ErrorCode=Comando_Set("SHT",0);
	return ErrorCode;
}

void ACS::SetNumberIOPort(int n)
{
	MAXINP=n;
}

int ACS::SetMotorOn(AXES ax)
{
	if(GetMotorStatus(ax)==0)
	{
		sbld(command,"S%cMO",axes[(int)ax]);
		ErrorCode=Comando_Set(command,1L);
		if(ErrorCode==-1){
			MOTOR_STATUS[(int)ax]=1;
			return ACSOK;
		}
		else
			return ErrorCode;
	}
	else 
		return ACSOK;
}

int ACS::SetMotorOff(AXES ax)
{
	if(GetMotorStatus(ax)==1)
	{
		sbld(command,"S%cMO",axes[(int)ax]);
		ErrorCode=Comando_Set(command,0L);
		if(ErrorCode==-1){
			MOTOR_STATUS[(int)ax]=0;
			return ACSOK;
		}
		else
			return ErrorCode;
	}
	else 
		return ACSOK;
}

int ACS::SetAbsTargPos(AXES ax,double val)
{
	LONG temp=0L;
	//if(val>MAX_POS[ax] || val<MIN_POS[ax]) 
		//return ACS_POS_OVERFLOW;
	temp=(LONG)dnint(CONVFACTOR[ax]*val);
	sbld(command,"S%cAP",axes[(int)ax]);
	ErrorCode=Comando_Set(command,temp);
	return ErrorCode;
}

int ACS::SetRelTargPos(AXES ax,double val)
{
	LONG temp=0L;
	if(val>MAX_POS[ax] || val<MIN_POS[ax]) 
		return ACS_POS_OVERFLOW;
	temp=(LONG)dnint(CONVFACTOR[ax]*val);
	sbld(command,"S%cRP",axes[(int)ax]);
	ErrorCode=Comando_Set(command,temp);
	return ErrorCode;
}

int ACS::SetAxisZeroPos(AXES ax,double val)
{
	LONG temp=0L;
	temp=(LONG)dnint(CONVFACTOR[ax]*val);
	sbld(command,"S%cZP",axes[(int)ax]);
	ErrorCode=Comando_Set(command,temp);
	return ErrorCode;
}

int ACS::SetSlewMode(AXES ax)
{

	if(MOTION_MODE[(int)ax]!=0)
	{
		sbld(command,"S%cMM",axes[(int)ax]);
		ErrorCode=Comando_Set(command,0L);
		if(ErrorCode==-1)MOTION_MODE[(int)ax]=0;
		return ErrorCode;
	}
	return ACSOK;
}

int ACS::SetTrackMode(AXES ax)
{
	if(MOTION_MODE[(int)ax]!=1)
	{
		sbld(command,"S%cMM",axes[(iValorent)ax]);
		ErrorCode=Comando_Set(command,10L);
		if(ErrorCode==-1)MOTION_MODE[(int)ax]=1;
		return ErrorCode;
	}
	return ACSOK;
}

int  ACS::SetMotVel(AXES ax,double val)
{
	LONG temp=0L;
	if(val >MAX_VEL[ax]) val=MAX_VEL[ax];
	if(val <MIN_VEL[ax]) val=MIN_VEL[ax];
	temp=(LONG)dnint(CONVFACTOR[ax]*val);
	sbld(command,"S%cLV",axes[(int)ax]);
	ErrorCode=Comando_Set(command,temp);
	return ErrorCode;
}

int  ACS::SetMotAcc(AXES ax,double val)
{
	LONG temp=0L;
	if(val >MAX_ACC[ax]) val=MAX_ACC[ax];
	if(val <MIN_ACC[ax]) val=MIN_ACC[ax];
	temp=(LONG)dnint(CONVFACTOR[ax]*val);
	sbld(command,"S%cLA",axes[(int)ax]);
	ErrorCode=Comando_Set(command,temp);
	return ErrorCode;
}

int  ACS::SetMotDec(AXES ax,double val)
{
	LONG temp=0L;
	if(val >MAX_ACC[ax]) val=MAX_ACC[ax];
	temp=(LONG)dnint(CONVFACTOR[ax]*val);
	sbld(command,"S%cLD",axes[(int)ax]);
	ErrorCode=Comando_Set(command,temp);
	return ErrorCode;
}

int ACS::SetOutPortOn(int ipno)
{
	if(ipno > MAXINP) return -2;
	ErrorCode=Comando_Set("SHI",(LONG)ipno);
	return ErrorCode;
}

int ACS::SetOutPortOff(int ipno)
{
	if(ipno > MAXINP) return -2;
	ErrorCode=Comando_Set("SLO",(LONG)ipno);
	return ErrorCode;
}

//
//// Funzioni Get  
//

int  ACS::GetMotorStatus(AXES ax)
{
	ErrorCode=Comando_Tell("T0");
	
	if (ax==X){
	if (((Tell_0.T_Stato_MotorX)&(1<<3))==0)
		MOTOR_STATUS[(int)ax]=0; 
	else 
		MOTOR_STATUS[(int)ax]=1;
	}

	if (ax==Y){
	if (((Tell_0.T_Stato_MotorY)&(1<<3))==0)
		MOTOR_STATUS[(int)ax]=0;
	else 
		MOTOR_STATUS[(int)ax]=1;
	}
	if (ax==Z){
	if (((Tell_0.T_Stato_MotorZ)&(1<<3))==0)
		MOTOR_STATUS[(int)ax]=0;
	else 
		MOTOR_STATUS[(int)ax]=1;
	}
	return (int)MOTOR_STATUS[(int)ax];
}

int ACS::IsMoving(AXES ax)
{
	int res;
		ErrorCode=Comando_Tell("T0");

	if (ax==X){
		if (((Tell_0.T_Stato_MotorX)&(1))==0)
			res=0;
		else 
			res=1;
	}
	
	if (ax==Y){
		if (((Tell_0.T_Stato_MotorY)&(1))==0)
			res=0;
		else 
			res=1;
	}

	if (ax==Z){
		if (((Tell_0.T_Stato_MotorZ)&(1))==0)
			res=0;
		else 
			res=1;
	}
	return res;
	
}

int ACS::GetEndMotionStatus(AXES ax)
{
	ErrorCode=Comando_Tell("T2");

	if (ax==X)return Tell_2.T2_DataX;
	if (ax==Y)return Tell_2.T2_DataY;
	if (ax==Z)return Tell_2.T2_DataZ;
	return ACSOK;
}

int ACS::GetEncoderRes(AXES ax,LONG *Valore)
{
	LONG temp=0L;
	sbld(command,"R%cLR",axes[(int)ax]);
	ErrorCode=Comando_Report(command,Valore);
	if (ErrorCode!=ACSOK)return ErrorCode;
	sbld(command,"R%cLF",axes[(int)ax]);
	ErrorCode=Comando_Report(command,&temp);
	*Valore=*Valore*((LONG)dnint(pow(2.,temp)));
	return ErrorCode;

}

int ACS::GetMotPos(AXES ax,double *Valore)
{	
	LONG temp=0L;
	sbld(command,"R%cCP",axes[(int)ax]);
	ErrorCode=Comando_Report(command,&temp);
	//printf("\n Pos= %ld",temp);
	*Valore=((double)temp)/CONVFACTOR[(int)ax];
	return ErrorCode;
}

int ACS::GetMotEncPos(AXES ax,LONG *Valore)
{	
	//LONG temp=0L;
	sbld(command,"R%cCP",axes[(int)ax]);
	ErrorCode=Comando_Report(command,Valore);
	//printf("\n Pos= %ld",temp);
	//*Valore=((double)temp)/CONVFACTOR[(int)ax];
	return ErrorCode;
}

int  ACS::GetMotVel(AXES ax,double *Valore)
{	
	LONG temp=0L;
	sbld(command,"R%cLV",axes[(int)ax]);
	ErrorCode=Comando_Report(command,&temp);
	//char fff[50];
	//	sprintf(fff,"%ld",temp);
	//AfxMessageBox(fff);
	*Valore=((double)temp)/CONVFACTOR[(int)ax];
	return ErrorCode;
}

int  ACS::GetMotAcc(AXES ax,double *Valore)
{
	LONG temp=0L;
	sbld(command,"R%cLA",axes[(int)ax]);
	ErrorCode=Comando_Report(command,&temp);
	*Valore=((double)temp)/CONVFACTOR[ax];
	return ErrorCode;
}

int  ACS::GetMotDec(AXES ax,double *Valore)
{	
	LONG temp=0L;
	sbld(command,"R%cLD",axes[(int)ax]);
	ErrorCode=Comando_Report(command,&temp);
	*Valore=((double)temp)/CONVFACTOR[ax];
	return ErrorCode;
}

int ACS::GetMotMaxMinPos(AXES ax,double *Valore,double *Valore1)
{
	LONG temp=0L;
	sbld(command,"R%cPH",axes[(int)ax]);
	ErrorCode=Comando_Report(command,&temp);
	if(ErrorCode==ACSOK)
		MAX_POS[ax]=*Valore=(double)temp/CONVFACTOR[ax];
	else 
		return ErrorCode;
	sbld(command,"R%cPL",axes[(int)ax]);
	ErrorCode=Comando_Report(command,&temp);
	if(ErrorCode==ACSOK)
		MIN_POS[ax]=*Valore1=(double)temp/CONVFACTOR[ax];
	return ErrorCode;
}

int ACS::GetMotionMode(AXES ax,LONG *Valore)
{
	sbld(command,"R%cMM",axes[(int)ax]);
	ErrorCode=Comando_Report(command,Valore);
	return ErrorCode;
}

int ACS::GetSysInp(int ipno, LONG *Valore)
{
	if(ipno > MAXSYSINP) return -2;
	ErrorCode=Comando_Report_Parametri(ipno,"RSI",Valore);
	return ErrorCode;
}

int ACS::GetInpLog(int ipno,LONG *Valore)
{
	if(ipno > MAXSYSINP) return -2;
	ErrorCode=Comando_Report_Parametri(ipno,"RIL",Valore);
	return ErrorCode;
}

int ACS::GetInpPortStatus(int ipno,LONG *Valore)
{
	if(ipno > MAXINP) return -2;
	ErrorCode=Comando_Report_Parametri(ipno,"RIP",Valore);
	return ErrorCode;
}

int ACS::GetOutPortStatus(int ipno,LONG *Valore)
{
	if(ipno > MAXINP) return -2;
	ErrorCode=Comando_Report_Parametri(ipno,"ROP",Valore);
	return ErrorCode;
}

// utility

int ACS::StartMove(AXES ax)
{
	sbld(command,"B%c",axes[(int)ax]);
	ErrorCode=Comando_Moto(command);
	return ErrorCode;
}


int ACS::StopMove(AXES ax)
{
	sbld(command,"K%c",axes[(int)ax]);
	ErrorCode=Comando_Moto(command);
	if(IsMoving(ax))
		Sleep(500);
	return ErrorCode;
}

int  ACS::ExecProg(int id)
{
	sbld(command,"PX%d",id);
	ErrorCode=Comando_Moto(command);
	return ErrorCode;
}

int  ACS::ExecProg(char *Label)
{
	sbld(command,"PX%s",Label);
	ErrorCode=Comando_Moto(command);
	return ErrorCode;
}



int ACS::Consolle()
{
  char ch=0;
  int f=0;
	
	//printf("\n **** Per terminare premere ESC****\n");


	char piso[255];
	char pis[1024];
	char posta[255];
	DWORD result;
	if( CommStatus==1)SetTermMode();
	AllocConsole();
	SetConsoleTitle("Comunicazione Diretta ACS Controller");
	HANDLE hStdin =GetStdHandle(STD_INPUT_HANDLE);
	HANDLE hStdout =GetStdHandle(STD_OUTPUT_HANDLE);
	//SetConsoleTextAttribute(hStdout,FOREGROUND_RED | BACKGROUND_RED | BACKGROUND_GREEN | BACKGROUND_BLUE );
	//wsprintf(piso,"Command>");
	//WriteConsole(hStdout,piso,strlen(piso),&result,NULL);
	//ReadConsole(hStdin,&pis,80,&result,NULL);
	//pis[result]='\0';
	//wsprintf(piso,"%s",pis);
	//WriteConsole(hStdout,piso,strlen(piso),&result,NULL);
  do
  {
	//wsprintf(piso,"Command>");
	posta[0]=0;
	pis[0]=0;
	WriteConsole(hStdout,"Comando>",strlen("Command>"),&result,NULL);
	ReadConsole(hStdin,&pis,80*sizeof(TCHAR),&result,NULL);
	//wsprintf(piso,"lunghezza %d",result);
	//WriteConsole(hStdout,piso,strlen(piso),&result,NULL);
	pis[result]='\0';
	strncpy(posta,pis,(result-2));
	if(!strncmp(pis,"FINE",4)) break;
	posta[result-2]='\0';
	//wsprintf(piso,"lunghezza %d",result);
	//WriteConsole(hStdout,posta,strlen(posta),&result,NULL);
	if(CommStatus==1){
	CommPort.Scrittura_Comm((TCHAR *)posta);
	result=(int)CommPort.Lettura_Comm((TCHAR *)pis);
	pis[result]='\0';
	WriteConsole(hStdout,pis,strlen(pis),&result,NULL);
	}
	else
	  WriteConsole(hStdout,posta,strlen(posta),&result,NULL);

    /*f=CommPort.Read_Comm_Char();
    if(f!=1)
		putchar(f);
    if(_kbhit()!=0)
    {
		ch=toupper(_getche());
	if(ch==27)
	    break;
	else
	{
	    CommPort.Write_Comm_Char(ch);
	    if (ch==0x0d) putchar(0x0a);
	}
     }*/
   }while(TRUE);
  if( CommStatus==1)SetHostMode();
  //Sleep(100);
  FreeConsole();
  return 0;
}
// private function
void ACS::sbld(char * dest,char *control, ...)
{
   char buf[255];
   va_list args;

   va_start(args, control);     /* get variable arg pointer */
   vsprintf(buf,control,args);  /* format into buf with variable args */
   va_end(args);                /* finish the arglist */

   strcpy(dest,buf);            /* copy result */
}

LONG ACS::Conversione_Dati_Rx(unsigned char D_Trasf[],unsigned char D_Ntrasf[])
{
  LONG Val_Numero,Num[4];
	 char pis[20];
	 int i;
	 for (i=0;i<4;i++){
		D_Ntrasf[i]=D_Trasf[i];
		Num[i]=(LONG)D_Trasf[i];
	 }
	 if (Num[0] >127)
	 	Val_Numero = (Num[3] - 256L) + (Num[2] - 255L) * 256L + (Num[1] - 255L) * 65536 + (Num[0] - 255) * 16777216;
	 else Val_Numero = Num[3] + Num[2] * 256L + Num[1] * 65536L + Num[0] * 16777216L;
 	 sbld(pis,"%ld",Val_Numero);
	 //printf("**%ld** %s",Val_Numero,pis);
	 return Val_Numero;
}   /* Conversione_Dati_Rx */

int ACS::Scrittura_Seriale(char c[],long int Valore[],int Num_Dati)
/* Spedisce una stringa attraverso la porta seriale al SB202:
	Num_Dati contiene il numero dei campi di dati da spedire*/
{
  int i,j,CheckSum=0;
  char Dati[4];

  for(i=0;i<(int)strlen(c);i++)
   {
	 CheckSum=CheckSum+c[i];
	 CommPort.Write_Comm_Char(c[i]);
   }
   for (j=0;j<Num_Dati;j++)
   {
	 for(i=0;i<4;i++)
		Dati[3-i]=(int)(Valore[j]>>8*i);
	 for(i=0;i<4;i++)
	  {
	   if ((Dati[i]==16)||(Dati[i]==13))
	   	CommPort.Write_Comm_Char(0x010);
		 
	   CheckSum=CheckSum+Dati[i];
	   CommPort.Write_Comm_Char(Dati[i]);
	  }
   }
   CheckSum=mod(CheckSum,256);
   if ((CheckSum==13)||(CheckSum==16)) CheckSum+=128;
   	CommPort.Write_Comm_Char(CheckSum);
   CommPort.Write_Comm_Char(CR);
   return 0;
} /*Scrittura_Seriale*/

int ACS::Lettura_Seriale(unsigned char c[])
{
	return(CommPort.Lettura_Comm((TCHAR *)c));
} /*Lettura_Seriale*/


int ACS::Togli_DLE(int *Num_Carattere)
{
	int    Cont,Cont1=0, Car_da_sottrarre=0;

	for (Cont = 1; Cont <= *Num_Carattere; Cont++)
	{
		Cont1 = Cont1 + 1;
		if (Risposta_Seriale[Cont-1] == (0x10))
		{
			if ((Risposta_Seriale[Cont + 1-1] == (0x0D)) || (Risposta_Seriale[Cont + 1-1] == (0x10)))
			{
				Risposta_Seriale[Cont1-1] = Risposta_Seriale[Cont + 1-1];
				Cont = Cont + 1; Car_da_sottrarre = Car_da_sottrarre + 1;
			}
		}
		else Risposta_Seriale[Cont1-1] = Risposta_Seriale[Cont-1];
	}
	*Num_Carattere = *Num_Carattere - Car_da_sottrarre;
	return 0;
}   /* Togli_DLE */

int	 ACS::Errore(void)
{
  int P_Interrogativo=0;

  if (Risposta_Seriale[0] == '?') P_Interrogativo = 1;
  if (Risposta_Seriale[1] == '?') P_Interrogativo = 2;
  if (P_Interrogativo != 0)
	return Risposta_Seriale[P_Interrogativo];
  else return -1;
}   /* Errore */

int ACS::Scansione_Tell(char SubComando)
{

  switch (SubComando)
  {     
	case '0':Tell_0.T0_Controllore=Risposta_Seriale[0];
		Tell_0.T_Stato_MotorX=Risposta_Seriale[1];
		Tell_0.T_Modo_MotX=Risposta_Seriale[2];
		Tell_0.T_SModo_MotX=Risposta_Seriale[3];
		Tell_0.T_StepX=Risposta_Seriale[4];
		

		Tell_0.T_Stato_MotorY=Risposta_Seriale[5];
		Tell_0.T_Modo_MotY=Risposta_Seriale[6];
		Tell_0.T_SModo_MotY=Risposta_Seriale[7];
		Tell_0.T_StepY=Risposta_Seriale[8];
		Tell_0.T_Stato_MotorZ=Risposta_Seriale[9];
		Tell_0.T_Modo_MotZ=Risposta_Seriale[10];
		Tell_0.T_SModo_MotZ=Risposta_Seriale[11];
		Tell_0.T_StepZ=Risposta_Seriale[12];
		Tell_0.T_Inform=Risposta_Seriale[5];
		Tell_0.T0_CheckSum=Risposta_Seriale[6];
		break;
  case '1':Tell_1.T1_Controllore=Risposta_Seriale[0];
		Tell_1.T1_Codice=Risposta_Seriale[1];
		Tell_1.T1_DataX=Risposta_Seriale[2];
		Tell_1.T1_DataY=Risposta_Seriale[3];
		Tell_1.T1_DataZ=Risposta_Seriale[4];
		Tell_1.T1_DataT=Risposta_Seriale[5];
		Tell_1.T1_CheckSum=Risposta_Seriale[6];
		break;
  case '2':Tell_2.T2_Controllore=Risposta_Seriale[0];
		Tell_2.T2_Codice=Risposta_Seriale[1];
		Tell_2.T2_DataX=Risposta_Seriale[2];
		Tell_2.T2_DataY=Risposta_Seriale[3];
		Tell_2.T2_DataZ=Risposta_Seriale[4];
		Tell_2.T2_DataT=Risposta_Seriale[5];
		Tell_2.T2_CheckSum=Risposta_Seriale[6];
		break;
 }
 return 0;
}   /* Scansione_Tell */

int	 ACS::Comando_Set(char *Istruzione,long int Valore)
{
  int Err;
  Scrittura_Seriale(Istruzione,&Valore,1);
  Lettura_Seriale(Risposta_Seriale);
  if ((Err=Errore())!=-1) return Err;
  else return -1;
}   /* Comando_Set */

int	 ACS::Comando_Report(char *Istruzione,LONG *Valore)
{
  int Count,Err,Num_Carattere=0;
  unsigned char Dati_Nt[20];
  unsigned char Dati[20];
  LONG Val=0L;
  
  Scrittura_Seriale(Istruzione,&Val,0);
  Num_Carattere=Lettura_Seriale(Risposta_Seriale);
  Risposta_Seriale[Num_Carattere]='\0';
  Togli_DLE(&Num_Carattere);
  if(Num_Carattere==-2)return -2;
  if ((Err=Errore())!=-1) return Err;
  for (Count=4;Count<8;Count++) Dati[Count-4]=Risposta_Seriale[Count];
  *Valore=Conversione_Dati_Rx(Dati,Dati_Nt);

  return -1;
}   /* Comando_Report */

int ACS::Comando_Report_Parametri(int iopo,char *Istruzione,LONG *Valore)
{
  int Count,Err,Num_Carattere=0;
  unsigned char Dati_Nt[20];
  unsigned char Dati[20];
  LONG Val=0L;

  Scrittura_Seriale(Istruzione,&Val,0);
  Num_Carattere=Lettura_Seriale(Risposta_Seriale);
  //printf("###Risposta Seriale =@@ %s@@\n",Risposta_Seriale);
  Risposta_Seriale[Num_Carattere]='\0';
  Togli_DLE(&Num_Carattere);
  if(Num_Carattere==-2)return -2;
  if ((Err=Errore())!=-1) return Err;
  Dati[0]=0;Dati[1]=0;
  for (Count=5;Count<7;Count++) Dati[Count-3]=Risposta_Seriale[Count];
  *Valore=Conversione_Dati_Rx(Dati,Dati_Nt);
  *Valore=(long) ((*Valore>>(iopo-1))&(1));
  return -1;
}   /* Comando_Report_Parametri */

int ACS::Comando_Array(char *Istruzione,long int Elemento,long int *Valore)
{
  int Num_Carattere=0,Count;
  unsigned char Dati_Nt[20];
  unsigned char Dati[20];
  long int Val[2];

  if (Istruzione[2]=='S')
  {
	 Val[0]=Elemento;Val[1]=*Valore;
	 Scrittura_Seriale(Istruzione,Val,2);
	 Num_Carattere=Lettura_Seriale(Risposta_Seriale);
	 Risposta_Seriale[0]='\0';
  }
  else
  {
	 Scrittura_Seriale(Istruzione,&Elemento,1);
	 Num_Carattere=Lettura_Seriale(Risposta_Seriale);
	 Togli_DLE(&Num_Carattere);
	 for (Count=1;Count<5;Count++) Dati[Count-1]=Risposta_Seriale[Count];
	 *Valore=Conversione_Dati_Rx(Dati,Dati_Nt);
  }
  return 0;
} /*Comando_Array*/

int ACS::Comando_Moto(char *Istruzione)
{
  int Err,Num_Carattere;
  long int Val;

  Scrittura_Seriale(Istruzione,&Val,0);
  Num_Carattere=Lettura_Seriale(Risposta_Seriale);
#ifdef PRINTA
  for(int i=0;i<Num_Carattere;i++)
  printf("\nRISPOSTA SERIALE[%d]=%c",i,Risposta_Seriale[i]);
#endif
  if ((Err=Errore())!=-1) return Err;
  return -1;
}   /* Comando_Moto */

int	 ACS::Comando_Tell(char *Istruzione)
{
  int Err,Num_Carattere=0;
  long int Val;
 
  Scrittura_Seriale(Istruzione,&Val,0);
#ifdef PRINTA
  printf("FATTA SCRITTURA SERIALE");
#endif
  Num_Carattere=Lettura_Seriale(Risposta_Seriale);
#ifdef PRINTA
  printf("FATTA LETTURA SERIALE");
#endif
  if ((Err=Errore())!=-1) return Err;
  else
  {
	 Togli_DLE(&Num_Carattere);
#ifdef PRINTA
	 printf("FATTO TOGLI DLE");
#endif
	 Scansione_Tell(Istruzione[1]);

  }
  return -1;
}  /* Comando_Tell */












