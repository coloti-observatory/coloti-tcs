package opcua.newexample;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;

import coloti.tcs.TCS;

public class ConsumerLibrary {

    private static TCS tcs;
    
    public ConsumerLibrary() {}

    public static Consumer<DataValue> SetAzMaxVelConsumer = new Consumer<DataValue>() {
        @Override
        public void accept(DataValue t) {
            tcs.SetAzMaxVel((double)t.getValue().getValue());
            System.out.println("READ:" + t.toString());
        }
    };

    public static Consumer<DataValue> SetAzMinVelConsumer = new Consumer<DataValue>() {
        @Override
        public void accept(DataValue t) {
            tcs.SetAzMinVel((double)t.getValue().getValue());
            System.out.println("READ:" + t.toString());
        }
    };

    public static Consumer<DataValue> SetAzMaxAccConsumer = new Consumer<DataValue>() {
        @Override
        public void accept(DataValue t) {
            tcs.SetAzMaxAcc((double)t.getValue().getValue());
            System.out.println("READ:" + t.toString());
        }
    };

    public static Consumer<DataValue> SetAzMinAccConsumer = new Consumer<DataValue>() {
        @Override
        public void accept(DataValue t) {
            tcs.SetAzMinAcc((double)t.getValue().getValue());
            System.out.println("READ:" + t.toString());
        }
    };

    public static Consumer<DataValue> SetAzMaxDecConsumer = new Consumer<DataValue>() {
        @Override
        public void accept(DataValue t) {
            tcs.SetAzMaxDec((double)t.getValue().getValue());
            System.out.println("READ:" + t.toString());
        }
    };

    public static Consumer<DataValue> SetAzMinDecConsumer = new Consumer<DataValue>() {
        @Override
        public void accept(DataValue t) {
            tcs.SetAzMinDec((double)t.getValue().getValue());
            System.out.println("READ:" + t.toString());
        }
    };


















    public static Map<String, Consumer<DataValue>> consumerMap;
    static {
        consumerMap = new HashMap<>();
        consumerMap.put("AZ_MAX_VEL", SetAzMaxVelConsumer);
    }







    public TCS getTcs() {
        return tcs;
    }





    public Map<String, Consumer<DataValue>> getConsumerMap() {
        return consumerMap;
    }
    
}
