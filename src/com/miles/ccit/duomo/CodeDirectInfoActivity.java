package com.miles.ccit.duomo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.miles.ccit.util.AbsBaseActivity;
import com.miles.ccit.util.AbsCreatCodeActivity;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.JSONUtil;
import com.miles.ccit.util.MyLog;

public class CodeDirectInfoActivity extends AbsBaseActivity
{

	private BaseMapObject map = null;
	private BaseMapObject detail = new BaseMapObject();
	TextView text_content = null;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_code_direct_info);
		if (getIntent().getSerializableExtra("item") != null)
		{
			map = BaseMapObject.HashtoMyself((HashMap<String, Object>) getIntent().getSerializableExtra("item"));
			
		}
	}

	
	private void analyContent()
	{
		String[] param = map.get("codecontent").toString().split("&");
		for(String s : param)
		{
			String[] sp = s.split("=");
			if(sp.length==2)
				detail.put(sp[0], sp[1]);
		}
		
		MyLog.SystemOut(detail.toString());
		showDetail();
	}
	
	private String getcodenumName(String codetype,String code)
	{
		Map<String,Object> datasky = JSONUtil.getMapFromJson(getassetsCode(mContext,"junbiaocode.txt"));
		List<Map<String,Object>> skycode = (List<Map<String,Object>>)datasky.get(codetype);
		for(Map<String,Object> t:skycode)
		{
			if(t.get("code").toString().equals(code))
			{
				return t.get("name").toString();
			}
		}
		return code;
	}
	String analydata = "";
	private void showDetail()
	{
//		Map<String,Object> data = JSONUtil.getMapFromJson(AbsCreatCodeActivity.getassetsCode(mContext,"actioncode.txt"));
		switch(Integer.parseInt(detail.get("P1").toString()))
		{
		case 0:
			analydata+="短波数据类型:"+ "军标"+"\r\n\r\n";
			switch(Integer.parseInt(detail.get("P2").toString()))
			{
			case 0:
				analydata +="军标类型:"+ "空中目标"+"\r\n\r\n";
				analydata+="军标编号："+getcodenumName("skycode", detail.get("P3").toString())+"\r\n\r\n";
				analydata+=(detail.get("P4")==null?"":"军标名称："+detail.get("P4").toString()+"\r\n\r\n");
				analydata+="军标颜色："+getcodenumName("jbcolor", detail.get("P5").toString())+"\r\n\r\n";
				analydata+="时间戳："+detail.get("P6").toString()+"\r\n\r\n";
				
				break;
			case 1:
				analydata +="军标类型:"+ "地面目标"+"\r\n\r\n";
				analydata+="军标编号："+getcodenumName("earthcode", detail.get("P3").toString())+"\r\n\r\n";
				analydata+=(detail.get("P4")==null?"":"军标名称："+detail.get("P4").toString()+"\r\n\r\n");
				analydata+="军标颜色："+getcodenumName("jbcolor", detail.get("P5").toString())+"\r\n\r\n";
				analydata+="时间戳："+detail.get("P6").toString()+"\r\n\r\n";
				
				break;
			case 10:
				analydata +="军标类型:"+ "水面目标"+"\r\n\r\n";
				analydata+="军标编号："+getcodenumName("watercode", detail.get("P3").toString())+"\r\n\r\n";
				analydata+=(detail.get("P4")==null?"":"军标名称："+detail.get("P4").toString()+"\r\n\r\n");
				analydata+="军标颜色："+getcodenumName("jbcolor", detail.get("P5").toString())+"\r\n\r\n";
				analydata+="时间戳："+detail.get("P6").toString()+"\r\n\r\n";
				
				break;
			case 11:
				analydata +="军标类型:"+ "其他目标"+"\r\n\r\n";
				analydata+="军标编号："+getcodenumName("skycode", detail.get("P3").toString())+"\r\n\r\n";
				analydata+=(detail.get("P4")==null?"":"军标名称："+detail.get("P4").toString()+"\r\n\r\n");
				analydata+="军标颜色："+getcodenumName("jbcolor", detail.get("P5").toString())+"\r\n\r\n";
				analydata+="时间戳："+detail.get("P6").toString()+"\r\n\r\n";
				
				break;
			}
			break;
		case 1:
			analydata+="短波数据类型:"+ "行动命令"+"\r\n\r\n";
			switch(Integer.parseInt(detail.get("P2").toString()))
			{
			case 0:
				analydata +="行动命令类型:"+ "集结"+"\r\n\r\n";
				break;
			case 1:
				analydata +="行动命令类型:"+ "疏散"+"\r\n\r\n";
				break;
			case 10:
				analydata +="行动命令类型:"+ "进攻"+"\r\n\r\n";
				break;
			case 11:
				analydata +="行动命令类型:"+ "撤退"+"\r\n\r\n";
				break;
			}
			analydata+=(detail.get("P3")==null?"":"命令名称："+detail.get("P4").toString()+"\r\n\r\n");
			analydata+=("应急队伍编号："+detail.get("P4").toString())+"\r\n\r\n";
			analydata+=(detail.get("P5")==null?"":"目的地名称："+detail.get("P5").toString()+"\r\n\r\n");
			analydata+=(detail.get("P6")==null?"":"目的地介绍："+detail.get("P6").toString()+"\r\n\r\n");
			analydata+=(detail.get("P8")==null?"":"开始执行时间："+detail.get("P8").toString()+"\r\n\r\n");
			analydata+=(detail.get("P9")==null?"":"间隔时间："+detail.get("P9").toString()+"\r\n\r\n");
			
			break;
		case 10:
			analydata+="短波数据类型:"+ "行动命令反馈"+"\r\n\r\n";
			switch(Integer.parseInt(detail.get("P3").toString()))
			{
			case 0:
				analydata +="反馈类型:"+ "待执行"+"\r\n\r\n";
				break;
			case 1:
				analydata +="反馈类型:"+ "已执行"+"\r\n\r\n";
				break;
			case 10:
				analydata +="反馈类型:"+ "不能执行"+"\r\n\r\n";
				break;
			case 11:
				analydata +="反馈类型:"+ "执行完毕"+"\r\n\r\n";
				break;
			}
			analydata+=(detail.get("P2")==null?"":"命令名称："+detail.get("P2").toString()+"\r\n\r\n");
			analydata+=(detail.get("P4")==null?"":"反馈描述信息："+detail.get("P4").toString()+"\r\n\r\n");
			analydata+=(detail.get("P5")==null?"":"反馈时间："+detail.get("P5").toString()+"\r\n\r\n");
			
			break;
		case 11:
			analydata+="短波数据类型:"+ "资源申请"+"\r\n\r\n";
			
			analydata+=(detail.get("P2")==null?"":"应急队伍编号："+detail.get("P2").toString()+"\r\n\r\n");
			analydata+=(detail.get("P3")==null?"":"物资名称："+detail.get("P3").toString()+"\r\n\r\n");
			analydata+=(detail.get("P4")==null?"":"物资数量："+detail.get("P4").toString()+"\r\n\r\n");
			analydata+=(detail.get("P5")==null?"":"物资单位："+detail.get("P5").toString()+"\r\n\r\n");
			analydata+=(detail.get("P6")==null?"":"受领物资地："+detail.get("P6").toString()+"\r\n\r\n");
			
			break;
		case 100:
			analydata+="短波数据类型:"+ "威胁报警通报"+"\r\n\r\n";
			switch(Integer.parseInt(detail.get("P2").toString()))
			{
			case 0:
				analydata +="威胁报警类型:"+ "规避区"+"\r\n\r\n";
				break;
			case 1:
				analydata +="威胁报警类型:"+ "化学污染区"+"\r\n\r\n";
				break;
			case 10:
				analydata +="威胁报警类型:"+ "核污染区"+"\r\n\r\n";
				break;
			}
			
			switch(Integer.parseInt(detail.get("P3").toString()))
			{
			case 0:
				analydata +="威胁区形状:"+ "图形"+"\r\n\r\n";
				break;
			case 1:
				analydata +="威胁区形状:"+ "椭圆"+"\r\n\r\n";
				break;
			case 10:
				analydata +="威胁区形状:"+ "正方形"+"\r\n\r\n";
				break;
			case 11:
				analydata +="威胁区形状:"+ "长方形"+"\r\n\r\n";
				break;
			case 100:
				analydata +="威胁区形状:"+ "多边形"+"\r\n\r\n";
				break;
			}
			analydata+=(detail.get("P4")==null?"":"威胁区说明："+detail.get("P4").toString()+"\r\n\r\n");
			
			break;
		case 101:
			analydata+="短波数据类型:"+ "警报控制"+"\r\n\r\n";
			switch(Integer.parseInt(detail.get("P2").toString()))
			{
			case 0:
				analydata +="威胁报警类型:"+ "预警"+"\r\n\r\n";
				break;
			case 1:
				analydata +="威胁报警类型:"+ "空袭"+"\r\n\r\n";
				break;
			case 10:
				analydata +="威胁报警类型:"+ "解警"+"\r\n\r\n";
				break;
			case 11:
				analydata +="威胁报警类型:"+ "消防"+"\r\n\r\n";
				break;
			}
			
			switch(Integer.parseInt(detail.get("P3").toString()))
			{
			case 0:
				analydata +="警报方式:"+ "文字报警"+"\r\n\r\n";
				break;
			case 1:
				analydata +="警报方式:"+ "声音报警"+"\r\n\r\n";
				break;
			}
			analydata+=(detail.get("P4")==null?"":"警报控制说明："+detail.get("P4").toString()+"\r\n\r\n");
			
			break;
		case 110:
			analydata+="短波数据类型:"+ "北斗报文"+"\r\n\r\n";
			
			analydata+=(detail.get("P2")==null?"":"北斗入网卡号："+detail.get("P2").toString()+"\r\n\r\n");
			analydata+=(detail.get("P3")==null?"":"时间戳："+detail.get("P3").toString()+"\r\n\r\n");
			
			break;
		}
		text_content.setText(analydata);
	}
	
	@Override
	public void onClick(View arg0)
	{
		// TODO Auto-generated method stub
		switch (arg0.getId())
		{
		case R.id.bt_left:
			this.finish();
			break;
		}
	}

	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		initBaseView("代码指挥详情");
		Btn_Left.setOnClickListener(this);
		Btn_Right.setVisibility(View.INVISIBLE);
		text_content = (TextView)findViewById(R.id.text_content);
		analyContent();
	}
}
