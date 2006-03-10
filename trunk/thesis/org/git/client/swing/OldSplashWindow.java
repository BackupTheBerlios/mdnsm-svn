package org.git.client.swing;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

class OldSplashWindow extends JWindow
{
    Robot     r;
    public Image img;
    Image ant;
    JCheckBox cb;
    
    public OldSplashWindow(String filename, Frame f, int waitTime)
    {
        super(f);
        ImageIcon splash = new ImageIcon(getClass().getResource("/images/"+filename));
        JLabel iconLabel = new JLabel(splash);
        iconLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        getContentPane().add(iconLabel);
        getContentPane().setBackground(new Color(255, 255, 255));
        cb = new JCheckBox("Don't hang around next time");
        cb.setForeground(Color.WHITE);
            cb.setOpaque(false);
            cb.setSize(cb.getPreferredSize());
            cb.setLocation(0, splash.getIconHeight() - cb.getHeight() + cb.getInsets().bottom / 2);
        getLayeredPane().add(cb, new Integer(200));
        addMouseListener(new MouseAdapter()
            {
                public void mousePressed(MouseEvent e)
                {
                    setVisible(false);
                    dispose();
                }
            });
        final int pause = waitTime;
        final Runnable closerRunner = new Runnable()
            {
                public void run()
                {
                    setVisible(false);
                    dispose();
                }
            };
        Runnable waitRunner = new Runnable()
            {
                public void run()
                {
                    try
                        {
                            Thread.sleep(pause);
                            SwingUtilities.invokeAndWait(closerRunner);
                        }
                    catch(Exception e)
                        {
                            e.printStackTrace();
                            // can catch InvocationTargetException
                            // can catch InterruptedException
                        }
                }
            };
        Thread splashThread = new Thread(waitRunner, "SplashThread");
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension img = new Dimension(splash.getIconWidth(), splash.getIconHeight());
        setBounds(d.width / 2 - (img.width / 2), d.height / 2 - (img.height / 2), img.width, img.height);
        setVisible(true);
        splashThread.start();
    }
    
}