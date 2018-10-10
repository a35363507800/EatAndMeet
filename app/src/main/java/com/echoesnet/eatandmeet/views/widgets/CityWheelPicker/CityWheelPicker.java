package com.echoesnet.eatandmeet.views.widgets.CityWheelPicker;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.SelectWheelPicker.ISelectItemFinishListener;
import com.echoesnet.eatandmeet.views.widgets.cascadeWheelView.ArrayWheelAdapter;
import com.echoesnet.eatandmeet.views.widgets.cascadeWheelView.CityModel;
import com.echoesnet.eatandmeet.views.widgets.cascadeWheelView.DistrictModel;
import com.echoesnet.eatandmeet.views.widgets.cascadeWheelView.OnWheelChangedListener;
import com.echoesnet.eatandmeet.views.widgets.cascadeWheelView.ProvinceModel;
import com.echoesnet.eatandmeet.views.widgets.cascadeWheelView.WheelView;
import com.echoesnet.eatandmeet.views.widgets.cascadeWheelView.XmlParserHandler;
import com.zhy.autolayout.AutoRelativeLayout;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by wangben on 2016/6/6.
 */
public class CityWheelPicker extends PopupWindow
{
    private Activity mContext;
    private ISelectItemFinishListener mSelectItemFinishListener;
    int selectIndex=-1;
    WheelView wv_province;
    WheelView wv_city;

    public CityWheelPicker(Activity context)
    {
        this.mContext=context;
        initWindow();
    }

