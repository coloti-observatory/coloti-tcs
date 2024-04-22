// tcsDlg.cpp : implementation file
//

#include "stdafx.h"
#include "tcs.h"
#include <math.h>
#include <fstream.h>
#include "tcsDlg.h"
#include "Paddle.h"
#include "InputDlg.h"
#include "CInitSys.h"
#include "ACS.h"
#include "messaggi.h"
#include "pippodlg.h"
#include "ParametriTelescopio.h"
#include "GPS.h"
#include "MyMeteo.h"
#include "ProvaMySQLSet.h"






#ifdef _DEBUG
#define new DEBUG_NEW
#undef THIS_FILE
static char THIS_FILE[] = __FILE__;
#endif

#define USOCONTR

/////////////////////VARIABILI STATICHE DALLA CLASSE///////////////
// variabile per utilizzare porte seriali
CCommClass	CTcsDlg::ComPort;
///////////////////////////////////////////////////////////////////



/////////////////////FUNZIONI E VARIABILI DEI THREAD///////////////
// Update campi coordinate
volatile BOOL bThread=FALSE;
volatile BOOL bSuspUpdate=FALSE;
volatile BOOL g_done=FALSE;
UINT UpdatePos(LPVOID lpPar);
int Joy=0;
ACS		AsseX,AsseY,AsseZ, AsseCupola;
CAstroClass			OggettoPuntato;
CTimeClass			TelTime;
int SetTrackX=0;
int SetTrackY=0;
int SetTrackCup=0;
int SetPointX=0;
int SetPointY=0;
int SetZeroStar=0;
CDTelescopio		Telescopio;
int noCentrato=0;
char ExternalCommand[1000];
bool REMOTE=false;
int MirrorStatus=0;
///////////////////////////////////////////////////////////////////

///////////////////// FUNZIONI AUSILIARIE /////////////////////////
int VerificaVisibilitaAstro();
//Conversione di corrdinate alta-az a equatorial
void AzEl2HaDec ( double az, double el, double phi, double *ha, double *dec );
void HaDec2AzEl( double ha, double dec, double phi, double *az, double *el );
///////////////////////////////////////////////////////////////////


/////////////////////////////////////////////////////////////////////////////
// CTcsDlg dialog

CTcsDlg::CTcsDlg(CWnd* pParent /*=NULL*/)
	: CDialog(CTcsDlg::IDD, pParent)
{
	//{{AFX_DATA_INIT(CTcsDlg)
	UpdateTimeStatus=FALSE;
	RemoteConnection=FALSE;

	//}}AFX_DATA_INIT
	
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
}

void CTcsDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialog::DoDataExchange(pDX);
	//{{AFX_DATA_MAP(CTcsDlg)
	//}}AFX_DATA_MAP
}

BEGIN_MESSAGE_MAP(CTcsDlg, CDialog)
	//{{AFX_MSG_MAP(CTcsDlg)
	ON_WM_DESTROY()
	ON_WM_PAINT()
	ON_WM_QUERYDRAGICON()
	ON_COMMAND(IDM_UTILITA_WWW, OnWeb)
	ON_COMMAND(IDM_UTILITA_MAIL, OnMailto)
	ON_COMMAND(IDM_UTILITA_NOTEPAD, OnNotepad)
	ON_COMMAND(IDM_CENTRAGGIO_MANUALE, OnComandiTastierino)
	ON_COMMAND(IDM_PUNTAMENTO_MINIMO,OnPuntamentoMinimo)
	ON_COMMAND(IDM_PUNTAMENTO_COORDINATE,OnPuntamentoCoordinate)
	ON_COMMAND(IDM_EXIT, OnExit)
	ON_COMMAND(IDM_HELP, OnHelp)
	ON_COMMAND(IDM_OSSERVATORIO, OnOsservatorio)
	ON_COMMAND(IDM_TELE, OnTelescopio)
	ON_COMMAND(IDM_MOTORI, OnMotori)
	ON_COMMAND(IDM_CUPOLA, OnCupola)
	ON_COMMAND(IDM_CONTROLLORE, OnControllore)
	ON_COMMAND(IDM_CONTROLLORE_SB1291, OnControlloreSb1291)
	ON_COMMAND(IDM_CONTROLLORE_SB1000, OnControlloreSb1000)
	ON_MESSAGE(TCS_MSG_UPDATETIME,OnUpdateTime)
	ON_MESSAGE(TCS_MSG_UPDATEPOS,OnUpdatePos)
	ON_MESSAGE(TCS_MSG_FINEPUNTAMIN,OnFinePuntaMin)
	ON_MESSAGE(TCS_MSG_PUNTAMENTO,OnPuntamento)
	ON_MESSAGE(TCS_MSG_ESEGUIPUNTA,OnEseguiPuntamento)
	ON_MESSAGE(TCS_MSG_STARTTRACK,OnStartTrack)
	ON_MESSAGE(TCS_MSG_REMOTE,OnExecuteRemote)
	ON_COMMAND(IDM_INIZIALIZZAZIONE, OnInizializzazione)
	ON_COMMAND(IDM_CAMBIA_CONFIG, OnCambiaConfig)
	ON_COMMAND(IDM_SALVA_CONFIG, OnSalvaConfig)
	ON_COMMAND(IDM_SETTA_POS_HOME, OnSettaPosHome)
	ON_COMMAND(IDM_SETTATIME, OnSettaTempo)
	ON_COMMAND(IDM_SETTAMETEO, OnSettaMeteo)
	ON_COMMAND(IDM_PUNTAMENTO_CATALOGO, OnPuntamentoCatalogo)
	ON_COMMAND(IDM_CUPOLA_APRI, OnCupolaApri)
	ON_COMMAND(IDM_CUPOLA_CHIUDI, OnCupolaChiudi)
	ON_COMMAND(IDM_CUPOLA_OVEST, OnCupolaOvest)
	ON_COMMAND(IDM_CUPOLA_EST, OnCupolaEst)
	ON_COMMAND(IDM_CUPOLA_VAI, OnCupolaVai)
	ON_COMMAND(IDM_CUPOLA_SETTAZERO, OnCupolaSettazero)
	ON_COMMAND(IDM_TELESCOPIO_JOYST, OnTelescopioJoyst)
	ON_COMMAND(IDM_TELESCOPIO_SETSW, OnTelescopioSetHome)
	ON_COMMAND(IDM_TELESCOPIO_SETTAZERO_STAR, OnTelescopioSettazeroStar)
	ON_COMMAND(IDM_CONTROLLORE_Y, OnControlloreY)
	ON_WM_TIMER()
	ON_COMMAND(IDM_CUPOLA_FERMA, OnCupolaFerma)
	ON_COMMAND(IDM_CONTROLLORE_CUPOLA, OnControlloreCupola)
	ON_COMMAND(IDM_CUPOLA_INSEGUIMENTO, OnCupolaInseguimento)
	ON_COMMAND(IDM_TELESCO_FERMA_MOTO, OnTelescoFermaMoto)
	ON_COMMAND(IDM_TELESCO_START_MOTO_ORARIO, OnTelescoStartMotoOrario)
	ON_COMMAND(IDM_TELESCO_STOP_INSEGUIMENTO, OnTelescoStopInseguimento)
	ON_COMMAND(IDM_TELESCO_INIT_ASSI, OnTelescoInitAssi)
	ON_COMMAND(IDM_TELESCO_PARAMETRI, OnTelescoParametri)
	ON_COMMAND(IDM_TELESCO_VERIFICAP, OnTelescoVerificap)
	ON_COMMAND(IDM_PUNTAMENTO_PIANETI, OnPuntamentoPianeti)
	ON_COMMAND(GPS_SETTIME, OnSetGPStime)
	ON_COMMAND(GPS_GETTIME, OnGetGPStime)
	ON_COMMAND(SPECCHIO_APRI, OnApriSpecchio)
	ON_COMMAND(SPECCHIO_CHIUDI, OnChiudiSpecchio)
	ON_COMMAND(METEO_GETVALORI, OnMostraDatiMeteo)
	ON_COMMAND(TCS_External_Obj, OnExternalObj)
	ON_COMMAND(IDM_CONNESSIONE_REMOTA, OnConnessioneRemota)
	//}}AFX_MSG_MAP
END_MESSAGE_MAP()

/////////////////////////////////////////////////////////////////////////////
// CTcsDlg message handlers

BOOL CTcsDlg::OnInitDialog()
{
	CDialog::OnInitDialog();

	SetIcon(m_hIcon, TRUE);			// Set big icon
	SetIcon(m_hIcon, FALSE);		// Set small icon
	
	// TODO: Add extra initialization here

		CMenu* pMenu = GetMenu();
		ASSERT(pMenu != NULL);
	pMenu->EnableMenuItem(IDM_PUNTAMENTO_CATALOGO, MF_DISABLED|MF_GRAYED);
	pMenu->EnableMenuItem(IDM_PUNTAMENTO_MINIMO, MF_DISABLED|MF_GRAYED);
	pMenu->EnableMenuItem(IDM_PUNTAMENTO_COORDINATE, MF_DISABLED|MF_GRAYED);
	pMenu->EnableMenuItem(IDM_CENTRAGGIO_MANUALE, MF_DISABLED|MF_GRAYED);
	pMenu->EnableMenuItem(IDM_TELESCOPIO_JOYST, MF_DISABLED|MF_GRAYED);
	pMenu->EnableMenuItem(IDM_PUNTAMENTO_PIANETI,MF_DISABLED|MF_GRAYED);

	m_Font.CreateFont(14,0 , 0, 0, 600, FALSE, FALSE,0,0,0,0,0,0, "Arial");
	InitStaticControl(&m_sDate[0],IDC_UTDATE);
	InitStaticControl(&m_sDate[1],IDC_UTTIME);
	InitStaticControl(&m_sDate[2],IDC_ORAL);
	InitStaticControl(&m_sDate[3],IDC_LST);
	InitStaticControl(&m_sDate[4],IDC_JD);

	InitStaticControl(&m_sMCoord[0],IDC_MAR);
	InitStaticControl(&m_sMCoord[1],IDC_MDEC);
	InitStaticControl(&m_sMCoord[2],IDC_MHA);
	InitStaticControl(&m_sMCoord[3],IDC_MAZ);
	InitStaticControl(&m_sMCoord[4],IDC_MAL);
	InitStaticControl(&m_sMCoord[5],IDC_MPA);

	InitStaticControl(&m_sOCoord[0],IDC_OAR);
	InitStaticControl(&m_sOCoord[1],IDC_ODEC);
	InitStaticControl(&m_sOCoord[2],IDC_OHA);
	InitStaticControl(&m_sOCoord[3],IDC_OAZ);
	InitStaticControl(&m_sOCoord[4],IDC_OAL);
	InitStaticControl(&m_sOCoord[5],IDC_OPA);

	InitStaticControl(&m_sAVel[0],IDC_MVHA);
	InitStaticControl(&m_sAVel[1],IDC_MVDEC);
	InitStaticControl(&m_sAVel[2],IDC_MVAZ);
	InitStaticControl(&m_sAVel[3],IDC_MVAL);
	InitStaticControl(&m_sAVel[4],IDC_MVPA);

	InitStaticControl(&m_sCupola[0],IDC_CFEN);
	InitStaticControl(&m_sCupola[1],IDC_CAZ);
	InitStaticControl(&m_sCupola[2],IDC_CROT);
	InitStaticControl(&m_sCupola[3],IDC_CHS);


	InitStaticControl(&m_sMeteo[0],IDC_METT);
	InitStaticControl(&m_sMeteo[1],IDC_METPRE);
	InitStaticControl(&m_sMeteo[2],IDC_METH);
	InitStaticControl(&m_sMeteo[3],IDC_METWS);
	InitStaticControl(&m_sMeteo[4],IDC_METWD);

	InitStaticControl(&m_sMessaggi,IDC_MESSAGGI);
	InitStaticControl(&m_sGeninfo[0],IDC_GEM_INFO_SRA);
	InitStaticControl(&m_sGeninfo[1],IDC_GEM_INFO_SDEC);
	InitStaticControl(&m_sGeninfo[2],IDC_GEM_INFO_SRISE);
	InitStaticControl(&m_sGeninfo[3],IDC_GEM_INFO_SSET);
	InitStaticControl(&m_sGeninfo[4],IDC_GEM_INFO_LRA);
	InitStaticControl(&m_sGeninfo[5],IDC_GEM_INFO_LDEC);
	InitStaticControl(&m_sGeninfo[6],IDC_GEM_INFO_LRISE);
	InitStaticControl(&m_sGeninfo[7],IDC_GEM_INFO_LSET);
	InitStaticControl(&m_sGeninfo[8],IDC_GEM_INFO_LFASE);
	InitStaticControl(&m_sGeninfo[9],IDC_GEM_INFO_IN);
	InitStaticControl(&m_sGeninfo[10],IDC_GEM_INFO_FN);

	InitStaticControl(&m_sMotAbil[0],IDC_M_AAR);
	InitStaticControl(&m_sMotAbil[1],IDC_M_ADEC);
	InitStaticControl(&m_sMotAbil[2],IDC_M_APA);
	InitStaticControl(&m_sMotMoti[0],IDC_M_MAR);
	InitStaticControl(&m_sMotMoti[1],IDC_M_MDEC);
	InitStaticControl(&m_sMotMoti[2],IDC_M_MPA);
	InitStaticControl(&m_sMotHs[0],IDC_M_HSAR);
	InitStaticControl(&m_sMotHs[1],IDC_M_HSDEC);
	InitStaticControl(&m_sMotHs[2],IDC_M_HSPA);
	InitStaticControl(&m_sMotVel[0],IDC_M_VAR);
	InitStaticControl(&m_sMotVel[1],IDC_M_VDEC);
	InitStaticControl(&m_sMotVel[2],IDC_M_VPA);
	InitStaticControl(&m_sMotAcc[0],IDC_M_ACCAR);
	InitStaticControl(&m_sMotAcc[1],IDC_M_ACCDEC);
	InitStaticControl(&m_sMotAcc[2],IDC_M_ACCPA);
	InitStaticControl(&m_sMotE1[0],IDC_M_E1AR);
	InitStaticControl(&m_sMotE1[1],IDC_M_E1DEC);
	InitStaticControl(&m_sMotE1[2],IDC_M_E1PA);
	InitStaticControl(&m_sMotE2[0],IDC_M_E2AR);
	InitStaticControl(&m_sMotE2[1],IDC_M_E2DEC);
	InitStaticControl(&m_sMotE2[2],IDC_M_E2PA);



	SetDlgItemText(IDC_MESSAGGI,mesg[0]);
	sprintf(m_LogFileName,"Log\\%s.log",TelTime.GetUTDateLong());
	sprintf(m_ErrFileName,"Log\\%s.err",TelTime.GetUTDateLong());
	UpdateTcsLog("TCS Started ");
	//SetDlgItemText(IDC_UTTIME,m_LogFileName);
	ReadCostPun();
	
	TcsServer.hwnd=GetSafeHwnd();
	
	
	return TRUE;  // return TRUE  unless you set the focus to a control
}

void CTcsDlg::OnDestroy()
{
	WinHelp(0L, HELP_QUIT);
	CDialog::OnDestroy();
}

// If you add a minimize button to your dialog, you will need the code below
//  to draw the icon.  For MFC applications using the document/view model,
//  this is automatically done for you by the framework.

void CTcsDlg::OnPaint() 
{
	if (IsIconic())
	{
		CPaintDC dc(this); // device context for painting

		SendMessage(WM_ICONERASEBKGND, (WPARAM) dc.GetSafeHdc(), 0);

		// Center icon in client rectangle
		int cxIcon = GetSystemMetrics(SM_CXICON);
		int cyIcon = GetSystemMetrics(SM_CYICON);
		CRect rect;
		GetClientRect(&rect);
		int x = (rect.Width() - cxIcon + 1) / 2;
		int y = (rect.Height() - cyIcon + 1) / 2;

		// Draw the icon
		dc.DrawIcon(x, y, m_hIcon);
	}
	else
	{
		CDialog::OnPaint();
	}
}

HCURSOR CTcsDlg::OnQueryDragIcon()
{
	return (HCURSOR) m_hIcon;
}

void CTcsDlg::OnOK() 
{
	// TODO: Add extra validation here
	

}

void CTcsDlg::InitStaticControl(CColorStaticST *m_stc, UINT nID)
{
	m_stc->SubclassDlgItem(nID, this);
	m_stc->SetFont(&m_Font);
	m_stc->SetBkColor(STATIC_BKCOLOR);
	m_stc->SetTextColor(STATIC_TXTCOLOR);
}

