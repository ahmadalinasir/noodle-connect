package ahmadalinasir.com.noodleconnectsample;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;

import java.util.Observable;
import java.util.Observer;

import ahmadalinasir.com.noodleconnect.BaseModel;
import ahmadalinasir.com.noodleconnect.ErrorModel;
import ahmadalinasir.com.noodleconnect.RestServiceClient;
import ahmadalinasir.com.noodleconnectsample.models.SampleModel;


@EActivity
public class MainActivity extends AppCompatActivity implements Observer {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String url = "http://ip.jsontest.com/";

    @Bean
    RestServiceClient restServiceClient;

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.ip);

        restServiceClient.callService(this, url, SampleModel.class, "GET", null, true);

    }

    @Override
    public void update(Observable observable, Object data) {

        BaseModel baseModel = ((BaseModel) data);

        if(baseModel instanceof ErrorModel){

            showAlertMessage("Sorry","Something went wrong! please try again");
            Log.d(TAG, ((ErrorModel) baseModel).getException());

        }else {

            if (baseModel instanceof SampleModel){

                textView.setText(((SampleModel) baseModel).getIp());
                Log.d(TAG, ((SampleModel) baseModel).getIp());
            }
        }
    }


    private void showAlertMessage(String title,String message){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton("OK", null);
        builder.show();
    }

}
