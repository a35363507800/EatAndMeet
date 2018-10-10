package com.echoesnet.eatandmeet.views.widgets;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/4/21.
 */

public class ContextMenuDialog
{
    private MenuDialogCallBack menuDialogCallBack;
    public ContextMenuDialog(final MenuDialogCallBack menuDialogCallBack)
    {
        this.menuDialogCallBack = menuDialogCallBack;
    }

    /**
     * 显示操作图片上下文菜单
     *
     * @param operUrls 点击的当前图片位置
     * @return
     */
    public Dialog showContextMenuBox(Context mContext, final List<String> operUrls)
    {
        final Dialog dialog = new Dialog(mContext, R.style.AlertDialogStyle);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.menudialog_img_operation, null);
        dialog.setContentView(contentView);
        ListView tvCheckImg = (ListView) contentView.findViewById(R.id.menudialog_lv);

        tvCheckImg.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (menuDialogCallBack != null)
                    menuDialogCallBack.menuOnClick(operUrls.get(position), position);
                dialog.dismiss();
            }
        });

        tvCheckImg.setAdapter(new ArrayAdapter<String>(mContext, R.layout.menudialog_img_operation_item, operUrls));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = CommonUtils.dp2px(mContext, 250);
        //lp.width= (int) (CommonUtils.getScreenSize(mContext).width*0.85f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
        return dialog;
    }

    public interface MenuDialogCallBack
    {
        void menuOnClick(String menuItem, int position);
    }
}