void CTcsDlg::OnWeb() 
{
	// TODO: Add your control notification handler code here
	HINSTANCE value;
	CInputDlg Dlg;
	Dlg.m_InputLabel=_T("URL: http//");
	int nResponse = Dlg.DoModal();
	if (nResponse == IDOK)
	{
	Dlg.m_InputValue=_T("url:http://")+Dlg.m_InputValue;
	value=::ShellExecute(NULL,NULL,Dlg.m_InputValue,NULL,_T("C:\\windows"),SW_SHOWNORMAL);
	TRACE("\nerror code= %d\n",value);
	}
}

void CTcsDlg::OnMailto() 
{
	// TODO: Add your control notification handler code here
	HINSTANCE value;
	CInputDlg Dlg;
	Dlg.m_InputLabel=_T("Destinatario");
	int nResponse = Dlg.DoModal();
	if (nResponse == IDOK)
	{
	if(Dlg.m_InputValue!=_T("")){
	Dlg.m_InputValue=_T("mailto:")+Dlg.m_InputValue;
	value=::ShellExecute(NULL,NULL,Dlg.m_InputValue,NULL,_T("C:\\windows"),SW_SHOWNORMAL);
	TRACE("\nerror code= %d\n",value);}

	}
}

void CTcsDlg::OnNotepad() //lancia Notepad
{
	// TODO: Add your control notification handler code here
	HINSTANCE value;
	CInputDlg Dlg;
	Dlg.m_InputLabel=_T("Nome Programma");
	int nResponse = Dlg.DoModal();
	if (nResponse == IDOK)
	{
		if(Dlg.m_InputValue!=_T("")){
		value=::ShellExecute(NULL,"open",Dlg.m_InputValue,NULL,_T("C:\\windows"),SW_SHOWNORMAL);
		TRACE("\nerror code= %d\n",value);}
	}
	else if (nResponse == IDCANCEL)
	{

	};

}

void CTcsDlg::InitStar()
{
	strcpy(StellaCorrente.Name,"<NULL>");	
	StellaCorrente.Id=0;	
	StellaCorrente.Epoch=TelTime.GetUTDecYear();
	StellaCorrente.Equinox=StellaCorrente.Epoch;
	StellaCorrente.Ra=TelTime.GetLSATHour();
	StellaCorrente.Dec=0.0;

}
//MARK: Puntamento
void CTcsDlg::OnEseguiPuntamento(LPARAM lp,WPARAM wp)
{
	
	/*if( bSuspUpdate==TRUE){
		 bSuspUpdate=FALSE;
		 //Sleep(150);
	}*/
	
	//OggettoPuntato.CalcStarPos();
	SetTrackCup=SetTrackX=SetTrackY=0;
	noCentrato=0;
	if(AsseX.CommStatus==1)
	{
	KillTimer(2);
	
	AsseX.StopMove(X);
	if(AsseX.IsMoving(X))
		Sleep(200);
	AsseY.StopMove(X);
	if(AsseY.IsMoving(X))
		Sleep(200);
	
	AsseX.SetSlewMode(X);
	AsseY.SetSlewMode(X);
	
	TraiettoriaX();
	TraiettoriaY();
	//CUPOLA
	AsseX.SetMotAcc(X,m_telescopeInfo.MaxAccX);
	AsseX.SetMotDec(X,m_telescopeInfo.MaxAccX);
	AsseY.SetMotAcc(X,m_telescopeInfo.MaxAccY);
	AsseY.SetMotDec(X,m_telescopeInfo.MaxAccY);
	AsseX.Move(X,m_telescopeInfo.TargetPosX,m_telescopeInfo.SlewVelX);
	AsseY.Move(X,m_telescopeInfo.TargetPosY,m_telescopeInfo.SlewVelY);
	UpdateTcsLog("MOUNT:slewing");
	Sleep(300);
	PuntaCupola();
	SetTimer(2,1000,NULL);

	CDStopPunta pos;
	pos.m_iTipoMoto=2;
	pos.m_InputString=_T("Puntamento: Attendere");
	int risp=pos.DoModal();
	if (risp == IDOK){
		//SetTrack=1;
		/*OggettoPuntato.CalcStarPos();
		AsseX.SetTrackMode(X);
		AsseY.SetTrackMode(X);
		if(Telescopio.m_TelMonTipo==0){
			AsseX.SetMotVel(X,m_telescopeInfo.DirX*OggettoPuntato.ObsVAZ);
			AsseX.StartMove(X);
			AsseY.SetMotVel(X,OggettoPuntato.ObsVEL);
			AsseY.StartMove(X);
		}
		else{
			AsseX.SetMotVel(X,m_telescopeInfo.DirX*15.041);
			AsseX.StartMove(X);
			AsseY.SetMotVel(X,0.);
			AsseY.StartMove(X);
		}	*/		
		}
	else if (risp == IDCANCEL){
		AsseX.StopMove(X);
		AsseY.StopMove(X);	
		SetTrackCup=SetTrackX=SetTrackY=0;
		SetPointY=0;SetPointX=0;
		UpdateTcsLog("MOUNT:slewing stopped by user");
	}
	}	
}


void CTcsDlg::OnPuntamento(LPARAM lp,WPARAM wp)
{
	//Mettere gestione delle velocit�
	CAstroClass OggTemp;
	char *buff = new char[80];
	BeginWaitCursor();
	OggTemp.SetLocationInfo(m_Osservatorio.Latitudine,
								   m_Osservatorio.Longitudine,
								   m_Osservatorio.Altitudine,
								   m_Osservatorio.Timezone);
	/*bSuspUpdate=TRUE;
	Sleep(200);*/
	//OnMostraDatiMeteo() ;
	OggettoPuntato.SetRefraPar(me.m_dP,me.m_dT,me.m_dH);
	/*
	CString	m_Nome;
	int		m_rah;
	int		m_ram;
	int		m_ras;
	double	m_raVel;
	double	m_epoca;
	int		m_decd;
	int		m_decm;
	double	m_decs;
	double	m_decVell;
	*/
	//StellaCorrente.ObjType 
	// Metter1 uno switch case: 0 Puntamento minimo, 1 puntamento completo, 2 puntamento catalogo
	StellaCorrente.Mag=0.0;
	StellaCorrente.promora=0.0;	
    StellaCorrente.promodec=0.0;
    StellaCorrente.parallax=0.0;
    StellaCorrente.radialvelocity=0.0;
	// PUNTAMENTO MINIMO////
	
	if(((int)wp)==0){
	
	OggTemp.InputStarJ2000(PuntamentoMinimo->m_RA,PuntamentoMinimo->m_DEC,PuntamentoMinimo->m_epoca);
	
	OggTemp.CalcStarPos();
	if (OggTemp.ObsEL<Motori.m_Asse2Pl) {
		AfxMessageBox("L'Oggetto non � ancora Sorto");	
		::PostMessage(PuntamentoMinimo->m_hWnd,DPM_MSG_OGGBAS,0,0);
	//OggettoPuntato.InputStarJ2000(StellaCorrente.Ra,StellaCorrente.Dec,StellaCorrente.Epoch);
	}
	else{
		//OggettoPuntato.SetRefraPar(me.m_dP,me.m_dT,me.m_dH);
		strcpy(StellaCorrente.Name,PuntamentoMinimo->m_Nome);
		
		StellaCorrente.Id=StellaCorrente.Id+1;	
		StellaCorrente.Epoch=PuntamentoMinimo->m_epoca;
		StellaCorrente.Equinox=PuntamentoMinimo->m_epoca;
		StellaCorrente.Ra=PuntamentoMinimo->m_RA;
		StellaCorrente.Dec=PuntamentoMinimo->m_DEC;
		sprintf(buff,"NEXT TARGET: %s RA=%.5lf DEC=%.5lf",StellaCorrente.Name,StellaCorrente.Ra,StellaCorrente.Dec);
		UpdateTcsLog(buff);
		OggettoPuntato.InputStarJ2000(StellaCorrente.Ra,StellaCorrente.Dec,StellaCorrente.Epoch);
		::PostMessage(PuntamentoMinimo->m_hWnd,DPM_MSG_OK,0,0);	
		}
	}
	// PUNTAMENTO COMPLETO ////
	if(((int)wp)==1){
		//AfxMessageBox("Pippo");
		double equi;
		if(NuovoOggetto->m_Eq1950==TRUE){
			equi=1950;
			OggTemp.InputStarB1950(OggTemp.hms2h(NuovoOggetto->m_Ra_H,NuovoOggetto->m_Ra_M,NuovoOggetto->m_Ra_S),
								   OggTemp.gms2g(NuovoOggetto->m_Dec_G,NuovoOggetto->m_Dec_M,NuovoOggetto->m_Dec_G),
								   NuovoOggetto->m_PMra,
								   NuovoOggetto->m_PMdec,
								   NuovoOggetto->m_Paral,
								   NuovoOggetto->m_Rvel);
		}
		else{
			equi=2000.;
			OggTemp.InputStarJ2000( OggTemp.hms2h(NuovoOggetto->m_Ra_H,NuovoOggetto->m_Ra_M,NuovoOggetto->m_Ra_S),
									OggTemp.gms2g(NuovoOggetto->m_Dec_G,NuovoOggetto->m_Dec_M,NuovoOggetto->m_Dec_G),
									NuovoOggetto->m_Epoch,
				                    NuovoOggetto->m_PMra,
				                    NuovoOggetto->m_PMdec,
				                    NuovoOggetto->m_Paral,
				                    NuovoOggetto->m_Rvel);
		}		
		OggTemp.CalcStarPos();
		if (OggTemp.ObsEL<Motori.m_Asse2Pl) {
		AfxMessageBox("L'Oggetto non � ancora Sorto");	
		::PostMessage(NuovoOggetto->m_hWnd,DPM_MSG_OGGBAS,0,0);
		//TODo - inserire un Input cooord;
		}
		else
		{
			strcpy(StellaCorrente.Name,NuovoOggetto->m_ObjName);	
			StellaCorrente.Id=StellaCorrente.Id+1;
			StellaCorrente.Ra=OggTemp.hms2h(NuovoOggetto->m_Ra_H,NuovoOggetto->m_Ra_M,NuovoOggetto->m_Ra_S);
			StellaCorrente.Dec=OggTemp.gms2g(NuovoOggetto->m_Dec_G,NuovoOggetto->m_Dec_M,NuovoOggetto->m_Dec_G);
			StellaCorrente.Epoch=NuovoOggetto->m_Epoch;
			StellaCorrente.Equinox=equi;
			StellaCorrente.Mag=NuovoOggetto->m_Mag;
			StellaCorrente.promora=	NuovoOggetto->m_PMra;
			StellaCorrente.promodec=NuovoOggetto->m_PMdec;
			StellaCorrente.parallax=NuovoOggetto->m_Paral;
		    StellaCorrente.radialvelocity=NuovoOggetto->m_Rvel;
			sprintf(buff,"NEXT TARGET: %s RA=%.5lf DEC=%.5lf",StellaCorrente.Name,StellaCorrente.Ra,StellaCorrente.Dec);
			UpdateTcsLog(buff);
			OggettoPuntato.InputStarJ2000(StellaCorrente.Ra,StellaCorrente.Dec,StellaCorrente.Epoch);
			::PostMessage(NuovoOggetto->m_hWnd,DPM_MSG_OK,0,0);
	
		}
	}

	/**/
	//MessageBox("Ho Fatto",StellaCorrente.Name);
	
	// PUNTAMENTO DA CATALOGO //
	if(((int)wp)==2){
		/*
		CString	m_NOME;
		long	m_RAH;
		long	m_RAM;
		double	m_RAS;
		long	m_DECD;
		long	m_DECM;
		double	m_DECS;
		double	m_MAG;
		double	m_EPOCH;
		*/
		//char piso[30];
		//sprintf(piso,"%lf",pis.m_DEC);
		//AfxMessageBox(piso);
		OggTemp.InputStarJ2000(pis.m_RA,pis.m_DEC,pis.m_Data.m_EPOCH);
		OggTemp.CalcStarPos();
	if (OggTemp.ObsEL<Motori.m_Asse2Pl) {
		AfxMessageBox("L'Oggetto non � ancora Sorto");	
		//::PostMessage(PuntamentoMinimo->m_hWnd,DPM_MSG_OGGBAS,0,0);
		//OggettoPuntato.InputStarJ2000(StellaCorrente.Ra,StellaCorrente.Dec,StellaCorrente.Epoch);
	}
	else{
		//OggettoPuntato.SetRefraPar(me.m_dP,me.m_dT,me.m_dH);
		strcpy(StellaCorrente.Name,pis.m_Data.m_NOME);	
		StellaCorrente.Id=StellaCorrente.Id+1;	
		StellaCorrente.Epoch=pis.m_Data.m_EPOCH;
		StellaCorrente.Equinox=pis.m_Data.m_EPOCH;
		StellaCorrente.Ra=pis.m_RA;
		StellaCorrente.Dec=pis.m_DEC;
		sprintf(buff,"NEXT TARGET: %s RA=%.5lf DEC=%.5lf",StellaCorrente.Name,StellaCorrente.Ra,StellaCorrente.Dec);
		UpdateTcsLog(buff);
		OggettoPuntato.InputStarJ2000(StellaCorrente.Ra,StellaCorrente.Dec,StellaCorrente.Epoch);
		//::PostMessage(PuntamentoMinimo->m_hWnd,DPM_MSG_OK,0,0);	
		}
		
		//AfxMessageBox("Puntamento Catalogo");	
		;
	}

	//PUNTAMENTO PIANETI
	if(((int)wp)==3){
		/*
		CString	m_NOME;
		long	m_RAH;
		long	m_RAM;
		double	m_RAS;
		long	m_DECD;
		long	m_DECM;
		double	m_DECS;
		double	m_MAG;
		double	m_EPOCH;
		*/
		OggTemp.InputPlanet( PantaSoledlg.m_RA,PantaSoledlg.m_DEC);
		OggTemp.CalcStarPos();
	if (OggTemp.ObsEL<10) {
		AfxMessageBox("L'Oggetto non � ancora Sorto");	
		//::PostMessage(PuntamentoMinimo->m_hWnd,DPM_MSG_OGGBAS,0,0);
		//OggettoPuntato.InputStarJ2000(StellaCorrente.Ra,StellaCorrente.Dec,StellaCorrente.Epoch);
	}
	else{
		//char piso[50];
		//sprintf(piso,"%lf",PantaSoledlg.m_RA);
		//AfxMessageBox(piso);
		sprintf(buff,"NEXT TARGET: solar system object RA=%.5lf DEC=%.5lf",PantaSoledlg.m_RA,PantaSoledlg.m_DEC);
		UpdateTcsLog(buff);
		OggettoPuntato.InputPlanet( PantaSoledlg.m_RA,PantaSoledlg.m_DEC);
		}
	}
	
	EndWaitCursor();
	delete[] buff;
	
	
}


void CTcsDlg::OnComandiTastierino() 
{
	// TODO: Add your control notification handler code here
	CPaddle dlg;
	SetTrackX=SetTrackY=0;
	Joy=1;
	dlg.m_VelBassa=PadVelBassa;
	dlg.m_VelMedia=PadVelMedia;
	dlg.m_VelAlta=PadVelAlta;
	dlg.m_RATrackVel=m_telescopeInfo.DirX*OggettoPuntato.ObsVAZ;
	if(fabs(OggettoPuntato.ObsVEL)<=1.)
		dlg.m_DECTrackVel=0.0;
	else 
		dlg.m_DECTrackVel=OggettoPuntato.ObsVEL;
	int nResponse = dlg.DoModal();
	if (nResponse == IDOK)
	{
	}
	else if (nResponse == IDCANCEL)
	{
	}
	;
	
	Joy=0;
	noCentrato=1;
	SetTrackX=SetTrackY=1;

	GetTelInfo();
	double H=OggettoPuntato.ObsH*DR2H;
	if(H>24)H-=24.;
	if(H<0.)H+=24.;
	double DPH=(m_telescopeInfo.H-H)*54000.;
	double DPDEC=(m_telescopeInfo.DEC-OggettoPuntato.ObsDEC*DR2D)*3600;
	double DPX=-1.0*(m_telescopeInfo.AZ-OggettoPuntato.ObsAZ)*3600;
	double DPY=(m_telescopeInfo.EL-OggettoPuntato.ObsEL)*3600;
	ofstream errout(m_ErrFileName,ios::app);
	if(!errout.bad()){
		errout<<TelTime.GetUTStr()<<"-->\t";
		errout<<OggettoPuntato.ObsRA<<" ";
		errout<<OggettoPuntato.ObsDEC*DR2D<<" ";
		errout<<H<<" ";
		errout<<DPH<<" ";
		errout<<DPDEC<<" ";
		errout<<DPX<<" ";
		errout<<DPY<<endl;
		errout.close();
	}
}

