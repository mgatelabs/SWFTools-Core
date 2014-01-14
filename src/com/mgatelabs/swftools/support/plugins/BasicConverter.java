package com.mgatelabs.swftools.support.plugins;

import com.mgatelabs.swftools.exploit.conversion.*;
import com.mgatelabs.swftools.exploit.render.AdvancedPath;
import com.mgatelabs.swftools.support.swf.objects.*;

import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.util.Vector;

public class BasicConverter implements Converter, Plugin {
    private int myTotal;
    private int myValue;

    private int myObjectTotal;
    private int myObjectValue;

    private boolean finished;

    private boolean cancel;

    private final String myStatusStrings[] = {"Setup", "Sorting Lines", "Sorting Fills"};

    private int myStatus;

    public BasicConverter() {
        myStatus = 0;
        myTotal = 0;
        myValue = 0;
        myObjectTotal = 0;
        myObjectValue = 0;
        finished = false;
        cancel = false;
    }

    public String getName() {
        return "Prototype Conversion Engine";
    }

    public int getType() {
        return CONVERSION;
    }

    public String getStatus() {
        return myStatusStrings[myStatus];
    }

    public void cancel() {
        cancel = true;
    }

    public int getObjectCount() {
        return myObjectTotal;
    }

    public int getCurrentObject() {
        return myObjectValue;
    }

    public boolean finished() {
        return finished;
    }

    public int getValue() {
        return myValue;
    }

    public int getTotal() {
        return myTotal;
    }

    public void update(FMovie anObject) {
        // Convert Shapes
        Vector objects = anObject.getObjectVector();
        myObjectTotal = objects.size();
        for (int x = 0; x < objects.size() && !cancel; x++) {
            myObjectValue = x;
            Object o = (Object) objects.get(x);

            if (o instanceof FShape) {
                FShape fs = (FShape) o;

                Vector data = update(fs);

                fs.setRenderData(data);
                fs.clean();
            } else if (o instanceof FFont) {
                FFont ff = (FFont) o;
                FShape ts = new FShape(3, 0, null);

                Vector fontRenderData[] = ff.getGlyphs();

                for (int y = 0; y < ff.getGlyphCount(); y++) {
                    ts.getStyle().setShapeRecords(ff.getGlyphs()[y]);
                    ff.getGlyphs()[y] = update(ts);
                }
            }

        }

        finished = true;

        if (!cancel) {
            anObject.setReady(true);
        } else {
            anObject.setReady(false);
        }
    }

