package com.apeng.filtpick.config.util;

import com.apeng.filtpick.config.gson.Adaptable;
import com.apeng.filtpick.config.gson.IllegalConfigStructure;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public record WidgetPosOffset(int xOffset, int yOffset) implements Adaptable<WidgetPosOffset> {

    private static final String X_OFFSET_NAME = "Horizontal Offset";
    private static final String Y_OFFSET_NAME = "Vertical Offset";

    public static final WidgetPosOffset DEFAULT = new WidgetPosOffset(0, 0);

    @Override
    public TypeAdapter<WidgetPosOffset> getTypeAdapter() {
        return new TypeAdapter<WidgetPosOffset>() {
            @Override
            public void write(JsonWriter out, WidgetPosOffset value) throws IOException {
                out.beginObject();
                out.name(X_OFFSET_NAME).value(xOffset);
                out.name(Y_OFFSET_NAME).value(yOffset);
                out.endObject();
            }

            @Override
            public WidgetPosOffset read(JsonReader in) throws IOException, IllegalConfigStructure {
                in.beginObject();
                checkNextName(in, X_OFFSET_NAME);
                int xOffset = in.nextInt();
                checkNextName(in, Y_OFFSET_NAME);
                int yOffset = in.nextInt();
                in.endObject();

                return new WidgetPosOffset(xOffset, yOffset);
            }

            private void checkNextName(JsonReader in, String name) throws IOException {
                if (!in.nextName().equals(name)) throw new IllegalConfigStructure("The config file content is illegal");
            }
        }.nullSafe();
    }
}
