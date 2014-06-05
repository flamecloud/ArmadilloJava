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
import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.lessThan;

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
public class TestInPlaceGenColVecElemIndNumElems extends TestClass {

  @Parameters(name = "{index}: GenColVec = {0}, ElemInd = {2}, NumElems = {4}")
  public static Collection<Object[]> getParameters() {
    List<InputClass> inputClasses = new ArrayList<>();

    inputClasses.add(InputClass.GenColVec);
    inputClasses.add(InputClass.ElemInd);
    inputClasses.add(InputClass.NumElems);

    return Input.getTestParameters(inputClasses);
  }

  @Parameter(0)
  public String    _genColVecString;

  @Parameter(1)
  public Col       _genColVec;

  protected Col    _copyOfGenColVec;

  @Parameter(2)
  public String    _elemIndString;

  @Parameter(3)
  public int       _elemInd;

  protected int    _copyOfElemInd;

  @Parameter(4)
  public String    _numElemsString;

  @Parameter(5)
  public int	_numElems;

  protected int _copyOfNumElems;

  @Before
  public void before() {
    _fileSuffix = _genColVecString + "," + _elemIndString + "," + _numElemsString;

    _copyOfGenColVec = new Col(_genColVec);
    _copyOfElemInd = new Integer(_elemInd);
    _copyOfNumElems = _numElems;
  }

  @After
  public void after() {
    _genColVec.inPlace(Op.EQUAL, _copyOfGenColVec);
    _elemInd = new Integer(_copyOfElemInd);
    _numElems = _copyOfNumElems;
  }

  @Test
  public void testColVecInsertRows() throws IOException {
    assumeThat(_elemInd, is(lessThan(_genColVec.n_elem)));

    _genColVec.insert_rows(_elemInd, _numElems);

    assertMatEquals(_genColVec, load("Col.insertRows"));
  }
  
  @Test
  public void testColVecInsertRowsTrue() throws IOException {
    assumeThat(_elemInd, is(lessThan(_genColVec.n_elem)));

    _genColVec.insert_rows(_elemInd, _numElems, true);

    assertMatEquals(_genColVec, load("Col.insertRowsTrue"));
  }
  
  @Test
  public void testColVecInsertRowsFalse() throws IOException {
    assumeThat(_elemInd, is(lessThan(_genColVec.n_elem)));

    _genColVec.insert_rows(_elemInd, _numElems, false);

    assertEquals(_genColVec.n_elem, load("Col.insertRowsFalse").n_elem);
  }

}