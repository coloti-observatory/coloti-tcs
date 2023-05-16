package coloti.tcs.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "Nome",
    "Latitudine",
    "Longitudine",
    "Altitudine",
    "Timezone",
    "Gps",
    "Meteo"
})
public class Osservatorio {

    @JsonProperty("Nome")
    private String nome;
    @JsonProperty("Latitudine")
    private Double latitudine;
    @JsonProperty("Longitudine")
    private Double longitudine;
    @JsonProperty("Altitudine")
    private Integer altitudine;
    @JsonProperty("Timezone")
    private Integer timezone;
    @JsonProperty("Gps")
    private Integer gps;
    @JsonProperty("Meteo")
    private Integer meteo;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Osservatorio() {
    }

    /**
     * 
     * @param latitudine
     * @param altitudine
     * @param timezone
     * @param meteo
     * @param nome
     * @param gps
     * @param longitudine
     */
    public Osservatorio(String nome, Double latitudine, Double longitudine, Integer altitudine, Integer timezone, Integer gps, Integer meteo) {
        super();
        this.nome = nome;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
        this.altitudine = altitudine;
        this.timezone = timezone;
        this.gps = gps;
        this.meteo = meteo;
    }

    @JsonProperty("Nome")
    public String getNome() {
        return nome;
    }

    @JsonProperty("Nome")
    public void setNome(String nome) {
        this.nome = nome;
    }

    public Osservatorio withNome(String nome) {
        this.nome = nome;
        return this;
    }

    @JsonProperty("Latitudine")
    public Double getLatitudine() {
        return latitudine;
    }

    @JsonProperty("Latitudine")
    public void setLatitudine(Double latitudine) {
        this.latitudine = latitudine;
    }

    public Osservatorio withLatitudine(Double latitudine) {
        this.latitudine = latitudine;
        return this;
    }

    @JsonProperty("Longitudine")
    public Double getLongitudine() {
        return longitudine;
    }

    @JsonProperty("Longitudine")
    public void setLongitudine(Double longitudine) {
        this.longitudine = longitudine;
    }

    public Osservatorio withLongitudine(Double longitudine) {
        this.longitudine = longitudine;
        return this;
    }

    @JsonProperty("Altitudine")
    public Integer getAltitudine() {
        return altitudine;
    }

    @JsonProperty("Altitudine")
    public void setAltitudine(Integer altitudine) {
        this.altitudine = altitudine;
    }

    public Osservatorio withAltitudine(Integer altitudine) {
        this.altitudine = altitudine;
        return this;
    }

    @JsonProperty("Timezone")
    public Integer getTimezone() {
        return timezone;
    }

    @JsonProperty("Timezone")
    public void setTimezone(Integer timezone) {
        this.timezone = timezone;
    }

    public Osservatorio withTimezone(Integer timezone) {
        this.timezone = timezone;
        return this;
    }

    @JsonProperty("Gps")
    public Integer getGps() {
        return gps;
    }

    @JsonProperty("Gps")
    public void setGps(Integer gps) {
        this.gps = gps;
    }

    public Osservatorio withGps(Integer gps) {
        this.gps = gps;
        return this;
    }

    @JsonProperty("Meteo")
    public Integer getMeteo() {
        return meteo;
    }

    @JsonProperty("Meteo")
    public void setMeteo(Integer meteo) {
        this.meteo = meteo;
    }

    public Osservatorio withMeteo(Integer meteo) {
        this.meteo = meteo;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Osservatorio.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("nome");
        sb.append('=');
        sb.append(((this.nome == null)?"<null>":this.nome));
        sb.append(',');
        sb.append("latitudine");
        sb.append('=');
        sb.append(((this.latitudine == null)?"<null>":this.latitudine));
        sb.append(',');
        sb.append("longitudine");
        sb.append('=');
        sb.append(((this.longitudine == null)?"<null>":this.longitudine));
        sb.append(',');
        sb.append("altitudine");
        sb.append('=');
        sb.append(((this.altitudine == null)?"<null>":this.altitudine));
        sb.append(',');
        sb.append("timezone");
        sb.append('=');
        sb.append(((this.timezone == null)?"<null>":this.timezone));
        sb.append(',');
        sb.append("gps");
        sb.append('=');
        sb.append(((this.gps == null)?"<null>":this.gps));
        sb.append(',');
        sb.append("meteo");
        sb.append('=');
        sb.append(((this.meteo == null)?"<null>":this.meteo));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
