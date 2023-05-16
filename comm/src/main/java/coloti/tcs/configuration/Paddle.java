package coloti.tcs.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "VelocitaBassa",
    "VelocitaMedia",
    "VelocitaAlta"
})
public class Paddle {

    @JsonProperty("VelocitaBassa")
    private Integer velocitaBassa;
    @JsonProperty("VelocitaMedia")
    private Integer velocitaMedia;
    @JsonProperty("VelocitaAlta")
    private Integer velocitaAlta;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Paddle() {
    }

    /**
     * 
     * @param velocitaAlta
     * @param velocitaBassa
     * @param velocitaMedia
     */
    public Paddle(Integer velocitaBassa, Integer velocitaMedia, Integer velocitaAlta) {
        super();
        this.velocitaBassa = velocitaBassa;
        this.velocitaMedia = velocitaMedia;
        this.velocitaAlta = velocitaAlta;
    }

    @JsonProperty("VelocitaBassa")
    public Integer getVelocitaBassa() {
        return velocitaBassa;
    }

    @JsonProperty("VelocitaBassa")
    public void setVelocitaBassa(Integer velocitaBassa) {
        this.velocitaBassa = velocitaBassa;
    }

    public Paddle withVelocitaBassa(Integer velocitaBassa) {
        this.velocitaBassa = velocitaBassa;
        return this;
    }

    @JsonProperty("VelocitaMedia")
    public Integer getVelocitaMedia() {
        return velocitaMedia;
    }

    @JsonProperty("VelocitaMedia")
    public void setVelocitaMedia(Integer velocitaMedia) {
        this.velocitaMedia = velocitaMedia;
    }

    public Paddle withVelocitaMedia(Integer velocitaMedia) {
        this.velocitaMedia = velocitaMedia;
        return this;
    }

    @JsonProperty("VelocitaAlta")
    public Integer getVelocitaAlta() {
        return velocitaAlta;
    }

    @JsonProperty("VelocitaAlta")
    public void setVelocitaAlta(Integer velocitaAlta) {
        this.velocitaAlta = velocitaAlta;
    }

    public Paddle withVelocitaAlta(Integer velocitaAlta) {
        this.velocitaAlta = velocitaAlta;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Paddle.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("velocitaBassa");
        sb.append('=');
        sb.append(((this.velocitaBassa == null)?"<null>":this.velocitaBassa));
        sb.append(',');
        sb.append("velocitaMedia");
        sb.append('=');
        sb.append(((this.velocitaMedia == null)?"<null>":this.velocitaMedia));
        sb.append(',');
        sb.append("velocitaAlta");
        sb.append('=');
        sb.append(((this.velocitaAlta == null)?"<null>":this.velocitaAlta));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
