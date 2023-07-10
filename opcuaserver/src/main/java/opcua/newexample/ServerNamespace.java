package opcua.newexample;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;

import org.eclipse.milo.opcua.sdk.client.nodes.UaObjectNode;
import org.eclipse.milo.opcua.sdk.core.AccessLevel;
import org.eclipse.milo.opcua.sdk.core.Reference;
import org.eclipse.milo.opcua.sdk.core.ValueRank;
import org.eclipse.milo.opcua.sdk.server.Lifecycle;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.UaNodeManager;
import org.eclipse.milo.opcua.sdk.server.api.DataItem;
import org.eclipse.milo.opcua.sdk.server.api.ManagedNamespaceWithLifecycle;
import org.eclipse.milo.opcua.sdk.server.api.MonitoredItem;

import org.eclipse.milo.opcua.sdk.server.model.nodes.objects.BaseEventTypeNode;
import org.eclipse.milo.opcua.sdk.server.model.nodes.objects.ServerTypeNode;
import org.eclipse.milo.opcua.sdk.server.model.nodes.variables.AnalogItemTypeNode;
import org.eclipse.milo.opcua.sdk.server.nodes.AttributeObserver;
import org.eclipse.milo.opcua.sdk.server.nodes.UaFolderNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaNodeContext;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableNode;
import org.eclipse.milo.opcua.sdk.server.nodes.factories.NodeFactory;
import org.eclipse.milo.opcua.sdk.server.nodes.filters.AttributeFilters;
import org.eclipse.milo.opcua.sdk.server.util.SubscriptionModel;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.ByteString;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.XmlElement;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;
import org.eclipse.milo.opcua.stack.core.types.structured.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import astri.aiv.AstrimaIcdDataPoint;
import opcua.example.AttributeLoggingFilter;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ubyte;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ulong;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ushort;

public class ServerNamespace extends ManagedNamespaceWithLifecycle {

    public static final String NAMESPACE_URI = "urn:eclipse:milo:GinoTest";

    private static final Object[][] STATIC_SCALAR_NODES = new Object[][] {
            { "Boolean", Identifiers.Boolean, new Variant(false) },
            { "Byte", Identifiers.Byte, new Variant(ubyte(0x00)) },
            { "SByte", Identifiers.SByte, new Variant((byte) 0x00) },
            { "Integer", Identifiers.Integer, new Variant(32) },
            { "Int16", Identifiers.Int16, new Variant((short) 16) },
            { "Int32", Identifiers.Int32, new Variant(32) },
            { "Int64", Identifiers.Int64, new Variant(64L) },
            { "UInteger", Identifiers.UInteger, new Variant(uint(32)) },
            { "UInt16", Identifiers.UInt16, new Variant(ushort(16)) },
            { "UInt32", Identifiers.UInt32, new Variant(uint(32)) },
            { "UInt64", Identifiers.UInt64, new Variant(ulong(64L)) },
            { "Float", Identifiers.Float, new Variant(3.14f) },
            { "Double", Identifiers.Double, new Variant(3.14d) },
            { "String", Identifiers.String, new Variant("string value") },
            { "DateTime", Identifiers.DateTime, new Variant(DateTime.now()) },
            { "Guid", Identifiers.Guid, new Variant(UUID.randomUUID()) },
            { "ByteString", Identifiers.ByteString,
                    new Variant(new ByteString(new byte[] { 0x01, 0x02, 0x03, 0x04 })) },
            { "XmlElement", Identifiers.XmlElement, new Variant(new XmlElement("<a>hello</a>")) },
            { "LocalizedText", Identifiers.LocalizedText, new Variant(LocalizedText.english("localized text")) },
            { "QualifiedName", Identifiers.QualifiedName, new Variant(new QualifiedName(1234, "defg")) },
            { "NodeId", Identifiers.NodeId, new Variant(new NodeId(1234, "abcd")) },
            { "Variant", Identifiers.BaseDataType, new Variant(32) },
            { "Duration", Identifiers.Duration, new Variant(1.0) },
            { "UtcTime", Identifiers.UtcTime, new Variant(DateTime.now()) },
    };

