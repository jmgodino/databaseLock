package com.picoto;

import java.sql.SQLException;
import java.util.List;

public class Test2 {

	public static void main (String args[]) throws SQLException {
		NRCDao t = new NRCDao();

		List<String> pagos = t.getPagos("12345678Z", true);
		for (String pago : pagos) {
			Utils.debug(pago);
		}
		Utils.debug("Terminado");
	}
}
