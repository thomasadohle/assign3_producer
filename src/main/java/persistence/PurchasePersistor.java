package persistence;

import models.Purchase;
import models.PurchaseItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PurchasePersistor {

    private Purchase purchase;
    private boolean purchasePersisted = false;
    private boolean purchaseItemsPersisted = false;
    private int persistPurchaseAttempts = 0;
    private int PersistPurchaseItemsAttempts = 0;

    public PurchasePersistor(Purchase p){
        this.purchase = p;
    }

    public boolean persistPurchase(){
        while (! this.purchasePersisted && this.persistPurchaseAttempts<5){
            this.executePersistPurchase();
        }
        return true;
    }

    public boolean persistPurchaseItems(){
        while (! this.purchaseItemsPersisted && this.PersistPurchaseItemsAttempts<5){
            this.executePersistPurchaseItems();
        }
        return true;
    }




    private void executePersistPurchase(){
        this.persistPurchaseAttempts ++;
        int storeId = this.purchase.getStoreId();
        int customerId = purchase.getCustomerId();
        String purchaseQuery = "INSERT INTO purchase (store_id, customer_id, purchase_date) VALUES(?,?,?)";
        try (Connection cnxn = DbConnection.getConnection(); PreparedStatement purchaseStatement = cnxn.prepareStatement(purchaseQuery)) {
            purchaseStatement.setInt(1, storeId);
            purchaseStatement.setInt(2, customerId);
            purchaseStatement.setString(3, purchase.getDate());
            boolean purchaseStored = purchaseStatement.execute();
            this.purchasePersisted = true;
        } catch (SQLException e) {
            System.out.println("Failed to persist purchase " + this.purchase);
        }

    }

    private void executePersistPurchaseItems() {
        this.PersistPurchaseItemsAttempts ++;
        int storeId = this.purchase.getStoreId();
        int customerId = purchase.getCustomerId();
        String purchaseItemsQuery = "INSERT INTO purchase_item (item_id, num_items, store_id, customer_id) VALUES (?,?,?,?)";
        try (Connection cnxn = DbConnection.getConnection(); PreparedStatement purchaseItemsStatement = cnxn.prepareStatement(purchaseItemsQuery)) {
            for (PurchaseItem item : purchase.getItems()) {
                purchaseItemsStatement.setInt(1, item.getItemID());
                purchaseItemsStatement.setInt(2, item.getNumberOfItems());
                purchaseItemsStatement.setInt(3, storeId);
                purchaseItemsStatement.setInt(4, customerId);
                purchaseItemsStatement.addBatch();
            }
            purchaseItemsStatement.executeBatch();
            this.purchaseItemsPersisted = true;
        } catch (Exception e){
            System.out.println("Failed to persist PurcahseItems for Purchase " + this.purchase);
        }
    }
}
