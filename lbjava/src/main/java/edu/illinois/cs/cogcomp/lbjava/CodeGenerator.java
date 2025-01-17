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
package edu.illinois.cs.cogcomp.lbjava;


/**
  * All IR classes for which code is generated implement this interface.
  *
  * @author Nick Rizzolo
 **/
public interface CodeGenerator
{
  /** Returns the name of the code generator. */
  public String getName();

  /**
    * Returns the line number on which this AST node is found in the source
    * (starting from line 0).
   **/
  public int getLine();

  /** Returns a shallow textual representation of the AST node. */
  public StringBuffer shallow();
}