    public Vector update(FShape aShape) {
        FStyle aShapeStyle = aShape.getStyle();
        Vector tempLine = new Vector();
        Vector tempFill = new Vector();
        Vector result = new Vector();

        Vector fillStyles = aShapeStyle.getFillStyles();
        Vector lineStyles = aShapeStyle.getLineStyles();
        Vector records = aShapeStyle.getShapeRecords();
        FFill color1 = null;
        FFill color0 = null;
        FLine line = null;
        long startX = 0;
        long startY = 0;

        myStatus = 0;
        myValue = 0;
        myTotal = records.size();

        for (int x = 0; x < records.size(); x++) {
            myValue++;
            if (cancel) {
                return null;
            }

            Object t = records.get(x);
            if (t instanceof FStyleChange) {
                FStyleChange aChange = (FStyleChange) t;
                if (aChange.isMoveTo()) {
                    startX = aChange.getMoveDeltaX();
                    startY = aChange.getMoveDeltaY();
                }

                if (aChange.isFillStyle1()) {
                    int temp = aChange.getFillStyle1();
                    if (temp == 0) {
                        color1 = null;
                    } else {
                        if ((temp - 1) < fillStyles.size()) {
                            color1 = (FFill) fillStyles.get(temp - 1);
                        } else {
                            //System.out.println("Bad Fill 1");
                            color1 = null;
                        }
                    }
                }

                if (aChange.isFillStyle0()) {
                    int temp = aChange.getFillStyle0();
                    if (temp == 0) {
                        color0 = null;
                    } else {
                        if ((temp - 1) < fillStyles.size()) {
                            color0 = (FFill) fillStyles.get(temp - 1);
                        } else {
                            //System.out.println("Bad Fill 0");
                            color0 = null;
                        }
                    }
                }

                if (aChange.isLineStyle()) {
                    int temp = aChange.getLineStyle();
                    if (temp == 0) {
                        line = null;
                    } else {
                        if ((temp - 1) < lineStyles.size()) {
                            line = (FLine) lineStyles.get(temp - 1);
                        } else {
                            //System.out.println("Bad Fill 1");
                            line = null;
                        }
                    }
                }

                if (aChange.isNewStyles()) {
                    fillStyles = aChange.getFillStyleArray();
                    lineStyles = aChange.getLineStyleArray();
                }
            } else if (t instanceof FEdge) {
                FEdge aEdge = (FEdge) t;
                long tempX = 0;
                long tempY = 0;

                //System.out.println(aEdge.getStyle());

                if (aEdge.getStyle() == aEdge.STRAIGHT) {

                    if (color1 != null && color1 == color0) {
                        //System.out.println("Skipping Line ");
                        REdge re = new REdge(null, null, startX, startY, aEdge.getDeltaX(), aEdge.getDeltaY());
                        tempX = re.getEndX();
                        tempY = re.getEndY();
                        //tempFill.add(re);
                    } else {
                        if (color1 != null || (line == null && color0 == null && color1 == null)) {
                            //System.out.println("Adding Edge  Color 1");
                            REdge re = new REdge(color1, null, startX, startY, aEdge.getDeltaX(), aEdge.getDeltaY());
                            tempX = re.getEndX();
                            tempY = re.getEndY();
                            tempFill.add(re);
                        }

                        if (color0 != null) {
                            REdge re = new REdge(color0, null, startX, startY, aEdge.getDeltaX(), aEdge.getDeltaY());
                            re.flip();
                            tempX = re.getStartX();
                            tempY = re.getStartY();
                            tempFill.add(re);
                        }
                    }

                    if (line != null) {
                        //System.out.println("Adding Line");
                        REdge re = new REdge(null, line, startX, startY, aEdge.getDeltaX(), aEdge.getDeltaY());
                        tempX = re.getEndX();
                        tempY = re.getEndY();
                        tempLine.add(re);
                    }

                    startX = tempX;
                    startY = tempY;
                } else if (aEdge.getStyle() == aEdge.CURVED) {

                    if (color1 != null && color1 == color0) {
                        //System.out.println("Skipping Curve");
                        RCurve re = new RCurve(null, null, startX, startY, aEdge.getAnchorDeltaX(), aEdge.getAnchorDeltaY(), aEdge.getControlDeltaX(), aEdge.getControlDeltaY());
                        tempX = re.getEndX();
                        tempY = re.getEndY();
                        //tempFill1.add(re);
                    } else {
                        if (color1 != null || (line == null && color0 == null && color1 == null)) {
                            //System.out.println("Adding Curve Color 1");
                            RCurve re = new RCurve(color1, null, startX, startY, aEdge.getAnchorDeltaX(), aEdge.getAnchorDeltaY(), aEdge.getControlDeltaX(), aEdge.getControlDeltaY());
                            //RenderEdge re = new RenderEdge(RenderEdge.EDGE, color1, null, startX, startY, aEdge.getDeltaX(), aEdge.getDeltaY());
                            tempX = re.getEndX();
                            tempY = re.getEndY();
                            tempFill.add(re);
                        }

                        if (color0 != null) {
                            RCurve re = new RCurve(color0, null, startX, startY, aEdge.getAnchorDeltaX(), aEdge.getAnchorDeltaY(), aEdge.getControlDeltaX(), aEdge.getControlDeltaY());
                            re.flip();
                            tempX = re.getStartX();
                            tempY = re.getStartY();
                            tempFill.add(re);
                        }
                    }

                    if (line != null) {
                        //System.out.println("Adding Line");
                        RCurve re = new RCurve(null, line, startX, startY, aEdge.getAnchorDeltaX(), aEdge.getAnchorDeltaY(), aEdge.getControlDeltaX(), aEdge.getControlDeltaY());

                        tempX = re.getEndX();
                        tempY = re.getEndY();
                        tempLine.add(re);
                        //tempFill.add(re);
                    }

                    startX = tempX;
                    startY = tempY;
                }
            }
        }

        // At This Point I have All of the Edges
        // Can go Back And make it better.

        // Fill Styles 0

        // Fill Styles 1
        sortPaths(tempFill, result, true);
        // 	// Line Styles
        sortPaths(tempLine, result, false);

        // sortPaths(tempFill, result, true);

        return result;
    }

