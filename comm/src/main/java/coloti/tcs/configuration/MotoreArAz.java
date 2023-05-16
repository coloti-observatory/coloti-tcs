package coloti.tcs.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "RisoluzioneEncoder1",
    "RisoluzioneEncoder2",
    "NumeroGiriMotore",
    "VelocitaMassima",
    "PosizioneLimiteInf",
    "PosizioneLimiteSup",
    "RiduzioneMotore",
    "PosizioneEncoder1",
    "PosizioneEncoder2"
})
public class MotoreArAz {

    @JsonProperty("RisoluzioneEncoder1")
    private Integer risoluzioneEncoder1;
    @JsonProperty("RisoluzioneEncoder2")
    private Integer risoluzioneEncoder2;
    @JsonProperty("NumeroGiriMotore")
    private Integer numeroGiriMotore;
    @JsonProperty("VelocitaMassima")
    private Double velocitaMassima;
    @JsonProperty("PosizioneLimiteInf")
    private Integer posizioneLimiteInf;
    @JsonProperty("PosizioneLimiteSup")
    private Integer posizioneLimiteSup;
    @JsonProperty("RiduzioneMotore")
    private Integer riduzioneMotore;
    @JsonProperty("PosizioneEncoder1")
    private Integer posizioneEncoder1;
    @JsonProperty("PosizioneEncoder2")
    private Integer posizioneEncoder2;

    /**
     * No args constructor for use in serialization
     * 
     */
    public MotoreArAz() {
    }

    /**
     * 
     * @param risoluzioneEncoder1
     * @param posizioneEncoder2
     * @param risoluzioneEncoder2
     * @param velocitaMassima
     * @param numeroGiriMotore
     * @param posizioneLimiteInf
     * @param posizioneLimiteSup
     * @param riduzioneMotore
     * @param posizioneEncoder1
     */
    public MotoreArAz(Integer risoluzioneEncoder1, Integer risoluzioneEncoder2, Integer numeroGiriMotore, Double velocitaMassima, Integer posizioneLimiteInf, Integer posizioneLimiteSup, Integer riduzioneMotore, Integer posizioneEncoder1, Integer posizioneEncoder2) {
        super();
        this.risoluzioneEncoder1 = risoluzioneEncoder1;
        this.risoluzioneEncoder2 = risoluzioneEncoder2;
        this.numeroGiriMotore = numeroGiriMotore;
        this.velocitaMassima = velocitaMassima;
        this.posizioneLimiteInf = posizioneLimiteInf;
        this.posizioneLimiteSup = posizioneLimiteSup;
        this.riduzioneMotore = riduzioneMotore;
        this.posizioneEncoder1 = posizioneEncoder1;
        this.posizioneEncoder2 = posizioneEncoder2;
    }

    @JsonProperty("RisoluzioneEncoder1")
    public Integer getRisoluzioneEncoder1() {
        return risoluzioneEncoder1;
    }

    @JsonProperty("RisoluzioneEncoder1")
    public void setRisoluzioneEncoder1(Integer risoluzioneEncoder1) {
        this.risoluzioneEncoder1 = risoluzioneEncoder1;
    }

    public MotoreArAz withRisoluzioneEncoder1(Integer risoluzioneEncoder1) {
        this.risoluzioneEncoder1 = risoluzioneEncoder1;
        return this;
    }

    @JsonProperty("RisoluzioneEncoder2")
    public Integer getRisoluzioneEncoder2() {
        return risoluzioneEncoder2;
    }

    @JsonProperty("RisoluzioneEncoder2")
    public void setRisoluzioneEncoder2(Integer risoluzioneEncoder2) {
        this.risoluzioneEncoder2 = risoluzioneEncoder2;
    }

    public MotoreArAz withRisoluzioneEncoder2(Integer risoluzioneEncoder2) {
        this.risoluzioneEncoder2 = risoluzioneEncoder2;
        return this;
    }

    @JsonProperty("NumeroGiriMotore")
    public Integer getNumeroGiriMotore() {
        return numeroGiriMotore;
    }

    @JsonProperty("NumeroGiriMotore")
    public void setNumeroGiriMotore(Integer numeroGiriMotore) {
        this.numeroGiriMotore = numeroGiriMotore;
    }

    public MotoreArAz withNumeroGiriMotore(Integer numeroGiriMotore) {
        this.numeroGiriMotore = numeroGiriMotore;
        return this;
    }

    @JsonProperty("VelocitaMassima")
    public Double getVelocitaMassima() {
        return velocitaMassima;
    }

