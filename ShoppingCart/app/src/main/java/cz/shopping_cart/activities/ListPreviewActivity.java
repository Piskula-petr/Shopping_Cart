package cz.shopping_cart.activities;

import android.content.pm.ActivityInfo;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import cz.shopping_cart.R;
import cz.shopping_cart.pojo.Product;

public class ListPreviewActivity extends AppCompatActivity {
		
		private ListView listViewPreviewProducts;
		
		// Název seznamu
		private String listName;
		
		// Produkty v seznamu
		private List<Product> products = new ArrayList<>();
		
		// Množství + název produktu v jednom řádku
		private List<String> productsOneLine = new ArrayList<>();
		
		
		/**
		 * Vytvoření aktivity
		 *
		 * @param savedInstanceState
		 */
		@Override
		protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				
				setContentView(R.layout.activity_list_preview);
				
				// Nastavení orientace na výšku
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
				
				// Nastavení režimu celé obrazovky
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
				
				getSupportActionBar().setDisplayHomeAsUpEnabled(true);
				
// Název seznamu ////////////////////////////////////////////////////////////////////////////////////////////////////////////
				
				listName = getIntent().getStringExtra("listName");
				
				getSupportActionBar().setTitle(listName);
				
// Produkty ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				
				products = (List<Product>) getIntent().getSerializableExtra("products");
				
				// Spojení množství a názvu produktu
				for (Product product : products) {
						
						productsOneLine.add(product.getAmount() + " x  " + product.getName());
				}
				
				ArrayAdapter productsAdapter = new ArrayAdapter(ListPreviewActivity.this, android.R.layout.simple_list_item_1, productsOneLine) {
						
						@NonNull
						@Override
						public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
								
								TextView textView = (TextView) super.getView(position, convertView, parent);
								textView.setPadding(75, 25, 75, 25);
								textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
								
								return textView;
						}
				};
				
				listViewPreviewProducts = findViewById(R.id.listViewPreviewProducts);
				listViewPreviewProducts.setAdapter(productsAdapter);
				listViewPreviewProducts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
						
						@Override
						public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
								
								TextView textView = (TextView) view;
								
								// Přeškrtnutí produktu při dlouhém podržení
								if (textView.getPaintFlags() != Paint.STRIKE_THRU_TEXT_FLAG) {
										
										textView.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
										
								} else textView.setPaintFlags(0);
								
								return true;
						}
				});
		}
		
		
		/**
		 * Přesměrování zpět na vytvoření seznamu
		 *
		 * @param item - položka z panelu
		 */
		@Override
		public boolean onOptionsItemSelected(@NonNull MenuItem item) {
				
				
				switch (item.getItemId()) {
						
						case android.R.id.home:

								finish();
								return true;
				}
				
				return super.onOptionsItemSelected(item);
		}

}