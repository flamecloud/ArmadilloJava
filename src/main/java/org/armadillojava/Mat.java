/*******************************************************************************
 * Copyright 2013-2014 Sebastian Niemann <niemann@sra.uni-hannover.de>.
 * 
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://opensource.org/licenses/MIT
 * 
 * Developers:
 * Sebastian Niemann - Lead developer
 * Daniel Kiechle - Unit testing
 ******************************************************************************/
package org.armadillojava;

import java.util.Arrays;

import org.netlib.util.intW;

import com.github.fommil.netlib.BLAS;
import com.github.fommil.netlib.LAPACK;

/**
 * Provides a real-valued dense matrix with interfaces similar to the Armadillo C++ Algebra Library (Armadillo) by
 * Conrad Sanderson et al..
 * <p>
 * If not stated otherwise (marked as non-canonical), the provided interfaces are identical to Armadillo (e.g. same
 * ordering of arguments, accepted values, ...). However, numeric results may slightly differ from the Armadillo C++
 * Algebra Library.
 * 
 * @author Sebastian Niemann
 * 
 * @see <a href="http://arma.sourceforge.net/">Armadillo C++ Algebra Library</a>
 */
public class Mat extends AbstractMat {

  /**
   * Creates an empty matrix.
   */
  public Mat() {
    set_size(0, 0);
  }

  /**
   * Creates an uninitialised matrix with the specified number of rows and columns.
   * 
   * @param n_rows The number of rows
   * @param n_cols The number of columns
   * 
   * @throws NegativeArraySizeException The specified number of rows ({@code n_rows}) must be positive.
   * @throws NegativeArraySizeException The specified number of columns ({@code n_cols}) must be positive.
   */
  public Mat(final int n_rows, final int n_cols) throws NegativeArraySizeException {
    /*
     * The parameters "n_rows" and "n_cols" are validated within set_size(int, int).
     */
    set_size(n_rows, n_cols);
  }

  /**
   * Creates a matrix with the specified number of rows and columns that is filled according to {@code fill_type}.
   * 
   * @param n_rows The number of rows
   * @param n_cols The number of columns
   * @param fill_type The fill type
   * 
   * @throws NegativeArraySizeException The specified number of rows ({@code n_rows}) must be positive.
   * @throws NegativeArraySizeException The specified number of columns ({@code n_cols}) must be positive.
   * 
   * @see Fill
   */
  public Mat(final int n_rows, final int n_cols, final Fill fill_type) throws NegativeArraySizeException {
    /*
     * The parameters "n_rows" and "n_cols" are validated within zeros(int, int), ones(int, int), randu(int, int) and
     * randn(int, int).
     */

    switch (fill_type) {
      case NONE:
        set_size(n_rows, n_cols);
        break;
      case ZEROS:
        zeros(n_rows, n_cols);
        break;
      case ONES:
        ones(n_rows, n_cols);
        break;
      case EYE:
        eye(n_rows, n_cols);
        break;
      case RANDU:
        randu(n_rows, n_cols);
        break;
      case RANDN:
        randn(n_rows, n_cols);
      default:
        // TODO throw Exception
    }
  }

  /**
   * Creates a deep copy of a matrix.
   * 
   * @param mat The matrix
   */
  public Mat(final AbstractMat mat) {
    copy_size(mat);
    System.arraycopy(mat._data, 0, _data, 0, mat.n_elem);
  }

  /**
   * Creates a matrix in the shape of a column vector with the same number of elements and values as the provided array.
   * 
   * @param array The array
   */
  public Mat(final double[] array) {
    set_size(array.length);
    System.arraycopy(array, 0, _data, 0, array.length);
  }

  /**
   * Creates a matrix with the same number of elements and values as the provided array.
   * <p>
   * The array is assumed to is structured as {@code array[rows][columns]}.
   * 
   * @param array The array
   * 
   * @throws IllegalArgumentException All rows must have the same length.
   */
  public Mat(double[][] array) throws IllegalArgumentException {
    set_size(array.length, array[0].length);

    for (int i = 0; i < n_rows; i++) {
      for (int j = 0; j < n_cols; j++) {
        if (array[i].length != n_cols) {
          throw new IllegalArgumentException("All rows must have the same length.");
        }

        _data[i + j * n_rows] = array[i][j];
      }
    }
  }

  /**
   * Creates a deep copy of a matrix sub view.
   * 
   * @param view The sub view
   */
  protected Mat(final AbstractView view) {
    copy_size(view);

    view.iteratorReset();
    for (int n = 0; n < n_elem; n++) {
      _data[n] = view._data[view.iteratorNext()];
    }
  }

  /**
   * TODO
   * 
   * @param A TODO
   */
  protected void copy_size(final AbstractView A) {
    set_size(A.n_rows, A.n_cols);
  }

  /**
   * Returns a deep copy of the {@code col_number}th column.
   * 
   * @param col_number The column
   * 
   * @return TODO
   * 
   * @throws IndexOutOfBoundsException The specified column ({@code col_number}) is out of bounds.
   */
  public Col col(final int col_number) throws IndexOutOfBoundsException {
    if (col_number < 0 || col_number >= n_cols) {
      throw new IndexOutOfBoundsException("The specified column (" + col_number + ") is out of bounds.");
    }

    int n = col_number * n_rows;
    return new Col(Arrays.copyOfRange(_data, n, n + n_rows));
  }

  /**
   * Performs an in-place binary operation on the {@code col_number}th column with the specified right-hand side
   * operand.
   * 
   * @param col_number The column
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws IndexOutOfBoundsException The specified column ({@code col_number}) is out of bounds.
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   */
  public void col(final int col_number, final Op binary_operator, final double operand) throws IndexOutOfBoundsException, UnsupportedOperationException {
    /*
     * The parameter "binary_operator" is validated within AbstractView.inPlace(Op, double).
     */

    if (col_number < 0 || col_number >= n_cols) {
      throw new IndexOutOfBoundsException("The specified column (" + col_number + ") is out of bounds.");
    }

    new ViewSubCol(this, col_number).inPlace(binary_operator, operand);
  }

  /**
   * Performs an in-place binary operation on the {@code col_number}th column with the specified right-hand side
   * operand.
   * 
   * @param col_number The column
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws IndexOutOfBoundsException The specified column ({@code col_number}) is out of bounds.
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   * @throws RuntimeException The provided ({@code operand.n_rows}, {@code operand.n_cols})-matrix must be equally in
   *           shape to a ({@code n_rows}, 1)-matrix.
   */
  public void col(final int col_number, final Op binary_operator, final AbstractMat operand) throws IndexOutOfBoundsException, UnsupportedOperationException, RuntimeException {
    /*
     * The parameter "binary_operator" is validated within AbstractView.inPlace(Op, AbstractMat).
     */

    if (col_number < 0 || col_number >= n_cols) {
      throw new IndexOutOfBoundsException("The specified column (" + col_number + ") is out of bounds.");
    }

    if (!operand.is_colvec() || operand.n_elem != n_rows) {
      throw new RuntimeException("The provided (" + operand.n_rows + ", " + operand.n_cols + ")-matrix must be equally in shape to a (" + n_rows + ", 1)-matrix.");
    }

    new ViewSubCol(this, col_number).inPlace(binary_operator, operand);
  }

  /**
   * Returns a deep copy of the {@code span._first}th to {@code span._last} row of the {@code col_number}th column.
   * 
   * @param span The span
   * @param col_number The column
   * 
   * @return TODO
   * 
   * @throws IndexOutOfBoundsException The specified column ({@code col_number}) is out of bounds.
   * @throws IndexOutOfBoundsException The first specified row ({@code span._first}) is out of bounds.
   * @throws IndexOutOfBoundsException The last specified row ({@code span._last}) is out of bounds.
   */
  public Col col(final Span span, final int col_number) throws IndexOutOfBoundsException {
    /*
     * The parameter "span" was already validated during its instantiation.
     */

    if (span._isEntireRange) {
      return col(col_number);
    } else {
      if (col_number < 0 || col_number >= n_cols) {
        throw new IndexOutOfBoundsException("The specified column (" + col_number + ") is out of bounds.");
      }

      if (span._first < 0) {
        throw new IndexOutOfBoundsException("The first specified row (" + span._first + ") is out of bounds.");
      }

      if (span._last >= n_rows) {
        throw new IndexOutOfBoundsException("The last specified row (" + span._last + ") is out of bounds.");
      }

      int n = col_number * n_rows;
      return new Col(Arrays.copyOfRange(_data, span._first + n, span._last + 1 + n));
    }
  }

  /**
   * Performs an in-place binary operation on the {@code span._first}th to {@code span._last} row of the
   * {@code col_number}th column with the specified right-hand side operand.
   * 
   * @param span The span
   * @param col_number The column
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws IndexOutOfBoundsException The specified column ({@code col_number}) is out of bounds.
   * @throws IndexOutOfBoundsException The first specified row ({@code span._first}) is out of bounds.
   * @throws IndexOutOfBoundsException The last specified row ({@code span._last}) is out of bounds.
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   */
  public void col(final Span span, final int col_number, final Op binary_operator, final double operand) throws IndexOutOfBoundsException, UnsupportedOperationException {
    /*
     * The parameter "span" was already validated during its instantiation.
     * The parameter "binary_operator" is validated within AbstractView.inPlace(Op, double).
     */

    if (span._isEntireRange) {
      col(col_number, binary_operator, operand);
    } else {
      if (col_number < 0 || col_number >= n_cols) {
        throw new IndexOutOfBoundsException("The specified column (" + col_number + ") is out of bounds.");
      }

      if (span._first < 0) {
        throw new IndexOutOfBoundsException("The first specified row (" + span._first + ") is out of bounds.");
      }

      if (span._last >= n_rows) {
        throw new IndexOutOfBoundsException("The last specified row (" + span._last + ") is out of bounds.");
      }

      new ViewSubCol(this, col_number, span._first, span._last - span._first + 1).inPlace(binary_operator, operand);
    }
  }

  /**
   * Performs an in-place binary operation on the {@code span._first}th to {@code span._last} row of the
   * {@code col_number}th column with the specified right-hand side operand.
   * 
   * @param span The span
   * @param col_number The column
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws IndexOutOfBoundsException The specified column ({@code col_number}) is out of bounds.
   * @throws IndexOutOfBoundsException The first specified row ({@code span._first}) is out of bounds.
   * @throws IndexOutOfBoundsException The last specified row ({@code span._last}) is out of bounds.
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   * @throws RuntimeException The provided ({@code operand.n_rows}, {@code operand.n_cols})-matrix must be equally in
   *           shape to a ({@code span._last} - {@code span._first} + 1, 1)-matrix.
   */
  public void col(final Span span, final int col_number, final Op binary_operator, AbstractMat operand) throws IndexOutOfBoundsException, UnsupportedOperationException, RuntimeException {
    /*
     * The parameter "span" was already validated during its instantiation.
     * The parameter "binary_operator" is validated within AbstractView.inPlace(Op, AbstractMat).
     */

    if (span._isEntireRange) {
      col(col_number, binary_operator, operand);
    } else {
      if (col_number < 0 || col_number >= n_cols) {
        throw new IndexOutOfBoundsException("The specified column (" + col_number + ") is out of bounds.");
      }

      if (span._first < 0) {
        throw new IndexOutOfBoundsException("The first specified row (" + span._first + ") is out of bounds.");
      }

      if (span._last >= n_rows) {
        throw new IndexOutOfBoundsException("The last specified row (" + span._last + ") is out of bounds.");
      }

      if (!operand.is_colvec() || operand.n_elem != span._last - span._first + 1) {
        throw new RuntimeException("The provided (" + operand.n_rows + ", " + operand.n_cols + ")-matrix must be equally in shape to a (" + (span._last - span._first + 1) + ", 1)-matrix.");
      }

      new ViewSubCol(this, col_number, span._first, span._last - span._first + 1).inPlace(binary_operator, operand);
    }
  }

  /**
   * Returns a deep copy of the {@code row_number}th row.
   * 
   * @param row_number The column
   * 
   * @return TODO
   * 
   * @throws IndexOutOfBoundsException The specified row ({@code row_number}) is out of bounds.
   */
  public Row row(final int row_number) throws IndexOutOfBoundsException {
    if (row_number < 0 || row_number >= n_rows) {
      throw new IndexOutOfBoundsException("The specified row (" + row_number + ") is out of bounds.");
    }

    return new Row(new ViewSubRow(this, row_number));
  }

  /**
   * Performs an in-place binary operation on the {@code row_number}th row with the specified right-hand side operand.
   * 
   * @param row_number The row
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws IndexOutOfBoundsException The specified row ({@code row_number}) is out of bounds.
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   */
  public void row(final int row_number, final Op binary_operator, final double operand) throws IndexOutOfBoundsException, UnsupportedOperationException {
    /*
     * The parameter "binary_operator" is validated within AbstractView.inPlace(Op, double).
     */

    if (row_number < 0 || row_number >= n_rows) {
      throw new IndexOutOfBoundsException("The specified row (" + row_number + ") is out of bounds.");
    }

    new ViewSubRow(this, row_number).inPlace(binary_operator, operand);
  }

