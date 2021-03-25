package persistence;

import models.Purchase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PurchasePersistor {

    private Purchase purchase;

    public PurchasePersistor(Purchase p){
        this.purchase = p;
    }

    public boolean persistPurchase() throws SQLException {
        Connection cnxn = null;
        int storeId = this.purchase.getStoreId();
        int customerId = purchase.getCustomerId();
        String purchaseQuery = "INSERT INTO purchase (store_id, customer_id, purchase_date) VALUES(?,?,?)";
        try{
            cnxn = DbConnection.getConnection();
            PreparedStatement purchaseStatement = cnxn.prepareStatement(purchaseQuery);
            purchaseStatement.setInt(1,storeId);
            purchaseStatement.setInt(2,customerId);
            purchaseStatement.setString(3,purchase.getDate());
            boolean purchaseStored = purchaseStatement.execute();
            purchaseStatement.close();
            cnxn.close();
        } catch (SQLException e){
            System.out.println("Failed to persist purchase " + this.purchase);
        } finally{
            if (cnxn != null) {
                cnxn.close();
            }
            }
        return true;
    }

    public boolean persistPurchaseItems(){
        return false;
    }
}