    public void sortPaths(Vector paths, Vector result, boolean fill) {
        //System.out.println("Sorting " + paths.size() + (fill ? " Fill" : " Line") + " Paths");
        Vector currentCtyleGroup = new Vector();
        // Break up Into Style Groups

        int initial = paths.size();
        myTotal = initial;

        if (fill) {
            myStatus = 2;
        } else {
            myStatus = 1;
        }

        myValue = 0;
        myTotal = paths.size();

        while (paths.size() > 0) {
            myValue++;

            if (cancel) {
                return;
            }

            myValue = initial - paths.size();
            // Get Item off Top
            currentCtyleGroup.clear();
            RBase rb = (RBase) paths.get(0);
            currentCtyleGroup.add(rb);
            paths.remove(0);

            for (int x = 0; x < paths.size(); x++) {
                RBase temp = (RBase) paths.get(x);
                if (temp.isSameStyle(rb)) {
                    currentCtyleGroup.add(temp);
                }
            }

            for (int x = 0; x < currentCtyleGroup.size(); x++) {
                paths.remove(currentCtyleGroup.get(x));
            }

            testPaths(currentCtyleGroup, result, fill);
        }
    }

    public void testPaths(Vector paths, Vector result, boolean fill) {
        //System.out.println("Testing " + paths.size() + (fill ? " Fill" : " Line") + " Paths");
        Vector currentCtyleGroup = new Vector();
        Vector tempResults = new Vector();

        while (paths.size() > 0) {
            if (cancel) {
                return;
            }

            //System.out.println("Path Loop");
            currentCtyleGroup.clear();
            RBase root = (RBase) paths.get(0);
            paths.remove(0);
            currentCtyleGroup.add(root);

            boolean found = true;

            while (found == true) {
                found = false;
                for (int x = 0; x < paths.size(); x++) {
                    RBase temp = (RBase) paths.get(x);
                    if (testPath(currentCtyleGroup, temp, !fill)) {
                        currentCtyleGroup.add(temp);
                        found = true;
                    }
                }

                for (int x = 0; x < currentCtyleGroup.size(); x++) {
                    paths.remove(currentCtyleGroup.get(x));
                }
            }

            // Now I have a vector of lines of the same style that also touch
            if (currentCtyleGroup.size() > 1) {
                if (fill) {
                    findFillPaths(currentCtyleGroup, tempResults);
                } else {
                    findLinePaths(currentCtyleGroup, tempResults);
                }

            } else if (currentCtyleGroup.size() == 1 && !fill) {
                result.add(new AdvancedPath(null, root.getLine(), root.getPath()));
            }
        }

        if (tempResults.size() > 0) {
            AdvancedPath rootPath = (AdvancedPath) tempResults.get(0);
            for (int x = 1; x < tempResults.size(); x++) {
                AdvancedPath otherPath = (AdvancedPath) tempResults.get(x);
                rootPath.getPath().append(otherPath.getPath(), false);
            }
            result.add(rootPath);
        }
    }

    // Create Line Paths
    // LINE PATHS

