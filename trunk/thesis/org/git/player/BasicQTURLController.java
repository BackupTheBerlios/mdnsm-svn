package org.git.player;

import quicktime.*;
import quicktime.std.*;
import quicktime.app.view.*;
import quicktime.std.movies.*;
import quicktime.std.movies.media.*;
import quicktime.io.*;
 
import java.awt.*;
 
public class BasicQTURLController extends Frame {
 
  QTComponent qc;
 
  public BasicQTURLController ( ) throws QTException {
      super ("Basic QT DataRef/Controller");
      Movie dummyMovie = new Movie();
      qc = QTFactory.makeQTComponent (dummyMovie);
      Component c = qc.asComponent( );
      add (c);
      pack( );
  }
 
  public static void main (String[ ] args) {
      try {
          QTSessionCheck.check();
          BasicQTURLController f =
              new BasicQTURLController ( );
          String url =
              javax.swing.JOptionPane.showInputDialog (f,
                                              "Enter URL");
          DataRef dr = new DataRef (url);
          Movie m = Movie.fromDataRef (dr,
                                 StdQTConstants.newMovieActive | StdQTConstants4.newMovieIdleImportOK);
          MovieController mc = new MovieController (m);
          f.qc.setMovieController (mc);
          f.setVisible(true);
          f.pack( );
          m.prePreroll(0, 1.0f);
          m.preroll(0, 1.0f);
          m.start( );
      } catch (Exception e) {
          e.printStackTrace( );
      }
  }
}