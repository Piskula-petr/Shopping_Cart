package cz.shopping_cart.recycler_views.list_form;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cz.shopping_cart.R;

public class ListFormViewHolder extends RecyclerView.ViewHolder {
		
		protected TextView textViewListFormRowAmount, textViewListFormRowName;
		
		
		/**
		 * Konstruktor
		 *
		 * @param itemView
		 */
		public ListFormViewHolder(@NonNull View itemView) {
				super(itemView);
				
				textViewListFormRowAmount = itemView.findViewById(R.id.textViewListFormRowAmount);
				textViewListFormRowName = itemView.findViewById(R.id.textViewListFormRowName);
		}
		
}