void CTcsDlg::OnExit() 
{
	// TODO: Add your command handler code here
	/*if(UpdateTimeStatus==TRUE)	{
		m_pUpTimeThread->Stop();
		delete m_pUpTimeThread;
	bThread=TRUE;
	Sleep(200);
	}*/
	//if(ComPort.Status==1)ComPort.Close();
	ofstream lastpos("lastpos.dat");
	long Valox,Valoy,Valoc;
	int err;
	KillTimer(1);
	KillTimer(2);

	
	if(AsseX.CommStatus==1)
	{
		if(AsseX.IsMoving(X)){
			AsseX.StopMove(X);
			UpdateTcsLog("MOUNT:Azimut motor stopped");
		}
		AsseX.SetMotorOff(X);
		err = AsseX.GetMotEncPos(X,&Valox);
		//Valox=Valox+AsseX.CONVFACTOR[X]*CostX[0];
		AsseX.CloseComm();
	}
	if(AsseY.CommStatus==1)
	{
		if(AsseY.IsMoving(X)){
			AsseY.StopMove(X);
			UpdateTcsLog("MOUNT:Elevation motor stopped");
		}
		AsseY.SetMotorOff(X);
		err = AsseY.GetMotEncPos(X,&Valoy);
		//Valoy=Valoy+AsseY.CONVFACTOR[X]*CostY[0];
		AsseY.CloseComm();
	}
	/*if(AsseZ.CommStatus==1)
	{
		AsseZ.CloseComm();
	}*/
	
	if(AsseCupola.CommStatus==1)
	{
		if(m_CupolaInfo.StatusApertura==1){
			OnCupolaChiudi();
			Sleep(8000);
		}
		if(MirrorStatus==1){
			OnChiudiSpecchio();
			Sleep(10000);
		}
		err = AsseCupola.GetMotEncPos(X,&Valoc);
		
		AsseCupola.CloseComm();
	}
	lastpos<<Valox<<" "<<Valoy<<" "<<Valoc;
	lastpos.flush();
	lastpos.close();
	//AfxMessageBox("END TCS SESSION");
	if(REMOTE)
		StopConnessioneRemota();
	UpdateTcsLog("TCS Ended ");
	Sleep(1000);
	CDialog::OnCancel();
}

void CTcsDlg::OnHelp() 
{
	// TODO: Add your control notification handler code here
	WinHelp(0L,HELP_CONTENTS); 

	
}

void CTcsDlg::OnOsservatorio() 
{
	// TODO: Add your command handler code here
	
	Oss.m_Altitudine = m_Osservatorio.Altitudine;
	Oss.m_Fuso = m_Osservatorio.Timezone;
	CAngle lat(m_Osservatorio.Latitudine);
	Oss.m_LatG = (int)lat.hh;
	Oss.m_LatM = (int)lat.mm;
	Oss.m_LatS = lat.ss;
	CAngle lon(m_Osservatorio.Longitudine);
	Oss.m_LonG =(int)lon.hh ;
	Oss.m_LonM = (int)lon.mm;
	Oss.m_LonS = lon.ss;
	Oss.m_Nome = m_Osservatorio.Nome;
	Oss.m_Gps = m_Osservatorio.Gps;
	Oss.m_Meteo =m_Osservatorio.Meteo;
	

	

	int nResponse = Oss.DoModal();
	if (nResponse == IDOK)
	{
	}
	else if (nResponse == IDCANCEL)
	{
	}
	;
}

void CTcsDlg::OnTelescopio() 
{
	// TODO: Add your command handler code here
		
	int nResponse = Telescopio.DoModal();
	if (nResponse == IDOK)
	{
	}
	else if (nResponse == IDCANCEL)
	{
	}
	;
}

void CTcsDlg::OnMotori() 
{
	// TODO: Add your command handler code here
		
	int nResponse = Motori.DoModal();
	if (nResponse == IDOK)
	{
	}
	else if (nResponse == IDCANCEL)
	{
	}
	;
}
void CTcsDlg::OnCupola() 
{
	// TODO: Add your command handler code here
		
	int nResponse = Cupola.DoModal();
	if (nResponse == IDOK)
	{
	}
	else if (nResponse == IDCANCEL)
	{
	}
	;
}

void CTcsDlg::OnControlloreCupola() 
{
	// TODO: Add your command handler code here
	KillTimer(2);
	if(AsseCupola.CommStatus==1)
	 AsseCupola.Consolle();
	SetTimer(2,1000,NULL);
}


void CTcsDlg::OnControllore() 
{
	// TODO: Add your command handler code here
	KillTimer(2);
	if(AsseX.CommStatus==1)
	 AsseX.Consolle();
	SetTimer(2,1000,NULL);
}

void CTcsDlg::OnControlloreSb1291() 
{
	// TODO: Add your command handler code here
	
	int nResponse = AcsSB1291.DoModal();
	if (nResponse == IDOK)
	{
	}
	else if (nResponse == IDCANCEL)
	{
	}
	;
}

void CTcsDlg::OnControlloreSb1000() 
{
	// TODO: Add your command handler code here
	int nResponse = AcsSB1000.DoModal();
	if (nResponse == IDOK)
	{
	}
	else if (nResponse == IDCANCEL)
	{
	}
	;
}
void CTcsDlg::OnPuntamentoCoordinate()
{
	// TODO: Add your command handler code here
	//SetTrackY=SetTrackX=0;
	CMenu* pMenu = GetMenu();
		ASSERT(pMenu != NULL);

	pMenu->EnableMenuItem(IDM_PUNTAMENTO_CATALOGO, MF_DISABLED|MF_GRAYED);
	pMenu->EnableMenuItem(IDM_PUNTAMENTO_MINIMO, MF_DISABLED|MF_GRAYED);
	pMenu->EnableMenuItem(IDM_PUNTAMENTO_COORDINATE, MF_DISABLED|MF_GRAYED);
	
	NuovoOggetto = new InputStar(this);
		if (NuovoOggetto->Create() == TRUE)
			NuovoOggetto->ShowWindow(SW_SHOW);
	
}
void CTcsDlg::OnPuntamentoMinimo() 
{
	// TODO: Add your command handler code here
	//if (PuntamentoMinimo == NULL)
	//SetTrackY=SetTrackX=0;
	CMenu* pMenu = GetMenu();
		ASSERT(pMenu != NULL);

	pMenu->EnableMenuItem(IDM_PUNTAMENTO_CATALOGO, MF_DISABLED|MF_GRAYED);
	pMenu->EnableMenuItem(IDM_PUNTAMENTO_MINIMO, MF_DISABLED|MF_GRAYED);
	pMenu->EnableMenuItem(IDM_PUNTAMENTO_COORDINATE, MF_DISABLED|MF_GRAYED);

	//{
		PuntamentoMinimo = new CDPuntamentoMinimo(this);
		if (PuntamentoMinimo->Create() == TRUE)
			PuntamentoMinimo->ShowWindow(SW_SHOW);
	//}
	//else
		//PuntamentoMinimo->SetActiveWindow();
}

void CTcsDlg::OnUpdateTime(LPARAM lp,WPARAM wp) 
{
	SetDlgItemText(IDC_UTDATE,TelTime.GetUTDateStr());
	SetDlgItemText(IDC_UTTIME,TelTime.GetUTStr());
	SetDlgItemText(IDC_ORAL,TelTime.CivilTimeStr());
	SetDlgItemText(IDC_LST,TelTime.GetLSATStr());
	SetDlgItemText(IDC_JD,TelTime.GetJDStr());
}

void CTcsDlg::OnUpdatePos(LPARAM lp,WPARAM wp)
{	
	char buf[80];
	LONG Valo;
	double val, PosX=180,PosY=46;
	double ra,ha,dec;
	int err;
	OggettoPuntato.CalcStarPos();
	SetDlgItemText(IDC_OAR, OggettoPuntato.GetRAStr(OggettoPuntato.ObsRA));
	SetDlgItemText(IDC_ODEC, OggettoPuntato.GetDECStr(OggettoPuntato.GetObsDEC()));
	SetDlgItemText(IDC_OHA,OggettoPuntato.GetRAStr(OggettoPuntato.GetObsHA()));
	SetDlgItemText(IDC_OAZ, OggettoPuntato.GetDECStr(OggettoPuntato.ObsAZ));
	SetDlgItemText(IDC_OAL,OggettoPuntato.GetRAStr(OggettoPuntato.ObsEL));
	SetDlgItemText(IDC_OPA, OggettoPuntato.GetDECStr(OggettoPuntato.ObsPA));
	sprintf(buf,"%10.3lf",OggettoPuntato.ObsVEL);
	SetDlgItemText(IDC_MVAL, buf);
	sprintf(buf,"%10.3lf",OggettoPuntato.ObsVAZ);
	SetDlgItemText(IDC_MVAZ, buf);
	sprintf(buf,"%10.3lf",OggettoPuntato.ObsVPA);
	SetDlgItemText(IDC_MVPA, buf);
	sprintf(buf,"%10.3lf",OggettoPuntato.ObsVH);
	SetDlgItemText(IDC_MVHA, buf);

	if(AsseX.CommStatus){

		err = AsseX.GetMotEncPos(X,&Valo);
		sprintf(buf,"%ld",Valo);
		SetDlgItemText(IDC_M_E1AR,buf);
		//PosX=(180-PosX/3600.);
		err = AsseX.GetMotPos(X,&PosX);
		PosX=(180-PosX/3600.);
		FormatCoord(buf, PosX,0);

		//sprintf(buf,"%6.1lf",val);
		SetDlgItemText(IDC_MAZ,buf);

		err = AsseX.GetMotVel(X,&val);
		sprintf(buf,"%6.1lf(\"/s)",val);
		SetDlgItemText(IDC_M_VAR,buf);
		if(AsseX.GetMotorStatus(X))
				SetDlgItemText(IDC_M_MAR,"On");
		else
				SetDlgItemText(IDC_M_MAR,"Off");
		if(AsseX.IsMoving(X))
			SetDlgItemText(IDC_M_HSAR,"On");
		else
			SetDlgItemText(IDC_M_HSAR,"Off");
	}

	if(AsseY.CommStatus){

		err = AsseY.GetMotEncPos(X,&Valo);
		sprintf(buf,"%ld",Valo);
		SetDlgItemText(IDC_M_E1DEC,buf);

		err = AsseY.GetMotPos(X,&PosY);
		PosY=(PosY/3600.);
		FormatCoord(buf, (PosY/3600.),0);
		//sprintf(buf,"%6.1lf",val);
		SetDlgItemText(IDC_MAL,buf);

		err = AsseY.GetMotVel(X,&val);
		sprintf(buf,"%6.1lf(\"/s)",val);
		SetDlgItemText(IDC_M_VDEC,buf);
		if(AsseY.GetMotorStatus(X))
			 SetDlgItemText(IDC_M_MDEC,"On");
			else
				SetDlgItemText(IDC_M_MDEC,"Off");
		if(AsseY.IsMoving(X))
			SetDlgItemText(IDC_M_HSDEC,"On");
		else
			SetDlgItemText(IDC_M_HSDEC,"Off");
	}

	AzEl2HaDec ( PosX, PosY, m_Osservatorio.Latitudine, &ha, &dec );
	ra=TelTime.GetLSATHour()-ha;
	if(ra<0.)ra+=24.;
	if(ra>24)ra-=24.;
	FormatCoord(buf, ra,0);
	SetDlgItemText(IDC_MAR,buf);
	FormatCoord(buf, ha,0);
	SetDlgItemText(IDC_MDEC,buf);
	FormatCoord(buf, dec,0);
	SetDlgItemText(IDC_MDEC,buf);

	/*
	if(AsseZ.CommStatus){

		err = AsseZ.GetMotEncPos(X,&Valo);
		sprintf(buf,"%ld",Valo);
		SetDlgItemText(IDC_M_E1DEC,buf);

		err = AsseZ.GetMotPos(X,&val);
		FormatCoord(buf, (val/3600.),0);
		//sprintf(buf,"%6.1lf",val);
		SetDlgItemText(IDC_MAL,buf);

		err = AsseZ.GetMotVel(X,&val);
		sprintf(buf,"%6.1lf(\"/s)",val);
		SetDlgItemText(IDC_M_VDEC,buf);
	}
	*/
}

