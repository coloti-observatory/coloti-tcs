package coloti.tcs.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Nome",
    "DiametroSpecchio",
    "LunghezzaFocale",
    "RapportoRiduzioneAR",
    "RapportoRiduzioneDEC",
    "RapportoRiduzioneAZ",
    "RapportoRiduzioneAL",
    "RapportoRiduzioneDE",
    "CampoDiVista"
})
public class Telescopio {

    @JsonProperty("Nome")
    private String nome;
    @JsonProperty("DiametroSpecchio")
    private Integer diametroSpecchio;
    @JsonProperty("LunghezzaFocale")
    private Integer lunghezzaFocale;
    @JsonProperty("RapportoRiduzioneAR")
    private Integer rapportoRiduzioneAR;
    @JsonProperty("RapportoRiduzioneDEC")
    private Integer rapportoRiduzioneDEC;
    @JsonProperty("RapportoRiduzioneAZ")
    private Integer rapportoRiduzioneAZ;
    @JsonProperty("RapportoRiduzioneAL")
    private Integer rapportoRiduzioneAL;
    @JsonProperty("RapportoRiduzioneDE")
    private Integer rapportoRiduzioneDE;
    @JsonProperty("CampoDiVista")
    private Integer campoDiVista;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Telescopio() {
    }

    /**
     * 
     * @param rapportoRiduzioneAL
     * @param rapportoRiduzioneAZ
     * @param rapportoRiduzioneDEC
     * @param diametroSpecchio
     * @param nome
     * @param rapportoRiduzioneAR
     * @param rapportoRiduzioneDE
     * @param campoDiVista
     * @param lunghezzaFocale
     */
    public Telescopio(String nome, Integer diametroSpecchio, Integer lunghezzaFocale, Integer rapportoRiduzioneAR, Integer rapportoRiduzioneDEC, Integer rapportoRiduzioneAZ, Integer rapportoRiduzioneAL, Integer rapportoRiduzioneDE, Integer campoDiVista) {
        super();
        this.nome = nome;
        this.diametroSpecchio = diametroSpecchio;
        this.lunghezzaFocale = lunghezzaFocale;
        this.rapportoRiduzioneAR = rapportoRiduzioneAR;
        this.rapportoRiduzioneDEC = rapportoRiduzioneDEC;
        this.rapportoRiduzioneAZ = rapportoRiduzioneAZ;
        this.rapportoRiduzioneAL = rapportoRiduzioneAL;
        this.rapportoRiduzioneDE = rapportoRiduzioneDE;
        this.campoDiVista = campoDiVista;
    }

    @JsonProperty("Nome")
    public String getNome() {
        return nome;
    }

    @JsonProperty("Nome")
    public void setNome(String nome) {
        this.nome = nome;
    }

    public Telescopio withNome(String nome) {
        this.nome = nome;
        return this;
    }

    @JsonProperty("DiametroSpecchio")
    public Integer getDiametroSpecchio() {
        return diametroSpecchio;
    }

    @JsonProperty("DiametroSpecchio")
    public void setDiametroSpecchio(Integer diametroSpecchio) {
        this.diametroSpecchio = diametroSpecchio;
    }

    public Telescopio withDiametroSpecchio(Integer diametroSpecchio) {
        this.diametroSpecchio = diametroSpecchio;
        return this;
    }

    @JsonProperty("LunghezzaFocale")
    public Integer getLunghezzaFocale() {
        return lunghezzaFocale;
    }

    @JsonProperty("LunghezzaFocale")
    public void setLunghezzaFocale(Integer lunghezzaFocale) {
        this.lunghezzaFocale = lunghezzaFocale;
    }

    public Telescopio withLunghezzaFocale(Integer lunghezzaFocale) {
        this.lunghezzaFocale = lunghezzaFocale;
        return this;
    }

    @JsonProperty("RapportoRiduzioneAR")
    public Integer getRapportoRiduzioneAR() {
        return rapportoRiduzioneAR;
    }

    @JsonProperty("RapportoRiduzioneAR")
    public void setRapportoRiduzioneAR(Integer rapportoRiduzioneAR) {
        this.rapportoRiduzioneAR = rapportoRiduzioneAR;
    }

    public Telescopio withRapportoRiduzioneAR(Integer rapportoRiduzioneAR) {
        this.rapportoRiduzioneAR = rapportoRiduzioneAR;
        return this;
    }

