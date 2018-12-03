package com.salehd1979.android.hideimages;
import android.support.v7.app.*;
import android.view.*;
import android.content.*;
import android.util.*;
import android.os.*;

public class AboutActivity extends AppCompatActivity
{
	public static Intent newIntent(Context packageContext){
		Intent intent = new Intent(packageContext, AboutActivity.class);
		return intent;
	}
	
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		
		super.onCreate ( savedInstanceState );
		setContentView(R.layout.about_activity);
	}
	
	
}