void CTcsDlg::OnInizializzazione() 
{
		// TODO: Add your command handler code here

		CInitSys	Init;
		TCHAR buf[80];
		CMenu* pMenu = GetMenu();
			ASSERT(pMenu != NULL);
		int statoc=0;
	// Lettura Configurazione 
		BeginWaitCursor();
			ReadConfig();
			SetDlgItemText(IDC_MESSAGGI,mesg[2]);
		EndWaitCursor();
	// Inizializzaione localita'
		OggettoPuntato.SetLocationInfo(m_Osservatorio.Latitudine,
									m_Osservatorio.Longitudine,
									m_Osservatorio.Altitudine,
									m_Osservatorio.Timezone);
	// Aggiornamento tempo	
	/*	BeginWaitCursor();
		OnSetGPStime();
		SetDlgItemText(IDC_MESSAGGI,mesg[10]);
		EndWaitCursor();*/

		SetTimer(1,1000,NULL);
	// apertura finestra inizializzazione	
		int risp=Init.DoModal();
		if(risp==IDOK)
		{//-1
	//Immissione nuova Ora

			/*if(Init.m_iSetTime==1)
				ti.DoModal();*/ //commentato 2 10 2001

	//Update Time
		
			/*if(Init.m_iUpTime==1)	{//-2
				/*m_pUpTimeThread =(CUpdateThread*)AfxBeginThread(RUNTIME_CLASS(CUpdateThread),THREAD_PRIORITY_NORMAL);
				m_pUpTimeThread->Start(GetSafeHwnd(),TCS_MSG_UPDATETIME);
				UpdateTimeStatus=TRUE;
				SetTimer(1,1000,NULL);
			}*/ //-2 commentato 2 10 2001

	//Dati Meteo
			if(Init.m_iSetMeteo==1){
			OnSettaMeteo();
				OggettoPuntato.SetRefraPar(me.m_dP,me.m_dT,me.m_dH);
			}
			else{
				OggettoPuntato.SetRefraPar(950.,20.,0.5);
				me.m_dP=950.;
				me.m_dT=20.;
				me.m_dH=0.5;
				char s[20];
					sprintf(s,"%3.1f ",me.m_dH);
					SetDlgItemText(IDC_METH,s);
					sprintf(s,"%05.1f ",me.m_dP);
					SetDlgItemText(IDC_METPRE,s);
					sprintf(s,"%05.1f ",me.m_dT);
					SetDlgItemText(IDC_METT,s);

			}
					

	/////Inizio stella///	
		InitStar();
		//HWND	m_phwnd;
		//OggettoPuntato.m_phwnd=GetSafeHwnd();
		//AfxBeginThread((AFX_THREADPROC)UpdatePos,&OggettoPuntato);
		OggettoPuntato.InputStarJ2000(StellaCorrente.Ra,StellaCorrente.Dec,StellaCorrente.Epoch);
		


	//Comunicazione Assi
			if(Init.m_iCommAssi==1){ //1
				double val;
				
				LONG Valo;
				int err;
					
	////Uso Classi ACS TODO implementare struttura per i dati da visualizzare

	/// Asse X
				if(m_TipoControllore==1){//2

				if(m_NumeroControllori>=1){//3
				if(!AsseX.OpenComm(AsseX.m_Port,AsseX.m_Baud)){//4
				SetDlgItemText(IDC_M_AAR,"On");
					if(!Telescopio.m_TelMonTipo)
						AsseX.SetUserUnit(X,ARCSECS, Telescopio.m_Ridaz*Motori.m_RidMot1);
					else
					AsseX.SetUserUnit(X,ARCSECS, Telescopio.m_Ridar*Motori.m_RidMot1);
					AsseX.SetMaxMinPos(X,3600*Motori.m_Asse1Pl,3600*Motori.m_Asse1Ph);
					AsseX.SetMaxMinVel(X,m_telescopeInfo.MaxVelX,-1.*m_telescopeInfo.MaxVelX);
				AsseX.GetMotEncPos(X,&Valo);
				sprintf(buf,"%ld",Valo);
				SetDlgItemText(IDC_M_E1AR,buf);
				if(Init.m_iVerMot==1){//-4
					int ERR=AsseX.SetMotorOn(X);
						if(ERR==ACSOK){//-5
							if(AsseX.GetMotorStatus(X))
								SetDlgItemText(IDC_M_MAR,"On");
							else
								SetDlgItemText(IDC_M_MAR,"Off");

							if(AsseX.IsMoving(X))
								SetDlgItemText(IDC_M_HSAR,"On");
							else
								SetDlgItemText(IDC_M_HSAR,"Off");
						}//-5
						else
						{//-6
							char bu[50];
							sprintf(bu,"impossibile Attivare Motore X:ERROR %d",ERR);
								AfxMessageBox(bu);
						}//-6


					err = AsseX.GetMotVel(X,&val);
					sprintf(buf,"%6.1lf(\"/s)",val);
					SetDlgItemText(IDC_M_VAR,buf);
					err = AsseX.GetMotAcc(X,&val);
					sprintf(buf,"%6.1lf(\"/s)",val);
					SetDlgItemText(IDC_M_ACCAR,buf);
					Valo=0;
					AsseX.Comando_Array("AVSE",0,&Valo);
					Valo=(long)(150.*AsseX.CONVFACTOR[X]+0.5);
					AsseX.Comando_Array("AVSE",12,&Valo);
					statoc=1;
					}//-4
				}//4
				else{
					AfxMessageBox("Impossibile Comunicare con il Controllore AsseX\n Verificare le connessioni elettriche");
					SetDlgItemText(IDC_M_AAR,"Off");
				}
				}//3
		/// Fine Asse X
				if(m_NumeroControllori>=2){//5
		/// Asse Y
				if(!AsseY.OpenComm(AsseY.m_Port,AsseY.m_Baud)){//6
					SetDlgItemText(IDC_M_ADEC,"On");
				if(!Telescopio.m_TelMonTipo)
					AsseY.SetUserUnit(X,ARCSECS, Telescopio.m_Ridal*Motori.m_RidMot2);
				else
					AsseY.SetUserUnit(X,ARCSECS, Telescopio.m_Riddec*Motori.m_RidMot2);
				AsseY.SetMaxMinPos(X,3600*Motori.m_Asse2Pl,3600*Motori.m_Asse2Ph);
				AsseY.SetMaxMinVel(X,m_telescopeInfo.MaxVelY,-1.*m_telescopeInfo.MaxVelY);

				AsseY.GetMotEncPos(X,&Valo);
				sprintf(buf,"%ld",Valo);
				SetDlgItemText(IDC_M_E1DEC,buf);
				if(Init.m_iVerMot==1){//-4
					int ERR=AsseY.SetMotorOn(X);
					if(ERR==ACSOK){//-5
				if(AsseY.GetMotorStatus(X))
					SetDlgItemText(IDC_M_MDEC,"On");
				else
					SetDlgItemText(IDC_M_MDEC,"Off");
				if(AsseY.IsMoving(X))
					SetDlgItemText(IDC_M_HSDEC,"On");
					else
					SetDlgItemText(IDC_M_HSDEC,"Off");
					}//-5
				else
				{//-6
					char bu[50];
					sprintf(bu,"impossibile Attivare Motore Y:ERROR %d",ERR);
					AfxMessageBox(bu);
				}//-6
				err=AsseY.GetMotVel(X,&val);
				sprintf(buf,"%6.1lf(\"/s)",val);
				SetDlgItemText(IDC_M_VDEC,buf);
				err = AsseY.GetMotAcc(X,&val);
				sprintf(buf,"%6.1lf(\"/s)",val);
				SetDlgItemText(IDC_M_ACCDEC,buf);
				Valo=0;
				AsseY.Comando_Array("AVSE",0,&Valo);
				Valo=(long)(150*AsseY.CONVFACTOR[X]+0.5);
				AsseY.Comando_Array("AVSE",12,&Valo);
				statoc++;
				}//-4
				}//6
				else{
					AfxMessageBox("Impossibile Comunicare con il Controllore AsseY\n Verificare le connessioni elettriche");
					SetDlgItemText(IDC_M_ADEC,"Off");
				}
				}//5
	/// Fine Asse Y
				if(m_NumeroControllori==3){//7
					if(Telescopio.m_TelMonTipo==0 && Cupola.m_ControlloreCupola!=3){
	///	Asse Z
				/*if(!AsseZ.OpenComm(AsseZ.m_Port,AsseZ.m_Baud)){//8
				if(Cupola.m_ControlloreCupola==3)
					AsseZ.SetUserUnit(X,GRAD, Cupola.m_Riduzione);
				else
					AsseZ.SetUserUnit(X,ARCSECS, Telescopio.m_Ridel*Motori.m_RidMot3);
				AsseZ.GetMotEncPos(X,&Valo);
				sprintf(buf,"%ld",Valo);
				SetDlgItemText(IDC_M_E1PA,buf);
				if(Cupola.m_ControlloreCupola!=3){//9
				if(AsseZ.GetMotorStatus(X))
					SetDlgItemText(IDC_M_APA,"On");
				else
					SetDlgItemText(IDC_M_APA,"Off");
				
				err=AsseZ.GetMotVel(X,&val);
				sprintf(buf,"%6.1lf(\"/s)",val);
				SetDlgItemText(IDC_M_VDEC,buf);
				Valo=0;
				AsseZ.Comando_Array("AVSE",0,&Valo);}//9
				statoc++;
				}//8
				else
					AfxMessageBox("Impossibile Comunicare con il Controllore AsseX\n Verificare le connessioni elettriche");*/
					}
					else {
						
						if(Cupola.m_ControlloreCupola==3){
							if(!AsseCupola.OpenComm(AsseCupola.m_Port,AsseCupola.m_Baud)){
								AsseCupola.SetUserUnit(X,ARCSECS,Cupola.m_Riduzione);
								AsseCupola.GetMotEncPos(X,&Valo);
								sprintf(buf,"%ld",Valo);
								SetDlgItemText(IDC_M_E1PA,buf);
							//Verifica Pos iniziale Cupola	
								if(Init.m_iVerPosCup==1){
									//OnCupolaSettazero();
								}
							//Apertura Cupola
								if(Init.m_iApriCup==1){
									OnCupolaApri();
								}
								//	AfxMessageBox("MessageBox");
									statoc++;
							}
							else
								AfxMessageBox("Impossibile Comunicare con il Controllore Cupola\n Verificare le connessioni elettriche");				
						}
					}
				}//7
				
		/// Fine Asse Z
				}//2
				else{//9
					////	Controllore SB1003
				}//9
				}//1

			}//-1

			
		//  Attivo Voci Menu
		if(statoc==m_NumeroControllori){
			
			
		
			if(Init.m_iVerTelPos==1)
					{
						//OnSettaPosHome();
						OnSettaZezoTelFile();
						Sleep(300);
						GetTelInfo();
						StellaCorrente.Ra=m_telescopeInfo.RA;
						StellaCorrente.Dec=m_telescopeInfo.DEC;
						OggettoPuntato.InputStarJ2000(m_telescopeInfo.RA,m_telescopeInfo.DEC,StellaCorrente.Epoch);
						/*long Valo=0;
						Valo=(long)(ZeroX*3600*AsseX.CONVFACTOR[X]+0.5);
						AsseX.Comando_Array("AVSE",8,&Valo);
						AsseX.ExecProg("HOMEX");

						Valo=(long)(ZeroY*3600*AsseY.CONVFACTOR[X]+0.5);
						AsseY.Comando_Array("AVSE",8,&Valo);
						AsseY.ExecProg("HOMEX");
						CDStopPunta pos;
						pos.m_iTipoMoto=1;
						pos.m_InputString=_T("Ricerca Zero: Attendere");
						int risp=pos.DoModal();
						if (risp == IDOK){;
							}
						else if (risp == IDCANCEL){;
						}*/

					}
		SetTimer(2,500,NULL);
		OnCupolaInseguimento();
		SetTelTrackVel();
		GetTelInfo();
		pMenu->EnableMenuItem(IDM_PUNTAMENTO_CATALOGO, MF_ENABLED);
		pMenu->EnableMenuItem(IDM_PUNTAMENTO_MINIMO, MF_ENABLED);
		pMenu->EnableMenuItem(IDM_PUNTAMENTO_COORDINATE, MF_ENABLED);
		pMenu->EnableMenuItem(IDM_PUNTAMENTO_PIANETI, MF_ENABLED);
		pMenu->EnableMenuItem(IDM_CENTRAGGIO_MANUALE, MF_ENABLED);
		pMenu->EnableMenuItem(IDM_TELESCOPIO_JOYST, MF_ENABLED);
		pMenu->EnableMenuItem(IDM_SETTA_POS_HOME, MF_ENABLED);
		pMenu->EnableMenuItem(IDM_TELESCOPIO_SETSW, MF_ENABLED);
		pMenu->EnableMenuItem(IDM_TELESCOPIO_SETTAZERO_STAR, MF_ENABLED);
		}

	//	pMenu->EnableMenuItem(IDM_CONTROLLORE,MF_DISABLED|MF_GRAYED);
		
		pMenu->EnableMenuItem(IDM_CAMBIA_CONFIG, MF_ENABLED);
		pMenu->EnableMenuItem(IDM_INIZIALIZZAZIONE, MF_DISABLED|MF_GRAYED);

		SetDlgItemText(IDC_MESSAGGI,mesg[8]);
		////
		//pMenu->EnableMenuItem(IDM_PUNTAMENTO_CATALOGO, MF_ENABLED);
		//
		//}
		
		//}

}


void CTcsDlg::OnCambiaConfig() 
{
	// TODO: Add your command handler code here
	CMenu* pMenu = GetMenu();
		ASSERT(pMenu != NULL);
	  pMenu->EnableMenuItem(IDM_OSSERVATORIO, MF_ENABLED);
	  pMenu->EnableMenuItem(IDM_MOTORI, MF_ENABLED);
	  if(m_Osservatorio.Gps)
		pMenu->EnableMenuItem(IDM_GPS, MF_ENABLED);
	  if(m_Osservatorio.Meteo)
		pMenu->EnableMenuItem(IDM_METEO, MF_ENABLED);
	  if(!m_TipoControllore)
		pMenu->EnableMenuItem(IDM_CONTROLLORE_SB1000, MF_ENABLED);
	  else
		pMenu->EnableMenuItem(IDM_CONTROLLORE_SB1291, MF_ENABLED);
	  pMenu->EnableMenuItem(IDM_CUPOLA, MF_ENABLED);  
	  pMenu->EnableMenuItem(IDM_SALVA_CONFIG, MF_ENABLED);
	  pMenu->EnableMenuItem(IDM_DIRECTORY, MF_ENABLED);
	  pMenu->EnableMenuItem(IDM_TELE, MF_ENABLED);
	

}

void CTcsDlg::OnSalvaConfig() 
{
	// TODO: Add your command handler code here
	
}



