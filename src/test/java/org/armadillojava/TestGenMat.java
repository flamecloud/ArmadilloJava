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
import static org.junit.Assume.assumeThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.IsCloseTo.closeTo;

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
public class TestGenMat extends TestClass {

  @Parameters(name = "{index}: GenMat = {0}")
  public static Collection<Object[]> getParameters() {
    List<InputClass> inputClasses = new ArrayList<>();

    inputClasses.add(InputClass.GenMat);

    return Input.getTestParameters(inputClasses);
  }

  @Parameter(0)
  public String _genMatString;

  @Parameter(1)
  public Mat    _genMat;

  protected Mat _copyOfGenMat;

  @Before
  public void before() {
    _fileSuffix = _genMatString;

    _copyOfGenMat = new Mat(_genMat);
  }

  @After
  public void after() {
    assertMatEquals(_genMat, _copyOfGenMat, 0);
  }

  @Test
  public void testAbs() throws IOException {
    assertMatEquals(Arma.abs(_genMat), load("Arma.abs"));
  }

  @Test
  public void testEps() throws IOException {
    assertMatEquals(Arma.eps(_genMat), load("Arma.eps"));
  }

  @Test
  public void testExp() throws IOException {
    assertMatEquals(Arma.exp(_genMat), load("Arma.exp"));
  }

  @Test
  public void testExp2() throws IOException {
    assertMatEquals(Arma.exp2(_genMat), load("Arma.exp2"));
  }

  @Test
  public void testExp10() throws IOException {
    assertMatEquals(Arma.exp10(_genMat), load("Arma.exp10"));
  }

  @Test
  public void testTrunc_exp() throws IOException {
    assertMatEquals(Arma.trunc_exp(_genMat), load("Arma.trunc_exp"));
  }

  @Test
  public void testLog() throws IOException {
    assertMatEquals(Arma.log(_genMat), load("Arma.log"));
  }

  @Test
  public void testLog2() throws IOException {
    assertMatEquals(Arma.log2(_genMat), load("Arma.log2"));
  }

  @Test
  public void testLog10() throws IOException {
    assertMatEquals(Arma.log10(_genMat), load("Arma.log10"));
  }

  @Test
  public void testTrunc_log() throws IOException {
    assertMatEquals(Arma.trunc_log(_genMat), load("Arma.trunc_log"));
  }

  @Test
  public void testSquare() throws IOException {
    assertMatEquals(Arma.square(_genMat), load("Arma.square"));
  }

  @Test
  public void testFloor() throws IOException {
    assertMatEquals(Arma.floor(_genMat), load("Arma.floor"));
  }

  @Test
  public void testCeil() throws IOException {
    assertMatEquals(Arma.ceil(_genMat), load("Arma.ceil"));
  }

  @Test
  public void testRound() throws IOException {
    assertMatEquals(Arma.round(_genMat), load("Arma.round"));
  }

  @Test
  public void testSign() throws IOException {
    assertMatEquals(Arma.sign(_genMat), load("Arma.sign"));
  }

  @Test
  public void testSin() throws IOException {
    assertMatEquals(Arma.sin(_genMat), load("Arma.sin"));
  }

  @Test
  public void testAsin() throws IOException {
    assertMatEquals(Arma.asin(_genMat), load("Arma.asin"));
  }

  @Test
  public void testSinh() throws IOException {
    assertMatEquals(Arma.sinh(_genMat), load("Arma.sinh"));
  }

  @Test
  public void testAsinh() throws IOException {
    assertMatEquals(Arma.asinh(_genMat), load("Arma.asinh"));
  }

  @Test
  public void testCos() throws IOException {
    assertMatEquals(Arma.cos(_genMat), load("Arma.cos"));
  }

  @Test
  public void testAcos() throws IOException {
    assertMatEquals(Arma.acos(_genMat), load("Arma.acos"));
  }

  @Test
  public void testCosh() throws IOException {
    assertMatEquals(Arma.cosh(_genMat), load("Arma.cosh"));
  }

  @Test
  public void testAcosh() throws IOException {
    assertMatEquals(Arma.acosh(_genMat), load("Arma.acosh"));
  }

  @Test
  public void testTan() throws IOException {
    assertMatEquals(Arma.tan(_genMat), load("Arma.tan"));
  }

  @Test
  public void testAtan() throws IOException {
    assertMatEquals(Arma.atan(_genMat), load("Arma.atan"));
  }

  @Test
  public void testTanh() throws IOException {
    assertMatEquals(Arma.tanh(_genMat), load("Arma.tanh"));
  }

