/*
 *CLASSE INTERNA
  classe creata solo per essere usata nella funzione del model fermataRaggiungibili, quindi in realtà invece di creare una nuova pag 
  di classe pubblica posso crearla privatamente all'interno della classe model. 
  Così sarà private e il model avrà accesso diretto a tutte le sue variabili senza l'uso del get
   
   id.addTraversalListener(new Model.EdgeTraveredLinster());
   
   CLASSE USA E GETTA
   se devo usare una classe una sola volta e poi nn mi serve più potrei anche definire una classe senza nome
   a volte può essere utile creare una classe anonima, senza nome, usa e getta
   in questo caso nel model si crea un oggetto che implementa l'interfaccia
   it.addTraversalLinsterner(new TraversalLinstener<Fremata, DefaultEdge>());
   e poi si aggiungono i metodi di quella classe anonima che implementa l'interfaccia
   
   
 */


package it.polito.tdp.metroparis.model;

import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.DefaultEdge;

public class EdgeTraversedListner implements TraversalListener<Fermata, DefaultEdge>{

	
	Map<Fermata, Fermata> back;		//mappa che contiene gli archi: come chiave la fermata figlia e come valore la fermata padre
	Graph<Fermata, DefaultEdge> grafo;
	
	
	
	public EdgeTraversedListner( Graph<Fermata, DefaultEdge> grafo, Map<Fermata, Fermata> back) {
		super();
		this.back = back;
		this.grafo=grafo;
	}

	@Override
	public void connectedComponentFinished(ConnectedComponentTraversalEvent arg0) {		
	}

	@Override
	public void connectedComponentStarted(ConnectedComponentTraversalEvent arg0) {		
	}

	@Override
	public void vertexFinished(VertexTraversalEvent<Fermata> arg0) {		
	}

	@Override
	public void vertexTraversed(VertexTraversalEvent<Fermata> arg0) {		
	}

	

	@Override
	public void edgeTraversed(EdgeTraversalEvent<DefaultEdge> ev) { 	//ev evento
		/* back codifica relazioni del tipo child-parent
		 * Per ogni nuovo vertice child scoperto è necessario che:
		 * 	-child sia ancora sconosciuto(non ancora visitato)
		 * 	-parent sia già stato visitato
		*/
		Fermata sourceVertex=grafo.getEdgeSource(ev.getEdge());		//per avere la fermata del padre
		Fermata targetVertex=grafo.getEdgeTarget(ev.getEdge());		//per avere la fermata del figlio
		
		/*se il grafo è orientato, allora source=parent, target==child
		  se il grafo non è orientato, potrebbe anche essere al contrario*/
		
		
		//se il figlio non è ancora nella mappa, ma il padre si, si aggiunge il figli nella mappa che avrà come chiave la fermata figlio e come valore la fermata padre
		if(!back.containsKey(targetVertex) && back.containsKey(sourceVertex)) {
			back.put(targetVertex, sourceVertex);
		}else if(!back.containsKey(sourceVertex) && back.containsKey(targetVertex)) {	//se il grafo non è orientato
			back.put(sourceVertex, targetVertex);
		}
		
		
		//back.put(ev.getEdge().destinationVertex(), ev.getEdge().sourceVertex());
		
	}
	
	
	
	

}