int CTcsDlg::ReadConfig()
{

	char* pFileName = "tcs.cdv";
	//CStdioFile f1;
	//CMyString Temp;
	//char Key[20];
		char *buf = new char[100];
		memset(buf, 0, sizeof(buf));
		CTcsCfg Cfg;

		if (!Cfg.OpenCfgFile(pFileName)){
			AfxMessageBox("Inpossibile trovare il file di Configurazione");
			CFileDialog fdlg(TRUE);
			if(fdlg.DoModal()==IDOK){
				CString file =fdlg.GetFileName();
				Cfg.OpenCfgFile((LPCTSTR)file);
			}

		}
			//Mettere il controllo comune apri file;

	///INIZIO LETTURA CONFIGURAZIONE
	//

	//[GENERALE]
		Cfg.ReadKeyValue("GENERALE","TipoCupola", &Cupola.m_TipoCupola);
		Cfg.ReadKeyValue("GENERALE","TipoTelescopio", &Telescopio.m_TelTipo);
		Cfg.ReadKeyValue("GENERALE","Montatura", &Telescopio.m_TelMonTipo);
		Cfg.ReadKeyValue("GENERALE","TipoControlloreAssi", &m_TipoControllore);
		Cfg.ReadKeyValue("GENERALE","NumeroAssi", &m_NumeroAssi);
		if(m_TipoControllore==1)
			Cfg.ReadKeyValue("GENERALE","NumeroControllori", &m_NumeroControllori);
			//AfxMessageBox("Letto GENERALE");
	//[OSSERVATORIO]
		Cfg.ReadKeyValue("OSSERVATORIO","Altitudine", &m_Osservatorio.Altitudine);
		Cfg.ReadKeyValue("OSSERVATORIO","Gps", &m_Osservatorio.Gps);
		Cfg.ReadKeyValue("OSSERVATORIO","Meteo", &m_Osservatorio.Meteo);
		Cfg.ReadKeyValue("OSSERVATORIO","Nome", m_Osservatorio.Nome);
		Cfg.ReadKeyValue("OSSERVATORIO","Latitudine", buf);
		CAngle lat(buf);
		m_Osservatorio.Latitudine=lat.Ang;
		Cfg.ReadKeyValue("OSSERVATORIO","Longitudine", buf);
		CAngle lon(buf);
		m_Osservatorio.Longitudine=lon.Ang;
		Cfg.ReadKeyValue("OSSERVATORIO","Timezone", &m_Osservatorio.Timezone);
		//AfxMessageBox("Letto OSSE");
	//[TELESCOPIO]
		Cfg.ReadKeyValue("TELESCOPIO","Nome", buf);
		Telescopio.m_Nome.Format("%s",(LPCTSTR)buf);

		Cfg.ReadKeyValue("TELESCOPIO","DiametroSpecchio",&Telescopio.m_Diametro);
		Cfg.ReadKeyValue("TELESCOPIO","LunghezzaFocale", &Telescopio.m_Focale);
		if(Telescopio.m_TelMonTipo)
		{
		Cfg.ReadKeyValue("TELESCOPIO","RapportoRiduzioneAR", &Telescopio.m_Ridar);
		Cfg.ReadKeyValue("TELESCOPIO","RapportoRiduzioneDEC",&Telescopio.m_Riddec);
		}
		else
		{
		Cfg.ReadKeyValue("TELESCOPIO","RapportoRiduzioneAZ", &Telescopio.m_Ridaz);
		Cfg.ReadKeyValue("TELESCOPIO","RapportoRiduzioneAL", &Telescopio.m_Ridal);
		Cfg.ReadKeyValue("TELESCOPIO","RapportoRiduzioneDE", &Telescopio.m_Ridde);
		}
		Cfg.ReadKeyValue("TELESCOPIO","CampoDiVista", &Telescopio.m_Fov);

	//[CUPOLA]
		Cfg.ReadKeyValue("CUPOLA","ControlloreCupola",&Cupola.m_ControlloreCupola);
		Cfg.ReadKeyValue("CUPOLA","CupolaEncoderRis", &Cupola.m_RisoluzioneEncoder);
		Cfg.ReadKeyValue("CUPOLA","StadioRiduzione", &Cupola.m_Riduzione);
		//AfxMessageBox("Letto buf");
		if(m_NumeroAssi>=2)
		{
			//[MOTORE_AR_AZ]
		//Cfg.ReadKeyValue("MOTORE_AR_AZ","TipoMotore", &Motori.m_Tipo1);
		Cfg.ReadKeyValue("MOTORE_AR_AZ","RisoluzioneEncoder1", &Motori.m_Asse1RisEnc1);
		Cfg.ReadKeyValue("MOTORE_AR_AZ","RisoluzioneEncoder2", &Motori.m_Asse1ResEnc2);
		Cfg.ReadKeyValue("MOTORE_AR_AZ","NumeroGiriMotore", &Motori.m_Mot1Giri);
		Cfg.ReadKeyValue("MOTORE_AR_AZ","VelocitaMassima", &Motori.m_Asse1MaxVel);
		m_telescopeInfo.MaxVelX=Motori.m_Asse1MaxVel*3600.;
		m_telescopeInfo.MaxAccX=Motori.m_Asse1MaxVel*3600.;
		m_telescopeInfo.SlewVelX=Motori.m_Asse1MaxVel*3600.;
		Cfg.ReadKeyValue("MOTORE_AR_AZ","PosizioneLimiteInf",&Motori.m_Asse1Pl);
		Cfg.ReadKeyValue("MOTORE_AR_AZ","PosizioneLimiteSup", &Motori.m_Asse1Ph);
		Cfg.ReadKeyValue("MOTORE_AR_AZ","RiduzioneMotore", &Motori.m_RidMot1);
		Cfg.ReadKeyValue("MOTORE_AR_AZ","PosizioneEncoder1",&Motori.m_Asse1PosEnc1);
		Cfg.ReadKeyValue("MOTORE_AR_AZ","PosizioneEncoder2", &Motori.m_Asse1PosEnc2);
		//AfxMessageBox("Letto MOT1");
	//[MOTORE_DEC_AL]
		//Cfg.ReadKeyValue("MOTORE_DEC_AL","TipoMotore", &Motori.m_Tipo2);
		Cfg.ReadKeyValue("MOTORE_DEC_AL","RisoluzioneEncoder1", &Motori.m_Asse2RisEnc1);
		Cfg.ReadKeyValue("MOTORE_DEC_AL","RisoluzioneEncoder2", &Motori.m_Asse2ResEnc2);
		Cfg.ReadKeyValue("MOTORE_DEC_AL","NumeroGiriMotore", &Motori.m_Mot2Giri);
		Cfg.ReadKeyValue("MOTORE_DEC_AL","VelocitaMassima", &Motori.m_Asse2MaxVel);
		m_telescopeInfo.MaxVelY=Motori.m_Asse2MaxVel*3600.;
		m_telescopeInfo.MaxAccY=Motori.m_Asse2MaxVel*3600.;
		m_telescopeInfo.SlewVelY=Motori.m_Asse2MaxVel*3600.;
		Cfg.ReadKeyValue("MOTORE_DEC_AL","PosizioneLimiteInf",&Motori.m_Asse2Pl);
		Cfg.ReadKeyValue("MOTORE_DEC_AL","PosizioneLimiteSup", &Motori.m_Asse2Ph);
		Cfg.ReadKeyValue("MOTORE_DEC_AL","RiduzioneMotore", &Motori.m_RidMot2);
		Cfg.ReadKeyValue("MOTORE_DEC_AL","PosizioneEncoder1",&Motori.m_Asse2PosEnc1);
		Cfg.ReadKeyValue("MOTORE_DEC_AL","PosizioneEncoder2", &Motori.m_Asse2PosEnc2);
		//AfxMessageBox("Letto MOT2");
		}
		if(m_NumeroAssi==3)
		{
	//[MOTORE_DEROT]
		Cfg.ReadKeyValue("MOTORE_DEROT","RisoluzioneEncoder1", &Motori.m_Asse3RisEnc1);
		Cfg.ReadKeyValue("MOTORE_DEROT","RisoluzioneEncoder2", &Motori.m_Asse3ResEnc2);
		Cfg.ReadKeyValue("MOTORE_DEROT","NumeroGiriMotore", &Motori.m_Mot3Giri);
		Cfg.ReadKeyValue("MOTORE_DEROT","VelocitaMassima", &Motori.m_Asse3MaxVel);
		m_telescopeInfo.MaxVelZ=Motori.m_Asse3MaxVel*3600.;
		m_telescopeInfo.MaxAccZ=Motori.m_Asse3MaxVel*3600.;
		m_telescopeInfo.SlewVelZ=Motori.m_Asse3MaxVel*3600.;
		Cfg.ReadKeyValue("MOTORE_DEROT","PosizioneLimiteInf", &Motori.m_Asse3Pl);
		Cfg.ReadKeyValue("MOTORE_DEROT","PosizioneLimiteSup", &Motori.m_Asse3Ph);
		Cfg.ReadKeyValue("MOTORE_DEROT","RiduzioneMotore", &Motori.m_RidMot3);
		Cfg.ReadKeyValue("MOTORE_DEROT","PosizioneEncoder1",&Motori.m_Asse3PosEnc1);
		Cfg.ReadKeyValue("MOTORE_DEROT","PosizioneEncoder2",&Motori.m_Asse3PosEnc2);
		}
		if(!m_TipoControllore)
		{	//[SB100X]
		Cfg.ReadKeyValue("SB100X","PortaComunicazione",&AsseX.m_Baud);
		Cfg.ReadKeyValue("SB100X","BaudRate", &AsseX.m_Baud);
		}
		else
		{	//[SB129X]
			if(m_NumeroControllori==1)
			{
				Cfg.ReadKeyValue("SB129X","PortaComunicazione1", &AsseX.m_Port);
				Cfg.ReadKeyValue("SB129X","BaudRate1", &AsseX.m_Baud);
			}
			if(m_NumeroControllori==2)
			{
				Cfg.ReadKeyValue("SB129X","PortaComunicazione1", &AsseX.m_Port);
				Cfg.ReadKeyValue("SB129X","BaudRate1", &AsseX.m_Baud);
				Cfg.ReadKeyValue("SB129X","PortaComunicazione2",&AsseY.m_Port);
				Cfg.ReadKeyValue("SB129X","BaudRate2", &AsseY.m_Baud);
			}
			if(m_NumeroControllori==3)
			{
				Cfg.ReadKeyValue("SB129X","PortaComunicazione1",&AsseX.m_Port);
				Cfg.ReadKeyValue("SB129X","BaudRate1", &AsseX.m_Baud);
				Cfg.ReadKeyValue("SB129X","PortaComunicazione2",&AsseY.m_Port);
				Cfg.ReadKeyValue("SB129X","BaudRate2", &AsseY.m_Baud);
				if(Cupola.m_ControlloreCupola==3){

				Cfg.ReadKeyValue("SB129X","PortaComunicazione3", &AsseCupola.m_Port);
				Cfg.ReadKeyValue("SB129X","BaudRate3", &AsseCupola.m_Baud);
				}
				else
				{
					Cfg.ReadKeyValue("SB129X","PortaComunicazione3", &AsseZ.m_Port);
					Cfg.ReadKeyValue("SB129X","BaudRate3", &AsseZ.m_Baud);
				}
			}
		}
	//[PADDLE]
		Cfg.ReadKeyValue("PADDLE","VelocitaBassa", &PadVelBassa);
		Cfg.ReadKeyValue("PADDLE","VelocitaMedia", &PadVelMedia);
		Cfg.ReadKeyValue("PADDLE","VelocitaAlta", &PadVelAlta);
	//[CATALOGHI]
		Cfg.ReadKeyValue("CATALOGHI","dir", buf);
	//[POSZERO]
		Cfg.ReadKeyValue("POSZERO","ZeroX", &ZeroX);
		Cfg.ReadKeyValue("POSZERO","ZeroY", &ZeroY);
		Cfg.ReadKeyValue("POSZERO","ZeroZ", &ZeroZ);
		Cfg.ReadKeyValue("POSZERO","ZeroCup", &ZeroCup);
		//AfxMessageBox(buf);

		
		Cfg.CloseCfgFile();
	//FINE LETTURA CONFIGURAZIONE////////////
		
	//DIREZIONE ASSE TELESCOPIO
		if(Telescopio.m_TelMonTipo==0){
		m_telescopeInfo.DirX=-1;
		m_telescopeInfo.DirZ=1;
		m_telescopeInfo.DirY=1;
		}
		else{
		m_telescopeInfo.DirX=1;
		m_telescopeInfo.DirY=1;
		}

	// INIZIALIZZAZIONE PARAMETRI TIME
	TelTime.SetLongitudine(m_Osservatorio.Longitudine);
	TelTime.SetTimeZone(m_Osservatorio.Timezone);
	/////// SOLSYSTEM
	Sole.SetObj(SUN);
	Luna.SetObj(MOON);
	Sole.SetLocation(m_Osservatorio.Latitudine,
					 m_Osservatorio.Longitudine,
		             m_Osservatorio.Altitudine);
	Luna.SetLocation(m_Osservatorio.Latitudine,
					 m_Osservatorio.Longitudine,
		             m_Osservatorio.Altitudine);
	
	//SOLE//////
	Sole.SetJD(TelTime.GetJD());//-(long();
	Sole.CalculatePos();
	FormatCoord(buf,Sole.HRa,0);
	SetDlgItemText(IDC_GEM_INFO_SRA, buf);
	FormatCoord(buf,Sole.DDec,0);
	SetDlgItemText(IDC_GEM_INFO_SDEC, buf);
	Sole.RiseSet();
	FormatCoord(buf,Sole.RiseTu,1);
	SetDlgItemText(IDC_GEM_INFO_SRISE, buf);
	FormatCoord(buf,Sole.SetTu,1);
	SetDlgItemText(IDC_GEM_INFO_SSET, buf);
	Sole.RiseSet(18.);
	FormatCoord(buf,Sole.SetTu,1);
	SetDlgItemText(IDC_GEM_INFO_IN, buf);
	//Sole.SetJD((TelTime.GetJD()+1.));
	Sole.RiseSet(18.);
	FormatCoord(buf,Sole.RiseTu,1);
	SetDlgItemText(IDC_GEM_INFO_FN, buf);
	//LUNA///

	Luna.SetJD(TelTime.GetJD());
	Luna.CalculatePos();
	FormatCoord(buf,Luna.HRa,0);
	SetDlgItemText(IDC_GEM_INFO_LRA, buf);
	FormatCoord(buf,Luna.DDec,0);
	SetDlgItemText(IDC_GEM_INFO_LDEC, buf);
	Luna.RiseSet();
	FormatCoord(buf,Luna.RiseTu,1);
	SetDlgItemText(IDC_GEM_INFO_LRISE, buf);
	FormatCoord(buf,Luna.SetTu,1);
	SetDlgItemText(IDC_GEM_INFO_LSET, buf);
	sprintf(buf,"%5.1f",(Luna.Phase));
	SetDlgItemText(IDC_GEM_INFO_LFASE, buf);

	////////
	//AfxBeginThread((AFX_THREADPROC)UpdatePos,&OggettoPuntato);*///THREAD_PRIORITY_NORMAL);

	delete[] buf;
	return TRUE;
}


void  CTcsDlg::OnSettaTempo()
{
		ti.DoModal();
}

void  CTcsDlg::OnSettaMeteo()
{
		if(me.DoModal()==IDOK)
			{	char s[20];
				sprintf(s,"%3.1f ",me.m_dH);
				SetDlgItemText(IDC_METH,s);
				sprintf(s,"%05.1f ",me.m_dP);
				SetDlgItemText(IDC_METPRE,s);
				sprintf(s,"%05.1f ",me.m_dT);
				SetDlgItemText(IDC_METT,s);
			}
}


void  CTcsDlg::UpdateSole()
{
	char buf[50];
	Sole.SetJD(TelTime.GetJD());
	Sole.CalculatePos();
	FormatCoord(buf,Sole.HRa,0);
	SetDlgItemText(IDC_GEM_INFO_SRA, buf);
	FormatCoord(buf,Sole.DDec,0);
	SetDlgItemText(IDC_GEM_INFO_SDEC, buf);

	Luna.SetJD(TelTime.GetJD());
	Luna.CalculatePos();
	FormatCoord(buf,Luna.HRa,0);
	SetDlgItemText(IDC_GEM_INFO_LRA, buf);
	FormatCoord(buf,Luna.DDec,0);
	SetDlgItemText(IDC_GEM_INFO_LDEC, buf);

}

/*void  CTcsDlg::()
{

}*/

/*void  CTcsDlg::()
{

}*/



//////////////////MESSAGGI ESTERNI

void  CTcsDlg::OnFinePuntaMin(LPARAM lp,WPARAM wp)
{
	CMenu* pMenu = GetMenu();
		ASSERT(pMenu != NULL);

	/*if( bSuspUpdate==TRUE)
	{
		bSuspUpdate=FALSE;
		Sleep(200);
	}*/
	pMenu->EnableMenuItem(IDM_PUNTAMENTO_CATALOGO, MF_ENABLED);
	pMenu->EnableMenuItem(IDM_PUNTAMENTO_MINIMO, MF_ENABLED);
	pMenu->EnableMenuItem(IDM_PUNTAMENTO_COORDINATE, MF_ENABLED);
}

/*void  CTcsDlg::(LPARAM lp,WPARAM wp)
{

}*/




void FormatCoord(char *buf, double ang, int flag)
{
	double h,m,s, sign=1;
	int ss=O2hms(ang ,&h,&m,&s);
	if(ss==1)sign=-1;
	if(flag==0 && ss==1)
		sprintf(buf,"-%02d:%02d:%04.1lf",(int)(h),(int)m,s);
	if(flag==0 && ss==0)
		sprintf(buf,"%02d:%02d:%04.1lf",(int)(h),(int)m,s);
	if(flag==1 && ss==1)
		sprintf(buf,"-%02d:%02d",(int)(h*sign),(int)(m+s/60.));
	if(flag==1 && ss==0)
	    sprintf(buf,"%02d:%02d",(int)(h*sign),(int)(m+s/60.));

}


int  O2hms(double A,double *O,double *Mi, double *Se)
{
	double M;

	M=modf(A,O);
	M=modf((M*60.),Mi);
	M=modf((M*60.),Se);
	//*Se=M*60.;
	*O=fabs(*O);
	*Mi=fabs(*Mi);
	*Se=fabs(*Se);
	if(A<0.) return 1;
	else return 0;
}


int VerificaVisibilitaAstro()
{
	return 0;
}


UINT UpdatePos(LPVOID lpPar)
{
	UINT nExitCode=0;
	CAstroClass *t = (CAstroClass *)lpPar;
	//DWORD	oldcount = GetTickCount();
	
	while(!bThread)
	{
		//if((GetTickCount()-oldcount)>1000){
		if(!bSuspUpdate){
		  //t->CalcStarPos();
		//::PostMessage(t->m_phwnd,TCS_MSG_UPDATEPOS,0,0);
		
	//oldcount=GetTickCount();
		}
		Sleep(1000);
	}
	
	return 0;
}

void CTcsDlg::OnPuntamentoCatalogo() 
{
	// TODO: Add your command handler code here
	SetTrackY=SetTrackX=0;
	SetTrackCup=0;
	pis.DoModal();
}

void CTcsDlg::OnCupolaApri() 
{
	// TODO: Add your command handler code here
		if(AsseCupola.CommStatus==1){

			AsseCupola.ExecProg("APRICUP");
			
		}
		
		
		m_CupolaInfo.StatusApertura=1;
	

}

void CTcsDlg::OnCupolaChiudi() 
{
	// TODO: Add your command handler code here
	if(AsseCupola.CommStatus==1){
		AsseCupola.ExecProg("CHIUDCUP");
		}

	m_CupolaInfo.StatusApertura=0;
}

void CTcsDlg::OnCupolaOvest() 
{
	// TODO: Add your command handler code here
	 if(AsseCupola.CommStatus==1){
		 AsseCupola.ExecProg("SXCUP");
		
		}

	m_CupolaInfo.StatusRotazione=1;
	m_CupolaInfo.Dir=1;
}

void CTcsDlg::OnCupolaEst() 
{
	// TODO: Add your command handler code here
	if(AsseCupola.CommStatus==1){
		AsseCupola.ExecProg("DXCUP");

		}

	m_CupolaInfo.StatusRotazione=1;
	m_CupolaInfo.Dir=-1;
}

void CTcsDlg::OnCupolaVai() 
{
	// TODO: Add your command handler code here
		CInputDlg Dlg;
		if(AsseCupola.CommStatus==1){
		
			
	Dlg.m_InputLabel=_T("Az.(deg)");
	int nResponse = Dlg.DoModal();
	if (nResponse == IDOK)
	{
		long int azi=3600*atof(Dlg.m_InputValue)*AsseCupola.CONVFACTOR[X];
		AsseCupola.Comando_Array("AVSE", 10, &azi);
		AsseCupola.ExecProg("PUNTA");
	}
		
		}

		m_CupolaInfo.StatusRotazione=1;
		m_CupolaInfo.Dir=-1;

}

void CTcsDlg::OnCupolaSettazero() 
{
	// TODO: Add your command handler code here
	  if(AsseCupola.CommStatus==1){
		  AsseCupola.ExecProg("HOMECUP");
		  UpdateTcsLog("DOME:searching home position");
		}

	
}

void CTcsDlg::OnCupolaFerma() 
{
	// TODO: Add your command handler code here
	AsseCupola.ExecProg("FERMACUP");
	m_CupolaInfo.StatusRotazione=0;
	m_CupolaInfo.Dir=0;
	UpdateTcsLog("DOME:stopped");
}

void CTcsDlg::OnCupolaInseguimento() 
{
	// TODO: Add your command handler code here
	SetTimer(3,5100,NULL);
}

void CTcsDlg::OnTelescopioCentra() 
{
	// TODO: Add your command handler code here
		CTcsDlg::OnComandiTastierino();
}

