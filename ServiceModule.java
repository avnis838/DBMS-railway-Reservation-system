
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.sql.*;

class QueryRunner implements Runnable {

    protected Socket socketConnection;

    public QueryRunner(Socket clientSocket) {
        this.socketConnection = clientSocket;
    }

    public static void bookTicket(Connection con, String query) throws SQLException {
        // String user = "postgres";
        // String password = "Avnish@2020";
        // String url = "jdbc:postgresql://localhost:5432/railway";
        // con = DriverManager.getConnection(url, user, password);
        // if (con != null) {
        // System.out.println("Connection successfull");
        // } else {
        // System.out.println("Connection failed!");
        // }

        String s = "insert into ticket values (?, ?, ?, ?, ?)";
        String[] str = query.split(" ");
        int total_passenger = Integer.parseInt(str[0]);
        int t = total_passenger + 1;
        int trainNo = Integer.parseInt(str[t]);
        t++;
        String doj = str[t];
        t++;
        String coch = str[t];
        String newQry = "select * from train where train_no = ? and doj = ?";
        PreparedStatement prstmt = con.prepareStatement(newQry);
        prstmt.setInt(1, trainNo);
        prstmt.setString(2, doj);
        ResultSet set = prstmt.executeQuery();
        if (set.next()) {
            String stmt = "SELECT uuid_generate_v1()";
            Statement stm = con.createStatement();
            ResultSet result = stm.executeQuery(stmt);
            result.next();
            String pnr = result.getString(1);
            // System.out.println(pnr);

            System.out.println("Coach = " + coch);
            if (coch.equals("AC")) {
                int available = set.getInt(5);
                System.out.println("Total no of AC Seats present = " + available);
                if (available >= total_passenger) {
                    int j = 1;
                    PreparedStatement p3 = con.prepareStatement(s);
                    p3.setString(1, pnr);
                    p3.setInt(2, total_passenger);
                    p3.setInt(3, trainNo);
                    p3.setString(4, doj);
                    p3.setString(5, coch);
                    p3.executeUpdate();
                    System.out.println("Total " + total_passenger + " tickets booked Successfully!!");
                    for (int i = 0; i < total_passenger; i++) {
                        newQry = "select * from train where train_no = ? and doj = ?";
                        prstmt = con.prepareStatement(newQry);
                        prstmt.setInt(1, trainNo);
                        prstmt.setString(2, doj);
                        set = prstmt.executeQuery();
                        set.next();
                        available = set.getInt(5);
                        String qry = "insert into passenger values(?, ?, ?)";
                        if (i != total_passenger - 1)
                            str[j] = str[j].substring(0, str[j].length() - 1);
                        p3 = con.prepareStatement(qry);
                        p3.setString(1, pnr);
                        p3.setString(2, str[j]);
                        int total = set.getInt(3);
                        int rem = set.getInt(5);
                        int coach_no = (total - rem) / 18 + 1;
                        int seat_no = (total - rem) % 18 + 1;
                        String seatID = "A" + (coach_no) + "-" + (seat_no);
                        if (seat_no % 6 == 1 || seat_no % 6 == 2) {
                            seatID += "-LB";
                        } else if (seat_no % 6 == 3 || seat_no % 6 == 4) {
                            seatID += "-UB";
                        } else if (seat_no % 6 == 5) {
                            seatID += "-SL";
                        } else
                            seatID += "-SU";
                        System.out.println("Pnr = " + pnr + " Name = " + str[j] + " Coach_no = " + coach_no
                                + " Seat_no = " + seat_no + " SeatID = " + seatID);
                        p3.setString(3, seatID);
                        j++;
                        p3.executeUpdate();
                        String q2 = "update train set remac = ? where train_no = ? and doj = ?";
                        PreparedStatement p4 = con.prepareStatement(q2);
                        p4.setInt(1, available - 1);
                        p4.setInt(2, trainNo);
                        p4.setString(3, doj);
                        p4.executeUpdate();
                    }
                    String newStr = "select * from ticket, passenger where ticket.pnr = ? and passenger.pnr = ticket.pnr";
                    PreparedStatement newStmt = con.prepareStatement(newStr);
                    newStmt.setString(1, pnr);
                    ResultSet newSet = newStmt.executeQuery();
                    while (newSet.next()) {
                        String str1 = "insert into ticketinfo values(?, ?, ?, ?, ?, ?)";
                        PreparedStatement p4 = con.prepareStatement(str1);
                        p4.setString(1, newSet.getString(1));
                        p4.setString(2, newSet.getString(7));
                        p4.setString(3, newSet.getString(5));
                        p4.setString(4, newSet.getString(8));
                        p4.setInt(5, newSet.getInt(3));
                        p4.setString(6, newSet.getString(4));
                        p4.executeUpdate();
                    }

                } else {
                    System.out.println("OOps " + available + " ticket are not available.");
                }
            } else {
                int available = set.getInt(6);
                System.out.println("Total no of SL Seats present = " + available);
                if (available >= total_passenger) {
                    int j = 1;
                    PreparedStatement p3 = con.prepareStatement(s);
                    p3.setString(1, pnr);
                    p3.setInt(2, total_passenger);
                    p3.setInt(3, trainNo);
                    p3.setString(4, doj);
                    p3.setString(5, coch);
                    p3.executeUpdate();
                    System.out.println("Total " + total_passenger + " tickets booked Successfully!!");
                    for (int i = 0; i < total_passenger; i++) {
                        newQry = "select * from train where train_no = ? and doj = ?";
                        prstmt = con.prepareStatement(newQry);
                        prstmt.setInt(1, trainNo);
                        prstmt.setString(2, doj);
                        set = prstmt.executeQuery();
                        set.next();
                        available = set.getInt(6);
                        String qry = "insert into passenger values(?, ?, ?)";
                        if (i != total_passenger - 1)
                            str[j] = str[j].substring(0, str[j].length() - 1);
                        p3 = con.prepareStatement(qry);
                        p3.setString(1, pnr);
                        p3.setString(2, str[j]);
                        int total = set.getInt(4);
                        int rem = set.getInt(6);
                        int coach_no = (total - rem) / 24 + 1;
                        int seat_no = (total - rem) % 24 + 1;
                        String seatID = "S" + (coach_no) + "-" + (seat_no);
                        if (seat_no % 8 == 1 || seat_no % 8 == 4) {
                            seatID += "-LB";
                        } else if (seat_no % 8 == 2 || seat_no % 8 == 5) {
                            seatID += "-MB";
                        } else if (seat_no % 8 == 3 || seat_no % 8 == 6) {
                            seatID += "-UB";
                        } else if (seat_no % 8 == 7) {
                            seatID += "-SL";
                        } else
                            seatID += "-SU";
                        System.out.println("Pnr = " + pnr + " Name = " + str[j] + " Coach_no = " + coach_no
                                + " Seat_no = " + seat_no + " SeatID = " + seatID);
                        p3.setString(3, seatID);
                        j++;
                        p3.executeUpdate();
                        String q2 = "update train set remsleeper = ? where train_no = ? and doj = ?";
                        PreparedStatement p4 = con.prepareStatement(q2);
                        p4.setInt(1, available - 1);
                        p4.setInt(2, trainNo);
                        p4.setString(3, doj);
                        p4.executeUpdate();
                    }
                    String newStr = "select * from ticket, passenger where ticket.pnr = ? and passenger.pnr = ticket.pnr";
                    PreparedStatement newStmt = con.prepareStatement(newStr);
                    newStmt.setString(1, pnr);
                    ResultSet newSet = newStmt.executeQuery();
                    while (newSet.next()) {
                        String str1 = "insert into ticketinfo values(?, ?, ?, ?, ?, ?)";
                        PreparedStatement p4 = con.prepareStatement(str1);
                        p4.setString(1, newSet.getString(1));
                        p4.setString(2, newSet.getString(7));
                        p4.setString(3, newSet.getString(5));
                        p4.setString(4, newSet.getString(8));
                        p4.setInt(5, newSet.getInt(3));
                        p4.setString(6, newSet.getString(4));
                        p4.executeUpdate();
                    }

                } else {
                    System.out.println("OOps " + available + " ticket are unavailables.");
                }
            }
        }
    }

