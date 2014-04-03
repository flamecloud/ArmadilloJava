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
import static org.junit.Assert.assertThat;
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
public class TestLogicMatNumElemsSearch extends TestClass {

  @Parameters(name = "{index}: LogicMat = {0}, NumElems = {2}, Search = {4}")
  public static Collection<Object[]> getParameters() {
    List<InputClass> inputClasses = new ArrayList<>();

    inputClasses.add(InputClass.LogicMat);
    inputClasses.add(InputClass.NumElems);
    inputClasses.add(InputClass.Search);

    return Input.getTestParameters(inputClasses);
  }

  @Parameter(0)
  public String    _logicMatString;

  @Parameter(1)
  public Mat       _logicMat;

  protected Mat    _copyOfLogicMat;

  @Parameter(2)
  public String    _numElemsString;

  @Parameter(3)
  public int       _numElems;

  protected int    _copyOfNumElems;

  @Parameter(4)
  public String    _searchString;

  @Parameter(5)
  public String    _search;

  protected String _copyOfSearch;

  @Before
  public void before() {
    _fileSuffix = _logicMatString + "," + _numElemsString + "," + _searchString;

    _copyOfLogicMat = new Mat(_logicMat);
    _copyOfNumElems = new Integer(_numElems);
    _copyOfSearch = new String(_search);
  }

  @After
  public void after() {
    assertMatEquals(_logicMat, _copyOfLogicMat, 0);
    assertThat(_numElems, is(_copyOfNumElems));
    assertThat(_search, is(_copyOfSearch));
  }

  @Test
  public void testFind() throws IOException {
    assertMatEquals(Arma.find(_logicMat, _numElems, _search), load("Arma.find"));
  }

}
