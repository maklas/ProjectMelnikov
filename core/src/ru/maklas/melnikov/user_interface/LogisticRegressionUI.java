package ru.maklas.melnikov.user_interface;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.widget.VisCheckBox;

public class LogisticRegressionUI extends CornerStage {

	public final VisCheckBox enableCoordinates;
	public final VisCheckBox enableFunctions;
	public final VisCheckBox enableGradient;
	public final VisCheckBox enableFullGradient;

	public LogisticRegressionUI() {
		super();
		enableCoordinates = new VisCheckBox("Система координат", true);
		enableCoordinates.getStyle().fontColor = Color.BLACK;
		enableFunctions = new VisCheckBox("Функции", true);
		enableGradient = new VisCheckBox("Град. предсказаний", true);
		enableFullGradient = new VisCheckBox("Весь Град.", false);

		Table table = new Table();

		table.add(enableCoordinates).left();
		table.row();
		table.add(enableFunctions).left();
		table.row();
		table.add(enableGradient).left();
		table.row();
		table.add(enableFullGradient).left();
		table.row();

		setBottomLeft(table);
	}
}
