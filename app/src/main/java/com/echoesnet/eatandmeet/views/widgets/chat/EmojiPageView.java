package com.echoesnet.eatandmeet.views.widgets.chat;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.datamodel.EmojiGroupEntity;
import com.echoesnet.eatandmeet.models.datamodel.EmojiIcon;
import com.echoesnet.eatandmeet.utils.IMUtils.EamSmileUtils;
import com.echoesnet.eatandmeet.views.adapters.EmojiconGridAdapter;
import com.echoesnet.eatandmeet.views.adapters.EmojiconPagerAdapter;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by yqh on 2017/7/17.
 */

public class EmojiPageView extends ViewPager
{
    private static final String TAG = EmojiPageView.class.getSimpleName();
    private Context context;
    private List<EmojiGroupEntity> groupEntities;
    private List<EmojiIcon> totalEmojiconList = new ArrayList<EmojiIcon>();

    private PagerAdapter pagerAdapter;

    private int emojiconRows = 3;
    private int emojiconColumns = 7;

    private int bigEmojiconRows = 2;
    private int bigEmojiconColumns = 4;

    private int firstGroupPageSize;

    private int maxPageCount;
    private int previousPagerPosition;
    private EmojiconPagerViewListener pagerViewListener;
    private List<View> viewpages;

