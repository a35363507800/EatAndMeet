package com.echoesnet.eatandmeet.views.widgets;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.FileUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.views.widgets.cascadeWheelView.ArrayWheelAdapter;
import com.echoesnet.eatandmeet.views.widgets.cascadeWheelView.OnWheelChangedListener;
import com.echoesnet.eatandmeet.views.widgets.cascadeWheelView.WheelView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.echoesnet.eatandmeet.R.id.wv_city;
import static com.echoesnet.eatandmeet.R.id.wv_province;

/**
 * Created by Administrator on 2017/2/15.
 */

public class LiveSetAddressPopup extends PopupWindow implements OnWheelChangedListener
{
    private final static String TAG = LiveSetAddressPopup.class.getSimpleName();
    private Activity mAct;
    private WheelView wvProvince, wvCity, wvSquare, wvRoad;
    Button setAddress;
    private ISelectedAreaFinishListener mSelectItemFinishListener;

    private int type;
    private String area;

    /**
     * 所有省
     */
    private String[] mProvinceData;
    private ArrayList<String> tempProvince = new ArrayList<>();
    /**
     * key - 省 value - 市s
     */
    private HashMap<String, String[]> mCitisDataMap = new HashMap<>();
    private ArrayList<String> tempCity = new ArrayList<>();

    /**
     * key - 市 values - 区s
     */
    private HashMap<String, String[]> mAreaDataMap = new HashMap<>();
    private ArrayList<String> tempArea = new ArrayList<>();
    /**
     * 存放全国所有区的街道
     * key - 区 values - 街道s
     */
    private HashMap<String, List<HashMap<String, String>>> mRoadDataMap = new HashMap<>();

    /**
     * @param mAct
     */
    public LiveSetAddressPopup(Activity mAct)
    {
        this.mAct = mAct;
        initWindow();
    }

