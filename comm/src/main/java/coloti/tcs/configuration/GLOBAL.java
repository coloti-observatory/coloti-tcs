package coloti.tcs.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "GENERALE",
    "OSSERVATORIO",
    "TELESCOPIO",
    "CUPOLA",
    "MOTORE_AR_AZ",
    "MOTORE_DEC_AL",
    "SB129X",
    "PADDLE",
    "POSZERO",
    "CATALOGHI"
})
public class GLOBAL {

    @JsonProperty("GENERALE")
    private Generale generale;
    @JsonProperty("OSSERVATORIO")
    private Osservatorio osservatorio;
    @JsonProperty("TELESCOPIO")
    private Telescopio telescopio;
    @JsonProperty("CUPOLA")
    private Cupola cupola;
    @JsonProperty("MOTORE_AR_AZ")
    private MotoreArAz motoreArAz;
    @JsonProperty("MOTORE_DEC_AL")
    private MotoreDecAl motoreDecAl;
    @JsonProperty("SB129X")
    private Sb129x sb129x;
    @JsonProperty("PADDLE")
    private Paddle paddle;
    @JsonProperty("POSZERO")
    private Poszero poszero;
    @JsonProperty("CATALOGHI")
    private Cataloghi cataloghi;

    /**
     * No args constructor for use in serialization
     * 
     */
    public GLOBAL() {
    }

    /**
     * 
     * @param cupola
     * @param osservatorio
     * @param generale
     * @param sb129x
     * @param motoreDecAl
     * @param poszero
     * @param cataloghi
     * @param telescopio
     * @param motoreArAz
     * @param paddle
     */
    public GLOBAL(Generale generale, Osservatorio osservatorio, Telescopio telescopio, Cupola cupola, MotoreArAz motoreArAz, MotoreDecAl motoreDecAl, Sb129x sb129x, Paddle paddle, Poszero poszero, Cataloghi cataloghi) {
        super();
        this.generale = generale;
        this.osservatorio = osservatorio;
        this.telescopio = telescopio;
        this.cupola = cupola;
        this.motoreArAz = motoreArAz;
        this.motoreDecAl = motoreDecAl;
        this.sb129x = sb129x;
        this.paddle = paddle;
        this.poszero = poszero;
        this.cataloghi = cataloghi;
    }

    @JsonProperty("GENERALE")
    public Generale getGenerale() {
        return generale;
    }

    @JsonProperty("GENERALE")
    public void setGenerale(Generale generale) {
        this.generale = generale;
    }

    public GLOBAL withGenerale(Generale generale) {
        this.generale = generale;
        return this;
    }

    @JsonProperty("OSSERVATORIO")
    public Osservatorio getOsservatorio() {
        return osservatorio;
    }

    @JsonProperty("OSSERVATORIO")
    public void setOsservatorio(Osservatorio osservatorio) {
        this.osservatorio = osservatorio;
    }

    public GLOBAL withOsservatorio(Osservatorio osservatorio) {
        this.osservatorio = osservatorio;
        return this;
    }

    @JsonProperty("TELESCOPIO")
    public Telescopio getTelescopio() {
        return telescopio;
    }

    @JsonProperty("TELESCOPIO")
    public void setTelescopio(Telescopio telescopio) {
        this.telescopio = telescopio;
    }

    public GLOBAL withTelescopio(Telescopio telescopio) {
        this.telescopio = telescopio;
        return this;
    }

    @JsonProperty("CUPOLA")
    public Cupola getCupola() {
        return cupola;
    }

    @JsonProperty("CUPOLA")
    public void setCupola(Cupola cupola) {
        this.cupola = cupola;
    }

    public GLOBAL withCupola(Cupola cupola) {
        this.cupola = cupola;
        return this;
    }

    @JsonProperty("MOTORE_AR_AZ")
    public MotoreArAz getMotoreArAz() {
        return motoreArAz;
    }

    @JsonProperty("MOTORE_AR_AZ")
    public void setMotoreArAz(MotoreArAz motoreArAz) {
        this.motoreArAz = motoreArAz;
    }

    public GLOBAL withMotoreArAz(MotoreArAz motoreArAz) {
        this.motoreArAz = motoreArAz;
        return this;
    }

    @JsonProperty("MOTORE_DEC_AL")
    public MotoreDecAl getMotoreDecAl() {
        return motoreDecAl;
    }

    @JsonProperty("MOTORE_DEC_AL")
    public void setMotoreDecAl(MotoreDecAl motoreDecAl) {
        this.motoreDecAl = motoreDecAl;
    }

    public GLOBAL withMotoreDecAl(MotoreDecAl motoreDecAl) {
        this.motoreDecAl = motoreDecAl;
        return this;
    }

    @JsonProperty("SB129X")
    public Sb129x getSb129x() {
        return sb129x;
    }

    @JsonProperty("SB129X")
    public void setSb129x(Sb129x sb129x) {
        this.sb129x = sb129x;
    }

    public GLOBAL withSb129x(Sb129x sb129x) {
        this.sb129x = sb129x;
        return this;
    }

    @JsonProperty("PADDLE")
    public Paddle getPaddle() {
        return paddle;
    }

    @JsonProperty("PADDLE")
    public void setPaddle(Paddle paddle) {
        this.paddle = paddle;
    }

    public GLOBAL withPaddle(Paddle paddle) {
        this.paddle = paddle;
        return this;
    }

    @JsonProperty("POSZERO")
    public Poszero getPoszero() {
        return poszero;
    }

    @JsonProperty("POSZERO")
    public void setPoszero(Poszero poszero) {
        this.poszero = poszero;
    }

    public GLOBAL withPoszero(Poszero poszero) {
        this.poszero = poszero;
        return this;
    }

    @JsonProperty("CATALOGHI")
    public Cataloghi getCataloghi() {
        return cataloghi;
    }

    @JsonProperty("CATALOGHI")
    public void setCataloghi(Cataloghi cataloghi) {
        this.cataloghi = cataloghi;
    }

    public GLOBAL withCataloghi(Cataloghi cataloghi) {
        this.cataloghi = cataloghi;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(GLOBAL.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("generale");
        sb.append('=');
        sb.append(((this.generale == null)?"<null>":this.generale));
        sb.append(',');
        sb.append("osservatorio");
        sb.append('=');
        sb.append(((this.osservatorio == null)?"<null>":this.osservatorio));
        sb.append(',');
        sb.append("telescopio");
        sb.append('=');
        sb.append(((this.telescopio == null)?"<null>":this.telescopio));
        sb.append(',');
        sb.append("cupola");
        sb.append('=');
        sb.append(((this.cupola == null)?"<null>":this.cupola));
        sb.append(',');
        sb.append("motoreArAz");
        sb.append('=');
        sb.append(((this.motoreArAz == null)?"<null>":this.motoreArAz));
        sb.append(',');
        sb.append("motoreDecAl");
        sb.append('=');
        sb.append(((this.motoreDecAl == null)?"<null>":this.motoreDecAl));
        sb.append(',');
        sb.append("sb129x");
        sb.append('=');
        sb.append(((this.sb129x == null)?"<null>":this.sb129x));
        sb.append(',');
        sb.append("paddle");
        sb.append('=');
        sb.append(((this.paddle == null)?"<null>":this.paddle));
        sb.append(',');
        sb.append("poszero");
        sb.append('=');
        sb.append(((this.poszero == null)?"<null>":this.poszero));
        sb.append(',');
        sb.append("cataloghi");
        sb.append('=');
        sb.append(((this.cataloghi == null)?"<null>":this.cataloghi));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
