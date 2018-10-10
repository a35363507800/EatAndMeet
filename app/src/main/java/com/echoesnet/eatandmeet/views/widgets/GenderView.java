package com.echoesnet.eatandmeet.views.widgets;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.echoesnet.eatandmeet.R;

/**
 * Created by lzy on 2017/8/10.
 */

public class GenderView extends LevelView
{

    public GenderView(Context context)
    {
        this(context, null);
    }

    public GenderView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public GenderView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public void setSex(String age, String sex)
    {

        if (TextUtils.isEmpty(sex))
        {
            this.setVisibility(GONE);
            return;
        }


        ivLevel.setVisibility(View.VISIBLE);
        ivIconLL.setVisibility(View.GONE);

        ivIcon.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextSize() - 3);
        ivIcontv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextSize() - 3);


        try
        {
            if (age.length() == 1)
            {
                ivNumber1.setImageResource(ageNumber[0]);
                ivNumber2.setImageResource(ageNumber[Integer.parseInt(age.charAt(0) + "")]);
            }
            else
            {
                ivNumber1.setImageResource(ageNumber[Integer.parseInt(age.charAt(0) + "")]);
                ivNumber2.setImageResource(ageNumber[Integer.parseInt(age.charAt(1) + "")]);
            }
        } catch (NumberFormatException e)
        {
            ivNumber2.setImageResource(R.drawable.age0);
        } catch (IndexOutOfBoundsException e)
        {
            ivNumber2.setImageResource(R.drawable.age0);
        }


        if ("男".equals(sex))
            ivLevel.setImageResource(R.drawable.man);
        else
            ivLevel.setImageResource(R.drawable.woman);

//        if(age>9)
//         ivIcontv.setText(""+age);
//         else
//         ivIcontv.setText("0"+age);
//
//
//        if(sex.equals("男"))
//        {
//            ivIcon.setText("{eam-e950} ");
//            ivIconLL.setBackgroundResource(R.drawable.shape_round_2corner_15_mc7);
//        }else
//            {
//                ivIconLL.setBackgroundResource(R.drawable.shape_round_2corner_15_mc5);
//                ivIcon.setText("{eam-e94f} ");
//            }


    }

    public static int[] ageNumber =
            {
                    R.drawable.age0,
                    R.drawable.age1,
                    R.drawable.age2,
                    R.drawable.age3,
                    R.drawable.age4,
                    R.drawable.age5,
                    R.drawable.age6,
                    R.drawable.age7,
                    R.drawable.age8,
                    R.drawable.age9
            };

}
