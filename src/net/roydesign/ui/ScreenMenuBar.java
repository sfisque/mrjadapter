/*******************************************************************************

	File:		ScreenMenuBar.java
	Author:		Steve Roy <steve@sillybit.com>
				
	Part of MRJ Adapter, a unified API for easy integration of Mac OS specific
	functionality within your cross-platform Java application.
	
	This library is open source and can be modified and/or distributed under
	the terms of the Artistic License.
	<http://mrjadapter.dev.java.net/license.html>
	
	Change History:
	02/23/03	Created this file - Steve
	02/26/04    Merged into MRJ Adapter - Steve
	04/16/04    Renamed from MenuBar to ScreenMenuBar - Steve

*******************************************************************************/

package net.roydesign.ui;

import net.roydesign.mac.MRJAdapter;
import java.awt.Frame;

/**
 * <p>A subclass of <code>java.awt.MenuBar</code> that adds the logistics needed
 * to make menu bars conform to the Mac OS screen menu bar requirements without
 * sacrificing the usual way of presenting menu bars on other platforms. On
 * Mac OS and Mac OS X, the menu bar sits at the top of the screen, contrary to
 * other platforms that locate a distinct menu bar in each frame. This difference
 * in UI design makes it challenging to write code that gracefully handles all
 * platforms, and this class, along with <code>ScreenMenu</code> and
 * <code>ScreenMenuItem</code> improve on the existing AWT classes to fill the
 * gap.</p>
 *
 * <p>This works with the introduction of a concept called user frames. The word
 * "user" here doesn't refer to the computer user but rather to frames that make
 * use of a menu and a menu item. Rather then if/elsing your code when constructing
 * your menu bars to handle different platforms, you simply code one menu bar where
 * you specify which menus and menu items are actually used by which frame classes.
 * With this in place, you just add an instance of the menu bar to all your frames,
 * and it will automatically decide by itself which menus and menu items should be
 * visible or not, and enabled or not, depending on the current platform and whether
 * the screen menu bar is used. This is all done via the method
 * <code>addUserFrame()</code> in the <code>ScreenMenu</code> and
 * <code>ScreenMenuItem</code> classes. The behavior is as follows. On the Mac
 * with the screen menu bar in use, when the host frame is not a user frame,
 * then the menu or menu item is simply disabled. On other platforms, the menu
 * is instead removed. This satisfies the requirement of having an all-inclusive
 * menu bar on the Mac while allowing distinct menu bars on other platforms.</p>
 *
 * <p>Typically, the most convenient way of using these classes is to subclass
 * <code>ScreenMenuBar</code> and then instantiate it for each of your frames.</p>
 *
 * <pre>
 * public class MyMenuBar extends ScreenMenuBar
 * {
 *     public MyMenuBar()
 *     {
 *         // All frames get this menu
 *         ScreenMenu m = new ScreenMenu("Foo");
 *         add(m);
 *         
 *         // Even though all frames get the Foo menu, only
 *         // MyFrame gets this menu item
 *         ScreenMenuItem mi = new ScreenMenuItem("Hello");
 *         mi.addUserFrame(MyFrame.class);
 *         m.add(mi);
 *         
 *         // Just MyFrame and MyFrameToo get this menu
 *         m = new ScreenMenu("Bar");
 *         m.addUserFrame(MyFrame.class);
 *         m.addUserFrame(MyFrameToo.class);
 *         add(m);
 *         
 *         // And all frames that get the Bar menu get
 *         // this menu item
 *         mi = ScreenMenuItem("Bye Bye");
 *         m.add(mi);
 *     }
 * }
 * </pre>
 *
 * <p>It's important to mention that this set of menu classes doesn't provide
 * support for shared menus, which developers sometimes request. A shared menu
 * shares its state within all its displayed instances, meaning for example that
 * if you disable it, all its instances will be disabled, which might or might
 * not be what you desire, especially in the context of a document-based user
 * interface. On the contrary, this set of classes creates separate instances
 * of menus and menu items, each with its own state, even though they might all 
 * look like the same menu to the user, particularly on Mac OS.</p>
 * 
 * @version MRJ Adapter 1.2
 */
public class ScreenMenuBar extends java.awt.MenuBar
{
	/**
	 * Construct a menu bar.
	 */
	public ScreenMenuBar()
	{
		super();
	}
	
	/**
	 * This method is overriden to disable or hide the menus that don't
	 * belong in the menu bar for the parent frame.
	 */
	public void addNotify()
	{
		// Get the parent frame
		Frame f = (Frame)getParent();
		
		// Check if the frame makes any use of each menu
		int n = getMenuCount();
		for (int i = n - 1; i >= 0; i--)
		{
			java.awt.Menu m = getMenu(i);
			if (m instanceof ScreenMenu && !((ScreenMenu)m).isUsedBy(f))
			{
				if (MRJAdapter.isAWTUsingScreenMenuBar())
					m.setEnabled(false);
				else
					remove(i);
			}
		}
		
		super.addNotify();
	}
}
