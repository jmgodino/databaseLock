package com.picoto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Test1 {

	// Ejecuar h2sql en modo server
	// cd ~/.m2/repository/com/h2database/h2/2.1.214; java -cp  h2-2.1.214.jar org.h2.tools.Server
	public static void main (String args[]) throws Exception {

		// Preparar modelo de datos
		NRCDao t = new NRCDao(NRCDao.MODO.H2SQL);
		t.destruir();
		t.preparar();
		String nrc1 = "9991234567890123456789";
		t.registrarNRC("12345678Z",nrc1, new BigDecimal("123.45"), new Date());
		t.getNRC(nrc1);
		Utils.debug("Consolidado? "+ t.isConsolidado(nrc1));
		t.consolidarNRC(nrc1);
		Utils.debug("Consolidado? "+ t.isConsolidado(nrc1));

		// Preparamos proceso concurrente
		Callable<Void> c = () -> {
			NRCDao tc = new NRCDao(NRCDao.MODO.H2SQL);
			List<String> pagos = tc.getPagos("12345678Z", true, 3);
			for (String pago : pagos) {
				Utils.debug(pago);
			}
			return null;
		};
		
		// Programamos ejecución paralela
		ScheduledExecutorService exec = Executors.newScheduledThreadPool(2);
		exec.schedule(c, 0, TimeUnit.SECONDS);
		exec.schedule(c, 0, TimeUnit.SECONDS);
		
		
		exec.awaitTermination(10, TimeUnit.SECONDS);
		exec.shutdown();

	}
}