    private static final Object[][] STATIC_ARRAY_NODES = new Object[][] {
            { "BooleanArray", Identifiers.Boolean, false },
            { "ByteArray", Identifiers.Byte, ubyte(0) },
            { "SByteArray", Identifiers.SByte, (byte) 0x00 },
            { "Int16Array", Identifiers.Int16, (short) 16 },
            { "Int32Array", Identifiers.Int32, 32 },
            { "Int64Array", Identifiers.Int64, 64L },
            { "UInt16Array", Identifiers.UInt16, ushort(16) },
            { "UInt32Array", Identifiers.UInt32, uint(32) },
            { "UInt64Array", Identifiers.UInt64, ulong(64L) },
            { "FloatArray", Identifiers.Float, 3.14f },
            { "DoubleArray", Identifiers.Double, 3.14d },
            { "StringArray", Identifiers.String, "string value" },
            { "DateTimeArray", Identifiers.DateTime, DateTime.now() },
            { "GuidArray", Identifiers.Guid, UUID.randomUUID() },
            { "ByteStringArray", Identifiers.ByteString, new ByteString(new byte[] { 0x01, 0x02, 0x03, 0x04 }) },
            { "XmlElementArray", Identifiers.XmlElement, new XmlElement("<a>hello</a>") },
            { "LocalizedTextArray", Identifiers.LocalizedText, LocalizedText.english("localized text") },
            { "QualifiedNameArray", Identifiers.QualifiedName, new QualifiedName(1234, "defg") },
            { "NodeIdArray", Identifiers.NodeId, new NodeId(1234, "abcd") }
    };

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private volatile Thread eventThread;
    private volatile boolean keepPostingEvents = true;

    private final Random random = new Random();

    private Map<String, ServerItem> siMap = new HashMap<>();
    private final SubscriptionModel subscriptionModel;
    private DeviceNodeManager dnm;

    public ServerNamespace(OpcUaServer server) {
        super(server, NAMESPACE_URI);

        subscriptionModel = new SubscriptionModel(server, this);

        getLifecycleManager().addLifecycle(subscriptionModel);

        getLifecycleManager().addStartupTask(this::createAndAddNodes);

        getLifecycleManager().addLifecycle(new Lifecycle() {
            @Override
            public void startup() {
                // startBogusEventNotifier();
            }

            @Override
            public void shutdown() {
                keepPostingEvents = false;
                // eventThread.interrupt();
                // eventThread.join();
            }
        });
    }

    public NodeId getNewNodeId(String name) {
        return newNodeId(name);
    }

    public UaNodeContext getNodeCxt(){
        return getNodeContext();
    }

    public UaNodeManager getNodeMan(){
        return getNodeManager();
    }

    public QualifiedName getQualifiedName(String name){
        return newQualifiedName(name);
    }

    public UShort getNamespaceIdx(){
        return getNamespaceIndex();
    }

    private void createAndAddNodes() {
        // Create a "HelloWorld" folder and add it to the node manager
        /*NodeId folderNodeId = newNodeId("HelloWorld");

        UaFolderNode folderNode = new UaFolderNode(
                getNodeContext(),
                folderNodeId,
                newQualifiedName("HelloWorld"),
                LocalizedText.english("HelloWorld"));

        getNodeManager().addNode(folderNode);

        // Make sure our new folder shows up under the server's Objects folder.
        folderNode.addReference(new Reference(
                folderNode.getNodeId(),
                Identifiers.Organizes,
                Identifiers.ObjectsFolder.expanded(),
                false));
        */
        dnm = new DeviceNodeManager(this, "/home/coloti/coloti-tcs/opcuaserver/MCS_ICD_13_03_23.xlsx");
        

        dnm.close();
        // Add the rest of the nodes
        addVariableNodes(dnm.getDeviceRootFolder());

        // addCustomObjectTypeAndInstance(folderNode);
    }

    private void startBogusEventNotifier() {
        // Set the EventNotifier bit on Server Node for Events.
        UaNode serverNode = getServer()
                .getAddressSpaceManager()
                .getManagedNode(Identifiers.Server)
                .orElse(null);

        if (serverNode instanceof ServerTypeNode) {
            ((ServerTypeNode) serverNode).setEventNotifier(ubyte(1));

            // Post a bogus Event every couple seconds
            eventThread = new Thread(() -> {
                while (keepPostingEvents) {
                    try {
                        BaseEventTypeNode eventNode = getServer().getEventFactory().createEvent(
                                newNodeId(UUID.randomUUID()),
                                Identifiers.BaseEventType);

                        eventNode.setBrowseName(new QualifiedName(1, "foo"));
                        eventNode.setDisplayName(LocalizedText.english("foo"));
                        eventNode.setEventId(ByteString.of(new byte[] { 0, 1, 2, 3 }));
                        eventNode.setEventType(Identifiers.BaseEventType);
                        eventNode.setSourceNode(serverNode.getNodeId());
                        eventNode.setSourceName(serverNode.getDisplayName().getText());
                        eventNode.setTime(DateTime.now());
                        eventNode.setReceiveTime(DateTime.NULL_VALUE);
                        eventNode.setMessage(LocalizedText.english("event message!"));
                        eventNode.setSeverity(ushort(2));

                        // noinspection UnstableApiUsage
                        getServer().getEventBus().post(eventNode);

                        eventNode.delete();
                    } catch (Throwable e) {
                        logger.error("Error creating EventNode: {}", e.getMessage(), e);
                    }

                    try {
                        // noinspection BusyWait
                        Thread.sleep(2_000);
                    } catch (InterruptedException ignored) {
                        // ignored
                    }
                }
            }, "bogus-event-poster");

            eventThread.start();
        }
    }

