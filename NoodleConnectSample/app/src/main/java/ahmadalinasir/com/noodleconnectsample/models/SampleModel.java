package ahmadalinasir.com.noodleconnectsample.models;

import ahmadalinasir.com.noodleconnect.BaseModel;

/**
 * Created by ahmadalinasir on 08/03/16.
 *
 */

public class SampleModel extends BaseModel {

    //URL: http://ip.jsontest.com/

    String ip;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