    private void initWindow()
    {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View contentView =  inflater.inflate(R.layout.cascade_wheelview_layout, null);

        AutoRelativeLayout arl_top = (AutoRelativeLayout) contentView.findViewById(R.id.arl_top);
        wv_province = (WheelView) contentView.findViewById(R.id.wv_province);
        wv_city = (WheelView) contentView.findViewById(R.id.wv_city);

      //  Button btnOk = (Button) contentView.findViewById(R.id.btn_ok_city);
        TextView btnOk = (TextView) contentView.findViewById(R.id.tv_ok);
        TextView tvCancel = (TextView) contentView.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
                if(!TextUtils.isEmpty(mCurrentCityName))
                    mSelectItemFinishListener.finishSelect(selectIndex,mCurrentCityName);
            }
        });
        // 设置SelectPicPopupWindow的View
        this.setContentView(contentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(CommonUtils.getScreenSize(mContext).width);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        this.backgroundAlpha(0.5f);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.PopupAnimation);

        contentView.setOnTouchListener(new View.OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent event)
            {
                int height = contentView.findViewById(R.id.arl_top).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    if (y < height)
                    {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }

    public void setOnSelectFinishListener(ISelectItemFinishListener mSelectItemFinishListener)
    {
        this.mSelectItemFinishListener=mSelectItemFinishListener;
    }
    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent)
    {
        if (!this.isShowing())
        {
            this.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        } else
        {
            this.dismiss();
        }
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        mContext.getWindow().setAttributes(lp);
    }

    public void setUpListener()
    {
        // 添加change事件
        wv_province.addChangingListener(new MyOnWheelChangeListener());
        // 添加change事件
        wv_city.addChangingListener(new MyOnWheelChangeListener());
    }

    public void setUpData(String filePath)
    {
        initProvinceDatas(filePath);
        wv_province.setViewAdapter(new ArrayWheelAdapter<String>(mContext, mProvinceDatas));
        // 设置可见条目数量
        wv_province.setVisibleItems(3);
        wv_city.setVisibleItems(3);
        updateCities();
        updateAreas();
    }

    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void updateAreas()
    {
        int pCurrent = wv_city.getCurrentItem();
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
        String[] areas = mDistrictDatasMap.get(mCurrentCityName);

        if (areas == null)
        {
            areas = new String[]{""};
        }
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities()
    {
        int pCurrent = wv_province.getCurrentItem();
        mCurrentProviceName = mProvinceDatas[pCurrent];
        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null)
        {
            cities = new String[]{""};
        }
        wv_city.setViewAdapter(new ArrayWheelAdapter<String>(mContext, cities));
        wv_city.setCurrentItem(0);
        updateAreas();
    }

    private void showSelectedResult()
    {
        ToastUtils.showShort("当前选中:" + mCurrentProviceName + "," + mCurrentCityName);
    }


    /**
     * 所有省
     */
    protected String[] mProvinceDatas;
    /**
     * key - 省 value - 市
     */
    protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
    /**
     * key - 市 values - 区
     */
    protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();

    /**
     * key - 区 values - 邮编
     */
    protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>();

    /**
     * 当前省的名称
     */
    protected String mCurrentProviceName;
    /**
     * 当前市的名称
     */
    protected String mCurrentCityName;
    /**
     * 当前区的名称
     */
    protected String mCurrentDistrictName = "";
    /**
     * 当前区的邮政编码
     */
    protected String mCurrentZipCode = "";

    /**
     * 解析省市区的XML数据
     */
    private void initProvinceDatas(String filePath)
    {
        List<ProvinceModel> provinceList = null;
        AssetManager asset = mContext.getAssets();
        try {
//            InputStream input = asset.open("provincedata/province_data.xml");
            InputStream input = asset.open(filePath);
            // 创建一个解析xml的工厂对象
            SAXParserFactory spf = SAXParserFactory.newInstance();
            // 解析xml
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input, handler);
            input.close();
            // 获取解析出来的数据
            provinceList = handler.getDataList();
            //*/ 初始化默认选中的省、市、区
            if (provinceList != null && !provinceList.isEmpty()) {
                mCurrentProviceName = provinceList.get(0).getName();
                List<CityModel> cityList = provinceList.get(0).getCityList();
                if (cityList != null && !cityList.isEmpty())
                {
                    mCurrentCityName = cityList.get(0).getName();
                    List<DistrictModel> districtList = cityList.get(0).getDistrictList();
                    mCurrentDistrictName = districtList.get(0).getName();
                    mCurrentZipCode = districtList.get(0).getZipcode();
                }
            }
            //*/
            mProvinceDatas = new String[provinceList.size()];
            for (int i = 0; i < provinceList.size(); i++) {
                // 遍历所有省的数据
                mProvinceDatas[i] = provinceList.get(i).getName();
                List<CityModel> cityList = provinceList.get(i).getCityList();
                String[] cityNames = new String[cityList.size()];
                for (int j = 0; j < cityList.size(); j++) {
                    // 遍历省下面的所有市的数据
                    cityNames[j] = cityList.get(j).getName();
                    List<DistrictModel> districtList = cityList.get(j).getDistrictList();
                    String[] distrinctNameArray = new String[districtList.size()];
                    DistrictModel[] distrinctArray = new DistrictModel[districtList.size()];
                    for (int k = 0; k < districtList.size(); k++) {
                        // 遍历市下面所有区/县的数据
                        DistrictModel districtModel = new DistrictModel(districtList.get(k).getName(), districtList.get(k).getZipcode());
                        // 区/县对于的邮编，保存到mZipcodeDatasMap
                        mZipcodeDatasMap.put(districtList.get(k).getName(), districtList.get(k).getZipcode());
                        distrinctArray[k] = districtModel;
                        distrinctNameArray[k] = districtModel.getName();
                    }
                    // 市-区/县的数据，保存到mDistrictDatasMap
                    mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
                }
                // 省-市的数据，保存到mCitisDatasMap
                mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
            }
        } catch (Throwable e)
        {
            e.printStackTrace();
        } finally {

        }
    }

    private class MyOnWheelChangeListener implements OnWheelChangedListener
    {

        @Override
        public void onChanged(WheelView wheel, int oldValue, int newValue)
        {
            if (wheel == wv_province)
            {
                updateCities();
            } else if (wheel == wv_city)
            {
                updateAreas();
            }
        }
    }
}
