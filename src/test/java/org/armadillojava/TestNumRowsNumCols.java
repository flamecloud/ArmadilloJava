package org.armadillojava;

import static org.armadillojava.TestUtil.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TestNumRowsNumCols extends TestClass {

  @Parameters(name = "{index}: _genColVec = {0}")
  public static Collection<Object[]> getParameters() {
    List<InputClass> inputClasses = new ArrayList<>();

    inputClasses.add(InputClass.NumRows);
    inputClasses.add(InputClass.NumCols);

    return Input.getTestParameters(inputClasses);
  }

  @Parameter(0)
  public String _numRowsString;

  @Parameter(1)
  public int    _numRows;

  @Parameter(2)
  public String _numColsString;

  @Parameter(3)
  public int    _numCols;

  @Before
  public void before() {
    _fileSuffix = _numRowsString + _numColsString;
  }

  @Test
  public void testEye() throws IOException {
    assertMatEquals(Arma.eye(_numRows, _numCols), load("eye"));
  }

  @Test
  public void testOnes() throws IOException {
    assertMatEquals(Arma.ones(_numRows, _numCols), load("ones"));
  }

  @Test
  public void testZeros() throws IOException {
    assertMatEquals(Arma.zeros(_numRows, _numCols), load("zeros"));
  }

}