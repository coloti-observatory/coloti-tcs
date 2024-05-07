package coloti.tcs.trajectory;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "Id",
        "Func",
        "coeff",
        "HorCoord",
        "description"
})
@Generated("jsonschema2pojo")
public class Term {

    @JsonProperty("Id")
    private int id;
    @JsonProperty("Func")
    private String func;
    @JsonProperty("coeff")
    private double coeff;
    @JsonProperty("HorCoord")
    private String horCoord;
    @JsonProperty("description")
    private String description;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public Term() {
    }

    /**
     *
     * @param horCoord
     * @param func
     * @param description
     * @param id
     * @param coeff
     */
    public Term(int id, String func, double coeff, String horCoord, String description) {
        super();
        this.id = id;
        this.func = func;
        this.coeff = coeff;
        this.horCoord = horCoord;
        this.description = description;
    }

    @JsonProperty("Id")
    public int getId() {
        return id;
    }

    @JsonProperty("Id")
    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty("Func")
    public String getFunc() {
        return func;
    }

    @JsonProperty("Func")
    public void setFunc(String func) {
        this.func = func;
    }

    @JsonProperty("coeff")
    public double getCoeff() {
        return coeff;
    }

    @JsonProperty("coeff")
    public void setCoeff(double coeff) {
        this.coeff = coeff;
    }

    @JsonProperty("HorCoord")
    public String getHorCoord() {
        return horCoord;
    }

    @JsonProperty("HorCoord")
    public void setHorCoord(String horCoord) {
        this.horCoord = horCoord;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Term.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this)))
                .append('[');
        sb.append("id");
        sb.append('=');
        sb.append(this.id);
        sb.append(',');
        sb.append("func");
        sb.append('=');
        sb.append(((this.func == null) ? "<null>" : this.func));
        sb.append(',');
        sb.append("coeff");
        sb.append('=');
        sb.append(this.coeff);
        sb.append(',');
        sb.append("horCoord");
        sb.append('=');
        sb.append(((this.horCoord == null) ? "<null>" : this.horCoord));
        sb.append(',');
        sb.append("description");
        sb.append('=');
        sb.append(((this.description == null) ? "<null>" : this.description));
        sb.append(',');
        sb.append("additionalProperties");
        sb.append('=');
        sb.append(((this.additionalProperties == null) ? "<null>" : this.additionalProperties));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
