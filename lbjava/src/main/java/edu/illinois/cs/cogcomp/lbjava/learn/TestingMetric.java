/**
 * This software is released under the University of Illinois/Research and
 *  Academic Use License. See the LICENSE file in the root folder for details.
 * Copyright (c) 2016
 *
 * Developed by:
 * The Cognitive Computations Group
 * University of Illinois at Urbana-Champaign
 * http://cogcomp.cs.illinois.edu/
 */
package edu.illinois.cs.cogcomp.lbjava.learn;

import edu.illinois.cs.cogcomp.lbjava.classify.Classifier;
import edu.illinois.cs.cogcomp.lbjava.parse.Parser;


/**
  * <code>TestingMetric</code> is an interface through which the user may
  * implement their own testing method for use by LBJava's internal cross
  * validation algorithm.
  *
  * @author Dan Muriello
 **/
public interface TestingMetric
{
  /** Returns the name of the testing metric. */
  public String getName();


  /**
    * Evaluates a classifier against an oracle on the data provided by a
    * parser.
    *
    * @param classifier The classifier whose accuracy is being measured.
    * @param oracle     A classifier that returns the label of each example.
    * @param parser     A parser to supply the example objects.
    * @return A value assessing the performance of the classifier.
   **/
  public double test(Classifier classifier, Classifier oracle, Parser parser);
}