  /**
   * Performs an in-place binary operation on the {@code row_number}th row with the specified right-hand side operand.
   * 
   * @param row_number The row
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws IndexOutOfBoundsException The specified row ({@code row_number}) is out of bounds.
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   * @throws RuntimeException The provided ({@code operand.n_rows}, {@code operand.n_cols})-matrix must be equally in
   *           shape to a (1, {@code n_cols})-matrix.
   */
  public void row(final int row_number, final Op binary_operator, final AbstractMat operand) throws IndexOutOfBoundsException, UnsupportedOperationException, RuntimeException {
    /*
     * The parameter "binary_operator" is validated within AbstractView.inPlace(Op, AbstractMat).
     */

    if (row_number < 0 || row_number >= n_rows) {
      throw new IndexOutOfBoundsException("The specified row (" + row_number + ") is out of bounds.");
    }

    if (!operand.is_rowvec() || operand.n_elem != n_cols) {
      throw new RuntimeException("The provided (" + operand.n_rows + ", " + operand.n_cols + ")-matrix must be equally in shape to a (1, " + n_cols + ")-matrix.");
    }

    new ViewSubRow(this, row_number).inPlace(binary_operator, operand);
  }

  /**
   * Returns a deep copy of the {@code row_number}th row of the {@code span._first}th to {@code span._last} column.
   * 
   * @param row_number The row
   * @param span The span
   * 
   * @return TODO
   * 
   * @throws IndexOutOfBoundsException The specified row ({@code row_number}) is out of bounds.
   * @throws IndexOutOfBoundsException The first specified column ({@code span._first}) is out of bounds.
   * @throws IndexOutOfBoundsException The last specified column ({@code span._last}) is out of bounds.
   */
  public Row row(final int row_number, final Span span) throws IndexOutOfBoundsException {
    /*
     * The parameter "span" was already validated during its instantiation.
     */

    if (span._isEntireRange) {
      return row(row_number);
    } else {
      if (row_number < 0 || row_number >= n_rows) {
        throw new IndexOutOfBoundsException("The specified row (" + row_number + ") is out of bounds.");
      }

      if (span._first < 0) {
        throw new IndexOutOfBoundsException("The first specified column (" + span._first + ") is out of bounds.");
      }

      if (span._last >= n_cols) {
        throw new IndexOutOfBoundsException("The last specified column (" + span._last + ") is out of bounds.");
      }

      return new Row(new ViewSubRow(this, row_number, span._first, span._last - span._first + 1));
    }
  }

  /**
   * Performs an in-place binary operation on the {@code row_number}th row of the {@code span._first}th to
   * {@code span._last} column with the specified right-hand side operand.
   * 
   * @param row_number The row
   * @param span The span
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws IndexOutOfBoundsException The specified row ({@code row_number}) is out of bounds.
   * @throws IndexOutOfBoundsException The first specified column ({@code span._first}) is out of bounds.
   * @throws IndexOutOfBoundsException The last specified column ({@code span._last}) is out of bounds.
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   */
  public void row(final int row_number, final Span span, final Op binary_operator, final double operand) throws IndexOutOfBoundsException, UnsupportedOperationException {
    /*
     * The parameter "span" was already validated during its instantiation.
     * The parameter "binary_operator" is validated within AbstractView.inPlace(Op, double).
     */

    if (span._isEntireRange) {
      row(row_number, binary_operator, operand);
    } else {
      if (row_number < 0 || row_number >= n_rows) {
        throw new IndexOutOfBoundsException("The specified row (" + row_number + ") is out of bounds.");
      }

      if (span._first < 0) {
        throw new IndexOutOfBoundsException("The first specified column (" + span._first + ") is out of bounds.");
      }

      if (span._last >= n_cols) {
        throw new IndexOutOfBoundsException("The last specified column (" + span._last + ") is out of bounds.");
      }

      new ViewSubRow(this, row_number, span._first, span._last - span._first + 1).inPlace(binary_operator, operand);
    }
  }

  /**
   * Performs an in-place binary operation on the {@code row_number}th row of the {@code span._first}th to
   * {@code span._last} column with the specified right-hand side operand.
   * 
   * @param row_number The row
   * @param span The span
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws IndexOutOfBoundsException The specified row ({@code row_number}) is out of bounds.
   * @throws IndexOutOfBoundsException The first specified column ({@code span._first}) is out of bounds.
   * @throws IndexOutOfBoundsException The last specified column ({@code span._last}) is out of bounds.
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   * @throws RuntimeException The provided ({@code operand.n_rows}, {@code operand.n_cols})-matrix must be equally in
   *           shape to a (1, {@code span._last} - {@code span._first} + 1)-matrix.
   */
  public void row(final int row_number, final Span span, final Op binary_operator, final AbstractMat operand) throws IndexOutOfBoundsException, UnsupportedOperationException, RuntimeException {
    /*
     * The parameter "span" was already validated during its instantiation.
     * The parameter "binary_operator" is validated within AbstractView.inPlace(Op, AbstractMat).
     */

    if (span._isEntireRange) {
      row(row_number, binary_operator, operand);
    } else {
      if (row_number < 0 || row_number >= n_rows) {
        throw new IndexOutOfBoundsException("The specified row (" + row_number + ") is out of bounds.");
      }

      if (span._first < 0) {
        throw new IndexOutOfBoundsException("The first specified column (" + span._first + ") is out of bounds.");
      }

      if (span._last >= n_cols) {
        throw new IndexOutOfBoundsException("The last specified column (" + span._last + ") is out of bounds.");
      }

      if (!operand.is_rowvec() || operand.n_elem != span._last - span._first + 1) {
        throw new RuntimeException("The provided (" + operand.n_rows + ", " + operand.n_cols + ")-matrix must be equally in shape to a (1, " + (span._last - span._first + 1) + ")-matrix.");
      }

      new ViewSubRow(this, row_number, span._first, span._last - span._first + 1).inPlace(binary_operator, operand);
    }
  }

  /**
   * Returns a deep copy of the {@code first_col}th to {@code last_col} column.
   * 
   * @param first_col The first column
   * @param last_col The last column
   * 
   * @return TODO
   * 
   * @throws RuntimeException The first specified column ({@code first_col}) must be less than or equal the last
   *           specified column ({@code last_col}).
   * @throws IndexOutOfBoundsException The first specified row ({@code first_col}) is out of bounds.
   * @throws IndexOutOfBoundsException The last specified row ({@code last_row}) is out of bounds.
   */
  public Mat cols(final int first_col, final int last_col) throws RuntimeException, IndexOutOfBoundsException {
    if (last_col < first_col) {
      throw new RuntimeException("The first specified column (" + first_col + ") must be less than or equal the last specified column (" + last_col + ").");
    }

    if (first_col < 0) {
      throw new IndexOutOfBoundsException("The first specified column (" + first_col + ") is out of bounds.");
    }

    if (last_col >= n_cols) {
      throw new IndexOutOfBoundsException("The last specified column (" + last_col + ") is out of bounds.");
    }

    return new Mat(new ViewSubCols(this, first_col, last_col - first_col + 1));
  }

  /**
   * Performs an in-place binary operation on the {@code first_col}th to {@code last_col} column with the specified
   * right-hand side operand.
   * 
   * @param first_col The first column
   * @param last_col The last column
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws RuntimeException The first specified column ({@code first_col}) must be less than or equal the last
   *           specified column ({@code last_col}).
   * @throws IndexOutOfBoundsException The first specified column ({@code first_row}) is out of bounds.
   * @throws IndexOutOfBoundsException The last specified column ({@code last_row}) is out of bounds.
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   */
  public void cols(final int first_col, final int last_col, final Op binary_operator, final double operand) throws RuntimeException, IndexOutOfBoundsException, UnsupportedOperationException {
    /*
     * The parameter "binary_operator" is validated within AbstractView.inPlace(Op, double).
     */

    if (last_col < first_col) {
      throw new RuntimeException("The first specified column (" + first_col + ") must be less than or equal the last specified column (" + last_col + ").");
    }

    if (first_col < 0) {
      throw new IndexOutOfBoundsException("The first specified column (" + first_col + ") is out of bounds.");
    }

    if (last_col >= n_cols) {
      throw new IndexOutOfBoundsException("The last specified column (" + last_col + ") is out of bounds.");
    }

    new ViewSubCols(this, first_col, last_col - first_col + 1).inPlace(binary_operator, operand);
  }

  /**
   * Performs an in-place binary operation on the {@code first_col}th to {@code last_col} column with the specified
   * right-hand side operand.
   * 
   * @param first_col The first column
   * @param last_col The last column
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws RuntimeException The first specified column ({@code first_col}) must be less than or equal the last
   *           specified column ({@code last_col}).
   * @throws IndexOutOfBoundsException The first specified column ({@code first_row}) is out of bounds.
   * @throws IndexOutOfBoundsException The last specified column ({@code last_row}) is out of bounds.
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   * @throws RuntimeException The provided ({@code operand.n_rows}, {@code operand.n_cols})-matrix must be equally in
   *           shape to a ({@code last_row} - {@code first_row} + 1, {@code n_cols})-matrix.
   */
  public void cols(final int first_col, final int last_col, final Op binary_operator, final AbstractMat operand) throws RuntimeException, IndexOutOfBoundsException, UnsupportedOperationException, RuntimeException {
    /*
     * The parameter "binary_operator" is validated within AbstractView.inPlace(Op, AbstractMat).
     */

    if (last_col < first_col) {
      throw new RuntimeException("The first specified column (" + first_col + ") must be less than or equal the last specified column (" + last_col + ").");
    }

    if (first_col < 0) {
      throw new IndexOutOfBoundsException("The first specified column (" + first_col + ") is out of bounds.");
    }

    if (last_col >= n_cols) {
      throw new IndexOutOfBoundsException("The last specified column (" + last_col + ") is out of bounds.");
    }

    if (operand.n_cols != last_col - first_col + 1 || operand.n_rows != n_rows) {
      throw new RuntimeException("The provided (" + operand.n_rows + ", " + operand.n_cols + ")-matrix must be equally in shape to a (" + (n_rows - first_col + 1) + ", " + n_cols + ")-matrix.");
    }

    new ViewSubCols(this, first_col, last_col - first_col + 1).inPlace(binary_operator, operand);
  }

  /**
   * Returns a deep copy of the specified columns.
   * <p>
   * <b>Note:</b> No explicit bounds checking handling. However, the JVM should throw IndexOutOfBoundsException
   * exceptions upon errors.
   * 
   * @param vector_of_column_indices The columns
   * 
   * @return TODO
   */
  public Mat cols(final AbstractVector vector_of_column_indices) {
    return new Mat(new ViewElemCols(this, vector_of_column_indices._data));
  }

  /**
   * Performs an in-place binary operation on the specified columns with the specified right-hand side operand.
   * <p>
   * <b>Note:</b> No explicit bounds checking handling. However, the JVM should throw IndexOutOfBoundsException
   * exceptions upon errors.
   * 
   * @param vector_of_column_indices The columns
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   */
  public void cols(final AbstractVector vector_of_column_indices, final Op binary_operator, final double operand) throws UnsupportedOperationException {
    new ViewElemCols(this, vector_of_column_indices._data).inPlace(binary_operator, operand);
  }

  /**
   * Performs an in-place binary operation on the specified columns with the specified right-hand side operand.
   * <p>
   * <b>Note:</b> No explicit bounds checking handling. However, the JVM should throw IndexOutOfBoundsException
   * exceptions upon errors.
   * 
   * @param vector_of_column_indices The columns
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   * @throws RuntimeException The provided ({@code operand.n_rows}, {@code operand.n_cols})-matrix must be equally in
   *           shape to a ({@code n_rows}, {@code vector_of_column_indices.n_elem})-matrix.
   */
  public void cols(final AbstractVector vector_of_column_indices, final Op binary_operator, final AbstractMat operand) throws UnsupportedOperationException, RuntimeException {
    if (operand.n_rows != n_rows || operand.n_cols != vector_of_column_indices.n_elem) {
      throw new RuntimeException("The provided (" + operand.n_rows + ", " + operand.n_cols + ")-matrix must be equally in shape to a (" + n_rows + ", " + vector_of_column_indices.n_elem + ")-matrix.");
    }

    new ViewElemCols(this, vector_of_column_indices._data).inPlace(binary_operator, operand);
  }

  /**
   * Returns a deep copy of the {@code first_row}th to {@code last_row} row.
   * 
   * @param first_row The first row
   * @param last_row The last row
   * 
   * @return TODO
   * 
   * @throws RuntimeException The first specified row ({@code first_row}) must be less than or equal the last specified
   *           row ({@code last_row}).
   * @throws IndexOutOfBoundsException The first specified row ({@code first_row}) is out of bounds.
   * @throws IndexOutOfBoundsException The last specified row ({@code last_row}) is out of bounds.
   */
  public Mat rows(final int first_row, final int last_row) throws RuntimeException, IndexOutOfBoundsException {
    if (last_row < first_row) {
      throw new RuntimeException("The first specified row (" + first_row + ") must be less than or equal the last specified row (" + last_row + ").");
    }

    if (first_row < 0) {
      throw new IndexOutOfBoundsException("The first specified row (" + first_row + ") is out of bounds.");
    }

    if (last_row >= n_rows) {
      throw new IndexOutOfBoundsException("The last specified row (" + last_row + ") is out of bounds.");
    }

    return new Mat(new ViewSubRows(this, first_row, last_row - first_row + 1));
  }

