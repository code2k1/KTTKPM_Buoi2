package demo02.ex02;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.BasicConfigurator;

public class TestSwingReceiver extends JFrame{
	
	private static JTextArea area;
	public TestSwingReceiver() {
		setSize(400, 300);
		
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		JPanel pnMain = new JPanel();
		 area  = new JTextArea(10, 50);
		pnMain.add(area);
		this.add(pnMain);
		
	}
	public static void main(String[] args) throws NamingException, JMSException {
		new TestSwingReceiver().setVisible(true);
		BasicConfigurator.configure();
		Properties settings = new Properties();
		settings.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
		Context ctx = new InitialContext(settings);
		Object obj = ctx.lookup("ConnectionFactory");
		ConnectionFactory factory = (ConnectionFactory) obj;
		Destination destination = (Destination) ctx.lookup("dynamicQueues/thanthidet");
		Connection con = factory.createConnection("admin", "admin");
		con.start();
		Session session = con.createSession(/* transaction */false, /* ACK */Session.CLIENT_ACKNOWLEDGE);
		MessageConsumer receiver = session.createConsumer(destination);
		System.out.println("Tý was listened on queue...");
		receiver.setMessageListener(new MessageListener() {
			@Override
			public void onMessage(Message msg) {
				// msg là message nhận được
				try {
					if (msg instanceof TextMessage) {
						TextMessage tm = (TextMessage) msg;
						String txt = tm.getText();
						String temp = area.getText()+txt;
						area.setText(temp);
						msg.acknowledge();// gửi tín hiệu ack
					} else if (msg instanceof ObjectMessage) {
						ObjectMessage om = (ObjectMessage) msg;
//						System.out.println(om);
					}
					// others message type....
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
