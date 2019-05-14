package it.polito.tdp.metroparis.model;

import java.util.*;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import it.polito.tdp.metroparis.db.MetroDAO;

public class Model{
	
	
	
	//per creare una classe interna all'interno del model devo devo:
	
	
	
	//crea il grafo e lo gestisce utilizzando i metodi opportuni
	//per realizzare il grafo è necessario prendere dal DB le fermate che saranno i vertici del grafo
	
	private Graph<Fermata, DefaultEdge>  grafo; //defaultedge se non è pesato
	private List<Fermata> fermate;		//lista vertici
	private Map<Integer, Fermata> fermateIdMap;		
	private Map<Fermata, Fermata> backVisit;	

	
	public void creaGrafo() {
		
		grafo=new SimpleDirectedGraph<>(DefaultEdge.class);		//grafo semplice, orientato
		
		//aggiungere vertici
		MetroDAO dao=new MetroDAO();
		this.fermate=dao.getAllFermate();
		Graphs.addAllVertices(this.grafo,this.fermate);
		
		//crea idMap, una mappa che contiene tutte le fermate della lista del database
		this.fermateIdMap=new HashMap<Integer, Fermata>();
		for(Fermata f:this.fermate) {
			fermateIdMap.put(f.getIdFermata(), f);
		}
		

		//aggiungere archi-ci possono essere 3 metodi:
		
		/*1- posso prendere tutte le combinazioni delle fermate e vedere se esiste una connessione tra le due
			 non va bene perchè ci impiega troppo tempo
			 
		for(Fermata partenza:this.grafo.vertexSet()) {
			for(Fermata arrivo:this.grafo.vertexSet()) {
				if(dao.esisteConnessione(partenza, arrivo)) {
					this.grafo.addEdge(partenza, arrivo);
				}
				
			}
		}*/
			
		//2- per ogni stazine di partenza prendo una stazione a lei connessa direttamente(fermata di arrivo) e genero la connessione
		for (Fermata partenza: this.grafo.vertexSet()) {
			
			List<Fermata> arrivi=dao.stazioneArrivo(partenza, fermateIdMap);
			
			for(Fermata arrivo: arrivi) {
				this.grafo.addEdge(partenza, arrivo);	
			}
			
		}
			
		
		//3- metodo dalla tabella connessione c'è già una colonna che indica la partenza e una l'arrivo, se non ci fosse già una tabella devo "cercare l'info da altre tabelle e le connetto tra loro"
		
	}

	
	
	
	
	
	/**
	 * Capire quale siano le fermate che si devono passare prima di raggiungere quella finale, partendo dalla fermata di partenza
	 * @param source fermata di partenza
	 * @return lista delle {@link fermate} percorse per arrivare a quella finale
	 */
	public List<Fermata> fermateRaggiungibili(Fermata source){
		
		this.creaGrafo();
		
		List<Fermata> result=new ArrayList<Fermata>();
		backVisit=new HashMap<>();
		
		GraphIterator<Fermata, DefaultEdge> iteratore=new BreadthFirstIterator<Fermata, DefaultEdge>(this.grafo, source); //mi dice su quale grafo intersre, se non specifico il vertice di partenza ne sceglie uno di default
		
		iteratore.addTraversalListener(new EdgeTraversedListner(grafo, backVisit));
		
		//ogni interatore mi serve per una sola iterazione, per farne un'altra devo creare un nuovo interatore
		
		backVisit.put(source, null);		//definisco l'origine sorgente, tutti i nodi hanno un padre eccetto la radice che non ne ha e quindi metto null
		
		while(iteratore.hasNext()) {
			result.add(iteratore.next());		//next() è un metodo che restituisce l'elemento e avanza in quello successivo
		}
		
		//System.out.println(backVisit);
		
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
	

	


	public Graph<Fermata, DefaultEdge> getGrafo() {
		return grafo;
	}

	
	public List<Fermata> getFermate() {
		MetroDAO dao=new MetroDAO();
		return dao.getAllFermate();
	}



	
}
