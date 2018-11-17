package com.bigkoo.pickerviewdemo;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerviewdemo.bean.DrugClassModel;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * 解析省市区数据示例
 *
 * @author 小嵩
 * @date 2017-3-16
 */
public class JsonDataActivity extends AppCompatActivity implements View.OnClickListener {


    private ArrayList<DrugClassModel> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<DrugClassModel>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<DrugClassModel>>> options3Items = new ArrayList<>();
    private Thread thread;
    private static final int MSG_LOAD_DATA = 0x0001;
    private static final int MSG_LOAD_SUCCESS = 0x0002;
    private static final int MSG_LOAD_FAILED = 0x0003;

    private boolean isLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json_data);
        initView();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_DATA:
                    if (thread == null) {//如果已创建就不再重新创建子线程了

                        Toast.makeText(JsonDataActivity.this, "Begin Parse Data", Toast.LENGTH_SHORT).show();
                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 子线程中解析省市区数据
                                initJsonData();
                            }
                        });
                        thread.start();
                    }
                    break;

                case MSG_LOAD_SUCCESS:
                    Toast.makeText(JsonDataActivity.this, "Parse Succeed", Toast.LENGTH_SHORT).show();
                    isLoaded = true;
                    break;

                case MSG_LOAD_FAILED:
                    Toast.makeText(JsonDataActivity.this, "Parse Failed", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void initView() {
        findViewById(R.id.btn_data).setOnClickListener(this);
        findViewById(R.id.btn_show).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_data:
                mHandler.sendEmptyMessage(MSG_LOAD_DATA);
                break;
            case R.id.btn_show:
                if (isLoaded) {
                    showPickerView();
                } else {
                    Toast.makeText(JsonDataActivity.this, "Please waiting until the data is parsed", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    private void showPickerView() {// 弹出选择器

        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
//                String tx = options1Items.get(options1).getPickerViewText() +
//                        options2Items.get(options1).get(options2) +
//                        options3Items.get(options1).get(options2).get(options3);

                Toast.makeText(JsonDataActivity.this, options1 + " - " + options2 + " - "+ options3, Toast.LENGTH_SHORT).show();
            }
        })

                .setTitleText("城市选择")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .build();

        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }

    private void initJsonData() {//解析数据

        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        String JsonData = new GetJsonDataUtil().getJson(this, "drug.json");//获取assets目录下的json文件数据

        ArrayList<DrugClassModel> jsonBean = parseData(JsonData);//用Gson 转成实体
        for(DrugClassModel model : jsonBean){
            model.setDictname(model.getDictname().length() > 4 ?model.getDictname().substring(0,4)+"...":model.getDictname() );
        }
        //一级列表
        for(DrugClassModel model:jsonBean){
            if (TextUtils.isEmpty(model.getDictparentid())){
                options1Items.add(model);
            }
        }

        for (DrugClassModel item1:options1Items){
            //二级列表
            ArrayList<DrugClassModel> secondList = new ArrayList<>();
            for (DrugClassModel model : jsonBean) {
                if (item1.getDictid().equals(model.getDictparentid())) {
                    secondList.add(model);
                }
            }
            options2Items.add(secondList);

            //三级列表
            ArrayList<ArrayList<DrugClassModel>> thirdList = new ArrayList<>();
            for (DrugClassModel sendItem : secondList) {
                ArrayList<DrugClassModel> thirdInnerList = new ArrayList<>();
                for (DrugClassModel model : jsonBean) {
                    if (sendItem.getDictid().equals(model.getDictparentid())) {
                        thirdInnerList.add(model);
                    }
                }
                thirdList.add(thirdInnerList);
            }
            options3Items.add(thirdList);
        }

        mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);

    }


    public ArrayList<DrugClassModel> parseData(String result) {//Gson 解析
        ArrayList<DrugClassModel> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                DrugClassModel entity = gson.fromJson(data.optJSONObject(i).toString(), DrugClassModel.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
