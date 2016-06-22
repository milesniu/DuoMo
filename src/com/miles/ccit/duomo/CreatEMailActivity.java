package com.miles.ccit.duomo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.util.AbsCreatActivity;
import com.miles.ccit.util.FileUtils;
import com.miles.ccit.util.MutiChoiseDlg;

import java.io.File;

public class CreatEMailActivity extends AbsCreatActivity {

    private EditText edit_inputSubject;
    private EditText edit_inputmailContent;
    private Button Btn_Fujian;
    private ImageView img_Fj;
    private EditText edit_inputChaosong;
    private Button Btn_Chaosong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creat_email);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.creat_email, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.bt_left:
                this.finish();
                break;
            case R.id.bt_right:
                String contact = edit_inputContact.getText().toString();
                String chaosong = edit_inputChaosong.getText().toString();
                String subject = edit_inputSubject.getText().toString();
                String content = edit_inputmailContent.getText().toString();
                String fj = img_Fj.getTag(R.id.img_path) == null ? null : img_Fj.getTag(R.id.img_path).toString();
                if (contact.equals("")) {
                    Toast.makeText(this, "联系人不能为空。", Toast.LENGTH_SHORT).show();
                    return;
                } else if (subject.equals("")) {
                    Toast.makeText(this, "主题不能为空。", Toast.LENGTH_SHORT).show();
                    return;
                } else if (content.equals("")) {
                    Toast.makeText(this, "邮件内容不能为空。", Toast.LENGTH_SHORT).show();
                    return;
                }

                sendEmail(contact, chaosong, subject, content, fj);
                this.finish();
                break;
            case R.id.bt_selectfj:
                if (img_Fj.getVisibility() == View.INVISIBLE) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    try {
                        startActivityForResult(Intent.createChooser(intent, "请选择附件"), 0);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Btn_Fujian.setBackgroundResource(R.drawable.fujian);
                    img_Fj.setVisibility(View.INVISIBLE);
                    img_Fj.setTag(R.id.img_name, null);
                    img_Fj.setTag(R.id.img_path, null);
                }
                break;
            case R.id.bt_addcontact:
                new MutiChoiseDlg(mContext, GetData4DB.getObjectListData(mContext, "contact", "type", "0"), 0).getDlg(edit_inputContact);
                break;
            case R.id.bt_addchasong:
                new MutiChoiseDlg(mContext, GetData4DB.getObjectListData(mContext, "contact", "type", "0"), 0).getDlg(edit_inputChaosong);

                break;
            case R.id.image_fujian:
                showFile(mContext, img_Fj.getTag(R.id.img_name).toString(), img_Fj.getTag(R.id.img_path).toString());
                break;

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    String path = FileUtils.getPath(this, uri);
                    File f = new File(path);
                    if (f.exists() && f.isFile()) {
                        long lenth = f.length();
                        if (lenth > 10240) {
                            Toast.makeText(mContext, "附件大小不允许超过10KB", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        return;
                    }
                    String name = getFileName(path);
                    img_Fj.setTag(R.id.img_name, name);
                    img_Fj.setTag(R.id.img_path, path);
                    img_Fj.setVisibility(View.VISIBLE);
                    Btn_Fujian.setBackgroundResource(R.drawable.delfj);
                    if (isImage(getFileType(name))) {

                        img_Fj.setImageResource(R.drawable.image_enriched);
                    } else {
                        img_Fj.setImageResource(R.drawable.text_enriched);
                    }

                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void initView() {
        // TODO Auto-generated method stub
        initBaseView("新建邮件");
        Btn_Right.setBackgroundResource(R.drawable.sendmail);
        edit_inputSubject = (EditText) findViewById(R.id.edit_subject);
        edit_inputmailContent = (EditText) findViewById(R.id.edit_mailcontent);
        Btn_Fujian = (Button) findViewById(R.id.bt_selectfj);
        Btn_addContact = (Button) findViewById(R.id.bt_addcontact);
        edit_inputChaosong = (EditText) findViewById(R.id.edit_chaosong);
        edit_inputContact = (EditText) findViewById(R.id.edit_concotact);
        Btn_Chaosong = (Button) findViewById(R.id.bt_addchasong);
        img_Fj = (ImageView) findViewById(R.id.image_fujian);
        img_Fj.setOnClickListener(this);
        Btn_Fujian.setOnClickListener(this);
        Btn_addContact.setOnClickListener(this);
        Btn_Chaosong.setOnClickListener(this);
    }

}
