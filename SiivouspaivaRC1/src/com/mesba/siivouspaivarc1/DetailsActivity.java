package com.mesba.siivouspaivarc1;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class DetailsActivity extends Activity {
	private ShopInfo shop;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.details_view);

		TextView nameTxt = (TextView) findViewById(R.id.name_label);
		TextView addressTxt = (TextView) findViewById(R.id.address_label);
		TextView detailsTxt = (TextView) findViewById(R.id.details_label);
		TextView operatingTxt = (TextView) findViewById(R.id.operating_label);
		TextView linkTxt = (TextView) findViewById(R.id.link_label);

		shop = (ShopInfo) getIntent().getSerializableExtra("shop");

		nameTxt.setText(shop.getName());
		addressTxt.setText(shop.getAddress());
		detailsTxt.setText(shop.getDescription());

		operatingTxt.setText("From " + shop.getStartHour() + ":"
				+ shop.getStartMinute() + " To " + shop.getEndHour() + ":"
				+ shop.getEndMinute());

		linkTxt.setText("Link: " + shop.getLink());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
