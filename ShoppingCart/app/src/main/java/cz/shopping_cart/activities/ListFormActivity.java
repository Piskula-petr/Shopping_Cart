package cz.shopping_cart.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cz.shopping_cart.DatabaseHelper;
import cz.shopping_cart.R;
import cz.shopping_cart.pojo.Product;
import cz.shopping_cart.recycler_views.list_form.ListFormRecyclerViewAdapter;

public class ListFormActivity extends AppCompatActivity {
		
		private EditText editTextListFormName;
		private AutoCompleteTextView autoCompleteListFormProductName;
		private TextView textViewListFormAmount;
		private ImageView imageViewListFormDecrement, imageViewListFormIncrement;
		private DrawerLayout drawerLayoutListForm;
		private ActionBarDrawerToggle actionBarDrawerToggle;
		private NavigationView navigationViewListForm;
		
		private DatabaseHelper databaseHelper;
		
		// Upravení seznamu
		private boolean listModification = false;
		
		// Upravený RecyclerView
		private RecyclerView recyclerViewListForm;
		private ListFormRecyclerViewAdapter listFormRecyclerViewAdapter;
		
		// Název seznamu
		private String listName;
		
		// List produktů
		private List<Product> products = new ArrayList<>();
		
		// Přidané produkty, během úpravy seznamu
		private List<Product> productsAdded = new ArrayList<>();
		
		// Odstraněné produkty, během úpravy seznamu
		private List<Product> productsRemoved = new ArrayList<>();
		
		// Přesun produktu - [dlouhé podržení]
		// Smazání produktu - [posun doprava]
		private ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(
				ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT) {

				@Override
				public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
						
						// Smazání aktuálního produktu + vytvoření produktu na novém indexu
						Product product = products.get(viewHolder.getAbsoluteAdapterPosition());
						
						products.remove(product);
						products.add(target.getAbsoluteAdapterPosition(), product);
						
						listFormRecyclerViewAdapter.notifyItemMoved(viewHolder.getLayoutPosition(), target.getLayoutPosition());
						
						return true;
				}
				
				@Override
				public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
						
						// Přidání seznamu do Listu k odstranění
						if (listModification) {
								
								productsRemoved.add(products.get(viewHolder.getAbsoluteAdapterPosition()));
						}
						
						// Odstranění produktu
						products.remove(viewHolder.getAbsoluteAdapterPosition());

						listFormRecyclerViewAdapter.notifyItemRemoved(viewHolder.getLayoutPosition());
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
				
				setContentView(R.layout.activity_list_form);
				
				// Nastavení orientace na výšku
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
				
				// Nastavení režimu celé obrazovky
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
				
				// Data z předchozí aktivity
				listModification = getIntent().getBooleanExtra("listModification", false);
				listName = getIntent().getStringExtra("listName");
				
				drawerLayoutListForm = findViewById(R.id.drawerLayoutListForm);
				
				// Úprava seznamu
				if (!listModification) {
						
						// Tlačítko pro zobrazení / skrytí navigačního menu
						actionBarDrawerToggle = new ActionBarDrawerToggle(ListFormActivity.this, drawerLayoutListForm, R.string.open, R.string.close);
						actionBarDrawerToggle.syncState();
						
						drawerLayoutListForm.addDrawerListener(actionBarDrawerToggle);
						
						// Navigační menu
						navigationViewListForm = findViewById(R.id.navigationViewListForm);
						navigationViewListForm.setCheckedItem(R.id.menu_new_list);
						navigationViewListForm.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
								
								@Override
								public boolean onNavigationItemSelected(@NonNull MenuItem item) {
										
										// Přesměrování na jinou aktivitu
										switch (item.getItemId()) {
												
												// Produkty
												case R.id.menu_products:
														
														startActivity(new Intent(ListFormActivity.this, ProductsActivity.class));
														break;
												
												// Seznamy
												case R.id.menu_lists:
														
														startActivity(new Intent(ListFormActivity.this, ListsActivity.class));
														break;
										}
										
										// Uzavření menu
										drawerLayoutListForm.closeDrawer(GravityCompat.START);
										
										return true;
								}
						});
				}
				
				// Nastavení panelu akcí
				getSupportActionBar().setDisplayHomeAsUpEnabled(true);
				getSupportActionBar().setTitle((listModification) ? getString(R.string.edit_list) : getString(R.string.new_list));
				getSupportActionBar().setDisplayShowCustomEnabled(true);
				
				// Obrázek pro potvrzení seznamu
				ImageView imageViewConfirm = new ImageView(ListFormActivity.this);
				imageViewConfirm.setImageResource(R.drawable.ok);
				imageViewConfirm.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View view) {
								
								// Přesměrování na náhled seznamu
								confirmList(view);
						}
				});
				
				ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(100, 100, Gravity.RIGHT);
				getSupportActionBar().setCustomView(imageViewConfirm, layoutParams);

				databaseHelper = new DatabaseHelper(ListFormActivity.this);
				
