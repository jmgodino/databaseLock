package com.picoto;

import java.sql.SQLException;
import java.util.List;

public class Test1 {

	// cd ~/.m2/repository/com/h2database/h2/2.1.214
	// java -cp  h2-2.1.214.jar org.h2.tools.Server
	public static void main (String args[]) throws SQLException {
		NRCDao t = new NRCDao();

		/*
		String nrc1 = "9991234567890123456789";
		t.registrarNRC("12345678Z",nrc1, new BigDecimal("123.45"), new Date());
		t.getNRC(nrc1);
		Utils.debug("Consolidado? "+ t.isConsolidado(nrc1));
		t.consolidarNRC(nrc1);
		Utils.debug("Consolidado? "+ t.isConsolidado(nrc1));
		 */
		
		List<String> pagos = t.getPagos("12345678Z", true);
		for (String pago : pagos) {
			Utils.debug(pago);
		}

	}
}