  @Test
  public void testAtanh() throws IOException {
    assertMatEquals(Arma.atanh(_genMat), load("Arma.atanh"));
  }

  @Test
  public void testCond() throws IOException {
    double expected = load("Arma.cond")._data[0];
    if (Double.isInfinite(expected) || Double.isNaN(expected)) {
      assertThat(Arma.cond(_genMat), is(expected));
    } else {
      assertThat(Arma.cond(_genMat), is(closeTo(expected, Math.abs(expected) * 1e-10)));
    }
  }

  @Test
  public void testRank() throws IOException {
    int expected = (int) load("Arma.rank")._data[0];
    assertThat(Arma.rank(_genMat), is(expected));
  }

  @Test
  public void testDiagvec() throws IOException {
    assertMatEquals(Arma.diagvec(_genMat), load("Arma.diagvec"));
  }

  @Test
  public void testMin() throws IOException {
    assertMatEquals(Arma.min(Row.class, _genMat), load("Arma.min"));
    assertMatEquals(Arma.min(Col.class, _genMat), load("Arma.min").t());
  }

  @Test
  public void testMax() throws IOException {
    assertMatEquals(Arma.max(Row.class, _genMat), load("Arma.max"));
    assertMatEquals(Arma.max(Col.class, _genMat), load("Arma.max").t());
  }

  @Test
  public void testProd() throws IOException {
    assertMatEquals(Arma.prod(Row.class, _genMat), load("Arma.prod"));
    assertMatEquals(Arma.prod(Col.class, _genMat), load("Arma.prod").t());
  }

  @Test
  public void testMean() throws IOException {
    assertMatEquals(Arma.mean(Row.class, _genMat), load("Arma.mean"));
    assertMatEquals(Arma.mean(Col.class, _genMat), load("Arma.mean").t());
  }

  @Test
  public void testMedian() throws IOException {
    assertMatEquals(Arma.median(Row.class, _genMat), load("Arma.median"));
    assertMatEquals(Arma.median(Col.class, _genMat), load("Arma.median").t());
  }

  @Test
  public void testStddev() throws IOException {
    assertMatEquals(Arma.stddev(Row.class, _genMat), load("Arma.stddev"));
    assertMatEquals(Arma.stddev(Col.class, _genMat), load("Arma.stddev").t());
  }

  @Test
  public void testVar() throws IOException {
    assertMatEquals(Arma.var(Row.class, _genMat), load("Arma.var"));
    assertMatEquals(Arma.var(Col.class, _genMat), load("Arma.var").t());
  }

  @Test
  public void testCor() throws IOException {
    assertMatEquals(Arma.cor(_genMat), load("Arma.cor"));
  }

  @Test
  public void testCov() throws IOException {
    assertMatEquals(Arma.cov(_genMat), load("Arma.cov"));
  }

  @Test
  public void testCumsum() throws IOException {
    assertMatEquals(Arma.cumsum(_genMat), load("Arma.cumsum"));
  }

  @Test
  public void testFliplr() throws IOException {
    assertMatEquals(Arma.fliplr(_genMat), load("Arma.fliplr"));
  }

  @Test
  public void testFlipud() throws IOException {
    assertMatEquals(Arma.flipud(_genMat), load("Arma.flipud"));
  }

  @Test
  public void testHist() throws IOException {
    assertMatEquals(Arma.hist(_genMat), load("Arma.hist"));
  }

  @Test
  public void testSort() throws IOException {
    assertMatEquals(Arma.sort(_genMat), load("Arma.sort"));
  }

  @Test
  public void testTrans() throws IOException {
    assertMatEquals(Arma.trans(_genMat), load("Arma.trans"));
  }

  @Test
  public void testUnique() throws IOException {
    assertMatEquals(Arma.unique(_genMat), load("Arma.unique"));
  }

  @Test
  public void testVectorise() throws IOException {
    assertMatEquals(Arma.vectorise(Col.class, _genMat), load("Arma.vectorise"));
    assertMatEquals(Arma.vectorise(Row.class, _genMat), load("Arma.vectorise").t());
  }

