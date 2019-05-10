package it.polito.tdp.metroparis.model;

import java.util.List;

public class TestModel {
	
	
	public static void main(String[] args) {

	Model m=new Model();
	m.creaGrafo();
	
	System.out.println("Il GRAFO è:\n"+m.getGrafo());
	
	System.out.format("creati %d vertici e %d archi\n", m.getGrafo().vertexSet().size(), m.getGrafo().edgeSet().size());
	
	Fermata source=m.getFermate().get(0);
	List<Fermata> raggiungibili=m.fermateRaggiungibili(source);
	System.out.print("Parto da: "+source+"\n");
	System.out.println("Fermate raggiunte: "+raggiungibili+"("+raggiungibili.size()+")");
	
	Fermata fermataFinale=m.getFermate().get(150);
	System.out.println("La stazione di arrivo è: "+fermataFinale);
	List<Fermata> percorso=m.percorsoFinoA(fermataFinale);
	System.out.println("il percorso per raggiungerla è: "+percorso);
	}
	
	
}