package bgu.spl.net.impl.BGRSServer;

import java.util.Collections;

/**
 *  Class that represents Admin request to get the status of a specific course message actions.
 */

public class OP7PrintCourseStatMessage implements OPMessage {
    private int opcode;
    private int courseNum ;
    private String loggedInUser;
    public OP7PrintCourseStatMessage(int opCode, int courseNum) {
        this.opcode=opCode;
        this.courseNum=courseNum;
        this.loggedInUser=null;
    }

    @Override
    public OPMessage react(String s) {
        this.loggedInUser = s;
        Database database = Database.getInstance();
        if (loggedInUser == null) {
            return new OP13ErrMessage(13, (short) 7);
        }
        if (!(database.getUsersInfo().get(loggedInUser).isAdmin())){
            return new OP13ErrMessage(13, (short) 7);
        }
        if (!(database.getCoursesInfo().containsKey(courseNum))){
            return new OP13ErrMessage(13, (short) 7);
        }
        synchronized (database.getCoursesInfo().get(courseNum)) {
            Course c = database.getCoursesInfo().get(courseNum);
            Collections.sort(c.getStudsReg());
            String studReg = "[";
            for  (int j =0; j<c.getStudsReg().size() ; j++) {
                    studReg=studReg+c.getStudsReg().get(j) +",";
                }
            if (studReg.length()==1){
                String stat = "Course: (" + courseNum + ") " + c.getCourseName() + "\n"
                        + "Seats Available: " +(c.getMaxStudents()-c.getCurrStudents()) + "/"
                        + c.getMaxStudents() + "\n" + "Students Registered: " + "[]";
                return new OP12AckMessage(12, (short) 7, stat);
            }
            studReg=studReg.substring(0,studReg.length()-1);
            studReg=studReg+"]";
            String stat = "Course: (" + courseNum + ") " + c.getCourseName() + "\n"
                    + "Seats Available: " +(c.getMaxStudents()-c.getCurrStudents()) + "/"
                    + c.getMaxStudents() + "\n" + "Students Registered: " + studReg;
            return new OP12AckMessage(12, (short) 7, stat);
        }
    }

    @Override
    public int getOpCode() {
        return opcode;
    }

    @Override
    public String getLoggedInUser() {
        return loggedInUser;
    }
}
