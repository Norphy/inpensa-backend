package com.orphy.inpensa_backend.v1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement(order = 0)
public class InpensaBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(InpensaBackendApplication.class, args);
	}

//	@Bean
//	CommandLineRunner commandLineRunner(JdbcTemplate jdbcTemplate) {
//		return args -> {
//
//			LoggerFactory.getLogger(getClass()).info("Starting command line runner");
//			String SQL_PRINT_ALL_WALLETS = "SELECT * FROM T_WALLET";
//			String SQL_PRINT_ALL_SUB_CATEGORY = "SELECT * FROM T_SUB_CATEGORY";
//			String SQL_PRINT_ALL_CATEGORY = "SELECT * FROM T_CATEGORY";
//			String SQL_PRINT_ALL_USER = "SELECT * FROM T_USER";
//			String SQL_PRINT_ALL_TRANSACTIONS = "SELECT * FROM T_TRANSACTION";
//			LoggerFactory.getLogger(getClass()).info("PRINTING WALLETS");
//            jdbcTemplate.query(SQL_PRINT_ALL_WALLETS, printRows);
//
//			LoggerFactory.getLogger(getClass()).info("- PRINTING SUB CATEGORY");
//			jdbcTemplate.query(SQL_PRINT_ALL_SUB_CATEGORY , printRows);
//
//			LoggerFactory.getLogger(getClass()).info("- PRINTING CATEGORY");
//			jdbcTemplate.query( SQL_PRINT_ALL_CATEGORY, printRows);
//
//			LoggerFactory.getLogger(getClass()).info("- PRINTING USER");
//			jdbcTemplate.query(SQL_PRINT_ALL_USER, printRows);
//
//			LoggerFactory.getLogger(getClass()).info("- PRINTING TRANSACTION");
//			jdbcTemplate.query(SQL_PRINT_ALL_TRANSACTIONS , printRows);
//        };
//    }
//
//	RowMapper printRows = new RowMapper() {
//		@Override
//		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
//			do {
//				int count = rs.getMetaData().getColumnCount();
//				int counter = 1;
//				LoggerFactory.getLogger(getClass()).info("--- Each Row");
//				while(counter <= count) {
//					String columnName = rs.getMetaData().getColumnLabel(counter);
//					Object columnValue = rs.getObject(counter);
//					rs.getMetaData().getColumnLabel(count);
//					LoggerFactory.getLogger(getClass()).info("----- Column Name: {} and Column Value: {}", columnName, columnValue);
//					counter++;
//				}
//			} while(rs.next());
//			return null;
//		}
//	};
}
