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

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

//public class WebServiceAsyncTask extends Activity {
public class WebServiceAsyncTask extends AsyncTask<String, Void, String> {
//public class WebServiceAsyncTask {

//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		WebServiceJsonStream task = new WebServiceJsonStream();
//		task.execute(new String[] { "http://10.0.2.2/pathtracker/pathtracker.php" });
//	}

	// private String wsResult;
	Context context;
	String url = "http://nomad.host22.com/pathtracker.php";

//	String url = "http://10.0.2.2/pathtracker/pathtracker.php";

	WebServiceAsyncTask(Context context) {
		this.context = context;
	}

//public class WebServiceJsonStream extends AsyncTask<String, Void, String> {
	@Override
	protected String doInBackground(String... urls) {
		return queryWebService(urls);
	}

	@Override
	protected void onPostExecute(String result) {
		if (result != null && !result.equalsIgnoreCase("success"))
			Toast.makeText(context, "Error: " + result, Toast.LENGTH_LONG).show();

		// Toast.makeText(context, "Json result: " + result, Toast.LENGTH_LONG).show();
		Log.i("log_tag", "Json result: " + result);
		// textView.setText(result);
		// wsResult = result;
	}

//	protected String getWebServiceResult() {
//		return wsResult;
//	}

	private String queryWebService(String[] params) {
		String result = "";
		InputStream is = null;

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		// for (String param : params) {
		nameValuePairs.add(new BasicNameValuePair("user_name", params[0]));
		nameValuePairs.add(new BasicNameValuePair("provider", params[1]));
		nameValuePairs.add(new BasicNameValuePair("latitude", params[2]));
		nameValuePairs.add(new BasicNameValuePair("longitude", params[3]));
		nameValuePairs.add(new BasicNameValuePair("accuracy", params[4]));
		nameValuePairs.add(new BasicNameValuePair("time", params[5]));
		// }

		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
			Log.e("log_tag", "connection success ");
			// Toast.makeText(getApplicationContext(), "pass", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Log.e("log_tag", "Error in http connection " + e.toString());
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
			Log.e("log_tag", "Error converting result " + e.toString());
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
					Log.i("log_tag", "Result: " + result);
					return result;
				} else
					Log.i("log_tag",
							"id: " + json_data.getInt("id") + ", user_name: " + json_data.getString("user_name")
									+ ", provider: " + json_data.getString("provider") + ", latitude: "
									+ json_data.getDouble("latitude") + ", longitude: "
									+ json_data.getDouble("longitude") + ", accuracy: " + json_data.getInt("accuracy")
									+ ", time: " + json_data.getInt("time"));

				// Toast.makeText(getApplicationContext(), "pass", Toast.LENGTH_SHORT).show();
			}

			return result;
		} catch (JSONException e) {
			Log.e("log_tag", "Error parsing data " + e.toString());
			// Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show();
			return null;
		}
	}
//}

//	public void queryWebService() {
//		WebServiceJsonStream task = new WebServiceJsonStream();
//		task.execute(new String[] { "http://10.0.2.2/pathtracker/pathtracker.php" });
//
//	}
}
