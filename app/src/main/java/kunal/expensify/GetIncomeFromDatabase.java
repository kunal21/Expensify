package kunal.expensify;

/**
 * Created by Kunal on 29-04-2017.
 */

public class GetIncomeFromDatabase  {
    private String amount;

    public GetIncomeFromDatabase()
    {}

    public GetIncomeFromDatabase(String amount)
    {
        this.amount = amount;

    }
    public String getAmount()
    {
        return amount;
    }
    public void setAmount(String amount)
    {
        this.amount = amount;
    }
}
