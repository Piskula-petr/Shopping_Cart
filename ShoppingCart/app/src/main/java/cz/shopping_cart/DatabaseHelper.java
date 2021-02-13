package cz.shopping_cart;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cz.shopping_cart.pojo.ListInfo;
import cz.shopping_cart.pojo.ListName;
import cz.shopping_cart.pojo.Product;

public class DatabaseHelper extends SQLiteOpenHelper {
		
		// Názvy tabůlek
		private final String TABLE_PRODUCTS = "products";
		private final String TABLE_LISTS = "lists";
		private final String TABLE_LIST_ITEMS = "list_items";
		
		// Názvy řádků atributů
		private final String TABLE_ROW_ID = "id";
		private final String TABLE_ROW_LIST_ID = "list_id";
		private final String TABLE_ROW_PRODUCT_ID = "product_id";
		private final String TABLE_ROW_NAME = "name";
		private final String TABLE_ROW_DATE = "date";
		private final String TABLE_ROW_AMOUNT = "amount";
		
		
		/**
		 * Konstruktor
		 *
		 * @param context
		 */
		public DatabaseHelper(@Nullable Context context) {
				super(context, "database.db", null, 1);
		}
		
		
		/**
		 * Vytvoření databáze
		 *
		 * @param database
		 */
		@Override
		public void onCreate(SQLiteDatabase database) {

				// Produkty
				database.execSQL(
						
						"CREATE TABLE " + TABLE_PRODUCTS + " ( "
							+ TABLE_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
							+ TABLE_ROW_NAME + " TEXT UNIQUE"
					+ ");"
				);
				
				// Seznamy
				database.execSQL(
						
						"CREATE TABLE " + TABLE_LISTS + " ( "
							+ TABLE_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
							+ TABLE_ROW_NAME + " TEXT UNIQUE, "
							+ TABLE_ROW_DATE + " TEXT"
					+ ");"
				);
				
				// Položky seznamu
				database.execSQL(
						
						"CREATE TABLE " + TABLE_LIST_ITEMS + " ( "
							+ TABLE_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
							+ TABLE_ROW_LIST_ID + " INTEGER, "
							+ TABLE_ROW_PRODUCT_ID + " INTEGER, "
							+ TABLE_ROW_AMOUNT + " INTEGER, "
							+ "FOREIGN KEY (" + TABLE_ROW_LIST_ID + ") REFERENCES " + TABLE_LISTS + "(" + TABLE_ROW_ID + "), "
							+ "FOREIGN KEY (" + TABLE_ROW_PRODUCT_ID + ") REFERENCES " + TABLE_PRODUCTS + "(" + TABLE_ROW_ID + ")"
					+ ");"
				);
		}
		
		
		/**
		 * Při otevření databáze
		 *
		 * @param database
		 */
		@Override
		public void onOpen(SQLiteDatabase database) {
				super.onOpen(database);
				
				// Povolení cizích klíčů
				database.setForeignKeyConstraintsEnabled(true);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {}
		
		
// Vložení do databáze //////////////////////////////////////////////////////////////////////////////////////////////////////
		
		
		/**
		 * Přidání produktu do databáze
		 *
		 * @param name - název produktu
		 *
		 * @return - vrací ID přidaného produktu
		 */
		public long addProduct(String name) {
				
				SQLiteDatabase database = this.getWritableDatabase();
				
				ContentValues values = new ContentValues();
				values.put(TABLE_ROW_NAME, name);

				long productId = database.insertWithOnConflict(TABLE_PRODUCTS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
				
				database.close();
				
				return productId;
		}
		
		
		/**
		 * Přidání seznamu do databáze
		 *
		 * @param listName - název seznamu
		 * @param products - List produktů
		 *
		 * @return - vrací výsledek oprace
		 */
		public boolean addList(String listName, List<Product> products) {
				
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Calendar calendar = Calendar.getInstance();
				
				SQLiteDatabase database = this.getWritableDatabase();
				
				long result = -1;
				
				try {
						
						database.beginTransaction();

// Uložení seznamu /////////////////////////////////
						
						ContentValues values = new ContentValues();
						values.put(TABLE_ROW_NAME, listName);
						values.put(TABLE_ROW_DATE, format.format(calendar.getTime()));
						
						long listID = database.insert(TABLE_LISTS, null, values);

// Uložení produktů do seznamu //////////////////////
						
						for (Product product : products) {
								
								values.clear();
								values.put(TABLE_ROW_LIST_ID, listID);
								values.put(TABLE_ROW_PRODUCT_ID, product.getId());
								values.put(TABLE_ROW_AMOUNT, product.getAmount());
								
								result = database.insert(TABLE_LIST_ITEMS, null, values);
						}
						
				} finally {
						
						database.setTransactionSuccessful();
						database.endTransaction();
						database.close();
				}
				
				return (result == -1) ? false : true;
		}
		
		
// Získání z databáze //////////////////////////////////////////////////////////////////////////////////////////////////////
		
		
		/**
		 * Získání ID produktu, podle názvu
		 *
		 * @param name - název produktu
		 *
		 * @return - vrací ID produktu
		 */
		public long getProductIdByName(String name) {
				
				long productID = 0;
				
				SQLiteDatabase database = this.getReadableDatabase();
				Cursor cursor = database.rawQuery(
						
						"SELECT " + TABLE_ROW_ID + " "
					+ "FROM " + TABLE_PRODUCTS + " "
					+ "WHERE " + TABLE_ROW_NAME + " = ?;"
					
				, new String[] {name});
				
				if (cursor.moveToFirst()) {
						
						productID = cursor.getLong(0);
				}
				
				cursor.close();
				database.close();
				
				return productID;
		}
		
		
		/**
		 * Získání názvů všech produktů
		 *
		 * @return - vrací List názvů produktů
		 */
		public List<String> getProductNames() {
				
				List<String> productNames = new ArrayList<>();
				
				SQLiteDatabase database = this.getReadableDatabase();
				Cursor cursor = database.rawQuery(
						
						"SELECT " + TABLE_ROW_NAME + " "
					+ "FROM " + TABLE_PRODUCTS + " "
					+ "ORDER BY " + TABLE_ROW_NAME + ";"
						
				, null);
				
				if (cursor.moveToFirst()) {
						
						do {
								
								productNames.add(cursor.getString(0));
								
						} while (cursor.moveToNext());
				}
				
				cursor.close();
				database.close();
				
				return productNames;
		}
		
		
		/**
		 * Získání produktů ze seznamu, podle ID
		 *
		 * @param listID - ID seznamu
		 *
		 * @return - vrací List produktů v seznamu
		 */
		public List<Product> getProductsByListID(long listID) {
				
				List<Product> products = new ArrayList<>();
				
				SQLiteDatabase database = this.getReadableDatabase();
				Cursor cursor = database.rawQuery(
						
						"SELECT " + TABLE_PRODUCTS + "." + TABLE_ROW_ID + ", " + TABLE_LIST_ITEMS + "." + TABLE_ROW_AMOUNT + ", " + TABLE_PRODUCTS + "." + TABLE_ROW_NAME + " "
					+ "FROM " + TABLE_LIST_ITEMS + " "
					+ "JOIN " + TABLE_PRODUCTS + " ON " + TABLE_PRODUCTS + "." + TABLE_ROW_ID + " = " + TABLE_LIST_ITEMS + "." + TABLE_ROW_PRODUCT_ID	 + " "
					+	"WHERE " + TABLE_ROW_LIST_ID + " = ?;"
					
				, new String[] {String.valueOf(listID)});
				
				if (cursor.moveToFirst()) {
						
						do {
								
								Product product = new Product();
								product.setId(cursor.getLong(0));
								product.setAmount(cursor.getInt(1));
								product.setName(cursor.getString(2));
								products.add(product);
								
						} while (cursor.moveToNext());
				}
				
				cursor.close();
				database.close();
				
				return products;
		}
		
		
		
		/**
		 * Získání výchozího názvu seznamu
		 *
		 * @return - výchozí název seznamu
		 */
		public String getDefaultListName() {
				
				String defaultListName = "Můj seznam ";
				int count = 1;
				
				SQLiteDatabase database = this.getReadableDatabase();
				Cursor cursor = database.rawQuery(
						
						"SELECT SUBSTR(" + TABLE_ROW_NAME + ", 12) AS count "
								+ "FROM " + TABLE_LISTS + " "
								+ "WHERE " + TABLE_ROW_NAME + " LIKE 'Můj seznam% %' "
								+ "ORDER BY LENGTH(count);"
						
						, null);
				
				if (cursor.moveToFirst()) {
						
						while (true) {
								
								// Ukončení cyklu, při nalezení hodnoty, která neodpovídá hodnotě z databáze
								if (count != cursor.getInt(0)) {
										
										break;
										
								// Při shodě, načtení další hodnoty z databáze
								} else if (count == cursor.getInt(0)) {
										
										boolean hasNext = cursor.moveToNext();
										
										// Ukončení cyklu, při vyčerpání všech hodnot z databáze
										if (!hasNext) {
												
												count++;
												break;
										}
								}
								count++;
						}
				}
				
				cursor.close();
				database.close();
				
				return defaultListName + count;
		}
		
		
		/**
		 * Získání všech seznamů
		 *
		 * @return - získání Listu seznamů
		 */
		public List<ListInfo> getLists() {
				
				List<Long> listIDs = getListIDs();
				List<ListInfo> lists = new ArrayList<>();
				
				SQLiteDatabase database = this.getReadableDatabase();
				
				for (Long id : listIDs) {
						
						Cursor cursor = database.rawQuery(
								
								"SELECT " + TABLE_LISTS + "." + TABLE_ROW_ID + ", " + TABLE_LISTS + "." + TABLE_ROW_NAME + ", ("
										
										+ "SELECT COUNT(*) "
										+ "FROM " + TABLE_LIST_ITEMS + " "
										+ "WHERE " + TABLE_ROW_LIST_ID + " = ? "
										
										+ "), " + TABLE_LISTS + "." + TABLE_ROW_DATE + " "
										+ "FROM " + TABLE_LIST_ITEMS + " "
										+ "JOIN " + TABLE_LISTS + " ON " + TABLE_LISTS + "." + TABLE_ROW_ID + " = " + TABLE_LIST_ITEMS + "." + TABLE_ROW_LIST_ID + " "
										+ "WHERE " + TABLE_ROW_LIST_ID + " = ?"
										+ "LIMIT 1;"
								
								, new String[] {String.valueOf(id), String.valueOf(id)});
						
						if (cursor.moveToFirst()) {
								
								do {
										
										ListInfo listInfo = new ListInfo();
										listInfo.setId(cursor.getLong(0));
										listInfo.setName(cursor.getString(1));
										listInfo.setProductCount(cursor.getInt(2));
										
										SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
										SimpleDateFormat outputFormat = new SimpleDateFormat("d.M.yyyy");
										
										listInfo.setLastUse(dateFormat(cursor.getString(3), inputFormat, outputFormat));
										lists.add(listInfo);
										
								} while (cursor.moveToNext());
						}
				}
				
				// Setřídění Listu podle datumu použití
				Collections.sort(lists, new Comparator<ListInfo>() {
						
						@Override
						public int compare(ListInfo listInfo1, ListInfo listInfo2) {
								return listInfo2.getLastUse().compareTo(listInfo1.getLastUse());
						}
				});
				
				database.close();
				
				return lists;
		}
		
		
		/**
		 * Získání počtu seznamů
		 *
		 * @return - vrací počet seznamů
		 */
		public int getListsCount() {
				
				int listsCount = 0;
				
				SQLiteDatabase database = this.getReadableDatabase();
				Cursor cursor = database.rawQuery(
						
						"SELECT COUNT(*) "
								+ "FROM " + TABLE_LISTS + ";"
						
						, null);
				
				if (cursor.moveToFirst()) {
						
						listsCount = cursor.getInt(0);
				}
				
				cursor.close();
				database.close();
				
				return listsCount;
		}
		
		
		/**
		 * Získání názvů seznamu, podle ID produktu
		 *
		 * @param productID - produkt ID
		 *
		 * @return - vrací List názvů seznamu
		 */
		public List<ListName> getListNamesOfProduct(long productID) {
				
				List<ListName> listNames = new ArrayList<>();
				
				SQLiteDatabase database = this.getReadableDatabase();
				Cursor cursor = database.rawQuery(
						
						"SELECT " + TABLE_LISTS + "." + TABLE_ROW_ID + ", " + TABLE_LISTS + "." + TABLE_ROW_NAME + " "
								+ "FROM " + TABLE_LIST_ITEMS + " "
								+ "JOIN " + TABLE_PRODUCTS + " ON " + TABLE_PRODUCTS + "." + TABLE_ROW_ID + " = " + TABLE_LIST_ITEMS + "." + TABLE_ROW_PRODUCT_ID	+ " "
								+ "JOIN " + TABLE_LISTS + " ON " + TABLE_LISTS + "." + TABLE_ROW_ID + " = " + TABLE_LIST_ITEMS + "." + TABLE_ROW_LIST_ID + " "
								+ "WHERE " + TABLE_PRODUCTS + "." + TABLE_ROW_ID + " = ?;"
						
						, new String[] {String.valueOf(productID)});
				
				if (cursor.moveToFirst()) {
						
						do {
								
								ListName listName = new ListName();
								listName.setId(cursor.getLong(0));
								listName.setName(cursor.getString(1));
								listNames.add(listName);
								
						} while (cursor.moveToNext());
				}
				
				cursor.close();
				database.close();
				
				return listNames;
		}
		
		
		/**
		 * Zíksání ID všech seznamů
		 *
		 * @return - vrací List ID seznamů
		 */
		private List<Long> getListIDs() {
				
				List<Long> listIDs = new ArrayList<>();
				
				SQLiteDatabase database = this.getReadableDatabase();
				Cursor cursor = database.rawQuery(
						
						"SELECT " + TABLE_ROW_ID + " "
								+ "FROM " + TABLE_LISTS + ";"
						
						, null);
				
				if (cursor.moveToFirst()) {
						
						do {
								
								listIDs.add(cursor.getLong(0));
								
						} while (cursor.moveToNext());
				}
				
				cursor.close();
				database.close();
				
				return listIDs;
		}
		
		
// Odstranění z databáze ///////////////////////////////////////////////////////////////////////////////////////////////
		
		
		/**
		 * Smazání produktu, podle ID
		 *
		 * @param productId - ID produktu
		 *
		 * @return - vrací výsledek operace
		 */
		public boolean deleteProductById(long productId) {
				
				SQLiteDatabase database = this.getWritableDatabase();
				
				int result = database.delete(TABLE_PRODUCTS, TABLE_ROW_ID + " = ?", new String[] {String.valueOf(productId)});
				
				database.close();
				
				return (result == 0) ? false : true;
		}
		
		
		/**
		 * Smazání produktu ze seznamů, podle ID
		 *
		 * @param productId - ID produktu
		 *
		 * @return - vrací výsledek operace
		 */
		public boolean deleteProductFromListsById(long productId) {
				
				int listResult = 0;
				int productResult = 0;
				
				SQLiteDatabase database = this.getWritableDatabase();
				
				try {
						
						database.beginTransaction();
						
						listResult = database.delete(TABLE_LIST_ITEMS, TABLE_ROW_PRODUCT_ID + " = ?", new String[] {String.valueOf(productId)});
						productResult = database.delete(TABLE_PRODUCTS, TABLE_ROW_ID + " = ?", new String[] {String.valueOf(productId)});
						
				} finally {
						
						database.setTransactionSuccessful();
						database.endTransaction();
						database.close();
				}
				
				return (listResult == 0 | productResult == 0) ? false : true;
		}
		
		
		/**
		 * Smazání seznamu, podle ID
		 *
		 * @param listId - ID seznamu
		 *
		 * @return - vrací výsledek operace
		 */
		public boolean deleteListById(long listId) {
				
				int listItemResult = 0;
				int listResult = 0;
				
				SQLiteDatabase database = this.getWritableDatabase();
				
				try {
						
						database.beginTransaction();
						
						listItemResult = database.delete(TABLE_LIST_ITEMS, TABLE_ROW_LIST_ID + " = ?", new String[] {String.valueOf(listId)});
						listResult = database.delete(TABLE_LISTS, TABLE_ROW_ID + " = ?", new String[] {String.valueOf(listId)});
						
				} finally {
						
						database.setTransactionSuccessful();
						database.endTransaction();
						database.close();
				}
				
				return (listItemResult == 0 | listResult == 0) ? false : true;
		}
		
		
		/**
		 * Smazání prázdných seznamů
		 */
		public void deleteEmptyLists() {
				
				SQLiteDatabase database = getWritableDatabase();
				
				database.execSQL(
						
						"DELETE FROM " + TABLE_LISTS + " "
					+	"WHERE " + TABLE_LISTS + "." + TABLE_ROW_ID + " NOT IN ( "
							
							+ "SELECT " + TABLE_LIST_ITEMS + "." + TABLE_ROW_LIST_ID + " "
							+ "FROM " + TABLE_LIST_ITEMS + " "
							
					+ ");"
				);
				
				database.close();
		}
		
// Úprava databáze //////////////////////////////////////////////////////////////////////////////////////////////////////
		
		
		/**
		 * Aktualizace datumu seznamu
		 *
		 * @param listId - ID seznamu
		 *
		 * @return - vrací výsledek operace
		 */
		public int updateListDate(long listId) {
				
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				Calendar calendar = Calendar.getInstance();
				
				SQLiteDatabase database = getWritableDatabase();
				
				ContentValues values = new ContentValues();
				values.put(TABLE_ROW_DATE, format.format(calendar.getTime()));
				
				// Podmínka
				String whereClause =
						
						TABLE_ROW_ID + " = ? AND NOT EXISTS ( "
								
						 	+ "SELECT " + TABLE_ROW_DATE + " "
						 	+ "FROM " + TABLE_LISTS + " "
							+ "WHERE " + TABLE_ROW_DATE + " = ? "
							
					+ ")";
				
				int result = database.update(TABLE_LISTS, values, whereClause, new String[] {String.valueOf(listId), format.format(calendar.getTime())});
				
				database.close();
				
				return result;
		}
		
		
		/**
		 * Úprava seznamu
		 *
		 * @param listID - ID seznamu
		 * @param listName - název seznamu
		 * @param productsRemoved - List produktů k odstranění
		 * @param productsAdded - List produktů k přidání
		 *
		 * @return - vrací výsledek oprace
		 */
		public boolean updateList(long listID, String listName, List<Product> productsRemoved, List<Product> productsAdded) {
				
				int resultListName = 0;
				int resultProductsRemoved = 0;
				long resultProductsAdded = 0;
				
				SQLiteDatabase database = this.getWritableDatabase();
				
				try {
						
						database.beginTransaction();
						
// Změna názvu seznamu ///////////////////////////
						
						ContentValues values = new ContentValues();
						values.put(TABLE_ROW_NAME, listName);
						
						// Podmínka
						String whereClause =
								
								TABLE_ROW_ID + " = ? AND NOT EXISTS ( "
								
										+ "SELECT " + TABLE_ROW_NAME + " "
										+ "FROM " + TABLE_LISTS + " "
										+ "WHERE " + TABLE_ROW_NAME + " = ? "
								
							+	");";
						
						resultListName = database.update(TABLE_LISTS, values, whereClause, new String[] {String.valueOf(listID), listName});
						
// Odstranění smazaných produktů //////////////////

						for (Product product : productsRemoved) {
								
								resultProductsRemoved = database.delete(TABLE_LIST_ITEMS, TABLE_ROW_PRODUCT_ID + " = ?", new String[] {String.valueOf(product.getId())});
						}
						
// Přidání nových produktů ////////////////////////
						
						for (Product product : productsAdded) {
								
								values.clear();
								values.put(TABLE_ROW_LIST_ID, listID);
								values.put(TABLE_ROW_PRODUCT_ID, product.getId());
								values.put(TABLE_ROW_AMOUNT, product.getAmount());
								
								resultProductsAdded = database.insert(TABLE_LIST_ITEMS, null, values);
						}
						
				} finally {
						
						database.setTransactionSuccessful();
						database.endTransaction();
						database.close();
				}
				
				return ((resultListName + resultProductsRemoved + resultProductsAdded) > 0);
		}
		
		
		/**
		 * Naformátování datumu z databáze do aplikace
		 *
		 * @param date - datum z databáze
		 * @param inputFormat - vstupní formát
		 * @param outputFormat - výstupní formát
		 *
		 * @return
		 */
		private String dateFormat(String date, SimpleDateFormat inputFormat, SimpleDateFormat outputFormat) {
				
				Calendar calendar = Calendar.getInstance();
				
				try {
						
						calendar.setTime(inputFormat.parse(date));
						
				} catch (ParseException e) {
						e.printStackTrace();
				}
				
				return outputFormat.format(calendar.getTime());
		}
		
}
