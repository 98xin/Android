package com.example.celia.demo1.index;

import android.content.Context;
import com.example.celia.demo1.R;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.celia.demo1.bean.RegionalEconomyBean;
import com.example.celia.demo1.TabHostActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RegionalEconomy extends AppCompatActivity {
    private ListView regionaleconomyListView;
    private List<RegionalEconomyBean> regionalEconomyList;
    //private List<Index3_CityBean> index3cityList;
    private RegionalEconomyListViewAdapter adapter;
    //private index3cityListViewAdapter adapter2;
    private TextView search;
    private EditText comment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.regionaleconomy);
        regionaleconomyListView=findViewById(R.id.economy);

        ImageView ivreturn = findViewById(R.id.iv_return);
        ivreturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(
                        RegionalEconomy.this, TabHostActivity.class);
                intent.putExtra("id", 0);
                startActivity(intent);
            }
        });

        //搜索
        comment = findViewById(R.id.et_name);
        search = findViewById(R.id.tv_search);
        ImageView searchIv=findViewById(R.id.iv_search);
        searchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(comment.getText()==null){
                    initData();
                }
                GetListAsyncTask asyncTask1 = new GetListAsyncTask(RegionalEconomy.this,regionaleconomyListView);
                asyncTask1.execute();
            }
        });
       comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                GetListAsyncTask asyncTask1 = new GetListAsyncTask(RegionalEconomy.this,regionaleconomyListView);
                asyncTask1.execute();
            }
        });


        //获取数据，从数据库中提取
        initData();


        //跳转到三级页面
        regionaleconomyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("test",regionalEconomyList.toString());
                Intent intent = new Intent();
                intent.setClass(RegionalEconomy.this,Index_city.class);
                intent.putExtra("cityImg",regionalEconomyList.get(position).getCityImg());
                intent.putExtra("cityTitle",regionalEconomyList.get(position).getCityTitle());
                intent.putExtra("cityName",regionalEconomyList.get(position).getCityName());
                intent.putExtra("cityType",regionalEconomyList.get(position).getCityType());
                intent.putExtra("province",regionalEconomyList.get(position).getProvince());
                intent.putExtra("cityContent",regionalEconomyList.get(position).getCityContent());
                intent.putExtra("citySalary",regionalEconomyList.get(position).getCitySalary());
                intent.putExtra("cityGdp",regionalEconomyList.get(position).getCityGdp());
                startActivity(intent);

            }
        });
    }
    //创建adapter
    private class RegionalEconomyListViewAdapter extends BaseAdapter{

        private Context context;
        private int itemLayout;
        private List<RegionalEconomyBean> RegionalEconomyList;

        public RegionalEconomyListViewAdapter(Context context,
                                              int itemLayout,
                                              List<RegionalEconomyBean> list){
            this.context=context;
            this.itemLayout=itemLayout;
            this.RegionalEconomyList=list;
        }
        @Override
        public int getCount() {
            return RegionalEconomyList.size();
        }

        @Override
        public Object getItem(int position) {
            return RegionalEconomyList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView==null){
                LayoutInflater inflater=LayoutInflater.from(context);
                convertView=inflater.inflate(itemLayout,null);
            }
            TextView city=convertView.findViewById(R.id.city);
            TextView cityGdp=convertView.findViewById(R.id.gdp);
            TextView collegeNum=convertView.findViewById(R.id.amount);
            RegionalEconomyBean regionalEconomy=RegionalEconomyList.get(position);
            Log.e("test",regionalEconomy.getCityName());
            city.setText(regionalEconomy.getCityName());
            cityGdp.setText(""+regionalEconomy.getCityGdp());
            collegeNum.setText(""+regionalEconomy.getCollegeNum());
            return convertView;
        }
    }
    //创建获取数据的异步任务
    class GetRegionalEconomyListAsyncTask extends AsyncTask<String,Void,List<RegionalEconomyBean>> {

        private Context mContext;
        private ListView regionaleconomyListView;

        public GetRegionalEconomyListAsyncTask(Context mContext,ListView regionaleconomyListView){
            this.mContext=mContext;
            this.regionaleconomyListView=regionaleconomyListView;
        }
        @Override
        protected List<RegionalEconomyBean> doInBackground(String... strings) {
            String path = getResources().getString(R.string.url_path);
            String urlStr = path+"RegionalEconomyServlet?remark=getRegionalEconomyList";
            try {
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("contentType", "utf-8");//解决给服务器端传输的乱码问题
                InputStream inputStream = connection.getInputStream();
                //字节流转字符流
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);//转换流
                BufferedReader reader = new BufferedReader(inputStreamReader);//字符流
                String str = reader.readLine();
                Log.e("test", str);
                //解析jsonarray
                JSONArray array = new JSONArray(str);
                regionalEconomyList=new ArrayList<>();
                for (int i = 0; i < array.length(); ++i) {
                    JSONObject object1 = array.getJSONObject(i);
                    RegionalEconomyBean regionaleconomy = new RegionalEconomyBean();
                    regionaleconomy.setCityName(object1.getString("cityName"));
                    regionaleconomy.setCityGdp(object1.getInt("cityGdp"));
                    regionaleconomy.setCollegeNum(object1.getInt("collegeNum"));
                    regionaleconomy.setCityTitle(object1.getString("cityTitle"));
                    regionaleconomy.setCityContent(object1.getString("cityContent"));
                    regionaleconomy.setCitySalary(object1.getInt("citySalary"));
                    regionaleconomy.setCityType(object1.getString("cityType"));
                    regionaleconomy.setCityImg(object1.getString("cityImg"));
                    regionaleconomy.setProvince(object1.getString("province"));
                    regionalEconomyList.add(regionaleconomy);
                }
                Log.e("test", regionalEconomyList.toString());
                return regionalEconomyList;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return regionalEconomyList;
        }

        @Override
        protected void onPostExecute(List<RegionalEconomyBean> result){
            Log.e("test","已经进行到异步类的显示阶段");
            adapter=new RegionalEconomyListViewAdapter(mContext,R.layout.economy_list_item,result);
            regionaleconomyListView.setAdapter(adapter);
        }
    }
    //获取数据
    public void initData() {
        GetRegionalEconomyListAsyncTask asyncTask=new GetRegionalEconomyListAsyncTask(RegionalEconomy.this,regionaleconomyListView);
        asyncTask.execute();
    }


    //根据搜索进行筛选的异步任务类
    class GetListAsyncTask extends AsyncTask<String,Void,List<RegionalEconomyBean>> {

        private Context mContext;
        private ListView listView;

        public GetListAsyncTask(Context mContext,ListView listView){
            this.mContext=mContext;
            this.listView=listView;
        }
        @Override
        protected List<RegionalEconomyBean> doInBackground(String... strings) {

            String name = String.valueOf(comment.getText());
            String path = getResources().getString(R.string.url_path);
            String urlStr = path+"RegionalEconomyServlet?remark=getcityListByName&name="
                    +name;
            try {
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("contentType", "utf-8");//解决给服务器端传输的乱码问题
                InputStream inputStream = connection.getInputStream();
                //字节流转字符流
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);//转换流
                BufferedReader reader = new BufferedReader(inputStreamReader);//字符流
                String str = reader.readLine();
                Log.e("test",str);
                //解析jsonarray
                JSONArray array = new JSONArray(str);
                regionalEconomyList=new ArrayList<>();
                for (int i = 0; i < array.length(); ++i) {
                    JSONObject object1 = array.getJSONObject(i);
                    RegionalEconomyBean regionaleconomy = new RegionalEconomyBean();
                    regionaleconomy.setCityName(object1.getString("cityName"));
                    regionaleconomy.setCityGdp(object1.getInt("cityGdp"));
                    regionaleconomy.setCollegeNum(object1.getInt("collegeNum"));
                    regionaleconomy.setCityContent(object1.getString("cityContent"));
                    regionaleconomy.setCityTitle(object1.getString("cityTitle"));
                    regionaleconomy.setCitySalary(object1.getInt("citySalary"));
                    regionaleconomy.setProvince(object1.getString("province"));
                    regionaleconomy.setCityType(object1.getString("cityType"));
                    regionaleconomy.setCityImg(object1.getString("cityImg"));
                    regionalEconomyList.add(regionaleconomy);
                }
                Log.e("test", regionalEconomyList.toString());
                return regionalEconomyList;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<RegionalEconomyBean> result){
            Log.e("test","已经进行到异步类的显示阶段");
            adapter = new RegionalEconomyListViewAdapter(mContext,R.layout.economy_list_item,result);
            listView.setAdapter(adapter);
        }

    }
}

