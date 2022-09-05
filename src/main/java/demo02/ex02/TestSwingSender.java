package demo02.ex02;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Properties;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.BasicConfigurator;
import data.Person;
import helper.XMLConvert;

public class TestSwingSender extends JFrame implements ActionListener {

	private static Connection con;
	private static Destination destination;
	private JButton btnSubmit;
	private JTextField txtName;
	private JTextField txtPass;

	public TestSwingSender() {
		setSize(400, 300);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		FlowLayout fl = new FlowLayout(FlowLayout.LEFT, 10, 5);
		JPanel pnMain = new JPanel(fl);
		JPanel flow1 = new JPanel();
		JPanel flow2 = new JPanel();
		JPanel flow3 = new JPanel();
		JLabel lblName = new JLabel("MSSV: ");
		lblName.setPreferredSize(new Dimension(60, 30));
		txtName = new JTextField(20);
		JLabel lblPass = new JLabel("Họ tên: ");
		lblPass.setPreferredSize(new Dimension(60, 30));
		txtPass = new JTextField(20);

		btnSubmit = new JButton("Submit");

		flow1.add(lblName);
		flow1.add(txtName);
		flow2.add(lblPass);
		flow2.add(txtPass);
		pnMain.add(flow1);
		pnMain.add(flow2);
		pnMain.add(btnSubmit);
		this.add(pnMain);
		btnSubmit.addActionListener(this);
	}

	public static void main(String[] args) throws Exception {
		new TestSwingSender().setVisible(true);
		BasicConfigurator.configure();
		// config environment for JNDI
		Properties settings = new Properties();
		settings.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
		// create context
		Context ctx = new InitialContext(settings);
		// lookup JMS connection factory
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		// lookup destination. (If not exist-->ActiveMQ create once)
		destination = (Destination) ctx.lookup("dynamicQueues/thanthidet");
		// get connection using credential
		con = factory.createConnection("admin", "admin");
		// connect to MOM
		con.start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if (o.equals(btnSubmit)) {
			try {
				// config environment for JMS

				// create session
				Session session = con.createSession(/* transaction */false, /* ACK */Session.AUTO_ACKNOWLEDGE);
				// create producer
				MessageProducer producer = session.createProducer(destination);
				// create text message
				int mssv = Integer.parseInt(txtName.getText());
				String hoTen = txtPass.getText();

				Person p = new Person(mssv, hoTen, new Date());
			
				String xml = new XMLConvert<Person>(p).object2XML(p);
				Message msg = session.createTextMessage(xml);
//				Message msg = session.createObjectMessage(xml);
				producer.send(msg);
				// shutdown connection
				session.close();
				System.out.println("Finished...");

			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

	}
}
