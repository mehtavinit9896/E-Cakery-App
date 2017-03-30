package com.example.sarthak_mehta.e_cakery;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;

/**
 * Created by vinit_mehta on 3/28/2017.
 */
public class HomeActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //Displays Home Screen
        invokeWS(this);
    }

    public void invokeWS(final Context context) {

        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://192.168.0.101:8080/E-Cakery/rest/webservices/getpackedorders", new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {


                try {
                    // JSON Object
                    JSONArray jArray = new JSONArray(response);

                    ViewGroup layout=(ViewGroup) findViewById(R.id.orders);

                    if(true) {
                        for (int i = 0; i < jArray.length(); i++) {
                            View layout2 = LayoutInflater.from(context).inflate(R.layout.activity_orders, layout, false);
                            JSONObject json = jArray.getJSONObject(i);
                            TextView order_no = (TextView) layout2.findViewById(R.id.order_no);
                            TextView cust_name = (TextView) layout2.findViewById(R.id.cust_name);
                            TextView cust_address = (TextView) layout2.findViewById(R.id.cust_address);
                            TextView cust_mob = (TextView) layout2.findViewById(R.id.cust_mob);
                            TextView cust_pin = (TextView) layout2.findViewById(R.id.cust_pin);
                            TextView seller_name = (TextView) layout2.findViewById(R.id.seller_name);
                            TextView seller_address = (TextView) layout2.findViewById(R.id.seller_address);
                            TextView price = (TextView) layout2.findViewById(R.id.price);
                            order_no.setText(json.getString("oid"));
                            price.setText(json.getString("amount"));
                            cust_name.setText(json.getString("fname") + "" + json.getString("lname"));
                            cust_mob.setText(json.getString("mobile"));
                            cust_address.setText(json.getString("address"));
                            cust_pin.setText(json.getString("pincode"));
                            seller_name.setText(json.getJSONObject("seller").getString("name"));
                            seller_address.setText(json.getJSONObject("seller").getString("address"));
                            layout.addView(layout2);
                        }

                    }else{

                    }
                    // When the JSON response has status boolean value assigned with true

                    // Else display error message
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }
            }

            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {


                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
