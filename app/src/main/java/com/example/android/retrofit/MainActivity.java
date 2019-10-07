package com.example.android.retrofit;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.retrofit.Model.RetroModel;
import com.example.android.retrofit.Rest.ApiInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView =  (TextView) findViewById(R.id.tv);
        getResponse();
    }

    private void getResponse(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiInterface.JSONURL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        ApiInterface api = retrofit.create(ApiInterface.class);
        Call<String> call = api.getString();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.i("Responsestring", response.body().toString());
                if(response.isSuccessful()){
                    if(response.body()!=null){
                        Log.i("onSucess",response.body().toString());
                        String jsonresponse = response.body().toString();
                        writeTv(jsonresponse);
                    }
                }
                else {
                    Log.i("onEmptyResponse", "Returned empty response");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void writeTv(String response){
        try{
            JSONObject obj = new JSONObject(response);
            if(obj.optString("status").equals("true")){

                ArrayList<RetroModel> retroModelArrayList = new ArrayList<>();
                JSONArray dataArray = obj.getJSONArray("data");
                for(int i=0;i<dataArray.length();i++){
                    RetroModel retroModel = new RetroModel();
                    JSONObject dataobj = dataArray.getJSONObject(i);
                    retroModel.setid(dataobj.getString("id"));
                    retroModel.setName(dataobj.getString("name"));
                    retroModel.setCountry(dataobj.getString("country"));
                    retroModel.setCity(dataobj.getString("city"));
                    retroModelArrayList.add(retroModel);
                }


                for (int j = 0; j < retroModelArrayList.size(); j++){
                    textView.setText(textView.getText()+ retroModelArrayList.get(j).getid()+ " "+ retroModelArrayList.get(j).getName()
                            + " "+ retroModelArrayList.get(j).getCountry()+ " "+retroModelArrayList.get(j).getCity()+" \n");
                }
            }
            else {
                Toast.makeText(MainActivity.this, obj.optString("message")+"", LENGTH_SHORT).show();
            }
        }
        catch (JSONException e) {
         e.printStackTrace();
        }
    }
}