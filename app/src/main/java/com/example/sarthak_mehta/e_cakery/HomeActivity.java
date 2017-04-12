package com.example.sarthak_mehta.e_cakery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vinit_mehta on 3/28/2017.
 */
public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener{
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    private SwipeRefreshLayout swipeRefreshLayout;
    DrawerLayout drawer;
    View layout2;
    ViewGroup layout;
    String page;
    String city;
    Handler mHandler;
    Context context;
    List<View> views=new ArrayList<View>();
    private SwipeRefreshLayout mSwipeRefreshLayout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //Displays Home Screen
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        sharedpreferences.getString("city",city);
        page="packed";
        context=this;
        invokeWS("packed");
        this.mHandler = new Handler();
        this.mHandler.postDelayed(m_Runnable,120000);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.content_frame);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        Log.d("Toggle", "" + toggle);
        Log.d("Drawer", "" + drawer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }
    /**
     * Method which navigates from Home Activity to Main Activity
     */
    public void navigatetoMainActivity(){
        Intent homeIntent = new Intent(getApplicationContext(),MainActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    public void invokeWS(final String s) {

        // Make RESTful webservice call using AsyncHttpClient object

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("order_status", s);
        params.put("city",city);
        client.get("http://192.168.0.101:8080/E-Cakery/rest/webservices/getpackedorders",params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {


                try {
                    // JSON Object
                    JSONArray jArray = new JSONArray(response);
                    layout=(ViewGroup) findViewById(R.id.orders);

                    if(true) {
                        for (int i = 0; i < jArray.length(); i++) {
                            layout2 = LayoutInflater.from(context).inflate(R.layout.activity_orders, layout, false);
                            JSONObject json = jArray.getJSONObject(i);
                            TextView order_no = (TextView) layout2.findViewById(R.id.order_no);
                            layout2.setId(Integer.parseInt(json.getString("oid")));
                            TextView cust_name = (TextView) layout2.findViewById(R.id.cust_name);
                            TextView cust_address = (TextView) layout2.findViewById(R.id.cust_address);
                            TextView cust_mob = (TextView) layout2.findViewById(R.id.cust_mob);
                            TextView cust_pin = (TextView) layout2.findViewById(R.id.cust_pin);
                            TextView seller_name = (TextView) layout2.findViewById(R.id.seller_name);
                            TextView seller_address = (TextView) layout2.findViewById(R.id.seller_address);
                            TextView price = (TextView) layout2.findViewById(R.id.price);
                            TextView status = (TextView) layout2.findViewById(R.id.status);
                            TextView _status = (TextView) layout2.findViewById(R.id._status);
                            if(s.equals("packed")){
                                status.setText(json.getString("status"));
                            }
                            else{
                                 status.setVisibility(View.GONE);
                                _status.setVisibility(View.GONE);
                            }
                            order_no.setText(json.getString("oid"));
                            price.setText(json.getString("amount"));
                            cust_name.setText(json.getString("fname") + " " + json.getString("lname"));
                            cust_mob.setText(json.getString("mobile"));
                            cust_address.setText(json.getString("address"));
                            cust_pin.setText(json.getString("pincode"));
                            seller_name.setText(json.getJSONObject("seller").getString("name"));
                            seller_address.setText(json.getJSONObject("seller").getString("address"));
                            Button b = (Button) layout2.findViewById(R.id.button);
                            if(s.equals("packed")){
                                b.setText("Delivered");
                            }
                            else{
                                b.setText("Pending");
                            }
                            b.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    for(View view:views) {
                                        if(v.equals(view.findViewById(R.id.button))){
                                            invokeDeliveredWS(Integer.toString(view.getId()),s);
                                            if(s.equals("packed")){
                                                Toast.makeText(getApplicationContext(), "Order"+view.getId()+" Delivered", Toast.LENGTH_LONG).show();
                                            }else{
                                                Toast.makeText(getApplicationContext(), "Order"+view.getId()+" is Pending", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }
                                }
                            });
                            layout.addView(layout2);
                            views.add(layout2);
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
                    Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void invokeDeliveredWS(String s, final String status){
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("order_no", s);
        params.put("status",status);
        client.get("http://192.168.0.101:8080/E-Cakery/rest/webservices/update_status",params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {


                try {
                    // JSON Object
                    JSONObject obj = new JSONObject(response);
                    // When the JSON response has status boolean value assigned with true
                    if (obj.getBoolean("status")) {
                        if(status.equals("packed")){
                            layout.removeAllViews();
                            invokeWS("packed");
                        }else if(status.equals("delivered")){
                            layout.removeAllViews();
                            invokeWS("delivered");
                        }
                    } else {

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
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        if(layout!=null && layout.getChildCount()!=0) {
            layout.removeAllViews();
        }
        invokeWS(page);
        mSwipeRefreshLayout.setRefreshing(false);
        }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.nav_packed) {
            page="packed";
            if(layout!=null && layout.getChildCount()!=0) {
                layout.removeAllViews();
            }
            invokeWS("packed");

        }else if (id == R.id.nav_delivered) {
            page="delivered";
            if(layout!=null && layout.getChildCount()!=0) {
                layout.removeAllViews();
            }
            invokeWS("delivered");

        } else if (id == R.id.nav_logout) {
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.remove("email");
            editor.remove("city");
            editor.commit();
            finish();
            navigatetoMainActivity();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private final Runnable m_Runnable = new Runnable()
    {
        public void run()

        {
            if(layout!=null && layout.getChildCount()!=0) {
                layout.removeAllViews();
            }
            invokeWS(page);
            mHandler.postDelayed(m_Runnable,120000);
        }

    };

}
