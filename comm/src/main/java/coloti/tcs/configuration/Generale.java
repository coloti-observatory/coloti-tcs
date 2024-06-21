
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
    "NumeroControllori",
    "ConnessioneAz",
    "ConnessioneEl",
    "ConnessioneDome",
    "ConnessioneMeteo",
    "IdSerialAz",
    "IdSerialEl",
    "IdSerialDome",
    "IdSerialWeather"
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
    @JsonProperty("ConnessioneAz")
    private Boolean connessioneAz;
    @JsonProperty("ConnessioneEl")
    private Boolean connessioneEl;
    @JsonProperty("ConnessioneDome")
    private Boolean connessioneDome;
    @JsonProperty("ConnessioneMeteo")
    private Boolean connessioneMeteo;
    @JsonProperty("IdSerialAz")
    private String idSerialAz;
    @JsonProperty("IdSerialEl")
    private String idSerialEl;
    @JsonProperty("IdSerialDome")
    private String idSerialDome;
    @JsonProperty("IdSerialWeather")
    private String idSerialWeather;
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
     * @param connessioneAz
     * @param connessioneEl
     * @param connessioneDome
     * @param connessioneMeteo
     * @param idSerialAz
     * @param idSerialEl
     * @param idSerialDome
     * @param idSerialWeather
     */
    public Generale(Integer tipoCupola, Integer tipoTelescopio, Integer montatura, Integer tipoControlloreAssi, Integer numeroAssi, Integer numeroControllori, Boolean connessioneAz, Boolean connessioneEl, Boolean connessioneDome, Boolean connessioneMeteo ,String idSerialAz, String idSerialEl, String idSerialDome, String idSerialWeather) {
        super();
        this.tipoCupola = tipoCupola;
        this.tipoTelescopio = tipoTelescopio;
        this.montatura = montatura;
        this.tipoControlloreAssi = tipoControlloreAssi;
        this.numeroAssi = numeroAssi;
        this.numeroControllori = numeroControllori;
        this.connessioneAz = connessioneAz;
        this.connessioneEl = connessioneEl;
        this.connessioneDome = connessioneDome;
        this.connessioneMeteo = connessioneMeteo;
        this.idSerialAz = idSerialAz;
        this.idSerialEl = idSerialEl;
        this.idSerialDome = idSerialDome;
        this.idSerialWeather = idSerialWeather;
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

    @JsonProperty("ConnessioneAz")
    public Boolean getConnessioneAz() {
        return connessioneAz;
    }

    @JsonProperty("ConnessioneAz")
    public void setConnessioneAz(Boolean connessioneAz) {
        this.connessioneAz = connessioneAz;
    }

    public Generale withConnessioneAz(Boolean connessioneAz) {
        this.connessioneAz = connessioneAz;
        return this;
    }

    @JsonProperty("ConnessioneEl")
    public Boolean getConnessioneEl() {
        return connessioneEl;
    }

    @JsonProperty("ConnessioneEl")
    public void setConnessioneEl(Boolean connessioneEl) {
        this.connessioneEl = connessioneEl;
    }

    public Generale withConnessioneEl(Boolean connessioneEl) {
        this.connessioneEl = connessioneEl;
        return this;
    }

    @JsonProperty("ConnessioneDome")
    public Boolean getConnessioneDome() {
        return connessioneDome;
    }

    @JsonProperty("ConnessioneDome")
    public void setConnessioneDome(Boolean connessioneDome) {
        this.connessioneDome = connessioneDome;
    }

    public Generale withConnessioneDome(Boolean connessioneDome) {
        this.connessioneDome = connessioneDome;
        return this;
    }

    @JsonProperty("ConnessioneMeteo")
    public Boolean getConnessioneMeteo() {
        return connessioneMeteo;
    }

    @JsonProperty("ConnessioneMeteo")
    public void setConnessioneMeteo(Boolean connessioneMeteo) {
        this.connessioneMeteo = connessioneMeteo;
    }

    public Generale withConnessioneMeteo(Boolean connessioneMeteo) {
        this.connessioneMeteo = connessioneMeteo;
        return this;
    }

    @JsonProperty("IdSerialAz")
    public String getIdSerialAz() {
        return idSerialAz;
    }

    @JsonProperty("IdSerialAz")
    public void setIdSerialAz(String idSerialAz) {
        this.idSerialAz = idSerialAz;
    }

    public Generale withIdSerialAz(String idSerialAz) {
        this.idSerialAz = idSerialAz;
        return this;
    }
    
    @JsonProperty("IdSerialEl")
    public String getIdSerialEl() {
        return idSerialEl;
    }

    @JsonProperty("IdSerialEl")
    public void setIdSerialEl(String idSerialEl) {
        this.idSerialEl = idSerialEl;
    }

    public Generale withIdSerialEl(String idSerialEl) {
        this.idSerialEl = idSerialEl;
        return this;
    }
    
    @JsonProperty("IdSerialDome")
    public String getIdSerialDome() {
        return idSerialDome;
    }

    @JsonProperty("IdSerialDome")
    public void setIdSerialDome(String idSerialDome) {
        this.idSerialDome = idSerialDome;
    }

    public Generale withIdSerialDome(String idSerialDome) {
        this.idSerialDome = idSerialDome;
        return this;
    }

    @JsonProperty("IdSerialWeather")
    public String getIdSerialWeather() {
        return idSerialWeather;
    }

    @JsonProperty("IdSerialWeather")
    public void setIdSerialWeather(String idSerialWeather) {
        this.idSerialWeather = idSerialWeather;
    }

    public Generale withIdSerialWeather(String idSerialWeather) {
        this.idSerialWeather = idSerialWeather;
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
        sb.append("connessioneAz");
        sb.append('=');
        sb.append(((this.connessioneAz == null)?"<null>":this.connessioneAz));
        sb.append(',');
        sb.append("connessioneEl");
        sb.append('=');
        sb.append(((this.connessioneEl == null)?"<null>":this.connessioneEl));
        sb.append(',');
        sb.append("connessioneDome");
        sb.append('=');
        sb.append(((this.connessioneDome == null)?"<null>":this.connessioneDome));
        sb.append(',');
        sb.append("idSerialAz");
        sb.append('=');
        sb.append(((this.idSerialAz == null)?"<null>":this.idSerialAz));
        sb.append(',');
        sb.append("idSerialEl");
        sb.append('=');
        sb.append(((this.idSerialEl == null)?"<null>":this.idSerialEl));
        sb.append(',');
        sb.append("idSerialDome");
        sb.append('=');
        sb.append(((this.idSerialDome == null)?"<null>":this.idSerialDome));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
