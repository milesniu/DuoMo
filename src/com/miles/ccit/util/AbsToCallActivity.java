package com.miles.ccit.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.miles.ccit.adapter.ContactAdapter;
import com.miles.ccit.duomo.CallWaitActivity;
import com.miles.ccit.duomo.CreatContactActivity;
import com.miles.ccit.duomo.FileStatusActivity;
import com.miles.ccit.duomo.R;
import com.miles.ccit.net.APICode;
import com.redfox.voip_pro.RedfoxManager;

import java.util.List;
import java.util.Vector;

public abstract class AbsToCallActivity extends AbsBaseActivity {
    public String strNumber = "";
    private EditText editInputFrom;
    private ListView listview;
    private ContactAdapter adapter;
    public List<BaseMapObject> all;
    public static final int TOCALLVOICE = 0;        //声码话
    public static final int TOCALLWIREDVOICE = 1;    //有线语音
    public static final int TOCALLWIREDFILE = 2;    //有线文件
    public static final int TOIPVOICE = 3;            //IP语音
    public static final int TOIPVEDIO = 4;            //IP视频
    public static int CurrentType = -1;
    public static String Recv_Call = "1";//接听
    public static String Send_Call = "2";//拨打
    public static String Recv_Error = "3";//未接


    public List<BaseMapObject> getContact(String code) {
        if (code.equals("")) {
            return all;
        }
        List<BaseMapObject> have = new Vector<BaseMapObject>();
        for (BaseMapObject item : all) {
            if (item.get("number").toString().indexOf(code) != -1) {
                have.add(item);
            }
        }
        return have;
    }