    private void addVariableNodes(UaFolderNode rootNode) {
        // addArrayNodes(rootNode);
        addScalarNodes(rootNode);
        // addAdminReadableNodes(rootNode);
        // addAdminWritableNodes(rootNode);
        // addDynamicNodes(rootNode);
        // addDataAccessNodes(rootNode);
        // addWriteOnlyNodes(rootNode);

    }

    private void addArrayNodes(UaFolderNode rootNode) {
        UaFolderNode arrayTypesFolder = new UaFolderNode(
                getNodeContext(),
                newNodeId("HelloWorld/ArrayTypes"),
                newQualifiedName("ArrayTypes"),
                LocalizedText.english("ArrayTypes"));

        getNodeManager().addNode(arrayTypesFolder);
        rootNode.addOrganizes(arrayTypesFolder);

        for (Object[] os : STATIC_ARRAY_NODES) {
            String name = (String) os[0];
            NodeId typeId = (NodeId) os[1];
            Object value = os[2];
            Object array = Array.newInstance(value.getClass(), 5);
            for (int i = 0; i < 5; i++) {
                Array.set(array, i, value);
            }
            Variant variant = new Variant(array);

            UaVariableNode.build(getNodeContext(), builder -> {
                builder.setNodeId(newNodeId("HelloWorld/ArrayTypes/" + name));
                builder.setAccessLevel(AccessLevel.READ_WRITE);
                builder.setUserAccessLevel(AccessLevel.READ_WRITE);
                builder.setBrowseName(newQualifiedName(name));
                builder.setDisplayName(LocalizedText.english(name));
                builder.setDataType(typeId);
                builder.setTypeDefinition(Identifiers.BaseDataVariableType);
                builder.setValueRank(ValueRank.OneDimension.getValue());
                builder.setArrayDimensions(new UInteger[] { uint(0) });
                builder.setValue(new DataValue(variant));

                builder.addReference(new Reference(
                        builder.getNodeId(),
                        Identifiers.Organizes,
                        arrayTypesFolder.getNodeId().expanded(),
                        Reference.Direction.INVERSE));

                return builder.buildAndAdd();
            });
        }
    }

    private void addScalarNodes(UaFolderNode rootNode) {
        UaFolderNode scalarTypesFolder = new UaFolderNode(
                getNodeContext(),
                newNodeId("ScalarTypes"),
                newQualifiedName("ScalarTypes"),
                LocalizedText.english("ScalarTypes"));

        getNodeManager().addNode(scalarTypesFolder);
        rootNode.addOrganizes(scalarTypesFolder);

        /*
         * for (Object[] os : STATIC_SCALAR_NODES) {
         * String name = (String) os[0];
         * NodeId typeId = (NodeId) os[1];
         * Variant variant = (Variant) os[2];
         * 
         * UaVariableNode node = new
         * UaVariableNode.UaVariableNodeBuilder(getNodeContext())
         * .setNodeId(newNodeId("HelloWorld/ScalarTypes/" + name))
         * .setAccessLevel(AccessLevel.READ_WRITE)
         * .setUserAccessLevel(AccessLevel.READ_WRITE)
         * .setBrowseName(newQualifiedName(name))
         * .setDisplayName(LocalizedText.english(name))
         * .setDataType(typeId)
         * .setTypeDefinition(Identifiers.BaseDataVariableType)
         * .build();
         * 
         * node.setValue(new DataValue(variant));
         * getNodeManager().addNode(node);
         * scalarTypesFolder.addOrganizes(node);
         * 
         * if (name.equals("Boolean")) {
         * Thread t = new Thread(() -> {
         * while (true) {
         * try {
         * Thread.sleep(1000L);
         * node.setValue(new DataValue(new Variant(Boolean.FALSE)));
         * System.out.println(node.getValue().getSourceTime());
         * } catch (InterruptedException e) {
         * Thread.currentThread().interrupt();
         * }
         * }
         * });
         * t.start();
         * }
         * }
         */
        dnm.createNode("AZ_MAX_VEL", scalarTypesFolder);
        /*Consumer<DataValue> consumer = new Consumer<DataValue>() {

            @Override
            public void accept(DataValue t) {

                logger.info(t.toString());
            }

        };
        ServerItem item = createServerItem("test", "int", consumer, scalarTypesFolder);
        siMap.put("test", item);*/
    }

    

