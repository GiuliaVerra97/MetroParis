package it.polito.tdp.metroparis.model;

import java.util.*;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model{
	
	
	private Graph<Fermata, DefaultWeightedEdge>  grafo; //defaultedge se non è pesato, defaultWeightEdge è pesato
	private List<Fermata> fermate;						//lista vertici
	private Map<Integer, Fermata> fermateIdMap;		
	private Map<Fermata, Fermata> backVisit;	

	
	
	
	
	public void creaGrafo() {
		
		grafo=new SimpleDirectedGraph<>(DefaultWeightedEdge.class);		//grafo semplice, orientato con peso
		
		//aggiungere vertici
		MetroDAO dao=new MetroDAO();
		this.fermate=dao.getAllFermate();
		Graphs.addAllVertices(this.grafo,this.fermate);
		
		//crea idMap, una mappa che contiene tutte le fermate della lista del database
		this.fermateIdMap=new HashMap<Integer, Fermata>();
		for(Fermata f:this.fermate) {
			fermateIdMap.put(f.getIdFermata(), f);
		}
		

		//aggiungi archi
		for (Fermata partenza: this.grafo.vertexSet()) {
			
			List<Fermata> arrivi=dao.stazioneArrivo(partenza, fermateIdMap);
			
			for(Fermata arrivo: arrivi) {
				this.grafo.addEdge(partenza, arrivo);	
			}
			
		}
			
		
		List<ConnessioneVelocita> archiPesati=dao.getConnessioneVelocita();
		for(ConnessioneVelocita con : archiPesati) {
			Fermata partenza=fermateIdMap.get(con.getStazioneP());
			Fermata arrivo=fermateIdMap.get(con.getStazioneA());
			double distanza=LatLngTool.distance(partenza.getCoords(), arrivo.getCoords(), LengthUnit.KILOMETER);
			double peso=distanza/con.getVelocita()*3600;
			Graphs.addEdgeWithVertices(grafo, partenza, arrivo, peso);
			//oppure si può scrivere:	grafo.setEdgeWeight(partenza, arrivo, peso);
			
		}
		
		
		
	}

	
	
	
	
	
	/**
	 * Capire quale siano le fermate che si devono passare prima di raggiungere quella finale, partendo dalla fermata di partenza
	 * @param source fermata di partenza
	 * @return lista delle {@link fermate} percorse per arrivare a quella finale
	 */
	public List<Fermata> fermateRaggiungibili(Fermata source){
		
		List<Fermata> result=new ArrayList<Fermata>();
		backVisit=new HashMap<>();
		
		GraphIterator<Fermata, DefaultWeightedEdge> iteratore=new BreadthFirstIterator<Fermata, DefaultWeightedEdge>(this.grafo, source); //mi dice su quale grafo intersre, se non specifico il vertice di partenza ne sceglie uno di default
		
		iteratore.addTraversalListener(new EdgeTraversedListner(grafo, backVisit));
				
		backVisit.put(source, null);		//definisco l'origine sorgente, tutti i nodi hanno un padre eccetto la radice che non ne ha e quindi metto null
		
		while(iteratore.hasNext()) {
			result.add(iteratore.next());		//next() è un metodo che restituisce l'elemento e avanza in quello successivo
		}
				
		return result;
		
		
	}


	
	/**
	 * Metodo che permette di trovare tutte le fermate che si devono attraversare prima di arrivare alla fermata passata come parametro
	 * @param fermataFinale
	 * @return lista di {@link fermata}
	 */
	
	public List<Fermata> percorsoFinoA(Fermata fermataFinale){
		
		//cerco nella mappa la fermata finale, se c'è  significa che la fermata è raggiungibile
		//se la mappa non contiene la fermata finale vuol dire che la fermata finale non è raggiungibile dalla source
		if(!backVisit.containsKey(fermataFinale)) {
		return null;
		}
		
		List<Fermata> percorso=new LinkedList<>();
		
		Fermata f=fermataFinale;
		
		while(f!=null) {
			percorso.add(0, f); //questo metodo permette di inserire gli elementi all'interno della lista nella posizione indicata: il primo elemento, aggiungendo altri elementi, si sposta fino ad occupare la posizione finale 
			f=backVisit.get(f);		//prende poi il padre e così via a ritroso;
		}
		
		return percorso;
	}
	

	


	public Graph<Fermata, DefaultWeightedEdge> getGrafo() {
		return grafo;
	}
	
	
	public List<Fermata> getFermate() {
		MetroDAO dao=new MetroDAO();
		return dao.getAllFermate();
	}

	
	
	
	/**
	 * Metodo utile per trovare il cammino minimo tra una fermata di partenza e una di arrivo
	 * @param partenza 
	 * @param arrivo
	 * @return lista di {@link fermata}
	 */
	public List<Fermata> trovaCamminoMinimo(Fermata partenza, Fermata arrivo){
		this.creaGrafo();
		DijkstraShortestPath<Fermata, DefaultWeightedEdge> dj=new DijkstraShortestPath<Fermata, DefaultWeightedEdge>(this.grafo);
		GraphPath<Fermata, DefaultWeightedEdge> path=dj.getPath(partenza, arrivo);
		return path.getVertexList();
	}
	
	

	
}