  /**
   * Performs an in-place binary operation on the {@code first_row}th to {@code last_row} row with the specified
   * right-hand side operand.
   * 
   * @param first_row The first row
   * @param last_row The last row
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws RuntimeException The first specified row ({@code first_row}) must be less than or equal the last specified
   *           row ({@code last_row}).
   * @throws IndexOutOfBoundsException The first specified row ({@code first_row}) is out of bounds.
   * @throws IndexOutOfBoundsException The last specified row ({@code last_row}) is out of bounds.
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   */
  public void rows(final int first_row, final int last_row, final Op binary_operator, final double operand) throws RuntimeException, IndexOutOfBoundsException, UnsupportedOperationException {
    /*
     * The parameter "binary_operator" is validated within AbstractView.inPlace(Op, double).
     */

    if (last_row < first_row) {
      throw new RuntimeException("The first specified row (" + first_row + ") must be less than or equal the last specified row (" + last_row + ").");
    }

    if (first_row < 0) {
      throw new IndexOutOfBoundsException("The first specified row (" + first_row + ") is out of bounds.");
    }

    if (last_row >= n_rows) {
      throw new IndexOutOfBoundsException("The last specified row (" + last_row + ") is out of bounds.");
    }

    new ViewSubRows(this, first_row, last_row - first_row + 1).inPlace(binary_operator, operand);
  }

  /**
   * Performs an in-place binary operation on the {@code first_row}th to {@code last_row} row with the specified
   * right-hand side operand.
   * 
   * @param first_row The first row
   * @param last_row The last row
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws RuntimeException The first specified row ({@code first_row}) must be less than or equal the last specified
   *           row ({@code last_row}).
   * @throws IndexOutOfBoundsException The first specified row ({@code first_row}) is out of bounds.
   * @throws IndexOutOfBoundsException The last specified row ({@code last_row}) is out of bounds.
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   * @throws RuntimeException The provided ({@code operand.n_rows}, {@code operand.n_cols})-matrix must be equally in
   *           shape to a ({@code last_row} - {@code first_row} + 1, {@code n_cols})-matrix.
   */
  public void rows(final int first_row, final int last_row, final Op binary_operator, final AbstractMat operand) throws RuntimeException, IndexOutOfBoundsException, UnsupportedOperationException, RuntimeException {
    /*
     * The parameter "binary_operator" is validated within AbstractView.inPlace(Op, AbstractMat).
     */

    if (last_row < first_row) {
      throw new RuntimeException("The first specified row (" + first_row + ") must be less than or equal the last specified row (" + last_row + ").");
    }

    if (first_row < 0) {
      throw new IndexOutOfBoundsException("The first specified row (" + first_row + ") is out of bounds.");
    }

    if (last_row >= n_rows) {
      throw new IndexOutOfBoundsException("The last specified row (" + last_row + ") is out of bounds.");
    }

    if (operand.n_rows != last_row - first_row + 1 || operand.n_cols != n_cols) {
      throw new RuntimeException("The provided (" + operand.n_rows + ", " + operand.n_cols + ")-matrix must be equally in shape to a (" + (n_rows - first_row + 1) + ", " + n_cols + ")-matrix.");
    }

    new ViewSubRows(this, first_row, last_row - first_row + 1).inPlace(binary_operator, operand);
  }

  /**
   * Returns a deep copy of the specified rows.
   * <p>
   * <b>Note:</b> No explicit bounds checking handling. However, the JVM should throw IndexOutOfBoundsException
   * exceptions upon errors.
   * 
   * @param vector_of_row_indices The rows
   * 
   * @return TODO
   */
  public Mat rows(final AbstractVector vector_of_row_indices) {
    return new Mat(new ViewElemRows(this, vector_of_row_indices._data));
  }

  /**
   * Performs an in-place binary operation on the specified rows with the specified right-hand side operand.
   * <p>
   * <b>Note:</b> No explicit bounds checking handling. However, the JVM should throw IndexOutOfBoundsException
   * exceptions upon errors.
   * 
   * @param vector_of_row_indices The rows
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   */
  public void rows(final AbstractVector vector_of_row_indices, final Op binary_operator, final double operand) throws UnsupportedOperationException {
    new ViewElemRows(this, vector_of_row_indices._data).inPlace(binary_operator, operand);
  }

  /**
   * Performs an in-place binary operation on the specified rows with the specified right-hand side operand.
   * <p>
   * <b>Note:</b> No explicit bounds checking handling. However, the JVM should throw IndexOutOfBoundsException
   * exceptions upon errors.
   * 
   * @param vector_of_row_indices The rows
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   * @throws RuntimeException The provided ({@code operand.n_rows}, {@code operand.n_cols})-matrix must be equally in
   *           shape to a ({@code vector_of_row_indices.n_elem}, {@code n_cols})-matrix.
   */
  public void rows(final AbstractVector vector_of_row_indices, final Op binary_operator, final AbstractMat operand) throws UnsupportedOperationException, RuntimeException {
    if (operand.n_rows != vector_of_row_indices.n_elem || operand.n_cols != n_cols) {
      throw new RuntimeException("The provided (" + operand.n_rows + ", " + operand.n_cols + ")-matrix must be equally in shape to a (" + vector_of_row_indices.n_elem + ", " + n_rows + ")-matrix.");
    }

    new ViewElemRows(this, vector_of_row_indices._data).inPlace(binary_operator, operand);
  }

  /**
   * Returns a deep copy of the {@code first_row}th to {@code last_row} row of the {@code first_col}th to
   * {@code last_col} column.
   * 
   * @param first_row The first row
   * @param first_col The first column
   * @param last_row The last row
   * @param last_col The last column
   * 
   * @return TODO
   * 
   * @throws RuntimeException The first specified row ({@code first_row}) must be less than or equal the last specified
   *           row ({@code last_row}).
   * @throws IndexOutOfBoundsException The first specified row ({@code first_row}) is out of bounds.
   * @throws IndexOutOfBoundsException The last specified row ({@code last_row}) is out of bounds.
   * @throws RuntimeException The first specified column ({@code first_col}) must be less than or equal the last
   *           specified column ({@code last_col}).
   * @throws IndexOutOfBoundsException The first specified column ({@code first_col}) is out of bounds.
   * @throws IndexOutOfBoundsException The last specified column ({@code last_col}) is out of bounds.
   */
  public Mat submat(final int first_row, final int first_col, final int last_row, final int last_col) throws RuntimeException, IndexOutOfBoundsException {
    if (last_row < first_row) {
      throw new RuntimeException("The first specified row (" + first_row + ") must be less than or equal the last specified row (" + last_row + ").");
    }

    if (first_row < 0) {
      throw new IndexOutOfBoundsException("The first specified row (" + first_row + ") is out of bounds.");
    }

    if (last_row >= n_rows) {
      throw new IndexOutOfBoundsException("The last specified row (" + last_row + ") is out of bounds.");
    }

    if (last_col < first_col) {
      throw new RuntimeException("The first specified column (" + first_col + ") must be less than or equal the last specified column (" + last_col + ").");
    }

    if (first_col < 0) {
      throw new IndexOutOfBoundsException("The first specified column (" + first_col + ") is out of bounds.");
    }

    if (last_col >= n_cols) {
      throw new IndexOutOfBoundsException("The last specified column (" + last_col + ") is out of bounds.");
    }

    return new Mat(new ViewSubMat(this, first_row, first_col, last_row - first_row + 1, last_col - first_col + 1));
  }

  /**
   * Performs an in-place binary operation on the {@code first_row}th to {@code last_row} row of the {@code first_col}th
   * to {@code last_col} column with the specified right-hand side operand.
   * 
   * @param first_row The first row
   * @param first_col The first column
   * @param last_row The last row
   * @param last_col The last column
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws RuntimeException The first specified row ({@code first_row}) must be less than or equal the last specified
   *           row ({@code last_row}).
   * @throws IndexOutOfBoundsException The first specified row ({@code first_row}) is out of bounds.
   * @throws IndexOutOfBoundsException The last specified row ({@code last_row}) is out of bounds.
   * @throws RuntimeException The first specified column ({@code first_col}) must be less than or equal the last
   *           specified column ({@code last_col}).
   * @throws IndexOutOfBoundsException The first specified column ({@code first_col}) is out of bounds.
   * @throws IndexOutOfBoundsException The last specified column ({@code last_col}) is out of bounds.
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   */
  public void submat(final int first_row, final int first_col, final int last_row, final int last_col, final Op binary_operator, final double operand) throws RuntimeException, IndexOutOfBoundsException, UnsupportedOperationException {
    /*
     * The parameter "binary_operator" is validated within AbstractView.inPlace(Op, double).
     */

    if (last_row < first_row) {
      throw new RuntimeException("The first specified row (" + first_row + ") must be less than or equal the last specified row (" + last_row + ").");
    }

    if (first_row < 0) {
      throw new IndexOutOfBoundsException("The first specified row (" + first_row + ") is out of bounds.");
    }

    if (last_row >= n_rows) {
      throw new IndexOutOfBoundsException("The last specified row (" + last_row + ") is out of bounds.");
    }

    if (last_col < first_col) {
      throw new RuntimeException("The first specified column (" + first_col + ") must be less than or equal the last specified column (" + last_col + ").");
    }

    if (first_col < 0) {
      throw new IndexOutOfBoundsException("The first specified column (" + first_col + ") is out of bounds.");
    }

    if (last_col >= n_cols) {
      throw new IndexOutOfBoundsException("The last specified column (" + last_col + ") is out of bounds.");
    }

    new ViewSubMat(this, first_row, first_col, last_row - first_row + 1, last_col - first_col + 1).inPlace(binary_operator, operand);
  }

  /**
   * Performs an in-place binary operation on the {@code first_row}th to {@code last_row} row of the {@code first_col}th
   * to {@code last_col} column with the specified right-hand side operand.
   * 
   * @param first_row The first row
   * @param first_col The first column
   * @param last_row The last row
   * @param last_col The last column
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws RuntimeException The first specified row ({@code first_row}) must be less than or equal the last specified
   *           row ({@code last_row}).
   * @throws IndexOutOfBoundsException The first specified row ({@code first_row}) is out of bounds.
   * @throws IndexOutOfBoundsException The last specified row ({@code last_row}) is out of bounds.
   * @throws RuntimeException The first specified column ({@code first_col}) must be less than or equal the last
   *           specified column ({@code last_col}).
   * @throws IndexOutOfBoundsException The first specified column ({@code first_col}) is out of bounds.
   * @throws IndexOutOfBoundsException The last specified column ({@code last_col}) is out of bounds.
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   * @throws RuntimeException The provided ({@code operand.n_rows}, {@code operand.n_cols})-matrix must be equally in
   *           shape to a ({@code last_row} - {@code first_row} + 1, {@code last_col} - {@code first_col} + 1)-matrix.
   */
  public void submat(final int first_row, final int first_col, final int last_row, final int last_col, final Op binary_operator, final AbstractMat operand) throws RuntimeException, IndexOutOfBoundsException, UnsupportedOperationException {
    /*
     * The parameter "binary_operator" is validated within AbstractView.inPlace(Op, AbstractMat).
     */

    if (last_row < first_row) {
      throw new RuntimeException("The first specified row (" + first_row + ") must be less than or equal the last specified row (" + last_row + ").");
    }

    if (first_row < 0) {
      throw new IndexOutOfBoundsException("The first specified row (" + first_row + ") is out of bounds.");
    }

    if (last_row >= n_rows) {
      throw new IndexOutOfBoundsException("The last specified row (" + last_row + ") is out of bounds.");
    }

    if (last_col < first_col) {
      throw new RuntimeException("The first specified column (" + first_col + ") must be less than or equal the last specified column (" + last_col + ").");
    }

    if (first_col < 0) {
      throw new IndexOutOfBoundsException("The first specified column (" + first_col + ") is out of bounds.");
    }

    if (last_col >= n_cols) {
      throw new IndexOutOfBoundsException("The last specified column (" + last_col + ") is out of bounds.");
    }

    if (operand.n_rows != last_row - first_row + 1 || operand.n_cols != last_col - first_col + 1) {
      throw new RuntimeException("The provided (" + operand.n_rows + ", " + operand.n_cols + ")-matrix must be equally in shape to a (" + (last_row - first_row + 1) + ", " + (last_col - first_col + 1) + ")-matrix.");
    }

    new ViewSubMat(this, first_row, first_col, last_row - first_row + 1, last_col - first_col + 1).inPlace(binary_operator, operand);
  }

