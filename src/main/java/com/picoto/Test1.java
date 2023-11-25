package com.picoto;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Test1 {

	// cd ~/.m2/repository/com/h2database/h2/2.1.214
	// java -cp  h2-2.1.214.jar org.h2.tools.Server
	public static void main (String args[]) throws Exception {
		
		Runnable c = () -> {
			NRCDao t = new NRCDao();
			List<String> pagos = t.getPagos("12345678Z", true);
			for (String pago : pagos) {
				Utils.debug(pago);
			}
		};
		
		ScheduledExecutorService exec = Executors.newScheduledThreadPool(2);
		exec.schedule(c, 0, TimeUnit.SECONDS);
		exec.schedule(c, 1, TimeUnit.SECONDS);
		
		
		exec.awaitTermination(10, TimeUnit.SECONDS);
		exec.shutdown();

		/*
		String nrc1 = "9991234567890123456789";
		t.registrarNRC("12345678Z",nrc1, new BigDecimal("123.45"), new Date());
		t.getNRC(nrc1);
		Utils.debug("Consolidado? "+ t.isConsolidado(nrc1));
		t.consolidarNRC(nrc1);
		Utils.debug("Consolidado? "+ t.isConsolidado(nrc1));
		 */
		
		

	}
}
