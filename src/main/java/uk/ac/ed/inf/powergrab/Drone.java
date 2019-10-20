package uk.ac.ed.inf.powergrab;

import java.util.List;
import java.util.Random;

public abstract class Drone {
	protected Game game;
	protected Position position;
	protected double power = 250;
	protected double coins = 0;
	protected Random rnd = new Random();
	protected static final double POWER_CONSUMPTION = 1.25;
	
	public Drone(Position position, long randomSeed) {
		this.position = position;
		this.rnd.setSeed(randomSeed);
	}
	
	public void charge(double powerIncr) {
		this.power += powerIncr;
	}
	
	public void receiveCoins(double coinsIncr) {
		this.coins += coinsIncr;
	}
	
	public void setGame(Game game) {
		this.game = game;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public double getCoins() {
		return coins;
	}
	
	public double getPower() {
		return power;
	}
	
	public abstract List<Position> planPath();
	
	public void move(Direction direction) {
		this.position = this.position.nextPosition(direction);
	}
	
}
