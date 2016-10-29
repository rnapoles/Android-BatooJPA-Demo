package org.batoo.jpa.android;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;

import org.batoo.common.log.BLogger;
import org.batoo.common.log.BLoggerFactory;
import org.batoo.jpa.android.model.Address;
import org.batoo.jpa.android.model.Country;
import org.batoo.jpa.android.model.Person;
import org.batoo.jpa.android.model.Phone;
import org.batoo.jpa.BJPASettings;
import org.batoo.jpa.core.BatooPersistenceProvider;
import org.batoo.jpa.JPASettings;
import org.batoo.jpa.jdbc.DDLMode;
import org.h2.Driver;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


import android.util.Log;

public class HelloAndroid extends Activity {

	private static final BLogger LOGGER = BLoggerFactory.getLogger(BasicDataSource.class);
	private StringBuilder log;
	private long start;
	private Country country;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		ScrollView sv = new ScrollView(this);
		TextView tv = new TextView(this);
		tv.setHorizontallyScrolling(true);
		tv.setVerticalScrollBarEnabled(true);
	
		log = new StringBuilder();

		/*
		try {
			Class.forName("org.h2.Driver");
			Connection conn = DriverManager.getConnection("jdbc:h2:/data/data/org.batoo.jpa.android/data/testdb;FILE_LOCK=FS", "sa", "");
			// add application code here
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("CREATE TABLE COFFEES " +
					   "(COF_NAME VARCHAR(32), SUP_ID INTEGER, PRICE FLOAT, " +
					   "SALES INTEGER, TOTAL INTEGER)");

			stmt.executeUpdate("INSERT INTO COFFEES " +
	                   "VALUES ('Colombian', 101, 7.99, 0, 0)");

			String query = "SELECT COF_NAME, PRICE FROM COFFEES";
			ResultSet rs = stmt.executeQuery(query);

			
			while (rs.next()) {
			   String s = rs.getString("COF_NAME");
			   sb.append(s+"\n");
			   float n = rs.getFloat("PRICE");
			   System.out.println(s + "   " + n);
			}			


			conn.close();			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		try {
			LOGGER.error("Init HelloAndroid");
		} catch(Exception e){
			e.printStackTrace();
			log.append(e.getMessage());
		}
		
        this.doTest();
		tv.setText(log.toString());
		sv.addView(tv);
		
		setContentView(sv);
	}
	
	private void doTest() {
		
		Map<String, String> properties = Maps.newHashMap();
		properties.put(JPASettings.JDBC_DRIVER, Driver.class.getName());
		properties.put(JPASettings.JDBC_URL, "jdbc:h2:/data/data/org.batoo.jpa.android/data/hello;FILE_LOCK=FS");
		properties.put(JPASettings.JDBC_USER, "sa");
		properties.put(JPASettings.JDBC_PASSWORD, "");
		properties.put(BJPASettings.DDL, DDLMode.DROP.name());
		properties.put(BJPASettings.DATASOURCE_POOL,BasicDataSource.class.getName() );
		
		final EntityManagerFactory emf = new BatooPersistenceProvider().createEntityManagerFactory("batoo", properties , new String[]{
				Address.class.getName(), //
				Country.class.getName(), //
				Person.class.getName(), //
				Phone.class.getName()
		});

		EntityManager em = null;
		try {
			em = emf.createEntityManager();
		} catch(Exception e){
			//System.err.println(e.getMessage());
			e.printStackTrace();
			Log.e("JPAErr",e.getClass().getName());
			return;
		}
		this.country = new Country();

		this.country.setName("Turkey");
		em.getTransaction().begin();
		em.persist(this.country);
		em.getTransaction().commit();

        List<Person> persons = new ArrayList<>();

        for (int i = 0; i < 10; i++) {

            for (int j = 0; j < 10; j++) {
                final Person person = new Person();

                person.setName("Hasan " + i + " " + j);

                final Address address = new Address();
                address.setCity("Istanbul " + i + " " + j);
                address.setPerson(person);
                address.setCountry(this.country);
                person.getAddresses().add(address);

                final Address address2 = new Address();
                address2.setCity("Istanbul " + (i + 1) + " " + j);
                address2.setPerson(person);
                address2.setCountry(this.country);
                person.getAddresses().add(address2);

                final Phone phone = new Phone();
                phone.setPhoneNo("111 222-3344-" + i + " " + j);
                phone.setPerson(person);
                person.getPhones().add(phone);

                final Phone phone2 = new Phone();
                phone2.setPhoneNo("111 222-3344-" + i + " " + j);
                phone2.setPerson(person);
                person.getPhones().add(phone2);

               persons.add(person);


            }

        }

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        for (final Person person : persons) {
            em.persist(person);
        }
        tx.commit();

        final TypedQuery<Person> q = em.createQuery("select p from Person p", Person.class);

        List<Person> resultList = q.getResultList();
        for(Person p:resultList){
            //System.err.println("Address :"+a.getCity());
            log.append(p.getName()+"\n");
        }

		em.close();

	}




}