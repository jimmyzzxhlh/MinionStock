package util.gson;

import java.io.IOException;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * An adapter for gson to convert an empty string to a null for a double type property. 
 */
public class DoubleTypeAdapter extends TypeAdapter<Double> {

  @Override
  public void write(JsonWriter out, Double value)
      throws IOException {
    out.value(value);
  }

  @Override
  public Double read(JsonReader in) throws IOException {
    if (in.peek() == JsonToken.NULL) {
      in.nextNull();
      return null;
    }
    try {
      String result = in.nextString();
      if ("".equals(result)) {
        return null;
      }
      return Double.parseDouble(result);
    } catch (NumberFormatException e) {
      throw new JsonSyntaxException(e);
    }
  }
}
