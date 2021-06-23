package net.square.intect.utils.objectable;

import lombok.Data;

@Data
public class BanObject {

    private long banTime;
    private String reason;
    private String id;

    public BanObject(long time, String reason, String id) {
        this.banTime = time;
        this.reason = reason;
        this.id = id;
    }
}
