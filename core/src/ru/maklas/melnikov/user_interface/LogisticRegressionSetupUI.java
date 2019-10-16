package ru.maklas.melnikov.user_interface;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.widget.*;
import ru.maklas.melnikov.states.Parameters;
import ru.maklas.melnikov.utils.StringUtils;

public class LogisticRegressionSetupUI extends BaseStage {

	private final VisTextButton pointButton;
	private final VisTextButton cloudButton;
	private final VisValidatableTextField cloudRadiusTextField;
	private final VisValidatableTextField learningRateTextField;
	private final VisValidatableTextField cloudSizeTextField;
	private final VisTextButton multipleButton;
	private final VisTextButton circleButton;
	private final VisSlider classNumberSlider;

	public LogisticRegressionSetupUI() {

		Table table = new VisTable();
		table.setFillParent(true);
		addActor(table);

		pointButton = new VisTextButton("Точки");
		cloudButton = new VisTextButton("Облачка");
		circleButton = new VisTextButton("Круг");
		multipleButton = new VisTextButton("Мульти-класс");
		cloudSizeTextField = new VisValidatableTextField(Validators.INTEGERS);
		cloudRadiusTextField = new VisValidatableTextField(Validators.INTEGERS);
		learningRateTextField = new VisValidatableTextField(Validators.FLOATS);
		classNumberSlider = new VisSlider(3, 5, 1, false);
		VisLabel classNumberLabel;


		table.defaults().padBottom(5);

		table.add(pointButton).colspan(2);
		table.row();
		table.add(cloudButton).colspan(2);
		table.row();
		table.add(circleButton).colspan(2);
		table.row();
		table.add(multipleButton).colspan(2).padBottom(20);
		table.row();
		table.add(new VisLabel("Размер облачков: ")).padRight(20);
		table.add(cloudSizeTextField).width(75);
		table.row();
		table.add(new VisLabel("Радиус облачков: ")).padRight(20);
		table.add(cloudRadiusTextField).width(75);
		table.row();
		table.add(new VisLabel("Learning rate: ")).padRight(20);
		table.add(learningRateTextField).width(75);
		table.row();
		table.add((classNumberLabel = new VisLabel("3 класса"))).padRight(20);
		table.add(classNumberSlider).width(75);
		table.row();

		Parameters defaultParameters = new Parameters();
		cloudSizeTextField.setText(String.valueOf(defaultParameters.getCloudSize()));
		cloudRadiusTextField.setText(String.valueOf(((int) defaultParameters.getCloudRadius())));
		learningRateTextField.setText(String.valueOf(defaultParameters.getLearningRate()));
		classNumberSlider.setValue(defaultParameters.getClassCount());
		classNumberSlider.addChangeLsitener(f -> {
			int numberOfClasses = MathUtils.round(f);
			String text;
			if (numberOfClasses >= 5) {
				text = numberOfClasses + " классов";
			} else {
				text = numberOfClasses + " класса";
			}
			classNumberLabel.setText(text);
		});
		classNumberSlider.setValue(3);
	}

	public int getCloudRadius(){
		return parseInt(cloudRadiusTextField.getText());
	}

	public double getLearningRate(){
		return parseDouble(learningRateTextField.getText());
	}

	public int getCloudSize(){
		return parseInt(cloudSizeTextField.getText());
	}

	public int getClassCount(){
		return MathUtils.round(classNumberSlider.getValue());
	}

	private int parseInt(String s){
		if (StringUtils.isEmpty(s)) return 0;
		return Integer.parseInt(s);
	}

	private double parseDouble(String s){
		if (StringUtils.isEmpty(s)) return 0;
		return Double.parseDouble(s);
	}

	public void onPointButton(Runnable r){
		pointButton.addChangeListener(r);
	}

	public void onCloudButton(Runnable r){
		cloudButton.addChangeListener(r);
	}

	public void onCircleButton(Runnable r){
		circleButton.addChangeListener(r);
	}

	public void onMultipleButton(Runnable r){
		multipleButton.addChangeListener(r);
	}

}
