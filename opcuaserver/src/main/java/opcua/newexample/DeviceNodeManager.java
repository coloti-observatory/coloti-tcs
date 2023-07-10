package opcua.newexample;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.eclipse.milo.opcua.sdk.core.Reference;
import org.eclipse.milo.opcua.sdk.server.nodes.UaFolderNode;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

import astri.aiv.AstrimaIcdDataPoint;
import ch.qos.logback.core.joran.conditional.ElseAction;
import coloti.tcs.TCS;

public class DeviceNodeManager {

    private ServerNamespace namespace;
    private String icdFile;
    private IcdManager tm;
    private UaFolderNode deviceRootFolder;
    private String deviceName;
    private Map<String, ServerItem> siMap = new HashMap<>();
    private TCS tcs;

    public DeviceNodeManager(ServerNamespace ns, String icdfile) {

        this.namespace = ns;
        this.icdFile = icdfile;
        loadIcd();
        createDeviceRootFolderNode();
        tcs = new TCS(true);
    }

    private void loadIcd() {
        tm = new IcdManager(icdFile);
        if (tm.isLoaded()) {
            // System.out.println(tm.get("AZ_MOTOR_BRAKE_STATUS"));
            this.deviceName = tm.getIcd().getDeviceName();
        }
        // tm.close();
    }

    private void createDeviceRootFolderNode() {

        this.deviceRootFolder = getNewFolder(this.deviceName);
        // Make sure our new folder shows up under the server's Objects folder.
        this.deviceRootFolder.addReference(new Reference(
                this.deviceRootFolder.getNodeId(),
                Identifiers.Organizes,
                Identifiers.ObjectsFolder.expanded(),
                false));
    }

    private ServerItem createServerItem(String itemId, String datatype, Consumer<DataValue> consumer,
            UaFolderNode folder) {
        ServerItem item = new ServerItem(itemId, datatype, namespace.getNodeCxt(), namespace.getNamespaceIdx(), folder);
        item.addWriteListener(consumer);
        // item.updateValue(Integer.valueOf(100));
        namespace.getNodeMan().addNode(item.getNode());
        folder.addOrganizes(item.getNode());
        return item;
    }

    private UaFolderNode getNewFolder(String name) {
        UaFolderNode folder = new UaFolderNode(
                namespace.getNodeCxt(),
                namespace.getNewNodeId(name),
                namespace.getQualifiedName(name),
                LocalizedText.english(name));

        namespace.getNodeMan().addNode(folder);
        return folder;
    }





    public void createNode(String name, UaFolderNode folder) {
        /*Consumer<DataValue> consumer = new Consumer<DataValue>() {

            @Override
            public void accept(DataValue t) {
                tcs.SetAzMaxVel((double)t.getValue().getValue());
                System.out.println("READ:" + t.toString());
            }

        };*/
        AstrimaIcdDataPoint dp = getIcdItem(name);
        System.out.println(dp.getActionCommand());
        System.out.println(dp.getOpcUaDataType());
        ServerItem item = createServerItem(dp,folder);
        siMap.put(name, item);
    }






    private ServerItem createServerItem(AstrimaIcdDataPoint dp, UaFolderNode folder) {
        ServerItem item = new ServerItem(dp.getShortName(), dp.getOpcUaDataType(), namespace.getNodeCxt(), namespace.getNamespaceIdx(), folder);
        item.addWriteListener(ConsumerLibrary.consumerMap.get(dp.getShortName()));
        String dataType = dp.getOpcUaDataType().toUpperCase();
        if(dataType.equals("DOUBLE"))
            item.updateValue(Double.parseDouble(dp.getDefaultValue()));
        else if(dataType.equals("INT32"))
            item.updateValue(Integer.parseInt(dp.getDefaultValue()));
        else if(dataType.equals("BOOLEAN"))
            item.updateValue(Boolean.parseBoolean(dp.getDefaultValue()));
        else if(dataType.equals("STRING"))
            item.updateValue(dp.getDefaultValue());
        else
            System.out.println("ERROR: UNSUPPORTED TYPE");
        namespace.getNodeMan().addNode(item.getNode());
        folder.addOrganizes(item.getNode());
        return item;
    }

    public UaFolderNode createFolderNode(String name) {
        UaFolderNode folder = getNewFolder(name);
        this.deviceRootFolder.addOrganizes(folder);
        return folder;
    }

    public UaFolderNode getDeviceRootFolder() {
        return deviceRootFolder;
    }

    public ServerNamespace getNamespace() {
        return namespace;
    }

    public IcdManager getTm() {
        return tm;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public AstrimaIcdDataPoint getIcdItem(String shortName) {
        if (tm.isLoaded()) {
            return tm.get(shortName);
        } else
            return null;

    }

    public void close() {
        tm.close();
    }
    
    public static void main(String[] args) {
        DeviceNodeManager dnm = new DeviceNodeManager(null,
                "/home/coloti/coloti-tcs/opcuaserver/MCS_ICD_13_03_23.xlsx");
        AstrimaIcdDataPoint dp = dnm.getIcdItem("AZ_MOTOR_BRAKE_STATUS");
        System.out.println(dp.getActionCommand());
        Map<String,AstrimaIcdDataPoint> map = dnm.getTm().getIcd().getCommands(); //getCommands()  getSetPoints() getMonitoringPoints();
        // classic way, loop a Map
        //for (Map.Entry<String, String> entry : map.entrySet()) {
          //  System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());}
		
	//Java 8 only, forEach and Lambda
	    map.forEach((k,v)->System.out.println("Key : " + k + " Value : " + v.printToString()+"\n\n"));

        dnm.close();
        
    }
}
