package cz.shopping_cart.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import cz.shopping_cart.DatabaseHelper;
import cz.shopping_cart.R;
import cz.shopping_cart.pojo.ListName;
import cz.shopping_cart.recycler_views.products.ProductsRecyclerViewAdapter;

public class ProductsActivity extends AppCompatActivity {
		
		private TextView textViewProductsEmpty;
		private DrawerLayout drawerLayoutProducts;
		private ActionBarDrawerToggle actionBarDrawerToggle;
		private NavigationView navigationViewProducts;
		
		private DatabaseHelper databaseHelper;
		
		// Upravený RecyclerView
		private RecyclerView recyclerViewProducts;
		private ProductsRecyclerViewAdapter productsRecyclerViewAdapter;
		
		// List názvů produktů
		private List<String> productNames = new ArrayList<>();
		
		// Smazání produktu - [posun doprava]
		private ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
				
				@Override
				public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
						return false;
				}
				
				@Override
				public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
				
						int productIndex = viewHolder.getAbsoluteAdapterPosition();
						long productID = databaseHelper.getProductIdByName(productNames.get(productIndex));
						
						// Názvy seznamů produktu
						List<ListName> listNames = databaseHelper.getListNamesOfProduct(productID);
						
						// Produkt je součástí seznamu
						if (listNames.size() > 0) {
								
								String message = "Tento produkt je součástí " +
										(listNames.size() == 1
												? "seznamu"
												: "seznamů") +" \n\n";
								
								// Přidání názvů seznamů do zprávy
								for (ListName listName : listNames) {
										message = message + listName.getName() + "\n";
								}
								
								// Vytvoření dialogu
								AlertDialog.Builder builder = new AlertDialog.Builder(ProductsActivity.this);
								builder.setTitle("Upozornění");
								builder.setCancelable(false);
								
								builder.setMessage(message + "\nze " +
										(listNames.size() == 1
												? "kterého"
												: "kterých") + " bude odstraněn.");
								
								builder.setPositiveButton("Potvrdit", new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
										
												// Smazání produktu ze všech seznamů + smazání produktu z databáze
												boolean result = databaseHelper.deleteProductFromListsById(productID);
												
												if (result) Toast.makeText(ProductsActivity.this, "Produkt byl odstraněn ze " +
														(listNames.size() == 1
																? "seznamu"
																: "seznamů") + ", i z databáze", Toast.LENGTH_LONG).show();
												
												// Smazání prázdných seznamů
												databaseHelper.deleteEmptyLists();
												
												// Odstranění produktu
												productNames.remove(productIndex);
												
												productsRecyclerViewAdapter.notifyItemRemoved(productIndex);
										}
								});
								
								builder.setNegativeButton("Zrušit", new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
												
												// Zavření dialogu
												dialog.cancel();
												
												productsRecyclerViewAdapter.notifyItemChanged(productIndex);
										}
								});
								
								// Zobrazení dialogu
								AlertDialog alertDialog = builder.create();
								alertDialog.show();
								
						// Produkt není součástí žádného seznamu
						} else {
								
								// Smazání produktu z databáze
								boolean result = databaseHelper.deleteProductById(productID);
								
								if (result) Toast.makeText(ProductsActivity.this, "Produkt byl odstraněn z databáze", Toast.LENGTH_LONG).show();
								
								// Odstranění produktu
								productNames.remove(productIndex);
								
								productsRecyclerViewAdapter.notifyItemRemoved(productIndex);
						}
						
						// Zobrazení zprávy, při odstranění všech produktů
						if (productNames.size() == 0) {
								
								textViewProductsEmpty.setVisibility(View.VISIBLE);
						}
				}
		};
		
		
		/**
		 * Vytvoření aktivity
		 *
		 * @param savedInstanceState
		 */
		@Override
		protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				
				setContentView(R.layout.activity_products);
				
				// Nastavení orientace na výšku
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
				
				// Nastavení režimu celé obrazovky
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
				
				drawerLayoutProducts = findViewById(R.id.drawerLayoutProducts);
				
				// Tlačítko pro zobrazení / skrytí navigačního menu
				actionBarDrawerToggle = new ActionBarDrawerToggle(ProductsActivity.this, drawerLayoutProducts, R.string.open, R.string.close);
				actionBarDrawerToggle.syncState();
				
				drawerLayoutProducts.addDrawerListener(actionBarDrawerToggle);
				
				// Navigační menu
				navigationViewProducts = findViewById(R.id.navigationViewLists);
				navigationViewProducts.setCheckedItem(R.id.menu_products);
				navigationViewProducts.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
						
						@Override
						public boolean onNavigationItemSelected(@NonNull MenuItem item) {
								
								// Přesměrování na jinou aktivitu
								switch (item.getItemId()) {
										
										// Seznamy
										case R.id.menu_lists:
												
												startActivity(new Intent(ProductsActivity.this, ListsActivity.class));
												break;
												
										// Nový seznam
										case R.id.menu_new_list:
												
												startActivity(new Intent(ProductsActivity.this, ListFormActivity.class));
												break;
								}
								
								// Uzavření menu
								drawerLayoutProducts.closeDrawer(GravityCompat.START);
								
								return true;
						}
				});
				
				// Nastavení panelu akcí
				getSupportActionBar().setDisplayHomeAsUpEnabled(true);
				getSupportActionBar().setTitle("Produkty");
				
				textViewProductsEmpty = findViewById(R.id.textViewProductsEmpty);
				
// Zobrazení produktů //////////////////////////////////////////////////////////////////////////////////////////////////////
				
				databaseHelper = new DatabaseHelper(ProductsActivity.this);
				productNames = databaseHelper.getProductNames();
				
				// Skrytí zprávy
				if (productNames.size() > 0) {
						
						textViewProductsEmpty.setVisibility(View.INVISIBLE);
				}
				
				productsRecyclerViewAdapter = new ProductsRecyclerViewAdapter(ProductsActivity.this, productNames);
				
				recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
				recyclerViewProducts.setAdapter(productsRecyclerViewAdapter);
				recyclerViewProducts.setLayoutManager(new LinearLayoutManager(ProductsActivity.this));
				
				ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
				itemTouchHelper.attachToRecyclerView(recyclerViewProducts);
		}
		
		
		/**
		 * Vybrání možnosti menu
		 *
		 * @param item - položka menu
		 *
		 * @return - true / false
		 */
		@Override
		public boolean onOptionsItemSelected(@NonNull MenuItem item) {
				
				// Otevření menu, po stisknutí tlačítka
				if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
						
						return true;
				}
				
				return super.onOptionsItemSelected(item);
		}
		
}