package persistence;

import models.Purchase;

public class PurchasePersistor {

    private Purchase purchase;

    public PurchasePersistor(Purchase p){
        this.purchase = p;
    }

    public boolean persistPurchase(){
        return false;
    }

    public boolean persistPurchaseItems(){
        return false;
    }
}
