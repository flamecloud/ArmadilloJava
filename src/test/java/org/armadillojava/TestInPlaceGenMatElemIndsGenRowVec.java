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
public class TestInPlaceGenMatElemIndsGenRowVec extends TestClass {

  @Parameters(name = "{index}: GenMat = {0}, ElemInds = {2}, GenRowVec = {4}")
  public static Collection<Object[]> getParameters() {
    List<InputClass> inputClasses = new ArrayList<>();

    inputClasses.add(InputClass.GenMat);
    inputClasses.add(InputClass.ElemInds);
    inputClasses.add(InputClass.GenRowVec);

    return Input.getTestParameters(inputClasses);
  }

  @Parameter(0)
  public String _genMatString;

  @Parameter(1)
  public Mat    _genMat;

  protected Mat _copyOfGenMat;

  @Parameter(2)
  public String _elemIndsString;

  @Parameter(3)
  public Col    _elemInds;

  protected Col _copyOfElemInds;

  @Parameter(4)
  public String _genRowVecString;

  @Parameter(5)
  public Row    _genRowVec;

  protected Row _copyOfGenRowVec;

  @Before
  public void before() {
    _fileSuffix = _genMatString + "," + _elemIndsString + "," + _genRowVecString;

    _copyOfGenMat = new Mat(_genMat);
    _copyOfElemInds = new Col(_elemInds);
    _copyOfGenRowVec = new Row(_genRowVec);
  }

  @After
  public void after() {
    _genMat.inPlace(Op.EQUAL, _copyOfGenMat);
    _elemInds.inPlace(Op.EQUAL, _copyOfElemInds);
    _genRowVec.inPlace(Op.EQUAL, _copyOfGenRowVec);
  }

  @Test
  public void testMatElemEqual() throws IOException {
    for (int n = 0; n < _elemInds.n_elem; n++) {
      assumeThat(_genMat.in_range((int) _elemInds.at(n)), is(true));
    }
    assumeThat(_genRowVec.n_elem, is(_elemInds.n_elem));

    _genMat.elem(_elemInds, Op.EQUAL, _genRowVec);

    assertMatEquals(_genMat, load("Mat.elemEqual"));
  }

  @Test
  public void testMatElemPlus() throws IOException {
    for (int n = 0; n < _elemInds.n_elem; n++) {
      assumeThat(_genMat.in_range((int) _elemInds.at(n)), is(true));
    }
    assumeThat(_genRowVec.n_elem, is(_elemInds.n_elem));

    _genMat.elem(_elemInds, Op.PLUS, _genRowVec);

    assertMatEquals(_genMat, load("Mat.elemPlus"));
  }

  @Test
  public void testMatElemMinus() throws IOException {
    for (int n = 0; n < _elemInds.n_elem; n++) {
      assumeThat(_genMat.in_range((int) _elemInds.at(n)), is(true));
    }
    assumeThat(_genRowVec.n_elem, is(_elemInds.n_elem));

    _genMat.elem(_elemInds, Op.MINUS, _genRowVec);

    assertMatEquals(_genMat, load("Mat.elemMinus"));
  }

  @Test
  public void testMatElemElemTimes() throws IOException {
    for (int n = 0; n < _elemInds.n_elem; n++) {
      assumeThat(_genMat.in_range((int) _elemInds.at(n)), is(true));
    }
    assumeThat(_genRowVec.n_elem, is(_elemInds.n_elem));

    _genMat.elem(_elemInds, Op.ELEMTIMES, _genRowVec);

    assertMatEquals(_genMat, load("Mat.elemElemTimes"));
  }

  @Test
  public void testMatElemElemDivide() throws IOException {
    for (int n = 0; n < _elemInds.n_elem; n++) {
      assumeThat(_genMat.in_range((int) _elemInds.at(n)), is(true));
    }
    assumeThat(_genRowVec.n_elem, is(_elemInds.n_elem));

    _genMat.elem(_elemInds, Op.ELEMDIVIDE, _genRowVec);

    assertMatEquals(_genMat, load("Mat.elemElemDivide"));
  }

}
