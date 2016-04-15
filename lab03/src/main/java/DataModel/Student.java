package DataModel;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Created by arasz on 15.04.2016.
 */
@XmlRootElement
public class Student
{
    private long indexNumber;
    @NotNull
    private String name;
    @NotNull
    private String surname;
    @NotNull
    private Date birthday;


    public Student(long indexNumber, String name, String surname, Date birthday)
    {

    }

    public Student()
    {

    }

    public Date getBirthday()
    {
        return birthday;
    }

    public void setBirthday(Date birthday)
    {
        this.birthday = birthday;
    }

    public String getSurname()
    {
        return surname;
    }

    public void setSurname(String surname)
    {
        this.surname = surname;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public long getIndexNumber()
    {
        return indexNumber;
    }

    public void setIndexNumber(long indexNumber)
    {
        this.indexNumber = indexNumber;
    }
}