  /**
   * Returns a deep copy of the {@code row_span._first}th to {@code row_span._last} row of the {@code col_span._first}th
   * to {@code col_span._last} column.
   * 
   * @param row_span The row span
   * @param col_span The column span
   * 
   * @return TODO
   * 
   * @throws IndexOutOfBoundsException The first specified row ({@code first_row}) is out of bounds.
   * @throws IndexOutOfBoundsException The last specified row ({@code last_row}) is out of bounds.
   * @throws IndexOutOfBoundsException The first specified column ({@code first_col}) is out of bounds.
   * @throws IndexOutOfBoundsException The last specified column ({@code last_col}) is out of bounds.
   */
  public Mat submat(final Span row_span, final Span col_span) throws IndexOutOfBoundsException {
    /*
     * The parameters "row_span" and "col_span" were already validated during their instantiation.
     */
    if (row_span._isEntireRange && col_span._isEntireRange) {
      return new Mat(this);
    } else if (row_span._isEntireRange) {
      return cols(col_span._first, col_span._last);
    } else if (col_span._isEntireRange) {
      return rows(row_span._first, row_span._last);
    } else {
      return submat(row_span._first, col_span._first, row_span._last, col_span._last);
    }
  }

  /**
   * Performs an in-place binary operation on the {@code row_span._first}th to {@code row_span._last} row of the
   * {@code col_span._first}th to {@code col_span._last} column with the specified right-hand side operand.
   * 
   * @param row_span The row span
   * @param col_span The column span
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws IndexOutOfBoundsException The first specified row ({@code first_row}) is out of bounds.
   * @throws IndexOutOfBoundsException The last specified row ({@code last_row}) is out of bounds.
   * @throws IndexOutOfBoundsException The first specified column ({@code first_col}) is out of bounds.
   * @throws IndexOutOfBoundsException The last specified column ({@code last_col}) is out of bounds.
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   */
  public void submat(final Span row_span, final Span col_span, final Op binary_operator, final double operand) throws IndexOutOfBoundsException, UnsupportedOperationException {
    /*
     * The parameters "row_span" and "col_span" were already validated during their instantiation.
     */
    if (row_span._isEntireRange && col_span._isEntireRange) {
      inPlace(binary_operator, operand);
    } else if (row_span._isEntireRange) {
      cols(col_span._first, col_span._last, binary_operator, operand);
    } else if (col_span._isEntireRange) {
      rows(row_span._first, row_span._last, binary_operator, operand);
    } else {
      submat(row_span._first, col_span._first, row_span._last, col_span._last, binary_operator, operand);
    }
  }

  /**
   * Performs an in-place binary operation on the {@code row_span._first}th to {@code row_span._last} row of the
   * {@code col_span._first}th to {@code col_span._last} column with the specified right-hand side operand.
   * 
   * @param row_span The row span
   * @param col_span The column span
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws IndexOutOfBoundsException The first specified row ({@code first_row}) is out of bounds.
   * @throws IndexOutOfBoundsException The last specified row ({@code last_row}) is out of bounds.
   * @throws IndexOutOfBoundsException The first specified column ({@code first_col}) is out of bounds.
   * @throws IndexOutOfBoundsException The last specified column ({@code last_col}) is out of bounds.
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   * @throws RuntimeException The provided ({@code operand.n_rows}, {@code operand.n_cols})-matrix must be equally in
   *           shape to a ({@code last_row} - {@code first_row} + 1, {@code last_col} - {@code first_col} + 1)-matrix.
   */
  public void submat(final Span row_span, final Span col_span, final Op binary_operator, final AbstractMat operand) throws IndexOutOfBoundsException, UnsupportedOperationException, RuntimeException {
    /*
     * The parameters "row_span" and "col_span" were already validated during their instantiation.
     */
    if (row_span._isEntireRange && col_span._isEntireRange) {
      inPlace(binary_operator, operand);
    } else if (row_span._isEntireRange) {
      cols(col_span._first, col_span._last, binary_operator, operand);
    } else if (col_span._isEntireRange) {
      rows(row_span._first, row_span._last, binary_operator, operand);
    } else {
      submat(row_span._first, col_span._first, row_span._last, col_span._last, binary_operator, operand);
    }
  }

  /**
   * Returns a deep copy starting at position ({@code first_row}, {@code first_col}) of {@code size.n_rows} rows and
   * {@code size.n_cols} columns.
   * 
   * @param first_row The first row
   * @param first_col The first column
   * @param size The size
   * 
   * @return TODO
   * 
   * @throws IndexOutOfBoundsException The first specified row ({@code first_row}) is out of bounds.
   * @throws IndexOutOfBoundsException The last specified row ({@code last_row}) is out of bounds.
   * @throws IndexOutOfBoundsException The first specified column ({@code first_col}) is out of bounds.
   * @throws IndexOutOfBoundsException The last specified column ({@code last_col}) is out of bounds.
   */
  public Mat submat(final int first_row, final int first_col, final Size size) throws IndexOutOfBoundsException {
    /*
     * The parameter "size" was already validated during its instantiation.
     */
    return submat(first_row, first_col, first_row + size.n_rows - 1, first_col + size.n_cols - 1);
  }

  /**
   * Performs an in-place binary operation on the position ({@code first_row}, {@code first_col}) of {@code size.n_rows}
   * rows and {@code size.n_cols} columns with the specified right-hand side operand.
   * 
   * @param first_row The first row
   * @param first_col The first column
   * @param size The size
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws IndexOutOfBoundsException The first specified row ({@code first_row}) is out of bounds.
   * @throws IndexOutOfBoundsException The last specified row ({@code last_row}) is out of bounds.
   * @throws IndexOutOfBoundsException The first specified column ({@code first_col}) is out of bounds.
   * @throws IndexOutOfBoundsException The last specified column ({@code last_col}) is out of bounds.
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   */
  public void submat(final int first_row, final int first_col, final Size size, final Op binary_operator, final double operand) throws IndexOutOfBoundsException, UnsupportedOperationException {
    /*
     * The parameter "size" was already validated during its instantiation.
     */
    submat(first_row, first_col, first_row + size.n_rows - 1, first_col + size.n_cols - 1, binary_operator, operand);
  }

  /**
   * Performs an in-place binary operation on the position ({@code first_row}, {@code first_col}) of {@code size.n_rows}
   * rows and {@code size.n_cols} columns with the specified right-hand side operand.
   * 
   * @param first_row The first row
   * @param first_col The first column
   * @param size The size
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws IndexOutOfBoundsException The first specified row ({@code first_row}) is out of bounds.
   * @throws IndexOutOfBoundsException The last specified row ({@code last_row}) is out of bounds.
   * @throws IndexOutOfBoundsException The first specified column ({@code first_col}) is out of bounds.
   * @throws IndexOutOfBoundsException The last specified column ({@code last_col}) is out of bounds.
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   * @throws RuntimeException The provided ({@code operand.n_rows}, {@code operand.n_cols})-matrix must be equally in
   *           shape to a ({@code last_row} - {@code first_row} + 1, {@code last_col} - {@code first_col} + 1)-matrix.
   */
  public void submat(final int first_row, final int first_col, final Size size, final Op binary_operator, final AbstractMat operand) throws IndexOutOfBoundsException, UnsupportedOperationException, RuntimeException {
    /*
     * The parameter "size" was already validated during its instantiation.
     */
    submat(first_row, first_col, first_row + size.n_rows - 1, first_col + size.n_cols - 1, binary_operator, operand);
  }

  /**
   * Returns a deep copy of the specified rows of the specified columns.
   * <p>
   * <b>Note:</b> No explicit bounds checking handling. However, the JVM should throw IndexOutOfBoundsException
   * exceptions upon errors.
   * 
   * @param vector_of_row_indices The rows
   * @param vector_of_column_indices The columns
   * 
   * @return TODO
   */
  public Mat submat(final AbstractVector vector_of_row_indices, final AbstractVector vector_of_column_indices) {
    return new Mat(new ViewElemSubMat(this, vector_of_row_indices._data, vector_of_column_indices._data));
  }

  /**
   * Performs an in-place binary operation on the specified rows of the specified columns with the specified right-hand
   * side operand.
   * <p>
   * <b>Note:</b> No explicit error handling. However, the JVM should throw IndexOutOfBoundsException exceptions upon
   * errors.
   * 
   * @param vector_of_row_indices The rows
   * @param vector_of_column_indices The columns
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   */
  public void submat(final AbstractVector vector_of_row_indices, final AbstractVector vector_of_column_indices, final Op binary_operator, final double operand) throws UnsupportedOperationException {
    new ViewElemSubMat(this, vector_of_row_indices._data, vector_of_column_indices._data).inPlace(binary_operator, operand);
  }

  /**
   * Performs an in-place binary operation on the specified rows of the specified columns with the specified right-hand
   * side operand.
   * <p>
   * <b>Note:</b> No explicit bounds checking handling. However, the JVM should throw IndexOutOfBoundsException
   * exceptions upon errors.
   * 
   * @param vector_of_row_indices The rows
   * @param vector_of_column_indices The columns
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   * @throws RuntimeException The provided ({@code operand.n_rows}, {@code operand.n_cols})-matrix must be equally in
   *           shape to a ({@code vector_of_row_indices.n_elem}, {@code vector_of_column_indices.n_elem})-matrix.
   */
  public void submat(final AbstractVector vector_of_row_indices, final AbstractVector vector_of_column_indices, final Op binary_operator, final AbstractMat operand) throws UnsupportedOperationException, RuntimeException {
    if (operand.n_rows != vector_of_row_indices.n_elem || operand.n_cols != vector_of_column_indices.n_elem) {
      throw new RuntimeException("The provided (" + operand.n_rows + ", " + operand.n_cols + ")-matrix must be equally in shape to a (" + vector_of_row_indices.n_elem + ", " + vector_of_column_indices.n_elem + ")-matrix.");
    }

    new ViewElemSubMat(this, vector_of_row_indices._data, vector_of_column_indices._data).inPlace(binary_operator, operand);
  }

  /**
   * Returns a deep copy of the main diagonal.
   * 
   * @return TODO
   */
  public Col diag() {
    return new Col(new ViewDiag(this, 0));
  }

  /**
   * Performs an in-place binary operation on the main diagonal with the specified right-hand side operand.
   * 
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   */
  public void diag(final Op binary_operator, final double operand) throws UnsupportedOperationException {
    /*
     * The parameter "binary_operator" is validated within AbstractView.inPlace(Op, double).
     */
    new ViewDiag(this, 0).inPlace(binary_operator, operand);
  }

  /**
   * Performs an in-place binary operation on the main diagonal with the specified right-hand side operand.
   * 
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   * @throws RuntimeException The provided ({@code operand.n_rows}, {@code operand.n_cols})-matrix must be in vector
   *           shape and have exactly Math.min({@code n_rows}, {@code n_cols}) elements.
   */
  public void diag(final Op binary_operator, final AbstractMat operand) throws UnsupportedOperationException, RuntimeException {
    /*
     * The parameter "binary_operator" is validated within AbstractView.inPlace(Op, AbstractMat).
     */

    if (!operand.is_vec() || operand.n_elem != Math.min(n_rows, n_cols)) {
      throw new RuntimeException("The provided (" + operand.n_rows + ", " + operand.n_cols + ")-matrix must be in vector shape and have exactly " + Math.min(n_rows, n_cols) + " elements.");
    }

    new ViewDiag(this, 0).inPlace(binary_operator, operand);
  }

  /**
   * Returns a deep copy of the {@code k}th diagonal.
   * <ul>
   * <li>For {@code k} = 0, its the main diagonal.
   * <li>For {@code k} &gt; 0, its the {@code k}th super-diagonal.
   * <li>For {@code k} &lt; 0, its the {@code k}th sub-diagonal.
   * </ul>
   * 
   * @param k The diagonal position
   * 
   * @return TODO
   * 
   * @throws IndexOutOfBoundsException The specified diagonal index ({@code k}) is out of bounds.
   */
  public Col diag(final int k) throws IndexOutOfBoundsException {
    /*
     * The parameter "binary_operator" is validated within AbstractView.inPlace(Op, AbstractMat).
     */

    if (k > 0 && k >= n_cols) {
      throw new IndexOutOfBoundsException("The specified diagonal index (" + k + ") is out of bounds.");
    }

    if (k < 0 && -k >= n_rows) {
      throw new IndexOutOfBoundsException("The specified diagonal index (" + k + ") is out of bounds.");
    }

    return new Col(new ViewDiag(this, k));
  }

  /**
   * Performs an in-place binary operation on the {@code k}th diagonal with the specified right-hand side operand.
   * <ul>
   * <li>For {@code k} = 0, its the main diagonal.
   * <li>For {@code k} &gt; 0, its the {@code k}th super-diagonal.
   * <li>For {@code k} &lt; 0, its the {@code k}th sub-diagonal.
   * </ul>
   * 
   * @param k The diagonal position
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws IndexOutOfBoundsException The specified diagonal index ({@code k}) is out of bounds.
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   */
  public void diag(final int k, final Op binary_operator, final double operand) throws IndexOutOfBoundsException, UnsupportedOperationException {
    /*
     * The parameter "binary_operator" is validated within AbstractView.inPlace(Op, AbstractMat).
     */

    if (k > 0 && k >= n_cols) {
      throw new IndexOutOfBoundsException("The specified diagonal index (" + k + ") is out of bounds.");
    }

    if (k < 0 && -k >= n_rows) {
      throw new IndexOutOfBoundsException("The specified diagonal index (" + k + ") is out of bounds.");
    }

    new ViewDiag(this, k).inPlace(binary_operator, operand);
  }

