package opcua.newexample;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;

import org.eclipse.milo.opcua.sdk.server.nodes.AttributeObserver;
import org.eclipse.milo.opcua.sdk.server.nodes.UaFolderNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaNodeContext;
import org.eclipse.milo.opcua.sdk.server.nodes.UaObjectNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableNode;
import org.eclipse.milo.opcua.sdk.core.AccessLevel;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServerItem {
    private static final Logger logger = LoggerFactory.getLogger(ServerItem.class);

    private UaObjectNode baseNode;
    private UaVariableNode item;

    private DataValue value = new DataValue(StatusCode.BAD);
    private final Set<Consumer<DataValue>> listeners = new CopyOnWriteArraySet<>();


    public ServerItem(final String itemId, String datatype, final UaNodeContext nodeManager, final UShort namespaceIndex, final UaFolderNode baseNode) {

        this.baseNode = baseNode;
        
        final NodeId nodeId = new NodeId(namespaceIndex, itemId);
        final QualifiedName qname = new QualifiedName(namespaceIndex, itemId);
        final LocalizedText displayName = LocalizedText.english(itemId);

        // create variable node

        this.item = new UaVariableNode(nodeManager, nodeId, qname, displayName);

        // item.setDataType();
        item.setDataType(ServerUtils.javaToOpcUa.get(datatype.toUpperCase()));
    
        this.item.setAccessLevel(AccessLevel.toValue(AccessLevel.READ_WRITE));
        this.item.setUserAccessLevel(AccessLevel.toValue(AccessLevel.READ_WRITE));
        this.item.addAttributeObserver(new AttributeObserver() {

                @Override
                public void attributeChanged(UaNode node, AttributeId attributeId, Object value) {
                    if(attributeId.name().equals("Value")){
                        dispatch((DataValue)value);
                    }
                }
            });
        
    }

    public void dispose() {
        this.baseNode.removeComponent(this.item);
        this.listeners.clear();
    }

    public void addWriteListener(final Consumer<DataValue> consumer) {
        this.listeners.add(consumer);
    }

    public void removeWriteListener(final Consumer<DataValue> consumer) {
        this.listeners.remove(consumer);
    }

    protected void dispatch(final DataValue value) {
        //logger.debug("setValue -> {}", value);
        this.value= value;
        runThrough(this.listeners, c -> c.accept(value)); 
    }

    
    protected <T> void runThrough(final Collection<Consumer<T>> list, final Consumer<Consumer<T>> consumer) {
        LinkedList<Throwable> errors = null;

        for (final Consumer<T> listener : list) {
            try {
                consumer.accept(listener);
            } catch (final Throwable e) {
                if (errors == null) {
                    errors = new LinkedList<>();
                }
                errors.add(e);
            }
        }

        if (errors == null || errors.isEmpty()) {
            return;
        }

        final RuntimeException ex = new RuntimeException(errors.pollFirst());
        errors.forEach(ex::addSuppressed);
        throw ex;
    }

    protected DataValue getDataValue() {
        return this.value;
    }

    public void updateValue(final Object value) {
        this.value = new DataValue(new Variant(value), StatusCode.GOOD, DateTime.now());
        this.item.setValue(getDataValue());
    }

    public UaNode getNode() {
        return this.item;
    }

    public UaVariableNode getItem() {
        return item;
    }

    
}
