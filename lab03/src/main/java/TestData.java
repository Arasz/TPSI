import DataModel.Mark;
import DataModel.Student;
import DataModel.Subject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by arasz on 15.04.2016.
 */
@Path("test")
public class TestData
{
    List<Mark> marks;
    List<Student> students;
    List<Subject> subjects;

    public TestData()
    {
        marks = new ArrayList<>();
        students = new ArrayList<>();
        subjects = new ArrayList<>();

        for(int i =0; i < 4; i++)
        {
            Student student = new Student(i, "Tadeusz"+i, "Tokarz"+i, Calendar.getInstance().getTime());
            students.add(student);
            Mark mark = new Mark(student, i+1, student.getBirthday());
            marks.add(mark);
        }

        Subject subject = new Subject("WF","Włodek", marks);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Mark> getMarks()
    {
        return marks;
    }

}