    private void addWriteOnlyNodes(UaFolderNode rootNode) {
        UaFolderNode writeOnlyFolder = new UaFolderNode(
                getNodeContext(),
                newNodeId("HelloWorld/WriteOnly"),
                newQualifiedName("WriteOnly"),
                LocalizedText.english("WriteOnly"));

        getNodeManager().addNode(writeOnlyFolder);
        rootNode.addOrganizes(writeOnlyFolder);

        String name = "String";
        UaVariableNode node = new UaVariableNode.UaVariableNodeBuilder(getNodeContext())
                .setNodeId(newNodeId("HelloWorld/WriteOnly/" + name))
                .setAccessLevel(AccessLevel.WRITE_ONLY)
                .setUserAccessLevel(AccessLevel.WRITE_ONLY)
                .setBrowseName(newQualifiedName(name))
                .setDisplayName(LocalizedText.english(name))
                .setDataType(Identifiers.String)
                .setTypeDefinition(Identifiers.BaseDataVariableType)
                .build();

        node.setValue(new DataValue(new Variant("can't read this")));

        getNodeManager().addNode(node);
        writeOnlyFolder.addOrganizes(node);
    }

    private void addAdminReadableNodes(UaFolderNode rootNode) {
        UaFolderNode adminFolder = new UaFolderNode(
                getNodeContext(),
                newNodeId("HelloWorld/OnlyAdminCanRead"),
                newQualifiedName("OnlyAdminCanRead"),
                LocalizedText.english("OnlyAdminCanRead"));

        getNodeManager().addNode(adminFolder);
        rootNode.addOrganizes(adminFolder);

        String name = "String";
        UaVariableNode node = new UaVariableNode.UaVariableNodeBuilder(getNodeContext())
                .setNodeId(newNodeId("HelloWorld/OnlyAdminCanRead/" + name))
                .setAccessLevel(AccessLevel.READ_WRITE)
                .setBrowseName(newQualifiedName(name))
                .setDisplayName(LocalizedText.english(name))
                .setDataType(Identifiers.String)
                .setTypeDefinition(Identifiers.BaseDataVariableType)
                .build();

        node.setValue(new DataValue(new Variant("shh... don't tell the lusers")));

        getNodeManager().addNode(node);
        adminFolder.addOrganizes(node);
    }

    private void addAdminWritableNodes(UaFolderNode rootNode) {
        UaFolderNode adminFolder = new UaFolderNode(
                getNodeContext(),
                newNodeId("HelloWorld/OnlyAdminCanWrite"),
                newQualifiedName("OnlyAdminCanWrite"),
                LocalizedText.english("OnlyAdminCanWrite"));

        getNodeManager().addNode(adminFolder);
        rootNode.addOrganizes(adminFolder);

        String name = "String";
        UaVariableNode node = new UaVariableNode.UaVariableNodeBuilder(getNodeContext())
                .setNodeId(newNodeId("HelloWorld/OnlyAdminCanWrite/" + name))
                .setAccessLevel(AccessLevel.READ_WRITE)
                .setBrowseName(newQualifiedName(name))
                .setDisplayName(LocalizedText.english(name))
                .setDataType(Identifiers.String)
                .setTypeDefinition(Identifiers.BaseDataVariableType)
                .build();

        node.setValue(new DataValue(new Variant("admin was here")));

        getNodeManager().addNode(node);
        adminFolder.addOrganizes(node);
    }

