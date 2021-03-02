package spoon;

public class CommentClass {

    /**
     * The value of this constant is {@value}.
     */
    public static final int SCRIPT_START = 3;

    /**
     * Returns an Image object that can then be painted on the screen.
     * The url argument must specify an absolute <a href="#{@link}">{@link spoon.toStringBugTest.ToStringBugTest}</a>. The name
     * argument is a specifier that is relative to the url argument.
     * <p>
     * This method always returns immediately, whether or not the
     * image exists. When this {@literal applet} attempts to draw the image on
     * the screen, the data will be loaded. a custom inline tag {@custominlinetag with some data}. The graphics primitives
     * that draw the image will incrementally paint on the screen.
     *
     * @param  url  an absolute URL giving the base location of the image
     * @param  name the location of the image, relative to the url argument
     * @return      the image at the specified URL
     * @see         spoon.test.javadoc.JavaDocTest
     * @customtag a custom tag
     */
    public int getImage(int url, String name) {
        return 1;
    }
}

