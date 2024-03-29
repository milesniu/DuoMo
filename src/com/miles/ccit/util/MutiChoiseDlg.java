package com.miles.ccit.util;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.miles.ccit.duomo.R;

public class MutiChoiseDlg {
    private Context mContext;
    private List<BaseMapObject> contactList;
    boolean[] selected;
    private int type = -1;


    public MutiChoiseDlg(Context contex, List<BaseMapObject> contacts, int type) {
        this.mContext = contex;
        contactList = contacts;
        this.type = type;
        selected = new boolean[contactList.size()];
    }

    public String getDlg(final EditText edit) {
        return getDlg(edit, null);
    }


    public String getDlg(final EditText edit, final EditText editgroup) {
        Dialog dialog = null;
        Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("联系人选择");
        builder.setIcon(R.drawable.ic_launcher);
        DialogInterface.OnMultiChoiceClickListener mutiListener = new DialogInterface.OnMultiChoiceClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int which,
                                boolean isChecked) {
                selected[which] = isChecked;
            }
        };
        String[] arrayc = new String[contactList.size()];
        for (int i = 0; i < contactList.size(); i++) {
            arrayc[i] = contactList.get(i).get("name").toString();
        }
        builder.setMultiChoiceItems(arrayc, selected, mutiListener);
        DialogInterface.OnClickListener btnListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                String selectedStr = "";
                for (int i = 0; i < selected.length; i++) {
                    if (selected[i] == true) {
                        selectedStr = selectedStr
                                + contactList.get(i).get("number").toString().split("#")[type == 2 ? 1 : 0] + ",";
                    }
                }

                selectedStr = selectedStr.equals("") ? "" : selectedStr.subSequence(0, selectedStr.length() - 1) + "";

                if (editgroup != null) {
                    if (type == O.NET && selectedStr.indexOf(",") != -1) {
                        editgroup.setVisibility(View.VISIBLE);
                    } else {
                        editgroup.setVisibility(View.GONE);
                    }
                }
                edit.setText(selectedStr);
            }
        };
        builder.setPositiveButton("确定", btnListener);
        builder.setNegativeButton("取消", null);
        dialog = builder.create();
        dialog.show();
        return null;
    }
}