  /**
   * Performs an in-place binary operation on the {@code k}th diagonal with the specified right-hand side operand.
   * <ul>
   * <li>For {@code k} = 0, its the main diagonal.
   * <li>For {@code k} &gt; 0, its the {@code k}th super-diagonal.
   * <li>For {@code k} &lt; 0, its the {@code k}th sub-diagonal.
   * </ul>
   * 
   * @param k The diagonal position
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws IndexOutOfBoundsException The specified diagonal index ({@code k}) is out of bounds.
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   * @throws RuntimeException The provided ({@code operand.n_rows}, {@code operand.n_cols})-matrix must be in vector
   *           shape and have exactly Math.min({@code n_rows}, {@code n_cols} - {@code k}) elements.
   * @throws RuntimeException The provided ({@code operand.n_rows}, {@code operand.n_cols})-matrix must be in vector
   *           shape and have exactly Math.min({@code n_rows} + {@code k}, {@code n_cols}) elements.
   */
  public void diag(final int k, final Op binary_operator, final AbstractMat operand) throws IndexOutOfBoundsException, UnsupportedOperationException {
    /*
     * The parameter "binary_operator" is validated within AbstractView.inPlace(Op, AbstractMat).
     */

    if (k > 0) {
      if (k >= n_cols) {
        throw new IndexOutOfBoundsException("The specified diagonal index (" + k + ") is out of bounds.");
      }

      if (!operand.is_vec() || operand.n_elem != Math.min(n_rows, n_cols - k)) {
        throw new RuntimeException("The provided (" + operand.n_rows + ", " + operand.n_cols + ")-matrix must be in vector shape and have exactly " + Math.min(n_rows, n_cols - k) + " elements.");
      }
    }

    if (k < 0) {
      if (-k >= n_rows) {
        throw new IndexOutOfBoundsException("The specified diagonal index (" + k + ") is out of bounds.");
      }

      if (!operand.is_vec() || operand.n_elem != Math.min(n_rows + k, n_cols)) {
        throw new RuntimeException("The provided (" + operand.n_rows + ", " + operand.n_cols + ")-matrix must be in vector shape and have exactly " + Math.min(n_rows + k, n_cols) + " elements.");
      }
    }

    new ViewDiag(this, k).inPlace(binary_operator, operand);
  }

  /**
   * Performs an in-place binary operation on each column of the matrix individually with the specified right-hand side
   * operand.
   * 
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   */
  public void each_col(final Op binary_operator, final AbstractMat operand) throws UnsupportedOperationException {
    /*
     * The parameter "binary_operator" is validated within AbstractView.inPlace(Op, AbstractMat).
     */

    for (int j = 0; j < n_cols; j++) {
      col(j, binary_operator, operand);
    }
  }

  /**
   * Performs an in-place binary operation on each specified column of the matrix individually with the specified
   * right-hand side operand.
   * <p>
   * <b>Note:</b> No explicit error handling. However, the JVM should throw IndexOutOfBoundsException exceptions upon
   * errors.
   * 
   * @param vector_of_indices The column positions
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   */
  public void each_col(final AbstractVector vector_of_indices, final Op binary_operator, final double operand) throws UnsupportedOperationException {
    /*
     * The parameter "binary_operator" is validated within AbstractView.inPlace(Op, AbstractMat).
     */
    cols(vector_of_indices, binary_operator, operand);
  }

  /**
   * Performs an in-place binary operation on each specified column of the matrix individually with the specified
   * right-hand side operand.
   * <p>
   * <b>Note:</b> No explicit error handling. However, the JVM should throw IndexOutOfBoundsException exceptions upon
   * errors.
   * 
   * @param vector_of_indices The column positions
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   */
  public void each_col(final AbstractVector vector_of_indices, final Op binary_operator, final AbstractMat operand) throws UnsupportedOperationException {
    /*
     * The parameter "binary_operator" is validated within AbstractView.inPlace(Op, AbstractMat).
     */

    for (int n = 0; n < vector_of_indices.n_elem; n++) {
      col((int) vector_of_indices._data[n], binary_operator, operand);
    }
  }

  /**
   * Performs an in-place binary operation on each row of the matrix individually with the specified right-hand side
   * operand.
   * 
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   */
  public void each_row(final Op binary_operator, final AbstractMat operand) throws UnsupportedOperationException {
    /*
     * The parameter "binary_operator" is validated within AbstractView.inPlace(Op, AbstractMat).
     */

    for (int i = 0; i < n_rows; i++) {
      row(i, binary_operator, operand);
    }
  }

  /**
   * Performs an in-place binary operation on each specified row of the matrix individually with the specified
   * right-hand side operand.
   * <p>
   * <b>Note:</b> No explicit error handling. However, the JVM should throw IndexOutOfBoundsException exceptions upon
   * errors.
   * 
   * @param vector_of_indices The row positions
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   */
  public void each_row(final AbstractVector vector_of_indices, final Op binary_operator, final double operand) throws UnsupportedOperationException {
    /*
     * The parameter "binary_operator" is validated within AbstractView.inPlace(Op, AbstractMat).
     */
    rows(vector_of_indices, binary_operator, operand);
  }

  /**
   * Performs an in-place binary operation on each specified row of the matrix individually with the specified
   * right-hand side operand.
   * <p>
   * <b>Note:</b> No explicit error handling. However, the JVM should throw IndexOutOfBoundsException exceptions upon
   * errors.
   * 
   * @param vector_of_indices The row positions
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws UnsupportedOperationException Unexpected operator ({@code binary_operator}).
   */
  public void each_row(final AbstractVector vector_of_indices, final Op binary_operator, final AbstractMat operand) throws UnsupportedOperationException {
    /*
     * The parameter "binary_operator" is validated within AbstractView.inPlace(Op, AbstractMat).
     */

    for (int n = 0; n < vector_of_indices.n_elem; n++) {
      row((int) vector_of_indices._data[n], binary_operator, operand);
    }
  }

  /**
   * Returns the value of the element at the {@code i}th row and {@code j}th column.
   * 
   * @param i The row position
   * @param j The column position
   * 
   * @return TODO
   */
  public double at(final int i, final int j) {
    return _data[i + j * n_rows];
  }

  /**
   * Performs an in-place unary operation on the element at the {@code i}th row and {@code j}th column.
   * <p>
   * <b>Note:</b> Index checking is not enforced. However, the JVM should throw IndexOutOfBoundsException exceptions
   * upon errors.
   * 
   * @param i The row position
   * @param j The column position
   * @param unary_operator The unary operator
   * 
   * @throws UnsupportedOperationException Unsupported operator {@code unary_operator}.
   */
  public void at(final int i, final int j, final Op unary_operator) throws UnsupportedOperationException {
    switch (unary_operator) {
      case INCREMENT:
        _data[i + j * n_rows]++;
        break;
      case DECREMENT:
        _data[i + j * n_rows]--;
        break;
      default:
        throw new UnsupportedOperationException("Unsupported operator " + unary_operator + ".");
    }
  }

  /**
   * Performs an in-place binary operation on the element at the {@code i}th row and {@code j}th column with the
   * specified right-hand side operand.
   * <p>
   * <b>Note:</b> Index checking is not enforced. However, the JVM should throw IndexOutOfBoundsException exceptions
   * upon errors.
   * 
   * @param i The row position
   * @param j The column position
   * @param binary_operator The binary operator
   * @param operand The operand
   * 
   * @throws UnsupportedOperationException Unsupported operator {@code binary_operator}.
   */
  public void at(final int i, final int j, final Op binary_operator, double operand) throws UnsupportedOperationException {
    switch (binary_operator) {
      case EQUAL:
        _data[i + j * n_rows] = operand;
        break;
      case PLUS:
        _data[i + j * n_rows] += operand;
        break;
      case MINUS:
        _data[i + j * n_rows] -= operand;
        break;
      case TIMES:
        _data[i + j * n_rows] *= operand;
        break;
      case DIVIDE:
        _data[i + j * n_rows] /= operand;
        break;
      default:
        throw new UnsupportedOperationException("Unsupported operator " + binary_operator + ".");
    }
  }

  /**
   * Sets all elements along the main diagonal to 1 and all others to 0.
   */
  public void eye() {
    _data = new double[n_elem];
    /*
     * All entries of an array are already set to 0 during creation.
     * 
     * See http://docs.oracle.com/javase/specs/jls/se7/html/jls-10.html#jls-10.3
     * and http://docs.oracle.com/javase/specs/jls/se7/html/jls-4.html#jls-4.12.5
     */

    int length = Math.min(n_rows, n_cols);
    for (int n = 0; n < length; n++) {
      _data[n + n * n_rows] = 1;
    }
  }

  /**
   * Resizes the matrix to the specified number of rows and columns and sets all elements along the main diagonal to 1
   * and all others to 0.
   * 
   * @param n_rows The number of rows
   * @param n_cols The number of columns
   * 
   * @throws NegativeArraySizeException The specified number of rows ({@code n_rows}) must be positive.
   * @throws NegativeArraySizeException The specified number of columns ({@code n_cols}) must be positive.
   */
  public void eye(final int n_rows, final int n_cols) throws NegativeArraySizeException {
    /*
     * The parameters "n_rows" and "n_cols" are validated within set_size(int, int).
     */

    set_size(n_rows, n_cols);
    fill(0);

    int length = Math.min(n_rows, n_cols);
    for (int n = 0; n < length; n++) {
      _data[n + n * n_rows] = 1;
    }
  }

  /**
   * Returns the inverse.
   * 
   * @return TODO
   * 
   * @throws RuntimeException The ({@code n_rows}, {@code n_cols})-matrix must be square.
   * @throws RuntimeException The matrix appears to be singular.
   */
  public Mat i() throws RuntimeException {
    if (!is_square()) {
      throw new RuntimeException("The (" + n_rows + ", " + n_cols + ")-matrix must be square.");
    }

    Mat inverse = new Mat(this);
    int[] pivotIndices = new int[Math.min(n_rows, n_cols)];
    intW info = new intW(0);

    LAPACK.getInstance().dgetrf(n_rows, n_cols, inverse._data, n_rows, pivotIndices, info);
    if (info.val != 0) {
      throw new RuntimeException("The matrix appears to be singular.");
    }

    double[] temp = new double[n_cols];
    LAPACK.getInstance().dgetri(n_rows, inverse._data, n_rows, pivotIndices, temp, n_cols, info);
    if (info.val != 0) {
      throw new RuntimeException("The matrix appears to be singular.");
    }

    return inverse;
  }

  /**
   * Returns {@code true} if the number of rows equals the number of columns.
   * 
   * @return TODO
   */
  public boolean is_square() {
    return (n_rows == n_cols);
  }

  @Override
  public boolean is_vec() {
    return (is_colvec() || is_rowvec());
  }

  @Override
  public boolean is_colvec() {
    return (n_cols == 1);
  }

  @Override
  public boolean is_rowvec() {
    return (n_rows == 1);
  }

  /**
   * Inserts the rows from {@code X} at row position {@code row_number}.
   * 
   * @param row_number The row position
   * @param X The column vector
   * 
   * @throws IndexOutOfBoundsException The specified row position ({@code row_number}) is out of bounds.
   * @throws RuntimeException Both matrices must have the same number of columns ({@code A.n_cols} and {@code B.n_cols}
   *           ).
   */
  public void insert_rows(final int row_number, final AbstractMat X) throws IndexOutOfBoundsException, RuntimeException {
    if (row_number < 0 || row_number > n_rows) {
      throw new IndexOutOfBoundsException("The specified row position (" + row_number + ") is out of bounds.");
    }

    if (X.is_empty()) {
      return; // Nothing to do here.
    } else if (is_empty()) {
      copy_size(X);
      System.arraycopy(X._data, 0, _data, 0, X.n_elem);
    } else {
      if (n_cols != X.n_cols) {
        throw new RuntimeException("Both matrices must have the same number of columns (" + n_cols + " and " + X.n_cols + ").");
      }

      Mat temp = new Mat(this);
      set_size(n_rows + X.n_rows, n_cols);

      new ViewSubRows(this, 0, row_number).inPlace(Op.EQUAL, new ViewSubRows(temp, 0, row_number));
      new ViewSubRows(this, row_number, X.n_rows).inPlace(Op.EQUAL, X);
      /*
       * The attribute "n_rows" has been updated by set_size(int, int).
       */
      new ViewSubRows(this, row_number + X.n_rows, n_rows - (row_number + X.n_rows)).inPlace(Op.EQUAL, new ViewSubRows(temp, row_number, temp.n_rows - row_number));
    }
  }

