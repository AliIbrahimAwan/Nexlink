package nexlink.client.gui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class StatusListRenderer extends DefaultListCellRenderer {
    
    private ImageIcon onlineIcon;
    private ImageIcon offlineIcon;

    public StatusListRenderer() {
        try {
            ClassLoader cl = this.getClass().getClassLoader();
            URL onlineURL = cl.getResource("nexlink/client/gui/icons/online.png");
            URL offlineURL = cl.getResource("nexlink/client/gui/icons/offline.png");
            
            // 💡 Define a clean, uniform icon size
            int iconSize = 16;
            
            if (onlineURL != null) {
                ImageIcon rawOnline = new ImageIcon(onlineURL);
                Image scaledImg = rawOnline.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
                onlineIcon = new ImageIcon(scaledImg);
            }
            
            if (offlineURL != null) {
                ImageIcon rawOffline = new ImageIcon(offlineURL);
                Image scaledImg = rawOffline.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
                offlineIcon = new ImageIcon(scaledImg);
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error loading icons: " + e.getMessage());
        }
    }
    
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                                                  boolean isSelected, boolean cellHasFocus) {
        // 1. Get standard background/foreground properties from Swing
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        
        if (value instanceof String) {
            String item = ((String) value).trim();
            
            // Clear default icon settings for safety
            label.setIcon(null); 
            
            // 💡 2. Direct string checking to completely match what your server sends
            if (item.contains("(online)")) {
                // Strip out the bracket text entirely so only the name is left
                String cleanName = item.replace("(online)", "").trim();
                label.setText(cleanName); 
                label.setIcon(onlineIcon); // 🟢 Attach your custom green icon
                label.setForeground(Color.WHITE); 
                label.setFont(label.getFont().deriveFont(Font.BOLD));
            } 
            else if (item.contains("(offline)")) {
                // Strip out the bracket text entirely so only the name is left
                String cleanName = item.replace("(offline)", "").trim();
                
                // Safety check: if it's an empty database name row, hide it cleanly
                if (cleanName.isEmpty()) {
                    label.setText("");
                    return label;
                }
                
                label.setText(cleanName); 
                label.setIcon(offlineIcon); // 🔴 Attach your custom red icon
                label.setForeground(Color.WHITE); 
                label.setFont(label.getFont().deriveFont(Font.PLAIN));
            }
        }
        
        // 3. Add visual spacing to prevent the items from feeling clustered
        label.setIconTextGap(8); 
        label.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10)); 
        
        return label;
    }
}