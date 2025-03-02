package ch.bfh.akka.botrace.board.model.game.figures;

public class Human extends Figure implements Attacks, Defends {


    public Human(String name, int carryStrength, int healthPoints) {
        super(name, carryStrength, healthPoints,1.0);
    }

}