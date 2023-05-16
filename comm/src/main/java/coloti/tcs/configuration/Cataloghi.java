package coloti.tcs.configuration;

//import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "dir"
})
//@Generated("jsonschema2pojo")
public class Cataloghi {

    @JsonProperty("dir")
    private String dir;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Cataloghi() {
    }

    /**
     * 
     * @param dir
     */
    public Cataloghi(String dir) {
        super();
        this.dir = dir;
    }

    @JsonProperty("dir")
    public String getDir() {
        return dir;
    }

    @JsonProperty("dir")
    public void setDir(String dir) {
        this.dir = dir;
    }

    public Cataloghi withDir(String dir) {
        this.dir = dir;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Cataloghi.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("dir");
        sb.append('=');
        sb.append(((this.dir == null)?"<null>":this.dir));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

}
