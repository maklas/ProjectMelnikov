package ru.maklas.melnikov.utils.math;

import com.badlogic.gdx.utils.Array;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class Matrix implements Iterable<DoubleArray> {

	private Array<DoubleArray> rows;
	private int rowSize = 0;

	public Matrix() {
		rows = new Array<>(true, 16);
	}

	public Matrix(int height, int width, double value) {
		rows = new Array<>(true, height);
		for (int i = 0; i < height; i++) {
			rows.add(DoubleArray.of(value, width));
		}
		rowSize = width;
	}

	public Matrix(int height, int width) {
		rows = new Array<>(true, height);
		for (int i = 0; i < height; i++) {
			rows.add(DoubleArray.of(0, width));
		}
		rowSize = width;
	}

	public Matrix(Matrix matrix) {
		rows = new Array<>(true, 16);
		for (DoubleArray row : matrix.rows) {
			rows.add(row.cpy());
		}
		rowSize = matrix.rowSize;
	}

	public Matrix(double[][] matrix) {
		rows = new Array<>(true, 16);
		for (int i = 0; i < matrix.length; i++) {
			double[] row = matrix[i];
			addRow(new DoubleArray(row));
		}
	}

	@NotNull
	@Override
	public Iterator<DoubleArray> iterator() {
		return rows.iterator();
	}

	/** Number of rows **/
	public int getHeight(){
		return rows.size;
	}

	/** Number of columns **/
	public int getWidth(){
		return rowSize;
	}

	public Array<DoubleArray> getRows() {
		return rows;
	}

	public double get(int row, int col) {
		return rows.get(row).get(col);
	}

	public void set(int row, int col, double val) {
		rows.get(row).set(col, val);
	}

	public DoubleArray getRow(int row) {
		return rows.get(row);
	}

	public DoubleArray firstRow() {
		return rows.get(0);
	}

	public DoubleArray firstColumn() {
		return getColumn(0);
	}

	public DoubleArray getColumn(int col) {
		DoubleArray column = new DoubleArray();
		for (DoubleArray row : rows) {
			column.add(row.items[col]);
		}
		return column;
	}

	public void addRow(DoubleArray row){
		if (rows.size == 0){
			rowSize = row.size;
		} else if (row.size != rowSize){
			throw new RuntimeException("Matrix has rows with size of " + rowSize + ". Tried to addRow row of size " + row.size);
		}
		rows.add(row);
	}

	public void addRow(DoubleArray row, int index){
		if (rows.size == 0){
			rowSize = row.size;
		} else if (row.size != rowSize){
			throw new RuntimeException("Matrix has rows with size of " + rowSize + ". Tried to addRow row of size " + row.size);
		}
		rows.insert(index, row);
	}

	public Matrix addColumn(DoubleArray column){
		if (column.size != rows.size) {
			throw new RuntimeException("Can't append column of size " + column.size + " to matrix" + toStringSize(this));
		}
		if (column.size == 0) return this;
		for (int i = 0; i < rows.size; i++) {
			rows.get(i).add(column.items[i]);
		}
		rowSize++;
		return this;
	}

	public Matrix addColumn(DoubleArray column, int index){
		if (column.size != rows.size) {
			throw new RuntimeException("Can't append column of size " + column.size + " to matrix" + toStringSize(this));
		}
		if (column.size == 0) return this;
		for (int i = 0; i < rows.size; i++) {
			rows.get(i).insert(index, column.items[i]);
		}
		rowSize++;
		return this;
	}

	public Matrix cpy(){
		return new Matrix(this);
	}

	public Matrix transpose(){
		Matrix matrix = new Matrix();
		for (int i = 0; i < rowSize; i++) {
			DoubleArray row = new DoubleArray();
			for (int j = 0; j < rows.size; j++) {
				row.add(get(j, i));
			}
			matrix.addRow(row);
		}

		return matrix;
	}

	public Matrix transposeCounter(){
		Matrix matrix = new Matrix();
		for (int i = 0; i < rowSize; i++) {
			DoubleArray row = new DoubleArray();
			for (int j = 0; j < rows.size; j++) {
				row.add(get(j, i));
			}
			matrix.addRow(row);
		}
		matrix.rows.reverse();
		return matrix;
	}

	public Matrix mul(double val){
		return mul(val, false);
	}

	public Matrix mul(double val, boolean mutateThis){
		Matrix m = mutateThis ? this : new Matrix(this);
		for (DoubleArray row : m.rows) {
			for (int i = 0; i < row.size; i++) {
				row.items[i] *= val;
			}
		}
		return m;
	}

	public Matrix mul(Matrix matrix){
		if (rowSize != matrix.rows.size){
			throw new RuntimeException("Matrix " + toStringSize(this) + " can't be multiplied by " + toStringSize(matrix));
		}

		int newHeight = this.getHeight();
		int newWidth = matrix.rowSize;
		int size = rowSize;

		Matrix result = new Matrix(newHeight, newWidth);
		for (int i = 0; i < newHeight; i++) {
			for (int j = 0; j < newWidth; j++) {
				double val = 0;
				for (int k = 0; k < size; k++) {
					val += get(i, k) * matrix.get(k, j);
				}
				result.set(i, j, val);
			}
		}
		return result;
	}

	public Matrix map(MatrixMapFunction mapper){
		return new Matrix(this)._map(mapper);
	}
	public Matrix _map(MatrixMapFunction mapper){
		for (int i = 0; i < rows.size; i++) {
			DoubleArray row = rows.get(i);
			for (int j = 0; j < row.size; j++) {
				row.items[j] = mapper.map(i, j, row.items[j]);
			}
		}
		return this;
	}

	private static String toStringSize(Matrix m){
		return "[" + m.getHeight() + ", " + m.getWidth() + "]";
	}

	@Override
	public String toString(){
		if (rows.size == 0) return "";
		StringBuilder sb = new StringBuilder();
		sb.append(rows.get(0));
		for (int i = 1; i < rows.size; i++) {
			sb.append("\n").append(rows.get(i));
		}
		return sb.toString();
	}
}