    @JsonProperty("VelocitaMassima")
    public void setVelocitaMassima(Double velocitaMassima) {
        this.velocitaMassima = velocitaMassima;
    }

    public MotoreArAz withVelocitaMassima(Double velocitaMassima) {
        this.velocitaMassima = velocitaMassima;
        return this;
    }

    @JsonProperty("PosizioneLimiteInf")
    public Integer getPosizioneLimiteInf() {
        return posizioneLimiteInf;
    }

    @JsonProperty("PosizioneLimiteInf")
    public void setPosizioneLimiteInf(Integer posizioneLimiteInf) {
        this.posizioneLimiteInf = posizioneLimiteInf;
    }

    public MotoreArAz withPosizioneLimiteInf(Integer posizioneLimiteInf) {
        this.posizioneLimiteInf = posizioneLimiteInf;
        return this;
    }

    @JsonProperty("PosizioneLimiteSup")
    public Integer getPosizioneLimiteSup() {
        return posizioneLimiteSup;
    }

    @JsonProperty("PosizioneLimiteSup")
    public void setPosizioneLimiteSup(Integer posizioneLimiteSup) {
        this.posizioneLimiteSup = posizioneLimiteSup;
    }

    public MotoreArAz withPosizioneLimiteSup(Integer posizioneLimiteSup) {
        this.posizioneLimiteSup = posizioneLimiteSup;
        return this;
    }

    @JsonProperty("RiduzioneMotore")
    public Integer getRiduzioneMotore() {
        return riduzioneMotore;
    }

    @JsonProperty("RiduzioneMotore")
    public void setRiduzioneMotore(Integer riduzioneMotore) {
        this.riduzioneMotore = riduzioneMotore;
    }

    public MotoreArAz withRiduzioneMotore(Integer riduzioneMotore) {
        this.riduzioneMotore = riduzioneMotore;
        return this;
    }

    @JsonProperty("PosizioneEncoder1")
    public Integer getPosizioneEncoder1() {
        return posizioneEncoder1;
    }

    @JsonProperty("PosizioneEncoder1")
    public void setPosizioneEncoder1(Integer posizioneEncoder1) {
        this.posizioneEncoder1 = posizioneEncoder1;
    }

    public MotoreArAz withPosizioneEncoder1(Integer posizioneEncoder1) {
        this.posizioneEncoder1 = posizioneEncoder1;
        return this;
    }

    @JsonProperty("PosizioneEncoder2")
    public Integer getPosizioneEncoder2() {
        return posizioneEncoder2;
    }

    @JsonProperty("PosizioneEncoder2")
    public void setPosizioneEncoder2(Integer posizioneEncoder2) {
        this.posizioneEncoder2 = posizioneEncoder2;
    }

    public MotoreArAz withPosizioneEncoder2(Integer posizioneEncoder2) {
        this.posizioneEncoder2 = posizioneEncoder2;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MotoreArAz.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("risoluzioneEncoder1");
        sb.append('=');
        sb.append(((this.risoluzioneEncoder1 == null)?"<null>":this.risoluzioneEncoder1));
        sb.append(',');
        sb.append("risoluzioneEncoder2");
        sb.append('=');
        sb.append(((this.risoluzioneEncoder2 == null)?"<null>":this.risoluzioneEncoder2));
        sb.append(',');
        sb.append("numeroGiriMotore");
        sb.append('=');
        sb.append(((this.numeroGiriMotore == null)?"<null>":this.numeroGiriMotore));
        sb.append(',');
        sb.append("velocitaMassima");
        sb.append('=');
        sb.append(((this.velocitaMassima == null)?"<null>":this.velocitaMassima));
        sb.append(',');
        sb.append("posizioneLimiteInf");
        sb.append('=');
        sb.append(((this.posizioneLimiteInf == null)?"<null>":this.posizioneLimiteInf));
        sb.append(',');
        sb.append("posizioneLimiteSup");
        sb.append('=');
        sb.append(((this.posizioneLimiteSup == null)?"<null>":this.posizioneLimiteSup));
        sb.append(',');
        sb.append("riduzioneMotore");
        sb.append('=');
        sb.append(((this.riduzioneMotore == null)?"<null>":this.riduzioneMotore));
        sb.append(',');
        sb.append("posizioneEncoder1");
        sb.append('=');
        sb.append(((this.posizioneEncoder1 == null)?"<null>":this.posizioneEncoder1));
        sb.append(',');
        sb.append("posizioneEncoder2");
        sb.append('=');
        sb.append(((this.posizioneEncoder2 == null)?"<null>":this.posizioneEncoder2));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
