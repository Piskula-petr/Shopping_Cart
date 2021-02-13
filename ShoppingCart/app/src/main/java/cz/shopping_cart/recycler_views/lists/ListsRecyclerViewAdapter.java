package cz.shopping_cart.recycler_views.lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cz.shopping_cart.R;
import cz.shopping_cart.pojo.ListInfo;

public class ListsRecyclerViewAdapter extends RecyclerView.Adapter<ListsViewHolder> {
		
		private Context context;
		private List<ListInfo> lists;
		private LayoutInflater layoutInflater;
		
		
		/**
		 * Konstruktor
		 *
		 * @param context
		 * @param lists
		 */
		public ListsRecyclerViewAdapter(Context context, List<ListInfo> lists) {
				this.context = context;
				this.lists = lists;
				layoutInflater = LayoutInflater.from(context);
		}
		
		
		@NonNull
		@Override
		public ListsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
				
				View view = layoutInflater.inflate(R.layout.recycler_view_lists_item, parent, false);
				ListsViewHolder listsViewHolder = new ListsViewHolder(view, context);
				
				return listsViewHolder;
		}
		
		
		@Override
		public void onBindViewHolder(@NonNull ListsViewHolder viewHolder, int i) {
		
				viewHolder.textViewListsRowName.setText(lists.get(i).getName());
				viewHolder.textViewListsRowCount.setText(String.valueOf(lists.get(i).getProductCount()));
				viewHolder.textViewListsRowDate.setText(lists.get(i).getLastUse());
				viewHolder.textViewListRowID.setText(String.valueOf(lists.get(i).getId()));
		}
		
		
		@Override
		public int getItemCount() {
				return lists.size();
		}
		
}
