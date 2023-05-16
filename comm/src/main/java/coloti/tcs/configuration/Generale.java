
package coloti.tcs.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "TipoCupola",
    "TipoTelescopio",
    "Montatura",
    "TipoControlloreAssi",
    "NumeroAssi",
    "NumeroControllori"
})
public class Generale {

    @JsonProperty("TipoCupola")
    private Integer tipoCupola;
    @JsonProperty("TipoTelescopio")
    private Integer tipoTelescopio;
    @JsonProperty("Montatura")
    private Integer montatura;
    @JsonProperty("TipoControlloreAssi")
    private Integer tipoControlloreAssi;
    @JsonProperty("NumeroAssi")
    private Integer numeroAssi;
    @JsonProperty("NumeroControllori")
    private Integer numeroControllori;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Generale() {
    }

    /**
     * 
     * @param tipoCupola
     * @param tipoControlloreAssi
     * @param tipoTelescopio
     * @param numeroControllori
     * @param numeroAssi
     * @param montatura
     */
    public Generale(Integer tipoCupola, Integer tipoTelescopio, Integer montatura, Integer tipoControlloreAssi, Integer numeroAssi, Integer numeroControllori) {
        super();
        this.tipoCupola = tipoCupola;
        this.tipoTelescopio = tipoTelescopio;
        this.montatura = montatura;
        this.tipoControlloreAssi = tipoControlloreAssi;
        this.numeroAssi = numeroAssi;
        this.numeroControllori = numeroControllori;
    }

    @JsonProperty("TipoCupola")
    public Integer getTipoCupola() {
        return tipoCupola;
    }

    @JsonProperty("TipoCupola")
    public void setTipoCupola(Integer tipoCupola) {
        this.tipoCupola = tipoCupola;
    }

    public Generale withTipoCupola(Integer tipoCupola) {
        this.tipoCupola = tipoCupola;
        return this;
    }

    @JsonProperty("TipoTelescopio")
    public Integer getTipoTelescopio() {
        return tipoTelescopio;
    }

    @JsonProperty("TipoTelescopio")
    public void setTipoTelescopio(Integer tipoTelescopio) {
        this.tipoTelescopio = tipoTelescopio;
    }

    public Generale withTipoTelescopio(Integer tipoTelescopio) {
        this.tipoTelescopio = tipoTelescopio;
        return this;
    }

    @JsonProperty("Montatura")
    public Integer getMontatura() {
        return montatura;
    }

    @JsonProperty("Montatura")
    public void setMontatura(Integer montatura) {
        this.montatura = montatura;
    }

    public Generale withMontatura(Integer montatura) {
        this.montatura = montatura;
        return this;
    }

    @JsonProperty("TipoControlloreAssi")
    public Integer getTipoControlloreAssi() {
        return tipoControlloreAssi;
    }

    @JsonProperty("TipoControlloreAssi")
    public void setTipoControlloreAssi(Integer tipoControlloreAssi) {
        this.tipoControlloreAssi = tipoControlloreAssi;
    }

    public Generale withTipoControlloreAssi(Integer tipoControlloreAssi) {
        this.tipoControlloreAssi = tipoControlloreAssi;
        return this;
    }

    @JsonProperty("NumeroAssi")
    public Integer getNumeroAssi() {
        return numeroAssi;
    }

    @JsonProperty("NumeroAssi")
    public void setNumeroAssi(Integer numeroAssi) {
        this.numeroAssi = numeroAssi;
    }

    public Generale withNumeroAssi(Integer numeroAssi) {
        this.numeroAssi = numeroAssi;
        return this;
    }

    @JsonProperty("NumeroControllori")
    public Integer getNumeroControllori() {
        return numeroControllori;
    }

    @JsonProperty("NumeroControllori")
    public void setNumeroControllori(Integer numeroControllori) {
        this.numeroControllori = numeroControllori;
    }

    public Generale withNumeroControllori(Integer numeroControllori) {
        this.numeroControllori = numeroControllori;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Generale.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("tipoCupola");
        sb.append('=');
        sb.append(((this.tipoCupola == null)?"<null>":this.tipoCupola));
        sb.append(',');
        sb.append("tipoTelescopio");
        sb.append('=');
        sb.append(((this.tipoTelescopio == null)?"<null>":this.tipoTelescopio));
        sb.append(',');
        sb.append("montatura");
        sb.append('=');
        sb.append(((this.montatura == null)?"<null>":this.montatura));
        sb.append(',');
        sb.append("tipoControlloreAssi");
        sb.append('=');
        sb.append(((this.tipoControlloreAssi == null)?"<null>":this.tipoControlloreAssi));
        sb.append(',');
        sb.append("numeroAssi");
        sb.append('=');
        sb.append(((this.numeroAssi == null)?"<null>":this.numeroAssi));
        sb.append(',');
        sb.append("numeroControllori");
        sb.append('=');
        sb.append(((this.numeroControllori == null)?"<null>":this.numeroControllori));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
