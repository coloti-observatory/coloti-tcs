package opcua.newexample;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

public class ServerUtils {
    public static Map<String, NodeId> javaToOpcUa;
    static {
        javaToOpcUa = new HashMap<>();
        javaToOpcUa.put("BOOLEAN", Identifiers.Boolean);
        javaToOpcUa.put("BYTE", Identifiers.Byte);
        javaToOpcUa.put("INT16", Identifiers.Int16);
        javaToOpcUa.put("SHORT", Identifiers.Int16);
        javaToOpcUa.put("USHORT", Identifiers.UInt16);
        javaToOpcUa.put("INT", Identifiers.Int32);
        javaToOpcUa.put("INT32", Identifiers.Int32);
        javaToOpcUa.put("INTEGER", Identifiers.Int32);
        javaToOpcUa.put("UINT", Identifiers.UInt32);
        javaToOpcUa.put("UINT32", Identifiers.UInt32);
        javaToOpcUa.put("INT64", Identifiers.Int64);
        javaToOpcUa.put("LONG", Identifiers.Int64);
        javaToOpcUa.put("LONG", Identifiers.UInt64);
        javaToOpcUa.put("FLOAT", Identifiers.Float);
        javaToOpcUa.put("DOUBLE", Identifiers.Double);
        javaToOpcUa.put("STRING", Identifiers.String);
    }
}
