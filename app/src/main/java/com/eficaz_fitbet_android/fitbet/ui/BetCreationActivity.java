package com.eficaz_fitbet_android.fitbet.ui;

import android.content.Context;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Space;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;


import com.android.billingclient.api.BillingClient;
import com.eficaz_fitbet_android.fitbet.R;
import com.eficaz_fitbet_android.fitbet.customview.CustomProgress;
import com.eficaz_fitbet_android.fitbet.network.Constant;
import com.eficaz_fitbet_android.fitbet.network.RetroClient;
import com.eficaz_fitbet_android.fitbet.network.RetroInterface;
import com.eficaz_fitbet_android.fitbet.utils.AppPreference;
import com.eficaz_fitbet_android.fitbet.utils.Contents;
import com.eficaz_fitbet_android.fitbet.utils.Utils;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.eficaz_fitbet_android.fitbet.utils.Contents.DASH_BOARD_CREDIT_SCORE;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.DASH_BOARD_USERS;

public class BetCreationActivity extends BaseActivity {





    @Bind(R.id.bet_from_date)
    TextView bet_from_date;


    @Bind(R.id.btn_back)
    TableRow btn_back;

    @Bind(R.id.date_pic_from)
    TableRow date_pic_from;

    @Bind(R.id.bet_to_date)
    TextView bet_to_date;

    @Bind(R.id.bet_distance_by_location)
    TextView bet_distance_by_location;

    @Bind(R.id.bet_distance_by_km)
    TextView bet_distance_by_km;

    @Bind(R.id.view1)
    View view1;

    @Bind(R.id.view2)
    View view2;

    @Bind(R.id.btnCreateBet)
    Button btnCreateBet;

    @Bind(R.id.edBet_km)
    EditText edBet_km;

    @Bind(R.id.bet_km)
    TextView bet_km;

    @Bind(R.id.from_address)
    TextView from_address;

    @Bind(R.id.to_address)
    TextView to_address;

    @Bind(R.id.bet_name)
    EditText bet_name;

    @Bind(R.id.bet_credit)
    EditText bet_credit;

    @Bind(R.id.bet_description)
    EditText bet_description;

    @Bind(R.id.toRow)
    TableRow toRow;

    @Bind(R.id.fromRow)
    TableRow fromRow;

    @Bind(R.id.space)
    Space space;


    String fromDate="",toDate="";

    String new_todate="";

    String distance="0";

    SimpleDateFormat sdf;
    SimpleDateFormat sdf1;


    boolean fromORto=false;

    boolean distanceKm=false;

    boolean distanceLocation=false;


    String credits="0";

    String startLat;
    String startLog;
    String endLat;
    String endLog;
    String distance_draw;
    String overview_polyline;
    int kmInDec=0;
    double meter;
    private LocationManager mLocationManager;

    private int REQUEST_CHECK_SETTINGS=111;

