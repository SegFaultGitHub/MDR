import lombok.Getter;

/**
 * Created by SegFault on 17/01/2017.
 */
public class CustomDocument {
    @Getter
    private String bodyText;
    @Getter
    private String location;

    public CustomDocument(String location_, String bodyText_) {
        location = location_;
        bodyText = bodyText_;
    }
}
