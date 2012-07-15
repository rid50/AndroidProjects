package de.vogella.android.locationapi.maps;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class ShowMapActivity extends MapActivity {

	private static final String TAG = ShowMapActivity.class.getSimpleName();
	// private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 10; // in Meters
	// private static final long MINIMUM_TIME_BETWEEN_UPDATES = 2000; // in Milliseconds
	private MapController mapController;
	private MapView mapView;
	// private LocationListener locationListener;
	// private LocationListener locationListener2;
	// private LocationManager locationManager;
	// private String provider;
	private List<Overlay> mapOverlays;
	private Drawable marker, marker_red, circle;
	private MyItemizedOverlay itemizedOverlay, previousItemizedOverlay = null;
	private Context context;
	// private LocationDbAdapter dbAdapter;

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "Intent received: " + intent.toString());

			if (MyApplication.BRODCAST_LOCATION.equals(intent.getAction())) {
				Location location = (Location) intent.getParcelableExtra(MyApplication.EXTRA_MAP_INFO);
				if (location != null) {
					onLocationChanged((Location) location);
				}
			} else if (MyApplication.BRODCAST_ERROR.equals(intent.getAction())) {
				String extraInfo = intent.getExtras().getString(MyApplication.EXTRA_MAP_INFO);
				if (extraInfo != null) {
					if (!((String) extraInfo).equalsIgnoreCase("success"))
						Toast.makeText(context, "Web Service Request Error: " + (String) extraInfo, Toast.LENGTH_LONG)
								.show();
					else
						Toast.makeText(context, "Web Service Request: " + (String) extraInfo, Toast.LENGTH_LONG).show();
				}
			}
		}
	};

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		setContentView(R.layout.main); // bind the layout to the activity

		context = getApplicationContext();

		Toast.makeText(context, "onCreate", Toast.LENGTH_SHORT).show();

		marker = this.getResources().getDrawable(R.drawable.android);
		marker_red = this.getResources().getDrawable(R.drawable.happy_robot);
		circle = this.getResources().getDrawable(R.drawable.circle);

		// create a map view
		// RelativeLayout linearLayout = (RelativeLayout) findViewById(R.id.mainlayout);
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		// mapView.setStreetView(true);

		mapController = mapView.getController();
		mapController.setZoom(16);

		// locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		ComponentName cName = new ComponentName(getPackageName(), LocationListenerService.class.getName());
		startService(new Intent().setComponent(cName));

		// locationListener = new GeoUpdateHandler();

		// startActivity(new Intent(this, WebServiceAsyncTask.class));
		// task.execute(new String[] { "http://10.0.2.2/pathtracker/pathtracker.php" });
		// Toast.makeText(getApplicationContext(), "Json result: " + task.getWebServiceResult(),
		// Toast.LENGTH_LONG).show();

		// locationListener2 = new GeoUpdateHandler();

//		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//		if (locationManager.isProviderEnabled(locationManager.GPS_PROVIDER))
//			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATES,
//					MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, new GeoUpdateHandler());
//		if (locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER))
//			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATES,
//					MINIMUM_DISTANCE_CHANGE_FOR_UPDATES, new GeoUpdateHandler());

	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(MyApplication.BRODCAST_ERROR);
		this.registerReceiver(this.broadcastReceiver, intentFilter);

		intentFilter = new IntentFilter();
		intentFilter.addAction(MyApplication.BRODCAST_LOCATION);
		this.registerReceiver(this.broadcastReceiver, intentFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(broadcastReceiver);
	}

	public void onLocationChanged(Location location) {
		// Log.d(TAG, "onLocationChanged");

		if (location != null) {
			Toast.makeText(context, "onLocationChanged - Provider: " + location.getProvider(), Toast.LENGTH_SHORT)
					.show();

			mapOverlays = mapView.getOverlays();

//			if (mapOverlays.size() > 0) {
//				float[] results = new float[1];
//				GeoPoint tempPoint = previousItemizedOverlay.getOverlay().getPoint();
//				Location.distanceBetween(location.getLatitude(), location.getLongitude(),
//						tempPoint.getLatitudeE6() / 1E6, tempPoint.getLongitudeE6() / 1E6, results);
//
//				Toast.makeText(context, "Distance: " + results[0] + " / " + results.length, Toast.LENGTH_SHORT).show();
//				if (results[0] < MINIMUM_DISTANCE_CHANGE_FOR_UPDATES)
//					return;
//			}

//			dbAdapter.addLocation(location);
//			if (((MyApplication) getApplicationContext()).isNetworkAvailable())
//				context.startService(new Intent(context, WebIntentService.class));

//			WebServiceAsyncTask task = new WebServiceAsyncTask(getApplicationContext());
//			task.execute(new String[] { "roman", String.valueOf(location.getLatitude()),
//					String.valueOf(location.getLongitude()), String.valueOf(location.getAccuracy()),
//					String.valueOf(location.getTime()) });

			// provider = location.getProvider();

			if (location.getProvider().equals("network"))
				itemizedOverlay = new MyItemizedOverlay(marker_red);
			else if (location.getProvider().equals("gps"))
				itemizedOverlay = new MyItemizedOverlay(marker);

			// else
			// Toast.makeText(context, "Provider: " + provider, Toast.LENGTH_SHORT).show();

			OverlayItem tempOverlayItem;
			MyItemizedOverlay tempItemizedOverlay;
			if (mapOverlays.size() > 0) {
				tempOverlayItem = new OverlayItem(previousItemizedOverlay.getOverlay().getPoint(), "", "");

				previousItemizedOverlay.removeOverlay();
				mapOverlays.remove(previousItemizedOverlay);

				tempItemizedOverlay = new MyItemizedOverlay(circle);
				tempItemizedOverlay.addOverlay(tempOverlayItem);
				mapOverlays.add(tempItemizedOverlay);
			}

			int lat = (int) (location.getLatitude() * 1E6);
			int lng = (int) (location.getLongitude() * 1E6);
			GeoPoint point = new GeoPoint(lat, lng);
			OverlayItem overlayItem = new OverlayItem(point, "", "");

			itemizedOverlay.addOverlay(overlayItem);
			mapOverlays.add(itemizedOverlay);

			// if (true)
			// return;

			previousItemizedOverlay = itemizedOverlay;

			mapController.animateTo(point); // mapController.setCenter(point);
//				Toast.makeText(context,
//						location.getProvider() + " / " + location.getLatitude() + " / " + location.getLongitude(),
//						Toast.LENGTH_SHORT).show();
		}
		// else {
		// Toast.makeText(context, "location is null", Toast.LENGTH_SHORT).show();
		// Log.d(TAG, "location is null");
		// }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itemPrefs:
			startActivity(new Intent(this, PrefsActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			break;
		}
		return true;
	}

	private static final int TWO_MINUTES = 1000 * 60 * 2;

	/**
	 * Determines whether one Location reading is better than the current Location fix
	 * 
	 * @param location
	 *            The new Location that you want to evaluate
	 * @param currentBestLocation
	 *            The current Location fix, to which you want to compare the new one
	 */
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}
}