    public void run() {
        try {
            // Reading data from client
            InputStreamReader inputStream = new InputStreamReader(socketConnection
                    .getInputStream());
            BufferedReader bufferedInput = new BufferedReader(inputStream);
            OutputStreamWriter outputStream = new OutputStreamWriter(socketConnection
                    .getOutputStream());
            BufferedWriter bufferedOutput = new BufferedWriter(outputStream);
            PrintWriter printWriter = new PrintWriter(bufferedOutput, true);
            String clientCommand = "";
            String responseQuery = "";
            // Read client query from the socket endpoint
            clientCommand = bufferedInput.readLine();
            while (!clientCommand.equals("#")) {

                System.out.println("Recieved data <" + clientCommand + "> from client : "
                        + socketConnection.getRemoteSocketAddress().toString());

                /*******************************************
                 * Your DB code goes here
                 ********************************************/
                while (true) {
                    try {
                        String user = "postgres";
                        String password = "Avnish@2020";
                        String url = "jdbc:postgresql://localhost:5432/railway";
                        Connection con = DriverManager.getConnection(url, user, password);
                        if (con != null) {
                            System.out.println("Connection successfull");
                        } else {
                            System.out.println("Connection failed!");
                        }
                        con.setTransactionIsolation(8);
                        con.setAutoCommit(false);
                        bookTicket(con, clientCommand);
                        con.commit();
                        con.close();
                        break;
                    } catch (SQLException exception) {
                        System.out.println("Client : " + exception.getMessage());
                    }
                }
                // Dummy response send to client
                responseQuery = "******* Dummy result ******";
                // Sending data back to the client
                printWriter.println(responseQuery);
                // Read next client query
                clientCommand = bufferedInput.readLine();
            }
            inputStream.close();
            bufferedInput.close();
            outputStream.close();
            bufferedOutput.close();
            printWriter.close();
            socketConnection.close();
        } catch (IOException e) {
            return;
        }
    }
}