    @JsonProperty("RapportoRiduzioneDEC")
    public Integer getRapportoRiduzioneDEC() {
        return rapportoRiduzioneDEC;
    }

    @JsonProperty("RapportoRiduzioneDEC")
    public void setRapportoRiduzioneDEC(Integer rapportoRiduzioneDEC) {
        this.rapportoRiduzioneDEC = rapportoRiduzioneDEC;
    }

    public Telescopio withRapportoRiduzioneDEC(Integer rapportoRiduzioneDEC) {
        this.rapportoRiduzioneDEC = rapportoRiduzioneDEC;
        return this;
    }

    @JsonProperty("RapportoRiduzioneAZ")
    public Integer getRapportoRiduzioneAZ() {
        return rapportoRiduzioneAZ;
    }

    @JsonProperty("RapportoRiduzioneAZ")
    public void setRapportoRiduzioneAZ(Integer rapportoRiduzioneAZ) {
        this.rapportoRiduzioneAZ = rapportoRiduzioneAZ;
    }

    public Telescopio withRapportoRiduzioneAZ(Integer rapportoRiduzioneAZ) {
        this.rapportoRiduzioneAZ = rapportoRiduzioneAZ;
        return this;
    }

    @JsonProperty("RapportoRiduzioneAL")
    public Integer getRapportoRiduzioneAL() {
        return rapportoRiduzioneAL;
    }

    @JsonProperty("RapportoRiduzioneAL")
    public void setRapportoRiduzioneAL(Integer rapportoRiduzioneAL) {
        this.rapportoRiduzioneAL = rapportoRiduzioneAL;
    }

    public Telescopio withRapportoRiduzioneAL(Integer rapportoRiduzioneAL) {
        this.rapportoRiduzioneAL = rapportoRiduzioneAL;
        return this;
    }

    @JsonProperty("RapportoRiduzioneDE")
    public Integer getRapportoRiduzioneDE() {
        return rapportoRiduzioneDE;
    }

    @JsonProperty("RapportoRiduzioneDE")
    public void setRapportoRiduzioneDE(Integer rapportoRiduzioneDE) {
        this.rapportoRiduzioneDE = rapportoRiduzioneDE;
    }

    public Telescopio withRapportoRiduzioneDE(Integer rapportoRiduzioneDE) {
        this.rapportoRiduzioneDE = rapportoRiduzioneDE;
        return this;
    }

    @JsonProperty("CampoDiVista")
    public Integer getCampoDiVista() {
        return campoDiVista;
    }

    @JsonProperty("CampoDiVista")
    public void setCampoDiVista(Integer campoDiVista) {
        this.campoDiVista = campoDiVista;
    }

    public Telescopio withCampoDiVista(Integer campoDiVista) {
        this.campoDiVista = campoDiVista;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Telescopio.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("nome");
        sb.append('=');
        sb.append(((this.nome == null)?"<null>":this.nome));
        sb.append(',');
        sb.append("diametroSpecchio");
        sb.append('=');
        sb.append(((this.diametroSpecchio == null)?"<null>":this.diametroSpecchio));
        sb.append(',');
        sb.append("lunghezzaFocale");
        sb.append('=');
        sb.append(((this.lunghezzaFocale == null)?"<null>":this.lunghezzaFocale));
        sb.append(',');
        sb.append("rapportoRiduzioneAR");
        sb.append('=');
        sb.append(((this.rapportoRiduzioneAR == null)?"<null>":this.rapportoRiduzioneAR));
        sb.append(',');
        sb.append("rapportoRiduzioneDEC");
        sb.append('=');
        sb.append(((this.rapportoRiduzioneDEC == null)?"<null>":this.rapportoRiduzioneDEC));
        sb.append(',');
        sb.append("rapportoRiduzioneAZ");
        sb.append('=');
        sb.append(((this.rapportoRiduzioneAZ == null)?"<null>":this.rapportoRiduzioneAZ));
        sb.append(',');
        sb.append("rapportoRiduzioneAL");
        sb.append('=');
        sb.append(((this.rapportoRiduzioneAL == null)?"<null>":this.rapportoRiduzioneAL));
        sb.append(',');
        sb.append("rapportoRiduzioneDE");
        sb.append('=');
        sb.append(((this.rapportoRiduzioneDE == null)?"<null>":this.rapportoRiduzioneDE));
        sb.append(',');
        sb.append("campoDiVista");
        sb.append('=');
        sb.append(((this.campoDiVista == null)?"<null>":this.campoDiVista));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
