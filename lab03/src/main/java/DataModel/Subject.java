package DataModel;

import DataModel.Mark;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arasz on 15.04.2016.
 */
@XmlRootElement
public class Subject
{
    @NotNull
    private String name;

    @NotNull
    private String teacher;

    @NotNull
    private List<Mark> marks = new ArrayList<>();

    public Subject(String name, String teacher, List<Mark> marks)
    {
        this.name = name;
        this.teacher = teacher;
        this.marks =marks;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getTeacher()
    {
        return teacher;
    }

    public void setTeacher(String teacher)
    {
        this.teacher = teacher;
    }

    public List<Mark> getMarks()
    {
        return marks;
    }

    public void setMarks(List<Mark> marks)
    {
        this.marks = marks;
    }
}