void CTcsDlg::OnTelescopioJoyst() 
{
	// TODO: Add your command handler code here
		long i=1;
		Joy=1;
		//etZeroStar=1;
		SetTrackX=SetTrackY=0;
		long xvel=(long)(-1.*OggettoPuntato.ObsVAZ*AsseX.CONVFACTOR[X]+0.5);
		long yvel=(long)(OggettoPuntato.ObsVEL*AsseY.CONVFACTOR[X]+0.5);
		AsseX.Comando_Array("AVSE",0,&i);
		AsseX.Comando_Array("AVSE",18,&xvel);
		AsseY.Comando_Array("AVSE",0,&i);
		AsseY.Comando_Array("AVSE",18,&yvel);
		if(AfxMessageBox("Accendere, Usare, Spegnere il Joistick\n\n Al Termine, premere OK ") == IDOK){
			AsseX.GetInpPortStatus(1,&i);
			if(i==1)
			AfxMessageBox("Si Prega di spegnere\n il tastierino!!!");

		i=0;
		AsseX.Comando_Array("AVSE",0,&i);
		AsseY.Comando_Array("AVSE",0,&i);
		}
		Joy=0;
		noCentrato=1;
		SetTrackX=SetTrackY=1;
		//etZeroStar=0;
		
		OggettoPuntato.CalcStarPos();
	GetTelInfo();
	double H=OggettoPuntato.ObsH*DR2H;
	if(H>24)H-=24.;
	if(H<0.)H+=24.;
	double DPH=(m_telescopeInfo.H-H)*54000.;
	double DPDEC=(m_telescopeInfo.DEC-OggettoPuntato.ObsDEC*DR2D)*3600;
	double DPX=-1.0*(m_telescopeInfo.AZ-OggettoPuntato.ObsAZ)*3600;
	double DPY=(m_telescopeInfo.EL-OggettoPuntato.ObsEL)*3600;
	char buf[100];
	sprintf(buf,"Correzioni: dAz=%6.2f -- dEl=%6.2f",DPX,DPY);
	SetDlgItemText(IDC_MESSAGGI,buf);
	ofstream errout(m_ErrFileName,ios::app);
	if(!errout.bad()){
		errout<<TelTime.GetUTStr()<<"-->\t";
		errout<<OggettoPuntato.ObsRA<<" ";
		errout<<OggettoPuntato.ObsDEC*DR2D<<" ";
		errout<<H<<" ";
		errout<<DPH<<" ";
		errout<<DPDEC<<" ";
		errout<<DPX<<" ";
		errout<<DPY<<endl;
		errout.close();
	}
}

void CTcsDlg::OnTelescopioSetHome() 
{
	// TODO: Add your command handler code here
	double val,val1;
	double DPX,DPY;
	GetTelInfo();
	OggettoPuntato.CalcStarPos();
	if(Telescopio.m_TelMonTipo==0){
		val=(180 - OggettoPuntato.ObsAZ)*3600;
		val1=OggettoPuntato.ObsEL*3600;	
		DPX=m_telescopeInfo.PosX-val;
		DPY=m_telescopeInfo.PosY-val1;

	}
	else
	{
		val=OggettoPuntato.ObsH;
		if(val>12)val=(val-24)*54000;
		else val=val*54000;
		val1=OggettoPuntato.ObsDEC*3600;
		DPX=m_telescopeInfo.PosX-val;
		DPY=m_telescopeInfo.PosY-val1;
	}
	AsseX.SetAxisZeroPos(X,val);
	AsseY.SetAxisZeroPos(X,val1);
	
	FILE *fp;
	fp=fopen("Zeri.dat","wt");
	fprintf(fp,"%ld %ld",(long)(-1.*DPX*AsseX.CONVFACTOR[X]+0.5),(long)(-1.*DPY*AsseY.CONVFACTOR[X]));
	fclose(fp);
}

void CTcsDlg::OnTelescopioSettazeroStar() 
{
	// TODO: Add your command handler code here
	double val, val1;
	double DPX,DPY;
	/*OggettoPuntato.CalcStarPos();
	val=(OggettoPuntato.ObsAZ-180)*3600;
	val1=OggettoPuntato.ObsEL*3600;*/
	SetZeroStar=1;
	pis.m_iFlagUso=2;
	if(pis.DoModal()==IDOK){
		//OggettoPuntato.CalcStarPos();
		OnTelescopioJoyst();
			//LEGGO POSIZIONE ASSI E SETTO LA POSIZIONE
			GetTelInfo();
			OggettoPuntato.CalcStarPos();
			if(Telescopio.m_TelMonTipo==0){
			val=(180 - OggettoPuntato.ObsAZ)*3600;
			val1=OggettoPuntato.ObsEL*3600;
			DPX=m_telescopeInfo.PosX-val;
			DPY=m_telescopeInfo.PosY-val1;

			}
			else{
				val=OggettoPuntato.ObsH;
				if(val>12)val=(val-24)*54000;
				else val=val*54000;
				val1=OggettoPuntato.ObsDEC*3600;
				DPX=m_telescopeInfo.PosX-val;
				DPY=m_telescopeInfo.PosY-val1;
			}			

			//AsseX.GetMotPos(X,&val);
			//if(SetTrackX==1)
				AsseX.SetAxisZeroPos(X,val);
			//AsseY.GetMotPos(X,&val1);
			//if(SetTrackY==1)
			    AsseY.SetAxisZeroPos(X,val1);

				FILE *fp;
				fp=fopen("Zeri.dat","wt");
				fprintf(fp,"%ld %ld",(long)(-1.*DPX*AsseX.CONVFACTOR[X]+0.5),(long)(-1.*DPY*AsseY.CONVFACTOR[X]));
				fclose(fp);
	}
	pis.m_iFlagUso=0;
	SetZeroStar=0;
	
}

void CTcsDlg::OnSettaPosHome() 
{
	// TODO: Add your command handler code here
	//double val, val1;
	//double DPX,DPY;

	long ValoX=0,ValoY=0,ValoZ=0;
	FILE *fp;
	///Modificare per tre assi
	fp=fopen("Zeri.dat","rt");
	if(fp!=NULL){
		fscanf(fp,"%ld %ld",&ValoX,&ValoY);
		fclose(fp);
	}

	if(Telescopio.m_TelMonTipo==0){
		ValoX+=(long)(ZeroX*3600*AsseX.CONVFACTOR[X]+0.5-30*AsseX.CONVFACTOR[X]);
		AsseX.Comando_Array("AVSE",8,&ValoX);
		AsseX.ExecProg("HOMEX");

		ValoY+=(long)(ZeroY*3600*AsseY.CONVFACTOR[X]-60.*AsseY.CONVFACTOR[X]+0.5);
		AsseY.Comando_Array("AVSE",8,&ValoY);
		AsseY.ExecProg("HOMEX");
	}
	else
	{
		//Valo=(long)(ZeroX*3600*AsseX.CONVFACTOR[X]+0.5);
		AsseX.Comando_Array("AVSE",8,&ValoX);
		AsseX.ExecProg("HOMEX");

		//Valo=(long)(ZeroY*3600*AsseY.CONVFACTOR[X]+0.5);
		AsseY.Comando_Array("AVSE",8,&ValoY);
		AsseY.ExecProg("HOMEX");
	}

	CDStopPunta pos;
	pos.m_iTipoMoto=1;
	pos.m_InputString=_T("Ricerca Zero: Attendere");
	int risp=pos.DoModal();
	if (risp == IDOK){;
						}
	else if (risp == IDCANCEL){
		AsseX.Comando_Moto("PS");
		AsseY.Comando_Moto("PS");
			
		AsseX.StopMove(X);
		AsseY.StopMove(X);
		/*if(Telescopio.m_TelMonTipo==0){
			AsseZ.Comando_Moto("PS");
			AsseY.StopMove(X);
		}*/
		
		}


}


void CTcsDlg::OnControlloreY() 
{
	// TODO: Add your command handler code here
	KillTimer(2);
	if(AsseY.CommStatus==1)
	 AsseY.Consolle();
	SetTimer(2,1000,NULL);
}

void CTcsDlg::OnStartTrack(LPARAM lp,WPARAM wp)
{
/*	if(wp==0){
		AsseX.SetTrackMode(X);
		OggettoPuntato.CalcStarPos();
		AsseX.SetMotVel(X,m_telescopeInfo.DirX*OggettoPuntato.ObsVAZ);
		AsseX.StartMove(X);
		AfxMessageBox("PPPP");
	}

	if(wp==1){

		AsseY.SetTrackMode(X);
		OggettoPuntato.CalcStarPos();
		AsseY.SetMotVel(X,m_telescopeInfo.DirY*OggettoPuntato.ObsVEL);
		AsseY.StartMove(X);

	}
		AfxMessageBox("PPPP");
*/
}


void CTcsDlg::OnTimer(UINT nIDEvent) 
{
	// TODO: Add your message handler code here and/or call default
	/*LONG Valo;
	double val;
	int err;*/
	char buf[80], buf1[80];
	double DPX=0;
	double DPY=0;
	long val=0;
	if(nIDEvent==1){
	SetDlgItemText(IDC_UTDATE,TelTime.GetUTDateStr());
	SetDlgItemText(IDC_UTTIME,TelTime.GetUTStr());
	SetDlgItemText(IDC_ORAL,TelTime.CivilTimeStr());
	SetDlgItemText(IDC_LST,TelTime.GetLSATStr());
	SetDlgItemText(IDC_JD,TelTime.GetJDStr());
	}

	if(nIDEvent==2){
	OggettoPuntato.CalcStarPos();
	GetTelInfo();
	
	///Modifica 13/19/2000
	if(Telescopio.m_TelMonTipo==0  && noCentrato==0){
		DPX=(m_telescopeInfo.AZ-OggettoPuntato.ObsAZ)*3600;
		DPY=(m_telescopeInfo.EL-OggettoPuntato.ObsEL)*3600;
	}
	if(Telescopio.m_TelMonTipo==1){
		DPX=(m_telescopeInfo.H-OggettoPuntato.ObsH)*54000.;
		DPY=(m_telescopeInfo.DEC-OggettoPuntato.ObsDEC)*3600;
	}
	///
	if(SetPointX==0){
		if(SetTrackX==1){//1	
		
		if(Telescopio.m_TelMonTipo==0){//2
			//DPX=(m_telescopeInfo.AZ-OggettoPuntato.ObsAZ)*3600;
			if(fabs(DPX)>1.0 && (Joy==0) && (SetTrackY==1)){//3
				//if(fabs(DPX)<=m_telescopeInfo.MaxVelX/4.)
		//Mettere controllo sulla velocita' massima
				if(DPX>0. ){
					AsseX.SetMotVel(X, (1.*OggettoPuntato.ObsVAZ/CostX[2]));
						sprintf(buf,"V%6.1lf",1.*OggettoPuntato.ObsVAZ/CostX[2]);
					SetDlgItemText(IDC_MVHA, buf);
				}
				if(DPX<0. )
					AsseX.SetMotVel(X, -1.*OggettoPuntato.ObsVAZ*CostY[2]);
			}//3
			else
				AsseX.SetMotVel(X,m_telescopeInfo.DirX*OggettoPuntato.ObsVAZ);
		}//2
		else{//4
			//DPX=(m_telescopeInfo.H-OggettoPuntato.ObsH)*54000.;
			if(fabs(DPX)>=0.5 && (Joy==0)){
				if(m_telescopeInfo.H>12.)m_telescopeInfo.H-=24.;
				if(OggettoPuntato.ObsH>12.)OggettoPuntato.ObsH-=24.;
				//DPX=(m_telescopeInfo.H-OggettoPuntato.ObsH)*54000.;
				if(DPX>0. )
					AsseX.SetMotVel(X, 0.9*OggettoPuntato.ObsVH);
				if(DPX<0. )
					AsseX.SetMotVel(X, 1.1*OggettoPuntato.ObsVH);

			}
			else 
				AsseX.SetMotVel(X,m_telescopeInfo.DirX*OggettoPuntato.ObsVH);
		}//4
	}//1
	}
	else{
		if((SetTrackY==1) ){
		if(fabs(DPX)>=1. ){
			if(DPX>0. ){
					AsseX.SetMotVel(X, (OggettoPuntato.ObsVAZ+DPX/1.5));
						sprintf(buf,"X%6.1lf",-1*OggettoPuntato.ObsVAZ+DPX/1.5);
					SetDlgItemText(IDC_MVHA, buf);

			}
			if(DPX<0. );
					AsseX.SetMotVel(X, -1*(OggettoPuntato.ObsVAZ+fabs(DPX)/3));
		}
		else{
			AsseX.SetMotVel(X, -1*OggettoPuntato.ObsVAZ);
			SetPointX=0;
		}

		}
	}

	if(SetPointY==0){
	if(SetTrackY==1)
	{	
		if(Telescopio.m_TelMonTipo==0){
			//DPY=(m_telescopeInfo.EL-OggettoPuntato.ObsEL)*3600;
			if(fabs(DPY)>=0.5 && (Joy==0) && (SetTrackX==1)){
				//if(fabs(DPY)<=m_telescopeInfo.MaxVelY/4.)
				if(DPY>0. ){
				if(OggettoPuntato.ObsVEL<0.)
				  AsseY.SetMotVel(X, m_telescopeInfo.DirY*OggettoPuntato.ObsVEL*1.1);
				else
				  AsseY.SetMotVel(X, m_telescopeInfo.DirY*OggettoPuntato.ObsVEL/1.1);
				}
				if(DPY<0. ){
					if(OggettoPuntato.ObsVEL<0.)
				 AsseY.SetMotVel(X, m_telescopeInfo.DirY*OggettoPuntato.ObsVEL/1.1);
					else
					AsseY.SetMotVel(X, m_telescopeInfo.DirY*OggettoPuntato.ObsVEL*1.1);
				}
				//AsseY.SetMotVel(X,-2.5*DPY);
			}
			else
				AsseY.SetMotVel(X,OggettoPuntato.ObsVEL);
		}
		else{
			if(fabs(DPY)>0.5 && (Joy==0)){
				//DPY=(m_telescopeInfo.DEC-OggettoPuntato.ObsDEC)*3600;
				AsseY.SetMotVel(X, -1.*DPY/1.25);
			}
			else 
				AsseY.SetMotVel(X,DPY); //DA VERIFICARE
		}
	}
	}
	else{
		if((SetTrackX==1)){
		if(fabs(DPY)>=0.5){
				//if(fabs(DPY)<=m_telescopeInfo.MaxVelY/4.)
				if(DPY>0. ){
					if(OggettoPuntato.ObsVEL<0.)
						AsseY.SetMotVel(X, m_telescopeInfo.DirY*OggettoPuntato.ObsVEL*1.15);
					else
						AsseY.SetMotVel(X, m_telescopeInfo.DirY*OggettoPuntato.ObsVEL/1.15);
					}
					if(DPY<0. ){
						if(OggettoPuntato.ObsVEL<0.)
							AsseY.SetMotVel(X, m_telescopeInfo.DirY*OggettoPuntato.ObsVEL/1.15);
						else
					AsseY.SetMotVel(X, m_telescopeInfo.DirY*OggettoPuntato.ObsVEL*1.15);
				}
				//AsseY.SetMotVel(X,-2.5*DPY);
			}
		else{
				AsseY.SetMotVel(X,OggettoPuntato.ObsVEL);
				SetPointY=0;
		}
		;
		}
	}

	
	SetDlgItemText(IDC_OAR, OggettoPuntato.GetRAStr(OggettoPuntato.ObsRA));
	SetDlgItemText(IDC_ODEC, OggettoPuntato.GetDECStr(OggettoPuntato.GetObsDEC()));
	SetDlgItemText(IDC_OHA,OggettoPuntato.GetRAStr(OggettoPuntato.GetObsHA()));
	SetDlgItemText(IDC_OAZ, OggettoPuntato.GetDECStr(OggettoPuntato.ObsAZ));
	SetDlgItemText(IDC_OAL,OggettoPuntato.GetRAStr(OggettoPuntato.ObsEL));
	SetDlgItemText(IDC_OPA, OggettoPuntato.GetDECStr(OggettoPuntato.ObsPA));
	sprintf(buf,"%10.1lf",OggettoPuntato.ObsVEL);
	SetDlgItemText(IDC_MVAL, buf);
	sprintf(buf,"%10.1lf",OggettoPuntato.ObsVAZ);
	SetDlgItemText(IDC_MVAZ, buf);
	if(SetTrackX==1 || SetPointX==1)
		sprintf(buf,"X%5.1lf",DPX);
	else
		sprintf(buf,"%10.1lf",OggettoPuntato.ObsVPA);
	SetDlgItemText(IDC_MVPA, buf);
	if(SetTrackY==1)
		sprintf(buf,"Y%5.1lf",DPY);
	else
	  sprintf(buf,"%10.1lf",OggettoPuntato.ObsVDEC);
	SetDlgItemText(IDC_MVDEC, buf);

	
	FormatCoord(buf, m_telescopeInfo.AZ,0);
	SetDlgItemText(IDC_MAZ,buf);
	FormatCoord(buf, m_telescopeInfo.EL,0);
	SetDlgItemText(IDC_MAL,buf);
	FormatCoord(buf, m_telescopeInfo.RA,0);
	SetDlgItemText(IDC_MAR,buf);
	FormatCoord(buf, m_telescopeInfo.H,0);
	SetDlgItemText(IDC_MHA,buf);
	FormatCoord(buf,m_telescopeInfo.DEC,0);
	SetDlgItemText(IDC_MDEC,buf);

	//err = AsseX.GetMotVel(X,&val);
	//AsseX.GetMotEncPos(X,&val);
	sprintf(buf,"%ld",(long)(m_telescopeInfo.PosX/0.0125));
	SetDlgItemText(IDC_M_E1AR,buf);
	//AsseY.GetMotEncPos(X,&val);
	sprintf(buf,"%ld",(long)(m_telescopeInfo.PosY/0.0125));
	SetDlgItemText(IDC_M_E1DEC,buf);
	sprintf(buf,"%7.1lf(\"/s)",m_telescopeInfo.SlewVelX);
	SetDlgItemText(IDC_M_VAR,buf);
	sprintf(buf,"%6.1lf(\"/s)",m_telescopeInfo.SlewVelY);
	SetDlgItemText(IDC_M_VDEC,buf);

		if(AsseX.GetMotorStatus(X))
				SetDlgItemText(IDC_M_MAR,"On");
		else
				SetDlgItemText(IDC_M_MAR,"Off");
		/*if(AsseX.IsMoving(X))
			SetDlgItemText(IDC_M_HSAR,"On");
		else
			SetDlgItemText(IDC_M_HSAR,"Off");*/

		if(AsseY.GetMotorStatus(X))
			 SetDlgItemText(IDC_M_MDEC,"On");
			else
				SetDlgItemText(IDC_M_MDEC,"Off");
		/*if(AsseY.IsMoving(X))
			SetDlgItemText(IDC_M_HSDEC,"On");
		else
			SetDlgItemText(IDC_M_HSDEC,"Off");*/
	}
//CUPOLA
	
	
	if(nIDEvent==3){
	GetCupolaInfo();
	sprintf(buf,"%5.2lf",m_CupolaInfo.AZ); 
	SetDlgItemText(IDC_CAZ,buf);
/*	if(SetTrackCup==1)
	{;}*/
	}

	//CDialog::OnTimer(nIDEvent);
}


