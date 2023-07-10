package opcua.newexample;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import astri.aiv.AstrimaIcd;
import astri.aiv.AstrimaIcdDataPoint;

public class IcdManager {

    private AstrimaIcd icd;
    private String astriIcdFile;

    private boolean isLoaded = false;
    private Map<String, AstrimaIcdDataPoint> icdPoints = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(IcdManager.class);

    public IcdManager(Properties prop) {
        this.astriIcdFile = prop.getProperty("assembly.icdfilename");
        loadAstriIcdFile();
    }

    public IcdManager(String file) {
        this.astriIcdFile = file;
        loadAstriIcdFile();
    }

    private void loadAstriIcdFile() {

        icd = new AstrimaIcd(astriIcdFile);
        
        icdPoints.putAll(icd.getMonitoringPoints());
        icdPoints.putAll(icd.getSetPoints());
        icdPoints.putAll(icd.getCommands());
        icdPoints.putAll(icd.getModes());
        isLoaded = true;

        logger.info("Loaded Icd for Assembly:{}", icd.getDeviceName());
    }

    public String getOpcUaAddress(String assembly){
        if(isLoaded){
            icd.setAssembly(assembly);
            return icd.getOpcUaServerAddress();
        }
        return null;
    }

    public AstrimaIcdDataPoint get(String v){
        return icdPoints.get(v);
    }

    public void close() {
        icd.close();
    }

    public AstrimaIcd getIcd() {
        return icd;
    }

    public String getAstriIcdFile() {
        return astriIcdFile;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public static void main(String[] args) throws Exception {

        IcdManager tm = new IcdManager("MCS_ICD_13_03_23.xlsx");
        if (tm.isLoaded()) {
            System.out.println(tm.get("AZ_MOTOR_BRAKE_STATUS"));
        }
        tm.close();
    }
}
