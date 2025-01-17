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
package edu.illinois.cs.cogcomp.lbjava.classify;

import edu.illinois.cs.cogcomp.lbjava.learn.ChildLexicon;
import edu.illinois.cs.cogcomp.lbjava.learn.Lexicon;
import edu.illinois.cs.cogcomp.lbjava.util.ByteString;
import edu.illinois.cs.cogcomp.lbjava.util.ExceptionlessInputStream;
import edu.illinois.cs.cogcomp.lbjava.util.ExceptionlessOutputStream;


/**
  * Represents the conjunction of two discrete features.
  *
  * @author Nick Rizzolo
 **/
public class DiscreteConjunctiveFeature extends DiscreteFeature
{
  /** One feature argument. */
  protected DiscreteFeature left;
  /** The other feature argument. */
  protected DiscreteFeature right;


  /**
    * For internal use only.
    *
    * @see Feature#readFeature(ExceptionlessInputStream)
   **/
  public DiscreteConjunctiveFeature() { }

  /**
    * Creates a new conjunctive feature.
    *
    * @param p  The new conjunctive feature's package.
    * @param c  The name of the classifier that produced this feature.
    * @param l  One feature argument.
    * @param r  The other feature argument.
   **/
  public DiscreteConjunctiveFeature(String p, String c, DiscreteFeature l,
                                    DiscreteFeature r) {
    this(p, c, l, r, (short) -1, (short) 0);
  }

  /**
    * Creates a new conjunctive feature.
    *
    * @param p  The new discrete feature's package.
    * @param c  The name of the classifier that produced this feature.
    * @param l  One feature argument.
    * @param r  The other feature argument.
    * @param vi The index corresponding to the value.
    * @param t  The total allowable values for this feature.
   **/
  public DiscreteConjunctiveFeature(String p, String c, DiscreteFeature l,
                                    DiscreteFeature r, short vi, short t) {
    super(p, c, vi, t);
    left = l;
    right = r;
  }

  /**
    * Creates a new conjunctive feature taking the package and name of the
    * given classifier.
    *
    * @param c  The classifier from which package and name information is
    *           taken.
    * @param l  One feature argument.
    * @param r  The other feature argument.
   **/
  public DiscreteConjunctiveFeature(Classifier c, DiscreteFeature l,
                                    DiscreteFeature r) {
    this(c.containingPackage, c.name, l, r);

    if (c.allowableValues().length == 0) valueIndex = -1;
    else {
      short lTotal = l.totalValues();
      valueIndex = (short) (lTotal * r.getValueIndex() + l.getValueIndex());
      totalValues = (short) (lTotal * r.totalValues());
    }
  }


  /**
    * Determines if this feature is conjunctive.
    *
    * @return <code>true</code> iff this feature is conjunctive.
   **/
  public boolean isConjunctive() { return true; }


  /** Returns the value of {@link #left}. */
  public DiscreteFeature getLeft() { return left; }
  /** Returns the value of {@link #right}. */
  public DiscreteFeature getRight() { return right; }


  /**
    * Retrieves this feature's identifier as a string.
    *
    * @return The empty string, since conjunctive features don't have
    *         identifiers.
   **/
  public String getStringIdentifier() { return ""; }


  /**
    * Retrieves this feature's identifier as a byte string.
    *
    * @return {@link ByteString#emptyString}, since conjunctive features don't
    *         have identifiers.
   **/
  public ByteString getByteStringIdentifier() {
    return ByteString.emptyString;
  }


  /**
    * Gives a string representation of the value of this feature.
    *
    * @return A string representation of the value of this feature.
   **/
  public String getStringValue() {
    return left.getStringValue() + right.getStringValue();
  }


  /**
    * Gives a string representation of the value of this feature.
    *
    * @return A string representation of the value of this feature.
   **/
  public ByteString getByteStringValue() {
    ByteString b = left.getByteStringValue();
    ByteString[] toAppend =
      { new ByteString("&", b.getEncoding()), right.getByteStringValue() };
    return b.append(toAppend);
  }


  /**
    * The depth of a feature is one more than the maximum depth of any of its
    * children, or 0 if it has no children.
    *
    * @return The depth of this feature as described above.
   **/
  public int depth() { return Math.max(left.depth(), right.depth()) + 1; }


  /**
    * Determines whether or not the parameter is equivalent to the string
    * representation of the value of this feature.
    *
    * @param v  The string to compare against.
    * @return <code>true</code> iff the parameter is equivalent to the string
    *         representation of the value of this feature.
   **/
  public boolean valueEquals(String v) { return getStringValue().equals(v); }


