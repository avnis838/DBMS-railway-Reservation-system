import java.sql.*;
import java.util.*;  
import java.io.*;

public class Irctc {

	public static void main(String []args) {
		try {
			  Class.forName("org.postgresql.Driver");
			  Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/railway","postgres","11082002.iitropar.1081");
			  String func = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'train'";
			  Statement st = con.createStatement();
			  ResultSet res = st.executeQuery(func);
			  if(res.next()) {
				 
				  func = "drop table train";
				  st = con.createStatement();
				  st.executeUpdate(func);
			  }
			  func = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'passenger'";
			  st = con.createStatement();
			  res = st.executeQuery(func);
			  if(res.next()) {
				
				  func = "drop table passenger";
				  st = con.createStatement();
				  st.executeUpdate(func);
			  }
			  func = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'ticket'";
			  st = con.createStatement();
			  res = st.executeQuery(func);
			  if(res.next()) {
				  
				  func = "drop table ticket";
				  st = con.createStatement();
				  st.executeUpdate(func);
			  }
			  func = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'ticketinfo'";
			  st = con.createStatement();
			  res = st.executeQuery(func);
			  if(res.next()) {
				  func = "drop table ticketinfo";
				  st = con.createStatement();
				  st.executeUpdate(func);
			  }
			  String s = "Create Table Train(train_no integer not null, doj varchar(10) not null, ac integer, sleeper integer, remac integer, remsleeper integer, PRIMARY KEY (train_no, doj))";
			  Statement stmt = con.createStatement();
			  stmt.executeUpdate(s);
			  System.out.println("Admin Table created");
			  s = "Create table Ticket(pnr integer primary key, totalpassenger integer not null, train_no integer not null, doj varchar(10) not null, coach varchar(2) not null)";
			  stmt = con.createStatement();
			  stmt.executeUpdate(s);
			  System.out.println("User Table Created");
			  s = "Create table passenger(pnr integer not null, nameofpassenger varchar(20) not null, seat varchar(10) not null, foreign key (pnr) References Ticket(pnr))";
			  stmt = con.createStatement();
			  stmt.executeUpdate(s);
			  s = "Create table ticketInfo(pnr integer not null, name varchar(20) not null, coach varchar(2) not null, seatID varchar(10) not null, train_no integer not null, doj varchar(10) not null)";
			  stmt = con.createStatement();
			  stmt.executeUpdate(s);
			  Scanner sc= new Scanner(System.in);
			  int k=1;
			  int pnr = 1;
			  while(k!=0) {
				  System.out.println();
				  System.out.println("1. Continue as Admin.");
				  System.out.println("2. Continue as User.");
				  System.out.println("Enter your choice = ");
				  int a= sc.nextInt();  
				  if(a==1) {
					  while(true) {
						  System.out.println();
						  System.out.println("1. Schedule a Train.");
						  System.out.println("2. Remove Train.");
						  System.out.println("3. Exit.");
						  System.out.println("Enter Your Choice = ");
						  int b = sc.nextInt();
						  if(b==1){
							  BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
							  System.out.println("Enter Train No., Date of journey, AC coaches, and Sleeper coaches for the trains you want to schedule. ");
							  String[] ans;
							  int x =0;
							  System.out.println("How many trains you want to add??");
							  int temp = sc.nextInt();
							  
							  while(temp!=0) {
								  s = "insert into train values(?, ?, ?, ?, ?, ?)";
								  ans = br.readLine().split(" ");
									  int number = Integer.parseInt(ans[0]);
									  String tarikh = ans[1];
									  int ac = Integer.parseInt(ans[2]);
									  int sleeper = Integer.parseInt(ans[3]);
									  PreparedStatement pstmt = con.prepareStatement(s);
									  pstmt.setInt(1, number);
									  pstmt.setString(2, tarikh);
									  pstmt.setInt(3, ac);
									  pstmt.setInt(4, sleeper);
									  pstmt.setInt(5, ac);
									  pstmt.setInt(6, sleeper);
									  pstmt.executeUpdate();
									  temp--;
								  x++;
							  }
							  System.out.println(x+" Trains added successfully!");
							  System.out.println();
						  }
						  else if(b==2) {
							  String[] str;
							  s = "delete from train where train_no = ? and doj = ?";
							  BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
					          System.out.println("Enter Train no. and Date of journey of the train.");
					          str = br.readLine().split(" ");
					          int number = Integer.parseInt(str[0]);
					          String tarikh = str[1];
					          PreparedStatement pstmt = con.prepareStatement(s);
					          pstmt.setInt(1, number);
					          pstmt.setString(2, tarikh);
					          pstmt.executeUpdate();
					          System.out.println("Train deleted Successfully!!");
					          System.out.println();
						  }
						  else if(b==3)
							  break;
						  else {
							  System.out.println("Thank You! But there is no choice that you selected. ");
						  }
					  }
					  }
					  else if(a==2) {
						  String[] str;
						  BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
						  System.out.println("Enter No. of passengers, name of all passengers, train-no, Date of journey, Choice of coach.");
						  int temp = 1;
						  while(temp!=0) {
							  s = "insert into ticket values (?, ?, ?, ?, ?)";
							  str = br.readLine().split(" ");
							  if(str[0].equals("#")) {
								  break;
							  }
							  int totalPassengers = Integer.parseInt(str[0]);
							  int t = totalPassengers+1;
							  int trainNo = Integer.parseInt(str[t]); t++;
							  String doj = str[t]; t++;
							  String coch = str[t];
							  String newQry = "select * from train where train_no = ? and doj = ?";
							  PreparedStatement pstmt = con.prepareStatement(newQry);
							  pstmt.setInt(1, trainNo);
							  pstmt.setString(2, doj);
							  ResultSet set = pstmt.executeQuery();
							  if(set.next()) {
								  System.out.println("Coach = "+coch);
								  if(coch.equals("AC")) {
									  int available = set.getInt(5);
									  System.out.println("Total ac availability = "+available);
									  if(available>=totalPassengers) {
										  int j=1;
										  PreparedStatement p3 = con.prepareStatement(s);
										  p3.setInt(1, pnr);
										  p3.setInt(2, totalPassengers);
										  p3.setInt(3, trainNo);
										  p3.setString(4, doj);
										  p3.setString(5, coch);
										  p3.executeUpdate();
										  System.out.println("You have booked "+totalPassengers+" tickets Successfully!!");
										  for(int i=0; i<totalPassengers; i++) {
											  newQry = "select * from train where train_no = ? and doj = ?";
											  pstmt = con.prepareStatement(newQry);
											  pstmt.setInt(1, trainNo);
											  pstmt.setString(2, doj);
											  set = pstmt.executeQuery();
											  set.next();
											  available = set.getInt(5);
											  String qry = "insert into passenger values(?, ?, ?)";
											  if(i!=totalPassengers-1)
												  str[j] = str[j].substring(0, str[j].length()-1);
											  p3 = con.prepareStatement(qry);
											  p3.setInt(1, pnr);
											  p3.setString(2, str[j]);
											  int total = set.getInt(3);
											  int rem = set.getInt(5);
											  int coach_no = (total-rem)/18+1;
											  int seat_no = (total-rem)%18+1;
											  String seatID = "A"+(coach_no)+"-"+(seat_no);
											  if(seat_no%6==1 || seat_no%6==2) {
												  seatID+="-LB";
											  }
											  else if(seat_no%6==3 || seat_no%6==4) {
												  seatID+="-UB";
											  }
											  else if(seat_no%6==5){
												  seatID+="-SL";
											  }
											  else
												  seatID+="-SU";
											  System.out.println("Pnr = "+pnr+" Name = "+str[j]+ " Coach_no = "+coach_no + " Seat_no = "+seat_no+" SeatID = "+seatID);
											  p3.setString(3, seatID);
											  j++;
											  p3.executeUpdate();
											  String q2 = "update train set remac = ? where train_no = ? and doj = ?";
											  PreparedStatement p4 = con.prepareStatement(q2);
											  p4.setInt(1, available-1);
											  p4.setInt(2, trainNo);
											  p4.setString(3, doj);
											  p4.executeUpdate();
										  }
										  String newStr = "select * from ticket, passenger where ticket.pnr = ? and passenger.pnr = ticket.pnr";
										  PreparedStatement newStmt = con.prepareStatement(newStr);
										  newStmt.setInt(1, pnr);
										  ResultSet newSet = newStmt.executeQuery();
										    while(newSet.next()) {
											  String str1 = "insert into ticketinfo values(?, ?, ?, ?, ?, ?)";
											  PreparedStatement p4 = con.prepareStatement(str1);
											  p4.setInt(1, newSet.getInt(1));
											  p4.setString(2, newSet.getString(7));
											  p4.setString(3, newSet.getString(5));
											  p4.setString(4, newSet.getString(8));
											  p4.setInt(5, newSet.getInt(3));
											  p4.setString(6, newSet.getString(4));
											  p4.executeUpdate();
										  }
										  pnr++;
										 
									  }
									  else {
										  System.out.println("Thanks for visiting!! But there are only "+available+" ticket availables.");
									  }
								  }
								  else {
									  int available = set.getInt(6);
									  System.out.println("Total sl availability = "+available);
									  if(available>=totalPassengers) {
										  int j=1;
										  PreparedStatement p3 = con.prepareStatement(s);
										  p3.setInt(1, pnr);
										  p3.setInt(2, totalPassengers);
										  p3.setInt(3, trainNo);
										  p3.setString(4, doj);
										  p3.setString(5, coch);
										  p3.executeUpdate();
										  System.out.println("You have booked "+totalPassengers+" tickets Successfully!!");
										  for(int i=0; i<totalPassengers; i++) {
											  newQry = "select * from train where train_no = ? and doj = ?";
											  pstmt = con.prepareStatement(newQry);
											  pstmt.setInt(1, trainNo);
											  pstmt.setString(2, doj);
											  set = pstmt.executeQuery();
											  set.next();
											  available = set.getInt(6);
											  String qry = "insert into passenger values(?, ?, ?)";
											  if(i!=totalPassengers-1)
												  str[j] = str[j].substring(0, str[j].length()-1);
											  p3 = con.prepareStatement(qry);
											  p3.setInt(1, pnr);
											  p3.setString(2, str[j]);
											  int total = set.getInt(4);
											  int rem = set.getInt(6);
											  int coach_no = (total-rem)/24+1;
											  int seat_no = (total-rem)%24+1;
											  String seatID = "S"+(coach_no)+"-"+(seat_no);
											  if(seat_no%8==1 || seat_no%8==4) {
												  seatID+="-LB";
											  }
											  else if(seat_no%8==2 || seat_no%8==5) {
												  seatID+="-MB";
											  }
											  else if(seat_no%8==3 || seat_no%8==6) {
												  seatID+="-UB";
											  }
											  else if(seat_no%8==7){
												  seatID+="-SL";
											  }
											  else
												  seatID+="-SU";
											  System.out.println("Pnr = "+pnr+" Name = "+str[j]+ " Coach_no = "+coach_no + " Seat_no = "+seat_no+" SeatID = "+seatID);
											  p3.setString(3, seatID);
											  j++;
											  p3.executeUpdate();
											  String q2 = "update train set remsleeper = ? where train_no = ? and doj = ?";
											  PreparedStatement p4 = con.prepareStatement(q2);
											  p4.setInt(1, available-1);
											  p4.setInt(2, trainNo);
											  p4.setString(3, doj);
											  p4.executeUpdate();
										  }
										  String newStr = "select * from ticket, passenger where ticket.pnr = ? and passenger.pnr = ticket.pnr";
										  PreparedStatement newStmt = con.prepareStatement(newStr);
										  newStmt.setInt(1, pnr);
										  ResultSet newSet = newStmt.executeQuery();
										    while(newSet.next()) {
											  String str1 = "insert into ticketinfo values(?, ?, ?, ?, ?, ?)";
											  PreparedStatement p4 = con.prepareStatement(str1);
											  p4.setInt(1, newSet.getInt(1));
											  p4.setString(2, newSet.getString(7));
											  p4.setString(3, newSet.getString(5));
											  p4.setString(4, newSet.getString(8));
											  p4.setInt(5, newSet.getInt(3));
											  p4.setString(6, newSet.getString(4));
											  p4.executeUpdate();
										  }
										  pnr++;
										 
									  }
									  else {
										  System.out.println("Thanks for visiting!! But there are only "+available+" ticket availables.");
									  }
								  }
							  }
							 
						  }
						  
					  }
					  else {
						  System.out.println("There is no other choice. Thank You!");
					  }
				  System.out.println("If you want to exit then press 0, otherwise press any number to continue.");
				  k = sc.nextInt();
			  }
			 
			  con.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}