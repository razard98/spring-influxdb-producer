package james.influxdb.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class GsStatusData {

    private int frame;
    private int cat;
    private int step;
    private int lockkey;
    private int shopcode;
    private int systype;
    private long time;
    private long online;
    private String ver;

    @Builder
    public GsStatusData(int frame, int cat, int step, int lockkey, int shopcode, int systype, long time, int online, String ver) {
        this.frame = frame;
        this.cat = cat;
        this.step = step;
        this.lockkey = lockkey;
        this.shopcode = shopcode;
        this.systype = systype;
        this.time = time;
        this.online = online;
        this.ver = ver;
    }
}