  /**
   * Inserts {@code number_of_rows} uninitialised rows at row position {@code row_number}.
   * 
   * @param row_number The row position
   * @param number_of_rows The number of rows
   * 
   * @throws NegativeArraySizeException The specified number of rows ({@code number_of_rows}) must be positive.
   * @throws IndexOutOfBoundsException The specified row position ({@code row_number}) is out of bounds.
   */
  public void insert_rows(final int row_number, final int number_of_rows) throws NegativeArraySizeException, IndexOutOfBoundsException {
    /*
     * The parameter "number_of_rows" is validated within set_size(int, int).
     */

    if (number_of_rows < 0) {
      throw new NegativeArraySizeException("The specified number of rows (" + number_of_rows + ") must be positive.");
    }

    if (row_number < 0 || row_number > n_rows) {
      throw new IndexOutOfBoundsException("The specified row position (" + row_number + ") is out of bounds.");
    }

    if (number_of_rows == 0) {
      return; // Nothing to do here.
    } else if (is_empty()) {
      set_size(number_of_rows, 1);
    } else {
      Mat temp = new Mat(this);
      set_size(n_rows + number_of_rows, n_cols);

      new ViewSubRows(this, 0, row_number).inPlace(Op.EQUAL, new ViewSubRows(temp, 0, row_number));
      /*
       * The attribute "n_rows" has been updated by set_size(int, int).
       */
      new ViewSubRows(this, row_number + number_of_rows, n_rows - (row_number + number_of_rows)).inPlace(Op.EQUAL, new ViewSubRows(temp, row_number, temp.n_rows - row_number));
    }
  }

  /**
   * Inserts {@code number_of_rows} rows at row position {@code row_number}.
   * <p>
   * All elements will be set to 0 ({@code set_to_zero} = true) or left uninitialised.
   * 
   * @param row_number The row position
   * @param number_of_rows The number of rows
   * @param set_to_zero Whether the inserted elements are to be set to 0
   * 
   * @throws NegativeArraySizeException The specified number of rows ({@code number_of_rows}) must be positive.
   * @throws IndexOutOfBoundsException The specified row position ({@code row_number}) is out of bounds.
   */
  public void insert_rows(final int row_number, final int number_of_rows, final boolean set_to_zero) throws NegativeArraySizeException, IndexOutOfBoundsException {
    /*
     * The parameter "number_of_rows" is validated within set_size(int, int).
     */

    /*
     * All entries of an array are already set to 0 during creation.
     * Therefore, set_to_zero will be ignored.
     * 
     * See http://docs.oracle.com/javase/specs/jls/se7/html/jls-10.html#jls-10.3
     * and http://docs.oracle.com/javase/specs/jls/se7/html/jls-4.html#jls-4.12.5
     */
    insert_rows(row_number, number_of_rows);
  }

  /**
   * Inserts the columns from {@code X} at column position {@code col_number}.
   * 
   * @param col_number The column position
   * @param X The column vector
   * 
   * @throws IndexOutOfBoundsException The column position ({@code col_number}) is out of bounds.
   * @throws RuntimeException Both matrices must have the same number of rows ({@code A.n_rows} and {@code B.n_rows} ).
   */
  public void insert_cols(final int col_number, final AbstractMat X) throws IndexOutOfBoundsException, RuntimeException {
    if (col_number < 0 || col_number > n_cols) {
      throw new IndexOutOfBoundsException("The column position (" + col_number + ") is out of bounds.");
    }

    if (X.is_empty()) {
      return; // Nothing to do here.
    } else if (is_empty()) {
      copy_size(X);
      System.arraycopy(X._data, 0, _data, 0, X.n_elem);
    } else {
      if (n_rows != X.n_rows) {
        throw new RuntimeException("Both matrices must have the same number of columns (" + n_rows + " and " + X.n_rows + ").");
      }

      double[] temp = Arrays.copyOf(_data, n_elem);
      set_size(n_rows, n_cols + X.n_cols);

      System.arraycopy(temp, 0, _data, 0, col_number * n_rows);
      System.arraycopy(X._data, 0, _data, col_number * n_rows, X.n_elem);
      System.arraycopy(temp, col_number * n_rows, _data, col_number * n_rows + X.n_elem, temp.length - col_number * n_rows);
    }
  }

  /**
   * Inserts {@code number_of_cols} uninitialised columns at column position {@code col_number}.
   * 
   * @param col_number The column position
   * @param number_of_cols The number of columns
   * 
   * @throws NegativeArraySizeException The specified number of columns ({@code number_of_cols}) must be positive.
   * @throws IndexOutOfBoundsException The column position ({@code col_number}) is out of bounds.
   */
  public void insert_cols(final int col_number, final int number_of_cols) throws NegativeArraySizeException, IndexOutOfBoundsException {
    /*
     * The parameter "number_of_rows" is validated within set_size(int, int).
     */

    if (number_of_cols < 0) {
      throw new NegativeArraySizeException("The specified number of columns (" + number_of_cols + ") must be positive.");
    }

    if (col_number < 0 || col_number > n_cols) {
      throw new IndexOutOfBoundsException("The row position (" + col_number + ") is out of bounds.");
    }

    if (number_of_cols == 0) {
      return; // Nothing to do here.
    } else if (is_empty()) {
      set_size(1, number_of_cols);
    } else {
      double[] temp = Arrays.copyOf(_data, n_elem);
      set_size(n_rows, n_cols + number_of_cols);

      System.arraycopy(temp, 0, _data, 0, col_number * n_rows);
      System.arraycopy(temp, col_number * n_rows, _data, (number_of_cols + col_number) * n_rows, temp.length - col_number * n_rows);
    }
  }

  /**
   * Inserts {@code number_of_cols} columns at column position {@code col_number}.
   * <p>
   * All elements will be set to 0 ({@code set_to_zero} = true) or left uninitialised.
   * 
   * @param col_number The column position
   * @param number_of_cols The number of columns
   * @param set_to_zero Whether the inserted elements are to be set to 0
   * 
   * @throws NegativeArraySizeException The specified number of columns ({@code number_of_cols}) must be positive.
   * @throws IndexOutOfBoundsException The row position ({@code row_number}) is out of bounds.
   */
  public void insert_cols(final int col_number, final int number_of_cols, final boolean set_to_zero) throws NegativeArraySizeException, IndexOutOfBoundsException {
    /*
     * The parameter "number_of_rows" is validated within set_size(int, int).
     */

    /*
     * All entries of an array are already set to 0 during creation.
     * Therefore, set_to_zero will be ignored.
     * 
     * See http://docs.oracle.com/javase/specs/jls/se7/html/jls-10.html#jls-10.3
     * and http://docs.oracle.com/javase/specs/jls/se7/html/jls-4.html#jls-4.12.5
     */
    insert_cols(col_number, number_of_cols);
  }

  /**
   * Returns the smallest value within the matrix and stores its row position in {@code row_of_min_val} and column
   * position in {@code col_of_min_val}.
   * <p>
   * <b>Note:</b> Unfortunately, the storage variables must be of the mutable type int[].
   * 
   * @param row_of_min_val The storage of the row position
   * @param col_of_min_val The storage of the column position
   * 
   * @return TODO
   * 
   * @throws RuntimeException The ({@code n_rows}, {@code n_cols})-matrix must have at least one element.
   */
  public double min(final int[] row_of_min_val, final int[] col_of_min_val) throws RuntimeException {
    if (is_empty()) {
      throw new RuntimeException("The (" + n_rows + ", " + n_cols + ")-matrix must have at least one element.");
    }

    double minimum = _data[0];
    row_of_min_val[0] = 0;
    col_of_min_val[0] = 0;
    int n = 0;
    for (int j = 0; j < n_cols; j++) {
      for (int i = 0; i < n_rows; i++) {
        double value = _data[n++];

        if (value < minimum) {
          minimum = value;
          row_of_min_val[0] = i;
          col_of_min_val[0] = j;
        }
      }
    }

    return minimum;
  }

  /**
   * Returns the largest value within the matrix and stores its row position in {@code row_of_min_val} and column
   * position in {@code col_of_min_val}.
   * <p>
   * <b>Note:</b> Unfortunately, the storage variables must be of the mutable type int[].
   * 
   * @param row_of_max_val The storage of the row position
   * @param col_of_max_val The storage of the column position
   * 
   * @return TODO
   * 
   * @throws RuntimeException The ({@code n_rows}, {@code n_cols})-matrix must have at least one element.
   */
  public double max(int[] row_of_max_val, int[] col_of_max_val) throws RuntimeException {
    if (is_empty()) {
      throw new RuntimeException("The (" + n_rows + ", " + n_cols + ")-matrix must have at least one element.");
    }

    double maximum = _data[0];
    row_of_max_val = new int[1];
    col_of_max_val = new int[1];

    int n = 0;
    for (int j = 1; j < n_cols; j++) {
      for (int i = 1; i < n_rows; i++) {
        double value = _data[n++];

        if (value > maximum) {
          maximum = value;
          row_of_max_val[0] = i;
          col_of_max_val[0] = j;
        }
      }
    }

    return maximum;
  }

  /**
   * Resizes the matrix to the specified number of rows and columns and sets all elements to 1.
   * 
   * @param n_rows The number of rows
   * @param n_cols The number of columns
   * 
   * @throws NegativeArraySizeException The specified number of rows ({@code n_rows}) must be positive.
   * @throws NegativeArraySizeException The specified number of columns ({@code n_cols}) must be positive.
   */
  public void ones(final int n_rows, final int n_cols) throws NegativeArraySizeException {
    /*
     * The parameters "n_rows" and "n_cols" are validated within set_size(int, int).
     */

    set_size(n_rows, n_cols);
    fill(1);
  }

  /**
   * Resizes the matrix to the specified number of rows and columns and sets each element to a pseudo-random value drawn
   * from the standard uniform distribution on the left-closed and right-open interval [0,1).
   * <p>
   * <b>Non-canonical:</b> Drawn from [0,1) instead of the closed interval [0,1].
   * 
   * @param n_rows The number of rows
   * @param n_cols The number of columns
   * 
   * @throws NegativeArraySizeException The specified number of rows ({@code n_rows}) must be positive.
   * @throws NegativeArraySizeException The specified number of columns ({@code n_cols}) must be positive.
   */
  public void randu(final int n_rows, final int n_cols) throws NegativeArraySizeException {
    /*
     * The parameters "n_rows" and "n_cols" are validated within set_size(int, int).
     */

    set_size(n_rows, n_cols);
    for (int n = 0; n < n_elem; n++) {
      _data[n] = RNG._rng.nextDouble();
    }
  }

  /**
   * Resizes the matrix to the specified number of rows and columns and sets each element to a pseudo-random value drawn
   * from the standard normal distribution with mean 0.0 and standard deviation 1.0.
   * 
   * @param n_rows The number of rows
   * @param n_cols The number of columns
   * 
   * @throws NegativeArraySizeException The specified number of rows ({@code n_rows}) must be positive.
   * @throws NegativeArraySizeException The specified number of columns ({@code n_cols}) must be positive.
   */
  public void randn(final int n_rows, final int n_cols) throws NegativeArraySizeException {
    /*
     * The parameters "n_rows" and "n_cols" are validated within set_size(int, int).
     */

    set_size(n_rows, n_cols);
    for (int n = 0; n < n_elem; n++) {
      _data[n] = RNG._rng.nextGaussian();
    }
  }

  /**
   * Resizes the matrix to the specified number of rows and columns and sets all elements to 0.
   * 
   * @param n_rows The number of rows
   * @param n_cols The number of columns
   * 
   * @throws NegativeArraySizeException The specified number of rows ({@code n_rows}) must be positive.
   * @throws NegativeArraySizeException The specified number of columns ({@code n_cols}) must be positive.
   */
  public void zeros(final int n_rows, final int n_cols) throws NegativeArraySizeException {
    /*
     * The parameters "n_rows" and "n_cols" are validated within set_size(int, int).
     */

    set_size(n_rows, n_cols);
    fill(0);
  }

  /**
   * Resizes the matrix to the specified number of rows and columns and reuses existing values in a column-wise manner.
   * 
   * @param n_rows The number of rows
   * @param n_cols The number of columns
   * 
   * @throws NegativeArraySizeException The specified number of rows ({@code n_rows}) must be positive.
   * @throws NegativeArraySizeException The specified number of columns ({@code n_cols}) must be positive.
   */
  public void reshape(final int n_rows, final int n_cols) throws NegativeArraySizeException {
    /*
     * The parameters "n_rows" and "n_cols" are validated within set_size(int, int).
     */

    double[] temp = Arrays.copyOf(_data, Math.min(n_elem, n_rows * n_cols));
    set_size(n_rows, n_cols);
    System.arraycopy(temp, 0, _data, 0, temp.length);
  }

