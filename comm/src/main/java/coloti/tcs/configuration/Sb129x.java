package coloti.tcs.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "PortaComunicazione1",
    "BaudRate1",
    "PortaComunicazione2",
    "BaudRate2",
    "PortaComunicazione3",
    "BaudRate3"
})
public class Sb129x {

    @JsonProperty("PortaComunicazione1")
    private Integer portaComunicazione1;
    @JsonProperty("BaudRate1")
    private Integer baudRate1;
    @JsonProperty("PortaComunicazione2")
    private Integer portaComunicazione2;
    @JsonProperty("BaudRate2")
    private Integer baudRate2;
    @JsonProperty("PortaComunicazione3")
    private Integer portaComunicazione3;
    @JsonProperty("BaudRate3")
    private Integer baudRate3;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Sb129x() {
    }

    /**
     * 
     * @param baudRate1
     * @param portaComunicazione3
     * @param portaComunicazione2
     * @param baudRate3
     * @param baudRate2
     * @param portaComunicazione1
     */
    public Sb129x(Integer portaComunicazione1, Integer baudRate1, Integer portaComunicazione2, Integer baudRate2, Integer portaComunicazione3, Integer baudRate3) {
        super();
        this.portaComunicazione1 = portaComunicazione1;
        this.baudRate1 = baudRate1;
        this.portaComunicazione2 = portaComunicazione2;
        this.baudRate2 = baudRate2;
        this.portaComunicazione3 = portaComunicazione3;
        this.baudRate3 = baudRate3;
    }

    @JsonProperty("PortaComunicazione1")
    public Integer getPortaComunicazione1() {
        return portaComunicazione1;
    }

    @JsonProperty("PortaComunicazione1")
    public void setPortaComunicazione1(Integer portaComunicazione1) {
        this.portaComunicazione1 = portaComunicazione1;
    }

    public Sb129x withPortaComunicazione1(Integer portaComunicazione1) {
        this.portaComunicazione1 = portaComunicazione1;
        return this;
    }

    @JsonProperty("BaudRate1")
    public Integer getBaudRate1() {
        return baudRate1;
    }

    @JsonProperty("BaudRate1")
    public void setBaudRate1(Integer baudRate1) {
        this.baudRate1 = baudRate1;
    }

    public Sb129x withBaudRate1(Integer baudRate1) {
        this.baudRate1 = baudRate1;
        return this;
    }

    @JsonProperty("PortaComunicazione2")
    public Integer getPortaComunicazione2() {
        return portaComunicazione2;
    }

    @JsonProperty("PortaComunicazione2")
    public void setPortaComunicazione2(Integer portaComunicazione2) {
        this.portaComunicazione2 = portaComunicazione2;
    }

    public Sb129x withPortaComunicazione2(Integer portaComunicazione2) {
        this.portaComunicazione2 = portaComunicazione2;
        return this;
    }

    @JsonProperty("BaudRate2")
    public Integer getBaudRate2() {
        return baudRate2;
    }

    @JsonProperty("BaudRate2")
    public void setBaudRate2(Integer baudRate2) {
        this.baudRate2 = baudRate2;
    }

    public Sb129x withBaudRate2(Integer baudRate2) {
        this.baudRate2 = baudRate2;
        return this;
    }

    @JsonProperty("PortaComunicazione3")
    public Integer getPortaComunicazione3() {
        return portaComunicazione3;
    }

    @JsonProperty("PortaComunicazione3")
    public void setPortaComunicazione3(Integer portaComunicazione3) {
        this.portaComunicazione3 = portaComunicazione3;
    }

    public Sb129x withPortaComunicazione3(Integer portaComunicazione3) {
        this.portaComunicazione3 = portaComunicazione3;
        return this;
    }

    @JsonProperty("BaudRate3")
    public Integer getBaudRate3() {
        return baudRate3;
    }

    @JsonProperty("BaudRate3")
    public void setBaudRate3(Integer baudRate3) {
        this.baudRate3 = baudRate3;
    }

    public Sb129x withBaudRate3(Integer baudRate3) {
        this.baudRate3 = baudRate3;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Sb129x.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("portaComunicazione1");
        sb.append('=');
        sb.append(((this.portaComunicazione1 == null)?"<null>":this.portaComunicazione1));
        sb.append(',');
        sb.append("baudRate1");
        sb.append('=');
        sb.append(((this.baudRate1 == null)?"<null>":this.baudRate1));
        sb.append(',');
        sb.append("portaComunicazione2");
        sb.append('=');
        sb.append(((this.portaComunicazione2 == null)?"<null>":this.portaComunicazione2));
        sb.append(',');
        sb.append("baudRate2");
        sb.append('=');
        sb.append(((this.baudRate2 == null)?"<null>":this.baudRate2));
        sb.append(',');
        sb.append("portaComunicazione3");
        sb.append('=');
        sb.append(((this.portaComunicazione3 == null)?"<null>":this.portaComunicazione3));
        sb.append(',');
        sb.append("baudRate3");
        sb.append('=');
        sb.append(((this.baudRate3 == null)?"<null>":this.baudRate3));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