  @Test
  public void testLu1() throws IOException {
    assumeThat(load("Arma.lu")._data[0], is(1.0));

    Mat L = new Mat();
    Mat U = new Mat();
    Mat P = new Mat();

    Arma.lu(L, U, P, _genMat);

    if (_genMat.is_square()) {
      assertMatEquals(L, Arma.trimatl(L));
      assertMatEquals(U, Arma.trimatu(U));
    }

    assertMatEquals(Arma.sum(Col.class, P, 0), Arma.ones(Col.class, P.n_cols));
    assertMatEquals(Arma.sum(Col.class, P, 1), Arma.ones(Col.class, P.n_rows));

    assertMatEquals((P.t()).times(L).times(U), _genMat);
  }

  @Test
  public void testLu2() throws IOException {
    assumeThat(load("Arma.lu")._data[0], is(1.0));

    Mat L = new Mat();
    Mat U = new Mat();

    Arma.lu(L, U, _genMat);

    if (_genMat.is_square()) {
      assertMatEquals(U, Arma.trimatu(U));
    }
    assertMatEquals(L.times(U), _genMat);
  }

  @Test
  public void testPinvA() throws IOException {
    assertMatEquals(Arma.pinv(_genMat), load("Arma.pinv"), TestUtil.globalDelta(load("Arma.pinv"), 1e-12));
  }

  @Test
  public void testPinvB() throws IOException {
    Mat pinv = new Mat();

    Arma.pinv(pinv, _genMat);

    assertMatEquals(pinv, load("Arma.pinv"), TestUtil.globalDelta(load("Arma.pinv"), 1e-12));
  }

  @Test
  public void testPrincompA() throws IOException {
    Mat coeff = new Mat();
    Mat score = new Mat();
    Col latent = new Col();
    Col tsquared = new Col();

    Arma.princomp(coeff, score, latent, tsquared, _genMat);

    assertMatEquals(latent, load("Arma.princompLatent"));
  }

  @Test
  public void testPrincompB() throws IOException {
    Mat coeff = new Mat();
    Mat score = new Mat();
    Col latent = new Col();

    Arma.princomp(coeff, score, latent, _genMat);

    assertMatEquals(latent, load("Arma.princompLatent"));
  }

  @Test
  public void testQr() throws IOException {
    Mat Q = new Mat();
    Mat R = new Mat();

    Arma.qr(Q, R, _genMat);

    if (_genMat.is_square()) {
      assertMatEquals(Q.t(), Q.i());
      assertMatEquals(Arma.trimatu(R), R);
    }
    assertMatEquals(Q.times(R), _genMat);
  }

  @Test
  public void testQr_econ() throws IOException {
    Mat Q = new Mat();
    Mat R = new Mat();

    Arma.qr_econ(Q, R, _genMat);

    if (_genMat.n_rows <= _genMat.n_cols && _genMat.is_square()) {
      assertMatEquals(Q.t(), Q.i());
      assertMatEquals(Arma.trimatu(R), R);
    }
    assertMatEquals(Q.times(R), _genMat);
  }

  @Test
  public void testSvdA() throws IOException {
    assertMatEquals(Arma.svd(_genMat), load("Arma.svd"));
  }

  @Test
  public void testSvdB() throws IOException {
    Col s = new Col();

    Arma.svd(s, _genMat);

    assertMatEquals(s, load("Arma.svd"));
  }

  @Test
  public void testSvdC() throws IOException {
    Mat U = new Mat();
    Col s = new Col();
    Mat V = new Mat();

    Arma.svd(U, s, V, _genMat);

    assertMatEquals(s, load("Arma.svd"));

    if (_genMat.is_square()) {
      assertMatEquals(U.times(Arma.diagmat(s)).times(V.t()), _genMat);
    }
  }

  @Test
  public void testSvd_econ() throws IOException {
    Mat U = new Mat();
    Col s = new Col();
    Mat V = new Mat();

    Arma.svd_econ(U, s, V, _genMat);

    assertMatEquals(s, load("Arma.svd_econ"));

    if (_genMat.is_square()) {
      assertMatEquals(U.times(Arma.diagmat(s)).times(V.t()), _genMat);
    }
  }

  @Test
  public void testNegate() throws IOException {
    assertMatEquals(Arma.negate(_genMat), load("Arma.negate"));
  }

  @Test
  public void testReciprocal() throws IOException {
    assertMatEquals(Arma.reciprocal(_genMat), load("Arma.reciprocal"));
  }

  @Test
  public void testAccu() throws IOException {
    double expected = load("Arma.accu")._data[0];
    if (Double.isInfinite(expected) || Double.isNaN(expected)) {
      assertThat(Arma.accu(_genMat), is(expected));
    } else {
      assertThat(Arma.accu(_genMat), is(closeTo(expected, Math.abs(expected) * 1e-10)));
    }
  }

}
