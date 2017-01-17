import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonObject;
import org.jsoup.nodes.Document;

/**
 * Created by SegFault on 17/01/2017.
 */
public class DocumentCodec implements MessageCodec<Document, CustomDocument> {
    @Override
    public void encodeToWire(Buffer buffer, Document document) {
        JsonObject jsonToEncode = new JsonObject();
        jsonToEncode.put("body-text", document.body().text());
        jsonToEncode.put("location", document.location());
        String jsonToStr = jsonToEncode.encode();
        int length = jsonToStr.getBytes().length;
        buffer.appendInt(length);
        buffer.appendString(jsonToStr);
    }

    @Override
    public CustomDocument decodeFromWire(int pos, Buffer buffer) {
        int _pos = pos;
        int length = buffer.getInt(_pos);
        String jsonStr = buffer.getString(_pos += 4 , _pos += length);
        JsonObject contentJson = new JsonObject(jsonStr);
        String location = contentJson.getString("location");
        String bodyText = contentJson.getString("body-text");
        return new CustomDocument(location, bodyText);
    }

    @Override
    public CustomDocument transform(Document document) {
        return new CustomDocument(
                (document == null) ? null : document.location(),
                (document == null || document.body() == null) ? null : document.body().text()
        );
    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }
}
