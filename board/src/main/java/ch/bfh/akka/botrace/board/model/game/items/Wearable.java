package ch.bfh.akka.botrace.board.model.game.items;

public interface Wearable extends Collectable{

    public boolean wear();
    public void unwear();
}