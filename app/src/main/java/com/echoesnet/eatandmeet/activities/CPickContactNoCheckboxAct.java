/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.echoesnet.eatandmeet.activities;

public class CPickContactNoCheckboxAct extends BaseActivity// implements IPickContactView
{
//    private static final String TAG = CPickContactNoCheckboxAct.class.getSimpleName();
//    private ListView listView;
//    private SideBar sidebar;
//    protected EaseContactAdapter contactAdapter;
//    private List<EaseUser> contactList;
//    protected String uid;
//    private TopBarSwitch topBarSwitch;
//
//    private ImpPickContactView impPickContactView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//        CommonUtils.setTransparentTopBar(this);
//        impPickContactView = new ImpPickContactView(CPickContactNoCheckboxAct.this, this);
//        //uid = savedInstanceState.getString("uid");
//        setContentView(R.layout.em_activity_pick_contact_no_checkbox);
//        topBarSwitch = (TopBarSwitch) findViewById(R.id.rl_title_bar);
//        topBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
//        {
//            @Override
//            public void leftClick(View view)
//            {
//                finish();
//            }
//
//            @Override
//            public void right2Click(View view)
//            {
//
//            }
//        }).setText(getResources().getString(R.string.select_contacts));
//        listView = (ListView) findViewById(R.id.list);
//        sidebar = (SideBar) findViewById(R.id.sidebar);
//        sidebar.setListView(listView);
//        LinearLayout empty = (LinearLayout) findViewById(com.hyphenate.easeui.R.id.empty_view);
//        ((TextView) empty.findViewById(R.id.tv_default_des)).setText("暂时没有好友");
//        listView.setEmptyView(empty);
//        contactList = new ArrayList<EaseUser>();
//        // get contactlist
//        // getContactList();
//        // set adapter
//        contactAdapter = new EaseContactAdapter(this, R.layout.ease_row_contact, contactList);
//
//        if (impPickContactView != null)
//        {
//            impPickContactView.asyncFetchContactsFromServer(this);
//        }
//
//        listView.setAdapter(contactAdapter);
//        listView.setOnItemClickListener(new OnItemClickListener()
//        {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
//            {
//                onListItemClick(position);
//            }
//        });
//
//    }
//
//    protected void onListItemClick(int position)
//    {
//        setResult(RESULT_OK, new Intent().putExtra("username", contactAdapter.getItem(position)
//                .getUsername()));
//        finish();
//    }
//
//    private void getContactList()
//    {
//        contactList.clear();
//        Map<String, EaseUser> users = HuanXinIMHelper.getInstance().getContactList();
//        Iterator<Entry<String, EaseUser>> iterator = users.entrySet().iterator();
//        while (iterator.hasNext())
//        {
//            Entry<String, EaseUser> entry = iterator.next();
//            if (!entry.getKey().equals(Constant.NEW_FRIENDS_USERNAME) && !entry.getKey().equals(Constant.GROUP_USERNAME) && !entry.getKey().equals(Constant.CHAT_ROOM) && !entry.getKey().equals(Constant.CHAT_ROBOT))
//            {
//                Logger.t("------------>").d("------------>" + entry.getValue().getuId() + ",CForwardMessageAct.uid:" + uid);
//                if (entry.getValue().getuId().equals(uid))
//                {
//                    continue;
//                }
//                contactList.add(entry.getValue());
//            }
//        }
//        // sort
//        sortContactList();
//    }
//
//    private void sortContactList()
//    {
//        Collections.sort(contactList, new Comparator<EaseUser>()
//        {
//
//            @Override
//            public int compare(EaseUser lhs, EaseUser rhs)
//            {
//                if (lhs.getInitialLetter().equals(rhs.getInitialLetter()))
//                {
//                    return lhs.getNick().compareTo(rhs.getNick());
//                }
//                else
//                {
//                    if ("#".equals(lhs.getInitialLetter()))
//                    {
//                        return 1;
//                    }
//                    else if ("#".equals(rhs.getInitialLetter()))
//                    {
//                        return -1;
//                    }
//                    return lhs.getInitialLetter().compareTo(rhs.getInitialLetter());
//                }
//
//            }
//        });
//    }
//
//    @Override
//    public void requestNetError(Call call, Exception e, String exceptSource)
//    {
//        NetHelper.handleNetError(this, null, exceptSource, e);
//    }
//
//    @Override
//    public void getContactList(String response)
//    {
//        try
//        {
//            JSONObject jsonResponse = new JSONObject(response);
//            String messageJson = jsonResponse.getString("messageJson");
//            JSONObject object = new JSONObject(messageJson);
//            int status = object.getInt("status");
//            if (status == 0)
//            {
//                JSONArray friendLst = new JSONArray(object.getString("body"));
//
//                for (int i = 0; i < friendLst.length(); i++)
//                {
//                    JSONObject userObj = friendLst.getJSONObject(i);
//                    EaseUser user = new EaseUser(userObj.getString("imuId").toLowerCase());
//                    user.setuId(userObj.getString("uId"));
//                    user.setAvatar(userObj.getString("uphUrl"));
//                    user.setLevel(userObj.getString("level"));
//                    user.setSex(userObj.getString("sex"));
//                    user.setAge(userObj.getString("age"));
//                    String remark = userObj.getString("remark");
//                    if (TextUtils.isEmpty(remark))
//                    {
//                        user.setNickname(userObj.getString("nicName"));
//                    }
//                    else
//                    {
//                        user.setNickname(remark);
//                    }
//                    contactList.add(user);
//
//                }
//                //sort排序
//                sortContactList();
//                contactAdapter.notifyDataSetChanged();
//            }
//            else
//            {
//                String code = object.getString("code");
//                if (!ErrorCodeTable.handleErrorCode(code, this))
//                    ToastUtils.showShort(ErrorCodeTable.parseErrorCode(code));
//                Logger.t(TAG).d("错误码为：%s", code);
//            }
//        } catch (JSONException e)
//        {
//            Logger.t(TAG).d(e.getMessage());
//            e.printStackTrace();
//        }
//    }
}
