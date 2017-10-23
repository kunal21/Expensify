package kunal.expensify;

/**
 * Created by Kunal on 02-05-2017.
 */

public class GetExpenseFromDatabase {
    private String value;

    public GetExpenseFromDatabase(){}

    public GetExpenseFromDatabase(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
    public void setValue(String value)
    {
        this.value = value;
    }

}
