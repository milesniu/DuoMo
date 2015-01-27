package com.miles.ccit.duomo;

import java.lang.reflect.Method;
import java.util.HashMap;

import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.miles.ccit.database.GetData4DB;
import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.AbsCreatActivity;
import com.miles.ccit.util.BaseMapObject;

public class EmailInfoActivity extends AbsBaseActivity {

  private BaseMapObject map = null;
  private TextView text_Subject;
  private TextView text_Contact;
  private TextView text_Chaosong;
  private EditText text_content;
  private ImageView img_fj;

  @SuppressWarnings("unchecked")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_email_info);
    if (getIntent().getSerializableExtra("item") != null) {
      map =
          BaseMapObject.HashtoMyself((HashMap<String, Object>) getIntent().getSerializableExtra(
              "item"));
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.email_info, menu);
    return true;
  }

  @Override
  public void onClick(View v) {
    // TODO Auto-generated method stub
    switch (v.getId()) {
      case R.id.bt_left:
        this.finish();
        break;
      case R.id.image_fujian:
        CreatEMailActivity.showFile(mContext, map.get("attachmentsname").toString(),
            map.get("attachmentspath").toString());

        break;
    }
  }

  @Override
  public void initView() {
    // TODO Auto-generated method stub
    initBaseView("邮件详情");
    Btn_Left.setOnClickListener(this);
    Btn_Right.setVisibility(View.INVISIBLE);
    text_Subject = (TextView) findViewById(R.id.text_subject);
    text_content = (EditText) findViewById(R.id.edit_mailcontent);
    text_Contact = (TextView) findViewById(R.id.text_contact);
    text_Chaosong = (TextView) findViewById(R.id.text_chaosong);
    img_fj = (ImageView) findViewById(R.id.image_fujian);
    text_Subject.setText("主题：" + map.get("subject").toString());

    String[] csarr = map.get("csnumber").toString().split(",");
    String csStr = "";
    for (String i : csarr) {
      BaseMapObject csb = GetData4DB.getObjectByRowName(mContext, "contact", "number", i);
      if (csb != null) {
        csStr += csb.get("name").toString();
      } else {
        csStr += i;
      }
    }

    text_Chaosong.setText("抄送人：" + map.get("csnumber").toString());

    text_content.setText(map.get("mailcontent").toString());
    disableShowSoftInput(text_content);
    text_Contact.setText("联系人："
        + (map.get("name") == null ? map.get("number").toString() : map.get("name").toString()));
    if (map.get("haveattachments").toString().equals("1")) {
      img_fj.setVisibility(View.VISIBLE);
      if (AbsCreatActivity.isImage(AbsCreatActivity.getFileType(map.get("attachmentsname")
          .toString()))) {
        img_fj.setImageResource(R.drawable.image_enrichedimg);
        // img_fj.setBackgroundResource(R.drawable.image_enrichedbg);
      } else {
        img_fj.setImageResource(R.drawable.text_enriched);
        // img_fj.setBackgroundResource(R.drawable.image_enrichedbg);
      }
      img_fj.setOnClickListener(this);
    } else {

      img_fj.setVisibility(View.INVISIBLE);
    }
  }

  public void disableShowSoftInput(EditText editText) {
    if (android.os.Build.VERSION.SDK_INT <= 10) {
      editText.setInputType(InputType.TYPE_NULL);
    } else {
      Class<EditText> cls = EditText.class;
      Method method;
      try {
        method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
        method.setAccessible(true);
        method.invoke(editText, false);
      } catch (Exception e) {
        // TODO: handle exception
      }

      try {
        method = cls.getMethod("setSoftInputShownOnFocus", boolean.class);
        method.setAccessible(true);
        method.invoke(editText, false);
      } catch (Exception e) {
        // TODO: handle exception
      }
    }
  }

}
