/*
 * Copyright (c) 2012-2013, Batu Alp Ceylan
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */

package org.batoo.jpa.android;

import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import org.batoo.common.log.BLogger;
import org.batoo.common.log.BLoggerFactory;
import org.batoo.jpa.JPASettings;
import org.batoo.jpa.jdbc.datasource.AbstractInternalDataSource;


/**
 * Basic datasource wrapper 
 *
 * @author Reinier Nápoles Martínez
 */
public class BasicDataSource extends AbstractInternalDataSource {

    private static final BLogger LOGGER = BLoggerFactory.getLogger(BasicDataSource.class);
    public static final String CONFIG_PREFIX = "org.batoo.basiccp.";

    public BasicDataSource() {
        System.err.println("init BasicDataSource");
    }

    @Override
    public void open(String persistenceUnitName, Map<String, Object> props) {
        try {
            LOGGER.debug("Configuring BasicDataSource");
            //setWrappedDataSource(new HikariDataSource(loadConfiguration(mapProperties)));

            Properties cpProps = cropPrefixFromProperties(props, CONFIG_PREFIX);

            if (props.containsKey(JPASettings.JDBC_DRIVER)) {
                cpProps.setProperty("driverClassName", (String) props.get(JPASettings.JDBC_DRIVER));
            }
            if (props.containsKey(JPASettings.JDBC_URL)) {
                cpProps.setProperty("jdbcUrl", (String) props.get(JPASettings.JDBC_URL));
            }
            if (props.containsKey(JPASettings.JDBC_USER)) {
                cpProps.setProperty("username", (String) props.get(JPASettings.JDBC_USER));
            }
            if (props.containsKey(JPASettings.JDBC_PASSWORD)) {
                cpProps.setProperty("password", (String) props.get(JPASettings.JDBC_PASSWORD));
            }

            setWrappedDataSource(new DriverDataSource(cpProps));
            

        } catch (Exception e) {
            LOGGER.warn("Cannot configure BasicDataSource cause: ", e);
        }
        LOGGER.debug("BasicDataSource Configured");
    }

    @Override
    public void close() {
        try {
            this.getWrappedDataSource().getConnection().close();
        } catch (SQLException ex) {
            LOGGER.warn(ex.getMessage());
            
        }
        setWrappedDataSource(null);
    }


}