    private void initWindow()
    {
        LayoutInflater inflater = (LayoutInflater) mAct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View contentView = inflater.inflate(R.layout.set_address_picker, null);
        wvProvince = (WheelView) contentView.findViewById(wv_province);
        wvCity = (WheelView) contentView.findViewById(wv_city);
        wvSquare = (WheelView) contentView.findViewById(R.id.wv_square);
        wvRoad = (WheelView) contentView.findViewById(R.id.wv_road);
        setAddress = (Button) contentView.findViewById(R.id.set_address);

        // 设置SelectPicPopupWindow的View
        this.setContentView(contentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(CommonUtils.getScreenSize(mAct).width);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
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

        setAddress.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (type == 0)
                {
                    String province = mProvinceData[wvProvince.getCurrentItem()];
                    String city = mCitisDataMap.get(province)[wvCity.getCurrentItem()];
                    String area = mAreaDataMap.get(city)[wvSquare.getCurrentItem()];
                    if (mSelectItemFinishListener != null)
                        mSelectItemFinishListener.selectedArea(province, city, area);
                    dismiss();
                }
                else
                {
                    HashMap<String, String> roadPosMap = mRoadDataMap.get(area).get(wvRoad.getCurrentItem());
                    String roadName = (String) roadPosMap.keySet().toArray()[0];
                    String pos = roadPosMap.get(roadName);
                    if (mSelectItemFinishListener != null)
                        mSelectItemFinishListener.selectedRoad(roadName, pos);
                    dismiss();
                }
            }
        });
        wvProvince.addChangingListener(this);
        wvCity.addChangingListener(this);

        wvProvince.setVisibleItems(3);
        wvCity.setVisibleItems(3);
        wvSquare.setVisibleItems(3);
        wvRoad.setVisibleItems(3);
        initCityData();
    }

    private void initCityData()
    {
        StringBuilder json = FileUtils.readFile(NetHelper.getRootDirPath(mAct) +NetHelper.DATA_FOLDER+ "site.json", "utf-8");
        String responseData = json.toString();
        try
        {
            JSONArray object = new JSONArray(responseData);
            for (int i = 0; i < object.length(); i++)
            {
                JSONObject province = object.getJSONObject(i);
                String pName = province.getString("name");//省名
                tempProvince.add(pName);
                String pCode = province.getString("code"); // 101
                JSONArray pCity = province.getJSONArray("children");
                for (int j = 0; j < pCity.length(); j++)
                {
                    JSONObject city = pCity.getJSONObject(j);
                    String cName = city.getString("name");//市名
                    String cCode = city.getString("code");  //1001
                    tempCity.add(cName);
                    JSONArray cArea = city.getJSONArray("children");
                    for (int k = 0; k < cArea.length(); k++)
                    {
                        JSONObject area = cArea.getJSONObject(k);
                        String aName = area.getString("name");//区名
                        String aCode = area.getString("code");//10001
                        tempArea.add(aName);
                        JSONArray aRoad = area.getJSONArray("children");


                        //存放一个区的所有街道
                        List<HashMap<String, String>> tempRoadList = new ArrayList<>();
                        for (int l = 0; l < aRoad.length(); l++)
                        {
                            //存放某一街道地理信息key街道名称；value 位置
                            HashMap<String, String> rodePos = new HashMap<>();
                            JSONObject road = aRoad.getJSONObject(l);
                            String rName = road.getString("name");//街道名
                            String rCode = road.getString("code");//100001
                            String rPos = road.getString("pos");
                            String rChildren = road.getString("children");
                            rodePos.put(rName, rPos);
                            tempRoadList.add(rodePos);
                        }
                        mRoadDataMap.put(pName + CommonUtils.SEPARATOR + cName + CommonUtils.SEPARATOR + aName, tempRoadList);
                    }
                    String[] s = new String[tempArea.size()];
                    mAreaDataMap.put(tempCity.get(j), tempArea.toArray(s));
                }
                String[] s = new String[tempCity.size()];
                mCitisDataMap.put(tempProvince.get(i), tempCity.toArray(s));
            }
            mProvinceData = new String[tempProvince.size()];
            for (int i = 0; i < tempProvince.size(); i++)
            {
                mProvinceData[i] = tempProvince.get(i);
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * @param type 区分选择省市区 还是  选择街道   现在分为2种，省市区和街道是分开的
     *             0:省市区    1:街道   传入0时 area 可传空  ---yqh
     * @param area
     */
    public void switchData(int type, String area)
    {
        this.type = type;
        this.area = area;
        if (type == 0)
        {
            changeUI(type);
            wvProvince.setViewAdapter(new ArrayWheelAdapter<>(mAct, mProvinceData));
            updateCity();
            updateSquare();
        }
        else if (type == 1)
        {
            if (TextUtils.isEmpty(area))
            {
                return;
            }
            changeUI(type);
            List<String> roads = new ArrayList<>();
            for (HashMap<String, String> map : mRoadDataMap.get(area))
            {
                roads.add((String) map.keySet().toArray()[0]);
            }
            wvRoad.setViewAdapter(new ArrayWheelAdapter<>(mAct, roads.toArray()));
        }
    }

    private void changeUI(int type)
    {
        switch (type)
        {
            case 0:
                wvProvince.setVisibility(View.VISIBLE);
                wvCity.setVisibility(View.VISIBLE);
                wvSquare.setVisibility(View.VISIBLE);
                wvRoad.setVisibility(View.GONE);
                break;
            case 1:
                wvProvince.setVisibility(View.GONE);
                wvCity.setVisibility(View.GONE);
                wvSquare.setVisibility(View.GONE);
                wvRoad.setVisibility(View.VISIBLE);
                break;
            default:
                break;

        }
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue)
    {
        if (wheel == wvProvince)
        {
            updateCity();
        }
        if (wheel == wvCity)
        {
            updateSquare();
        }
    }

    private void updateCity()
    {
        int position = wvProvince.getCurrentItem();
        String province = mProvinceData[position];
        String[] city = mCitisDataMap.get(province);
        wvCity.setViewAdapter(new ArrayWheelAdapter<>(mAct, city));
        wvCity.setCurrentItem(0);
        updateSquare();
    }

    private void updateSquare()
    {
        int provinceIndex = wvProvince.getCurrentItem();
        String province = mProvinceData[provinceIndex];
        //选中城市的index
        int position = wvCity.getCurrentItem(); //2
        String[] city = mCitisDataMap.get(province);
        String[] currentSquare = mAreaDataMap.get(city[position]); //得出区
        wvSquare.setViewAdapter(new ArrayWheelAdapter<>(mAct, currentSquare));
        wvSquare.setCurrentItem(0);
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void setBackgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = mAct.getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        mAct.getWindow().setAttributes(lp);
    }

    public void setOnSelectFinishListener(ISelectedAreaFinishListener mSelectItemFinishListener)
    {
        this.mSelectItemFinishListener = mSelectItemFinishListener;
    }

    public interface ISelectedAreaFinishListener
    {
        void selectedArea(String province, String city, String area);

        void selectedRoad(String roadName, String position);
    }

}
