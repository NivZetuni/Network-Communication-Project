package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.srv.Reactor;




public class ReactorMain {

    public static void main(String[] args) {
        Reactor server = new Reactor(
                Integer.decode(args[1]),   // number of working threads
                Integer.decode(args[0]),  //port
                () -> new OPMessageProtocol(),
                () -> new OPMessageEncoderDecoder());
        Database.getInstance().initialize("./Courses.txt");
        server.serve();
    }

}