// Název seznamu ///////////////////////////////////////////////////////////////////////////////////////////////////////////
				
				editTextListFormName = findViewById(R.id.editTextListFormName);
				
				// Přednastavení názvu seznamu
				if (listModification) {
						
						editTextListFormName.setText(listName);
				}
				
// Název produktu //////////////////////////////////////////////////////////////////////////////////////////////////////////

				ArrayAdapter<String> productNamesAdapter = new ArrayAdapter<>(ListFormActivity.this,
						android.R.layout.simple_list_item_1, databaseHelper.getProductNames());
				
				autoCompleteListFormProductName = findViewById(R.id.autoCompleteListFormProductName);
				autoCompleteListFormProductName.setAdapter(productNamesAdapter);
				autoCompleteListFormProductName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
						
						@Override
						public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
								
								// Přidání produktu za použití virtuální klávesnice
								if (actionId == EditorInfo.IME_ACTION_DONE) {
										
										// Přidání produktu do Listu
										addProduct(view);
										
										return true;
								}
								
								return false;
						}
				});
				
// Snížení množství /////////////////////////////////////////////////////////////////////////////////////////////////////////
				
				imageViewListFormDecrement = findViewById(R.id.imageViewListFormDecrement);
				imageViewListFormDecrement.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
								
								// Dekrementace množství o 1
								decrementAmount(1);
						}
				});
				
				imageViewListFormDecrement.setOnLongClickListener(new View.OnLongClickListener() {
						
						@Override
						public boolean onLongClick(View view) {
								
								// Dekrementace množství o 10
								decrementAmount(10);
								
								return true;
						}
				});
				
// Množství /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				
				textViewListFormAmount = findViewById(R.id.textViewListFormAmount);
				textViewListFormAmount.setOnLongClickListener(new View.OnLongClickListener() {
						
						@Override
						public boolean onLongClick(View v) {
								
								// Reset hodnoty na 1
								textViewListFormAmount.setText(String.valueOf(1));
								
								return true;
						}
				});
				
// Zvýšení množství //////////////////////////////////////////////////////////////////////////////////////////////////////////
				
				imageViewListFormIncrement = findViewById(R.id.imageViewListFormIncrement);
				imageViewListFormIncrement.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
								
								// Inkrementace množství o 1
								incrementAmount(1);
						}
				});
				
				imageViewListFormIncrement.setOnLongClickListener(new View.OnLongClickListener() {
						
						@Override
						public boolean onLongClick(View view) {
								
								// Inkrementace množství o 10
								incrementAmount(10);
								
								return true;
						}
				});
				
