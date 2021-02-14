package cz.shopping_cart.activities;

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
import cz.shopping_cart.pojo.ListInfo;
import cz.shopping_cart.recycler_views.lists.ListsRecyclerViewAdapter;

public class ListsActivity extends AppCompatActivity {
		
		private TextView textViewListsEmpty;
		private DrawerLayout drawerLayoutLists;
		private ActionBarDrawerToggle actionBarDrawerToggle;
		private NavigationView navigationViewLists;
		
		private DatabaseHelper databaseHelper;
		
		// Upravený RecyclerView
		private RecyclerView recyclerViewLists;
		private ListsRecyclerViewAdapter listsRecyclerViewAdapter;
		
		// List informací o seznamu
		private List<ListInfo> lists = new ArrayList<>();
		
		// Smazání seznamu - [posun doprava]
		private ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
				
				@Override
				public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
						return false;
				}
				
				@Override
				public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
						
						int listIndex = viewHolder.getAbsoluteAdapterPosition();
						
						// Smazání položek v seznamu + smazání seznamu
						boolean result = databaseHelper.deleteListById(lists.get(listIndex).getId());
						
						if (result) Toast.makeText(ListsActivity.this, getString(R.string.list_delete_dialog), Toast.LENGTH_LONG).show();
						
						// Odstranění seznamu
						lists.remove(listIndex);
						
						// Zobrazení zprávy, při odstranění všech seznamů
						if (lists.size() == 0) {
								
								textViewListsEmpty.setVisibility(View.VISIBLE);
						}
						
						listsRecyclerViewAdapter.notifyItemRemoved(listIndex);
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
				
				setContentView(R.layout.activity_lists);
				
				// Nastavení orientace na výšku
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
				
				// Nastavení režimu celé obrazovky
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
				
				drawerLayoutLists = findViewById(R.id.drawerLayoutLists);
				
				// Tlačítko pro zobrazení / skrytí navigačního menu
				actionBarDrawerToggle = new ActionBarDrawerToggle(ListsActivity.this, drawerLayoutLists, R.string.open, R.string.close);
				actionBarDrawerToggle.syncState();
				
				drawerLayoutLists.addDrawerListener(actionBarDrawerToggle);
				
				// Navigační menu
				navigationViewLists = findViewById(R.id.navigationViewLists);
				navigationViewLists.setCheckedItem(R.id.menu_lists);
				navigationViewLists.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
						
						@Override
						public boolean onNavigationItemSelected(@NonNull MenuItem item) {
								
								// Přesměrování na jinou aktivitu
								switch (item.getItemId()) {
										
										// Produkty
										case R.id.menu_products:
												
												startActivity(new Intent(ListsActivity.this, ProductsActivity.class));
												break;
										
										// Nový seznam
										case R.id.menu_new_list:
												
												startActivity(new Intent(ListsActivity.this, ListFormActivity.class));
												break;
								}
								
								// Uzavření menu
								drawerLayoutLists.closeDrawer(GravityCompat.START);
								
								return true;
						}
				});
				
				// Nastavení panelu akcí
				getSupportActionBar().setDisplayHomeAsUpEnabled(true);
				getSupportActionBar().setTitle(getString(R.string.lists));
				
				textViewListsEmpty = findViewById(R.id.textViewListsEmpty);
				
// Zobrazení seznamů /////////////////////////////////////////////////////////////////////////////////////////////////////////

				databaseHelper = new DatabaseHelper(ListsActivity.this);
				lists = databaseHelper.getLists();
				
				// Skrytí zprávy
				if (lists.size() > 0) {
						
						textViewListsEmpty.setVisibility(View.INVISIBLE);
				}
				
				listsRecyclerViewAdapter = new ListsRecyclerViewAdapter(ListsActivity.this, lists);
				
				recyclerViewLists = findViewById(R.id.recyclerViewLists);
				recyclerViewLists.setAdapter(listsRecyclerViewAdapter);
				recyclerViewLists.setLayoutManager(new LinearLayoutManager(ListsActivity.this));
				
				ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
				itemTouchHelper.attachToRecyclerView(recyclerViewLists);
		}
		
		
		/**
		 * Obnovení aktivity
		 */
		@Override
		protected void onRestart() {
				super.onRestart();
				
				startActivity(new Intent(ListsActivity.this, ListsActivity.class));
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