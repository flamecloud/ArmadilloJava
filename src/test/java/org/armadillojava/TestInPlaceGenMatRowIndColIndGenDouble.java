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

import static org.armadillojava.TestUtil.assertMatEquals;
import static org.junit.Assume.assumeThat;
import static org.hamcrest.CoreMatchers.is;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TestInPlaceGenMatRowIndColIndGenDouble extends TestClass {

  @Parameters(name = "{index}: GenMat = {0}, RowInd = {2}, ColInd = {4}, GenDouble = {6}")
  public static Collection<Object[]> getParameters() {
    List<InputClass> inputClasses = new ArrayList<>();

    inputClasses.add(InputClass.GenMat);
    inputClasses.add(InputClass.RowInd);
    inputClasses.add(InputClass.ColInd);
    inputClasses.add(InputClass.GenDouble);

    return Input.getTestParameters(inputClasses);
  }

  @Parameter(0)
  public String _genMatString;

  @Parameter(1)
  public Mat    _genMat;

  protected Mat _copyOfGenMat;

  @Parameter(2)
  public String _rowIndString;

  @Parameter(3)
  public int    _rowInd;

  protected int _copyOfRowInd;

  @Parameter(4)
  public String _colIndString;

  @Parameter(5)
  public int    _colInd;

  protected int _copyOfColInd;

  @Parameter(6)
  public String _genDoubleString;

  @Parameter(7)
  public double    _genDouble;

  protected double _copyOfGenDouble;

  @Before
  public void before() {
    _fileSuffix = _genMatString + "," + _rowIndString + "," + _colIndString + "," + _genDoubleString;

    _copyOfGenMat = new Mat(_genMat);
    _copyOfRowInd = new Integer(_rowInd);
    _copyOfColInd = new Integer(_colInd);
    _copyOfGenDouble = new Double(_genDouble);
  }

  @After
  public void after() {
    _genMat.inPlace(Op.EQUAL, _copyOfGenMat);
    _rowInd = new Integer(_copyOfRowInd);
    _colInd = new Integer(_copyOfColInd);
    _genDouble = new Double(_copyOfGenDouble);
  }

  @Test
  public void testMatAtEqual() throws IOException {
    assumeThat(_genMat.in_range(_rowInd, _colInd), is(true));

    _genMat.at(_rowInd, _colInd, Op.EQUAL, _genDouble);

    assertMatEquals(_genMat, load("Mat.atEqual"));
  }

  @Test
  public void testMatAtPlus() throws IOException {
    assumeThat(_genMat.in_range(_rowInd, _colInd), is(true));

    _genMat.at(_rowInd, _colInd, Op.PLUS, _genDouble);

    assertMatEquals(_genMat, load("Mat.atPlus"));
  }

  @Test
  public void testMatAtMinus() throws IOException {
    assumeThat(_genMat.in_range(_rowInd, _colInd), is(true));

    _genMat.at(_rowInd, _colInd, Op.MINUS, _genDouble);

    assertMatEquals(_genMat, load("Mat.atMinus"));
  }

  @Test
  public void testMatAtTimes() throws IOException {
    assumeThat(_genMat.in_range(_rowInd, _colInd), is(true));

    _genMat.at(_rowInd, _colInd, Op.TIMES, _genDouble);

    assertMatEquals(_genMat, load("Mat.atTimes"));
  }

  @Test
  public void testMatAtDivide() throws IOException {
    assumeThat(_genMat.in_range(_rowInd, _colInd), is(true));

    _genMat.at(_rowInd, _colInd, Op.DIVIDE, _genDouble);

    assertMatEquals(_genMat, load("Mat.atDivide"));
  }

}
