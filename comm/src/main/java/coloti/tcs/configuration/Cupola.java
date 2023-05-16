package coloti.tcs.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "ControlloreCupola",
    "CupolaEncoderRis",
    "StadioRiduzione"
})
public class Cupola {

    @JsonProperty("ControlloreCupola")
    private Integer controlloreCupola;
    @JsonProperty("CupolaEncoderRis")
    private Integer cupolaEncoderRis;
    @JsonProperty("StadioRiduzione")
    private Integer stadioRiduzione;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Cupola() {
    }

    /**
     * 
     * @param cupolaEncoderRis
     * @param stadioRiduzione
     * @param controlloreCupola
     */
    public Cupola(Integer controlloreCupola, Integer cupolaEncoderRis, Integer stadioRiduzione) {
        super();
        this.controlloreCupola = controlloreCupola;
        this.cupolaEncoderRis = cupolaEncoderRis;
        this.stadioRiduzione = stadioRiduzione;
    }

    @JsonProperty("ControlloreCupola")
    public Integer getControlloreCupola() {
        return controlloreCupola;
    }

    @JsonProperty("ControlloreCupola")
    public void setControlloreCupola(Integer controlloreCupola) {
        this.controlloreCupola = controlloreCupola;
    }

    public Cupola withControlloreCupola(Integer controlloreCupola) {
        this.controlloreCupola = controlloreCupola;
        return this;
    }

    @JsonProperty("CupolaEncoderRis")
    public Integer getCupolaEncoderRis() {
        return cupolaEncoderRis;
    }

    @JsonProperty("CupolaEncoderRis")
    public void setCupolaEncoderRis(Integer cupolaEncoderRis) {
        this.cupolaEncoderRis = cupolaEncoderRis;
    }

    public Cupola withCupolaEncoderRis(Integer cupolaEncoderRis) {
        this.cupolaEncoderRis = cupolaEncoderRis;
        return this;
    }

    @JsonProperty("StadioRiduzione")
    public Integer getStadioRiduzione() {
        return stadioRiduzione;
    }

    @JsonProperty("StadioRiduzione")
    public void setStadioRiduzione(Integer stadioRiduzione) {
        this.stadioRiduzione = stadioRiduzione;
    }

    public Cupola withStadioRiduzione(Integer stadioRiduzione) {
        this.stadioRiduzione = stadioRiduzione;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Cupola.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("controlloreCupola");
        sb.append('=');
        sb.append(((this.controlloreCupola == null)?"<null>":this.controlloreCupola));
        sb.append(',');
        sb.append("cupolaEncoderRis");
        sb.append('=');
        sb.append(((this.cupolaEncoderRis == null)?"<null>":this.cupolaEncoderRis));
        sb.append(',');
        sb.append("stadioRiduzione");
        sb.append('=');
        sb.append(((this.stadioRiduzione == null)?"<null>":this.stadioRiduzione));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
