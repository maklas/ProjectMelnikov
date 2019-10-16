package ru.maklas.melnikov.user_interface;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTable;

public class CornerStage extends BaseStage {

    private final VisTable mainTable;
    public final Cell topLeft;
    public final Cell topRight;
    public final Cell bottomLeft;
    public final Cell bottomRight;

    public CornerStage() {
        mainTable = new VisTable();
        addActor(mainTable);
        mainTable.setFillParent(true);

        topLeft = mainTable.add().expand().align(Align.topLeft);
        topRight = mainTable.add().expand().align(Align.topRight);
        mainTable.row();
        bottomLeft = mainTable.add().expand().align(Align.bottomLeft);
        bottomRight = mainTable.add().expand().align(Align.bottomRight);
    }

    public void setTopLeft(Actor a) {
        topLeft.setActor(a);
    }

    public void setTopRight(Actor a) {
        topRight.setActor(a);
    }

    public void setBottomLeft(Actor a) {
        bottomLeft.setActor(a);
    }

    public void setBottomRight(Actor a) {
        bottomRight.setActor(a);
    }

}
