// ClockBeanInfo Class
// ClockBeanInfo.java

package Htpjb.Clock;

// Imports
import java.beans.*;

public class ClockBeanInfo extends SimpleBeanInfo {
  // Get the appropriate icon
  public java.awt.Image getIcon(int iconKind) {
    if (iconKind == BeanInfo.ICON_COLOR_16x16) {
      java.awt.Image img = loadImage("ClockIcon16.gif");
      return img;
    }
    if (iconKind == BeanInfo.ICON_COLOR_32x32) {
      java.awt.Image img = loadImage("ClockIcon32.gif");
      return img;
    }
    return null;
  }
}
