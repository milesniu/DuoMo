package com.miles.ccit.duomo;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap.OnMapLongClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfigeration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.miles.ccit.util.BaseMapObject;
import com.miles.ccit.util.MapBaseActivity;
import com.miles.ccit.util.MyLog;

public class CreatOptionActivity extends MapBaseActivity
{

	// 定位相关
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;
	boolean isFirstLoc = true;// 是否首次定位
	private LatLng latlng = null;
	
	public static final int SKYCODE = 1;
	public static final int EARTHCODE = 2;
	public static final int WATERCODE = 3;
	public static final int OTHERCODE = 4;
	public static final int ACTION1 = 51;
	public static final int ACTION2 = 52;
	private int typeCode = 0;
	
	
	private EditText edit_skyjiaci;
	private EditText edit_skybum;
	private EditText edit_skyhight;
	private EditText edit_skylane;
	private EditText edit_skyspeed;
	private EditText edit_skystep;
	
	private EditText edit_earthlane;
	private EditText edit_earthspeed;
	private EditText edit_earthstep;
	
	private EditText edit_waterlane;
	private EditText edit_waterdeep;
	private EditText edit_waterspeed;
	private EditText edit_waterstep;
	
	private LinearLayout linear_Sky;
	private LinearLayout linear_Earth;
	private LinearLayout linear_Water;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creat_option);
		typeCode = getIntent().getIntExtra("typecode", 0);
	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.bt_left:
			this.finish();
			break;
		case R.id.bt_right:
//			if(edit_skystep.getText().toString().equals(""))
//			{
//				MyLog.showToast(mContext, "间隔时间为必填项！");
//				return;
//			}
            setResult(RESULT_OK, new Intent().putExtra("option",composeData())); 
			this.finish();
			break;
		}
	}

//	private void checkStepTime()
//	{
//		switch(typeCode)
//		{
//		case SKYCODE:
//			
//		}
//		
//	}
	
	private HashMap<String, Object> composeData()
	{
		HashMap<String, Object> d = new HashMap<String, Object>();
		switch(typeCode)
		{
		case SKYCODE:
			d.put("lat", latlng.latitude+"");
			d.put("lng", latlng.longitude+"");
			d.put("height", edit_skyhight.getText().toString());
			d.put("lane", edit_skylane.getText().toString());
			d.put("speed", edit_skyspeed.getText().toString());
			d.put("jiaci", edit_skyjiaci.getText().toString());
			d.put("bum", edit_skybum.getText().toString());
			d.put("step", edit_skystep.getText().toString());
			break;
		case EARTHCODE:
			d.put("lat", latlng.latitude+"");
			d.put("lng", latlng.longitude+"");
			d.put("lane", edit_earthlane.getText().toString());
			d.put("speed", edit_earthspeed.getText().toString());
			d.put("step", edit_earthstep.getText().toString());
			break;
		case WATERCODE:
			d.put("lat", latlng.latitude+"");
			d.put("lng", latlng.longitude+"");
			d.put("deep", edit_waterdeep.getText().toString());
			d.put("lane", edit_waterlane.getText().toString());
			d.put("speed", edit_waterspeed.getText().toString());
			d.put("step", edit_waterstep.getText().toString());
			break;
		case OTHERCODE:
			d.put("lat", latlng.latitude+"");
			d.put("lng", latlng.longitude+"");
			break;
		case ACTION1:
		case ACTION2:
			d.put("lat", latlng.latitude+"");
			d.put("lng", latlng.longitude+"");
			break;
		}
		
		return d;
	}
	
	
	@Override
	public void initView()
	{
		// TODO Auto-generated method stub
		initBaseView("新建选项");
		Btn_Right.setBackgroundResource(R.drawable.btsure);
		linmap = (LinearLayout) findViewById(R.id.linear_map);
		linmap.removeAllViews();
		linmap.addView(mMapView, layoutParams2);
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();

		mBaiduMap.setOnMapLongClickListener(new OnMapLongClickListener()
		{

			@Override
			public void onMapLongClick(LatLng arg0)
			{
				// TODO Auto-generated method stub
//				 Toast.makeText(mContext, arg0.latitude+""+arg0.longitude,0).show();
				// 定义Maker坐标点
				latlng = arg0;
				dialog() ;
				
			}
		});

		linear_Sky = (LinearLayout)findViewById(R.id.linear_sky);
		linear_Earth = (LinearLayout)findViewById(R.id.linear_earth);
		linear_Water = (LinearLayout)findViewById(R.id.linear_water);
		switch(typeCode)
		{
		case SKYCODE:
			linear_Sky.setVisibility(View.VISIBLE);
			edit_skyhight = (EditText)findViewById(R.id.edit_jbheight);
			edit_skylane = (EditText)findViewById(R.id.edit_flylane);
			edit_skyspeed = (EditText)findViewById(R.id.edit_speed);
			edit_skyjiaci = (EditText)findViewById(R.id.edit_jiaci);
			edit_skybum = (EditText)findViewById(R.id.edit_bomnum);
			edit_skystep = (EditText)findViewById(R.id.edit_steptime);
			break;
		case EARTHCODE:
			linear_Earth.setVisibility(View.VISIBLE);
			edit_earthspeed = (EditText)findViewById(R.id.edit_earthspeed);
			edit_earthlane = (EditText)findViewById(R.id.edit_earth_lane);
			edit_earthstep = (EditText)findViewById(R.id.edit_earthstep);
			break;
		case WATERCODE:
			linear_Water.setVisibility(View.VISIBLE);
			edit_waterdeep = (EditText)findViewById(R.id.edit_waterdeep);
			edit_waterlane = (EditText)findViewById(R.id.edit_waterlane);
			edit_waterspeed = (EditText)findViewById(R.id.edit_waterspeed);
			edit_waterstep = (EditText)findViewById(R.id.edit_waterstep);
			break;
		}
		
		
	}
	
	private void dialog() 
	{
			
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setMessage("确认此处为军标位置吗？");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// TODO Auto-generated method stub
				mBaiduMap.clear();
				// 构建Marker图标
				BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding_red);
				// 构建MarkerOption，用于在地图上添加Marker
				OverlayOptions option = new MarkerOptions().position(latlng).icon(bitmap);
				// 在地图上添加Marker，并显示
				mBaiduMap.addOverlay(option);
			}
		});
		builder.setNegativeButton("取消",null);
		builder.create().show();
	}


	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener
	{

		@Override
		public void onReceiveLocation(BDLocation location)
		{
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
			// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc)
			{
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(u);
			}
		}

		@Override
		public void onReceivePoi(BDLocation arg0)
		{
			// TODO Auto-generated method stub
			
		}

	}
}
