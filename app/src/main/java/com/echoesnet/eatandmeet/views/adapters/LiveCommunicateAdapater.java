package com.echoesnet.eatandmeet.views.adapters;

/**
 * conversation list adapter
 */
public class LiveCommunicateAdapater //extends ArrayAdapter<EMConversation>
{
    /*private static final String TAG = "ChatAllHistoryAdapter";
    private List<EMConversation> conversationList;
    private List<EMConversation> copyConversationList;
    private boolean notiyfyByFilter;

    private int primaryColor;
    private int secondaryColor;
    private int timeColor;
    private int primarySize;
    private int secondarySize;
    private float timeSize;

    public LiveCommunicateAdapater(Context context, int resource,
                                   List<EMConversation> objects)
    {
        super(context, resource, objects);
        conversationList = objects;
        copyConversationList = new ArrayList<>();
        copyConversationList.addAll(objects);
    }

    @Override
    public int getCount()
    {
        return conversationList.size();
    }

    @Override
    public EMConversation getItem(int arg0)
    {
        if (arg0 < conversationList.size())
        {
            return conversationList.get(arg0);
        }
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ease_row_chat_history, parent, false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null)
        {
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.unreadLabel = (TextView) convertView.findViewById(R.id.unread_msg_number);
            holder.levelView = (LevelView) convertView.findViewById(R.id.level_view);
            holder.iconTextView = (IconTextView) convertView.findViewById(R.id.tv_age);
            holder.message = (TextView) convertView.findViewById(R.id.message);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.headImgView = (LevelHeaderView) convertView.findViewById(R.id.headImgView);
            holder.msgState = convertView.findViewById(R.id.msg_state);
            holder.list_itease_layout = (RelativeLayout) convertView.findViewById(R.id.list_itease_layout);
            holder.motioned = (TextView) convertView.findViewById(R.id.mentioned);
            convertView.setTag(holder);
        }
        //设置点击背景--wb
        holder.list_itease_layout.setBackgroundResource(R.drawable.ease_mm_listitem);

        // get conversation
        EMConversation conversation = getItem(position);
        // get username or group id
        String username = conversation.conversationId();
        EaseUser user = EaseUserUtils.getUserInfo(username);
        if (user != null)
        {
            EaseUserUtils.setUserAvatar(getContext(), username, holder.headImgView);
            EaseUserUtils.setUserNick(username, holder.name);
            holder.levelView.setLevel(user.getLevel());
            setSex(user.getSex(), user.getAge(), holder);
        }

        if (conversation.getUnreadMsgCount() > 0)
        {
            // show unread message count
            if (conversation.getUnreadMsgCount() > 99)
                holder.unreadLabel.setText("99+");
            else
                holder.unreadLabel.setText(String.valueOf(conversation.getUnreadMsgCount()));


            holder.unreadLabel.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.unreadLabel.setVisibility(View.INVISIBLE);
        }

        if (conversation.getAllMsgCount() != 0)
        {
            // show the content of latest message
            EMMessage lastMessage = conversation.getLastMessage();
            String content = null;
            if (cvsListHelper != null)
            {
                content = cvsListHelper.onSetItemSecondaryText(lastMessage);
            }
            content = EaseCommonUtils.getMessageDigest(lastMessage, (this.getContext()));
            if (content != null)
            {
                String fromName = EaseUserUtils.getUserInfo(lastMessage.getFrom()).getNick();
                if (content.contains("通过了你的好友请求") && TextUtils.isEmpty(EaseUserUtils.getUserInfo(lastMessage.getFrom()).getuId()))
                {
                    String headImgView = null;
                    try
                    {
                        headImgView = lastMessage.getStringAttribute("headImgView");
                        String uid = lastMessage.getStringAttribute("uid");
                        String level = lastMessage.getStringAttribute("level");
                        String sex = lastMessage.getStringAttribute("sex");
                        String nickname = lastMessage.getStringAttribute("nickname");
                        String age = lastMessage.getStringAttribute("age", "");
                        if (!TextUtils.isEmpty(headImgView) && !TextUtils.isEmpty(uid)
                                && !TextUtils.isEmpty(level) && !TextUtils.isEmpty(sex) && !TextUtils.isEmpty(nickname))
                        {
                            holder.headImgView.setHeadImageByUrl(headImgView);
                            holder.levelView.setLevel(level);
                            holder.name.setText(nickname);
                            setSex(sex, age, holder);
                            holder.name.setTag(uid);
                        }
                    } catch (HyphenateException e)
                    {
                        e.printStackTrace();
                    }
                }
                // 设置内容
                if (content.toString().equals(fromName + "通过了你的好友请求，现在可以发起聊天了") && lastMessage.direct() == EMMessage.Direct.SEND)
                {
                    String nickName = EaseUserUtils.getUserInfo(lastMessage.getTo()).getNick();
                    if (!TextUtils.isEmpty(nickName))
                        content = SmileUtils.getSmiledText(getContext(), "我通过了" + nickName + "的好友请求，现在可以发起聊天了").toString();
                }
                //System.out.println("内容》"+content+" FromName> "+fromName +"来源》"+ lastMessage.direct());
                //红包情况
                if (content.toString().contains(fromName + "领取了你的红包") && lastMessage.direct() == EMMessage.Direct.SEND)
                {
                    String nickName = EaseUserUtils.getUserInfo(lastMessage.getTo()).getNick();
                    if (!TextUtils.isEmpty(nickName))
                        content = SmileUtils.getSmiledText(getContext(), "你领取了" + nickName + "的红包").toString();
                }
                if (content.toString().contains("你领取了[" + fromName + "]的红包") && lastMessage.direct() == EMMessage.Direct.RECEIVE)
                {
                    String nickName = EaseUserUtils.getUserInfo(lastMessage.getTo()).getNick();
                    if (!TextUtils.isEmpty(nickName))
                        content = SmileUtils.getSmiledText(getContext(), fromName + "领取了我的红包").toString();
                }
                //将文字转化为spannable--wb
                Spannable span = SmileUtils.getSmiledText(this.getContext(), content);
                holder.message.setText(span, BufferType.SPANNABLE);
            }
            holder.time.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
            if (lastMessage.direct() == EMMessage.Direct.SEND && lastMessage.status() == EMMessage.Status.FAIL)
            {
                holder.msgState.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.msgState.setVisibility(View.GONE);
            }
        }

        //set property
        holder.name.setTextColor(primaryColor);
        holder.message.setTextColor(secondaryColor);
        holder.time.setTextColor(timeColor);
        if (primarySize != 0)
            holder.name.setTextSize(TypedValue.COMPLEX_UNIT_PX, primarySize);
        if (secondarySize != 0)
            holder.message.setTextSize(TypedValue.COMPLEX_UNIT_PX, secondarySize);
        if (timeSize != 0)
            holder.time.setTextSize(TypedValue.COMPLEX_UNIT_PX, timeSize);

        return convertView;
    }

    private void setSex(String sex, String age, ViewHolder holder)
    {
        //System.out.println("===== sex>"+sex+"age>"+age+ "holder>"+holder);
        if (TextUtils.isEmpty(age))
        {
            age = "18";   // 如果取不到数据, 默认18.
        }
        if ("女".equals(sex))
        {
            holder.iconTextView.setBackgroundResource(R.drawable.shape_round_2corner_15_mc5);
            holder.iconTextView.setText(String.format("%s %s", "{eam-e94f}", age), BufferType.SPANNABLE);
        }
        else
        {
            holder.iconTextView.setBackgroundResource(R.drawable.shape_round_2corner_15_mc7);
            holder.iconTextView.setText(String.format("%s %s", "{eam-e950}", age), BufferType.SPANNABLE);
        }
    }

    @Override
    public void notifyDataSetChanged()
    {
        super.notifyDataSetChanged();
        copyConversationList.clear();
        copyConversationList.addAll(conversationList);
*//*        if (!notiyfyByFilter)
        {
            copyConversationList.clear();
            copyConversationList.addAll(conversationList);
            notiyfyByFilter = false;
        }*//*
    }


    public void setPrimaryColor(int primaryColor)
    {
        this.primaryColor = primaryColor;
    }

    public void setSecondaryColor(int secondaryColor)
    {
        this.secondaryColor = secondaryColor;
    }

    public void setTimeColor(int timeColor)
    {
        this.timeColor = timeColor;
    }

    public void setPrimarySize(int primarySize)
    {
        this.primarySize = primarySize;
    }

    public void setSecondarySize(int secondarySize)
    {
        this.secondarySize = secondarySize;
    }

    public void setTimeSize(float timeSize)
    {
        this.timeSize = timeSize;
    }

    private EaseConversationListHelper cvsListHelper;

    public void setCvsListHelper(EaseConversationListHelper cvsListHelper)
    {
        this.cvsListHelper = cvsListHelper;
    }

    private static class ViewHolder
    {
        *//**
         * who you chat with
         *//*
        TextView name;
        *//**
         * unread message count
         *//*
        TextView unreadLabel;
        *//**
         * content of last message
         *//*
        TextView message;
        *//**
         * time of last message
         *//*
        TextView time;
        *//**
         * headImgView
         *//*
        LevelHeaderView headImgView;
        *//**
         * status of last message
         *//*
        View msgState;
        *//**
         * layout
         *//*
        RelativeLayout list_itease_layout;
        TextView motioned;

        LevelView levelView;
        IconTextView iconTextView;

    }*/
}

