package Htpjb.Clock;

//Imports
import java.awt.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Clock extends Canvas implements Serializable, Runnable {

    // state & properties
    private transient Image offImage;
    private transient Graphics offGrfx;
    private transient Thread clockThread = null;
    private transient Dimension offSize;
    private boolean raised;
    private boolean digital;

    // Constructors
    public Clock() {
        this(false, false);
    }

    public Clock(boolean r, boolean d) {
        // Allow the superclass constructor to do its thing
        super();

        // Set properties
        raised = r;
        digital = d;

        // set Background
        setBackground (getBackground());

        //set default size
        setSize(120,120);

        //create and start the clock thread
        clockThread = new Thread(this, "Clock");
        clockThread.start();
    }

    // Accessor methods
    public boolean isRaised() {
        return raised;
    }

    public void setRaised(boolean r) {
        raised = r;
        repaint();
    }

    public boolean isDigital() {
        return digital;
    }

    public void setDigital(boolean d) {
        digital = d;
        repaint();
    }

    // Other public methods
    @Override
    public void run() {
        while (clockThread != null) {
            repaint();
            try {
                clockThread.sleep(1000);
            }
            catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    @Override
    public synchronized void paint(Graphics g) {
        Dimension d = getSize();

        // Create the offscreen graphics context
        if (offGrfx == null || (offSize.width != d.width) || (offSize.height != d.height)) {
            offSize = d;
            offImage = createImage(d.width, d.height);
            offGrfx = offImage.getGraphics();
        }

        // Paint the background with 3D effects
        offGrfx.setColor(getBackground());
        offGrfx.fillRect(1, 1, d.width - 2, d.height - 2);
        offGrfx.draw3DRect(0, 0, d.width - 1, d.height - 1, raised);
        offGrfx.setColor(getForeground());

        // Paint the clock
        if (digital) {
            drawDigitalClock(offGrfx);
        }
        else {
            drawAnalogClock(offGrfx);
        }
        
        // Paint the image onto the screen
        g.drawImage(offImage, 0, 0, null);
    }

    // Private support methods
    private void drawAnalogClock (Graphics g) {
        Dimension d = getSize();
        int centerX = d.width / 2;
        int centerY = d.height / 2;
        Calendar now = Calendar.getInstance();

        // Draw the clock shape
        g.setFont(getFont());
        g.setColor(Color.white);
        g.fillArc(0, 0, d.width, d.height, 0, 360);
        g.setColor(Color.black);
        g.drawArc(0, 0, d.width, d.height, 0, 360);
        
        // draw hour numbers
        g.setColor(getForeground());
        for (int i = 1; i <= 12; i++) {
            double numberTheta = (double) i / 12 * 2 * Math.PI;
            double numberRadius = getEllipseRadius(centerX, centerY, numberTheta);
            int numberX = (int) (centerX + 0.9 * numberRadius * Math.sin(numberTheta) - g.getFontMetrics().getStringBounds(String.valueOf(i), g).getCenterX());
            int numberY = (int) (centerY - 0.9 * numberRadius * Math.cos(numberTheta) - g.getFontMetrics().getStringBounds(String.valueOf(i), g).getCenterY());
            
            g.drawString(String.valueOf(i), numberX, numberY);
        }
        
        // draw hour markers
        for (int i = 1; i <= 60; i++) {
            double barTheta = (double) i / 60 * 2 * Math.PI;
            double numberRadius = getEllipseRadius(centerX, centerY, barTheta);
            int numberXFull = (int) (centerX + numberRadius * Math.sin(barTheta));
            int numberYFull = (int) (centerY - numberRadius * Math.cos(barTheta));
            
            g.drawLine((int) (centerX + 0.97 * numberRadius * Math.sin(barTheta)), (int) (centerY - 0.97 * numberRadius * Math.cos(barTheta)), numberXFull,  numberYFull);
        }
        
        // Draw the hour hand
        double hourTheta = ((double) now.get(Calendar.HOUR) + (double) now.get(Calendar.MINUTE) / 60 + (double) now.get(Calendar.SECOND) / 3600) / 12 * 2 * Math.PI;
        double hourRadius = getEllipseRadius(centerX, centerY, hourTheta);
        int hourX = (int) (centerX + 0.5 * hourRadius * Math.sin(hourTheta));
        int hourY = (int) (centerY - 0.5 * hourRadius * Math.cos(hourTheta));
        int hourXOrigin = (int) (centerX - 0.1 * hourRadius * Math.sin(hourTheta));
        int hourYOrigin = (int) (centerY + 0.1 * hourRadius * Math.cos(hourTheta));
        g.setColor(Color.BLACK);
        g.fillArc(hourX - 3, hourY - 3, 6, 6, 0, 360);
        g.drawLine(hourXOrigin, hourYOrigin, hourX, hourY);

        // Draw the minute hand
        double minuteTheta = ((double) now.get(Calendar.MINUTE) + (double) now.get(Calendar.SECOND) / 60) / 60 * 2 * Math.PI;
        double minuteRadius = getEllipseRadius(centerX, centerY, minuteTheta);
        int minuteX = (int) (centerX + 0.8 * minuteRadius * Math.sin(minuteTheta));
        int minuteY = (int) (centerY - 0.8 * minuteRadius * Math.cos(minuteTheta));
        int minuteXOrigin = (int) (centerX - 0.1 * minuteRadius * Math.sin(minuteTheta));
        int minuteYOrigin = (int) (centerY + 0.1 * minuteRadius * Math.cos(minuteTheta));
        g.fillArc(minuteX - 3, minuteY - 3, 6, 6, 0, 360);
        g.drawLine(minuteXOrigin, minuteYOrigin, minuteX, minuteY);
        
        //Draw the second hand
        double secondTheta = ((double) now.get(Calendar.SECOND)) / 60 * 2 * Math.PI;
        double secondRadius = getEllipseRadius(centerX, centerY, secondTheta);
        int secondX = (int) (centerX + 0.9 * secondRadius * Math.sin(secondTheta));
        int secondY = (int) (centerY - 0.9 * secondRadius * Math.cos(secondTheta));
        int secondXOrigin = (int) (centerX - 0.1 * secondRadius * Math.sin(secondTheta));
        int secondYOrigin = (int) (centerY + 0.1 * secondRadius * Math.cos(secondTheta));
        // redness of second hand getting darker and darker
        g.setColor(new Color(255, 199 - (int)(199 * (double) now.get(Calendar.SECOND) / 60), 199 - (int)(199 * (double) now.get(Calendar.SECOND) / 60)));
        g.fillArc(secondX - 3,  secondY - 3, 6, 6, 0, 360);
        g.drawLine(secondXOrigin, secondYOrigin, secondX, secondY);
        
        // draw the center
        g.setColor(Color.BLACK);
        g.fillArc(centerX - 3, centerY - 3, 6, 6, 0, 360);
    }

    private void drawDigitalClock(Graphics g) {
        Dimension d = getSize();
        g.setColor(getForeground());
        g.setFont(getFont());

        // Get the time as a string
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(date);

        // Draw the time
        g.drawString(time, (int) (d.width/2 - g.getFontMetrics().getStringBounds(time, g).getCenterX()), (int) (d.height/2 - g.getFontMetrics().getStringBounds(time, g).getCenterY()));
    }
    
    // calculate the radius of a given ellipse with give angle
    private double getEllipseRadius(double a, double b, double theta) {
        double d = Math.pow(a, 2) * Math.pow(Math.cos(theta), 2) + Math.pow(b, 2) * Math.pow(Math.sin(theta), 2);
        double sd = Math.sqrt(d);
        return a * b / sd;
    }
}
