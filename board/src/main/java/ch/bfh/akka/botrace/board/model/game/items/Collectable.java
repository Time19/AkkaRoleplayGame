package ch.bfh.akka.botrace.board.model.game.items;

import ch.bfh.akka.botrace.board.model.game.figures.Figure;

public interface Collectable {

    public boolean pickup(Figure figure);
    public void drop();
}
