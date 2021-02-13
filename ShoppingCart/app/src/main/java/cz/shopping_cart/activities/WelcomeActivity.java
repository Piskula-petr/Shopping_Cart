package cz.shopping_cart.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import cz.shopping_cart.DatabaseHelper;
import cz.shopping_cart.R;

public class WelcomeActivity extends AppCompatActivity {
		
		private DatabaseHelper databaseHelper;
		
		/**
		 * Vytvoření aktivity
		 *
		 * @param savedInstanceState
		 */
		@Override
		protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				
				setContentView(R.layout.activity_welcome);
				
				// Nastavení orientace na výšku
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
				
				// Nastavení režimu celé obrazovky
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
				
				// Skrytí panelu akcí
				getSupportActionBar().hide();
				
				databaseHelper = new DatabaseHelper(WelcomeActivity.this);
				
				new Handler().postDelayed(new Runnable() {
						
						@Override
						public void run() {
								
								int listsCount = databaseHelper.getListsCount();
								
								// Přesměrování na vytvoření nového seznamu
								if (listsCount == 0) {
										
										startActivity(new Intent(WelcomeActivity.this, ListFormActivity.class));
										
								// Přesměrování na náhled seznamů
								} else startActivity(new Intent(WelcomeActivity.this, ListsActivity.class));
						}
				}, 1500);
		}
		
}