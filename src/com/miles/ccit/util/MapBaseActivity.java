package com.miles.ccit.util;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public abstract class MapBaseActivity extends AbsBaseActivity
{
	public MapView mMapView = null;
	public BaiduMap mBaiduMap;

	public BitmapDescriptor mCurrentMarker;
	public LinearLayout linmap = null;
	public InfoWindow mInfoWindow;
	public boolean isFirstLoc = true;// 是否首次定位
	public LayoutParams layoutParams2;
	public Timer timer = null;
	public TimerTask tTask = null;
	public int countTime = 10;
	public Handler hander = null;
	public LatLng showlatlng = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		BaiduMapOptions bo = new BaiduMapOptions().zoomControlsEnabled(true);
		mMapView = new MapView(mContext, bo);
		layoutParams2 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		mBaiduMap = mMapView.getMap();

	}

	public void setCenterPoint(BDLocation lo)
	{
		mBaiduMap.setMyLocationEnabled(true);
		if (mMapView == null)
			return;
		MyLocationData locData = new MyLocationData.Builder().accuracy(lo.getRadius()).direction(100).latitude(lo.getLatitude()).longitude(lo.getLongitude()).build();
		mBaiduMap.setMyLocationData(locData);
		isFirstLoc = false;
		LatLng ll = new LatLng(lo.getLatitude(), lo.getLongitude());
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
		mBaiduMap.animateMapStatus(u);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;

	}

	@Override
	protected void onResume()
	{
		super.onResume();
		mMapView.onResume();
	}

	
	
	@Override
	protected void onPause()
	{
		super.onPause();
		mMapView.onPause();
	}

}