//MARK: Traiettoria
void CTcsDlg ::TraiettoriaX()
{

	double Vmax,Amax,Tnew=0,Tmin=0.1;
	double Told=0,Vm,Dp;
	double Pi,Pf,Vi,Vf,Vs;
	double d,h,az,el,vaz,vel;
	//char buf[100];
	double P0;
	//Telescopio
	GetTelInfoX();
	if(Telescopio.m_TelMonTipo==0)
		P0=m_telescopeInfo.AZ*3600;
	else 
		P0=m_telescopeInfo.H*54000;
	Vi=0.;//m_telescopeInfo.TrackVelX;
	Amax=m_telescopeInfo.MaxAccX;
	Vmax=m_telescopeInfo.MaxVelX;
	//sprintf(buf,"Vm=%lf Pos=%lf Time=%lf\n",Pi,Vi,Amax);
	//AfxMessageBox(buf);
	//Oggetto
	OggettoPuntato.CalcStarPos();
	if(Telescopio.m_TelMonTipo==0){
		Pf=OggettoPuntato.ObsAZ*3600;
		Vs=OggettoPuntato.ObsVAZ;
		Vf=Vs;
		//sprintf(buf,"Vm=%lf Pos=%lf ",Pf,Vf);
	//AfxMessageBox(buf);
	}
	else{
		Pf=OggettoPuntato.ObsH;
		if(Pf>12.)Pf=Pf-24;
		Pf=Pf*15.*3600;
		Vf=OggettoPuntato.ObsVH;
	}

	do{
	Told=Tnew;
	double DP=Vs*Told;  //ricalcolo le posizioni finali al tempo Told
	//Dp=Pf-Pi+DP;
	Dp=Pf-P0+DP;
	double Dir=Dp/fabs(Dp);
	double A=Dir*Amax;
	Vm=sqrt(A*Dp +2.*(Vi*Vi+Vf*Vf));
		//printf("Vm=%lf\n",Vm);
	if(Vm>Vmax)Vm=Vmax;
	Vm=Dir*Vm;
	double T1=(Vm-Vi)/A;
	double S1=Vi*T1+A*T1*T1/2;
	double T3=(Vm-Vf)/A;
	double S3=Vm*T3-A*T3*T3/2;
	double S2=Dp-S1-S3;
	double T2=S2/Vm;
	if(T1<=0. || T2<=0 || T3<=0.)
		break; //Puntare senza rampe;
	Tnew=T1+T2+T3;
	//printf("Tnew=%lf\n",Tnew);
	/*OggettoPuntato.CalcStarPos(Tnew,&d,&h,&az,&el,&vaz,&vel);
	if(Telescopio.m_TelMonTipo==0){
		Pf=az*3600;
		//Vf=vaz;
	}
	else
		Pf=h*54000.;*/
	//sprintf(buf,"Dp=%lf Pos=%lf Time=%lf\n",Pf/3600, P0/3600,Tnew);
	//AfxMessageBox(buf);
	}while((Tnew>Tmin) && (fabs(Tnew-Told)>Tmin));
	//sprintf(buf,"Vm=%lf Pos=%lf Time=%lf\n",Vm, 180*3600-(Pi+Dp),Tnew);
	//AfxMessageBox(buf);
	m_telescopeInfo.SlewTimeX=Tnew;
	m_telescopeInfo.SlewVelX=fabs(Vm);
	if(Telescopio.m_TelMonTipo==0){
		CorreggiAZ(OggettoPuntato.ObsAZ,OggettoPuntato.ObsEL);
		m_telescopeInfo.TargetPosX=(180*3600-(P0+Dp))+CostX[0];
		//m_telescopeInfo.TargetPosX=(180*3600-Pf);
	}else{
		if((Pf/(54000.))>12.)Pf=(Pf/3600 -360.);
		//m_telescopeInfo.TargetPosX=Pf*3600.;
		m_telescopeInfo.TargetPosX=(P0+Dp)*3600.;
	}

	//m_telescopeInfo.Target;
	//m_telescopeInfo.TargetPosZ;

}

void CTcsDlg ::TraiettoriaY()
{

	double Vmax,Amax,Tnew=0,Tmin=0.1;
	double Told,Vm,Dp;
	double Pi,Pf,Vi,Vf;
	//char buf[100];
	
 //Telescopio
	GetTelInfoY();
	Pi=m_telescopeInfo.PosY;
	Vi=0.;//m_telescopeInfo.TrackVelY;
	Amax=m_telescopeInfo.MaxAccY;
	Vmax=m_telescopeInfo.MaxVelY;
	//sprintf(buf,"Vm=%lf Pos=%lf Time=%lf\n",Pi,Vi,Amax);
	//AfxMessageBox(buf);
 //Oggetto
	OggettoPuntato.CalcStarPos();
	if(Telescopio.m_TelMonTipo==0){
		Pf=OggettoPuntato.ObsEL*3600;
		Vf=OggettoPuntato.ObsVEL;
		//Vf=0.0;
	//	sprintf(buf,"Vm=%lf Pos=%lf ",Pf,Vf);
	//AfxMessageBox(buf);
	}
	else{
		Pf=OggettoPuntato.ObsDEC;
		Pf=Pf*3600;
		Vf=OggettoPuntato.ObsVDEC;
	}

	do{
	Told=Tnew;
	double DP=Vf*Told;  //ricalcolo le posizioni finali al tempo Told
	Dp=Pf-Pi+DP;
	double Dir=Dp/fabs(Dp);
	double A=Dir*Amax;
	Vm=sqrt(A*Dp +2.*(Vi*Vi+Vf*Vf));
		//printf("Vm=%lf\n",Vm);
	if(Vm>Vmax)Vm=Vmax;
	Vm=Dir*Vm;
	double T1=(Vm-Vi)/A;
	double S1=Vi*T1+A*T1*T1/2;
	double T3=(Vm-Vf)/A;
	double S3=Vm*T3-A*T3*T3/2;
	double S2=Dp-S1-S3;
	double T2=S2/Vm;
	if(T1<=0. || T2<0 || T3<=0.)
		break; //Puntare senza rampe;
	Tnew=T1+T2+T3;
	//printf("Tnew=%lf\n",Tnew);
	}while((Tnew>Tmin) && (fabs(Tnew-Told)>Tmin));
	//sprintf(buf,"Vm=%lf Pos=%lf Time=%lf\n",Vm,(Pi+Dp),Tnew);
	//AfxMessageBox(buf);
	m_telescopeInfo.SlewTimeY=Tnew;
	m_telescopeInfo.SlewVelY=fabs(Vm);
	CorreggiEL(OggettoPuntato.ObsAZ,OggettoPuntato.ObsEL);
	m_telescopeInfo.TargetPosY=((Pi+Dp))+CostY[0];

	//m_telescopeInfo.Target;
	//m_telescopeInfo.TargetPosZ;

}

void CTcsDlg ::GetTelInfo()
{
	double ra;
	if(Telescopio.m_TelMonTipo==0){

	    GetTelInfoX();
		GetTelInfoY();
		AzEl2HaDec ( m_telescopeInfo.AZ, m_telescopeInfo.EL, m_Osservatorio.Latitudine, &m_telescopeInfo.H, &m_telescopeInfo.DEC);
		ra=TelTime.GetLSATHour()-m_telescopeInfo.H;
		if(ra<0.)ra+=24.;
		if(ra>24)ra-=24.;
		m_telescopeInfo.RA=ra;
		//GetTelInfoZ();

	}
	else{
	GetTelInfoX();
	GetTelInfoY();
	ra=TelTime.GetLSATHour()-m_telescopeInfo.H;
	if(ra<0.)ra+=24.;
	if(ra>24)ra-=24.;
	m_telescopeInfo.RA=ra;
	HaDec2AzEl(m_telescopeInfo.H, m_telescopeInfo.DEC, m_Osservatorio.Latitudine, &m_telescopeInfo.AZ, &m_telescopeInfo.EL );
	}
}


void CTcsDlg ::GetTelInfoX()
{
	double PosX, val;
	long valo;
	int err;
	//char buf[100];
	if(AsseX.CommStatus){
		if(Telescopio.m_TelMonTipo==0){
			err = AsseX.GetMotEncPos(X,&valo);
			PosX=valo/AsseX.CONVFACTOR[X]-CostX[0];
			m_telescopeInfo.PosX=PosX;
			PosX=(180*3600-PosX);
			m_telescopeInfo.AZ=PosX/3600.;
			
		}
		else{
			err = AsseX.GetMotEncPos(X,&valo);
			PosX=valo/AsseX.CONVFACTOR[X];
			m_telescopeInfo.PosX=PosX;
			m_telescopeInfo.H=m_telescopeInfo.PosX/(54000.);
			if(m_telescopeInfo.H<0.)m_telescopeInfo.H+=24;
		}
		err = AsseX.GetMotVel(X,&val);
		m_telescopeInfo.SlewVelX=val;

		err = AsseX.GetMotAcc(X,&val);
		m_telescopeInfo.AccX=val;
	}

}


void CTcsDlg ::GetTelInfoY()
{
	double PosY, val;
	long valo;
	int err;
	    if(AsseY.CommStatus){
		err = AsseY.GetMotEncPos(X,&valo);
		PosY=valo/AsseY.CONVFACTOR[X]-CostY[0];
		m_telescopeInfo.PosY=PosY;	
		if(Telescopio.m_TelMonTipo==0){
			m_telescopeInfo.EL=PosY/3600.;
		}
		else{
			m_telescopeInfo.DEC=PosY/3600.;
		}

		err = AsseY.GetMotVel(X,&val);
		m_telescopeInfo.SlewVelY=val;
		err = AsseY.GetMotAcc(X,&val);
		m_telescopeInfo.AccY=val;
		}
}

void CTcsDlg::GetTelInfoZ()
{
	double PosZ, val;
	long valo;
	int err;

	    if(AsseZ.CommStatus){
		err = AsseZ.GetMotEncPos(X,&valo);
		PosZ=valo/AsseZ.CONVFACTOR[X];
		m_telescopeInfo.PosZ=PosZ;
		m_telescopeInfo.PA=PosZ/3600.;
		err = AsseZ.GetMotVel(X,&val);
		m_telescopeInfo.SlewVelZ=val;

		
		err = AsseZ.GetMotAcc(X,&val);
		m_telescopeInfo.AccZ=val;
		}
}

void CTcsDlg ::GetCupolaInfo()
{
	//double PosZ, val;
	long valo;
	int err;

	if(AsseCupola.CommStatus){
			err = AsseCupola.GetMotEncPos(X,&valo);
			m_CupolaInfo.Pos=valo/AsseCupola.CONVFACTOR[X];
			m_CupolaInfo.AZ=m_CupolaInfo.Pos/3600.;
			if(m_CupolaInfo.AZ>=360.)m_CupolaInfo.AZ-=360.;
	}

}





void CTcsDlg ::SetTelTrackVel()
{
	OggettoPuntato.CalcStarPos();
	double sign;
	if(Telescopio.m_TelMonTipo==0){
//ASSEX
		sign=OggettoPuntato.ObsVAZ/fabs(OggettoPuntato.ObsVAZ);
		if(fabs(OggettoPuntato.ObsVAZ)<=m_telescopeInfo.MaxVelX)
			m_telescopeInfo.TrackVelX=sign*m_telescopeInfo.DirX*OggettoPuntato.ObsVAZ;
		else
			m_telescopeInfo.TrackVelX=sign*m_telescopeInfo.DirX*m_telescopeInfo.MaxVelX;
//ASSEY
		sign=OggettoPuntato.ObsVEL/fabs(OggettoPuntato.ObsVEL);
		if(fabs(OggettoPuntato.ObsVEL)<=m_telescopeInfo.MaxVelY)
			m_telescopeInfo.TrackVelY=sign*m_telescopeInfo.DirY*OggettoPuntato.ObsVEL;
		else
			m_telescopeInfo.TrackVelY=sign*m_telescopeInfo.DirY*m_telescopeInfo.MaxVelY;
//ASSEZ
		sign=OggettoPuntato.ObsVPA/fabs(OggettoPuntato.ObsVPA);
		if(fabs(OggettoPuntato.ObsVPA)<=m_telescopeInfo.MaxVelZ)
			m_telescopeInfo.TrackVelZ=sign*m_telescopeInfo.DirZ*OggettoPuntato.ObsVPA;
		else
			m_telescopeInfo.TrackVelZ=sign*m_telescopeInfo.DirZ*m_telescopeInfo.MaxVelZ;
	}
	else{
//ASSE HA
		sign=OggettoPuntato.ObsVH/fabs(OggettoPuntato.ObsVH);
		if(fabs(OggettoPuntato.ObsVH)<=m_telescopeInfo.MaxVelX)
			m_telescopeInfo.TrackVelX=m_telescopeInfo.DirX*OggettoPuntato.ObsVH;
		else
			m_telescopeInfo.TrackVelX=sign*m_telescopeInfo.DirX*m_telescopeInfo.MaxVelX;

//ASSE DEC
		sign=OggettoPuntato.ObsVDEC/fabs(OggettoPuntato.ObsVDEC);
		if(fabs(OggettoPuntato.ObsVDEC)<=m_telescopeInfo.MaxVelY)
			m_telescopeInfo.TrackVelY=m_telescopeInfo.DirY*OggettoPuntato.ObsVDEC;
		else
			m_telescopeInfo.TrackVelY=sign*m_telescopeInfo.DirY*m_telescopeInfo.MaxVelY;

	}

}


