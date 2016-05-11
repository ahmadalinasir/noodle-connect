**noodle-connect**
------------------

An easy to use open source rest-client based upon **Façade design pattern** which is cooked using the best of ingredients

 - Android Annotations (https://github.com/excilys/androidannotations)
 - OkHttp (http://square.github.io/okhttp)
 - GSON (https://github.com/google/gson)
 - Spring Android Rest Template (http://projects.spring.io/spring-android)

----------

**How it works ?**
------------------

Step 1
------

 - Use `@EActivity` annotation for your Activity classes or `@EFragment`
   annotation or your Fragment classes.
 - Define POJOs based upon your JSON response.
 - Add a Bean for the RestServiceClient class 
 `@Bean 
 RestServiceClient restServiceClient;`

Step 2
------
Implement Observable interface in your Activity/Fragment and add the following method

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

Step 3
------
For **GET** Request just use the following 1 line:

    restServiceClient.callService(this, url, SampleModel.class, "GET", null, true);

**callService method PARAMS:**

 1. `Observer` backCommunicator
 2. `String` params
 3. `Class` response
 4. `String` responseType
 5. `MultiValueMap<String,String>` map
 6. `boolean` cacheResponse

----------

For **POST** Request follow the below mentioned steps:

 1. Define a MultiValueMap

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("first-name", firstName);
        map.add("last-name", lastName);
 

 2. User the following 1 line for posting a request:

          restServiceClient.callService(this, url, SamplePOSTModel.class, "POST", map, true);
