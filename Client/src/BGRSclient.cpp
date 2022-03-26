#include <stdlib.h>
#include "../include/connectionHandler.h"
#include <boost/lexical_cast.hpp>
#include <boost/algorithm/string.hpp>
#include <iostream>
#include <mutex>
#include <thread>
#include <condition_variable>



/**
* This code represent the client that connect with the server
*/
using namespace std;
int encode(string& line, char* bytesArr);
void shortToBytes(short num, char* bytesArr);

class Task{
private:
    int _id;
    mutex &_mtx;
    ConnectionHandler& _connectionHandler;
    condition_variable & _conditionVariable;
    bool terminate = false;
public:
    Task (int id,mutex& mtx,ConnectionHandler& connectionHandler, condition_variable & conditionVariable) : _id(id), _mtx(mtx), _connectionHandler(connectionHandler),_conditionVariable (conditionVariable), terminate(
            false) {}

    void canTerminate(){
        terminate = true;
    }

    void run() {
        const short bufsize = 1024;
        while (!terminate) {
            char buf[bufsize];
            std::cin.getline(buf, bufsize);
            std::string line(buf);
            char *sendBuf = new char[bufsize];
            int len = encode(line, sendBuf);
            if (len==-1){
                cout<< "not valid input \n";
                continue;
            }

            if (!_connectionHandler.sendBytes(sendBuf, len)) {
                std::cout << "Disconnected. Exiting...\n" << std::endl;
                break;
            }
            unique_lock<mutex> lock{_mtx};
            if(line == "LOGOUT"){
                _conditionVariable.wait(lock);
            }
            delete[] sendBuf;
        }
    }
};

int main (int argc, char *argv[]) {
    mutex mtx;
    condition_variable  conditionVariable;
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }

    Task sendMessages(1,mtx,connectionHandler,conditionVariable);

    std::thread keyboard(&Task::run ,&sendMessages);
    int len=0;
    while(1)
    {
        std::string answer;
        if (!connectionHandler.getFrameAscii(answer, '\n')) {
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }

        len=answer.length();
        answer.resize(len-1);

        std::cout<< answer <<std::endl;
        //upon receiving a logout request, the keyboard and the main thread should terminate.
        if (answer == "ACK 4") {
            sendMessages.canTerminate();
            conditionVariable.notify_one();
            keyboard.join();
            break;
        }
        else if (answer == "ERROR 4")
            conditionVariable.notify_one();
    }

    return 0;


}

/**
 * An encoder for messages received from the keyboard.
 */
int encode (std::string& line, char* bytesArr){
    std::vector<std::string> lineVec;
    boost::split(lineVec, line, boost::is_any_of(" "));
    if (lineVec.at(0)=="ADMINREG"){
        short op = 1;
        shortToBytes(op,bytesArr);
        int counter = 2;
        int lineVec1Length = lineVec.at(1).length();
        for (int i = 0; i < lineVec1Length ; ++i) {
            bytesArr[2+ i]=lineVec.at(1).c_str()[i];
            counter++;
        }
        bytesArr[counter]= 0;
        counter++;
        int curr = counter;
        int lineVec2Length = lineVec.at(2).length();
        for (int i = 0; i < lineVec2Length; ++i) {
            bytesArr[curr+ i]=lineVec.at(2).c_str()[i];
            counter++;
        }
        bytesArr[counter]= 0;
        counter++;
        return counter;
    }
    else if (lineVec.at(0)=="STUDENTREG"){
        short op = 2;
        shortToBytes(op,bytesArr);
        int counter = 2;
        int lineVec1Length = lineVec.at(1).length();
        for (int i = 0; i < lineVec1Length ; ++i) {
            bytesArr[2+ i]=lineVec.at(1)[i];
            counter++;
        }

        bytesArr[counter]= 0;
        counter++;
        int curr = counter;
        int lineVec2Length = lineVec.at(2).length();
        for (int i = 0; i < lineVec2Length ; ++i) {
            bytesArr[curr+ i]=lineVec.at(2).c_str()[i];
            counter++;
        }
        bytesArr[counter]= 0;
        counter++;
        return counter;
    }
    else if (lineVec.at(0)=="LOGIN"){
        short op = 3;
        shortToBytes(op,bytesArr);
        int counter = 2;
        int lineVec1Length = lineVec.at(1).length();
        for (int i = 0; i < lineVec1Length ; ++i) {
            bytesArr[2+ i]=lineVec.at(1).c_str()[i];
            counter++;
        }
        bytesArr[counter]= 0;
        counter++;
        int curr = counter;
        int lineVec2Length = lineVec.at(2).length();
        for (int i = 0; i < lineVec2Length ; ++i) {
            bytesArr[curr+ i]=lineVec.at(2).c_str()[i];
            counter++;
        }
        bytesArr[counter]= 0;
        counter++;
        return counter;
    }
    else if (lineVec.at(0)=="LOGOUT"){
        short op = 4;
        shortToBytes(op,bytesArr);
        return 2;
    }
    else if (lineVec.at(0)=="COURSEREG"){
        short op = 5;
        shortToBytes(op,bytesArr);
        short courseNum =boost::lexical_cast<short>(lineVec.at(1));
        bytesArr[2] = ((courseNum >> 8) & 0xFF);
        bytesArr[3] = (courseNum & 0xFF);
        return 4;
    }
    else if (lineVec.at(0)=="KDAMCHECK"){
        short op = 6;
        shortToBytes(op,bytesArr);

        short courseNum =boost::lexical_cast<short>(lineVec.at(1));
        bytesArr[2] = ((courseNum >> 8) & 0xFF);
        bytesArr[3] = (courseNum & 0xFF);
        return 4;
    }
    else if (lineVec.at(0)=="COURSESTAT"){
        short op = 7;
        shortToBytes(op,bytesArr);
        short courseNum =boost::lexical_cast<short>(lineVec.at(1));
        bytesArr[2] = ((courseNum >> 8) & 0xFF);
        bytesArr[3] = (courseNum & 0xFF);
        return 4;
    }
    else if (lineVec.at(0)=="STUDENTSTAT"){
        short op = 8;
        shortToBytes(op,bytesArr);
        int counter = 2;
        int lineVecLength= lineVec.at(1).length();
        for (int i = 0; i < lineVecLength; ++i) {
            bytesArr[2+ i]=lineVec.at(1).c_str()[i];
            counter++;
        }
        bytesArr[counter]= 0;
        counter++;
        return counter;
    }
    else if (lineVec.at(0)=="ISREGISTERED"){
        short op = 9;
        shortToBytes(op,bytesArr);
        short courseNum =boost::lexical_cast<short>(lineVec.at(1));
        bytesArr[2] = ((courseNum >> 8) & 0xFF);
        bytesArr[3] = (courseNum & 0xFF);
        return 4;
    }
    else if (lineVec.at(0)=="UNREGISTER"){
        short op = 10;
        shortToBytes(op,bytesArr);
        short courseNum =boost::lexical_cast<short>(lineVec.at(1));
        bytesArr[2] = ((courseNum >> 8) & 0xFF);
        bytesArr[3] = (courseNum & 0xFF);
        return 4;
    }
    else if (lineVec.at(0)=="MYCOURSES"){
        short op = 11;
        shortToBytes(op,bytesArr);
        return 2;
    }
    else
        return -1;
}
void shortToBytes(short num, char* bytesArr)
{
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}



