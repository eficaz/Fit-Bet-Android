package com.eficaz_fitbet_android.fitbet.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eficaz_fitbet_android.fitbet.R;
import com.eficaz_fitbet_android.fitbet.model.BetGroupList;
import com.eficaz_fitbet_android.fitbet.network.Constant;
import com.eficaz_fitbet_android.fitbet.network.RetroClient;
import com.eficaz_fitbet_android.fitbet.network.RetroInterface;
import com.eficaz_fitbet_android.fitbet.ui.adapters.MyBetGroupInviteListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.eficaz_fitbet_android.fitbet.utils.Contents.CREAT_DATE;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.GROUP_ID;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.GROUP_IMAGE;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_betgrops;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_betid;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_description;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.MYBETS_status;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.NAME;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.REG_KEY;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.STATUS_A;
import static com.eficaz_fitbet_android.fitbet.utils.Contents.TOTAL;

public class MyBetGroupInviteActivity extends BaseActivity {

    @Bind(R.id.invite_group_list)
    RecyclerView invite_group_list;

    @Bind(R.id.btn_back)
    TableRow btn_back;
    @Bind(R.id.no_data)
    TextView no_data;
    @Bind(R.id.searchview)
    EditText searchView;

    ArrayList<BetGroupList> invitemembersDetails;

    ArrayList<BetGroupList> multiselect_list = new ArrayList<>();


    MyBetGroupInviteListAdapter inviteListAdapter;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bet_group_invite);
        ButterKnife.bind(this);
        bundle =  getIntent().getExtras();
        inviteGroupList();
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent=new Intent(MyBetGroupInviteActivity.this,DashBoardActivity.class);
                startActivity(intent);*/
                finish();
            }
        });
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input
                filter(editable.toString());
            }
        });

    }
    private void filter(String text) {
        inviteListAdapter.filterList(text);
    }
    private void inviteGroupList() {
        final Bundle bundle = getIntent().getExtras();
        if(bundle.getString(GROUP_ID)!= null ||bundle.getString(GROUP_ID)!= "")
        {
            String search="";
            Call<ResponseBody> call = RetroClient.getClient(Constant.BASE_APP_URL).create(RetroInterface.class).MybetbetGroup(bundle.getString(GROUP_ID));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String bodyString = new String(response.body().bytes(), "UTF-8");
                        groupDetailsList(bodyString,bundle.getString(GROUP_ID));
                        //listOrderGroup(bodyString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                }
            });
        }else{

        }
    }
    private void groupDetailsList(String bodyString,String groupId) {
        try{
            final JSONObject jsonObject = new JSONObject(bodyString);
            String data = jsonObject.getString(STATUS_A);
            String betid = jsonObject.getString(MYBETS_betid);
            if(data.equals("Ok")){
                JSONObject jsonObject1 = new JSONObject(bodyString);
                String data1 = jsonObject1.getString(MYBETS_betgrops);
                JSONArray jsonArray = new JSONArray(data1);

                if(jsonArray.length()==0){
                    no_data.setVisibility(View.VISIBLE);
                }else{
                    no_data.setVisibility(View.GONE);
                }
                invitemembersDetails = new ArrayList<>();
                invitemembersDetails.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonList = jsonArray.getJSONObject(i);
                    BetGroupList model = new BetGroupList();
                    model.setGroupid(jsonList.getString(GROUP_ID));
                    model.setName(jsonList.getString(NAME));
                    model.setDescription(jsonList.getString(MYBETS_description));
                    model.setGroupimage(jsonList.getString(GROUP_IMAGE));
                    model.setReg_key(jsonList.getString(REG_KEY));
                    model.setCreatedate(jsonList.getString(CREAT_DATE));
                    model.setStatus(jsonList.getString(MYBETS_status));
                    model.setTotal(jsonList.getString(TOTAL));
                   /* model.setReg_key(jsonList.getString(REG_KEY));
                    model.setFirstname(jsonList.getString(FIRST_NAME));
                    model.setEmail(jsonList.getString(EMAIL));
                    model.setCreditScore(jsonList.getString(CREDIT_SCORE));
                    model.setWon(jsonList.getString(WON));
                    model.setLost(jsonList.getString(LOST));
                    model.setCountry(jsonList.getString(COUNTRY));
                    model.setProfile_pic(jsonList.getString(PROFILE_PIC));
                    model.setRegType(jsonList.getString(REG_TYPE));
                    model.setDistance(jsonList.getString(DISTANCE));*/
                    invitemembersDetails.add(model);
                }
                inviteListAdapter = new MyBetGroupInviteListAdapter(this, invitemembersDetails,bundle.getString(GROUP_ID));
                invite_group_list.setHasFixedSize(true);
                invite_group_list.setLayoutManager(new LinearLayoutManager(this));
                invite_group_list.setAdapter(inviteListAdapter);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       /* Intent intent=new Intent(this,DashBoardActivity.class);
        startActivity(intent);*/
        finish();
    }
}