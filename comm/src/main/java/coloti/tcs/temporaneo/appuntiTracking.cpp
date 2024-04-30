
DPX=(m_telescopeInfo.AZ-OggettoPuntato.ObsAZ)*3600;
DPY=(m_telescopeInfo.EL-OggettoPuntato.ObsEL)*3600;


if(SetPointX==0){
    if(SetTrackX==1){
            if(fabs(DPX)>1.0 && (SetTrackY==1)){
                if(DPX>0. ){
                    AsseX.SetMotVel(X, (1.*OggettoPuntato.ObsVAZ/CostX[2]));
                }
                if(DPX<0. )
                    AsseX.SetMotVel(X, -1.*OggettoPuntato.ObsVAZ*CostY[2]);
            }
            else
                AsseX.SetMotVel(X,m_telescopeInfo.DirX*OggettoPuntato.ObsVAZ);
    }
}
else{
    if((SetTrackY==1)){
        if(fabs(DPX)>=1. ){
            if(DPX>0. ){
                AsseX.SetMotVel(X, (OggettoPuntato.ObsVAZ+DPX/1.5));
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
    if(SetTrackY==1){	
        if(fabs(DPY)>=0.5 && (SetTrackX==1)){
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
        }
        else
            AsseY.SetMotVel(X,OggettoPuntato.ObsVEL);
    }
}
else{
    if((SetTrackX==1)){
        if(fabs(DPY)>=0.5){
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
        }
        else{
                AsseY.SetMotVel(X,OggettoPuntato.ObsVEL);
                SetPointY=0;
        }
    }
}