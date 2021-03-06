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

/**
 * Provides support for Matlab's index expression range a:b.
 * 
 * @author Sebastian Niemann
 */
public class Span {
  /**
   * First position of the span
   */
  protected final int     _first;
  /**
   * Last position of the span
   */
  protected final int     _last;
  /**
   * Whether the span includes the entire range
   */
  protected final boolean _isEntireRange;

  /**
   * TODO
   */
  protected Span() {
    // Illegal values by design. Should result in an exception if used together with other provided methods/classes.
    _first = -1;
    _last = -1;

    _isEntireRange = true;
  }
  
  /**
   * TODO
   * 
   * @param span TODO
   */
  protected Span(final Span span) {
    _first = span._first;
    _last = span._last;

    _isEntireRange = span._isEntireRange;
  }
  
  /**
   * Creates a span similar to Matlab's index expression range a:b, but without creating a vector.
   * 
   * @param first The first element of the span (a)
   * @param last The last element of the span (b)
   * 
   * @throws IllegalArgumentException The first position needs to be less than equal the last but were {@code first} and
   *           {@code last}.
   * @throws IllegalArgumentException All position must be non-negative.
   */
  public Span(final int first, final int last) throws IllegalArgumentException {
    if (last < first) {
      throw new IllegalArgumentException("The first position needs to be less than equal the last but were " + first + " and " + last + ".");
    }

    if (first < 0) {
      throw new IllegalArgumentException("All position must be non-negative.");
    }

    _first = first;
    _last = last;

    _isEntireRange = false;
  }

  /**
   * Creates a span similar to Matlab's index expression range a:a, but without creating a vector.
   * 
   * @param position The position (a)
   * 
   * @throws IllegalArgumentException All position must be non-negative.
   * @throws IllegalArgumentException NaN and infinity are not valid element positions.
   */
  public Span(final int position) throws IllegalArgumentException {
    this(position, position);
  }

  /**
   * Creates a span similar to Matlab's index expression special range :, but without creating a vector.
   * 
   * @return The span
   */
  public static Span all() {
    return new Span();
  }
}
