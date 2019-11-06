package uk.ac.ed.inf.powergrab;

public enum Direction {
    N   (0),
    NNE (Math.PI / 8),
    NE  (2 * Math.PI / 8),
    ENE (3 * Math.PI / 8),
    E   (4 * Math.PI / 8),
    ESE (5 * Math.PI / 8),
    SE  (6 * Math.PI / 8),
    SSE (7 * Math.PI / 8),
    S   (8 * Math.PI / 8),
    SSW (9 * Math.PI / 8),
    SW  (10 * Math.PI / 8),
    WSW (11 * Math.PI / 8),
    W	(12 * Math.PI / 8),
    WNW (13 * Math.PI / 8),
    NW  (14 * Math.PI / 8),
    NNW (15 * Math.PI / 8);

    private final double angle;
    public final double sine;
    public final double cosine;

    Direction(double angle) {
        this.angle = angle;
        this.sine = Math.sin(this.angle);
        this.cosine = Math.cos(this.angle);
    }

    public double getAngle( ) {
        return angle;
    }
}