    public void findLinePaths(Vector paths, Vector result) {
        //System.out.println("Line");
        RGraph myGraph = new RGraph(true);

        //System.out.println("Build Nodes");
        for (int x = 0; x < paths.size(); x++) {
            myGraph.addToNodeLibrary((RBase) paths.get(x));
        }

        //System.out.println(paths.size());
        myGraph.buildConnections(false);

        RNode root = myGraph.findUseableLinePath(null);

        while (root != null) {
            RNode troot = root;

            if (cancel) {
                return;
            }

            // Go through and build up the Object
            GeneralPath aPath = null;

            int count = 0;

            while (troot != null) {
                //System.out.println("Count " + count);
                GeneralPath fPath = troot.getObject().getPath();

                if (count == 0) {
                    aPath = (GeneralPath) fPath.clone();
                    count++;
                } // End IF
                else {
                    count++;
                    PathIterator pi = fPath.getPathIterator(null);
                    float[] floatArray = new float[6];

                    pi.next(); // Skip Move To

                    while (!pi.isDone()) {
                        //System.out.println("Conversion Path");
                        switch (pi.currentSegment(floatArray)) {
                            case PathIterator.SEG_CLOSE:
                                aPath.closePath();
                                break;
                            case PathIterator.SEG_QUADTO:
                                aPath.quadTo(floatArray[0], floatArray[1], floatArray[2], floatArray[3]);
                                break;
                            case PathIterator.SEG_LINETO:
                                aPath.lineTo(floatArray[0], floatArray[1]);
                                break;
                            case PathIterator.SEG_MOVETO:
                                aPath.moveTo(floatArray[0], floatArray[1]);
                                break;
                        } // End Case
                        pi.next();
                    } // End While

                } // End Else

                troot = troot.child;
                if (troot == root) {
                    troot = null;
                }
            }
            //System.out.println(count + " Lines");

            if (myGraph.isClosedPath()) {
                aPath.closePath();
            }

            result.add(new AdvancedPath(null, root.getObject().getLine(), aPath));

            myGraph.removeChain(root);

            root = myGraph.findUseableLinePath(null);
        }
    }

    // Create Fill Paths
    // FILL Paths

    public void findFillPaths(Vector paths, Vector result) {
        //System.out.println("Fill");
        // All Paths now Touch
        Vector tempResults = new Vector(paths.size() / 2);
        RGraph myGraph = new RGraph(false);

        for (int x = 0; x < paths.size(); x++) {
            myGraph.addToNodeLibrary((RBase) paths.get(x));
        }

        myGraph.buildConnections(false);

        RNode root = myGraph.findUseableFillPath(null);

        //System.out.println("Useable Path " + root);

        while (root != null) {
            RNode troot = root;

            if (cancel) {
                return;
            }

            if (root != null) {
                GeneralPath aPath = null;
                int count = 0;

                while (troot != null) {
                    GeneralPath fPath = troot.getObject().getPath();
                    if (count == 0) {
                        aPath = (GeneralPath) fPath.clone();
                        count = 1;
                    } else {
                        count++;
                        PathIterator pi = fPath.getPathIterator(null);
                        float[] floatArray = new float[6];

                        pi.next(); // Skip Move To

                        while (!pi.isDone()) {
                            //System.out.println("Conversion Path");
                            switch (pi.currentSegment(floatArray)) {
                                case PathIterator.SEG_CLOSE:
                                    aPath.closePath();
                                    break;
                                case PathIterator.SEG_QUADTO:
                                    aPath.quadTo(floatArray[0], floatArray[1], floatArray[2], floatArray[3]);
                                    break;
                                case PathIterator.SEG_LINETO:
                                    aPath.lineTo(floatArray[0], floatArray[1]);
                                    break;
                                case PathIterator.SEG_MOVETO:
                                    aPath.moveTo(floatArray[0], floatArray[1]);
                                    break;
                            }

                            pi.next();
                        }

                    }
                    troot = troot.child;
                }
                aPath.closePath();

                AdvancedPath aNewPath = new AdvancedPath(root.getObject().getColor(), null, aPath);
                result.add(aNewPath);

                myGraph.removeChain(root);

            }// Else the Node was Removed

            root = myGraph.findUseableFillPath(null);
        }
    }

    public boolean testPath(Vector paths, RBase path, boolean easy) {
        for (int x = 0; x < paths.size(); x++) {
            RBase temp = (RBase) paths.get(x);
            if (path.touch(temp, easy)) {
                return true;
            }
        }
        return false;
    }
}