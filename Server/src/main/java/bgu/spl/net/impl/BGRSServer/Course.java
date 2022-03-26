package bgu.spl.net.impl.BGRSServer;

import java.util.Vector;

/**
 * Class that represents all the information about a course.
 */

public class Course {
    private String courseName;
    private int maxStudents;
    private Integer currStudents;
    private Vector<Integer> kdamCourses;
    private Vector<String> studsReg;

    public Course (String courseName, int maxStudents,Vector<Integer> kdamCourses, Vector<String> studsReg )
    {
        this.courseName=courseName;
        this.maxStudents=maxStudents;
        this.currStudents=0;
        this.kdamCourses=kdamCourses;
        this.studsReg=studsReg;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getMaxStudents() {
        return maxStudents;
    }

    public Integer getCurrStudents() {
        return currStudents;
    }

    public Vector<Integer> getKdamCourses() {
        return kdamCourses;
    }

    public void regStudent ()
    {
        currStudents++;
    }
    public void unRegStudent ()
    {
        currStudents--;
    }

    public Vector<String> getStudsReg() {
        return studsReg;
    }
}
