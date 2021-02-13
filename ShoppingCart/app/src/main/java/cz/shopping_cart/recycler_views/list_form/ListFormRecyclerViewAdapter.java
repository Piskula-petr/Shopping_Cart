package cz.shopping_cart.recycler_views.list_form;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cz.shopping_cart.R;
import cz.shopping_cart.pojo.Product;

public class ListFormRecyclerViewAdapter extends RecyclerView.Adapter<ListFormViewHolder> {
		
		private Context context;
		private List<Product> products;
		private LayoutInflater layoutInflater;
		
		
		/**
		 * Konstruktor
		 *
		 * @param context
		 * @param products - List produktů
		 */
		public ListFormRecyclerViewAdapter(Context context, List<Product> products) {
				
				this.context = context;
				this.products = products;
				layoutInflater = LayoutInflater.from(context);
		}

		
		@NonNull
		@Override
		public ListFormViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
				
				View view = layoutInflater.inflate(R.layout.recycler_view_list_form_item, parent, false);
				ListFormViewHolder viewHolder = new ListFormViewHolder(view);
				
				return viewHolder;
		}
		
		
		@Override
		public void onBindViewHolder(@NonNull ListFormViewHolder viewHolder, int i) {
				
				viewHolder.textViewListFormRowAmount.setText(String.valueOf(products.get(i).getAmount()));
				viewHolder.textViewListFormRowName.setText(products.get(i).getName());
		}
		
		
		@Override
		public int getItemCount() {
				return products.size();
		}
		
}