    private void addDynamicNodes(UaFolderNode rootNode) {
        UaFolderNode dynamicFolder = new UaFolderNode(
                getNodeContext(),
                newNodeId("HelloWorld/Dynamic"),
                newQualifiedName("Dynamic"),
                LocalizedText.english("Dynamic"));

        getNodeManager().addNode(dynamicFolder);
        rootNode.addOrganizes(dynamicFolder);

        // Dynamic Boolean
        {
            String name = "Boolean";
            NodeId typeId = Identifiers.Boolean;
            Variant variant = new Variant(false);

            UaVariableNode node = new UaVariableNode.UaVariableNodeBuilder(getNodeContext())
                    .setNodeId(newNodeId("HelloWorld/Dynamic/" + name))
                    .setAccessLevel(AccessLevel.READ_WRITE)
                    .setBrowseName(newQualifiedName(name))
                    .setDisplayName(LocalizedText.english(name))
                    .setDataType(typeId)
                    .setTypeDefinition(Identifiers.BaseDataVariableType)
                    .build();

            node.setValue(new DataValue(variant));

            node.getFilterChain().addLast(
                    new AttributeLoggingFilter(),
                    AttributeFilters.getValue(
                            ctx -> new DataValue(new Variant(random.nextBoolean()))));

            getNodeManager().addNode(node);
            dynamicFolder.addOrganizes(node);
        }

        // Dynamic Int32
        {
            String name = "Int32";
            NodeId typeId = Identifiers.Int32;
            Variant variant = new Variant(0);

            UaVariableNode node = new UaVariableNode.UaVariableNodeBuilder(getNodeContext())
                    .setNodeId(newNodeId("HelloWorld/Dynamic/" + name))
                    .setAccessLevel(AccessLevel.READ_WRITE)
                    .setBrowseName(newQualifiedName(name))
                    .setDisplayName(LocalizedText.english(name))
                    .setDataType(typeId)
                    .setTypeDefinition(Identifiers.BaseDataVariableType)
                    .build();

            node.setValue(new DataValue(variant));

            node.getFilterChain().addLast(
                    new AttributeLoggingFilter(),
                    AttributeFilters.getValue(
                            ctx -> new DataValue(new Variant(random.nextInt()))));

            getNodeManager().addNode(node);
            dynamicFolder.addOrganizes(node);
        }

        // Dynamic Double
        {
            String name = "Double";
            NodeId typeId = Identifiers.Double;
            Variant variant = new Variant(0.0);

            UaVariableNode node = new UaVariableNode.UaVariableNodeBuilder(getNodeContext())
                    .setNodeId(newNodeId("HelloWorld/Dynamic/" + name))
                    .setAccessLevel(AccessLevel.READ_WRITE)
                    .setBrowseName(newQualifiedName(name))
                    .setDisplayName(LocalizedText.english(name))
                    .setDataType(typeId)
                    .setTypeDefinition(Identifiers.BaseDataVariableType)
                    .build();

            node.setValue(new DataValue(variant));

            getNodeManager().addNode(node);
            dynamicFolder.addOrganizes(node);
        }
    }

    private void addDataAccessNodes(UaFolderNode rootNode) {
        // DataAccess folder
        UaFolderNode dataAccessFolder = new UaFolderNode(
                getNodeContext(),
                newNodeId("HelloWorld/DataAccess"),
                newQualifiedName("DataAccess"),
                LocalizedText.english("DataAccess"));

        getNodeManager().addNode(dataAccessFolder);
        rootNode.addOrganizes(dataAccessFolder);

        try {
            AnalogItemTypeNode node = (AnalogItemTypeNode) getNodeFactory().createNode(
                    newNodeId("HelloWorld/DataAccess/AnalogValue"),
                    Identifiers.AnalogItemType,
                    new NodeFactory.InstantiationCallback() {
                        @Override
                        public boolean includeOptionalNode(NodeId typeDefinitionId, QualifiedName browseName) {
                            return true;
                        }
                    });

            node.setBrowseName(newQualifiedName("AnalogValue"));
            node.setDisplayName(LocalizedText.english("AnalogValue"));
            node.setDataType(Identifiers.Double);
            node.setValue(new DataValue(new Variant(3.14d)));

            node.setEURange(new Range(0.0, 100.0));

            getNodeManager().addNode(node);
            dataAccessFolder.addOrganizes(node);
        } catch (UaException e) {
            logger.error("Error creating AnalogItemType instance: {}", e.getMessage(), e);
        }

    }

    @Override
    public void onDataItemsCreated(List<DataItem> dataItems) {
        subscriptionModel.onDataItemsCreated(dataItems);
    }

    @Override
    public void onDataItemsModified(List<DataItem> dataItems) {
        subscriptionModel.onDataItemsModified(dataItems);
    }

    @Override
    public void onDataItemsDeleted(List<DataItem> dataItems) {
        subscriptionModel.onDataItemsDeleted(dataItems);
    }

    @Override
    public void onMonitoringModeChanged(List<MonitoredItem> monitoredItems) {
        subscriptionModel.onMonitoringModeChanged(monitoredItems);
    }

}