// Přidané produkty ////////////////////////////////////////////////////////////////////////////////////////////////////////
				
				if (listModification) {
						
						products = (List<Product>) getIntent().getSerializableExtra("products");
				}
				
				listFormRecyclerViewAdapter = new ListFormRecyclerViewAdapter(ListFormActivity.this, products);
				
				recyclerViewListForm = findViewById(R.id.recyclerViewListForm);
				recyclerViewListForm.setAdapter(listFormRecyclerViewAdapter);
				recyclerViewListForm.setLayoutManager(new LinearLayoutManager(ListFormActivity.this));
				
				ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
				itemTouchHelper.attachToRecyclerView(recyclerViewListForm);
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
				
				// Tlačítko zpět
				if (listModification && item.getItemId() == android.R.id.home) {
						
						finish();
						return true;
						
				// Navigační menu
				} else if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
						
						return true;
				}
				
				return super.onOptionsItemSelected(item);
		}
		
		
		/**
		 * Přesměrování na náhled seznamu
		 *
		 * @param view
		 */
		public void confirmList(View view) {
				
				String editTextListName = StringUtils.capitalize(editTextListFormName.getText().toString());
				
				// Nastavení defaultního jména seznamu
				if (editTextListName.isEmpty() || (editTextListName != listName && editTextListName.contains("Můj seznam "))) {
						
						editTextListName = databaseHelper.getDefaultListName();
				}
				
				// Úprava seznamu
				if (listModification) {
						
						long listID = getIntent().getLongExtra("listID", 0);
						
						boolean result = databaseHelper.updateList(listID, editTextListName, productsRemoved, productsAdded);
						
						if (result) Toast.makeText(ListFormActivity.this, getString(R.string.list_updated), Toast.LENGTH_LONG).show();
						
						// Odstranění prázdných seznamů, pokud při změně byly odstraněny všechny produkty
						databaseHelper.deleteEmptyLists();
						
						finish();
						
				// Nový seznam
				} else {
						
						if (products.size() > 0) {
								
								boolean result = databaseHelper.addList(editTextListName, products);
								
								if (result) Toast.makeText(ListFormActivity.this, getString(R.string.list_saved), Toast.LENGTH_LONG).show();
								
								Intent listPreview = new Intent(ListFormActivity.this, ListPreviewActivity.class);
								listPreview.putExtra("listName", editTextListName);
								listPreview.putExtra("products", (Serializable) products);
								
								startActivity(listPreview);
						}
				}
		}
		
		
		/**
		 * Inkrementace množství
		 *
		 * @param increaseBy - zvýšení hodnoty o
		 */
		public void incrementAmount(int increaseBy) {
				
				int amount = Integer.parseInt(textViewListFormAmount.getText().toString());
				
				if ((amount + increaseBy) < 100) {
						
						amount = amount + increaseBy;
				};
				
				textViewListFormAmount.setText(String.valueOf(amount));
		}
		
		
		/**
		 * Dekrementace množství
		 *
		 * @param decreaseBy - zmenšení hodnoty o
		 */
		public void decrementAmount(int decreaseBy) {
				
				int amount = Integer.parseInt(textViewListFormAmount.getText().toString());
				
				if ((amount - decreaseBy) > 0) {
						
						amount = amount - decreaseBy;
				}
				
				textViewListFormAmount.setText(String.valueOf(amount));
		}
		
		
		/**
		 * Přidání produktu do Listu
		 *
		 * @param view
		 */
		public void addProduct(View view) {
				
				// Skrytí klávesnice
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(autoCompleteListFormProductName.getWindowToken(), 0);
				
				if (!autoCompleteListFormProductName.getText().toString().isEmpty()) {
						
						// Vytvoření nového produktu
						Product product = new Product();
						product.setAmount(Integer.parseInt(textViewListFormAmount.getText().toString()));
						product.setName(StringUtils.capitalize(autoCompleteListFormProductName.getText().toString()));
						
						// Vynulování hodnot
						autoCompleteListFormProductName.setText("");
						textViewListFormAmount.setText(String.valueOf(1));
						
						if (listModification) {
								
								productsAdded.add(product);
						}
						
						products.add(product);
						
						// Uložení produkto do databáze + získání ID
						long productID =  databaseHelper.addProduct(product.getName());
						
						// Dodatečné získání ID, pokud produkt už existuje v databázi
						if (productID == -1) {
						
								productID = databaseHelper.getProductIdByName(product.getName());
								
						} else Toast.makeText(ListFormActivity.this, getString(R.string.product_saved), Toast.LENGTH_LONG).show();
						
						// Nastavení ID produktu
						products.get(products.size() - 1).setId(productID);
						
						listFormRecyclerViewAdapter.notifyItemChanged(products.size() - 1);
				}
		}
		
}