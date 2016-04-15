package DataModel;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

/**
 * Created by arasz on 15.04.2016.
 */
@XmlRootElement
public class Mark
{

    final static private HashSet<Double> marksSet = new HashSet<>(Arrays.asList(2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0));

    @NotNull
    private Student student;

    private double mark;

    @NotNull
    private Date markDate;

    public Mark(Student student, double mark, Date markDate)
    {
        setStudent(student);
        setMark(mark);
        setMarkDate(markDate);
    }

    public Mark()
    {

    }

    public Student getStudent()
    {
        return student;
    }

    public void setStudent(Student student)
    {
        this.student = student;
    }

    public double getMark()
    {
        return mark;
    }

    public void setMark(double mark)
    {
        if(marksSet.contains(mark))
            this.mark = mark;
        else
            throw new IllegalArgumentException("Wrong mark");
    }

    public Date getMarkDate()
    {
        return markDate;
    }

    public void setMarkDate(Date markDate)
    {
        this.markDate = markDate;
    }
}
