package pt.trycatch.jamify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class ChooseLearnActivity extends Activity {

	private String array_spinner[];
	
	public static final String THE_BEATLES = "The Beatles - Don't Let me Down";
	public static final String KARMA_POLICE = "Radiohead - Karma Police";
	public static final String ARCTIC_MONKEYS = "Arctic Monkeys - Mardy Bum";
	public static final String PINK_FLOYD = "Pink Floyd - Wish You Were Here";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_learn);
		
		array_spinner = new String[5];
		array_spinner[0] = "Select one";
		array_spinner[1] = THE_BEATLES;
		array_spinner[2] = KARMA_POLICE;
		array_spinner[3] = ARCTIC_MONKEYS;
		array_spinner[4] = PINK_FLOYD;
		
		Spinner s = (Spinner) findViewById(R.id.chooseSong);
		ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, array_spinner);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(adapter);
		
		s.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	if (position == 0) return;
				Intent intent = new Intent(ChooseLearnActivity.this, LearnActivity.class);
				intent.putExtra("song", array_spinner[position]);
				ChooseLearnActivity.this.startActivity(intent);
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		    }

		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choose_learn, menu);
		return true;
	}

}
