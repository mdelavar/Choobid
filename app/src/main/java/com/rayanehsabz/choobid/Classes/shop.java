package com.rayanehsabz.choobid.Classes;

/**
 * Created by sabz3 on 04/09/2017.
 */

public class shop {
    public long pId;
    public long price;

    public boolean bid = false;
    public String Pname = "";
    public String PEname= "";
    public String PfeId= "";

    public String name= "";
    public String phone= "";
    public String phone2= "";

    public String state= "";
    public String city = "0";

    public String token = "";
    public String merchant = "";

    public String postCode= "";
    public String street= "";
    public String kooche= "";
    public String pelak= "";
    public String tabaghe= "";
    public String vahed= "";

    public String address= "";

    public long TotalPrice = 0;

    public long Discount = 0;
    public String DiscountCode;

    public long count = 0;

    public long invoiceId = 0;

    public boolean factor = false;
    public boolean kaado = false;
    public boolean portable = false;

    public shop () {
        factor = false;
        kaado = false;
        count = 1;
        Discount = 0;
        DiscountCode = "0";
    }

}