void AzEl2HaDec ( double az, double el, double phi, double *ha, double *dec )
{
   double sa, ca, se, ce, sp, cp, x, y, z, r;

/* Useful trig functions */
   az=az*DD2R;
   el=el*DD2R;
   phi=phi*DD2R;
   sa = sin ( az );
   ca = cos ( az );
   se = sin ( el );
   ce = cos ( el );
   sp = sin ( phi );
   cp = cos ( phi );

/* HA,Dec as x,y,z */
   x = - ca * ce * sp + se * cp;
   y = - sa * ce;
   z = ca * ce * cp + se * sp;

/* To spherical */
   r = sqrt ( x * x + y * y );
   *ha = ( r == 0.0 ) ? 0.0 : atan2 ( y, x ) ;
   *dec = atan2 ( z, r );
   *ha=*ha*DR2H;
   if(*ha<0)*ha=*ha+24.;
   *dec=*dec*DR2D;
}

void HaDec2AzEl( double ha, double dec, double phi, double *az, double *el )
{
   double sh, ch, sd, cd, sp, cp, x, y, z, r, a;
   ha=ha*DH2R;
   dec=dec*DD2R;
   phi=phi*DD2R;
/* Useful trig functions */
   sh = sin ( ha );
   ch = cos ( ha );
   sd = sin ( dec );
   cd = cos ( dec );
   sp = sin ( phi );
   cp = cos ( phi );

/* Az,El as x,y,z */
   x = - ch * cd * sp + sd * cp;
   y = - sh * cd;
   z = ch * cd * cp + sd * sp;

/* To spherical */
   r = sqrt ( x * x + y * y );
   a = ( r == 0.0 ) ? 0.0 : atan2 ( y, x ) ;
   *az = ( a < 0.0 ) ? a + D2PI : a;
   *az=*az*DR2D;
   *el = atan2 ( z, r );
   *el=*el*DR2D;
}


void CTcsDlg::OnTelescoFermaMoto() 
{
	// TODO: Add your command handler code here
	//OnTelescoStopInseguimento();

	SetPointY=0;SetPointX=0;
	if(SetTrackX==1)
		SetTrackX=0;
	if(SetTrackY==1)
		SetTrackY=0;
	if(AsseX.IsMoving(X))
		AsseX.StopMove(X);
	if(AsseY.IsMoving(X))
		AsseY.StopMove(X);
	UpdateTcsLog("MOUNT:Motor Motion Killed");
	
}

void CTcsDlg::OnTelescoStartMotoOrario() 
{
	// TODO: Add your command handler code here
	SetTelTrackVel();
	AsseX.StopMove(X);
	AsseY.StopMove(X);	
	AsseX.SetSlewMode(X);
	AsseY.SetSlewMode(X);
	AsseX.StartMove(X);
	AsseY.StartMove(X);
	SetTrackX=1;
	SetTrackY=1;
	SetPointY=0;SetPointX=0;
	noCentrato=0;
	//if(Telescopio.m_TelMonTipo==0)
	//	SetTrackZ=0;
	
}

void CTcsDlg::OnTelescoStopInseguimento() 
{
	// TODO: Add your command handler code here
	SetPointY=0;SetPointX=0;
	SetTrackX=0;
	SetTrackY=0;
	AsseX.StopMove(X);
	AsseY.StopMove(X);
	//if(Telescopio.m_TelMonTipo==0)
	//	SetTrackZ=0;
	
	
}

void CTcsDlg::OnTelescoInitAssi() 
{
	// TODO: Add your command handler code here
		OnSettaPosHome();
}


void CTcsDlg::OnTelescoInitAsseX() 
{
	// TODO: Add your command handler code here
	
}

void CTcsDlg::OnTelescoInitAsseY() 
{
	// TODO: Add your command handler code here
	
}

void CTcsDlg::OnTelescoInitAsseZ() 
{
	// TODO: Add your command handler code here
	
}




void CTcsDlg::OnTelescoParametri() 
{
	// TODO: Add your command handler code here
	CParametriTelescopio para;

	para.m_CRa0 = CostX[1];
	para.m_CRa1 = CostX[2];
	para.m_CRa2 = 0.0;
	para.m_CRa3 = 0.0;
	para.m_CRa4 = 0.0;
	para.m_CRa5 = 0.0;
	para.m_CDec0 = CostY[1];
	para.m_CDec1 = CostY[2];
	para.m_CDec2 = 0.0;
	para.m_CDec3 = 0.0;
	para.m_CDec4 = 0.0;
	para.m_CDec5 = 0.0;

	long Valo=0;
	if(AsseX.CommStatus){
				AsseX.Comando_Array("AVRE",9,&Valo);
		para.m_VelBassaRa=Valo/AsseX.CONVFACTOR[X];
				AsseX.Comando_Array("AVRE",11,&Valo);
		para.m_VelMediaRa=Valo/AsseX.CONVFACTOR[X];
				AsseX.Comando_Array("AVRE",12,&Valo);
		para.m_VelAltaRa=Valo/AsseX.CONVFACTOR[X];
	}
	if(AsseY.CommStatus){
				AsseY.Comando_Array("AVRE",9,&Valo);
		para.m_VelBassaDec=Valo/AsseY.CONVFACTOR[X];
				AsseY.Comando_Array("AVRE",11,&Valo);
		para.m_VelMediaDec=Valo/AsseY.CONVFACTOR[X];
				AsseY.Comando_Array("AVRE",12,&Valo);
		para.m_VelAltaDec=Valo/AsseY.CONVFACTOR[X];
	}

	if(para.DoModal()==IDOK){
		if(AsseX.CommStatus){
		CostX[1]=para.m_CRa0 ;
	    CostY[1]=para.m_CDec0;
		CostX[2]=para.m_CRa1 ;
	    CostY[2]=para.m_CDec1;
		Valo=(long)(para.m_VelBassaRa*AsseX.CONVFACTOR[X]+0.5);
		AsseX.Comando_Array("AVSE",9,&Valo);
		Valo=(long)(para.m_VelMediaRa*AsseX.CONVFACTOR[X]+0.5);
		AsseX.Comando_Array("AVSE",11,&Valo);
		Valo=(long)(para.m_VelAltaRa*AsseX.CONVFACTOR[X]+0.5);
		AsseX.Comando_Array("AVSE",12,&Valo);
		}
		if(AsseY.CommStatus){
		Valo=(long)(para.m_VelBassaDec*AsseY.CONVFACTOR[X]+0.5);
		AsseY.Comando_Array("AVSE",9,&Valo);
		Valo=(long)(para.m_VelMediaDec*AsseY.CONVFACTOR[X]+0.5);
		AsseY.Comando_Array("AVSE",11,&Valo);
		Valo=(long)(para.m_VelAltaDec*AsseY.CONVFACTOR[X]+0.5);
		AsseY.Comando_Array("AVSE",12,&Valo);
		}
		
	}
}

void CTcsDlg::OnTelescoVerificap() 
{
	SetTrackY=SetTrackX=0;
	SetTrackCup=0;
	//pis.DoModal();
	pis.m_iFlagUso=3;
		if(pis.DoModal()==IDOK){
			OnTelescopioCentra();		
	}
	pis.m_iFlagUso=0;

	/*GetTelInfo();
			OggettoPuntato.CalcStarPos();
			FILE *fp;
			fp=fopen("Errore.dat","at");
			fprintf(fp,"%lf %lf %lf %lf %lf",TelTime.GetLSATHour(),m_telescopeInfo.RA,m_telescopeInfo.DEC,OggettoPuntato.ObsRA,OggettoPuntato.DEC);
			fclose(fp);
			*/
}

void CTcsDlg::OnPuntamentoPianeti() 
{
	// TODO: Add your command handler code here
CMenu* pMenu = GetMenu();
		ASSERT(pMenu != NULL);

	pMenu->EnableMenuItem(IDM_PUNTAMENTO_CATALOGO, MF_DISABLED|MF_GRAYED);
	pMenu->EnableMenuItem(IDM_PUNTAMENTO_MINIMO, MF_DISABLED|MF_GRAYED);
	pMenu->EnableMenuItem(IDM_PUNTAMENTO_COORDINATE, MF_DISABLED|MF_GRAYED);
	//pMenu->EnableMenuItem(IDM_PUNTAMENTO_PIANETI,MF_DISABLED|MF_GRAYED);
	PantaSoledlg.Lat=m_Osservatorio.Latitudine;
	PantaSoledlg.Lon=m_Osservatorio.Longitudine;
	PantaSoledlg.Alt=m_Osservatorio.Altitudine;
	PantaSoledlg.DoModal();
}

void CTcsDlg::PuntaCupola()
{
	
	long int azi=3600*OggettoPuntato.ObsAZ*AsseCupola.CONVFACTOR[X];
	char * buff = new char[50];
	AsseCupola.Comando_Array("AVSE", 10, &azi);
	AsseCupola.ExecProg("PUNTA");
	sprintf(buff,"DOME: slewing ->%.1lf",OggettoPuntato.ObsAZ);
	UpdateTcsLog(buff);
	delete[] buff;

}

void CTcsDlg::PuntaCupola(double ang)
{
	
	long int azi=3600*ang*AsseCupola.CONVFACTOR[X];
	char * buff = new char[50];
	AsseCupola.Comando_Array("AVSE", 10, &azi);
	AsseCupola.ExecProg("PUNTA");
	sprintf(buff,"DOME: slewing ->%.1lf",ang);
	UpdateTcsLog(buff);
	delete[] buff;

}

void CTcsDlg::OnExecuteRemote(LPARAM lp,WPARAM wp)
{
	CString Comando;
	g_done=FALSE;
	CMyString MyCommand(80);
	MyCommand.SetSep(" ,");
	MyCommand.SetStr(ExternalCommand);
	MyCommand.ScanStr();
	if(MyCommand.ntok>0){
	Comando.Format("%s",MyCommand.token[0]);
	Comando.MakeUpper();
	if(!Comando.Compare("DOMEOPEN")){
		OnCupolaApri();
		Sleep(15000);
		g_done=TRUE;
	}
	else if(!Comando.Compare("DOMECLOSE")){
		OnCupolaChiudi();
		Sleep(15000);
		g_done=TRUE;
		}
	else if(!Comando.Compare("DOMEINIT")){
		OnCupolaSettazero();
		//Metter controllo sulla fine del moto"
		//Sleep(15000);
		g_done=TRUE;
		}
	else if(!Comando.Compare("DOMEGO")){
			if(MyCommand.ntok==2){
			//AfxMessageBox(MyCommand.token[1]);
		    PuntaCupola(atof(MyCommand.token[1]));
			//Metter controllo sulla fine del moto"
		//Sleep(15000);
			g_done=TRUE;
			}
		}

	else
		g_done=TRUE;
	}
	
}

void CTcsDlg::OnSetGPStime() 
{
	// TODO: Add your command handler code here
	GPS GP;
	GP.Open();
	if(!GP.Error){
		GP.GetGPSQuality();
		if(!GP.Error){
			//AfxMessageBox("Connessione OK");
			//Sleep(300);
			if(GP.SetPCClock()){
				//if (GP.ReadTime()){
				   // AfxMessageBox(GP.m_sGPSTime);
				SetDlgItemText(IDC_MESSAGGI,mesg[1]);
				UpdateTcsLog("GPS:PC time set to GPS time");
				//}
				
			}
		else
			AfxMessageBox("Errore lettura GPS Time");
				
		}
		else
		AfxMessageBox(GP.GetErrorMsg());
	}
	else
		AfxMessageBox(GP.GetErrorMsg());
	GP.Close();
}

void CTcsDlg::OnGetGPStime() 
{
	// TODO: Add your command handler code here
	GPS GP;
	GP.Open();
	if(!GP.Error){
		GP.GetGPSQuality();
		if(!GP.Error){
			//AfxMessageBox("Connessione OK");
			//Sleep(300);
			while (!GP.ReadTime());
			 //GP.ReadTime();
				AfxMessageBox(GP.m_sGPSTime);
			
			//else
				//AfxMessageBox("Errore lettura GPS Time");
		}
		else
		AfxMessageBox(GP.GetErrorMsg());
	}
	else
		AfxMessageBox(GP.GetErrorMsg());
	GP.Close();
}

void CTcsDlg::OnApriSpecchio() 
{
	// TODO: Add your command handler code here
	if(AsseCupola.CommStatus==1 && MirrorStatus==0){

			AsseCupola.ExecProg("APRIMIR");
			UpdateTcsLog("Mirror Cover Opened");
			
		}
}

void CTcsDlg::OnChiudiSpecchio() 
{
	// TODO: Add your command handler code here
	if(AsseCupola.CommStatus==1 && MirrorStatus==1){

			AsseCupola.ExecProg("CHIUDMIR");
			MirrorStatus=0;
			UpdateTcsLog("Mirror Cover Closed");
			
		}
}
void CTcsDlg::OnMostraDatiMeteo() 
{
	// TODO: Add your command handler code here
	/*
	m_Data.m_met_date;
	m_Data.m_met_hour ;
	m_Data.m_met_tempint;
	m_Data.m_met_humint;
	m_Data.m_met_tempext;
	m_Data.m_met_humext;
	m_Data.m_met_presext;
	m_Data.m_met_windspe;
	m_Data.m_met_winddir;
	m_Data.m_met_rain;
  */

	MyMeteo m_Data;
	//BeginWaitCursor();
	
	m_Data.Open();
	//m_Data->MoveLast();
	if (m_Data.IsOpen()){
		m_Data.MoveNext();
	SetDlgItemText(IDC_METH,m_Data.m_met_humext);
	SetDlgItemText(IDC_METPRE,m_Data.m_met_presext);
	SetDlgItemText(IDC_METT,m_Data.m_met_tempext);
	SetDlgItemText(IDC_METWS,m_Data.m_met_windspe);
	SetDlgItemText(IDC_METWD,m_Data.m_met_winddir);
	//AfxMessageBox(m_Data.m_met_hour);
		m_Data.Close();
	me.m_dP=atof(m_Data.m_met_presext);
	me.m_dT=atof(m_Data.m_met_tempext);
	me.m_dH=atof(m_Data.m_met_humext)/100.;
	OggettoPuntato.SetRefraPar(me.m_dP,me.m_dT,me.m_dH);}
	//EndWaitCursor();
}

void CTcsDlg::OnExternalObj() 
{
	// TODO: Add your command handler code here
	CProvaMySQLSet m_Data;
	BeginWaitCursor();
	
	m_Data.Open();
	//m_Data->MoveLast();
	if (m_Data.IsOpen()){
		//m_Data.MoveLast();
		AfxMessageBox(m_Data.m_obj_name);
		m_Data.Close();}
	EndWaitCursor();
}

void CTcsDlg::OnConnessioneRemota() 
{
	// TODO: Add your command handler code here
	TcsServer.StartServerSocket(2000);
	REMOTE=true;
	if (AfxMessageBox("per terminare sessione remota\r\n Premere OK")==IDOK){
		REMOTE=false;
		TcsServer.StopServerSocket();
	}
}

int CTcsDlg::StopConnessioneRemota()
{
	TcsServer.StopServerSocket();
	return 0;
}

int CTcsDlg::UpdateTcsLog(const char *buff)
{
	ofstream out(m_LogFileName,ios::app);
	if(out.bad()) return 1;
	out<<TelTime.GetUTStr()<<"-->\t"<<buff<<endl;
	out.flush();
	out.close();
	Sleep(10);
	return 0;
}

int CTcsDlg::ReadCostPun()
{
	ifstream cost("costpunt.dat");
		CostX[2]=1.1;
		CostY[2]=1.05;
		if(!cost.bad()){
		cost>>CostX[1]>>CostY[1];
		cost.close();
		return 0;
		}
		else
			return 1;
}

void CTcsDlg::OnSettaZezoTelFile()
{
	long valx,valy,valc;
	ifstream in("lastpos.dat");
	in>>valx>>valy>>valc;
	in.close();
	AsseX.Comando_Set("SXZP",valx);
	Sleep(100);
	AsseY.Comando_Set("SXZP",valy);
	AsseCupola.Comando_Set("SXZP",valc);
}

void CTcsDlg::CorreggiAZ(double az, double el)
{
	double azr=az*DD2R;
	double elr=el*DD2R;
	double coel=cos(elr);
	double daz=CostX[1];//+874.98 - 892.4*coel -256.*sin(elr) -24.*coel*sin(2*azr) 
		//+ 36.*coel*sin(3.*azr);
	CostX[0]=daz;
}

void CTcsDlg::CorreggiEL(double az, double el)
{
	double elr=el*DD2R;
	double azr=az*DD2R;
	double coel=cos(elr);
	double del=CostY[1];//-51.86 +100.7*coel -46.23*sin(elr) +40*cos(azr)
		//-15.*sin(azr) +7.*cos(2.*azr) +6.*sin(2.*elr);
	CostY[0]=del;

}