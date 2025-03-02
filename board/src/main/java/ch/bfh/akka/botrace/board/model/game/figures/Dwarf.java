package ch.bfh.akka.botrace.board.model.game.figures;

public class Dwarf extends Figure implements Attacks, Defends {

    public Dwarf(String name, int carryStrength, int healthPoints) {
        super(name, carryStrength, healthPoints, 1.0);
    }

}