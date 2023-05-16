package coloti.tcs.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "ZeroX",
    "ZeroY",
    "ZeroZ",
    "ZeroCup"
})
public class Poszero {

    @JsonProperty("ZeroX")
    private Integer zeroX;
    @JsonProperty("ZeroY")
    private Integer zeroY;
    @JsonProperty("ZeroZ")
    private Integer zeroZ;
    @JsonProperty("ZeroCup")
    private Integer zeroCup;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Poszero() {
    }

    /**
     * 
     * @param zeroZ
     * @param zeroX
     * @param zeroY
     * @param zeroCup
     */
    public Poszero(Integer zeroX, Integer zeroY, Integer zeroZ, Integer zeroCup) {
        super();
        this.zeroX = zeroX;
        this.zeroY = zeroY;
        this.zeroZ = zeroZ;
        this.zeroCup = zeroCup;
    }

    @JsonProperty("ZeroX")
    public Integer getZeroX() {
        return zeroX;
    }

    @JsonProperty("ZeroX")
    public void setZeroX(Integer zeroX) {
        this.zeroX = zeroX;
    }

    public Poszero withZeroX(Integer zeroX) {
        this.zeroX = zeroX;
        return this;
    }

    @JsonProperty("ZeroY")
    public Integer getZeroY() {
        return zeroY;
    }

    @JsonProperty("ZeroY")
    public void setZeroY(Integer zeroY) {
        this.zeroY = zeroY;
    }

    public Poszero withZeroY(Integer zeroY) {
        this.zeroY = zeroY;
        return this;
    }

    @JsonProperty("ZeroZ")
    public Integer getZeroZ() {
        return zeroZ;
    }

    @JsonProperty("ZeroZ")
    public void setZeroZ(Integer zeroZ) {
        this.zeroZ = zeroZ;
    }

    public Poszero withZeroZ(Integer zeroZ) {
        this.zeroZ = zeroZ;
        return this;
    }

    @JsonProperty("ZeroCup")
    public Integer getZeroCup() {
        return zeroCup;
    }

    @JsonProperty("ZeroCup")
    public void setZeroCup(Integer zeroCup) {
        this.zeroCup = zeroCup;
    }

    public Poszero withZeroCup(Integer zeroCup) {
        this.zeroCup = zeroCup;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Poszero.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("zeroX");
        sb.append('=');
        sb.append(((this.zeroX == null)?"<null>":this.zeroX));
        sb.append(',');
        sb.append("zeroY");
        sb.append('=');
        sb.append(((this.zeroY == null)?"<null>":this.zeroY));
        sb.append(',');
        sb.append("zeroZ");
        sb.append('=');
        sb.append(((this.zeroZ == null)?"<null>":this.zeroZ));
        sb.append(',');
        sb.append("zeroCup");
        sb.append('=');
        sb.append(((this.zeroCup == null)?"<null>":this.zeroCup));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
