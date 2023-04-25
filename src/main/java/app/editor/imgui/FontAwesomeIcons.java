package app.editor.imgui;

public class FontAwesomeIcons {
    private FontAwesomeIcons(){}
    private static final short ICON_RANGE_MIN = (short) 0xe005;
    private static final short ICON_RANGE_MAX = (short) 0xf8ff;
    protected static final short[] ICON_RANGE = new short[]{ICON_RANGE_MIN, ICON_RANGE_MAX, 0};

    public static final String CAMERA = "\uf030";
    public static final String FILE = "\uf15b";
    public static final String FILE_AUDIO = "\uf1c7";
    public static final String FILE_IMAGE = "\uf1c5";
    public static final String FILE_IMPORT = "\uf56f";
    public static final String FOLDER_OPEN = "\uf07c";
    public static final String LAYER_GROUP = "\uf5fd";
    public static final String OUT_DENT = "\uf03b";
    public static final String PLUS = "\uf067";
    public static final String SAVE = "\uf0c7";
    public static final String TOOLS = "\uf7d9";
    public static final String WALKING = "\uf554";
    public static final String WRENCH = "\uf0ad";
}