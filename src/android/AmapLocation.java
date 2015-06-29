package me.duduche.cordova.plugins.amaplocation;


import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AmapLocation extends CordovaPlugin implements AMapLocationListener {

	private static final String STOP_ACTION = "stop";
	private static final String GET_ACTION = "getCurrentPosition";
	public boolean result = false;
	CallbackContext callbackContext;
	LocationManagerProxy mLocationManagerProxy;
	JSONObject jsonObj = new JSONObject();

	@Override
	public boolean execute(String action, JSONArray args,
			final CallbackContext callbackContext) {
		setCallbackContext(callbackContext);
		if (GET_ACTION.equals(action)) {
			cordova.getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mLocationManagerProxy = LocationManagerProxy.getInstance(cordova.getActivity());
					mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, 10*1000, 10, AmapLocation.this);

					mLocationManagerProxy.setGpsEnable(false);
				}

			});
			return true;
		} else if (STOP_ACTION.equals(action)) {
			stopLocation();
			callbackContext.success(200);
			return true;
		} else {
			callbackContext.error(PluginResult.Status.INVALID_ACTION.toString());
		}

		while (!result) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	private void stopLocation() {
		if (mLocationManagerProxy != null) {
			mLocationManagerProxy.removeUpdates(this);
			mLocationManagerProxy.destory();
		}
		mLocationManagerProxy = null;
	}

	@Override
	public void onDestroy() {
		stopLocation();
		super.onDestroy();
	}

	public CallbackContext getCallbackContext() {
		return callbackContext;
	}

	public void setCallbackContext(CallbackContext callbackContext) {
		this.callbackContext = callbackContext;
	}

	@Override
	public void onLocationChanged(AMapLocation aMapLocation) {
		if(aMapLocation != null && aMapLocation.getAMapException().getErrorCode() == 0) {
			//获取位置信息
			Double geoLat = aMapLocation.getLatitude();
			Double geoLng = aMapLocation.getLongitude();

			try {
				JSONObject position = new JSONObject();
				position.put("lat", aMapLocation.getLatitude());
				position.put("lng", aMapLocation.getLongitude());

				jsonObj.put("position", position);
				jsonObj.put("accuracy", aMapLocation.getAccuracy());

				Log.d("AmapLocationPlugin", "run: " + jsonObj.toString());

				callbackContext.success(jsonObj);
				result = true;
			} catch (JSONException e) {
				callbackContext.error(e.getMessage());
				result = true;
			}

		}
	}

	@Override
	public void onLocationChanged(Location location) {

	}

	@Override
	public void onStatusChanged(String s, int i, Bundle bundle) {

	}

	@Override
	public void onProviderEnabled(String s) {

	}

	@Override
	public void onProviderDisabled(String s) {

	}
}