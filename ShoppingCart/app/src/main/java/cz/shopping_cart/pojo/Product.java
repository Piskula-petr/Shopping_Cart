package cz.shopping_cart.pojo;

import java.io.Serializable;

public class Product implements Serializable {

		private long id;
		private int amount;
		private String name;
		
// Konstruktor /////////////////////////////////////////////////////////////////////////////////
		
		public Product() {
		
		}
		
// Gettery + Settery ///////////////////////////////////////////////////////////////////////////
		
		public long getId() {
				return id;
		}
		
		public void setId(long id) {
				this.id = id;
		}
		
		public int getAmount() {
				return amount;
		}
		
		public void setAmount(int amount) {
				this.amount = amount;
		}
		
		public String getName() {
				return name;
		}
		
		public void setName(String name) {
				this.name = name;
		}
		
}
