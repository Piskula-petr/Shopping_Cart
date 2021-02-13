package cz.shopping_cart.recycler_views.products;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import cz.shopping_cart.R;

public class ProductsViewHolder extends RecyclerView.ViewHolder {
		
		protected TextView textViewProductsRowName;
		
		
		/**
		 * Konstruktor
		 *
		 * @param itemView
		 */
		public ProductsViewHolder(@NonNull View itemView) {
				super(itemView);
				
				textViewProductsRowName = itemView.findViewById(R.id.textViewProductsRowName);
		}
		
}
