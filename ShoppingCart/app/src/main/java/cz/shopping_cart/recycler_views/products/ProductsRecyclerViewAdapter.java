package cz.shopping_cart.recycler_views.products;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cz.shopping_cart.R;
import cz.shopping_cart.recycler_views.products.ProductsViewHolder;

public class ProductsRecyclerViewAdapter extends RecyclerView.Adapter<ProductsViewHolder> {
		
		private Context context;
		private List<String> productNames;
		private LayoutInflater layoutInflater;
		
		
		/**
		 * Konstruktor
		 *
		 * @param context
		 * @param productNames - List názvů produktů
		 */
		public ProductsRecyclerViewAdapter(Context context, List<String> productNames) {
				this.context = context;
				this.productNames = productNames;
				layoutInflater = LayoutInflater.from(context);
		}
		
		
		@NonNull
		@Override
		public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
				
				View view =  layoutInflater.inflate(R.layout.recycler_view_products_item, parent, false);
				ProductsViewHolder productsViewHolder = new ProductsViewHolder(view);
				
				return productsViewHolder;
		}
		
		
		@Override
		public void onBindViewHolder(@NonNull ProductsViewHolder viewHolder, int i) {
		
				viewHolder.textViewProductsRowName.setText(productNames.get(i));
		}
		
		
		@Override
		public int getItemCount() {
				return productNames.size();
		}
		
}