    public void refreshList() {
        adapter = new ContactAdapter(mContext, getContact(strNumber), "name", "name", "number");
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {


                strNumber = getContact("").get(arg2).get("number").toString();
                // TODO Auto-generated method stub
//				toCall(TOCALLVOICE, getContact(strNumber).get(arg2).get("number").toString());
                if (CurrentType == TOCALLVOICE) {
                    strNumber = strNumber.split("#")[0];
//                    insertVoiceRecord(mContext,strNumber);
                    String innum = editInputFrom.getText().toString();
                    if (innum.length() > 0) {
                        editInputFrom.setText(innum + "*" + strNumber);
                    } else {
                        editInputFrom.setText(strNumber);
                    }
                } else if (CurrentType == TOCALLWIREDVOICE) {
                    insertWiredRecord(mContext, strNumber, null);
                } else if (CurrentType == TOCALLWIREDFILE) {
//                    Toast.makeText(mContext, "文件待操作", Toast.LENGTH_SHORT).show();
                    selectFile();
                } else if (CurrentType == TOIPVOICE) {
                    insertIPVoiceRecord(mContext, strNumber);

                } else if (CurrentType == TOIPVEDIO) {
                    insertIPVedioRecord(mContext, strNumber);

                }
            }
        });
    }

    public void insertNum(String num) {
        strNumber += num;
        editInputFrom.setText(strNumber);
        refreshList();
    }

    public boolean isHaveNum(String code) {
        for (BaseMapObject item : all) {
            if (item.get("number").toString().equals(code)) {
                return true;
            }
        }
        return false;
    }

    public void delNum() {
        if (strNumber.length() <= 0) {
            return;
        }
        strNumber = strNumber.substring(0, strNumber.length() - 1);
        editInputFrom.setText(strNumber);
        refreshList();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.bt_left:
                this.finish();
                break;
            case R.id.button0:
                insertNum("0");
                break;
            case R.id.button1:
                insertNum("1");
                break;
            case R.id.button2:
                insertNum("2");
                break;
            case R.id.button3:
                insertNum("3");
                break;
            case R.id.button4:
                insertNum("4");
                break;
            case R.id.button5:
                insertNum("5");
                break;
            case R.id.button6:
                insertNum("6");
                break;
            case R.id.button7:
                insertNum("7");
                break;
            case R.id.button8:
                insertNum("8");
                break;
            case R.id.button9:
                insertNum("9");
                break;
            case R.id.buttonx:
                insertNum("*");
                break;
            case R.id.buttony:
                insertNum("#");
                break;
            case R.id.buttoncall:
                if (strNumber.equals("")) {
                    MyLog.showToast(mContext, "请输入有效号码");
                    return;
                }
                if (CurrentType == TOCALLVOICE) {

                    insertVoiceRecord(mContext, editInputFrom.getText().toString());
                } else if (CurrentType == TOIPVOICE) {
                    insertIPVoiceRecord(mContext, strNumber);
                } else if (CurrentType == TOIPVEDIO) {
                    insertIPVedioRecord(mContext, strNumber);
                }
                break;
            case R.id.buttonadd:
                inserContact();
                break;
            case R.id.buttondel:
                delNum();
                break;
            case R.id.buttoncallvoice:
                if (strNumber.equals("")) {
                    MyLog.showToast(mContext, "请输入有效号码");
                    return;
                }
                if (CurrentType == TOCALLWIREDVOICE) {
                    insertWiredRecord(mContext, strNumber, null);
                } else if (CurrentType == TOCALLWIREDFILE) {
//                    MyLog.showToast(mContext, "选择文件");
                    selectFile();
                }
                break;

            //文件呼叫方式修改，此处不需要呼叫，注释
//		case R.id.buttoncallfile:
////			insertWiredRecord(strNumber, 1);
//			if(strNumber.equals(""))
//			{
//				MyLog.showToast(mContext, "请输入有效号码");
//				return;
//			}
//			CurrentType = TOCALLWIREDFILE;
//			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//			intent.setType("image/*");
//			intent.addCategory(Intent.CATEGORY_OPENABLE);
//			try
//			{
//				startActivityForResult(Intent.createChooser(intent, "请选择附件"), 0);
//			} catch (android.content.ActivityNotFoundException ex)
//			{
//				Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
//			}
//			break;


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1001:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    String path = FileUtils.getPath(this, uri);
//                    String name = AbsCreatActivity.getFileName(path);
                    startActivity(new Intent(mContext, FileStatusActivity.class).putExtra("path", path).putExtra("code", strNumber));
                    MyLog.showToast(mContext, path);
                    finish();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public static void insertVoiceRecord(Context contex, String code) {
        if (code.equals("")) {
            return;
        }
        BaseMapObject record = new BaseMapObject();
        record.put("id", null);
        record.put("number", code);
        record.put("status", "2");
        record.put("creattime", UnixTime.getStrCurrentUnixTime());
        record.put("priority", O.Priority);
        record.put("acknowledgemen", O.Acknowledgemen);

        record.InsertObj2DB(contex, "voicecoderecord");
        toCall(contex, code, null);
    }

    public static void insertIPVoiceRecord(Context contex, String code) {
        if (code.equals("")) {
            return;
        }
        BaseMapObject record = new BaseMapObject();
        record.put("id", null);
        record.put("number", code.indexOf("#") != -1 ? code.split("#")[0] : code);
        record.put("status", "2");
        record.put("creattime", UnixTime.getStrCurrentUnixTime());
        record.InsertObj2DB(contex, "ipvoice");
        toCall(contex, code, null);
    }

    public static void insertIPVedioRecord(Context contex, String code) {
        if (code.equals("")) {
            return;
        }
        BaseMapObject record = new BaseMapObject();
        record.put("id", null);
        record.put("number", code);
        record.put("status", "2");
        record.put("creattime", UnixTime.getStrCurrentUnixTime());

        record.InsertObj2DB(contex, "ipvideo");
        toCall(contex, code, null);
    }

    public static void insertWiredRecord(Context mcontext, String code, String filepath) {
        if (code.equals("")) {
            return;
        }
        BaseMapObject record = new BaseMapObject();
        record.put("id", null);
        record.put("number", code);
        record.put("sendtype", filepath == null ? "0" : "1");//语音0.文件1
        record.put("status", "2");//呼入成功/呼出成功/呼入失败/呼出失败(1,2,3,4)
        record.put("filepath", filepath);
        record.put("creattime", UnixTime.getStrCurrentUnixTime());

        record.InsertObj2DB(mcontext, "wiredrecord");
        if (code.indexOf("#") != -1) {
            code = code.split("#")[0];
        }
        toCall(mcontext, code, filepath);
    }

    public static void sendVoiceStarttoNet(String contact) {
        new SendDataTask().execute(APICode.SEND_VoiceCode + "", O.Account, contact);
    }

    public static void sendWiredStarttoNet(String contact) {
        new SendDataTask().execute(APICode.SEND_WiredVoice + "", O.Account, contact);
    }

    public static void toCall(Context contex, String code, String filepath) {
        if (CurrentType == TOCALLVOICE) {
            if (code.indexOf("#") != -1) {
                code = code.split("#")[0];
            }
            contex.startActivity(new Intent(contex, CallWaitActivity.class).putExtra("type", TOCALLVOICE).putExtra("code", code));
            sendVoiceStarttoNet(code);
        } else if (CurrentType == TOCALLWIREDVOICE) {
            contex.startActivity(new Intent(contex, CallWaitActivity.class).putExtra("type", TOCALLWIREDVOICE).putExtra("code", code));
            sendWiredStarttoNet(code);

        } else if (CurrentType == TOCALLWIREDFILE) {

        } else if (CurrentType == TOIPVOICE) {
            if (code.indexOf("#") != -1) {
                code = code.split("#")[0];
            }
            callIPAudio(code);

        } else if (CurrentType == TOIPVEDIO) {
            if (code.indexOf("#") != -1) {
                code = code.split("#")[0];
            }
            callIPVedio(code);


        }

    }


    private static void callIPAudio(String mAddress) {
        try {
            if (!RedfoxManager.getInstance().acceptCallIfIncomingPending()) {
                if (mAddress.length() > 0) {
                    RedfoxManager.getLcIfManagerNotDestroyedOrNull().setVideoPolicy(false, RedfoxManager.getLcIfManagerNotDestroyedOrNull().getVideoAutoAcceptPolicy());
                    RedfoxManager.getLcIfManagerNotDestroyedOrNull().setVideoPolicy(RedfoxManager.getLcIfManagerNotDestroyedOrNull().getVideoAutoInitiatePolicy(), false);
                    RedfoxManager.getInstance().newOutgoingCall(mAddress.toString(), mAddress.toString());
                }
            }
        } catch (Exception e) {
            RedfoxManager.getInstance().terminateCall();
        }

    }


    private static void callIPVedio(String mAddress) {
        try {
            if (!RedfoxManager.getInstance().acceptCallIfIncomingPending()) {
                if (mAddress.length() > 0) {
                    RedfoxManager.getLcIfManagerNotDestroyedOrNull().setVideoPolicy(true, RedfoxManager.getLcIfManagerNotDestroyedOrNull().getVideoAutoAcceptPolicy());
                    RedfoxManager.getLcIfManagerNotDestroyedOrNull().setVideoPolicy(RedfoxManager.getLcIfManagerNotDestroyedOrNull().getVideoAutoInitiatePolicy(), true);
                    RedfoxManager.getInstance().newOutgoingCall(mAddress.toString(), mAddress.toString());

                }
            }
        } catch (Exception e) {
            RedfoxManager.getInstance().terminateCall();
        }
    }

    public void inserContact() {
        if (strNumber.equals("")) {
            return;
        } else if (isHaveNum(strNumber)) {
            MyLog.showToast(mContext, "此号码已存在，不能添加。");
        } else {
            startActivity(new Intent(mContext, CreatContactActivity.class).putExtra("number", strNumber));
        }
    }

    @Override
    public void initView() {
        // TODO Auto-generated method stub
        initBaseView("拨号");
        Btn_Left.setOnClickListener(this);
        Btn_Right.setVisibility(View.INVISIBLE);
        editInputFrom = (EditText) findViewById(R.id.edit_form);
        listview = (ListView) findViewById(R.id.listview_content);
        findViewById(R.id.button0).setOnClickListener(this);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
        findViewById(R.id.button5).setOnClickListener(this);
        findViewById(R.id.button6).setOnClickListener(this);
        findViewById(R.id.button7).setOnClickListener(this);
        findViewById(R.id.button8).setOnClickListener(this);
        findViewById(R.id.button9).setOnClickListener(this);
        findViewById(R.id.buttonx).setOnClickListener(this);
        findViewById(R.id.buttony).setOnClickListener(this);
        findViewById(R.id.buttonadd).setOnClickListener(this);
        findViewById(R.id.buttondel).setOnClickListener(this);

        refreshList();

    }

}