    public EmojiPageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
    }

    public EmojiPageView(Context context)
    {
        this(context, null);
    }


    public void init(List<EmojiGroupEntity> emojiconGroupList, int emijiconColumns, int bigEmojiconColumns)
    {
        if (emojiconGroupList == null)
        {
            throw new RuntimeException("emojiconGroupList is null");
        }

        this.groupEntities = emojiconGroupList;
        this.emojiconColumns = emijiconColumns;
        this.bigEmojiconColumns = bigEmojiconColumns;

        viewpages = new ArrayList<View>();

        for (int i = 0; i < groupEntities.size(); i++)
        {
            EmojiGroupEntity group = groupEntities.get(i);
            List<EmojiIcon> groupEmojicons = group.getEmojiconList();
            totalEmojiconList.addAll(groupEmojicons);
            List<View> gridViews = getGroupGridViews(group);
            if (i == 0)
            {
                firstGroupPageSize = gridViews.size();
            }
            maxPageCount = Math.max(gridViews.size(), maxPageCount);
            viewpages.addAll(gridViews);
        }

        pagerAdapter = new EmojiconPagerAdapter(viewpages);
        setAdapter(pagerAdapter);
        addOnPageChangeListener(new EmojiPagerChangeListener());
        //setOnPageChangeListener(new EmojiPagerChangeListener());

        if (pagerViewListener != null)
        {
            pagerViewListener.onPagerViewInited(maxPageCount, firstGroupPageSize);
        }
    }

    public void setPagerViewListener(EmojiconPagerViewListener pagerViewListener)
    {
        this.pagerViewListener = pagerViewListener;
    }


    /**
     * set emojicon group position
     *
     * @param position
     */
    public void setGroupPostion(int position)
    {
        if (getAdapter() != null && position >= 0 && position < groupEntities.size())
        {
            //System.out.println("环信2》" + position);
            int count = 0;
            for (int i = 0; i < position; i++)
            {
                count += getPageSize(groupEntities.get(i));
            }
            //System.out.println("环信3》" + count);
            setCurrentItem(count);
        }
    }

    /**
     * get emojicon group gridview list
     *
     * @param groupEntity
     * @return
     */
    public List<View> getGroupGridViews(EmojiGroupEntity groupEntity)
    {
        List<EmojiIcon> emojiconList = groupEntity.getEmojiconList();
        if (emojiconList == null)
            return new ArrayList<>();
        System.out.print("emojiconList: " + emojiconList);
        int itemSize = emojiconColumns * emojiconRows - 1;//
        int totalSize = emojiconList.size();
        EmojiIcon.Type emojiType = groupEntity.getType();
        if (emojiType == EmojiIcon.Type.BIG_EXPRESSION)
        {
            itemSize = bigEmojiconColumns * bigEmojiconRows;
        }
        if (emojiType == EmojiIcon.Type.NORMAL_AS_EXPRESSION)
        {
            Logger.t(TAG).d("--->小海豚个数2" + emojiconColumns * emojiconRows);
            itemSize = emojiconColumns * emojiconRows;
        }
        int pageSize = totalSize % itemSize == 0 ? totalSize / itemSize : totalSize / itemSize + 1;
        List<View> views = new ArrayList<>();
        for (int i = 0; i < pageSize; i++)
        {
            View view = View.inflate(context, R.layout.ease_expression_gridview, null);
            GridView gv = (GridView) view.findViewById(R.id.gridview);
            if (emojiType == EmojiIcon.Type.BIG_EXPRESSION)
            {
                gv.setNumColumns(bigEmojiconColumns);
            }
            else
            {
                gv.setNumColumns(emojiconColumns);
            }
            List<EmojiIcon> list = new ArrayList<>();
            if (i != pageSize - 1)
            {
                list.addAll(emojiconList.subList(i * itemSize, (i + 1) * itemSize));
            }
            else
            {
                list.addAll(emojiconList.subList(i * itemSize, totalSize));
            }
            if (emojiType != EmojiIcon.Type.BIG_EXPRESSION && emojiType != EmojiIcon.Type.NORMAL_AS_EXPRESSION)
            {
                EmojiIcon deleteIcon = new EmojiIcon();
                deleteIcon.setEmojiText(EamSmileUtils.DELETE_KEY);
                list.add(deleteIcon);
            }
            final EmojiconGridAdapter gridAdapter = new EmojiconGridAdapter(context, 1, list, emojiType);
            gv.setAdapter(gridAdapter);
            gv.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    EmojiIcon emojicon = gridAdapter.getItem(position);
                    if (pagerViewListener != null)
                    {
                        String emojiText = emojicon.getEmojiText();
                        if (emojiText != null && emojiText.equals(EamSmileUtils.DELETE_KEY))
                        {
                            pagerViewListener.onDeleteImageClicked();
                        }
                        else
                        {
                            pagerViewListener.onExpressionClicked(emojicon);
                        }
                    }

                }
            });

            views.add(view);
        }
        return views;
    }


    /**
     * add emojicon group
     *
     * @param groupEntity
     */
    public void addEmojiconGroup(EmojiGroupEntity groupEntity, boolean notifyDataChange)
    {
        int pageSize = getPageSize(groupEntity);
        if (pageSize > maxPageCount)
        {
            maxPageCount = pageSize;
            if (pagerViewListener != null && pagerAdapter != null)
            {
                pagerViewListener.onGroupMaxPageSizeChanged(maxPageCount);
            }
        }
        viewpages.addAll(getGroupGridViews(groupEntity));
        if (pagerAdapter != null && notifyDataChange)
        {
            pagerAdapter.notifyDataSetChanged();
        }
    }

    /**
     * remove emojicon group
     *
     * @param position
     */
    public void removeEmojiconGroup(int position)
    {
        if (position > groupEntities.size() - 1)
        {
            return;
        }
        if (pagerAdapter != null)
        {
            pagerAdapter.notifyDataSetChanged();
        }
    }

    /**
     * get size of pages
     *
     * @return
     */
    private int getPageSize(EmojiGroupEntity groupEntity)
    {
        List<EmojiIcon> emojiconList = groupEntity.getEmojiconList();
        if (emojiconList == null)
            emojiconList = new ArrayList<>();
        int itemSize = emojiconColumns * emojiconRows - 1;//
        int totalSize = emojiconList.size();
        EmojiIcon.Type emojiType = groupEntity.getType();
        if (emojiType == EmojiIcon.Type.BIG_EXPRESSION)
        {
            itemSize = bigEmojiconColumns * bigEmojiconRows;
        }
        else if (emojiType == EmojiIcon.Type.NORMAL_AS_EXPRESSION)
        {
            Logger.t(TAG).d("--->小海豚个数1" + emojiconColumns * emojiconRows);
            itemSize = emojiconColumns * emojiconRows;
        }
        Logger.t(TAG).d("---------------------------》totalSize % itemSize:" + totalSize % itemSize + " | totalSize / itemSize:" + totalSize / itemSize);
        int pageSize = totalSize % itemSize == 0 ? totalSize / itemSize : totalSize / itemSize + 1;

        return pageSize;
    }

    private class EmojiPagerChangeListener implements OnPageChangeListener
    {
        @Override
        public void onPageSelected(int position)
        {
            int endSize = 0;
            int groupPosition = 0;
            for (EmojiGroupEntity groupEntity : groupEntities)
            {
                //System.out.println("环信4》"+groupEntity.getName());
                int groupPageSize = getPageSize(groupEntity);
                //if the position is in current group
                if (endSize + groupPageSize > position)
                {
                    //this is means user swipe to here from previous page
                    if (previousPagerPosition - endSize < 0)
                    {
                        if (pagerViewListener != null)
                        {
                            pagerViewListener.onGroupPositionChanged(groupPosition, groupPageSize);
                            pagerViewListener.onGroupPagePostionChangedTo(0);
                        }
                        break;
                    }
                    //this is means user swipe to here from back page
                    if (previousPagerPosition - endSize >= groupPageSize)
                    {
                        if (pagerViewListener != null)
                        {
                            pagerViewListener.onGroupPositionChanged(groupPosition, groupPageSize);
                            pagerViewListener.onGroupPagePostionChangedTo(position - endSize);
                        }
                        break;
                    }

                    //page changed
                    if (pagerViewListener != null)
                    {
                        Logger.t(TAG).d("---------------------------》1previousPagerPosition:" + previousPagerPosition + " | endSize:" + endSize + " | position:" + position);
                        pagerViewListener.onGroupInnerPagePostionChanged(previousPagerPosition - endSize, position - endSize);
                    }
                    break;
                }
                groupPosition++;
                endSize += groupPageSize;
            }

            previousPagerPosition = position;

            Logger.t(TAG).d("---------------------------》2previousPagerPosition:" + previousPagerPosition + " | endSize:" + endSize + " | position:" + position);

        }

        @Override
        public void onPageScrollStateChanged(int arg0)
        {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2)
        {
        }
    }


    public interface EmojiconPagerViewListener
    {
        /**
         * pagerview initialized
         *
         * @param groupMaxPageSize     --max pages size
         * @param firstGroupPageSize-- size of first group pages
         */
        void onPagerViewInited(int groupMaxPageSize, int firstGroupPageSize);

        /**
         * group position changed
         *
         * @param groupPosition--group   position
         * @param pagerSizeOfGroup--page size of group
         */
        void onGroupPositionChanged(int groupPosition, int pagerSizeOfGroup);

        /**
         * page position changed
         *
         * @param oldPosition
         * @param newPosition
         */
        void onGroupInnerPagePostionChanged(int oldPosition, int newPosition);

        /**
         * group page position changed
         *
         * @param position
         */
        void onGroupPagePostionChangedTo(int position);

        /**
         * max page size changed
         *
         * @param maxCount
         */
        void onGroupMaxPageSizeChanged(int maxCount);

        void onDeleteImageClicked();

        void onExpressionClicked(EmojiIcon emojicon);

    }

}
