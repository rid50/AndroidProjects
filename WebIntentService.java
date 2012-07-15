package de.vogella.android.locationapi.maps;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

public class WebIntentService extends IntentService {
	private static final String TAG = WebIntentService.class.toString();

	private SharedPreferences prefs;
	private LocationDbAdapter dbAdapter;

	public WebIntentService() {
		super(TAG);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		// new GetRandomGeneratedUserId().execute();
	}

	// private String wsResult;
	// Context context;
	// String url = "http://nomad.host22.com/pathtracker.php";

//	String url = "http://10.0.2.2/pathtracker/pathtracker.php";

//public class WebServiceJsonStream extends AsyncTask<String, Void, String> {
//	@Override
//	protected String doInBackground(String... urls) {
//		return queryWebService(urls);
//	}
//
//	@Override
//	protected void onPostExecute(String result) {
//		if (result != null && !result.equalsIgnoreCase("success"))
//			Toast.makeText(getApplicationContext(), "Error: " + result, Toast.LENGTH_LONG).show();
//
//		// Toast.makeText(context, "Json result: " + result, Toast.LENGTH_LONG).show();
//		Log.i("log_tag", "Json result: " + result);
//		// textView.setText(result);
//		// wsResult = result;
//	}

	@Override
	protected void onHandleIntent(Intent intent) {
		dbAdapter = new LocationDbAdapter(this);
		dbAdapter.open(true); // read mode
		queryWebService();
	}

	private String queryWebService() {
		String result = "";
		InputStream is = null;

		String user_id = this.prefs.getString("user_id", "unknown");

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		Cursor c = dbAdapter.fetchAllLocations();
		Log.i(TAG, c.getCount() + " records retrieved from SQLite");
		String value;
		while (c.moveToNext()) {
			nameValuePairs.add(new BasicNameValuePair("user_name[]", user_id));
			value = c.getString(c.getColumnIndex(LocationDbAdapter.KEY_PROVIDER_NAME));
			nameValuePairs.add(new BasicNameValuePair("provider[]", value));
			value = String.valueOf(c.getDouble(c.getColumnIndex(LocationDbAdapter.KEY_LATITUDE)));
			nameValuePairs.add(new BasicNameValuePair("latitude[]", value));
			value = String.valueOf(c.getDouble(c.getColumnIndex(LocationDbAdapter.KEY_LONGITUDE)));
			nameValuePairs.add(new BasicNameValuePair("longitude[]", value));
			value = String.valueOf(c.getLong(c.getColumnIndex(LocationDbAdapter.KEY_ACCURACY)));
			nameValuePairs.add(new BasicNameValuePair("accuracy[]", value));
			value = String.valueOf(c.getFloat(c.getColumnIndex(LocationDbAdapter.KEY_BEARING)));
			nameValuePairs.add(new BasicNameValuePair("bearing[]", value));
			value = String.valueOf(c.getLong(c.getColumnIndex(LocationDbAdapter.KEY_TIME)));
			nameValuePairs.add(new BasicNameValuePair("time[]", value));
		}

//		for (String param : params) {
//			nameValuePairs.add(new BasicNameValuePair("name", params[0]));
//			nameValuePairs.add(new BasicNameValuePair("latitude", params[1]));
//			nameValuePairs.add(new BasicNameValuePair("longitude", params[2]));
//			nameValuePairs.add(new BasicNameValuePair("accuracy", params[3]));
//			nameValuePairs.add(new BasicNameValuePair("time", params[4]));
//		}

		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(((MyApplication) getApplication()).getWebServiceUrl());
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
			Log.i(TAG, "connection success ");
			// Toast.makeText(getApplicationContext(), "pass", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Log.e(TAG, "Error in http connection " + e.toString());
			// Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();
			return null;
		}
		// convert response to string
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
				// Toast.makeText(getApplicationContext(), "pass", Toast.LENGTH_SHORT).show();
			}

			is.close();

			result = sb.toString();
		} catch (Exception e) {
			Log.e(TAG, "Error converting result " + e.toString());
			// Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();
			return null;
		}

		// parse json data
		try {
			JSONArray jArray = new JSONArray(result);
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject json_data = jArray.getJSONObject(i);
				if (json_data.has("result") && jArray.length() == 1) {
					result = json_data.getString("result");
					Log.i(TAG, "Result: " + result);
					Intent intent = new Intent(MyApplication.BRODCAST_ERROR);
					intent.putExtra(MyApplication.EXTRA_MAP_INFO, result);
					sendBroadcast(intent);

					if (result.equalsIgnoreCase("success"))
						dbAdapter.clearDatabase();

				} else
					Log.i(TAG, "id: " + json_data.getInt("id") + ", user_name: " + json_data.getString("user_name")
							+ ", provider: " + json_data.getString("provider") + ", latitude: "
							+ json_data.getDouble("latitude") + ", longitude: " + json_data.getDouble("longitude")
							+ ", accuracy: " + json_data.getInt("accuracy") + ", bearing: "
							+ json_data.getDouble("bearing") + ", time: " + json_data.getInt("time"));

				// Toast.makeText(getApplicationContext(), "pass", Toast.LENGTH_SHORT).show();
			}

			return result;
		} catch (JSONException e) {
			Log.e(TAG, "Error parsing data: " + e.toString());
			Log.e(TAG, "Error: " + result);
			// Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();
			return null;
		}
	}

//}

	@Override
	public void onDestroy() {
		super.onDestroy();
		dbAdapter.close();
		Log.i(TAG, "onDestroy()");
	}

//	public void queryWebService() {
//		WebServiceJsonStream task = new WebServiceJsonStream();
//		task.execute(new String[] { "http://10.0.2.2/pathtracker/pathtracker.php" });
//
//	}
}