// Main Class to controll the program flow

public class ServiceModule {
    // Server listens to port
    static int serverPort = 7008;
    // Max no of parallel requests the server can process
    static int numServerCores = 5;

    // ------------ Main----------------------
    public Connection connect() throws SQLException {
        String user = "postgres";
        String password = "Avnish@2020";
        String url = "jdbc:postgresql://localhost:5432/railway";
        Connection connection = DriverManager.getConnection(url, user, password);
        if (connection != null) {
            System.out.println("Connection successfull");
        } else {
            System.out.println("Connection failed!");
        }
        return connection;
    }

    public static void main(String[] args) throws IOException {
        // Creating a thread pool
        ExecutorService executorService = Executors.newFixedThreadPool(numServerCores);
        Connection con = null;
        try (// Creating a server socket to listen for clients
                ServerSocket serverSocket = new ServerSocket(serverPort)) {
            Socket socketConnection = null;
            ServiceModule App = new ServiceModule();
            try {
                con = App.connect();

                String func = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'train'";
                Statement st = con.createStatement();
                ResultSet res = st.executeQuery(func);
                if (res.next()) {

                    func = "drop table train";
                    st = con.createStatement();
                    st.executeUpdate(func);
                }
                func = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'passenger'";
                st = con.createStatement();
                res = st.executeQuery(func);
                if (res.next()) {

                    func = "drop table passenger";
                    st = con.createStatement();
                    st.executeUpdate(func);
                }
                func = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'ticket'";
                st = con.createStatement();
                res = st.executeQuery(func);
                if (res.next()) {

                    func = "drop table ticket";
                    st = con.createStatement();
                    st.executeUpdate(func);
                }
                func = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'ticketinfo'";
                st = con.createStatement();
                res = st.executeQuery(func);
                if (res.next()) {
                    func = "drop table ticketinfo";
                    st = con.createStatement();
                    st.executeUpdate(func);
                }
                String s = "Create Table Train(train_no integer not null, doj varchar(10) not null, ac integer, sleeper integer, remac integer, remsleeper integer, PRIMARY KEY (train_no, doj))";
                Statement stmt = con.createStatement();
                stmt.executeUpdate(s);
                System.out.println("Admin Table created");
                s = "Create table Ticket(pnr varchar primary key, totalpassenger integer not null, train_no integer not null, doj varchar(10) not null, coach varchar(2) not null)";
                stmt = con.createStatement();
                stmt.executeUpdate(s);
                System.out.println("User Table Created");
                s = "Create table passenger(pnr varchar not null, nameofpassenger varchar(20) not null, seat varchar(10) not null, foreign key (pnr) References Ticket(pnr))";
                stmt = con.createStatement();
                stmt.executeUpdate(s);
                s = "Create table ticketInfo(pnr varchar not null, name varchar(20) not null, coach varchar(2) not null, seatID varchar(10) not null, train_no integer not null, doj varchar(10) not null)";
                stmt = con.createStatement();
                stmt.executeUpdate(s);
                s = "CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\"";
                stmt = con.createStatement();
                stmt.executeUpdate(s);
                // schedule input
                try {
                    File schedule_file = new File("Input/Trainschedule.txt");
                    Scanner sc = new Scanner(schedule_file);
                    while (sc.hasNextLine()) {
                        String[] schedule = (sc.nextLine()).split("\\s+");
                        if (schedule.length < 4) {
                            System.out.println("out");
                            break;
                        }

                        s = "insert into train values(?, ?, ?, ?, ?, ?)";
                        PreparedStatement prstmt = con.prepareStatement(s);
                        prstmt.setInt(1, Integer.parseInt(schedule[0]));
                        prstmt.setString(2, schedule[1]);
                        prstmt.setInt(3, Integer.parseInt(schedule[2]) * 18);
                        prstmt.setInt(4, Integer.parseInt(schedule[3]) * 24);
                        prstmt.setInt(5, Integer.parseInt(schedule[2]) * 18);
                        prstmt.setInt(6, Integer.parseInt(schedule[3]) * 24);
                        prstmt.executeUpdate();
                    }
                    sc.close();
                } catch (Exception e) {
                    System.out.println("Schedule Exception");
                    System.out.println(e.getMessage());
                }

            } catch (SQLException exception) {

            }

            // Always-ON server
            while (true) {
                System.out.println("Listening port : " + serverPort
                        + "\nWaiting for clients...");
                socketConnection = serverSocket.accept(); // Accept a connection from a client
                System.out.println("Accepted client :"
                        + socketConnection.getRemoteSocketAddress().toString()
                        + "\n");
                // Create a runnable task
                Runnable runnableTask = new QueryRunner(socketConnection);
                // Submit task for execution
                executorService.submit(runnableTask);
            }
        }
    }
}
