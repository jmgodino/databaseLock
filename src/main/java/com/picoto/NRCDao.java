package com.picoto;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NRCDao {

	Connection con;

	public void destruir() throws SQLException {
		getConnection();

		String sql = "drop table tpv_nrcs if exists";

		Statement statement = con.createStatement();

		statement.execute(sql);

		Utils.debug("Tabla NRC eliminada");

		closeConnection(statement);

	}

	public void preparar() throws SQLException {

		getConnection();

		String sql = "create table tpv_nrcs (nrc varchar(22) primary key, nif varchar(9), importe decimal(13,2), fecha timestamp, estado integer)";

		Statement statement = con.createStatement();

		statement.execute(sql);

		Utils.debug("Tabla NRC creada");

		closeConnection(statement);

	}

	public void registrarNRC(String nif, String nrc, BigDecimal importe, Date fecha) {

		try {
			getConnection();
			String sql = "insert into tpv_nrcs (nif, nrc, importe, fecha, estado) values (?, ?, ?, ?, ?)";
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setString(1, nif);
			statement.setString(2, nrc);
			statement.setBigDecimal(3, importe);
			statement.setTimestamp(4, new java.sql.Timestamp(fecha.getTime()));
			statement.setInt(5, 1);
			int rows = statement.executeUpdate();
			if (rows > 0) {
				Utils.debug(String.format("Registro creado para NRC %s", nrc));
			}
			closeConnection(statement);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(String.format("No se ha podido registrar el NRC para %s", nrc));
		}

	}

	public void consolidarNRC(String nrc) {

		try {
			getConnection();
			String sql = "update tpv_nrcs set estado = 2 where nrc = ?";
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setString(1, nrc);
			int rows = statement.executeUpdate();
			if (rows > 0) {
				Utils.debug(String.format("***** Registro consolidado para NRC %s *****", nrc));
			}
			closeConnection(statement);

		} catch (Exception e) {
			throw new DAOException(String.format("No se ha podido consolidar el NRC para %s", nrc));
		}

	}

	public String getNRC(String nrc) {
		try {
			getConnection();
			String sql = "select nrc from tpv_nrcs where nrc = ?";
			String token = null;
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setString(1, nrc);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				token = rs.getString(1);
				Utils.debug(String.format("NRC encontrado: %s", token));
			}
			rs.close();
			statement.close();
			closeConnection(statement); // finally
			if (token == null) {
				throw new DAOException(String.format("No se ha encontrado el NRC para %s", nrc));
			} else {
				return token;
			}
		} catch (Exception e) {
			throw new DAOException(String.format("No se ha podido recuperar el token para %s", nrc));
		}
	}

	public List<String> getPagos(String nif, boolean bloquear) {
		try {
			Utils.debug("Comienza acceso a tabla para consulta");
			getConnection();
			List<String> lista = new ArrayList<String>();
			String sql = "select nrc, importe, fecha from tpv_nrcs where nif = ? order by fecha "
					+ ((bloquear) ? "for update" : "");
			PreparedStatement statement = con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_UPDATABLE);
			statement.setString(1, nif);
			ResultSet rs = statement.executeQuery();
			Utils.debug(sql);
			while (rs.next()) {
				lista.add("NRC: " + rs.getString(1) + " Importe: " + rs.getBigDecimal(2) + " " + rs.getTimestamp(3));
				if (bloquear) {
					Utils.debug("Registro leido con timestamp: "+rs.getTimestamp(3));
					Timestamp ts = new Timestamp(new Date().getTime());
					rs.updateTimestamp("fecha", ts);
					rs.updateRow();
					Utils.debug("Cursor modificado: "+ts);
				}
			}
			rs.close();
			statement.close();

			if (bloquear) {
				Utils.debug("Esperando...");
				Thread.sleep(8000);
				Utils.debug("Espera terminada");
			}
			
			closeConnection(statement); // finally

			return lista;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(String.format("No se ha podido recuperar la lista de NRCs para el NIF %s", nif));
		}
	}

	public boolean isConsolidado(String nrc) {
		try {
			getConnection();
			String sql = "select estado from tpv_nrcs where nrc = ?";
			int estado = 1;
			PreparedStatement statement = con.prepareStatement(sql);
			statement.setString(1, nrc);
			ResultSet rs = statement.executeQuery();
			if (rs.next()) {
				estado = rs.getInt(1);
				Utils.debug(String.format("NRC encontrado para consultar su estado: %s", nrc));
			}
			rs.close();
			statement.close();
			closeConnection(statement);
			if (estado == 1) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			throw new DAOException(String.format("No se ha podido recuperar el token para %s", nrc));
		}
	}

	private void getConnection() throws SQLException {
		//String jdbcURL = "jdbc:h2:./test";
		//String jdbcURL = "jdbc:h2:tcp://localhost/~/test";
		String jdbcURL = "jdbc:postgresql://localhost/postgres";

		//con = DriverManager.getConnection(jdbcURL, "jmgodino","???");
		con = DriverManager.getConnection(jdbcURL, "postgres","???");
		
		
		con.setAutoCommit(false);
	}

	private void closeConnection(Statement statement) throws SQLException {
		statement.close();
		con.commit();
		Utils.debug("Commit realizado. ¿Estado bloqueos?");
		con.close();
	}

}
