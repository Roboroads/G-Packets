package me.roboroads.gearth.gpackets.support;

public interface JsonSerializable {
    static <T> T fromJson(Class<T> self, String json) {
        return Json.parse(self, json);
    }

    default String toJson() {
        return Json.stringify(this);
    }
}
