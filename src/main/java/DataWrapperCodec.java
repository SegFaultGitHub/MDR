import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;

/**
 * Created by SegFault on 17/01/2017.
 */
public class DataWrapperCodec implements MessageCodec<DataWrapper, DataWrapper> {
    /*
    private String url;
    private String word;
    private double frequency;
    private ArrayList<Integer> positions;
     */
    @Override
    public void encodeToWire(Buffer buffer, DataWrapper datas) {
        JsonArray jsonArray = new JsonArray();
        datas.getDatas().forEach(data -> {
            JsonObject o = new JsonObject();
            o.put("url", data.getUrl());
            o.put("word", data.getWord());
            o.put("frequency", data.getFrequency());
            JsonArray positions = new JsonArray();
            data.getPositions().forEach(positions::add);
            o.put("positions", positions);
            jsonArray.add(o);
        });
        String jsonToStr = jsonArray.encode();
        int length = jsonToStr.getBytes().length;
        buffer.appendInt(length);
        buffer.appendString(jsonToStr);
    }

    @Override
    public DataWrapper decodeFromWire(int pos, Buffer buffer) {
        DataWrapper result = new DataWrapper(new ArrayList<>());
        int _pos = pos;
        int length = buffer.getInt(_pos);
        String jsonStr = buffer.getString(_pos += 4, _pos += length);
        JsonArray contentJson = new JsonArray(jsonStr);
        contentJson.forEach(obj -> {
            JsonObject jsonObject = (JsonObject) obj;
            ArrayList<Integer> positions = new ArrayList<>();
            jsonObject.getJsonArray("positions").forEach(dObj -> positions.add((int) dObj));
            result.add(new Data(
                    jsonObject.getString("url"),
                    jsonObject.getString("word"),
                    jsonObject.getDouble("frequency"),
                    positions
            ));
        });
        return result;
    }

    @Override
    public DataWrapper transform(DataWrapper datas) {
        return datas;
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
