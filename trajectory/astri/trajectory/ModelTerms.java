package astri.trajectory;

import java.util.LinkedHashMap;
import java.util.List;
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
        "date",
        "Telescope",
        "Terms"
})
@Generated("jsonschema2pojo")
public class ModelTerms {

    @JsonProperty("date")
    private String date;
    @JsonProperty("Telescope")
    private String telescope;
    @JsonProperty("Terms")
    private List<Term> terms;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public ModelTerms() {
    }

    /**
     *
     * @param date
     * @param terms
     * @param telescope
     */
    public ModelTerms(String date, String telescope, List<Term> terms) {
        super();
        this.date = date;
        this.telescope = telescope;
        this.terms = terms;
    }

    @JsonProperty("date")
    public String getDate() {
        return date;
    }

    @JsonProperty("date")
    public void setDate(String date) {
        this.date = date;
    }

    @JsonProperty("Telescope")
    public String getTelescope() {
        return telescope;
    }

    @JsonProperty("Telescope")
    public void setTelescope(String telescope) {
        this.telescope = telescope;
    }

    @JsonProperty("Terms")
    public List<Term> getTerms() {
        return terms;
    }

    @JsonProperty("Terms")
    public void setTerms(List<Term> terms) {
        this.terms = terms;
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
        sb.append(ModelTerms.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this)))
                .append('[');
        sb.append("date");
        sb.append('=');
        sb.append(((this.date == null) ? "<null>" : this.date));
        sb.append(',');
        sb.append("telescope");
        sb.append('=');
        sb.append(((this.telescope == null) ? "<null>" : this.telescope));
        sb.append(',');
        sb.append("terms");
        sb.append('=');
        sb.append(((this.terms == null) ? "<null>" : this.terms));
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
