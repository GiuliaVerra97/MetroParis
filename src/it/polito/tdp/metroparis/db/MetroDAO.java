package it.polito.tdp.metroparis.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.javadocmd.simplelatlng.LatLng;

import it.polito.tdp.metroparis.model.ConnessioneVelocita;
import it.polito.tdp.metroparis.model.Fermata;
import it.polito.tdp.metroparis.model.Linea;

public class MetroDAO {

	public List<Fermata> getAllFermate() {

		final String sql = "SELECT id_fermata, nome, coordx, coordy FROM fermata ORDER BY nome ASC";
		List<Fermata> fermate = new ArrayList<Fermata>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Fermata f = new Fermata(rs.getInt("id_Fermata"), rs.getString("nome"),
						new LatLng(rs.getDouble("coordx"), rs.getDouble("coordy")));
				fermate.add(f);
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return fermate;
	}

	
	
	
	
	
	public List<Linea> getAllLinee() {
		final String sql = "SELECT id_linea, nome, velocita, intervallo FROM linea ORDER BY nome ASC";

		List<Linea> linee = new ArrayList<Linea>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Linea f = new Linea(rs.getInt("id_linea"), rs.getString("nome"), rs.getDouble("velocita"),
						rs.getDouble("intervallo"));
				linee.add(f);
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return linee;
	}


	
	/**
	 * Metodo che mi permette di capire se esiste una connessione diretta (non ci sono stazioni in mezzo) tra la stazione di partenza e la stazione di arrivo
	 * @param partenza
	 * @param arrivo
	 * @return false se non esiste soluzione, true se invece la connessione esiste
	 */
	
	public boolean esisteConnessione(Fermata partenza, Fermata arrivo) {
		
		
		String sql="SELECT COUNT(*) AS conta " +
				"FROM connessione " + 
				"WHERE id_stazP=? AND id_stazA=?";
		
		Connection conn= DBConnect.getConnection();

		try {
		PreparedStatement st=conn.prepareStatement(sql);
		st.setInt(1, partenza.getIdFermata());
		st.setInt(2, arrivo.getIdFermata());
		
		ResultSet rs=st.executeQuery();
		
		rs.next();
		
		int numero=rs.getInt("conta");
		
		conn.close();
		
		return (numero>0);	//se il numero è 0 significa che c'è una connessione diretta e quindi il metodo ritorna true, se è maggiore di 0 significa che non c'è connessione diretta e quindi il metodo ritorna false
		
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		return false;
		
	}

	
	
	/**
	 * Metodo che permette di ricavare una lista di tutte le fermate che sono collegate direttamente (attraverso un arco solo)
	 * alla fermata di partenza
	 * @param partenza fermata
	 * @param idMap mappa di fermate identificate con un id numerico
	 * @return lista di {@link Fermata} 
	 */
	public List<Fermata> stazioneArrivo(Fermata partenza, Map<Integer, Fermata> idMap) {
		
		String sql="SELECT id_stazA " +
				"FROM connessione " + 
				"WHERE id_stazP=? ";
		
		Connection conn= DBConnect.getConnection();
		try {
			
			PreparedStatement st=conn.prepareStatement(sql);
			st.setInt(1, partenza.getIdFermata());
			ResultSet rs=st.executeQuery();
			List<Fermata> result=new ArrayList<>();
			
			while(rs.next()) {
				result.add(idMap.get(rs.getInt("id_stazA")));	//aggiungo la stazione di arrivo alla lista, grazie alla mappa che associa l'id  presente nel dataBase alla stazione presente nella mappa
			
			}
			conn.close();
			return result;
		
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		
		return null;
	}
	
	
	
	
	
	
	
	
	
	/**
	 * 
	 * @return
	 */
	public List<ConnessioneVelocita> getConnessioneVelocita(){
		String sql="SELECT connessione.id_stazP, connessione.id_stazA, MAX(linea.velocita) AS velocita " + 
				"FROM connessione, linea " + 
				"WHERE connessione.id_linea=linea.id_linea " + 
				"GROUP BY connessione.id_stazP, connessione.id_stazA ";
		
		
		
		Connection conn= DBConnect.getConnection();
		try {
			
			PreparedStatement st=conn.prepareStatement(sql);
			ResultSet rs=st.executeQuery();
			List<ConnessioneVelocita> result=new ArrayList<>();
			
			while(rs.next()) {
			ConnessioneVelocita con= new ConnessioneVelocita(rs.getInt("id_stazP"), rs.getInt("id_stazA"), rs.getDouble("velocita"));
			result.add(con);
			}
			
			conn.close();
			return result;
		
		}catch(SQLException e) {
			e.printStackTrace();
			return null;
		}
		
		
	}
		
		
		
		
		
	}
	
	
	
	

