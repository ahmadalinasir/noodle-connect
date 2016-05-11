package ahmadalinasir.com.noodleconnect;

import android.content.Context;
import android.util.Log;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.UiThread;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.OkHttpClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by ahmadalinasir on 7/21/15.
 *
 */

@EBean(scope = EBean.Scope.Default)
public class RestServiceClient  extends Observable {

    @SuppressWarnings("FieldCanBeLocal")
    private String URL = "";
    @SuppressWarnings("FieldCanBeLocal")
    private Object result;
    private String timeStamp;
    @SuppressWarnings("FieldCanBeLocal")
    private SimpleDateFormat dateFormat;
    @SuppressWarnings("FieldCanBeLocal")
    private String previousTimeStamp = "0000-00-00 00:00:00";
    private String request;
    @SuppressWarnings("FieldCanBeLocal")
    private NetworkStateManager networkStateManager;
    private DataSource ncDataSource;


    @RootContext
    Context context;


    @Background
    public void callService(Observer backCommunicator, String params, Class response, String responseType, MultiValueMap<String,String> map){

        SQLiteHelper.getInstance(context);

        networkStateManager = new NetworkStateManager();
        ncDataSource = new DataSource(context);

        addObserver(backCommunicator);
        request = "";
        request = URL+params;

        ncDataSource.open();
        result = new Object();

        try
        {
            if(!networkStateManager.isOnline(context) || isCacheValid()){

                result = deserializeObject(ncDataSource.getModel(request));
            }
            else if(networkStateManager.isOnline(context)){

                if(isCacheValid()) {

                    result = deserializeObject(ncDataSource.getModel(request));
                }
                else if(!isCacheValid()){

                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

                    byte[] bArray = new byte[0];

                    try{

                        if(responseType!=null&&responseType.equals("POST")){
                            result = restTemplate.postForObject(request,map,String.class);
                            System.out.print("");
                        }else{
                            result =  restTemplate.getForObject(request, response);
                        }
                        bArray = serializeObject(result);

                    }catch (Exception e){
                        ErrorModel err = new ErrorModel();
                        err.setStatus(-1);
                        err.setException(e.toString());
                        result = err;
                    }

                    if(result != null ){
                        ncDataSource.add(200, request, timeStamp,bArray);

                    }

                }
            }
            else if(!networkStateManager.isOnline(context) || !isCacheValid()){
                ErrorModel err = new ErrorModel();
                err.setStatus(-2);
                err.setException("Network Error");
                result = err;
            }
        }

        catch (Exception e)
        {   ErrorModel err = new ErrorModel();
            err.setStatus(-1);
            err.setException("Network Error");
            result = err;
        }

        updateUi(result);

    }

    @Background
    public void callService(Observer backCommunicator, String params, Class response, String responseType, MultiValueMap<String,String> map, boolean cacheResponse){

        networkStateManager = new NetworkStateManager();

        addObserver(backCommunicator);
        request = "";
        request = URL+params;
        result = new Object();

        if (map != null) {
            Log.d("Params", params + map.toString());
        } else {
            Log.d("Params", params);
        }

        try
        {
            if(networkStateManager.isOnline(context)){

                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
                restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
                restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

                try{

                    if(responseType!=null&&responseType.equals("POST")){
                        HttpHeaders requestHeaders = new HttpHeaders();
                        requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                        OkHttpClientHttpRequestFactory requestFactory = new OkHttpClientHttpRequestFactory();

                        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(map, requestHeaders);

                        restTemplate.setRequestFactory(requestFactory);
                        result = restTemplate.exchange(request, HttpMethod.POST, requestEntity, response).getBody();
                        Log.e("resultPost", String.valueOf(result));
                    }else{
                        result =  restTemplate.getForObject(request, response);
                        Log.e("resultPost", String.valueOf(result));
                    }

                }catch (Exception e){
                    ErrorModel err = new ErrorModel();
                    err.setStatus(-1);
                    err.setException(e.toString());
                    result = err;
                }

            }else {

                ErrorModel err = new ErrorModel();
                err.setStatus(-2);
                err.setException("Network Error");
                result = err;
            }
        }

        catch (Exception e)
        {   ErrorModel err = new ErrorModel();
            err.setStatus(-1);
            err.setException("Network Error");
            result = err;
        }

        updateUi(result);

    }

    @UiThread
    public void updateUi(Object result){

        Log.i("in Ui update:...", String.valueOf(result));
        RestServiceClient.this.setChanged();
        RestServiceClient.this.notifyObservers(result);
        RestServiceClient.this.deleteObservers();

    }

    private Object deserializeObject (byte[] byteArray) throws IOException, ClassNotFoundException {

        Object obj;
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
        ObjectInputStream objectInputS = new ObjectInputStream(byteArrayInputStream);
        obj = objectInputS.readObject();
        objectInputS.close();

        return obj;
    }
    private byte[] serializeObject(Object obj) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try{

            ObjectOutput objectOutput = new ObjectOutputStream(byteArrayOutputStream);
            objectOutput.writeObject((BaseModel)obj);
            objectOutput.close();
            return byteArrayOutputStream.toByteArray();

        }catch(IOException ioe){

            Log.e("serializeObject", "error" + ioe);

            return null;
        }

    }

    private boolean isCacheValid() {

        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        timeStamp = dateFormat.format(new Date());
        previousTimeStamp = ncDataSource.getTimeStamp(request);
        long timeDifference = 0;

        try {
            timeDifference = dateFormat.parse(timeStamp).getTime() - dateFormat.parse(previousTimeStamp).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeDifference < 6;
    }


}
