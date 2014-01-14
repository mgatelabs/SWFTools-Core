/*
    Copyright M-Gate Labs 2007
    Can be edited with permission only.
*/
   
   /*
       Don't know if this class is neccessary.
   */

package com.mgatelabs.swftools.support.swf.objects;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

public class FAffineTransform extends AffineTransform {
    public float table[][];

    public FAffineTransform() {
        super();
        //System.out.println("Flash Affline  From Nothing");
        table = new float[2][3];
        setIdinty();
    }

    public FAffineTransform(FAffineTransform aMatrix) {
        super();
        //System.out.println("Flash Affline  From FlashAffineMatrix");
        table = new float[2][3];

        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 3; y++) {
                table[x][y] = aMatrix.table[x][y];
            }
        }

    }

    public FAffineTransform(FAffineTransform aAfineMatrix, FMatrix aMatrix) {
        super();
        //System.out.println("Flash Affline  From FlashAffineMatrix");
        table = new float[2][3];

        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 3; y++) {
                table[x][y] = aAfineMatrix.table[x][y];
            }
        }

        FAffineTransform newMatrix = new FAffineTransform(aMatrix);

        concatenate(newMatrix);
        newMatrix = null;
    }

    public FAffineTransform(FMatrix aMatrix) {
        super();
        //System.out.println("Flash Affline From Matrix");
        table = new float[2][3];

        if (aMatrix == null) {
            //System.out.println("Bad Input Matrix");
            setIdinty();
            return;
        }

        table[0][0] = aMatrix.getScaleX();
        table[1][1] = aMatrix.getScaleY();
        table[1][0] = aMatrix.getRotateSkew0();
        table[0][1] = aMatrix.getRotateSkew1();
        table[0][2] = aMatrix.getTranslateX() / 20.0f;
        table[1][2] = aMatrix.getTranslateY() / 20.0f;
    }

    public double getScaleX() {
        return table[0][0];
    }

    public double getScaleY() {
        return table[1][1];
    }

    public double getSkewX() {
        return table[1][0];
    }

    public double getSkewY() {
        return table[0][1];
    }

    public double getTranslateX() {
        return table[0][2];
    }

    public double getTranslateY() {
        return table[1][2];
    }


    public void deltaTransform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) {
        System.out.println("deltaTransform");
    }

    public Point2D deltaTransform(Point2D ptSrc, Point2D ptDst) {
        System.out.println("deltaTransform");
        return ptSrc;
    }

    public int getType() {
        System.out.println("getType");
        return super.getType();
    }

    public void getMatrix(double[] flatmatrix) {
        System.out.println("getMatrix");
    }


    public void translate(double tx, double ty)
    // Concatenate a translation onto the front of our
    // matrix.  When transforming points, the translation
    // happens first, then our original xform.
    {
        //System.out.println("translate");
        table[0][2] += table[0][0] * tx + table[0][1] * ty;
        table[1][2] += table[1][0] * tx + table[1][1] * ty;
    }

    public void preConcatenate(AffineTransform Tx) {
        //System.out.println("preConcatenate");
    }

    public boolean isIdentity() {
        //System.out.println("isIdentity");
        return false;
    }

    public Shape createTransformedShape(Shape pSrc) {
        //System.out.println("createTransformedShape");

        if (pSrc == null) {
            return null;
        }

        if (pSrc instanceof GeneralPath) {
            return ((GeneralPath) pSrc).createTransformedShape(this);
        } else {
            PathIterator pi = pSrc.getPathIterator(this);
            GeneralPath gp = new GeneralPath(pi.getWindingRule());
            gp.append(pi, false);
            return gp;
        }
        // return pSrc;
    }

    public void concatenate(AffineTransform tx) {
        if (tx == null) {
            //System.out.println("Empty Concatenate");
            return;
        }

        //System.out.println("Concatenate");

        FAffineTransform b;
        if (tx instanceof FAffineTransform) {
            b = (FAffineTransform) tx;
        } else {
            return;
        }
         
         /*
          matrix	t;
      	t.m_[0][0] = m_[0][0] * m.m_[0][0] + m_[0][1] * m.m_[1][0];
      	t.m_[1][0] = m_[1][0] * m.m_[0][0] + m_[1][1] * m.m_[1][0];
      	t.m_[0][1] = m_[0][0] * m.m_[0][1] + m_[0][1] * m.m_[1][1];
      	t.m_[1][1] = m_[1][0] * m.m_[0][1] + m_[1][1] * m.m_[1][1];
      	t.m_[0][2] = m_[0][0] * m.m_[0][2] + m_[0][1] * m.m_[1][2] + m_[0][2];
      	t.m_[1][2] = m_[1][0] * m.m_[0][2] + m_[1][1] * m.m_[1][2] + m_[1][2];
      	*/

        float[][] tableB = new float[2][3];
        tableB[0][0] = table[0][0] * b.table[0][0] + table[0][1] * b.table[1][0];
        tableB[1][0] = table[1][0] * b.table[0][0] + table[1][1] * b.table[1][0];
        tableB[0][1] = table[0][0] * b.table[0][1] + table[0][1] * b.table[1][1];
        tableB[1][1] = table[1][0] * b.table[0][1] + table[1][1] * b.table[1][1];
        tableB[0][2] = table[0][0] * b.table[0][2] + table[0][1] * b.table[1][2] + table[0][2];
        tableB[1][2] = table[1][0] * b.table[0][2] + table[1][1] * b.table[1][2] + table[1][2];
        table = tableB;
    }

    public AffineTransform createInverse() {
        System.out.println("createInverse");
        return new AffineTransform();
    }

    public void inverseTransform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts) {
        System.out.println("inverseTransform");
    }

    //Inverse transforms an array of double precision coordinates by this transform.
    public Point2D inverseTransform(Point2D ptSrc, Point2D ptDst) {
        System.out.println("inverseTransform");
        return ptSrc;
    }
    //Inverse transforms the specified ptSrc and stores the result in ptDst.

    public void transform(double[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts)
        //Transforms an array of double precision coordinates by this transform.
      /*
          srcPts - the array containing the source point coordinates. Each point is stored as a pair of x, y coordinates.
      	dstPts - the array into which the transformed point coordinates are returned. Each point is stored as a pair of x, y coordinates.
      	srcOff - the offset to the first point to be transformed in the source array
      	dstOff - the offset to the location of the first transformed point that is stored in the destination array
      	numPts - the number of point objects to be transformed
      */ {
        //System.out.println("Transform A");
        double pointX = 0;
        double pointY = 0;
        for (int z = 0; z < numPts; z++) {
            pointX = srcPts[srcOff];
            srcOff++;
            pointY = srcPts[srcOff];
            srcOff++;

            dstPts[dstOff] = pointX * table[0][0] + pointY * table[0][1] + table[0][2];
            dstOff++;
            dstPts[dstOff] = pointX * table[1][0] + pointY * table[1][1] + table[1][2];
            dstOff++;
            //x' = x * ScaleX + y * RotateSkew1 + TranslateX
            //y' = x * RotateSkew0 + y * ScaleY + TranslateY
        }
    }

    public void transform(double[] srcPts, int srcOff, float[] dstPts, int dstOff, int numPts)
        //Transforms an array of double precision coordinates by this transform and stores the results into an array of floats.
      	/*
      	      srcPts - the array containing the source point coordinates. Each point is stored as a pair of x, y coordinates.
      			dstPts - the array into which the transformed point coordinates are returned. Each point is stored as a pair of x, y coordinates.
      			srcOff - the offset to the first point to be transformed in the source array
      			dstOff - the offset to the location of the first transformed point that is stored in the destination array
      			numPts - the number of point objects to be transformed
      	*/ {
        System.out.println("Transform B");
        double pointX = 0;
        double pointY = 0;
        for (int z = 0; z < numPts; z++) {
            pointX = srcPts[srcOff];
            srcOff++;
            pointY = srcPts[srcOff];
            srcOff++;

            dstPts[dstOff] = (float) (pointX * table[0][0] + pointY * table[0][1] + table[0][2]);
            dstOff++;
            dstPts[dstOff] = (float) (pointX * table[1][0] + pointY * table[1][1] + table[1][2]);
            dstOff++;
            //x' = x * ScaleX + y * RotateSkew1 + TranslateX
            //y' = x * RotateSkew0 + y * ScaleY + TranslateY
        }
    }

    public void transform(float[] srcPts, int srcOff, double[] dstPts, int dstOff, int numPts)
        //Transforms an array of floating point coordinates by this transform and stores the results into an array of doubles.
      	/*
      		srcPts - the array containing the source point coordinates. Each point is stored as a pair of x, y coordinates.
      		dstPts - the array into which the transformed point coordinates are returned. Each point is stored as a pair of x, y coordinates.
      		srcOff - the offset to the first point to be transformed in the source array
      		dstOff - the offset to the location of the first transformed point that is stored in the destination array
      		numPts - the number of points to be transformed
      	*/ {
        System.out.println("Transform C");
        double pointX = 0;
        double pointY = 0;
        for (int z = 0; z < numPts; z++) {
            pointX = srcPts[srcOff];
            srcOff++;
            pointY = srcPts[srcOff];
            srcOff++;

            dstPts[dstOff] = pointX * table[0][0] + pointY * table[0][1] + table[0][2];
            dstOff++;
            dstPts[dstOff] = pointX * table[1][0] + pointY * table[1][1] + table[1][2];
            dstOff++;
            //x' = x * ScaleX + y * RotateSkew1 + TranslateX
            //y' = x * RotateSkew0 + y * ScaleY + TranslateY
        }
    }

    public void transform(float[] srcPts, int srcOff, float[] dstPts, int dstOff, int numPts)
    //Transforms an array of floating point coordinates by this transform.
    {
        // This ones used the most
        //System.out.println("Transform D");
        float pointX = 0;
        float pointY = 0;
        for (int z = 0; z < numPts; z++) {
            pointX = srcPts[srcOff];
            srcOff++;
            pointY = srcPts[srcOff];
            srcOff++;

            dstPts[dstOff] = pointX * table[0][0] + pointY * table[0][1] + table[0][2];
            dstOff++;
            dstPts[dstOff] = pointX * table[1][0] + pointY * table[1][1] + table[1][2];
            dstOff++;
            //x' = x * ScaleX + y * RotateSkew1 + TranslateX
            //y' = x * RotateSkew0 + y * ScaleY + TranslateY
        }
    }

    public void transform(Point2D[] ptSrc, int srcOff, Point2D[] ptDst, int dstOff, int numPts)
        //Transforms an array of point objects by this transform.
      	/*
      	ptSrc - the array containing the source point objects
      	ptDst - the array into which the transform point objects are returned
      	srcOff - the offset to the first point object to be transformed in the source array
      	dstOff - the offset to the location of the first transformed point object that is stored in the destination array
      	numPts - the number of point objects to be transformed
      	*/ {
        System.out.println("Transform E");
        double pointX = 0;
        double pointY = 0;
        for (int z = 0; z < numPts; z++) {//getX()
            pointX = ptSrc[srcOff].getX();
            pointY = ptSrc[srcOff].getY();
            srcOff++;

            Point2D target = ptDst[dstOff];
            if (target == null) {
                ptDst[dstOff] = new Point2D.Double();
                target = ptDst[dstOff];
            }
            dstOff++;
            target.setLocation(pointX * table[0][0] + pointY * table[0][1] + table[0][2], pointX * table[1][0] + pointY * table[1][1] + table[1][2]);

            //pointY = srcPts[srcOff];
            //srcOff++;
            //dstPts[dstOff] = pointX * table[0][0] + pointY * table[0][1] + table[0][2];
            //dstPts[dstOff] = pointX * table[1][0] + pointY * table[1][1] + table[1][2];
            //dstOff++;
            //x' = x * ScaleX + y * RotateSkew1 + TranslateX
            //y' = x * RotateSkew0 + y * ScaleY + TranslateY
        }
    }

    public Point2D transform(Point2D ptSrc, Point2D ptDst)
    //Transforms the specified ptSrc and stores the result in ptDst.
    {
        //System.out.println("Transform F");
        double pointX = ptSrc.getX();
        double pointY = ptSrc.getY();

        Point2D target = ptDst;
        if (target == null) {
            ptDst = new Point2D.Double();
            target = ptDst;
        }
        target.setLocation(pointX * table[0][0] + pointY * table[0][1] + table[0][2], pointX * table[1][0] + pointY * table[1][1] + table[1][2]);

        return target;
    }

    public void setIdinty() {
        table[0][0] = 1;
        table[1][0] = 0;
        table[0][1] = 0;
        table[1][1] = 1;
        table[0][2] = 0;
        table[1][2] = 0;
    }

    public Object clone() {
        return (Object) new FAffineTransform(this);
    }
}