  /**
   * Resizes the matrix to the specified number of rows and columns and preserves existing values at their current
   * position.
   * 
   * @param n_rows The number of rows
   * @param n_cols The number of columns
   * 
   * @throws NegativeArraySizeException The specified number of rows ({@code n_rows}) must be positive.
   * @throws NegativeArraySizeException The specified number of columns ({@code n_cols}) must be positive.
   */
  public void resize(final int n_rows, final int n_cols) throws NegativeArraySizeException {
    /*
     * The parameters "n_rows" and "n_cols" are validated within set_size(int, int).
     */

    Mat temp = new Mat(this);
    set_size(n_rows, n_cols);

    int min_n_rows = Math.min(n_rows, temp.n_rows);
    int min_n_cols = Math.min(n_cols, temp.n_cols);

    for (int j = 0; j < min_n_cols; j++) {
      for (int i = 0; i < min_n_rows; i++) {
        _data[i + j * n_rows] = temp._data[i + j * temp.n_rows];
      }
    }
  }

  /**
   * Resizes the vector to the specified number of rows and columns.
   * <p>
   * If the requested size is equal to the current size, the existing memory is reused. Otherwise, new memory will be
   * allocated and left uninitialised.
   * 
   * @param n_rows The number of rows
   * @param n_cols The number of columns
   * 
   * @throws NegativeArraySizeException The specified number of rows ({@code n_rows}) must be positive.
   * @throws NegativeArraySizeException The specified number of columns ({@code n_cols}) must be positive.
   */
  public void set_size(final int n_rows, final int n_cols) throws NegativeArraySizeException {
    if (n_rows < 0) {
      throw new NegativeArraySizeException("The specified number of rows (" + n_rows + ") must be positive.");
    }

    if (n_cols < 0) {
      throw new NegativeArraySizeException("The specified number of columns (" + n_cols + ") must be positive.");
    }

    if (n_rows != this.n_rows || n_cols != this.n_cols || n_elem == 0) {
      if (n_rows > 0 && n_cols > 0) {
        this.n_rows = n_rows;
        this.n_cols = n_cols;
      } else {
        this.n_rows = 0;
        this.n_cols = 0;
      }

      this.n_elem = this.n_rows * this.n_cols;

      _data = new double[this.n_elem];
    }
  }

  /**
   * Removes the {@code row_number}th row.
   * 
   * @param row_number The row
   * 
   * @throws IndexOutOfBoundsException The specified row ({@code row_number}) is out of bound.
   */
  public void shed_row(final int row_number) throws IndexOutOfBoundsException {
    if (!in_range(row_number)) {
      throw new IndexOutOfBoundsException("The specified row (" + row_number + ") is out of bound.");
    }

    Mat temp = new Mat(this);

    set_size(n_rows - 1, n_cols);

    new ViewSubRows(this, 0, row_number).inPlace(Op.EQUAL, new ViewSubRows(temp, 0, row_number));
    /*
     * The attribute "n_rows" has been updated by set_size(int, int).
     */
    new ViewSubRows(this, row_number, n_rows - row_number).inPlace(Op.EQUAL, new ViewSubRows(temp, row_number + 1, n_rows - row_number));
  }

  /**
   * Removes all rows from the {@code first_row} to the {@code last_row} (inclusive).
   * 
   * @param first_row The first row
   * @param last_row The last row
   * 
   * @throws RuntimeException The first specified row ({@code first_row}) must be less than or equal the last specified
   *           row ({@code last_row}).
   * @throws IndexOutOfBoundsException The first specified row ({@code first_row}) is out of bound.
   * @throws IndexOutOfBoundsException The last specified row ({@code last_row}) is out of bound.
   */
  public void shed_rows(final int first_row, final int last_row) throws RuntimeException, IndexOutOfBoundsException {
    if (first_row > last_row) {
      throw new RuntimeException("The first specified row (" + first_row + ") must be less than or equal the last specified row (" + last_row + ") .");
    }

    if (!in_range(first_row)) {
      throw new IndexOutOfBoundsException("The first specified row (" + first_row + ") is out of bound.");
    }

    if (!in_range(last_row)) {
      throw new IndexOutOfBoundsException("The last specified row (" + last_row + ") is out of bound.");
    }

    Mat temp = new Mat(this);

    set_size(n_rows - (last_row - first_row + 1), n_cols);

    new ViewSubRows(this, 0, first_row).inPlace(Op.EQUAL, new ViewSubRows(temp, 0, first_row));
    /*
     * The attribute "n_rows" has been updated by set_size(int, int).
     */
    new ViewSubRows(this, first_row, n_rows - first_row).inPlace(Op.EQUAL, new ViewSubRows(temp, last_row + 1, n_rows - first_row));
  }

  /**
   * Removes the {@code col_number}th column.
   * 
   * @param col_number The column
   * 
   * @throws IndexOutOfBoundsException The specified column ({@code row_number}) is out of bound.
   */
  public void shed_col(final int col_number) throws IndexOutOfBoundsException {
    if (!in_range(col_number)) {
      throw new IndexOutOfBoundsException("The specified column (" + col_number + ") is out of bound.");
    }

    double[] temp = Arrays.copyOf(_data, n_elem);
    set_size(n_rows, n_cols - 1);

    System.arraycopy(temp, 0, _data, 0, col_number * n_rows);
    System.arraycopy(temp, (col_number + 1) * n_rows, _data, col_number * n_rows, n_elem - col_number * n_rows);
  }

  /**
   * Removes all columns from the {@code first_col} to the {@code last_col} (inclusive).
   * 
   * @param first_col The first column
   * @param last_col The last column
   * 
   * @throws RuntimeException The first specified column ({@code first_col}) must be less than or equal the last
   *           specified column ({@code last_col}).
   * @throws IndexOutOfBoundsException The first specified column ({@code first_col}) is out of bound.
   * @throws IndexOutOfBoundsException The last specified column ({@code last_col}) is out of bound.
   */
  public void shed_cols(final int first_col, final int last_col) throws RuntimeException, IndexOutOfBoundsException {
    if (first_col > last_col) {
      throw new RuntimeException("The first specified column (" + first_col + ") must be less than or equal the last specified column (" + last_col + ") .");
    }

    if (!in_range(first_col)) {
      throw new IndexOutOfBoundsException("The first specified column (" + first_col + ") is out of bound.");
    }

    if (!in_range(last_col)) {
      throw new IndexOutOfBoundsException("The last specified column (" + last_col + ") is out of bound.");
    }

    double[] temp = Arrays.copyOf(_data, n_elem);
    set_size(n_rows, n_cols - (last_col - first_col + 1));

    System.arraycopy(temp, 0, _data, 0, first_col * n_rows);
    System.arraycopy(temp, (last_col + 1) * n_rows, _data, first_col * n_rows, n_elem - first_col * n_rows);
  }

  @Override
  public void swap(final Mat X) throws RuntimeException {
    Mat temp = new Mat(this);

    copy_size(X);
    System.arraycopy(X._data, 0, _data, 0, X.n_elem);

    X.copy_size(temp);
    System.arraycopy(temp._data, 0, X._data, 0, temp.n_elem);
  }

  @Override
  public void swap(final Col X) throws RuntimeException {
    if (!is_colvec()) {
      throw new RuntimeException("The content of column vectors can only be swapped with matrices that are equivalent in shape to a column vector.");
    }

    Mat temp = new Mat(this);

    copy_size(X);
    System.arraycopy(X._data, 0, _data, 0, X.n_elem);

    X.copy_size(temp);
    System.arraycopy(temp._data, 0, X._data, 0, temp.n_elem);
  }

  @Override
  public void swap(final Row X) throws RuntimeException {
    if (!is_rowvec()) {
      throw new RuntimeException("The content of row vectors can only be swapped with matrices that are equivalent in shape to a row vector.");
    }

    Mat temp = new Mat(this);

    copy_size(X);
    System.arraycopy(X._data, 0, _data, 0, X.n_elem);

    X.copy_size(temp);
    System.arraycopy(temp._data, 0, X._data, 0, temp.n_elem);
  }

  @Override
  public Mat t() {
    Mat transpose = new Mat(n_cols, n_rows);

    if (is_vec()) {
      System.arraycopy(_data, 0, transpose._data, 0, n_elem);
    } else {
      int n = 0;
      for (int i = 0; i < transpose.n_rows; i++) {
        for (int j = 0; j < transpose.n_cols; j++) {
          transpose._data[i + j * transpose.n_rows] = _data[n++];
        }
      }
    }

    return transpose;
  }

  @Override
  public void copy_size(final AbstractMat A) {
    set_size(A.n_rows, A.n_cols);
  }

  @Override
  public Mat plus(final double X) {
    Mat result = new Mat(n_rows, n_cols);
    plus(result._data, _data, X);
    return result;
  }

  @Override
  public Mat plus(final Mat X) throws RuntimeException {
    if (n_rows != X.n_rows || n_cols != X.n_cols) {
      throw new RuntimeException("Both matrices (" + n_rows + ", " + n_cols + " and " + X.n_rows + ", " + X.n_cols + ") must have the same shape.");
    }

    Mat result = new Mat(n_rows, n_cols);
    plus(result._data, _data, X._data);
    return result;
  }

  @Override
  public Mat plus(final Col X) throws RuntimeException {
    if (n_rows != X.n_rows || n_cols != X.n_cols) {
      throw new RuntimeException("Both matrices (" + n_rows + ", " + n_cols + " and " + X.n_rows + ", " + X.n_cols + ") must have the same shape.");
    }

    Mat result = new Mat(n_rows, n_cols);
    plus(result._data, _data, X._data);
    return result;
  }

  @Override
  public Mat plus(final Row X) throws RuntimeException {
    if (n_rows != X.n_rows || n_cols != X.n_cols) {
      throw new RuntimeException("Both matrices (" + n_rows + ", " + n_cols + " and " + X.n_rows + ", " + X.n_cols + ") must have the same shape.");
    }

    Mat result = new Mat(n_rows, n_cols);
    plus(result._data, _data, X._data);
    return result;
  }

  @Override
  public Mat minus(final double X) {
    Mat result = new Mat(n_rows, n_cols);
    minus(result._data, _data, X);
    return result;
  }

  @Override
  public Mat minus(final Mat X) throws RuntimeException {
    if (n_rows != X.n_rows || n_cols != X.n_cols) {
      throw new RuntimeException("Both matrices (" + n_rows + ", " + n_cols + " and " + X.n_rows + ", " + X.n_cols + ") must have the same shape.");
    }

    Mat result = new Mat(n_rows, n_cols);
    minus(result._data, _data, X._data);
    return result;
  }

  @Override
  public Mat minus(final Col X) throws RuntimeException {
    if (n_rows != X.n_rows || n_cols != X.n_cols) {
      throw new RuntimeException("Both matrices (" + n_rows + ", " + n_cols + " and " + X.n_rows + ", " + X.n_cols + ") must have the same shape.");
    }

    Mat result = new Mat(n_rows, n_cols);
    minus(result._data, _data, X._data);
    return result;
  }

  @Override
  public Mat minus(final Row X) throws RuntimeException {
    if (n_rows != X.n_rows || n_cols != X.n_cols) {
      throw new RuntimeException("Both matrices (" + n_rows + ", " + n_cols + " and " + X.n_rows + ", " + X.n_cols + ") must have the same shape.");
    }

    Mat result = new Mat(n_rows, n_cols);
    minus(result._data, _data, X._data);
    return result;
  }

  @Override
  public Mat divide(final double X) {
    Mat result = new Mat(n_rows, n_cols);
    divide(result._data, _data, X);
    return result;
  }

  @Override
  public Mat elemDivide(final Mat X) throws RuntimeException {
    if (n_rows != X.n_rows || n_cols != X.n_cols) {
      throw new RuntimeException("Both matrices (" + n_rows + ", " + n_cols + " and " + X.n_rows + ", " + X.n_cols + ") must have the same shape.");
    }

    Mat result = new Mat(n_rows, n_cols);
    elemDivide(result._data, _data, X._data);
    return result;
  }

  @Override
  public Mat elemDivide(final Col X) throws RuntimeException {
    if (n_rows != X.n_rows || n_cols != X.n_cols) {
      throw new RuntimeException("Both matrices (" + n_rows + ", " + n_cols + " and " + X.n_rows + ", " + X.n_cols + ") must have the same shape.");
    }

    Mat result = new Mat(n_rows, n_cols);
    elemDivide(result._data, _data, X._data);
    return result;
  }

  @Override
  public Mat elemDivide(final Row X) throws RuntimeException {
    if (n_rows != X.n_rows || n_cols != X.n_cols) {
      throw new RuntimeException("Both matrices (" + n_rows + ", " + n_cols + " and " + X.n_rows + ", " + X.n_cols + ") must have the same shape.");
    }

    Mat result = new Mat(n_rows, n_cols);
    elemDivide(result._data, _data, X._data);
    return result;
  }

  @Override
  public Mat times(final double X) {
    Mat result = new Mat(n_rows, n_cols);
    times(result._data, _data, X);
    return result;
  }

  @Override
  protected AbstractMat times(final AbstractMat X) {
    if (n_cols != X.n_rows) {
      throw new RuntimeException("The numbers of columns (" + n_cols + ") must be equal to the number of rows (" + X.n_rows + ") in the specified multiplier.");
    }

    if (X.n_elem == 1) {
      return times(X._data[0]);
    } else {
      Mat result = new Mat(n_rows, X.n_cols);
      BLAS.getInstance().dgemm("N", "N", n_rows, X.n_cols, n_cols, 1, _data, n_rows, X._data, X.n_rows, 0, result._data, n_rows);
      return result;
    }
  }

