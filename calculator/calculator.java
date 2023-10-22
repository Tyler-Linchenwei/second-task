
package calculator;

import java.sql.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.*;

public class calculator extends JFrame implements ActionListener {
	// North component:
	private JPanel jp_north = new JPanel();
	private JTextField input_text = new JTextField();
	private JButton c_Button = new JButton("C");
	// The USER ID and PASSPORT:
	static final String USER = "lindashuai";
	static final String PASS = "abcd";
	// Central component:
	private JPanel jp_centre = new JPanel();
	String content = "";
	static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	static final String DB = "db";
	static final String DB_URL = "jdbc:mysql://localhost:3306/" + DB
			+ "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

	// Written by LinChenWei
	// Date:2023.10.21

	public calculator() throws HeadlessException {
		this.init();
		this.addNorthComponent();
		this.addCentreButton();
	}

	// Doing initialization:
	public void init() {
		this.setTitle(Const.TITLE);
		this.setSize(Const.FRAME_H, Const.FRAME_W);
		this.setLayout(new BorderLayout());
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	// Adding north component:
	public void addNorthComponent() {
		// Setting basic attribute of this calculator.
		this.input_text.setPreferredSize(new Dimension(300, 50));
		jp_north.add(input_text);
		this.c_Button.setForeground(Color.black);
		jp_north.add(c_Button);
		c_Button.setFont(new Font("    ", Font.BOLD, 16));
		c_Button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				input_text.setText("");
				content = "";
			}
		});
		this.add(jp_north, BorderLayout.NORTH);
	}

	// Adding central component:
	public void addCentreButton() {
		String[] txt = { "1", "2", "3", "+", "ans", "history", "4", "5", "6", "-", "log", "sin", "7", "8", "9", "*",
				"^", "cos", "0", ".", "=", "/", "%", "tan" };
		String reg = "[\\+\\-\\*\\/\\.\\=\\^\\%]";
		this.jp_centre.setLayout(new GridLayout(4, 6));
		for (int i = 0; i < 24; i++) {
			String temp = txt[i];
			JButton button = new JButton();
			button.setText(temp);
			if (temp.matches(reg)) {
				button.setFont(new Font("    ", Font.BOLD, 18));
				button.setForeground(Color.BLACK);
			} else {
				button.setFont(new Font("    ", Font.BOLD, 18));
				button.setForeground(Color.GRAY);
			}
			button.addActionListener(this);
			jp_centre.add(button);
		}
		this.add(jp_centre, BorderLayout.CENTER);
	}

	// Main function:
	public static void main(String[] args) {
		calculator calculator = new calculator();
		calculator.setVisible(true);
	}

	private String preInput = null;
	private String operator = null;

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String clickStr = e.getActionCommand();
		if (".0123456789".indexOf(clickStr) != -1) {
			// if enter the numbers on the calculator
			this.input_text.setText(input_text.getText() + clickStr);
			this.input_text.setHorizontalAlignment(JTextField.RIGHT);
			// Record the input into a string.
			content += clickStr;
		} else if (clickStr.matches("[\\+\\-\\*\\/\\^\\%]") || clickStr.matches("log") || clickStr.matches("sin")
				|| clickStr.matches("cos") || clickStr.matches("tan")) {
			// if click the operator.
			operator = clickStr;
			preInput = this.input_text.getText();
			this.input_text.setText("");
			// Record the operator into the string.
			content += clickStr;
		} else if (clickStr.equals("=")) {
			// if the clicked button is "=";
			content += clickStr;
			Double preValue = 0.0;
			Double latValue = 0.0;
			if (preInput.matches("-?\\d+(\\.\\d+)?")) {
				preValue = Double.valueOf(preInput);
			}
			if (this.input_text.getText().matches("-?\\d+(\\.\\d+)?")) {
				latValue = Double.valueOf(this.input_text.getText());
			}
			Double result = null;
			boolean flag = true;
			String error = null;
			switch (operator) {
			// doing calculation:
			case "+":
				result = preValue + latValue;
				break;
			case "-":
				result = preValue - latValue;
				break;
			case "*":
				result = preValue * latValue;
				break;
			case "/":
				if (latValue != 0) {
					result = preValue / latValue;
					break;
				} else {
					flag = false;
					error = "Invalid Operation!";
					break;
				}
			case "^":
				result = Math.pow(preValue, latValue);
				break;
			case "%":
				if (latValue != 0) {
					result = preValue % latValue;
					break;
				} else {
					flag = false;
					error = "Invalid Operation!";
					break;
				}
			case "log":
				result = Math.log(preValue) / Math.log(latValue);
				break;
			case "sin":
				BigDecimal SINresult = new BigDecimal(Math.sin(Math.toRadians(latValue)));
				result = SINresult.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				break;
			case "cos":
				BigDecimal COSresult = new BigDecimal(Math.cos(Math.toRadians(latValue)));
				result = COSresult.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				break;
			case "tan":
				BigDecimal TANresult = new BigDecimal(Math.tan(Math.toRadians(latValue)));
				result = TANresult.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				break;
			}
			content += result;

			// Display the result into the given column.
			if (flag)
				this.input_text.setText(result.toString());
			else
				this.input_text.setText(error);

			try {
				Class.forName(JDBC_DRIVER);
				Connection conn = DriverManager.getConnection(DB_URL, USER, PASS); // construct the connection to MySQL
				// database
				System.out.print(conn.isClosed());
				String histContent = "'" + content + "'"; // storing the previous operation
				int countNum = 1;
				String mySql = ""; // initialize the MySql sentence
				Statement statement = conn.createStatement();
				mySql = "update tb set CONTENT = " + result + " where ID = " + countNum;
				statement.executeUpdate(mySql); // execute MySql sentence to update the latest result on first row in
				// database
				countNum++;
				mySql = "select ID from tb where ID = " + countNum; // execute MySql sentence to select the result from
																	// second row in database
				ResultSet rs = statement.executeQuery(mySql);
				while (rs.next()) {
					histContent = rs.getString("CONTENT");
					countNum++;
				}
				if (countNum > 10) {
					mySql = "delete from tb where ID = 1";
					statement.executeUpdate(mySql); // execute MySql sentence to delete the oldest result from database
				}
				mySql = "insert into tb values(" + countNum + "," + histContent + ")";
				statement.executeUpdate(mySql); // execute MySql sentence to insert the latest result into database
				rs.close();
				statement.close();
				conn.close();
			} catch (SQLException se) {
				// Handle errors for JDBC
				se.printStackTrace();
			} catch (Exception ex) {
				// Handle errors for Class.forName
				ex.printStackTrace();
			}
		} else if (clickStr.equals("ans")) {
			// if the clicked button is "ans"
			try {
				Class.forName(JDBC_DRIVER);
				Connection conn = DriverManager.getConnection(DB_URL, USER, PASS); // construct the connection to MySQL
				// database
				System.out.print(conn.isClosed());
				String histContent = null; // storing the previous operation
				int countNum = 1;
				String mySql = ""; // initialize the MySql sentence
				Statement statement = conn.createStatement();
				mySql = "select ID, CONTENT from tb where ID = (select max(ID) from tb)";
				ResultSet rs = statement.executeQuery(mySql);
				while (rs.next()) {
					countNum = rs.getInt("ID");
					histContent = rs.getString("CONTENT");
				}
				rs.close();
				statement.close();
				conn.close();
				if (histContent != null) {
					this.input_text.setText(histContent);
					preInput = histContent;
				}
			} catch (SQLException se) {
				// Handle errors for JDBC
				se.printStackTrace();
			} catch (Exception ex) {
				// Handle errors for Class.forName
				ex.printStackTrace();
			}
		} else if (clickStr.equals("history")) {
			// if the clicked button is "history"
			try {
				Class.forName(JDBC_DRIVER);
				Connection conn = DriverManager.getConnection(DB_URL, USER, PASS); // construct the connection to MySQL
				// database
				System.out.print(conn.isClosed());
				String histContent = ""; // storing the previous operation
				int countNum = 1;
				String mySql = ""; // initialize the MySql sentence
				Statement statement = conn.createStatement();
				mySql = "select ID, CONTENT from tb";
				ResultSet rs = statement.executeQuery(mySql);
				while (rs.next()) {
					int id = rs.getInt("ID");
					String content = rs.getString("CONTENT");
					histContent += id + ": " + content + "\n";
				}
				rs.close();
				statement.close();
				conn.close();
				JOptionPane.showMessageDialog(null, histContent, "History", JOptionPane.INFORMATION_MESSAGE);
			} catch (SQLException se) {
				// Handle errors for JDBC
				se.printStackTrace();
			} catch (Exception ex) {
				// Handle errors for Class.forName
				ex.printStackTrace();
			}
		}
	}
}