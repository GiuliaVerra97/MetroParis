package it.polito.tdp.metroparis.model;

public class ConnessioneVelocita {

	
	private int stazioneP;
	private int stazioneA;
	private double velocita;
	public ConnessioneVelocita(int stazioneP, int stazioneA, double velocita) {
		super();
		this.stazioneP = stazioneP;
		this.stazioneA = stazioneA;
		this.velocita = velocita;
	}
	public int getStazioneP() {
		return stazioneP;
	}
	public void setStazioneP(int stazioneP) {
		this.stazioneP = stazioneP;
	}
	public int getStazioneA() {
		return stazioneA;
	}
	public void setStazioneA(int stazioneA) {
		this.stazioneA = stazioneA;
	}
	public double getVelocita() {
		return velocita;
	}
	public void setVelocita(double velocita) {
		this.velocita = velocita;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + stazioneA;
		result = prime * result + stazioneP;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConnessioneVelocita other = (ConnessioneVelocita) obj;
		if (stazioneA != other.stazioneA)
			return false;
		if (stazioneP != other.stazioneP)
			return false;
		return true;
	}
	
	
	
	
	
}
