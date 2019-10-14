package ru.maklas.melnikov.user_interface;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisValidatableTextField;
import ru.maklas.melnikov.states.Parameters;
import ru.maklas.melnikov.utils.StringUtils;

public class LogisticRegressionSetupUI extends BaseStage {

	private final VisTextButton pointButton;
	private final VisTextButton cloudButton;
	private final VisValidatableTextField cloudRadiusTextField;
	private final VisValidatableTextField learningRateTextField;
	private final VisValidatableTextField cloudSizeTextField;
	private final VisTextButton multipleButton;

	public LogisticRegressionSetupUI() {

		Table table = new VisTable();
		table.setFillParent(true);
		addActor(table);

		pointButton = new VisTextButton("Точки");
		cloudButton = new VisTextButton("Облачка");
		multipleButton = new VisTextButton("3 класса");
		cloudSizeTextField = new VisValidatableTextField(Validators.INTEGERS);
		cloudRadiusTextField = new VisValidatableTextField(Validators.INTEGERS);
		learningRateTextField = new VisValidatableTextField(Validators.FLOATS);


		table.defaults().padBottom(5);

		table.add(pointButton).colspan(2);
		table.row();
		table.add(cloudButton).colspan(2);
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

		Parameters defaultParameters = new Parameters();
		cloudSizeTextField.setText(String.valueOf(defaultParameters.getCloudSize()));
		cloudRadiusTextField.setText(String.valueOf(((int) defaultParameters.getCloudRadius())));
		learningRateTextField.setText(String.valueOf(defaultParameters.getLearningRate()));
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

	public void onMultipleButton(Runnable r){
		multipleButton.addChangeListener(r);
	}

}
