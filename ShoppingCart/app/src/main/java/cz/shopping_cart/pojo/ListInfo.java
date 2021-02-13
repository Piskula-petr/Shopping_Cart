package cz.shopping_cart.pojo;

public class ListInfo {
		
		private long id;
		private String name;
		private int productCount;
		private String lastUse;
		
// Konstruktor ////////////////////////////////////////////////////////////////////////////////
		
		public ListInfo() {
		
		}
		
// Gettery + Settery //////////////////////////////////////////////////////////////////////////
		
		public long getId() {
				return id;
		}
		
		public void setId(long id) {
				this.id = id;
		}
		
		public String getName() {
				return name;
		}
		
		public void setName(String name) {
				this.name = name;
		}
		
		public int getProductCount() {
				return productCount;
		}
		
		public void setProductCount(int productCount) {
				this.productCount = productCount;
		}
		
		public String getLastUse() {
				return lastUse;
		}
		
		public void setLastUse(String lastUse) {
				this.lastUse = lastUse;
		}
		
}
