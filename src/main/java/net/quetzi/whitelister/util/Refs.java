package net.quetzi.whitelister.util;

/**
 * Created by Quetzi on 24/09/14.
 */
public class Refs {
    public static final String MODID = "whitelister";
    public static final String NAME = "Whitelister";
    private static final String MAJOR   = "@MAJOR@";
    private static final String MINOR   = "@MINOR@";
    public static final String VERSION = MAJOR + "." + MINOR;
    public static final String BUILD   = "@BUILD_NUMBER@";
    public static final String CFGGENERAL = "General Settings";

    public static final String WHITELISTCMD_SYNTAX = "Syntax: /qw reload, /qw enable, /qw disable, /qw export, /qw list";
    public static final String RELOAD_SUCCESS = "Whitelist successfully reloaded";
    public static final String RELOAD_FAILED = "Whitelist could not be reloaded";
    public static final String ENABLED = "Whitelist enabled";
    public static final String DISABLED = "Whitelist disabled";
}
