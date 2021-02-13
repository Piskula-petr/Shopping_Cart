package cz.shopping_cart.recycler_views.lists;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.List;

import cz.shopping_cart.DatabaseHelper;
import cz.shopping_cart.R;
import cz.shopping_cart.activities.ListFormActivity;
import cz.shopping_cart.activities.ListPreviewActivity;
import cz.shopping_cart.pojo.Product;

public class ListsViewHolder extends RecyclerView.ViewHolder {
		
		protected TextView textViewListsRowName;
		protected TextView textViewListsRowCount;
		protected TextView textViewListsRowDate;
		protected TextView textViewListRowID;
		
		
		/**
		 * Konstruktor
		 *
		 * @param itemView
		 */
		public ListsViewHolder(@NonNull View itemView, Context context) {
				super(itemView);
				
				textViewListsRowName = itemView.findViewById(R.id.textViewListsRowName);
				textViewListsRowCount = itemView.findViewById(R.id.textViewListsRowCount);
				textViewListsRowDate = itemView.findViewById(R.id.textViewListsRowDate);
				textViewListRowID = itemView.findViewById(R.id.textViewListRowID);
				
				itemView.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View view) {
								
								Intent listPreview = createIntent(context, view, ListPreviewActivity.class);
								
								context.startActivity(listPreview);
						}
				});
				
				itemView.setOnLongClickListener(new View.OnLongClickListener() {
						
						@Override
						public boolean onLongClick(View view) {
								
								Intent listModification = createIntent(context, view, ListFormActivity.class);
								listModification.putExtra("listModification", true);
								
								context.startActivity(listModification);
								
								return true;
						}
				});
				
		}
		
		
		/**
		 * Nastavení přesměrování na požadovanou aktivitu
		 *
		 * @param context - context
		 * @param view - kliknuty view
		 * @param finalDestination - cílová třída
		 *
		 * @return - vrací nastavený Intent s parametry
		 */
		private Intent createIntent(Context context, View view, Class finalDestination) {
				
				// Získání ID seznamu
				TextView textViewListRowID = view.findViewById(R.id.textViewListRowID);
				long listID = Long.parseLong(textViewListRowID.getText().toString());
				
				// Získání názvu seznamu
				TextView textViewListsRowName = view.findViewById(R.id.textViewListsRowName);
				String listName = textViewListsRowName.getText().toString();
				
				DatabaseHelper databaseHelper = new DatabaseHelper(context);
				List<Product> products = databaseHelper.getProductsByListID(listID);
				
				// Aktualizace datumu seznamu
				int result = databaseHelper.updateListDate(listID);
				
				if (result > 0) Toast.makeText(context, "Datum posledního použití by změněn", Toast.LENGTH_LONG).show();
				
				// Přesměrování
				Intent intent = new Intent(context, finalDestination);
				intent.putExtra("listName", listName);
				intent.putExtra("listID", listID);
				intent.putExtra("products", (Serializable) products);
				
				return intent;
		}
		
}
