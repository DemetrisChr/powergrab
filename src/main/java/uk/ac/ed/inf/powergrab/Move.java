package uk.ac.ed.inf.powergrab;

public class Move {
    private Position positionBefore = null;
    private Direction moveDirection;
    private Position positionAfter = null;
    private Double coinsAfter = null;
    private Double powerAfter = null;
    private Drone droneToMove;
    private GameMap gameMap;

    public Move(Drone droneToMove, Direction moveDirection) {
        this.gameMap = droneToMove.gameMap;
        this.moveDirection = moveDirection;
        this.droneToMove = droneToMove;
    }

    public void executeMove() {
        this.positionBefore = droneToMove.getPosition();
        this.positionAfter = this.positionBefore.nextPosition(moveDirection);
        Station connectedStation = this.gameMap.getConnectedStation(this.positionAfter);
        this.droneToMove.position = this.positionAfter;
        if (connectedStation != null)
            connectedStation.connect(droneToMove);
        droneToMove.power -= GameRules.POWER_CONSUMPTION;
        this.powerAfter = droneToMove.getPower();
        this.coinsAfter = droneToMove.getCoins();
    }

    @Override
    public String toString() {
        return this.positionBefore.latitude + "," +
                this.positionBefore.longitude + "," +
                this.moveDirection + "," +
                this.positionAfter.latitude + "," +
                this.positionAfter.longitude + "," +
                this.coinsAfter + "," +
                this.powerAfter;
    }

    public Direction getMoveDirection() { return moveDirection; }

    public Position getPositionAfter() { return positionAfter; }

    public Position getPositionBefore() { return positionBefore; }
}
