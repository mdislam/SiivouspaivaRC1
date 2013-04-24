package com.mesba.siivouspaivarc1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.provider.Settings;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener {
	private ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
	private ListView lv;
	double latitude;
	double longitude;
	private LocationManager locationManager;
	private String provider;
//	private ShopInfo shop;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		boolean enabledGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean enabledWiFi = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		// Check if enabled and if not send user to the GSP settings
		// Better solution would be to display a dialog and suggesting to
		// go to the settings
		if (!enabledGPS) {
			Toast.makeText(this, "GPS signal not found", Toast.LENGTH_LONG)
					.show();
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(intent);
		}

		provider = LocationManager.GPS_PROVIDER;
		locationManager.requestLocationUpdates(provider, 0, 0, this);
		
		if(latitude == 0.0)
			latitude = 60;
		if(longitude == 0.0)
			longitude = 24;

		AsyncTaskExample asyncTask = new AsyncTaskExample();
		String latm = "" + (latitude - 1);
		String lngm = "" + (longitude - 1);

		String latM = "" + (latitude + 1);
		String lngM = "" + (longitude + 1);
		asyncTask.execute("http://siivouspaiva.com/data.php?query=load", latm, latM, lngm, lngM);

		// We get the ListView component from the layout
		lv = (ListView) findViewById(R.id.listView);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				ShopInfo shop = new ShopInfo();
				
				shop.setShopId(Integer.parseInt(mylist.get(position).get("id")));
				shop.setName(mylist.get(position).get("name"));
				shop.setAddress(mylist.get(position).get("address"));
				shop.setDescription(mylist.get(position).get("description"));
				
				shop.setLatitude(Double.parseDouble(mylist.get(position).get("u")));
				shop.setLongitude(Double.parseDouble(mylist.get(position).get("v")));
				
				shop.setStartHour(mylist.get(position).get("start_hour"));
				shop.setStartMinute(mylist.get(position).get("start_minute"));
				shop.setEndHour(mylist.get(position).get("end_hour"));
				shop.setEndMinute(mylist.get(position).get("end_minute"));
				
				shop.setLink(mylist.get(position).get("link"));
				shop.setTags(mylist.get(position).get("tags"));
				shop.setModified(mylist.get(position).get("modified"));

				// Launching new Activity on selecting single List Item
				Intent detailsIntent = new Intent(getApplicationContext(), DetailsActivity.class);
				// Here I am passing all values to other activity
				detailsIntent.putExtra("shop", shop);
				startActivity(detailsIntent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onLocationChanged(Location location) {
		// Getting latitude of the current location
		latitude = location.getLatitude();

		// Getting longitude of the current location
		longitude = location.getLongitude();
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
	}

	// //////////////////////////////////////////////////////////////////////
	private class AsyncTaskExample extends AsyncTask<String, Void, String> {

		private ProgressDialog Dialog;
		String response = "";

		@Override
		protected void onPreExecute() {
			Dialog = new ProgressDialog(MainActivity.this);
			Dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			Dialog.setMessage("Loading from SiivousPaiva server...");
			Dialog.setCancelable(false);
			Dialog.show();
		}

		@Override
		protected String doInBackground(String... urls) {
			// TODO Auto-generated method stub

			try {
				HttpClient hc = new DefaultHttpClient();
				HttpPost post = new HttpPost(urls[0]);
				
				String um = urls[1];
				String uM = urls[2];
				String vm = urls[3];
				String vM = urls[4];

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

				// HttpPost parameters
				nameValuePairs.add(new BasicNameValuePair("um", um));
				nameValuePairs.add(new BasicNameValuePair("uM", uM));
				nameValuePairs.add(new BasicNameValuePair("vm", vm));
				nameValuePairs.add(new BasicNameValuePair("vM", vM));
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse rp = hc.execute(post);

				if (rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					response = EntityUtils.toString(rp.getEntity());
				} else {
					Toast.makeText(getApplicationContext(), "Failed!!",
							Toast.LENGTH_LONG).show();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			return response;
		}

		@Override
		protected void onPostExecute(String result) {

			Dialog.dismiss();
			Dialog = null;

			String jsonStr = result;
			

			try {
				JSONArray jsonArray = new JSONArray(jsonStr);
				for (int i = 0; i < jsonArray.length(); i++) {
					HashMap<String, String> map = new HashMap<String, String>();
					ShopInfo shop = new ShopInfo();
					JSONObject objJson = jsonArray.getJSONObject(i);
					
					shop.setShopId(objJson.getInt("id"));
					shop.setName(objJson.getString("name"));
					shop.setAddress(objJson.getString("address"));
					shop.setDescription(objJson.getString("description"));
					shop.setLatitude(objJson.getDouble("u"));
					shop.setLongitude(objJson.getDouble("v"));
					shop.setStartHour(objJson.getString("start_hour"));
					shop.setStartMinute(objJson.getString("start_minute"));
					shop.setEndHour(objJson.getString("end_hour"));
					shop.setEndMinute(objJson.getString("end_minute"));
					shop.setLink(objJson.getString("link"));
					shop.setTags(objJson.getString("tags"));
					shop.setModified(objJson.getString("modified"));
					
//					map.put("shop", shop);					

					// add all properties to the hash map
					map.put("id", String.valueOf(shop.getShopId()));
					map.put("name", shop.getName());
					map.put("address", shop.getAddress());
					map.put("description", shop.getDescription());

					map.put("u", String.valueOf(shop.getLatitude()));
					map.put("v", String.valueOf(shop.getLongitude()));

					map.put("start_hour", String.valueOf(shop.getStartHour()));
					map.put("start_minute", String.valueOf(shop.getStartMinute()));
					map.put("end_hour", String.valueOf(shop.getEndHour()));
					map.put("end_minute", String.valueOf(shop.getEndMinute()));

					map.put("tags", shop.getTags());
					map.put("link", shop.getLink());
					map.put("modified", shop.getModified());

					mylist.add(map);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			SimpleAdapter simpleAdpt = new SimpleAdapter(MainActivity.this,
					mylist, android.R.layout.simple_list_item_2, new String[] {
							"name", "address" }, new int[] {
							android.R.id.text1, android.R.id.text2 });

			lv.setAdapter(simpleAdpt);
		}
	}

}