  /**
    * Returns the strength of this feature if it were to be placed in a
    * mathematical vector space.
   **/
  public double getStrength() { return 1; }


  /**
    * Return the feature that should be used to index this feature into a
    * lexicon.
    *
    * @param lexicon  The lexicon into which this feature will be indexed.
    * @param training Whether or not the learner is currently training.
    * @param label    The label of the example containing this feature, or -1
    *                 if we aren't doing per class feature counting.
    * @return A feature object appropriate for use as the key of a map.
   **/
  public Feature getFeatureKey(Lexicon lexicon, boolean training, int label) {
    if (!training) return this;
    return
      new DiscreteConjunctiveFeature(
            containingPackage, generatingClassifier,
            getArgumentKey(left, lexicon, label),
            getArgumentKey(right, lexicon, label),
            valueIndex, totalValues);
  }


  /**
    * A helper method for {@link #getFeatureKey(Lexicon,boolean,int)}, this
    * method computes the feature keys corresponding to the arguments of the
    * conjunction.  Here, we lookup the arguments to the conjunction in the
    * lexicon so that their counts are never less than the conjunction's, and
    * we return the actual feature object that's already a key in the lexicon.
    *
    * @param f        The argument feature for which a key will be computed.
    * @param lexicon  The lexicon into which this feature will be indexed.
    * @param label    The label of the example containing this feature, or -1
    *                 if we aren't doing per class feature counting.
    * @return A feature object appropriate for use as the key of a map.
   **/
  protected DiscreteFeature getArgumentKey(Feature f, Lexicon lexicon,
                                           int label) {
    if (!f.isPrimitive()) f = f.getFeatureKey(lexicon, true, label);
    return (DiscreteFeature) lexicon.getChildFeature(f, label);
  }


  /**
    * Returns a {@link RealConjunctiveFeature} with exactly the same children
    * as this feature.
   **/
  public RealFeature makeReal() {
    return
      new RealConjunctiveFeature(containingPackage, generatingClassifier,
                                 left, right);
  }


  /**
    * Returns a new feature object that's identical to this feature except its
    * strength is given by <code>s</code>.
    *
    * @param s  The strength of the new feature.
    * @return A new feature object as above, or <code>null</code> if this
    *         feature cannot take the specified strength.
   **/
  public Feature withStrength(double s) { return s == 1 ? this : null; }


  /**
    * Returns a feature object in which any strings that are being used to
    * represent an identifier or value have been encoded in byte strings.
    *
    * @param e  The encoding to use.
    * @return A feature object as above; possible this object.
   **/
  public Feature encode(String e) {
    DiscreteFeature newLeft = (DiscreteFeature) left.encode(e);
    DiscreteFeature newRight = (DiscreteFeature) right.encode(e);
    if (newLeft == left && newRight == right) return this;
    return
      new DiscreteConjunctiveFeature(
            containingPackage, generatingClassifier, newLeft, newRight,
            valueIndex, totalValues);
  }


  /**
    * Takes care of any feature-type-specific tasks that need to be taken care
    * of when removing a feature of this type from a {@link ChildLexicon}, in
    * particular updating parent counts and removing children of this feature
    * if necessary.
    *
    * @param lex  The child lexicon this feature is being removed from.
   **/
  public void removeFromChildLexicon(ChildLexicon lex) {
    lex.decrementParentCounts(left);
    lex.decrementParentCounts(right);
  }


  /**
    * Does a feature-type-specific lookup of this feature in the given
    * {@link ChildLexicon}.
    *
    * @param lex    The child lexicon this feature is being looked up in.
    * @param label  The label of the example containing this feature, or -1 if
    *               we aren't doing per class feature counting.
    * @return The index of <code>f</code> in this lexicon.
   **/
  public int childLexiconLookup(ChildLexicon lex, int label) {
    return lex.childLexiconLookup(this, label);
  }


  /**
    * Returns a hash code based on the hash codes of {@link #left} and
    * {@link #right}.
    *
    * @return The hash code of this feature.
   **/
  public int hashCode() {
    return 31 * super.hashCode() + 17 * left.hashCode() + right.hashCode();
  }


  /**
    * Two conjunctions are equivalent when their arguments are equivalent.
    *
    * @return <code>true</code> iff the argument is an equivalent
    *         <code>Feature</code>.
   **/
  public boolean equals(Object o) {
    if (!super.equals(o)) return false;
    DiscreteConjunctiveFeature c = (DiscreteConjunctiveFeature) o;
    return (left == c.left || left.equals(c.left))
           && (right == c.right || right.equals(c.right));
  }