    //Bundle bundle;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bet_creation);
        ButterKnife.bind(this);
        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        startLat= bundle.getString(Contents.pass_startlatitude);
        startLog= bundle.getString(Contents.pass_startlongitude);
        endLat= bundle.getString(Contents.pass_endlatitude);
        endLog= bundle.getString(Contents.pass_endlongitude);
        distance_draw= bundle.getString(Contents.MYBETS_distance);
        overview_polyline= bundle.getString(Contents.OVERVIEW_POLYLINE);
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //distanceKm=true;
        showKeyboard(bet_name);
        edBet_km.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                edBet_km.getWindowVisibleDisplayFrame(r);
                int screenHeight = edBet_km.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;
                if (keypadHeight > screenHeight * 0.15) {
                    space.setVisibility(View.VISIBLE);
                } else {
                    space.setVisibility(View.GONE);
                }
            }
        });
        edBet_km.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 1) {
                    if(edBet_km.getText().toString().startsWith("0")){
                        edBet_km.setText("");
                    }
                    else if(edBet_km.getText().toString().startsWith(".00")){
                        edBet_km.setText("");
                    }
                    else if(edBet_km.getText().toString().startsWith("0.")){
                        edBet_km.setText("");
                    }
                }
                if (charSequence.length() >= 0) {
                    if (!edBet_km.getText().toString().trim().equals("")) {
                        space.setVisibility(View.VISIBLE);
                        //space1.setVisibility(View.VISIBLE);
                    }
                }else{
                    space.setVisibility(View.GONE);
                    //space1.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
                if (editable.length() >= 1) {
                    if(edBet_km.getText().toString().startsWith("0")){
                        edBet_km.setText("");
                    }
                    else if(edBet_km.getText().toString().startsWith(".00")){
                        edBet_km.setText("");
                    }
                    else if(edBet_km.getText().toString().startsWith("0.")){
                        edBet_km.setText("");
                    }
                }
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppPreference.getPrefsHelper().savePref(Contents.BAT_POSICTION, "2");

                AppPreference.getPrefsHelper().savePref(Contents.DASH_BOARD_POSICTION, "1");
                AppPreference.getPrefsHelper().savePref(Contents.BET_PAGE_POSICTION, "1");

                Intent intent = new Intent(BetCreationActivity.this, DashBoardActivity.class);
                startActivity(intent);
                finish();
                CallInappPurchass();
            }
             });
                int Radius = 6371;
                double lat1 = Double.parseDouble(startLat);
                double lat2 = Double.parseDouble(endLat);
                double lon1 = Double.parseDouble(startLog);
                double lon2 = Double.parseDouble(endLog);
                double dLat = Math.toRadians(lat2 - lat1);
                double dLon = Math.toRadians(lon2 - lon1);
                double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                        + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                        * Math.sin(dLon / 2);
                double c = 2 * Math.asin(Math.sqrt(a));
                double valueResult = Radius * c;
                double km = valueResult / 1;
                DecimalFormat newFormat = new DecimalFormat("####");
                kmInDec = Integer.valueOf(newFormat.format(km));
                meter = valueResult % 1000;
                int meterInDec = Integer.valueOf(newFormat.format(meter));
                //Toast.makeText(BetCreationActivity.this, "Radius Value"+ "" + valueResult + "   KM  " + kmInDec + " Meter   " + meterInDec, Toast.LENGTH_LONG).show();
            if(startLat.equals("0")){
                edBet_km.setVisibility(View.VISIBLE);
                bet_km.setVisibility(View.GONE);
                fromRow.setVisibility(View.GONE);
                toRow.setVisibility(View.GONE);
                distanceKm=true;
                distanceLocation=false;
                bet_distance_by_km.setTextColor(getResources().getColor(R.color.light_blue));
                bet_distance_by_location.setTextColor(getResources().getColor(R.color.hint_text_color));
                view1.setBackgroundColor(getResources().getColor(R.color.light_blue));
                view2.setBackgroundColor(getResources().getColor(R.color.hint_text_color));
            }else{
                distanceKm=false;
                distanceLocation=true;
                edBet_km.setVisibility(View.GONE);
                bet_km.setVisibility(View.VISIBLE);
                fromRow.setVisibility(View.VISIBLE);
                toRow.setVisibility(View.VISIBLE);
                view1.setBackgroundColor(getResources().getColor(R.color.hint_text_color));
                view2.setBackgroundColor(getResources().getColor(R.color.light_blue));
                bet_distance_by_km.setTextColor(getResources().getColor(R.color.hint_text_color));
                bet_distance_by_location.setTextColor(getResources().getColor(R.color.light_blue));
                distanceKm=false;
                distanceLocation=true;
                double dis= Double.parseDouble(distance_draw.replace("km","").replace("m",""))/1000;
                bet_km.setText(""+dis+"km");
            }
            bet_name.setText(AppPreference.getPrefsHelper().getPref(Contents.MYBETS_betname,""));
            bet_credit.setText(AppPreference.getPrefsHelper().getPref(Contents.CREDIT_SCORE,""));
            if(!AppPreference.getPrefsHelper().getPref(Contents.START_DATE,"").equals("")){
                try{
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                    df.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date date = df.parse(AppPreference.getPrefsHelper().getPref(Contents.START_DATE,""));
                    df.setTimeZone(TimeZone.getDefault());
                    String formattedDate = df.format(date);
                    DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
                    String output = null;
                    output = outputformat.format(date);
                    bet_from_date.setText(output);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            bet_to_date.setText(AppPreference.getPrefsHelper().getPref(Contents.MYBETS_enddate,""));
            fromDate=AppPreference.getPrefsHelper().getPref(Contents.START_DATE,"");
            toDate=AppPreference.getPrefsHelper().getPref(Contents.MYBETS_enddate,"");
            bet_description.setText(AppPreference.getPrefsHelper().getPref(Contents.MYBETS_description,""));
                try{
                    Geocoder geocoder;
                    List<Address> start_addresses;
                    List<Address> end_addresses;
                    geocoder = new Geocoder(this, Locale.getDefault());
                    start_addresses = geocoder.getFromLocation(Double.valueOf(startLat), Double.valueOf(startLog), 1);
                    end_addresses = geocoder.getFromLocation(Double.valueOf(endLat), Double.valueOf(endLog), 1);
                    String start_address = start_addresses.get(0).getAddressLine(0);
                    String start_city = start_addresses.get(0).getLocality();
                    String start_state = start_addresses.get(0).getAdminArea();
                    String start_country = start_addresses.get(0).getCountryName();
                    String start_postalCode = start_addresses.get(0).getPostalCode();
                    String start_knownName = start_addresses.get(0).getFeatureName();
                    from_address.setVisibility(View.VISIBLE);
                    from_address.setText(start_address);
                    String end_address = end_addresses.get(0).getAddressLine(0);
                    String end_city = end_addresses.get(0).getLocality();
                    String end_state = end_addresses.get(0).getAdminArea();
                    String end_country = end_addresses.get(0).getCountryName();
                    String end_postalCode = end_addresses.get(0).getPostalCode();
                    String end_knownName = end_addresses.get(0).getFeatureName();
                    to_address.setVisibility(View.VISIBLE);
                    to_address.setText(end_address);
                }catch (Exception e){
                    e.printStackTrace();
                }
        callDashboardDetailsApi();
        date_pic_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromORto=true;
                new SingleDateAndTimePickerDialog.Builder(BetCreationActivity.this)
                        .bottomSheet()
                        .curved()
                        .displayMinutes(true)
                        .minutesStep(1)
                        .displayHours(true)
                        .displayDays(true)
                        .displayMonth(true)
                        .displayYears(true)
                        .mainColor(getResources().getColor(R.color.light_blue))
                        .backgroundColor(getResources().getColor(R.color.pading_gray))
                        .titleTextColor(getResources().getColor(R.color.light_blue))
                        .title(getResources().getString(R.string.select_date_time))
                        .listener(new SingleDateAndTimePickerDialog.Listener() {
                            @Override
                            public void onDateSelected(Date date) {
                                sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                                try{
                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                                    //df.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    // Date date1 = df.parse(String.valueOf(date));
                                    df.setTimeZone(TimeZone.getDefault());
                                    String formattedDate = df.format(date);
                                    DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
                                    String output = null;
                                    output = outputformat.format(date);

                                    if(fromORto==true){
                                        fromDate=sdf.format(date);
                                        bet_from_date.setText(""+output);
                                    } else{
                                        toDate=sdf.format(date);
                                        bet_to_date.setText(""+output);
                                    }
                                    DateFormat justDay = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    justDay.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    Date thisMorningMidnight = justDay.parse(fromDate);
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(thisMorningMidnight);
                                    cal.set(Calendar.MILLISECOND, 0);
                                    cal.set(Calendar.SECOND, 59);
                                    cal.set(Calendar.MINUTE, 59);
                                    cal.set(Calendar.HOUR_OF_DAY, 23);
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String cal_date = sdf.format(cal.getTime());
                                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    toDate=cal_date;
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        })
                        .display();
            }
        });
        bet_from_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromORto=true;
                Date currentTime = Calendar.getInstance().getTime();
                new SingleDateAndTimePickerDialog.Builder(BetCreationActivity.this)
                        .bottomSheet()
                        .curved()
                        .defaultDate(currentTime)
                        .displayMinutes(true)
                        .minutesStep(1)
                        .displayHours(true)
                        .displayDays(true)
                        .displayMonth(true)
                        .displayYears(true)
                        .mainColor(getResources().getColor(R.color.light_blue))
                        .backgroundColor(getResources().getColor(R.color.pading_gray))
                        .titleTextColor(getResources().getColor(R.color.light_blue))
                        .title(getResources().getString(R.string.select_date_time))
                        .listener(new SingleDateAndTimePickerDialog.Listener() {
                            @Override
                            public void onDateSelected(Date date) {
                                sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                                try{
                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                                    //df.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    //Date date1 = df.parse(String.valueOf(date));
                                    df.setTimeZone(TimeZone.getDefault());
                                    String formattedDate = df.format(date);
                                    DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
                                    String output = null;
                                    output = outputformat.format(date);
                                    Date strDate = outputformat.parse(output);
                                    if (System.currentTimeMillis() > strDate.getTime()) {
                                        Utils.showCustomToastMsg(BetCreationActivity.this, R.string.please_choose_after_date);
                                        fromDate="";
                                        bet_from_date.setText("");
                                        toDate="";
                                        bet_to_date.setText("");
                                        //catalog_outdated = 1;
                                        fromORto=true;
                                        Date currentTime = Calendar.getInstance().getTime();
                                        new SingleDateAndTimePickerDialog.Builder(BetCreationActivity.this)
                                                .bottomSheet()
                                                .curved()
                                                .defaultDate(currentTime)
                                                .displayMinutes(true)
                                                .displayHours(true)
                                                .displayDays(true)
                                                .displayMonth(true)
                                                .displayYears(true)
                                                .mainColor(getResources().getColor(R.color.light_blue))
                                                .backgroundColor(getResources().getColor(R.color.pading_gray))
                                                .titleTextColor(getResources().getColor(R.color.light_blue))
                                                .title(getResources().getString(R.string.select_date_time))
                                                .listener(new SingleDateAndTimePickerDialog.Listener() {
                                                    @Override
                                                    public void onDateSelected(Date date) {
                                                        sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                                                        try{
                                                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                                                            //df.setTimeZone(TimeZone.getTimeZone("UTC"));
                                                            //Date date1 = df.parse(String.valueOf(date));
                                                            df.setTimeZone(TimeZone.getDefault());
                                                            String formattedDate = df.format(date);

                                                            DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
                                                            String output = null;
                                                            output = outputformat.format(date);
                                                            Date strDate = outputformat.parse(output);


                                                            if (System.currentTimeMillis() > strDate.getTime()) {
                                                                Utils.showCustomToastMsg(BetCreationActivity.this, R.string.please_choose_after_date);
                                                                fromDate="";
                                                                bet_from_date.setText("");
                                                                toDate="";
                                                                bet_to_date.setText("");
                                                                //catalog_outdated = 1;
                                                            }else{
                                                                if(fromORto==true){
                                                                    fromDate=sdf.format(date);
                                                                    bet_from_date.setText(""+output);
                                                                } else{
                                                                    toDate=sdf.format(date);
                                                                    bet_to_date.setText(""+output);
                                                                }
                                                            }
                                                            DateFormat justDay = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                            justDay.setTimeZone(TimeZone.getTimeZone("UTC"));
                                                            Date thisMorningMidnight = justDay.parse(fromDate);
                                                            Calendar cal = Calendar.getInstance();
                                                            cal.setTime(thisMorningMidnight);
                                                            cal.set(Calendar.MILLISECOND, 0);
                                                            cal.set(Calendar.SECOND, 59);
                                                            cal.set(Calendar.MINUTE, 59);
                                                            cal.set(Calendar.HOUR_OF_DAY, 23);
                                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                                                            String cal_date = sdf.format(cal.getTime());
                                                            toDate=cal_date;

                                                        }catch (Exception e){
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                })
                                                .display();

                                    }else{
                                        if(fromORto==true){
                                            fromDate=sdf.format(date);
                                            bet_from_date.setText(""+output);
                                        } else{
                                            toDate=sdf.format(date);
                                            bet_to_date.setText(""+output);
                                        }
                                    }
                                        DateFormat justDay = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        justDay.setTimeZone(TimeZone.getTimeZone("UTC"));
                                        Date thisMorningMidnight = justDay.parse(fromDate);
                                        Calendar cal = Calendar.getInstance();

                                        cal.setTime(thisMorningMidnight);
                                        cal.set(Calendar.MILLISECOND, 0);
                                        cal.set(Calendar.SECOND, 59);
                                        cal.set(Calendar.MINUTE, 59);
                                        cal.set(Calendar.HOUR_OF_DAY, 23);
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                                        String cal_date = sdf.format(cal.getTime());
                                        toDate=cal_date;

                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }
                        })
                        .display();
            }
        });
        bet_distance_by_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(BetCreationActivity.this)) {
                    Log.e("fitbet","Gps not enabled");
    createLocationRequest();
                }else{
                    //CustomProgress.getInstance().showProgress(BetCreationActivity.this, "", false);
                    AppPreference.getPrefsHelper().savePref(Contents.MYBETS_betname, bet_name.getText().toString());
                    AppPreference.getPrefsHelper().savePref(Contents.CREDIT_SCORE, bet_credit.getText().toString());
                    AppPreference.getPrefsHelper().savePref(Contents.START_DATE, fromDate);
                    AppPreference.getPrefsHelper().savePref(Contents.MYBETS_enddate, toDate);
                    AppPreference.getPrefsHelper().savePref(Contents.MYBETS_description,  bet_description.getText().toString());
                    AppPreference.getPrefsHelper().savePref(Contents.MYBETS_EDIT_OR_CREATE, "true");
                    startActivity(new Intent(BetCreationActivity.this, MapDistanceByLoctionActivity.class));
                    //finish();
                    edBet_km.setVisibility(View.GONE);
                    view1.setBackgroundColor(getResources().getColor(R.color.hint_text_color));
                    view2.setBackgroundColor(getResources().getColor(R.color.light_blue));
                    bet_distance_by_km.setTextColor(getResources().getColor(R.color.hint_text_color));
                    bet_distance_by_location.setTextColor(getResources().getColor(R.color.light_blue));
                    distanceKm=false;
                    distanceLocation=true;
                }
            }
        });
        bet_distance_by_km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edBet_km.setVisibility(View.VISIBLE);
                bet_km.setVisibility(View.GONE);
                fromRow.setVisibility(View.GONE);
                toRow.setVisibility(View.GONE);
                 distanceKm=true;
                 distanceLocation=false;
                bet_distance_by_km.setTextColor(getResources().getColor(R.color.light_blue));
                bet_distance_by_location.setTextColor(getResources().getColor(R.color.hint_text_color));
                view1.setBackgroundColor(getResources().getColor(R.color.light_blue));
                view2.setBackgroundColor(getResources().getColor(R.color.hint_text_color));

            }
        });
        bet_description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 100) {
                    Utils.showCustomToastMsg(BetCreationActivity.this, R.string.max_alowed_five_cashier);
                    String text = bet_description.getText().toString();
                    bet_description.setText(text.substring(0, bet_description.length() - 1));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnCreateBet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String x = bet_credit.getText().toString();
                if(bet_name.getText().toString().equals("")){
                    Utils.showCustomToastMsg(BetCreationActivity.this, R.string.please_enter_bet_name);
                } else if(x.startsWith("0")){
                    bet_credit.setText("");
                } else if(bet_credit.getText().toString().equals("")){
                    Utils.showCustomToastMsg(BetCreationActivity.this, R.string.enter_credit);
                }else if(bet_credit.getText().toString().equals("")){
                    Utils.showCustomToastMsg(BetCreationActivity.this, R.string.enter_credit);
                }else if(fromDate.equals("")||toDate.equals("")){
                    Utils.showCustomToastMsg(BetCreationActivity.this, R.string.select_date);
                }else if(distanceKm){
                    if(edBet_km.getText().toString().startsWith("0")){
                        edBet_km.setText("");
                    }else{
                        if(!edBet_km.getText().toString().equals("")){
                            if(distanceKm){
                                callAddBetApi();
                                CustomProgress.getInstance().showProgress(BetCreationActivity.this, "", false);
                            }
                        } else if(kmInDec==0 ||distanceLocation){
                            Utils.showCustomToastMsg(BetCreationActivity.this, R.string.enter_km);
                        }
                    }
                }else if(distanceLocation){
                    callAddLocationTypeBetApi();
                    CustomProgress.getInstance().showProgress(BetCreationActivity.this, "", false);
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        AppPreference.getPrefsHelper().savePref(Contents.BAT_POSICTION, "2");
        AppPreference.getPrefsHelper().savePref(Contents.DASH_BOARD_POSICTION, "1");
        AppPreference.getPrefsHelper().savePref(Contents.BET_PAGE_POSICTION, "1");
        Intent intent = new Intent(BetCreationActivity.this, DashBoardActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void cleare_form() {
        bet_name.setText("");
        bet_credit.setText("");
        bet_from_date.setText("");
        bet_to_date.setText("");
        bet_description.setText("");
        edBet_km.setText("");
        from_address.setText("");
        to_address.setText("");
        bet_km.setText("");
        edBet_km.setVisibility(View.GONE);
        bet_km.setVisibility(View.VISIBLE);
        fromRow.setVisibility(View.VISIBLE);
        toRow.setVisibility(View.VISIBLE);
        distanceKm=false;
        distanceLocation=true;
        AppPreference.getPrefsHelper().savePref(Contents.MYBETS_betname, "");
        AppPreference.getPrefsHelper().savePref(Contents.CREDIT_SCORE, "");
        AppPreference.getPrefsHelper().savePref(Contents.START_DATE, "");
        AppPreference.getPrefsHelper().savePref(Contents.MYBETS_enddate, "");
        AppPreference.getPrefsHelper().savePref(Contents.MYBETS_description,  "");
    }
    private void createLocationRequest() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                Log.d("Location","High accuracy location enabled");
                // Toast.makeText(DashBoardActivity.this, "addOnSuccessListener", Toast.LENGTH_SHORT).show();
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(BetCreationActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }
    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }
    public Date atEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }
    private void callDashboardDetailsApi() {
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).DashboardDetails(AppPreference.getPrefsHelper().getPref(Contents.REG_KEY,""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    DashboardDetailReportapiresult(bodyString);
                    CustomProgress.getInstance().hideProgress();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                CustomProgress.getInstance().hideProgress();
            }
        });
    }
    private void DashboardDetailReportapiresult(String bodyString) {
        try {
            JSONObject jsonObject = new JSONObject(bodyString);
            String status = jsonObject.getString("Status");
            if (status.trim().equals("Ok")) {
                String data1 = jsonObject.getString(DASH_BOARD_USERS);
                JSONObject jsonObject1 = new JSONObject(data1);
                credits=jsonObject1.getString(DASH_BOARD_CREDIT_SCORE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void callAddLocationTypeBetApi() {
        double dis= Double.parseDouble(distance_draw.replace("km","").replace("m",""));
        String distance_draw_meter= String.valueOf(dis);
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).AddBet(bet_name.getText().toString(),
                bet_description.getText().toString(),fromDate,toDate, distance_draw_meter,
                from_address.getText().toString(),to_address.getText().toString(),startLog,endLog,startLat,endLat,overview_polyline, bet_credit.getText().toString().trim(),
                AppPreference.getPrefsHelper().getPref(Contents.REG_KEY,""),"location");
        call.enqueue(new Callback<ResponseBody>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    final JSONObject jsonObject = new JSONObject(bodyString);
                    String msg = jsonObject.getString("Msg");
                    String data = jsonObject.getString("Status");
                    if(Integer.parseInt(credits)>Integer.parseInt(bet_credit.getText().toString())){
                        if(data.equals("Ok")){
                            CustomProgress.getInstance().hideProgress();
                            AppPreference.getPrefsHelper().savePref(Contents.DASH_BOARD_POSICTION, "1");
                            AppPreference.getPrefsHelper().savePref(Contents.BET_PAGE_POSICTION, "1");
                            Intent intent = new Intent(BetCreationActivity.this, DashBoardActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }else{
                        CustomProgress.getInstance().hideProgress();
                        AppPreference.getPrefsHelper().savePref(Contents.DASH_BOARD_POSICTION, "1");
                        AppPreference.getPrefsHelper().savePref(Contents.BET_PAGE_POSICTION, "1");
                        Intent intent = new Intent(BetCreationActivity.this, DashBoardActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                CustomProgress.getInstance().hideProgress();
            }
        });
    }
    private void callAddBetApi() {
        double distance;
        distance = Double.parseDouble(edBet_km.getText().toString().trim())*1000;
        //double dis= Double.parseDouble(distance_draw.replace("km","").replace("m",""))/1000;
        Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).AddBet(bet_name.getText().toString(),
                bet_description.getText().toString(),fromDate,toDate, ""+distance,
                "","","","","","","", bet_credit.getText().toString().trim(),
                AppPreference.getPrefsHelper().getPref(Contents.REG_KEY,""),"distance");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String bodyString = new String(response.body().bytes(), "UTF-8");
                    final JSONObject jsonObject = new JSONObject(bodyString);
                    String msg = jsonObject.getString("Msg");
                    String data = jsonObject.getString("Status");
                        if(data.equals("Ok")){
                            CustomProgress.getInstance().hideProgress();
                            AppPreference.getPrefsHelper().savePref(Contents.DASH_BOARD_POSICTION, "1");
                            AppPreference.getPrefsHelper().savePref(Contents.BET_PAGE_POSICTION, "1");
                            Intent intent = new Intent(BetCreationActivity.this, DashBoardActivity.class);
                            startActivity(intent);
                            finish();
                        }else if(data.equals("Error_credit")){
                            CustomProgress.getInstance().hideProgress();
                            Utils.showCustomToastMsg(BetCreationActivity.this, msg);
                            hideKeyboard();
                            bet_credit.setText("");
                        }else if(data.equals("Error_date")){
                            CustomProgress.getInstance().hideProgress();
                            Utils.showCustomToastMsg(BetCreationActivity.this, msg);
                            hideKeyboard();
                        }
                        else{
                            CustomProgress.getInstance().hideProgress();
                            Utils.showCustomToastMsg(BetCreationActivity.this, msg);
                            hideKeyboard();
                        }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                CustomProgress.getInstance().hideProgress();
            }
        });
    }
    private void CallInappPurchass() {
        /*Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);*/
    }
    @Override
    public void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


}
