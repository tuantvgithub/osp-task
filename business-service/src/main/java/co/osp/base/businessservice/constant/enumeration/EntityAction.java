package co.osp.base.businessservice.constant.enumeration;

import co.osp.base.businessservice.entity.KeyValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum EntityAction {
    CREATE("TẠO MỚI"),
    UPDATE("CẬP NHẬT"),
    DELETE("XÓA"),
    VIEW("XEM");

    public final String label;

    private EntityAction(String label) {
        this.label = label;
    }

    public static final List<KeyValue> list = new ArrayList<>();

    public static final Map<String, String> map = new HashMap<>();

    static {
        for (EntityAction e : values()) {
            list.add(new KeyValue().key(e.toString()).value(e.label));
            map.put(e.toString(),e.label);
        }
    }
}
