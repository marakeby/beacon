package edu.vt.beacon.util;

import java.awt.geom.Point2D;

public class ArcManager
{
    private static final Point2D.Float currentPoint_ = new Point2D.Float();
    
    private static final Point2D.Float sourcePoint_ = new Point2D.Float();
    
    private static final Point2D.Float targetPoint_ = new Point2D.Float();
    
    /*
     * document method
     */
    public static Point2D.Float getCurrentPoint()
    {
        return currentPoint_;
    }
    
    /*
     * document method
     */
    private static float getDeltaX()
    {
        return targetPoint_.x - sourcePoint_.x;
    }
    
    /*
     * document method
     */
    private static float getDeltaY()
    {
        return targetPoint_.y - sourcePoint_.y;
    }
    
    /*
     * document method
     */
    private static float getIntercept()
    {
        return targetPoint_.y - getSlope() * targetPoint_.x;
    }
    
    /*
     * document method
     */
    public static double getRotation()
    {
        double adjacent = targetPoint_.x - sourcePoint_.x;
        double hypotenuse = targetPoint_.distance(sourcePoint_);
        
        return (targetPoint_.y <= sourcePoint_.y)
               ? -Math.acos(adjacent / hypotenuse)
               : Math.acos(adjacent / hypotenuse);
    }
    
    /*
     * document method
     */
    private static float getSlope()
    {
        return getDeltaY() / getDeltaX();
    }
    
    /*
     * document method
     */
    public static void next()
    {
        if (Math.abs(getDeltaX()) > Math.abs(getDeltaY())) {
            
            currentPoint_.x += (getDeltaX() >= 0.0F) ? 1.0F : -1.0F;
            
            if (getDeltaY() != 0.0F)
                currentPoint_.y = getSlope() * currentPoint_.x +
                                  getIntercept();
        }
        else {
            
            currentPoint_.y += (getDeltaY() >= 0.0F) ? 1.0F : -1.0F;
            
            if (getDeltaX() != 0.0F)
                currentPoint_.x = (currentPoint_.y - getIntercept()) /
                                  getSlope();
        }
    }
    
    /*
     * document method
     */
    public static void setPointCoordinates(Point2D.Float sourcePoint,
                                           Point2D.Float targetPoint)
    {
        currentPoint_.setLocation(sourcePoint);
        sourcePoint_.setLocation(sourcePoint);
        targetPoint_.setLocation(targetPoint);
    }

    public static Point2D.Float getPesudoTarget(double pesudoTargetDistance, Point2D.Float source, Point2D.Float target)
    {

//        System.out.println(sourcePoint_+ "" + targetPoint_);
        double adjacent = target.x - source.x;
        double opposite = target.y - source.y;
        double hypotenuse = target.distance(source);

        double x = (hypotenuse-pesudoTargetDistance)*adjacent/hypotenuse;
        double y = (hypotenuse-pesudoTargetDistance)*opposite/hypotenuse;
        x =  Math.abs( target.x -x);
        y = Math.abs(target.y -y);

        Point2D.Float p= new Point2D.Float();
        p.setLocation(x,y);
        return p;

    }

}