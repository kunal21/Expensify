package kunal.expensify;

/**
 * Created by Kunal on 26-04-2017.
 */

public class Expense
{
    private String category,date,description,value;

    public Expense()
    {

    }

    public Expense(String category,String date,String description,String value)
    {
        this.category = category;
        this.date = date;
        this.description = description;
        this.value = value;
    }

    public String getCategory()
    {
        return category;
    }
    public void setCategory(String category)
    {
        this.category = category;
    }
    public String getDate()
    {
        return date;
    }
    public void setDate(String date)
    {
        this.date = date;
    }
    public String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
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