  /**
   * Return the out-of-place matrix multiplication with the provided right-hand side multiplier.
   * 
   * @param X The multiplier
   * 
   * @return TODO
   * 
   * @throws RuntimeException The number of columns ({@code n_cols}) must be equal to the number of rows (
   *           {@code X.n_rows}) in the specified multiplier.
   */
  public Col times(final Col X) throws RuntimeException {
    if (n_cols != X.n_rows) {
      throw new RuntimeException("The numbers of columns (" + n_cols + ") must be equal to the number of rows (" + X.n_rows + ") in the specified multiplier.");
    }

    Col result = new Col(n_rows);
    BLAS.getInstance().dgemv("N", n_rows, n_cols, 1, _data, n_rows, X._data, 1, 0, result._data, 1);
    return result;
  }

  /**
   * Return the out-of-place matrix multiplication with the provided right-hand side multiplier.
   * 
   * @param X The multiplier
   * 
   * @return TODO
   * 
   * @throws RuntimeException The number of columns ({@code n_cols}) must be equal to the number of rows (
   *           {@code X.n_rows}) in the specified multiplier.
   */
  public Mat times(final Row X) throws RuntimeException {
    if (n_cols != X.n_rows) {
      throw new RuntimeException("The numbers of columns (" + n_cols + ") must be equal to the number of rows (" + X.n_rows + ") in the specified multiplier.");
    }

    Mat result = new Mat(n_rows, X.n_cols);
    BLAS.getInstance().dgemm("N", "N", n_rows, X.n_cols, n_cols, 1, _data, n_rows, X._data, X.n_rows, 0, result._data, n_rows);
    return result;
  }

  /**
   * Return the out-of-place matrix multiplication with the provided right-hand side multiplier.
   * 
   * @param X The multiplier
   * 
   * @return TODO
   * 
   * @throws RuntimeException The number of columns ({@code n_cols}) must be equal to the number of rows (
   *           {@code X.n_rows}) in the specified multiplier.
   */
  public Mat times(final Mat X) throws RuntimeException {
    if (n_cols != X.n_rows) {
      throw new RuntimeException("The numbers of columns (" + n_cols + ") must be equal to the number of rows (" + X.n_rows + ") in the specified multiplier.");
    }

    Mat result = new Mat(n_rows, X.n_cols);
    BLAS.getInstance().dgemm("N", "N", n_rows, X.n_cols, n_cols, 1, _data, n_rows, X._data, X.n_rows, 0, result._data, n_rows);
    return result;
  }

  @Override
  public Mat elemTimes(final Mat X) throws RuntimeException {
    if (n_rows != X.n_rows || n_cols != X.n_cols) {
      throw new RuntimeException("Both matrices (" + n_rows + ", " + n_cols + " and " + X.n_rows + ", " + X.n_cols + ") must have the same shape.");
    }

    Mat result = new Mat(n_rows, n_cols);
    elemTimes(result._data, _data, X._data);
    return result;
  }

  @Override
  public Mat elemTimes(final Col X) throws RuntimeException {
    if (n_rows != X.n_rows || n_cols != X.n_cols) {
      throw new RuntimeException("Both matrices (" + n_rows + ", " + n_cols + " and " + X.n_rows + ", " + X.n_cols + ") must have the same shape.");
    }

    Mat result = new Mat(n_rows, n_cols);
    elemTimes(result._data, _data, X._data);
    return result;
  }

  @Override
  public Mat elemTimes(final Row X) throws RuntimeException {
    if (n_rows != X.n_rows || n_cols != X.n_cols) {
      throw new RuntimeException("Both matrices (" + n_rows + ", " + n_cols + " and " + X.n_rows + ", " + X.n_cols + ") must have the same shape.");
    }

    Mat result = new Mat(n_rows, n_cols);
    elemTimes(result._data, _data, X._data);
    return result;
  }

  @Override
  public Mat equals(final double X) {
    Mat result = new Mat(n_rows, n_cols);
    equals(result._data, _data, X);
    return result;
  }

  @Override
  public Mat equals(final Mat X) throws RuntimeException {
    if (n_rows != X.n_rows || n_cols != X.n_cols) {
      throw new RuntimeException("Both matrices (" + n_rows + ", " + n_cols + " and " + X.n_rows + ", " + X.n_cols + ") must have the same shape.");
    }

    Mat result = new Mat(n_rows, n_cols);
    equals(result._data, _data, X._data);
    return result;
  }

  @Override
  public Mat equals(final Col X) throws RuntimeException {
    if (n_rows != X.n_rows || n_cols != X.n_cols) {
      throw new RuntimeException("Both matrices (" + n_rows + ", " + n_cols + " and " + X.n_rows + ", " + X.n_cols + ") must have the same shape.");
    }

    Mat result = new Mat(n_rows, n_cols);
    equals(result._data, _data, X._data);
    return result;
  }

  @Override
  public Mat equals(final Row X) throws RuntimeException {
    if (n_rows != X.n_rows || n_cols != X.n_cols) {
      throw new RuntimeException("Both matrices (" + n_rows + ", " + n_cols + " and " + X.n_rows + ", " + X.n_cols + ") must have the same shape.");
    }

    Mat result = new Mat(n_rows, n_cols);
    equals(result._data, _data, X._data);
    return result;
  }

  @Override
  public Mat nonEquals(final double X) {
    Mat result = new Mat(n_rows, n_cols);
    nonEquals(result._data, _data, X);
    return result;
  }

  @Override
  public Mat nonEquals(final Mat X) throws RuntimeException {
    if (n_rows != X.n_rows || n_cols != X.n_cols) {
      throw new RuntimeException("Both matrices (" + n_rows + ", " + n_cols + " and " + X.n_rows + ", " + X.n_cols + ") must have the same shape.");
    }

    Mat result = new Mat(n_rows, n_cols);
    nonEquals(result._data, _data, X._data);
    return result;
  }

  @Override
  public Mat nonEquals(final Col X) throws RuntimeException {
    if (n_rows != X.n_rows || n_cols != X.n_cols) {
      throw new RuntimeException("Both matrices (" + n_rows + ", " + n_cols + " and " + X.n_rows + ", " + X.n_cols + ") must have the same shape.");
    }

    Mat result = new Mat(n_rows, n_cols);
    nonEquals(result._data, _data, X._data);
    return result;
  }

  @Override
  public Mat nonEquals(final Row X) throws RuntimeException {
    if (n_rows != X.n_rows || n_cols != X.n_cols) {
      throw new RuntimeException("Both matrices (" + n_rows + ", " + n_cols + " and " + X.n_rows + ", " + X.n_cols + ") must have the same shape.");
    }

    Mat result = new Mat(n_rows, n_cols);
    nonEquals(result._data, _data, X._data);
    return result;
  }

  @Override
  public Mat greaterThan(final double X) {
    Mat result = new Mat(n_rows, n_cols);
    greaterThan(result._data, _data, X);
    return result;
  }

  @Override
  public Mat greaterThan(final Mat X) throws RuntimeException {
    if (n_rows != X.n_rows || n_cols != X.n_cols) {
      throw new RuntimeException("Both matrices (" + n_rows + ", " + n_cols + " and " + X.n_rows + ", " + X.n_cols + ") must have the same shape.");
    }

    Mat result = new Mat(n_rows, n_cols);
    greaterThan(result._data, _data, X._data);
    return result;
  }

  @Override
  public Mat greaterThan(final Col X) throws RuntimeException {
    if (n_rows != X.n_rows || n_cols != X.n_cols) {
      throw new RuntimeException("Both matrices (" + n_rows + ", " + n_cols + " and " + X.n_rows + ", " + X.n_cols + ") must have the same shape.");
    }

    Mat result = new Mat(n_rows, n_cols);
    greaterThan(result._data, _data, X._data);
    return result;
  }

  @Override
  public Mat greaterThan(final Row X) throws RuntimeException {
    if (n_rows != X.n_rows || n_cols != X.n_cols) {
      throw new RuntimeException("Both matrices (" + n_rows + ", " + n_cols + " and " + X.n_rows + ", " + X.n_cols + ") must have the same shape.");
    }

    Mat result = new Mat(n_rows, n_cols);
    greaterThan(result._data, _data, X._data);
    return result;
  }

  @Override
  public Mat lessThan(final double X) {
    Mat result = new Mat(n_rows, n_cols);
    lessThan(result._data, _data, X);
    return result;
  }

  @Override
  public Mat lessThan(final Mat X) throws RuntimeException {
    if (n_rows != X.n_rows || n_cols != X.n_cols) {
      throw new RuntimeException("Both matrices (" + n_rows + ", " + n_cols + " and " + X.n_rows + ", " + X.n_cols + ") must have the same shape.");
    }

    Mat result = new Mat(n_rows, n_cols);
    lessThan(result._data, _data, X._data);
    return result;
  }

  @Override
  public Mat lessThan(final Col X) throws RuntimeException {
    if (n_rows != X.n_rows || n_cols != X.n_cols) {
      throw new RuntimeException("Both matrices (" + n_rows + ", " + n_cols + " and " + X.n_rows + ", " + X.n_cols + ") must have the same shape.");
    }

    Mat result = new Mat(n_rows, n_cols);
    lessThan(result._data, _data, X._data);
    return result;
  }

  @Override
  public Mat lessThan(final Row X) throws RuntimeException {
    if (n_rows != X.n_rows || n_cols != X.n_cols) {
      throw new RuntimeException("Both matrices (" + n_rows + ", " + n_cols + " and " + X.n_rows + ", " + X.n_cols + ") must have the same shape.");
    }

    Mat result = new Mat(n_rows, n_cols);
    lessThan(result._data, _data, X._data);
    return result;
  }

  @Override
  public Mat strictGreaterThan(final double X) {
    Mat result = new Mat(n_rows, n_cols);
    strictGreaterThan(result._data, _data, X);
    return result;
  }

  @Override
  public Mat strictGreaterThan(final Mat X) throws RuntimeException {
    if (n_rows != X.n_rows || n_cols != X.n_cols) {
      throw new RuntimeException("Both matrices (" + n_rows + ", " + n_cols + " and " + X.n_rows + ", " + X.n_cols + ") must have the same shape.");
    }

    Mat result = new Mat(n_rows, n_cols);
    strictGreaterThan(result._data, _data, X._data);
    return result;
  }

  @Override
  public Mat strictGreaterThan(final Col X) throws RuntimeException {
    if (n_rows != X.n_rows || n_cols != X.n_cols) {
      throw new RuntimeException("Both matrices (" + n_rows + ", " + n_cols + " and " + X.n_rows + ", " + X.n_cols + ") must have the same shape.");
    }

    Mat result = new Mat(n_rows, n_cols);
    strictGreaterThan(result._data, _data, X._data);
    return result;
  }

  @Override
  public Mat strictGreaterThan(final Row X) throws RuntimeException {
    if (n_rows != X.n_rows || n_cols != X.n_cols) {
      throw new RuntimeException("Both matrices (" + n_rows + ", " + n_cols + " and " + X.n_rows + ", " + X.n_cols + ") must have the same shape.");
    }

    Mat result = new Mat(n_rows, n_cols);
    strictGreaterThan(result._data, _data, X._data);
    return result;
  }

  @Override
  public Mat strictLessThan(final double X) {
    Mat result = new Mat(n_rows, n_cols);
    strictLessThan(result._data, _data, X);
    return result;
  }

  @Override
  public Mat strictLessThan(final Mat X) throws RuntimeException {
    if (n_rows != X.n_rows || n_cols != X.n_cols) {
      throw new RuntimeException("Both matrices (" + n_rows + ", " + n_cols + " and " + X.n_rows + ", " + X.n_cols + ") must have the same shape.");
    }

    Mat result = new Mat(n_rows, n_cols);
    strictLessThan(result._data, _data, X._data);
    return result;
  }

  @Override
  public Mat strictLessThan(final Col X) throws RuntimeException {
    if (n_rows != X.n_rows || n_cols != X.n_cols) {
      throw new RuntimeException("Both matrices (" + n_rows + ", " + n_cols + " and " + X.n_rows + ", " + X.n_cols + ") must have the same shape.");
    }

    Mat result = new Mat(n_rows, n_cols);
    strictLessThan(result._data, _data, X._data);
    return result;
  }

  @Override
  public Mat strictLessThan(final Row X) throws RuntimeException {
    if (n_rows != X.n_rows || n_cols != X.n_cols) {
      throw new RuntimeException("Both matrices (" + n_rows + ", " + n_cols + " and " + X.n_rows + ", " + X.n_cols + ") must have the same shape.");
    }

    Mat result = new Mat(n_rows, n_cols);
    strictLessThan(result._data, _data, X._data);
    return result;
  }

  @Override
  protected void set_size(final int n_elem) throws NegativeArraySizeException {
    set_size(n_elem, 1);
  }

}
