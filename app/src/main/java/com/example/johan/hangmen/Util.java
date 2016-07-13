package com.example.johan.hangmen;

/**
 * Created by Dell on 3/19/2015.
 */
public class Util {

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";
    public static final String EMAIL = "email";
    public static final String USER_NAME = "user_name";

    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public final static String SENDER_ID = "209690052574";

    public static String base_url = "192.168.0.103";

    public final static String  register_url=base_url+"register.php";
    public final static String  send_chat_url=base_url+"sendChatmessage.php";

    // xmpp variables
    public static final String SERVER ="192.168.0.103";
    public static final String DOMAIN = "hangboardapp.com";
    public static final String XMPP_PASSWORD = "h4ngboard";
    public static final String XMPP_SECREAT_KEY = "e50xzfV7";

    public static final String SUFFIX_CHAT = "@" + DOMAIN;
    public static final String SUFFIX_CHAT_GROUP = "@conference." + DOMAIN;



}