  /**
    * Used to sort features into an order that is convenient both to page
    * through and for the lexicon to read off disk.
    *
    * @param o  An object to compare with.
    * @return Integers appropriate for sorting features first by {@link #left}
    *         and then by {@link #right}.
   **/
  public int compareTo(Object o) {
    int d = compareNameStrings(o);
    if (d != 0) return d;
    DiscreteConjunctiveFeature c = (DiscreteConjunctiveFeature) o;
    d = left.compareTo(c.left);
    if (d != 0) return d;
    return right.compareTo(c.right);
  }


  /**
    * Writes a string representation of this <code>Feature</code> to the
    * specified buffer.
    *
    * @param buffer The buffer to write to.
   **/
  public void write(StringBuffer buffer) {
    writeNameString(buffer);
    buffer.append('{');
    left.write(buffer);
    buffer.append(", ");
    right.write(buffer);
    buffer.append('}');
  }


  /**
    * Writes a string representation of this <code>Feature</code> to the
    * specified buffer, omitting the package name.
    *
    * @param buffer The buffer to write to.
   **/
  public void writeNoPackage(StringBuffer buffer) {
    String p = containingPackage;
    containingPackage = null;
    writeNameString(buffer);
    buffer.append('{');
    left.writeNoPackage(buffer);
    buffer.append(", ");
    right.writeNoPackage(buffer);
    buffer.append('}');
    containingPackage = p;
  }


  /**
    * Writes a complete binary representation of the feature.
    *
    * @param out  The output stream.
   **/
  public void write(ExceptionlessOutputStream out) {
    super.write(out);
    left.write(out);
    right.write(out);
  }


  /**
    * Reads the representation of a feature with this object's run-time type
    * from the given stream, overwriting the data in this object.
    *
    * <p> This method is appropriate for reading features as written by
    * {@link #write(ExceptionlessOutputStream)}.
    *
    * @param in The input stream.
   **/
  public void read(ExceptionlessInputStream in) {
    super.read(in);
    left = (DiscreteFeature) Feature.readFeature(in);
    right = (DiscreteFeature) Feature.readFeature(in);
  }


  /**
    * Writes a binary representation of the feature intended for use by a
    * lexicon, omitting redundant information when possible.
    *
    * @param out  The output stream.
    * @param lex  The lexicon out of which this feature is being written.
    * @param c    The fully qualified name of the assumed class.  The runtime
    *             class of this feature won't be written if it's equivalent to
    *             <code>c</code>.
    * @param p    The assumed package string.  This feature's package string
    *             won't be written if it's equivalent to <code>p</code>.
    * @param g    The assumed classifier name string.  This feature's
    *             classifier name string won't be written if it's equivalent
    *             to <code>g</code>.
    * @param si   The assumed identifier as a string.  If this feature has a
    *             string identifier, it won't be written if it's equivalent to
    *             <code>si</code>.
    * @param bi   The assumed identifier as a byte string.  If this feature
    *             has a byte string identifier, it won't be written if it's
    *             equivalent to <code>bi</code>.
    * @return The name of the runtime type of this feature.
   **/
  public String lexWrite(ExceptionlessOutputStream out, Lexicon lex, String c,
                         String p, String g, String si, ByteString bi) {
    String result = super.lexWrite(out, lex, c, p, g, si, bi);
    out.writeInt(lex.lookupChild(left)); 
    out.writeInt(lex.lookupChild(right));
    return result;
  }


  /**
    * Reads the representation of a feature with this object's run-time type
    * as stored by a lexicon, overwriting the data in this object.
    *
    * <p> This method is appropriate for reading features as written by
    * {@link #lexWrite(ExceptionlessOutputStream,Lexicon,String,String,String,String,ByteString)}.
    *
    * @param in   The input stream.
    * @param lex  The lexicon we are reading in to.
    * @param p    The assumed package string.  If no package name is given in
    *             the input stream, the instantiated feature is given this
    *             package.
    * @param g    The assumed classifier name string.  If no classifier name
    *             is given in the input stream, the instantiated feature is
    *             given this classifier name.
    * @param si   The assumed identifier as a string.  If the feature being
    *             read has a string identifier field and no identifier is
    *             given in the input stream, the feature is given this
    *             identifier.
    * @param bi   The assumed identifier as a byte string.  If the feature
    *             being read has a byte string identifier field and no
    *             identifier is given in the input stream, the feature is
    *             given this identifier.
   **/
  public void lexRead(ExceptionlessInputStream in, Lexicon lex, String p,
                      String g, String si, ByteString bi) {
    super.lexRead(in, lex, p, g, si, bi);
    left = (DiscreteFeature) lex.lookupKey(in.readInt());
    right = (DiscreteFeature) lex.lookupKey(in.readInt());
  }
}

