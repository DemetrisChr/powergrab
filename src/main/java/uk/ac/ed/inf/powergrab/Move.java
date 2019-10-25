package uk.ac.ed.inf.powergrab;

public class Move {
    private Position positionBefore;
    private Direction moveDirection;
    private Position positionAfter = null;
    private Double coinsAfter = null;
    private Double powerAfter = null;
    private Drone droneToMove;
    private Station connectedStation = null;

    public Move(Drone droneToMove, Direction moveDirection) {
        this.positionBefore = droneToMove.getPosition();
        this.moveDirection = moveDirection;
        this.droneToMove = droneToMove;
    }

    public void move() {
        this.positionAfter = this.positionBefore.nextPosition(moveDirection);
        this.droneToMove.position = this.positionAfter;
        this.connectedStation = Game.getInstance().getConnectedStation(this.positionAfter);
        if (this.connectedStation != null)
            this.connectedStation.connect(droneToMove);
        droneToMove.power -= GameRules.POWER_CONSUMPTION;
        this.powerAfter = droneToMove.getPower();
        this.coinsAfter = droneToMove.getCoins();
    }

    public Station getConnectedStation() {
        return this.connectedStation;
    }

    public String toString() {
        return this.positionBefore.latitude + "," +
                this.positionBefore.longitude + "," +
                this.moveDirection + "," +
                this.positionAfter.latitude + "," +
                this.positionAfter.longitude + "," +
                this.coinsAfter + "," +
                this.powerAfter;
    }

    public Position getPositionAfter() {
        return positionAfter;
    }

    public Position getPositionBefore() {
        return positionBefore;
    }

    public double getCoinsAfter() {
        return coinsAfter;
    }

    public double getPowerAfter() {
        return powerAfter;
    }
}
