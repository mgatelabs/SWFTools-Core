package com.mgatelabs.swftools.support.plugins;

import com.mgatelabs.swftools.exploit.conversion.*;
import com.mgatelabs.swftools.exploit.render.AdvancedPath;
import com.mgatelabs.swftools.support.swf.objects.*;

import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Stack;
import java.util.Vector;

public class AdvancedConverter implements Converter, Plugin {

    private int myTotal;
    private int myValue;

    private int myObjectTotal;
    private int myObjectValue;

    private boolean finished;

    private boolean cancel;

    private static final String myStatusMessages[] = {"Processing Shape", "Sorting Edges", "Building Graphs"};

    private int myStatus;

    private boolean printMore;

    public AdvancedConverter() {
        myStatus = 0;
        myTotal = 0;
        myValue = 0;
        myObjectTotal = 0;
        myObjectValue = 0;
        finished = false;
        cancel = false;
        printMore = false;
    }

    public String getName() {
        return "Advanced Conversion Engine";
    }

    public int getType() {
        return CONVERSION;
    }

    public String getStatus() {
        return myStatusMessages[myStatus];
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

    public int getValue() {
        return myValue;
    }

    public int getTotal() {
        return myTotal;
    }

    public boolean finished() {
        return finished;
    }

    public void update(FMovie anObject) {
        // Convert Shapes
        Vector objects = anObject.getObjectVector();
        myObjectTotal = objects.size();
        for (int x = 0; x < objects.size() && !cancel; x++) {
            myObjectValue = x;
            Object o = objects.get(x);

            if (o instanceof FShape) {
                FShape fs = (FShape) o;
                Vector data = update(fs, false);

                fs.setRenderData(data);
                fs.clean();
            } else if (o instanceof FFont) {
                FFont ff = (FFont) o;
                FShape ts = new FShape(3, 0, null);

                Vector fontRenderData[] = ff.getGlyphs();

                for (int y = 0; y < ff.getGlyphCount(); y++) {
                    ts.getStyle().setShapeRecords(ff.getGlyphs()[y]);
                    ff.getGlyphs()[y] = update(ts, true);
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

    public Vector update(FShape aShape, boolean skipAdvanced) {
        // Advanced
        myStatus = 0;
        FStyle aShapeStyle = aShape.getStyle();
        Vector tempShapes = new Vector();
        Vector result = new Vector();

        Vector fillStyles = aShapeStyle.getFillStyles();
        Vector lineStyles = aShapeStyle.getLineStyles();

        Vector allFillStyles = (Vector) fillStyles.clone();
        Vector allLineStyles = (Vector) lineStyles.clone();

        Vector records = aShapeStyle.getShapeRecords();

        FFill color1 = null;
        FFill color0 = null;
        FLine line = null;
        long startX = 0;
        long startY = 0;

        Stack myQueue = new Stack();
        Boolean holdLines = false;

        myValue = 0;

        myTotal = records.size();

        for (int x = 0; x < records.size(); x++) {
            myValue = x;

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
                            line = null;
                        }
                    }
                }

                if (aChange.isNewStyles()) {
                    fillStyles = aChange.getFillStyleArray();
                    lineStyles = aChange.getLineStyleArray();
                    for (int y = 0; y < fillStyles.size(); y++) {
                        allFillStyles.add(fillStyles.get(y));
                    }
                    for (int y = 0; y < lineStyles.size(); y++) {
                        allLineStyles.add(lineStyles.get(y));
                    }
                }
            } else if (t instanceof FEdge) {
                FEdge aEdge = (FEdge) t;
                long tempX = 0;
                long tempY = 0;

                if (aEdge.getStyle() == aEdge.STRAIGHT) {

                    if (color1 != null && color1 == color0) {

                        REdge re = new REdge(null, null, startX, startY, aEdge.getDeltaX(), aEdge.getDeltaY());
                        tempX = re.getEndX();
                        tempY = re.getEndY();
                        if (!skipAdvanced) {
                            holdLines = true;
                        }
                    } else {
                        if (color1 != null || (line == null && color0 == null && color1 == null)) // Change
                        {
                            if (!skipAdvanced) {
                                holdLines = false;
                            }
                            REdge re = new REdge(color1, null, startX, startY, aEdge.getDeltaX(), aEdge.getDeltaY());
                            tempX = re.getEndX();
                            tempY = re.getEndY();
                            tempShapes.add(re);
                        }

                        if (color0 != null) {
                            if (!skipAdvanced) {
                                holdLines = false;
                            }
                            REdge re = new REdge(color0, null, startX, startY, aEdge.getDeltaX(), aEdge.getDeltaY());
                            re.flip();
                            tempX = re.getStartX();
                            tempY = re.getStartY();
                            tempShapes.add(re);
                        }
                    }

                    //  if (line != null )
                    if (line != null || (line == null && color0 == null && color1 == null)) {

                        REdge re = new REdge(null, line, startX, startY, aEdge.getDeltaX(), aEdge.getDeltaY());
                        tempX = re.getEndX();
                        tempY = re.getEndY();
                        if (line != null) {
                            if (!skipAdvanced && holdLines) {
                                myQueue.add(re);
                            } else if (line != null) {
                                while (myQueue.size() > 0) {
                                    tempShapes.add(myQueue.pop());
                                }
                                tempShapes.add(re);
                            }
                        }
                    }

                    startX = tempX;
                    startY = tempY;
                } else if (aEdge.getStyle() == aEdge.CURVED) {

                    if (color1 != null && color1 == color0) {
                        RCurve re = null;

                        re = new RCurve(null, null, startX, startY, aEdge.getAnchorDeltaX(), aEdge.getAnchorDeltaY(), aEdge.getControlDeltaX(), aEdge.getControlDeltaY());
                        tempX = re.getEndX();
                        tempY = re.getEndY();

                        if (!skipAdvanced) {
                            holdLines = true;
                        }
                    } else {
                        if (color1 != null || (line == null && color0 == null && color1 == null)) // Change
                        {
                            if (!skipAdvanced) {
                                holdLines = false;
                            }
                            RCurve re = new RCurve(color1, null, startX, startY, aEdge.getAnchorDeltaX(), aEdge.getAnchorDeltaY(), aEdge.getControlDeltaX(), aEdge.getControlDeltaY());
                            tempX = re.getEndX();
                            tempY = re.getEndY();
                            tempShapes.add(re);
                        }

                        if (color0 != null) {
                            if (!skipAdvanced) {
                                holdLines = false;
                            }
                            RCurve re = new RCurve(color0, null, startX, startY, aEdge.getAnchorDeltaX(), aEdge.getAnchorDeltaY(), aEdge.getControlDeltaX(), aEdge.getControlDeltaY());
                            re.flip();
                            tempX = re.getStartX();
                            tempY = re.getStartY();
                            tempShapes.add(re);
                        }
                    }

                    // if ( line != null )  //if (line != null || (line == null && color0 == null && color1 == null) )
                    if (line != null || (line == null && color0 == null && color1 == null)) {
                        RCurve re = new RCurve(null, line, startX, startY, aEdge.getAnchorDeltaX(), aEdge.getAnchorDeltaY(), aEdge.getControlDeltaX(), aEdge.getControlDeltaY());
                        tempX = re.getEndX();
                        tempY = re.getEndY();

                        if (line != null) {
                            if (!skipAdvanced && holdLines) {
                                myQueue.add(re);
                            } else if (line != null) {
                                while (myQueue.size() > 0) {
                                    tempShapes.add(myQueue.pop());
                                }
                                tempShapes.add(re);
                            }
                        }
                    }

                    startX = tempX;
                    startY = tempY;
                }
            }
        }

        while (myQueue.size() > 0) {
            tempShapes.add(myQueue.pop());
        }

        // At This Point I have All of the Edges
        // Can go Back And make it better.
        sortPaths(allFillStyles, allLineStyles, tempShapes, result);

        //sortPaths(tempShapes, result);

        return result;
    }

    public void sortPaths(Vector fillStyles, Vector lineStyles, Vector paths, Vector results) {
        myStatus = 1;

        int graphSize = fillStyles.size() + lineStyles.size() + 1;

        RGraph myGraphs[] = new RGraph[graphSize];

        for (int x = 0; x < graphSize; x++) {
            myGraphs[x] = new RGraph(false);
        }

        // Put Each Node into a graph
        myValue = 0;
        myTotal = paths.size();

        for (int x = 0; x < paths.size(); x++) {
            myValue = x;

            if (cancel) {
                return;
            }

            RBase rb = (RBase) paths.get(x);
            if (rb.getFill() != null) // Has Fill
            {
                //System.out.print("F");
                RNode resultAddNode = myGraphs[(fillStyles.indexOf(rb.getFill()))].addToNodeLibrary(rb, x);
                paths.setElementAt(resultAddNode, x);
            } else if (rb.getLine() != null) // Has Stroke
            {
                //System.out.print("L");
                RNode resultAddNode = myGraphs[(fillStyles.size() + lineStyles.indexOf(rb.getLine()))].addToNodeLibrary(rb, x);
                paths.setElementAt(resultAddNode, x);
            } else // No Stroke or Fill
            {
                RNode resultAddNode = myGraphs[fillStyles.size() + lineStyles.size()].addToNodeLibrary(rb, x);
                paths.setElementAt(resultAddNode, x);
            }
        }
        //System.out.print("\n");
        // Build Node Connections, One Time, Speed +

        myStatus = 2;
        // Reset the status flag
        myValue = 0;
        myTotal = myGraphs.length;
        int myTestCount = 0;

        for (int x = 0; x < myGraphs.length; x++) {
            // Set the status flag
            myValue = x;
            // Set the ID
            myGraphs[x].setID(x);
            // Build the temp links between nodes
            myGraphs[x].buildConnections(x < fillStyles.size());
        }

        // My New Link List
        AdvPathLinkList myList = new AdvPathLinkList();

        // Set Status Flags
        myStatus = 1;
        myValue = 0;
        // Look through the different paths
        myTotal = paths.size();
        int pathCount = paths.size();
        RNode temp = null;

        for (int x = 0; x < pathCount; x++) {
            // Get a path/render node
            RNode aNode = (RNode) paths.get(x);
            // Set the flag
            myValue = x;
            // Reset the status
            myStatus = 1;

            //Check for cancel button
            if (cancel) {
                return;
            }

            // Don't Check dead Nodes
            if (aNode.isAlive()) {
                // Get the current graph
                RGraph aGraph = aNode.getGraph();
                temp = null;
                int index = aGraph.getID();
                GeneralPath p = null;

                // Fill Paths
                if (index < fillStyles.size() || (index == (fillStyles.size() + lineStyles.size()))) {
                    // Must find fill paths
                    temp = aGraph.findUseableFillPath(aNode);

                    if (temp != null) {
                        // Convert a fill to a path
                        //p = convertFill(temp, results);
                        p = convertFill(temp, results);

                        //if (isStandardRotation(p))
                        //{
                        //p = convertFillReverse(temp, results);
                        //}

                        // Get the lowest layer for sorting
                        int layer = aGraph.removeChain(temp, true);

                        // Was a target found?
                        AdvancedPath targetIfFound = null;

                        // Now I have a layer
                        boolean skipNext = false;

                        // Get the lists root
                        AdvPathLink pNode = myList.getRoot();

                        // Try to join a layer somewhre

                        // P was just converted

                        boolean chk1 = false, chk2 = false;

                        while (pNode != null) {
                            // Get the Path
                            AdvancedPath ap = pNode.path; //(AdvancedPath)results.get(y);

                            // Adding Engine

                            // Not a line and Fills Match, Strike Here
                            if (ap.getLine() == null && ap.getFill() == temp.getObject().getFill()) {
                                // Fully check
                                chk1 = RTools.containsShape(ap.getPath(), p);
                                chk2 = RTools.containsShape(p, ap.getPath());

                                if (chk1 || chk2) {
                                    if (targetIfFound == null) {
                                        boolean w1 = isStandardRotation(p);
                                        boolean w2 = isStandardRotation(ap.getPath());

                                        if (w1 == w2) {
                                            p = reversePath(p);
                                        }
                                    }

                                    if (targetIfFound == null) {
                                        ap.getPath().append(p, false);
                                        targetIfFound = ap;
                                    } else {
                                        targetIfFound.getPath().append(ap.getPath(), false);
                                        AdvPathLink tempNode = pNode.next;
                                        myList.remove(pNode);
                                        pNode = tempNode;
                                    }
                                }
                            }

                            // End Adding Engine

                            ////////////////////

                            // Under Engine

                            if (targetIfFound == null && ap.getLayer() >= layer) {
                                //System.out.println("Adding under: " + layer);
                                myList.addBefore(pNode, new AdvancedPath(temp.getObject().getFill(), null, p, layer));
                                p = null;
                                pNode = null;
                            }

                            // End Under Engine

                            if (skipNext) {
                                skipNext = false;
                            } else if (pNode != null) {
                                pNode = pNode.next;
                            }
                        }

                        // If nothing just add it
                        if (targetIfFound == null && p != null) {
                            myList.add(new AdvancedPath(temp.getObject().getFill(), null, p, layer));
                        }
                    }
                } else // Line Paths
                {
                    temp = aGraph.findUseableLinePath(aNode);
                    if (temp != null) {
                        p = convertLine(temp, results);
                        int layer = aGraph.removeChain(temp, false);
                        if (p != null) {
                            //System.out.println("Adding Line: " + layer );
                            myList.add(new AdvancedPath(null, temp.getObject().getLine(), p, layer));
                        }
                    }
                }
            }
        }

        myValue = 0;
        myTotal = 0;

        AdvPathLink aLink = myList.getRoot();

        while (aLink != null) {
            results.add(aLink.path);
            aLink = aLink.next;
        }
    }

    // Create Line(Fill) Paths

    public static GeneralPath convertLine(RNode root, Vector result) {

        RNode troot = root;
        RGraph myGraph = root.getGraph();

        if (root != null) {
            // Go through and build up the Object
            GeneralPath aPath = null;

            int count = 0;

            while (troot != null) {
                //System.out.println("Conversion Outer Path");
                GeneralPath fPath = troot.getObject().getPath();
                if (count == 0) {
                    aPath = (GeneralPath) fPath.clone();
                    count = 1;
                } // End IF
                else {
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
            }

            if (myGraph.isClosedPath()) {
                aPath.closePath();
            }

            //BasicStroke aStroke = new BasicStroke( (root.getObject().getLine().getWidth()/20.0f) , BasicStroke.CAP_ROUND , BasicStroke.JOIN_ROUND );

            //GeneralPath strokePath = new GeneralPath( aStroke.createStrokedShape(aPath) );

            //result.add(new AdvancedPath( new SolidFillStyle(root.getObject().getLine().getColor()) ,null, strokePath));
            return aPath;
        }
        return null;
        //myGraph.removeChain(root);
    }

    public boolean isStandardRotation(GeneralPath gp) {
        int degrees;
        if (gp != null) {
            float x1, y1;
            float x2, y2;

            Rectangle2D bound = gp.getBounds2D();
            Point2D point = new Point2D.Float((float) (bound.getX() + (bound.getWidth() / 2.0f)), (float) (bound.getY() + (bound.getHeight() / 2.0f)));

            float[][] points = new float[60][2];

            PathIterator pi = gp.getPathIterator(null);
            float[] floatArray = new float[6];

            pi.currentSegment(floatArray);

            int count = 0;

            points[count][0] = floatArray[0];
            points[count][1] = floatArray[1];
            count++;

            pi.next(); // Skip Move To
            while (!pi.isDone() && count < 60) {
                //System.out.println("Conversion Path");
                switch (pi.currentSegment(floatArray)) {
                    case PathIterator.SEG_CLOSE:
                        //aPath.closePath();
                        break;
                    case PathIterator.SEG_QUADTO:
                        //aPath.quadTo(floatArray[0], floatArray[1], floatArray[2], floatArray[3]);
                        points[count][0] = floatArray[2];
                        points[count][1] = floatArray[3];
                        count++;
                        break;
                    case PathIterator.SEG_LINETO:
                        //aPath.lineTo(floatArray[0], floatArray[1]);
                        points[count][0] = floatArray[0];
                        points[count][1] = floatArray[1];
                        count++;
                        break;
                    case PathIterator.SEG_MOVETO:
                        //aPath.moveTo(floatArray[0], floatArray[1]);
                        points[count][0] = floatArray[0];
                        points[count][1] = floatArray[1];
                        count++;
                        break;
                } // End Case
                pi.next();
            } // End While

            int i, j, k, n = count;
            //int flag = 0;
            double z;
            count = 0;

            if (n < 3) {
                return true;
            }

            if (n < 3) {
                return (true);
            }

            for (i = 0; i < n; i++) {
                j = (i + 1) % n;
                k = (i + 2) % n;
                z = (points[j][0] - points[i][0]) * (points[k][1] - points[j][1]);
                z -= (points[j][1] - points[i][1]) * (points[k][0] - points[j][0]);
                if (z < 0) {
                    count--;
                } else if (z > 0) {
                    count++;
                }
            }

            if (count > 0) {
                return (false); // COUNTERCLOCKWISE
            } else if (count < 0) {
                return (true); // CLOCKWISE
            } else {
                return (false);
            }
        }
        return false;
    }

    public GeneralPath convertFill(RNode root, Vector result) {

        RNode troot = root;
        //RGraph myGraph = root.getGraph();

        if (root != null) {
            // Go through and build up the Object
            GeneralPath aPath = null;

            int count = 0;

            while (troot != null) {
                //System.out.println("Conversion Outer Path");
                GeneralPath fPath = troot.getObject().getPath();
                if (count == 0) {
                    aPath = (GeneralPath) fPath.clone();
                    count = 1;
                } // End IF
                else {
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
            }

            aPath.closePath();

            // Temp Test

            return aPath;
            // AdvancedPath aNewPath = new AdvancedPath(root.getObject().getColor(),null, aPath);
            // result.add(aNewPath);
        }
        return null;
        //myGraph.removeChain(root);
    }

    public GeneralPath reversePath(GeneralPath origonal) {
        PathIterator pi = ReversePathIterator.getReversePathIterator(origonal);

        GeneralPath gp = new GeneralPath();

        float p[] = new float[6];

        while (!pi.isDone()) {
            switch (pi.currentSegment(p)) {
                case PathIterator.SEG_CLOSE:
                    gp.closePath();
                    break;
                case PathIterator.SEG_LINETO:
                    gp.lineTo(p[0], p[1]);
                    break;
                case PathIterator.SEG_MOVETO:
                    gp.moveTo(p[0], p[1]);
                    break;
                case PathIterator.SEG_QUADTO:
                    gp.quadTo(p[0], p[1], p[2], p[3]);
                    break;
            }
            pi.next();
        }

        return gp;
    }
}