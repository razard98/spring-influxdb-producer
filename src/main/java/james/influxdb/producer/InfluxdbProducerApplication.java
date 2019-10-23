package james.influxdb.producer;

import james.influxdb.model.GsStatusData;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@Slf4j
public class InfluxdbProducerApplication {

    private final static String dbName = "gs_monitor";
    private final static String measurement = "gs_status_data";

    private static Thread sendTask = new Thread();
    private static volatile boolean stopTask = false;

    public static void main(String[] args) {
        log.debug("===== start producer");
        sendTask = new Thread(() -> {

            InfluxDB influxDB = InfluxDBFactory.connect("http://172.20.40.73:8086", "amdin", "admin");
            influxDB.setDatabase(dbName);
            while (!stopTask) {


                int frame = getRandomIntRange(2, 10); //2 to 5
                int lockkey = getRandomIntRange(1, 10); //1 to 10

                GsStatusData data = GsStatusData.builder()
                        .time(System.currentTimeMillis())
                        .frame(frame)
                        .cat(frame)
                        .step(getRandomIntRange(1, 10)) //1 to 10
                        .lockkey(lockkey)
                        .online(1)
                        .shopcode(lockkey)
                        .systype(getRandomIntRange(1, 2))
                        .ver("ver:" + System.currentTimeMillis())
                        .build();

                log.info(data.toString());

                influxDB.write(Point.measurement(measurement)
                        .time(data.getTime(), TimeUnit.MILLISECONDS)
                        .addField("frame", data.getFrame())
                        .addField("cat", data.getCat())
                        .addField("step", data.getStep())
                        .addField("steplabel", getStepLabel(data.getFrame(), data.getStep()))
                        .addField("lockkey", data.getLockkey())
                        .addField("online", data.getOnline())
                        .addField("shopcode", data.getShopcode())
                        .addField("systype", data.getSystype())
                        .addField("ver", data.getVer())
                        .build());

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            influxDB.close();
        });
        sendTask.start();
        ConfigurableApplicationContext cac =
                SpringApplication.run(InfluxdbProducerApplication.class, args);

        cac.addApplicationListener(applicationEvent -> {
            stopTask = true;
            try {
                sendTask.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private static int getRandomIntRange(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    private static String getStepLabel(int frame, int step) {

        if (step == 1) {
            return "게임번호 요청";
        } else if (step == 2) {
            return "게임번호 수신";
        } else if (step == 3) {
            return "인트로 화면 유저 비교 통계 요청";

        } else if (step == 4) {
            return "인트로 화면 유저 비교 통계 수신";

        } else if (step == 5) {
            return "인트로 화면 유저 CC 통계 요청";

        } else if (step == 6) {
            return "인트로 화면 유저 CC 통계 수신";
        }

        return "기타";
    }
}
